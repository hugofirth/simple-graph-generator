package org.edbt.summerschool.simple_graph_generator.generator;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import java.util.Set;

/**
 * Created by jonny on 01/09/15.
 *
 * @author Jonny Daenen
 * @author Hugo Firth
 * @author Bas Ketsman
 */
public interface SelectionStrategy {

    /**
     * Generates sets of candidates that can be connected in the current iteration.
     *
     * @param g the current graph
     * @return a stream of vertex sets to be tried
     */
    public Iterable<Set<Vertex>> getCandidateIterable(Graph g, int numTriangles);

    /**
     * Handles the case when no candidate is accepted.
     *
     * @param g the current graph
     * @param optVector the optimisation vector of the graph
     * @return whether to continue with constructing the graph
     */
    public boolean handleNoOptimalFound(Graph g, OptimisationVector optVector);
}
