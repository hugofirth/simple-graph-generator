package org.edbt.summerschool.simple_graph_generator.generator.deg;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.edbt.summerschool.simple_graph_generator.generator.Generator;
import org.edbt.summerschool.simple_graph_generator.generator.OptimisationVector;
import org.edbt.summerschool.simple_graph_generator.generator.simple.SimpleGenerator;
import org.edbt.summerschool.simple_graph_generator.graph.GraphMethods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


/**
 * This class implements the generator as described in the Masters' thesis of Nidhi Parikh
 * entitled "Generating Random Graphs with Tunable Clustering Coefficient".
 */
public class AdaptedDEGGenerator extends SimpleGenerator implements Generator {

    private Iterable<Integer> degreeSubSequence;
    private int numTriangles;
    final int MAX_NUM_OF_TRIES = 20;

    public AdaptedDEGGenerator(Iterable<Integer> degreeSubSequence, int numTriangles) {
        super(null, 0, null);

        this.degreeSubSequence = degreeSubSequence;
        this.numTriangles = numTriangles;
    }

    @Override
    public Graph call() throws Exception {
        System.out.println("Num triangles");
System.out.println(numTriangles);
        // create empty graph
        Graph g = new TinkerGraph();

        DegreeDeficitVector degreeDeficit = new DegreeDeficitVector();

        int position = 0;
        for (int degree : degreeSubSequence) {
            Vertex v = g.addVertex(null);
            v.setProperty("position", new Integer(position));
            v.setProperty("degreeDeficit", new Integer(0));
            int realPosition = degreeDeficit.addDegree(degree);
            assert(realPosition == position);
            position++;
        }

        boolean nothingToAdd = false;
        int loopIterations = 0;
        while (numTriangles > 0 && !nothingToAdd && loopIterations < MAX_NUM_OF_TRIES) {
            Integer a = degreeDeficit.randomNodePosition();
            degreeDeficit.testPrint();
            if (a != null) {
                System.out.println("a:" + Integer.toString(a));

                Vertex vertexA = g.getVertex(a);

                // create copy of degreeDeficit in which all neighbours of a, including a itself get degree 0
                DegreeDeficitVector tmpA = degreeDeficit.clone();
                tmpA.update(a, 0);
                //for (Vertex neighbor : vertexA.getVertices(Direction.BOTH)) {
                //    System.out.println((String) neighbor.getId());
                //    tmpA.update((Integer) neighbor.getProperty("position"), 0);
               // }
                tmpA.testPrint();


                Integer b = tmpA.randomNodePosition();
                if (b != null) {
                    System.out.println("b:" + Integer.toString(b));

                    Vertex vertexB = g.getVertex(b);

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
                        tmpB.update(b, 0);
                        tmpB.testPrint();

                        Integer c = tmpB.randomNodePosition();
                        if (c != null) {
                            System.out.println("c:" + Integer.toString(c));

                            Vertex vertexC = g.getVertex(c);


                            Set<Vertex> vertexes = new HashSet<>();
                            vertexes.add(vertexA);
                            vertexes.add(vertexB);
                            vertexes.add(vertexC);

                            OptimisationVector v = new OptimisationVector(0,0,0,0,0,0);
                            v = considerEdges(g, v, vertexes);
                            if (numTriangles + v.getNumTrianglesLeft() < 0) {
                                loopIterations++;
                                continue;
                            } else {
                                createEdges(vertexes);
                            }

                            /*


                            int needsABEdge = GraphMethods.edgeExists(vertexA, vertexB)? 0 : 1;
                            int needsACEdge = GraphMethods.edgeExists(vertexA, vertexC)? 0 : 1;
                            int needsBCEdge = GraphMethods.edgeExists(vertexB, vertexC)? 0 : 1;




                            Edge ABEdge = null;
                            Edge ACEdge = null;
                            Edge BCEdge = null;

                            // try to add
                            if (needsABEdge == 1) {
                                degreeDeficit.decrease(a);
                                degreeDeficit.decrease(b);

                                addEdgeAndUpdate(a,b);


                                vertexA.addEdge("", vertexB);


                            }




                            if (needsBCEdge == 1) {
                                degreeDeficit.decrease(b);
                                degreeDeficit.decrease(c);
                                vertexB.addEdge("", vertexC);
                            }
                            if (needsACEdge == 1) {
                                degreeDeficit.decrease(a);
                                degreeDeficit.decrease(c);
                                vertexA.addEdge("", vertexC);
                            }

                            // TODO check how many



                            int numberOfNewTriangles = (GraphMethods.neighborIntersection(vertexA, vertexC).size()*(needsACEdge == 0? 1 : 0)) +
                                    (GraphMethods.neighborIntersection(vertexB, vertexC).size()*(needsBCEdge == 0? 1 : 0)) + 1;
                            System.out.println("New triangles:" + Integer.toString(numberOfNewTriangles));
                            if (numTriangles - numberOfNewTriangles < 0) {
                                loopIterations++;
                                continue;
                            } else {

                                if (degreeDeficit.getDegree(a) >= needsABEdge+needsACEdge
                                        && degreeDeficit.getDegree(b) >= needsABEdge+needsBCEdge
                                        && degreeDeficit.getDegree(c) >= needsBCEdge+needsACEdge) {
                                    numTriangles -= numberOfNewTriangles;

                                    if (needsABEdge == 1) {
                                        degreeDeficit.decrease(a);
                                        degreeDeficit.decrease(b);
                                        vertexA.addEdge("", vertexB);
                                    }
                                    if (needsBCEdge == 1) {
                                        degreeDeficit.decrease(b);
                                        degreeDeficit.decrease(c);
                                        vertexB.addEdge("", vertexC);
                                    }
                                    if (needsACEdge == 1) {
                                        degreeDeficit.decrease(a);
                                        degreeDeficit.decrease(c);
                                        vertexA.addEdge("", vertexC);
                                    }
                                    loopIterations = 0;
                                } else {
                                    loopIterations++;
                                    continue;
                                }
                            }*/
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

        System.out.println("loop iteration: " + Integer.toString(loopIterations));
        System.out.println("nothingToAdd: " + Boolean.toString(nothingToAdd));
        return g;
    }
}
