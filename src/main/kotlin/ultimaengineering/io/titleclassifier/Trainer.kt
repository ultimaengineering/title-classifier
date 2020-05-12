package ultimaengineering.io.titleclassifier

import org.deeplearning4j.parallelism.ParallelWrapper
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


class Trainer internal constructor(private val dataLocation: Path, private val modelLocation: Path, private val epochs: Int, private val batchSize: Int, private val trainPercentage: Int, private val gpuWorkers: Int, private val previousModel: String) {
    fun beginTrain() {
        val iterConfig = TitleDataIterator(dataLocation, trainPercentage, batchSize)
        val trainIter = iterConfig.getTrainIter()
        val testIter = iterConfig.getTestIter()
        writeLabels(trainIter.labels)
        val network = Network(trainIter.labels.size, iterConfig.getIterationsPerEpoch(), modelLocation, testIter, previousModel).network
        if (gpuWorkers <= 1) {
            network.fit(trainIter, epochs)
        } else {
            val wrapper = ParallelWrapper.Builder(network)
                    .prefetchBuffer(gpuWorkers)
                    .workers(gpuWorkers)
                    .build()
            for(x in 0 until epochs)
                try {
                    wrapper.fit(trainIter)
                } catch (e :Exception) {
                    print(e.stackTrace)
                }
        }
    }

    private fun writeLabels(labels: MutableList<String>) {
        val labelFile = Paths.get(modelLocation.toString(), "labels");
        Files.write(labelFile, labels, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    }
}