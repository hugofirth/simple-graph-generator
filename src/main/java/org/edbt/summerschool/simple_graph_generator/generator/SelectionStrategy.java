package org.edbt.summerschool.simple_graph_generator.generator;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import java.util.Set;

/**
 * Created by jonny on 01/09/15.
 */
public interface SelectionStrategy {

    public Iterable<Set<Vertex>> getCandidateIterable(Graph g);
}
