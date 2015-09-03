package org.edbt.summerschool.simple_graph_generator.generator;

/**
 * Created by jonny on 01/09/15.
 *
 * @author Jonny Daenen
 * @author Hugo Firth
 * @author Bas Ketsman
 */
public class OptimisationVector implements Comparable<OptimisationVector> {

    int triangleUpperLimit; // number of triangles that can be left
    int unfinishedVerticesUpperLimit;

    int numTrianglesLeft;
    int unfinishedVertices;
    int edgeDistance;

    public OptimisationVector(int triangleUpperLimit, int unfinishedVerticesUpperLimit, int numTrianglesLeft, int unfinishedVertices, int edgeDistance) {
        this.triangleUpperLimit = triangleUpperLimit;
        this.unfinishedVerticesUpperLimit = unfinishedVerticesUpperLimit;
        this.numTrianglesLeft = numTrianglesLeft;
        this.unfinishedVertices = unfinishedVertices;
        this.edgeDistance = edgeDistance;
    }

    public OptimisationVector(OptimisationVector optVector) {
        this.triangleUpperLimit = optVector.triangleUpperLimit;
        this.unfinishedVerticesUpperLimit = optVector.unfinishedVerticesUpperLimit;
        this.numTrianglesLeft = optVector.numTrianglesLeft;
        this.unfinishedVertices = optVector.unfinishedVertices;
        this.edgeDistance = optVector.edgeDistance;
    }

    public int getTriangleUpperLimit() {
        return triangleUpperLimit;
    }

    public int getUnfinishedVerticesUpperLimit() {
        return unfinishedVerticesUpperLimit;
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
    public int hashCode() {
        int result = triangleUpperLimit;
        result = 31 * result + unfinishedVerticesUpperLimit;
        result = 31 * result + numTrianglesLeft;
        result = 31 * result + unfinishedVertices;
        result = 31 * result + edgeDistance;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptimisationVector that = (OptimisationVector) o;

        if (triangleUpperLimit != that.triangleUpperLimit) return false;
        if (unfinishedVerticesUpperLimit != that.unfinishedVerticesUpperLimit) return false;
        if (numTrianglesLeft != that.numTrianglesLeft) return false;
        if (unfinishedVertices != that.unfinishedVertices) return false;
        return edgeDistance == that.edgeDistance;

    }

    /**
     * IMPROVE we need to extract this to a more generic comparator.
     * @param o
     * @return -1 when this object is smaller, 1 when the parameter is smaller, 0 when both can be considered equal
     */
    @Override
    public int compareTo(OptimisationVector o) {

        boolean tLimit1Exceeded = (numTrianglesLeft > triangleUpperLimit) || (numTrianglesLeft < -triangleUpperLimit);
        boolean tLimit2Exceeded = (o.numTrianglesLeft > o.triangleUpperLimit) || (o.numTrianglesLeft < -o.triangleUpperLimit);
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

    @Override
    public String toString() {
        return "OptimisationVector{" +
                "triangleUpperLimit=" + triangleUpperLimit +
                ", unfinishedVerticesUpperLimit=" + unfinishedVerticesUpperLimit +
                ", numTrianglesLeft=" + numTrianglesLeft +
                ", unfinishedVertices=" + unfinishedVertices +
                ", edgeDistance=" + edgeDistance +
                '}';
    }
}
