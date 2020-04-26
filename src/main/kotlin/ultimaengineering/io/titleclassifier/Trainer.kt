package ultimaengineering.io.titleclassifier

import java.nio.file.*

class Trainer internal constructor(private val dataLocation: Path, private val modelLocation: Path, private val epochs: Int, private val batchSize: Int, private val trainPercentage: Int, private val previousModel: String) {
    fun beginTrain() {
        val iterConfig = TitleDataIterator(dataLocation, trainPercentage, batchSize)
        val trainIter = iterConfig.getTrainIter()
        val testIter = iterConfig.getTestIter()
        writeLabels(trainIter.labels)
        val network = Network(trainIter.labels.size, iterConfig.getIterationsPerEpoch(), modelLocation, testIter, previousModel).network
        network.fit(trainIter, epochs)
    }

    private fun writeLabels(labels: MutableList<String>) {
        val labelFile = Paths.get(modelLocation.toString(), "labels");
        Files.write(labelFile, labels, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    }
}