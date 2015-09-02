package org.edbt.summerschool.simple_graph_generator.graph;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

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


}
