package org.edbt.summerschool.simple_graph_generator.graph;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jonny on 02/09/15.
 */
public class GraphMethods {

    /**
     * IMPROVE this method is inefficient, but TinkerGraph does not allow checking edge existance...
     *
     * @param v first vertex
     * @param w second vertex
     * @return true when there exists an edge between the vertices
     */
    public static boolean edgeExists(Vertex v, Vertex w) {
        for (Vertex u : v.getVertices(Direction.BOTH)){
            if (u.equals(w)){
                return true;
            }
        }
        return false;
    }


    public static Set<Vertex> neighborIntersection(Vertex v, Vertex w) {
        Iterable<Vertex> neighborsV = v.getVertices(Direction.BOTH);
        Iterable<Vertex> neighborsW = w.getVertices(Direction.BOTH);

        HashSet<Vertex> set = new HashSet<>();
        for (Vertex neighbor : neighborsV)
            set.add(neighbor);
        for (Vertex neighbor : neighborsW)
            set.add(neighbor);
        return set;
    }
}
