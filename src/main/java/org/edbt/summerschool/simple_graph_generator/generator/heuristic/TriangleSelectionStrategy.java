package org.edbt.summerschool.simple_graph_generator.generator.heuristic;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import org.edbt.summerschool.simple_graph_generator.generator.OptimisationVector;
import org.edbt.summerschool.simple_graph_generator.generator.SelectionStrategy;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static org.edbt.summerschool.simple_graph_generator.graph.GraphMethods.edgeExists;

/**
 * Created by jonny on 02/09/15.
 * FIXME this gives negative degreeDeficits!
 *
 * @author Jonny Daenen
 */
public class TriangleSelectionStrategy implements SelectionStrategy {
    @Override
    public Iterable<Set<Vertex>> getCandidateIterable(Graph g, int numTriangles) {

        // IMPROVE filter vertices


        // get vertices that have a deficit of more than 1
        Set<Vertex> allVertices = new HashSet<>();
        for (Vertex v: g.getVertices()) {
            if ((int)v.getProperty("degreeDeficit") > 1) {
                allVertices.add(v);
            }
        }


        // create all combinations
        LinkedList<Set<Vertex>> candidates = new LinkedList<>();

        for (Vertex v : allVertices) {
            for (Vertex w : allVertices) {
                for (Vertex u : allVertices) {
                    if (v.equals(w) || u.equals(v) || v.equals(u) || (edgeExists(u, v) && edgeExists(v, w) && edgeExists(w, u)))
                        continue;
                    Set<Vertex> set = new HashSet<>();
                    set.add(v);
                    set.add(w);
                    set.add(u);
                    candidates.add(set);
                }
            }
        }

        // get vertices that have a deficit of 1
        Set<Vertex> allVertices2 = new HashSet<>();
        for (Vertex v: g.getVertices()) {
            if ((int)v.getProperty("degreeDeficit") == 1) {
                allVertices.add(v);
            }
        }

        for (Vertex v : allVertices2) {
            for (Vertex w : allVertices) {
                if (v.equals(w) || edgeExists(v,w))
                    continue;
                Set<Vertex> set = new HashSet<>();
                set.add(v);
                set.add(w);
                candidates.add(set);
            }
            for (Vertex w : allVertices2) {
                if (v.equals(w) || edgeExists(v,w))
                    continue;
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
        return true;
    }
}
