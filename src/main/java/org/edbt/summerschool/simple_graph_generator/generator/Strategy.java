package org.edbt.summerschool.simple_graph_generator.generator;

import com.tinkerpop.blueprints.Graph;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by jonny on 01/09/15.
 */
public interface Strategy extends Callable<Graph> {

    static int numTriangles(Iterable<Integer> degreeSequence, double clusteringCoefficient) {

        double trianglesNeeded = 0;

        // calculate open triangles when edge degree is satisfied
        for (int degree : degreeSequence){
            trianglesNeeded += degree * (degree-1) / 2;
        }

        // use coefficient to determine number of triangles needed
        trianglesNeeded *= (clusteringCoefficient/3.0);

        return (int)trianglesNeeded;
    }

    static int openTriangles(Iterable<Integer> degreeSequence) {

        double trianglesNeeded = 0;

        // calculate open triangles when edge degree is satisfied
        for (int degree : degreeSequence){
            trianglesNeeded += degree * (degree-1) / 2;
        }


        return (int)trianglesNeeded;
    }

}
