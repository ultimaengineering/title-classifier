package ultimaengineering.io.titleclassifier

import java.nio.file.Path

class Trainer internal constructor(private val dataLocation: Path, private val modelLocation: Path, private val epochs: Int, private val batchSize: Int, private val trainPercentage: Int) {
    fun beginTrain() {
        val iterConfig = TitleDataIterator(dataLocation, trainPercentage, batchSize)
        val trainIter = iterConfig.getTrainIter();
        val testIter = iterConfig.getTestIter();
        val network = Network(trainIter.labels.size, 1, modelLocation, testIter).network
        network.fit(trainIter, epochs)
    }
}