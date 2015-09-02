package org.edbt.summerschool.simple_graph_generator.generator;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by jonny on 01/09/15.
 *
 * @author Jonny Daenen
 * @author Hugo Firth
 * @author Bas Ketsman
 */
public class SimpleGenerator implements Generator {

    private Iterable<Integer> degreeSubSequence;
    private int numTriangles;
    private SelectionStrategy selectionStrategy;


    public SimpleGenerator(Iterable<Integer> degreeSubSequence, int numTriangles) {
        this.degreeSubSequence = degreeSubSequence;
        this.numTriangles = numTriangles;
    }

    @Override
    public Graph call() throws Exception {


        // create empty graph
        Graph g = new TinkerGraph();

        int totalDegreeDeficit = 0;
        int unfinishedNodes = 0;
        boolean minimalDegreeDeficit = false;

        int position = 0;
        for (int degree : degreeSubSequence) {

            Vertex v = g.addVertex(null);

            v.setProperty("degreeDeficit", degree);
            v.setProperty("position", position++);

            totalDegreeDeficit += degree;
            if (degree > 0)
                unfinishedNodes++;
        }



        OptimisationVector optVector = new OptimisationVector(numTriangles,unfinishedNodes,0);
        while (!minimalDegreeDeficit) {
            // selection strategy
            Iterable<Set<Vertex>> candidates = selectionStrategy.getCandidateIterable(g);


            boolean newOptFound = false;
            Set<Vertex> optCandidates = new HashSet<>();

            for (Set<Vertex> candidateSet : candidates) {
                OptimisationVector newVector = considerEdges(g, optVector, candidateSet);

                // keep best vector
                if (newVector.compareTo(optVector) < 0) {
                    optVector = newVector;
                    optCandidates = candidateSet;
                    newOptFound = true;
                }

                // stop when condition is met
                if (optimisationOverThreshold(newVector)) {
                    optVector = newVector;
                    optCandidates = candidateSet;
                    newOptFound = true;
                    break;
                }
            }

            // handle the case when no local optimum is found
            if (!newOptFound) {
                minimalDegreeDeficit = selectionStrategy.handleNoOptimalFound(g, optVector);
            } else {

                // actually connect vertices
                createEdges(optCandidates);
            }
        }

        return g;
    }

    /**
     * Creates the set of all edges between the given vertices.
     * @param candidateSet
     * @return
     */
    private void createEdges(Set<Vertex> candidateSet) {

        for (Vertex v : candidateSet) {
            for (Vertex w : candidateSet) {
                if (!v.equals(w)) {
                   addEdgeAndUpdateDeficit(v,w);

                }
            }
        }

    }

    private Edge addEdgeAndUpdateDeficit(Vertex v, Vertex w) {

        // create new edge
        Edge e = v.addEdge("",w);

        // update the degree deficit
        v.setProperty("degreeDeficit", (int)v.getProperty("degreeDeficit") - 1);
        w.setProperty("degreeDeficit",(int)w.getProperty("degreeDeficit") - 1);

        return e;
    }

    /**
     * Calculate the effect of adding edges on the optimisation vector.
     * @param g the graph
     * @param o the optimisation vector
     * @param candidateSet the set of vertices to connect to each other
     *
     * @return the optimisation vector after adding edges
     */
    private OptimisationVector considerEdges(Graph g, OptimisationVector o, Set<Vertex> candidateSet) {


        int newTriangles = 0;
        int distance = 0;
        int finished = 0;
        Set<Edge> temporaryEdges = new HashSet<>();

        // add each edge
        for (Vertex v : candidateSet) {
            for (Vertex w : candidateSet) {

                // check if its a new edge
                if (v.equals(w) || edgeExists(v,w)) {
                   break;
                }
                    // calculate the number of new triangles
                    newTriangles += calculateOpenTriangles(v, w);

                    // add edge
                    Edge e = addEdgeAndUpdateDeficit(v,w);
                temporaryEdges.add(e);


                    // check if these nodes are done
                    if ((int)v.getProperty("degreeDeficit") == 0)
                        finished++;

                    if ((int)w.getProperty("degreeDeficit") == 0)
                        finished++;

                    // add new distance
                    distance += Math.abs((int)v.getProperty("position") - (int)w.getProperty("position"));


            }
        }

        // restore graph
        for (Edge e : temporaryEdges) {
            g.removeEdge(e);
        }

        // update the optimisation vector
        return new OptimisationVector(o.getNumTriangles() - numTriangles, o.getUnfinishedVertices() - finished, o.getEdgeDistance()+ distance);

    }

    /**
     * TODO this is inefficient, but TinkerGraph does not allow checking edge existance...
     *
     * @param v first vertex
     * @param w second vertex
     * @return true when there exists an edge between the vertices
     */
    private boolean edgeExists(Vertex v, Vertex w) {
        for (Vertex u : v.getVertices(Direction.BOTH)){
            if (u.equals(w)){
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates how many open triangles end in given vertices
     * @param v first edge endpoint
     * @param w second edge endpoint
     * @return the number of open triangles ending in the given vertices
     */
    private int calculateOpenTriangles(Vertex v, Vertex w) {

        Iterable<Vertex> neighborsV = v.getVertices(Direction.BOTH);
        Iterable<Vertex> neighborsW = w.getVertices(Direction.BOTH);


        HashSet<Vertex> setV = new HashSet<Vertex>();
        for (Vertex neighbor : neighborsV)
            setV.add(neighbor);

        HashSet<Vertex> setW = new HashSet<Vertex>();
        for (Vertex neighbor : neighborsW)
            setW.add(neighbor);

        setV.retainAll(setW);
        return setV.size();
    }

    private boolean optimisationOverThreshold(OptimisationVector vector) {
        return false;
    }
}
