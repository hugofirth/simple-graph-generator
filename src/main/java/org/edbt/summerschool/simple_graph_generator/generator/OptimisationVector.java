package org.edbt.summerschool.simple_graph_generator.generator;

/**
 * Created by jonny on 01/09/15.
 *
 * @author Jonny Daenen
 * @author Hugo Firth
 * @author Bas Ketsman
 */
public class OptimisationVector implements Comparable<OptimisationVector> {

    int triangleUpperLimit;
    int unfinishedVerticesUpperLimit;

    int numTrianglesLeft;
    int unfinishedVertices;
    int edgeDistance;

    public OptimisationVector(int numTriangles, int unfinishedVertices, int edgeDistance) {
        this.numTrianglesLeft = numTriangles;
        this.unfinishedVertices = unfinishedVertices;
        this.edgeDistance = edgeDistance;
    }

    public int getNumTrianglesLeft() {
        return numTrianglesLeft;
    }

    public int getUnfinishedVertices() {
        return unfinishedVertices;
    }

    public int getEdgeDistance() {
        return edgeDistance;
    }

    @Override
    public int compareTo(OptimisationVector o) {

        boolean tLimit1Exceeded = (numTrianglesLeft > triangleUpperLimit);
        boolean tLimit2Exceeded = (o.numTrianglesLeft > o.triangleUpperLimit);
        // if only one exceeds the limit, return the other one
        if (!tLimit1Exceeded && tLimit2Exceeded)
            return -1;
        if (tLimit1Exceeded && !tLimit2Exceeded)
            return 1;


        boolean uvLimit1Exceeded = (unfinishedVertices > unfinishedVerticesUpperLimit);
        boolean uvLimit2Exceeded = (o.unfinishedVertices > o.unfinishedVerticesUpperLimit);
        // if only one exceeds the limit, return the other one
        if (!uvLimit1Exceeded && uvLimit2Exceeded)
            return -1;
        if (uvLimit1Exceeded && !uvLimit2Exceeded)
            return 1;


        // recall: < 0 => current object is smaller
        if (numTrianglesLeft != o.numTrianglesLeft)
            return numTrianglesLeft - numTrianglesLeft;

        if (unfinishedVertices != o.unfinishedVertices)
            return unfinishedVertices - o.unfinishedVertices;

        if (edgeDistance != o.edgeDistance)
            return edgeDistance - o.edgeDistance;

        return 0;
    }
}
