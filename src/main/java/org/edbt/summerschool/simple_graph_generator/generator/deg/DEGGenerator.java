package org.edbt.summerschool.simple_graph_generator.generator.deg;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.edbt.summerschool.simple_graph_generator.generator.Generator;
import org.edbt.summerschool.simple_graph_generator.graph.GraphMethods;

import java.util.ArrayList;
import java.util.Random;


/**
 * This class implements the generator as described in the Masters' thesis of Nidhi Parikh
 * entitled "Generating Random Graphs with Tunable Clustering Coefficient".
 */
public class DEGGenerator implements Generator {

    private Iterable<Integer> degreeSubSequence;
    private int numTriangles;
    final int MAX_LOOP_ITERATIONS = 20;

    private class DegreeDeficitVector {
        private ArrayList<Integer> degreeDeficit = new ArrayList<>();
        private Integer totalDegreeDeficit = 0;

        public void setDegree(Integer i, Integer d) {
            Integer old = degreeDeficit.get(i);
            if (old == null) {
                totalDegreeDeficit += d;
            } else {
                totalDegreeDeficit += d - old;
            }

            degreeDeficit.set(i, d);
        }

        public void decrease(Integer position) {
            degreeDeficit.set(position, degreeDeficit.get(position)-1);
        }

        public Integer totalDegreeDeficit() {
            return totalDegreeDeficit;
        }

        public Integer randomNodePosition() {
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(totalDegreeDeficit()); // TODO: check if range is as desired?

            int prefixSum = 0;
            int position = 0;
            Integer returnPosition = null;
            for (Integer degree : degreeDeficit) {
                if (prefixSum < randomInt && randomInt <= prefixSum + degree); // TODO: check if this is a desired
                returnPosition = new Integer(position);
                prefixSum += degree;
                position ++;
            }

            return returnPosition;
        }
    }
    public DEGGenerator(Iterable<Integer> degreeSubSequence, int numTriangles) {
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
            Vertex v = g.addVertex(position); // POTENTIAL ISSUE: We assume that the graph library (always) uses 'position' as vertex id
            degreeDeficit.setDegree(position, degree);
            g.addVertex(position);
        }

        int loopIterations = 0;
        while (numTriangles > 0 && degreeDeficit.totalDegreeDeficit() > 1 && loopIterations < MAX_LOOP_ITERATIONS) {
            loopIterations++;
            Integer a = degreeDeficit.randomNodePosition();
            Integer b = degreeDeficit.randomNodePosition();
            Integer c = degreeDeficit.randomNodePosition();

            if (a.equals(b) || b.equals(c) || a.equals(c)) {
                continue;
            }

            Vertex vertexA = g.getVertex(a);
            Vertex vertexB = g.getVertex(b);
            Vertex vertexC = g.getVertex(c);

            // supposing a, b and c where found
            if (!GraphMethods.edgeExists(vertexA,vertexB)) {
                vertexA.addEdge("", vertexB);
                degreeDeficit.decrease(a);
                degreeDeficit.decrease(b);
            }
            if (!GraphMethods.edgeExists(vertexB,vertexC)) {
                vertexB.addEdge("", vertexC);
                degreeDeficit.decrease(b);
                degreeDeficit.decrease(c);
            }
            if (!GraphMethods.edgeExists(vertexA,vertexC)) {
                vertexA.addEdge("", vertexC);
                degreeDeficit.decrease(a);
                degreeDeficit.decrease(c);
            }
            numTriangles -= 1;
        }

        loopIterations = 0;
        while (degreeDeficit.totalDegreeDeficit() > 1 && loopIterations < MAX_LOOP_ITERATIONS) {
            loopIterations++;
            Integer a = degreeDeficit.randomNodePosition();
            Integer b = degreeDeficit.randomNodePosition();

            if (a == null || b == null) {
                continue;
            }

            Vertex vertexA = g.getVertex(a);
            Vertex vertexB = g.getVertex(b);

            if (!GraphMethods.edgeExists(vertexA, vertexB)) {
                vertexA.addEdge("", vertexB);
                degreeDeficit.decrease(a);
                degreeDeficit.decrease(b);
            }
        }

        return g;
    }
}
