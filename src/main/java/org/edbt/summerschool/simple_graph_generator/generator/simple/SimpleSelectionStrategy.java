package org.edbt.summerschool.simple_graph_generator.generator.simple;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import org.edbt.summerschool.simple_graph_generator.generator.OptimisationVector;
import org.edbt.summerschool.simple_graph_generator.generator.SelectionStrategy;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by jonny on 02/09/15.
 *
 * @author Jonny Daenen
 */
public class SimpleSelectionStrategy implements SelectionStrategy {
    @Override
    public Iterable<Set<Vertex>> getCandidateIterable(Graph g) {

        // IMPROVE filter vertices


        // get vertices that have a deficit
        Set<Vertex> allVertices = new HashSet<>();
        for (Vertex v: g.getVertices()) {
            if ((int)v.getProperty("degreeDeficiency") > 0)
                allVertices.add(v);
        }


        // create all combinations
        LinkedList<Set<Vertex>> candidates = new LinkedList<>();

        for (Vertex v : allVertices) {
            for (Vertex w : allVertices) {
                Set<Vertex> set = new HashSet<>();
                set.add(v);
                set.add(w);
                candidates.add(set);
            }
        }

        return candidates;
    }

    @Override
    public boolean handleNoOptimalFound(Graph g, OptimisationVector optVector) {
        // if we cannot add an edge, we are done.
        return false;
    }
}