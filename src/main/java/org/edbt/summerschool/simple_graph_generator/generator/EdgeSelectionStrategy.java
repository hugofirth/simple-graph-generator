package org.edbt.summerschool.simple_graph_generator.generator;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import java.util.Set;

/**
 * Created by jonny on 02/09/15.
 */
public class EdgeSelectionStrategy implements SelectionStrategy {
    @Override
    public Iterable<Set<Vertex>> getCandidateIterable(Graph g) {

        // TODO filter vertices
        // get vertices that have a deficit
        // create all combinations

        for (Vertex v: g.getVertices()) {
            for (Vertex w : g.getVertices()) {
                Set<Vertex> set = new HashSet<>();
            }
        }




        return null;
    }

    @Override
    public boolean handleNoOptimalFound(Graph g, OptimisationVector optVector) {
        // if we cannot add an edge, we are done.
        return false;
    }
}
