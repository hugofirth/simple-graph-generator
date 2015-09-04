package org.edbt.summerschool.simple_graph_generator.generator.deg;

import com.tinkerpop.blueprints.Graph;
import org.edbt.summerschool.simple_graph_generator.generator.Strategy;

/**
 * This class implements an adaptation of the strategy as described in the Masters' thesis of Nidhi Parikh
 * entitled "Generating Random Graphs with Tunable Clustering Coefficient".
 * NOTICE: does not work at the moment!
 */
public class AdaptedDEGStrategy implements Strategy {

    private Iterable<Integer> degreeSequence;
    private double clusteringCoefficient;

    public AdaptedDEGStrategy(Iterable<Integer> degreeSequence, double clusteringCoefficient) {
        this.degreeSequence = degreeSequence;
        this.clusteringCoefficient = clusteringCoefficient;
    }

    @Override
    public Graph call() throws Exception {
        int numTriangles = Strategy.numTriangles(degreeSequence, clusteringCoefficient);
        return new AdaptedDEGGenerator(degreeSequence, numTriangles).call();
    }
}
