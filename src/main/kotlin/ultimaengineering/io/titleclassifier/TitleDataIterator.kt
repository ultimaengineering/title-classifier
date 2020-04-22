package ultimaengineering.io.titleclassifier

import org.datavec.api.io.filters.RandomPathFilter
import org.datavec.api.io.labels.ParentPathLabelGenerator
import org.datavec.api.split.CollectionInputSplit
import org.datavec.api.split.InputSplit
import org.datavec.image.loader.BaseImageLoader
import org.datavec.image.recordreader.ImageRecordReader
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import java.io.IOException
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.stream.Collectors

class TitleDataIterator(dataDirectory: Path, trainDataPercentage: Int, batchSize: Int) {

    fun initDateSetIterators(batchSize: Int) {
        trainDataIterator = makeDataSetIterator(trainData, batchSize)
        testDataIterator = makeDataSetIterator(testData, batchSize)
    }

    @Throws(IOException::class)
    private fun findAllImages(dataDir: Path?): List<URI> {
        return Files.walk(dataDir)
                .filter { path: Path -> Files.isRegularFile(path) }
                .filter { file: Path -> validExtension(file) }
                .filter { path: Path -> sufficientLabelData(path) }
                .map { obj: Path -> obj.toUri() }
                .collect(Collectors.toList())
    }

    @Throws(IOException::class)
    private fun makeDataSetIterator(inputSplit: InputSplit, batchSize: Int): DataSetIterator {
        val recordReader = ImageRecordReader(height, width, channels, labelMaker)
        recordReader.initialize(inputSplit)
        return RecordReaderDataSetIterator(recordReader, batchSize, 1, recordReader.numLabels())
    }

    private fun sufficientLabelData(path: Path): Boolean {
        return try {
            if (fileCounts.containsKey(path.parent)) {
                return fileCounts[path.parent]!! > 20
            }
            val count = Files.walk(path.parent)
                    .filter { p -> Files.isRegularFile(p) }
                    .filter { file: Path -> validExtension(file) }
                    .count()
            fileCounts[path.parent] = count
            count > 20
        } catch (e: IOException) {
            throw RuntimeException("Unable to process directory", e)
        }
    }

    private fun validExtension(file: Path): Boolean {
        return extensions.contains(org.nd4j.shade.protobuf.common.io.Files.getFileExtension(file.toString()))
    }

    fun getTrainIter(): DataSetIterator {
        return trainDataIterator;
    }

    fun getTestIter(): DataSetIterator {
        return testDataIterator
    }

    companion object {
        private val allowedExtensions = BaseImageLoader.ALLOWED_FORMATS
        private val extensions: Set<String> = HashSet(Arrays.asList(*allowedExtensions))
        private val fileCounts = Hashtable<Path, Long>()
        private val labelMaker = ParentPathLabelGenerator()
        private lateinit var trainData: InputSplit
        private lateinit var testData: InputSplit
        lateinit var trainDataIterator: DataSetIterator;
        lateinit var testDataIterator: DataSetIterator;
        private const val height : Long = 224
        private const val width : Long  = 224
        private const val channels : Long  = 3
    }

    init {
        val collectionInputSplit = CollectionInputSplit(findAllImages(dataDirectory))
        val randomPathFilter = RandomPathFilter(Random(13), allowedExtensions, 0)
        val filesInDirSplit = collectionInputSplit.sample(randomPathFilter, trainDataPercentage.toDouble(), 100 - trainDataPercentage.toDouble())
        trainData = filesInDirSplit[0]
        testData = filesInDirSplit[1]
        initDateSetIterators(batchSize)
    }
}