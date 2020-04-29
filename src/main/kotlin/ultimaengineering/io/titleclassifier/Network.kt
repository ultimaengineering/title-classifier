package ultimaengineering.io.titleclassifier

import org.deeplearning4j.nn.conf.CacheMode
import org.deeplearning4j.nn.conf.WorkspaceMode
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.optimize.api.BaseTrainingListener
import org.deeplearning4j.optimize.api.InvocationType
import org.deeplearning4j.optimize.listeners.CheckpointListener
import org.deeplearning4j.optimize.listeners.EvaluativeListener
import org.deeplearning4j.optimize.listeners.PerformanceListener
import org.deeplearning4j.optimize.listeners.TimeIterationListener
import org.deeplearning4j.ui.api.UIServer.getInstance
import org.deeplearning4j.ui.stats.StatsListener
import org.deeplearning4j.ui.storage.InMemoryStatsStorage
import org.deeplearning4j.util.ModelSerializer
import org.deeplearning4j.zoo.model.Darknet19
import org.nd4j.evaluation.classification.Evaluation
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import java.nio.file.Path

class Network internal constructor(private val numLabels: Int,
                                   private val estimatedIterations : Int,
                                   private val modelDirectory : Path,
                                   private val testDataSet : DataSetIterator,
                                   private val previousModel : String

) {
    private val seed = 12345
    val network: ComputationGraph
        get() {
            val model : ComputationGraph
            if(previousModel.isEmpty()) {
                model = Darknet19.builder()
                        .numClasses(numLabels)
                        .seed(seed.toLong())
                        .workspaceMode(WorkspaceMode.ENABLED)
                        .cacheMode(CacheMode.HOST)
                        .cudnnAlgoMode(ConvolutionLayer.AlgoMode.PREFER_FASTEST)
                        .build().init()
            } else {
                    model = ModelSerializer.restoreComputationGraph(previousModel, true)
                    model.init()
            }
            addListeners(model)
            return model
        }

    private fun addListeners(model: ComputationGraph) {
        model.setListeners(addUI(), getEvaluationListener(), getCheckPointListener(), getPerformanceListener(), getTimeIterationListener())
    }

    private fun addUI(): StatsListener {
        val ui = getInstance()
        val statsStorage = InMemoryStatsStorage()
        ui.attach(statsStorage)
        return StatsListener(statsStorage)
    }

    private fun getPerformanceListener(): BaseTrainingListener {
        return PerformanceListener(1, true)
    }

    private fun getEvaluationListener(): BaseTrainingListener {
        val eval = Evaluation()
        eval.stats(true, true)
        return EvaluativeListener(testDataSet, 1, InvocationType.EPOCH_END, eval)
    }

    private fun getCheckPointListener(): BaseTrainingListener {
        return CheckpointListener.Builder(modelDirectory.toFile())
                  .keepLast(1)
                  .saveEveryNIterations(10)
                  .logSaving(true)
                  .deleteExisting(true)
                  .build()
    }

    private fun getTimeIterationListener(): BaseTrainingListener {
        return TimeIterationListener(estimatedIterations)
    }
}