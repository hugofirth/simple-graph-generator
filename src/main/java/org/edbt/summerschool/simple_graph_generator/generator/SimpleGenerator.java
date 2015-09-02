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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by jonny on 01/09/15.
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


            Set<Vertex> optCandidates = new HashSet<>();

            for (Set<Vertex> candidateSet : candidates) {
                OptimisationVector newVector = considerEdges(g, optVector, candidateSet);

                // keep best vector
                if (newVector.compareTo(optVector) < 0) {
                    optVector = newVector;
                    optCandidates = candidateSet;
                }

                // stop when condition is met
                if (optimisationOverThreshold(newVector)) {
                    optVector = newVector;
                    optCandidates = candidateSet;
                    break;
                }
            }

            // TODO reason about degree deficit

            // actually connect vertices

            for (Pair<Vertex, Vertex> edge : createEdges(optCandidates)) {
                g.addEdge(null, edge.getLeft(), edge.getRight(), "");
                // TODO change degree deficit
            }
        }

        return g;
    }

    private List<Pair<Vertex, Vertex>> createEdges(Set<Vertex> candidateSet) {

        List<Pair<Vertex, Vertex>> edgeList = new LinkedList<>();
        for (Vertex v : candidateSet) {
            for (Vertex w : candidateSet) {
                if (!v.equals(w)) {
                    Pair<Vertex, Vertex> pair = Pair.of(v, w);
                    edgeList.add(pair);
                }
            }
        }

        return edgeList;

    }

    private OptimisationVector considerEdges(Graph g, OptimisationVector o, Set<Vertex> candidateSet) {


        int newTriangles = 0;
        int distance = 0;
        int finished = 0;
        Set<Edge> temporaryEdges = new HashSet<>();

        // add each edge
        for (Vertex v : candidateSet) {
            for (Vertex w : candidateSet) {

                // check if its a new edge
                if (!v.equals(w) /*&& !g.hasEdge(v,w)*/) { // TODO
                    // calculate the number of new triangles
                    newTriangles += calculateNewTriangles(v,w);

                    // add edge
                    Edge e = g.addEdge(null, v, w, "");
                    temporaryEdges.add(e);

                    // update the degree deficit
                    v.setProperty("degreeDeficit", (int)v.getProperty("degreeDeficit") - 1);
                    w.setProperty("degreeDeficit",(int)w.getProperty("degreeDeficit")-1);

                    // check if these nodes are done
                    if ((int)v.getProperty("degreeDeficit") == 0)
                        finished++;

                    if ((int)w.getProperty("degreeDeficit") == 0)
                        finished++;

                    // add new distance
                    distance += Math.abs((int)v.getProperty("position") - (int)w.getProperty("position"))

                }
            }
        }

        // restore graph
        for (Edge e : temporaryEdges) {
            g.removeEdge(e);
        }

        // update the optimisation vector
        return new OptimisationVector(o.getNumTriangles() - numTriangles, o.getUnfinishedVertices() - finished, o.getEdgeDistance()+ distance)

    }

    private int calculateNewTriangles(Vertex v, Vertex w) {

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
