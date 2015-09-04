package org.edbt.summerschool.simple_graph_generator.generator.simple;


import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.edbt.summerschool.simple_graph_generator.generator.Generator;
import org.edbt.summerschool.simple_graph_generator.generator.OptimisationVector;
import org.edbt.summerschool.simple_graph_generator.generator.SelectionStrategy;
import org.edbt.summerschool.simple_graph_generator.graph.GraphMethods;
import org.edbt.summerschool.simple_graph_generator.generator.heuristic.MaxSelectionStrategy;
import org.edbt.summerschool.simple_graph_generator.generator.heuristic.TriangleSelectionStrategy;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static org.edbt.summerschool.simple_graph_generator.generator.Strategy.openTriangles;
import static org.edbt.summerschool.simple_graph_generator.graph.GraphMethods.edgeExists;


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


    public SimpleGenerator(Iterable<Integer> degreeSubSequence, int numTriangles, SelectionStrategy selectionStrategy) {
        this.degreeSubSequence = degreeSubSequence;
        this.numTriangles = numTriangles;
        this.selectionStrategy = selectionStrategy;
    }

    public void setSelectionStrategy(SelectionStrategy selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
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
        int n = position;
        int m = totalDegreeDeficit;

        OptimisationVector optVector = new OptimisationVector((int)(numTriangles*0.05),(int)(position*0.05),numTriangles,unfinishedNodes,totalDegreeDeficit/2,0);
        while (!minimalDegreeDeficit) {

//            System.out.println("---");
            OptimisationVector bestOpt = new OptimisationVector(optVector);
//            System.out.println("vector: " + optVector);
//            printDeficits(g);

            // selection strategy
            Iterable<Set<Vertex>> candidates = selectionStrategy.getCandidateIterable(g, 0);
//            System.out.println(candidates);

            boolean newOptFound = false;
            Set<Vertex> optCandidates = new HashSet<>();

            for (Set<Vertex> candidateSet : candidates) {

                OptimisationVector newVector = considerEdges(g, optVector, candidateSet);

                // keep best vector
                if (newVector.compareTo(bestOpt) < 0) {
                    bestOpt = newVector;
                    optCandidates = candidateSet;
                    newOptFound = true;
                }

                // stop when condition is met
                if (optimisationOverThreshold(newVector)) {
                    bestOpt = newVector;
                    optCandidates = candidateSet;
                    newOptFound = true;
                    break;
                }
            }

            // TODO we need a better stopping condition

            // handle the case when no local optimum is found
            if (!newOptFound) {
                minimalDegreeDeficit = selectionStrategy.handleNoOptimalFound(g, optVector);
            } else {
                optVector = bestOpt;
                // actually connect vertices
                createEdges(optCandidates);
            }

        }

        LinkedList<Integer> newDegSeq = new LinkedList<>();
        for (Vertex v : g.getVertices()) {
            int i = 0;
            for (Edge e : v.getEdges(Direction.BOTH)) {
                i++;
            }
            newDegSeq.add(i);
        }

        int sat_n = n - optVector.getUnfinishedVertices();
        int out_m = totalDegreeDeficit/2 - optVector.getEdgesLeft();
        int locality = optVector.getEdgeDistance();

//        System.out.print(sat_n + ", " + out_m + ", " + "$TODOCC" + ", " + locality);


        System.out.println("vector: " + optVector);


        return g;
    }

    private void printDeficits(Graph g) {
        for (Vertex v : g.getVertices()) {
            System.out.print((int)v.getProperty("degreeDeficit") + " ");
        }
        System.out.println();
    }

    /**
     * Creates the set of all edges between the given vertices.
     * @param candidateSet
     * @return
     */
    public void createEdges(Set<Vertex> candidateSet) {


//        System.out.println("creating edges between: " + candidateSet);

        for (Vertex v : candidateSet) {
            for (Vertex w : candidateSet) {
                   addEdgeAndUpdateDeficit(v,w);

            }
        }

    }

    private Edge addEdgeAndUpdateDeficit(Vertex v, Vertex w) {


        if ((int)v.getProperty("position") > (int)w.getProperty("position")) {
            Vertex help = v;
            v = w;
            w = help;
        }

        if (edgeExists(v,w) || (int)v.getProperty("position") == (int)w.getProperty("position"))
            return null;

        // create new edge
        Edge e = v.addEdge("",w);

        // update the degree deficit
        v.setProperty("degreeDeficit", (int) v.getProperty("degreeDeficit") - 1);
        w.setProperty("degreeDeficit", (int) w.getProperty("degreeDeficit") - 1);

        return e;
    }

    private void removeEdgeAndUpdateDeficit(Graph g, Edge e) {


        Vertex v = e.getVertex(Direction.IN);
        Vertex w = e.getVertex(Direction.OUT);


        if ((int)v.getProperty("position") >= (int)w.getProperty("position")) {
            Vertex help = v;
            v = w;
            w = help;
        }

        // create new edge
        g.removeEdge(e);

        // update the degree deficit
        v.setProperty("degreeDeficit", (int) v.getProperty("degreeDeficit") + 1);
        w.setProperty("degreeDeficit", (int) w.getProperty("degreeDeficit") + 1);

    }

    /**
     * Calculate the effect of adding edges on the optimisation vector.
     * @param g the graph
     * @param o the optimisation vector
     * @param candidateSet the set of vertices to connect to each other
     *
     * @return the optimisation vector after adding edges
     */
    public OptimisationVector considerEdges(Graph g, OptimisationVector o, Set<Vertex> candidateSet) {

//        System.out.println("Considering edges between: " + candidateSet);

        int newTriangles = 0;
        int distance = 0;
        int finished = 0;
        int edgesAdded = 0;
        Set<Edge> temporaryEdges = new HashSet<>();

        // add each edge
        for (Vertex v : candidateSet) {
            for (Vertex w : candidateSet) {

                // check if its a new edge and nodes are in right order and different
                if (edgeExists(v,w) || (int)v.getProperty("position") >= (int)w.getProperty("position")) {
                   continue;
                }

                int vDef = (int)v.getProperty("degreeDeficit");
                int wDef = (int)w.getProperty("degreeDeficit");

                // calculate the number of new triangles
                newTriangles += calculateOpenTriangles(v, w);

                // add edge
                Edge e = addEdgeAndUpdateDeficit(v,w);
                edgesAdded++;
                temporaryEdges.add(e);


                // FIXME check if these nodes are done
                if ((int)v.getProperty("degreeDeficit") == 0 && vDef > 0)
                    finished++;

                if ((int)w.getProperty("degreeDeficit") == 0 && wDef > 0)
                    finished++;

                // add new distance
                distance += Math.abs((int)v.getProperty("position") - (int)w.getProperty("position"));


            }
        }

        // restore graph
        for (Edge e : temporaryEdges) {
            removeEdgeAndUpdateDeficit(g,e);
        }

        // update the optimisation vector
        OptimisationVector opt = new OptimisationVector(o.getTriangleUpperLimit(),o.getUnfinishedVerticesUpperLimit(),o.getNumTrianglesLeft() - newTriangles, o.getUnfinishedVertices() - finished, o.getEdgesLeft() - edgesAdded, o.getEdgeDistance()+ distance);
//        System.out.println("Prev Vector: " + o);
//        System.out.println("New Vector: "+ opt);
        return opt;
    }



    /**
     * Calculates how many open triangles end in the given vertices.
     *
     * @param v first edge endpoint
     * @param w second edge endpoint
     * @return the number of open triangles ending in the given vertices
     */
    private int calculateOpenTriangles(Vertex v, Vertex w) {
        return GraphMethods.neighborIntersection(v, w).size();
    /* old
        Iterable<Vertex> neighborsV = v.getVertices(Direction.BOTH);
        Iterable<Vertex> neighborsW = w.getVertices(Direction.BOTH);

        HashSet<Vertex> setV = new HashSet<>();
        for (Vertex neighbor : neighborsV)
            setV.add(neighbor);

        HashSet<Vertex> setW = new HashSet<>();
        for (Vertex neighbor : neighborsW)
            setW.add(neighbor);

        setV.retainAll(setW);
        return setV.size();
        /*/
    }

    private boolean optimisationOverThreshold(OptimisationVector vector) {
        return false;
    }
}
