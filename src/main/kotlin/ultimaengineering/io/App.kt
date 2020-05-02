package ultimaengineering.io

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import ultimaengineering.io.titleclassifier.Trainer
import java.nio.file.Paths

class App(parser: ArgParser) {
    val dataPath by parser.storing(
            "-d", "--data",
            help = "data to be trained on").default("/data")

    val modelPath by parser.storing(
            "-m", "--model",
            help = "path to models").default("/model")

    val previousModel by parser.storing(
            "-o", "--old_model", help = "path to previous model"
    ).default("")

    val epochs by parser.storing(
            "-e", "--epochs",
            help = "epochs to train"
    ) { toInt() }.default(20)

    val batchSize by parser.storing(
            "-b", "--batchSize",
            help = "selected batch size for training"
    ) { toInt() }.default(56)

    val workers by parser.storing(
            "-g", "--gpus",
            help = "number of gpus to use"
    ) { toInt() }.default(0)

    val trainPercentage by parser.storing(
            "-t", "--trainingPercentage",
            help = "percentage of test data to use as a test set"
    ) { toInt() }.default(80)
}

fun main(args: Array<String>) {
    print(args)
    ArgParser(args).parseInto(::App).run {
        var dataDir = Paths.get(dataPath)
        val modelDir = Paths.get(modelPath)
        val trainer = Trainer(dataDir, modelDir, epochs, batchSize, trainPercentage, workers, previousModel)
        trainer.beginTrain()
    }
}