package ultimaengineering.io.titleclassifier;

import org.datavec.api.io.filters.RandomPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.CollectionInputSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.BaseImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.nd4j.shade.protobuf.common.io.Files.getFileExtension;


public class DataIterator {

    private static final String [] allowedExtensions = BaseImageLoader.ALLOWED_FORMATS;
    private static final Set<String> extensions = new HashSet<>(Arrays.asList(allowedExtensions));
    private static final Hashtable<Path, Long> fileCounts = new Hashtable<>();
    private static ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
    private static InputSplit trainData,testData;
    private static final int height = 224;
    private static final int batchSize = 128;
    private static final int width = 224;
    private static final int channels = 3;

    public DataIterator(Path dataDirectory, Integer trainDataPercentage, Integer trainPercentage, Integer batchSize) throws IOException {
        CollectionInputSplit collectionInputSplit = new CollectionInputSplit(findAllImages(dataDirectory));
        RandomPathFilter randomPathFilter = new RandomPathFilter(new Random(13), allowedExtensions, 0);
        InputSplit[] filesInDirSplit = collectionInputSplit.sample(randomPathFilter, trainDataPercentage, 100-trainDataPercentage);
        trainData = filesInDirSplit[0];
        testData = filesInDirSplit[1];
    }

    public URI[] findAllImages(Path dataDir) throws IOException {
        return Files.walk(dataDir)
                .filter(Files::isRegularFile)
                .filter(this::validExtension)
                .filter(this::sufficientLabelData)
                .map(Path::toUri)
                .toArray(URI[]::new);
    }

    private DataSetIterator makeDataSetIterator(InputSplit inputSplit) throws IOException {
        ImageRecordReader recordReader = new ImageRecordReader(height, width, channels, labelMaker);
        recordReader.initialize(inputSplit);
        DataSetIterator iter =  new RecordReaderDataSetIterator(recordReader, batchSize, 1, recordReader.numLabels());
        return iter;
    }

    private boolean sufficientLabelData(Path path) {
        try {
            if (fileCounts.containsKey(path.getParent())) {
                return fileCounts.get(path.getParent()) > 20;
            }
            Long count = Files.walk(path.getParent())
                    .filter(Files::isRegularFile)
                    .filter(this::validExtension)
                    .count();
            fileCounts.put(path.getParent(), count);
            return count > 20;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process directory", e);
        }
    }

    private boolean validExtension(Path file) {
        return extensions.contains(getFileExtension(file.toString()));
    }
}
