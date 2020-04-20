package ultimaengineering.io.titleclassifier

import org.deeplearning4j.nn.conf.CacheMode
import org.deeplearning4j.nn.conf.WorkspaceMode
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.zoo.model.Darknet19

class Network internal constructor(private val numLabels: Int) {
    private val seed = 12345
    val network: ComputationGraph
        get() {
            val model = Darknet19.builder()
                    .numClasses(numLabels)
                    .seed(seed.toLong())
                    .workspaceMode(WorkspaceMode.ENABLED)
                    .cacheMode(CacheMode.DEVICE)
                    .cudnnAlgoMode(ConvolutionLayer.AlgoMode.PREFER_FASTEST)
                    .build()
            return model.init()
        }
}