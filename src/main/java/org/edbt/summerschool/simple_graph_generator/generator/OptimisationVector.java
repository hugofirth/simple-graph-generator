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
    int edgesLeft;
    int edgeDistance;


    public OptimisationVector(int triangleUpperLimit, int unfinishedVerticesUpperLimit, int numTrianglesLeft, int unfinishedVertices, int edgesLeft, int edgeDistance) {
        this.numTrianglesLeft = numTrianglesLeft;
        this.triangleUpperLimit = triangleUpperLimit;
        this.unfinishedVerticesUpperLimit = unfinishedVerticesUpperLimit;
        this.unfinishedVertices = unfinishedVertices;
        this.edgesLeft = edgesLeft;
        this.edgeDistance = edgeDistance;
    }

    public OptimisationVector(OptimisationVector optVector) {
        this.triangleUpperLimit = optVector.triangleUpperLimit;
        this.unfinishedVerticesUpperLimit = optVector.unfinishedVerticesUpperLimit;
        this.numTrianglesLeft = optVector.numTrianglesLeft;
        this.unfinishedVertices = optVector.unfinishedVertices;
        this.edgesLeft = optVector.edgesLeft;
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

    public int getEdgesLeft() {
        return edgesLeft;
    }

    @Override
    public int hashCode() {
        int result = triangleUpperLimit;
        result = 31 * result + unfinishedVerticesUpperLimit;
        result = 31 * result + numTrianglesLeft;
        result = 31 * result + unfinishedVertices;
        result = 31 * result + edgesLeft;
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
        if (edgesLeft != that.edgesLeft) return false;
        return edgeDistance == that.edgeDistance;

    }
//
//    /**
//     * IMPROVE we need to extract this to a more generic comparator.
//     * @param o
//     * @return -1 when this object is smaller, 1 when the parameter is smaller, 0 when both can be considered equal
//     */
//    @Override
////    public int compareTo(OptimisationVector o) {
////        int tri = numTrianglesLeft - numTrianglesLeft;
////        int unf = unfinishedVertices - o.unfinishedVertices;
////        int el = edgesLeft - o.edgesLeft;
////        int ed = edgeDistance - o.edgeDistance;
////    }

    /**
     * IMPROVE we need to extract this to a more generic comparator.
     * @param o
     * @return -1 when this object is smaller, 1 when the parameter is smaller, 0 when both can be considered equal
     */
    @Override
    public int compareTo(OptimisationVector o) {

        // recall: < 0 => current object is smaller

        boolean tLimit1Exceeded = (numTrianglesLeft > triangleUpperLimit) || (numTrianglesLeft < -triangleUpperLimit);
        boolean tLimit2Exceeded = (o.numTrianglesLeft > o.triangleUpperLimit) || (o.numTrianglesLeft < -o.triangleUpperLimit);

        // if only one exceeds the limit, return the other one
        if (!tLimit1Exceeded && tLimit2Exceeded)
            return -1;
        if (tLimit1Exceeded && !tLimit2Exceeded)
            return 1;

        boolean tbLimit1Exceeded = (numTrianglesLeft < -triangleUpperLimit);
        boolean tbLimit2Exceeded = (o.numTrianglesLeft < -o.triangleUpperLimit);

        // if both exceed the bottom limit differently, return difference
//        if (tbLimit1Exceeded && tbLimit2Exceeded && numTrianglesLeft - o.numTrianglesLeft != 0) {
//            return -(numTrianglesLeft - o.numTrianglesLeft);
//        }


        boolean uvLimit1Exceeded = (unfinishedVertices > unfinishedVerticesUpperLimit);
        boolean uvLimit2Exceeded = (o.unfinishedVertices > o.unfinishedVerticesUpperLimit);

        // if only one exceeds the limit, return the other one
        if (!uvLimit1Exceeded && uvLimit2Exceeded)
            return -1;
        if (uvLimit1Exceeded && !uvLimit2Exceeded)
            return 1;


        if (numTrianglesLeft != o.numTrianglesLeft)
            return numTrianglesLeft - o.numTrianglesLeft;

        if (unfinishedVertices != o.unfinishedVertices)
            return unfinishedVertices - o.unfinishedVertices;

        if (edgesLeft != o.edgesLeft)
            return edgesLeft - o.edgesLeft;

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
                ", edgesLeft=" + edgesLeft +
                ", edgeDistance=" + edgeDistance +
                '}';
    }
}
