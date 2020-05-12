package ultimaengineering.io

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import ultimaengineering.io.titleclassifier.Trainer
import java.nio.file.Paths
import java.util.*

class App(parser: ArgParser) {
    val dataPath by parser.storing(
            "-d", "--data",
            help = "data to be trained on").default<String> {
            val env = System.getProperty("data")
            if (env.isNullOrEmpty()) {
                ""
            } else {
                env
            }
        }

    val modelPath by parser.storing(
            "-m", "--model",
            help = "path to models").default<String> {
        val env = System.getProperty("model")
        if (env.isNullOrEmpty()) {
            ""
        } else {
            env
        }
    }

    val previousModel by parser.storing(
            "-o", "--old_model", help = "path to previous model"
    ).default<String> {
        val env = System.getProperty("old_model")
        if (env.isNullOrEmpty()) {
            ""
        } else {
            env.toString()
        }
    }

    val epochs by parser.storing(
            "-e", "--epochs",
            help = "epochs to train"
    ) { toInt() }.default<Int>
        {
            val env = System.getProperty("epochs")
            if (env.isNullOrEmpty()) {
                20
            } else {
                env.toInt()
            }
        }

    val batchSize by parser.storing(
            "-b", "--batchSize",
            help = "selected batch size for training"
    ) { toInt() }.default<Int>
    {
        val env = System.getProperty("batchSize")
        if (env.isNullOrEmpty()) {
            32
        } else {
            env.toInt()
        }
    }

    val workers by parser.storing(
            "-g", "--gpus",
            help = "number of gpus to use"
    ) { toInt() }.default<Int> {
        val env = System.getProperty("gpus")
        if (env.isNullOrEmpty()) {
            1
        } else {
            env.toInt()
        }
    }

    val trainPercentage by parser.storing(
            "-t", "--trainingPercentage",
            help = "percentage of test data to use as a test set"
    ) { toInt() }.default<Int>
    {
        val env = System.getProperty("trainingPercentage")
        if (env.isNullOrEmpty()) {
            80
        } else {
            env.toInt()
        }
    }
}

fun main(args: Array<String>) {
    println(Arrays.toString(args))
    ArgParser(args).parseInto(::App).run {
        var dataDir = Paths.get(dataPath)
        val modelDir = Paths.get(modelPath)
        val trainer = Trainer(dataDir, modelDir, epochs, batchSize, trainPercentage, workers, previousModel)
        trainer.beginTrain()
    }
}