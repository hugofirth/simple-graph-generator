package org.edbt.summerschool.simple_graph_generator.generator;

import com.tinkerpop.blueprints.Graph;

/**
 * This class implements the strategy as described in the Masters' thesis of Nidhi Parikh
 * entitled "Generating Random Graphs with Tunable Clustering Coefficient".
 */
public class DEGStrategy implements Strategy {

    private Iterable<Integer> degreeSequence;
    private double clusteringCoefficient;

    public DEGStrategy(Iterable<Integer> degreeSequence, double clusteringCoefficient) {
        this.degreeSequence = degreeSequence;
        this.clusteringCoefficient = clusteringCoefficient;
    }

    @Override
    public Graph call() throws Exception {
        int numTriangles = Strategy.numTriangles(degreeSequence, clusteringCoefficient);
        return new DEGGenerator(degreeSequence, numTriangles).call();
    }
}
