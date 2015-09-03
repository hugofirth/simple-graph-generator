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
public class MaxSelectionStrategy implements SelectionStrategy {
    @Override
    public Iterable<Set<Vertex>> getCandidateIterable(Graph g) {


        int max1 = 0;
        int max2 = 0;
        for (Vertex v: g.getVertices()) {
            int degree = (int)v.getProperty("degreeDeficit");
            if (degree > max1) {
                max2 = max1;
                max1 = degree;
            }
        }


        // get vertices that have a high deficit

        Set<Vertex> allVertices = new HashSet<>();

        for (Vertex v: g.getVertices()) {
            if ((int)v.getProperty("degreeDeficit") >= max2 && (int)v.getProperty("degreeDeficit") > 0) {
                allVertices.add(v);
            }
        }


        // create all combinations
        LinkedList<Set<Vertex>> candidates = new LinkedList<>();

        for (Vertex v : allVertices) {
            for (Vertex w : allVertices) {
                if (v.equals(w) || edgeExists(v,w))
                    break;
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
