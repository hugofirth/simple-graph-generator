package org.edbt.summerschool.simple_graph_generator.generator;

import com.tinkerpop.blueprints.Graph;

import java.util.concurrent.Future;

/**
 * Created by jonny on 01/09/15.
 */
public interface Strategy {

    public Future<Graph> generate(Iterable<Integer> degreeSequence, double clusteringCoefficient);

    public static int numTriangles(Iterable<Integer> degreeSequence, double clusteringCoefficient) {
        // TODO add triangle formula
        return 0;
    }

}