package org.edbt.summerschool.simple_graph_generator.generator;

import java.util.Comparator;

/**
 * Created by jonny on 01/09/15.
 */
public class OptimisationVector implements Comparable<OptimisationVector> {

    double clusteringCoefficient;
    double degreeDerivation;
    int edgeDistance;

    public OptimisationVector(double clusteringCoefficient, int edgeDistance, double degreeDerivation) {
        this.clusteringCoefficient = clusteringCoefficient;
        this.edgeDistance = edgeDistance;
        this.degreeDerivation = degreeDerivation;
    }

    public double getClusteringCoefficient() {
        return clusteringCoefficient;
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
