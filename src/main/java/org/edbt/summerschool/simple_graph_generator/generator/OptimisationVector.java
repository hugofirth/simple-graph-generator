package org.edbt.summerschool.simple_graph_generator.generator;

import java.util.Comparator;

/**
 * Created by jonny on 01/09/15.
 */
public class OptimisationVector implements Comparable<OptimisationVector> {


    int numTriangles;
    int unfinishedVertices;
    int edgeDistance;

    public OptimisationVector(int numTriangles, int unfinishedVertices, int edgeDistance) {
        this.numTriangles = numTriangles;
        this.unfinishedVertices = unfinishedVertices;
        this.edgeDistance = edgeDistance;
    }

    public int getNumTriangles() {
        return numTriangles;
    }

    public int getUnfinishedVertices() {
        return unfinishedVertices;
    }

    public int getEdgeDistance() {
        return edgeDistance;
    }

    @Override
    public int compareTo(OptimisationVector o) {
        // TODO implement
        return 0;
    }
}
