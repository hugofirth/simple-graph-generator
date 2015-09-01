package org.edbt.summerschool.simple_graph_generator.generator;

import java.util.Comparator;

/**
 * Created by jonny on 01/09/15.
 */
public class OptimisationVector implements Comparable<OptimisationVector> {

    int numTriangles;
    double degreeDerivation;
    int edgeDistance;

    public OptimisationVector(int numTriangles, int edgeDistance, double degreeDerivation) {
        this.numTriangles = numTriangles;
        this.edgeDistance = edgeDistance;
        this.degreeDerivation = degreeDerivation;
    }

    public int getNumTriangles() {
        return numTriangles;
    }

    public double getDegreeDerivation() {
        return degreeDerivation;
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
