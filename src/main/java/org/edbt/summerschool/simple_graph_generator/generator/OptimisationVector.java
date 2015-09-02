package org.edbt.summerschool.simple_graph_generator.generator;

/**
 * Created by jonny on 01/09/15.
 *
 * @author Jonny Daenen
 * @author Hugo Firth
 * @author Bas Ketsman
 */
public class OptimisationVector implements Comparable<OptimisationVector> {


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
