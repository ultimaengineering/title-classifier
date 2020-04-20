package ultimaengineering.io.titleclassifier

import java.nio.file.Path

class Trainer internal constructor(private val dataLocation: Path, private val epochs: Int, private val batchSize: Int) {
    fun beginTrain() {

    }

    fun runNumbers(): Int {
        return 1
    }
}