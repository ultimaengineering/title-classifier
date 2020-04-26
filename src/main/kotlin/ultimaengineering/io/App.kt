package ultimaengineering.io

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import ultimaengineering.io.titleclassifier.Trainer
import java.nio.file.Paths

class App(parser: ArgParser) {
    val dataPath by parser.storing(
            "-d", "--data",
            help = "data to be trained on")

    val modelPath by parser.storing(
            "-m", "--model",
            help = "path to models")

    val previousModel by parser.storing(
            "-o", "--old_model", help = "path to previous model"
    ).default("")

    val epochs by parser.storing(
            "-e", "--epochs",
            help = "epochs to train"
    ) { toInt() }

    val batchSize by parser.storing(
            "-b", "--batchSize",
            help = "selected batch size for training"
    ) { toInt() }

    val trainPercentage by parser.storing(
            "-t", "--trainingPercentage",
            help = "percentage of test data to use as a test set"
    ) { toInt() }
}

fun main(args: Array<String>) {
    ArgParser(args).parseInto(::App).run {
        var dataDir = Paths.get(dataPath)
        val modelDir = Paths.get(modelPath)
        val trainer = Trainer(dataDir, modelDir, epochs, batchSize, trainPercentage, previousModel)
        trainer.beginTrain()
    }
}