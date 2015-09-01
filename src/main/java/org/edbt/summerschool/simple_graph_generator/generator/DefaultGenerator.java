package org.edbt.summerschool.simple_graph_generator.generator;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.apache.commons.lang3.tuple.Pair;

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
public class DefaultGenerator implements Generator {

    private Iterable<Integer> degreeSubSequence;
    private int numTriangles;
    private SelectionStrategy selectionStrategy;


    public DefaultGenerator(Iterable<Integer> degreeSubSequence, int numTriangles) {
        this.degreeSubSequence = degreeSubSequence;
        this.numTriangles = numTriangles;
    }

    @Override
    public Graph call() throws Exception {


        // create empty graph
        Graph g = new TinkerGraph();

        for (int degree : degreeSubSequence) {
            Vertex v = g.addVertex(null);
            v.setProperty("degreeDeficit", degree);
        }

        // selection strategy
        Iterable<Set<Vertex>> candidates = selectionStrategy.getCandidateIterable(g);


        OptimisationVector optVector = new OptimisationVector();
        List<Pair<Vertex,Vertex>> optEdges = new LinkedList<>();

        for (Set<Vertex> candidateSet : candidates) {
            OptimisationVector newVector = considerEdges(g, candidateSet);

            // keep best vector
            if (newVector.compareTo(optVector) < 0) {
                optVector = newVector;


            }

            // stop when condition is met
            if (optimisationOverThreshold(newVector)){
                optVector = newVector;
                break;
            }


        }

        // actually connect vertices

        for (Pair<Vertex,Vertex> edge : optEdges) {
            g.addEdge(null,edge.getLeft(),edge.getRight(),"");
        }

        return g;
    }

    private OptimisationVector considerEdges(Graph g, Set<Vertex> candidateSet) {

        // add each edge

        // update the optimisation vector

        return null;
    }

    private boolean optimisationOverThreshold(OptimisationVector vector) {
        return false;
    }
}
