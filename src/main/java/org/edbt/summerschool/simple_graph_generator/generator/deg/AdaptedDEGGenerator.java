package org.edbt.summerschool.simple_graph_generator.generator.deg;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.edbt.summerschool.simple_graph_generator.generator.Generator;
import org.edbt.summerschool.simple_graph_generator.graph.GraphMethods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


/**
 * This class implements the generator as described in the Masters' thesis of Nidhi Parikh
 * entitled "Generating Random Graphs with Tunable Clustering Coefficient".
 */
public class AdaptedDEGGenerator implements Generator {

    private Iterable<Integer> degreeSubSequence;
    private int numTriangles;
    final int MAX_NUM_OF_TRIES = 20;

    public AdaptedDEGGenerator(Iterable<Integer> degreeSubSequence, int numTriangles) {
        this.degreeSubSequence = degreeSubSequence;
        this.numTriangles = numTriangles;
    }

    @Override
    public Graph call() throws Exception {

        // create empty graph
        Graph g = new TinkerGraph();

        DegreeDeficitVector degreeDeficit = new DegreeDeficitVector();

        int position = 0;
        for (int degree : degreeSubSequence) {
            Vertex v = g.addVertex(position); // We assume that the graph library (always) uses 'position' as vertex id
            int realPosition = degreeDeficit.addDegree(degree);
            assert(realPosition == position);
            position++;
        }

        boolean nothingToAdd = false;
        int loopIterations = 0;
        while (numTriangles > 0 && !nothingToAdd && loopIterations < MAX_NUM_OF_TRIES) {
            Integer a = degreeDeficit.randomNodePosition();
            if (a != null) {
                Vertex vertexA = g.getVertex(a);

                // create copy of degreeDeficit in which all neighbours of a, including a itself get degree 0
                DegreeDeficitVector tmpA = degreeDeficit.clone();
                tmpA.update(a, 0);
                for (Vertex neighbor : vertexA.getVertices(Direction.BOTH)) {
                    tmpA.update((int) neighbor.getId(), 0);
                }

                Integer b = tmpA.randomNodePosition();
                if (b != null) {
                    Vertex vertexB = g.getVertex(a);

                    Set<Vertex> intersectionAB = GraphMethods.neighborIntersection(vertexA, vertexB);
                    if (intersectionAB.size() > 0) {
                        if (numTriangles - intersectionAB.size() < 0) {
                            loopIterations++;
                            continue;
                        } else {
                            vertexA.addEdge("", vertexB);
                            degreeDeficit.decrease(b);
                            degreeDeficit.decrease(a);
                            numTriangles -= intersectionAB.size();
                            loopIterations = 0;
                        }
                    } else { // intersection is empty
                        DegreeDeficitVector tmpB = tmpA.clone();
                        tmpB.update(a, 0);

                        Integer c = tmpB.randomNodePosition();
                        if (c != null) {
                            Vertex vertexC = g.getVertex(c);

                            int numberOfNewTriangles = GraphMethods.neighborIntersection(vertexA, vertexC).size() +
                                    GraphMethods.neighborIntersection(vertexB, vertexC).size() + 1;
                            if (numTriangles - numberOfNewTriangles < 0) {
                                loopIterations++;
                                continue;
                            } else {
                                numTriangles -= numberOfNewTriangles;

                                if (!GraphMethods.edgeExists(vertexA, vertexB)) {
                                    degreeDeficit.decrease(a);
                                    degreeDeficit.decrease(b);
                                    vertexA.addEdge("", vertexB);
                                }
                                if (!GraphMethods.edgeExists(vertexB, vertexC)) {
                                    degreeDeficit.decrease(b);
                                    degreeDeficit.decrease(c);
                                    vertexB.addEdge("", vertexC);
                                }
                                if (!GraphMethods.edgeExists(vertexA, vertexC)) {
                                    degreeDeficit.decrease(a);
                                    degreeDeficit.decrease(c);
                                    vertexA.addEdge("", vertexC);
                                }
                                loopIterations = 0;
                            }
                        } else {
                            nothingToAdd = true;
                        }
                    }
                } else {
                        nothingToAdd = true;
                    }
                } else {
                    nothingToAdd = true;
                }
            }
        return g;
    }
}
