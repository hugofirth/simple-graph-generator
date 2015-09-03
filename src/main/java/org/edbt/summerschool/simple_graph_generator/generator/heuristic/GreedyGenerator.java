/**
 * simple-graph-generator
 * <p>
 * Copyright (c) 2015 Jonny Daenen, Hugo Firth & Bas Ketsman
 * Email: <me@hugofirth.com/>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.edbt.summerschool.simple_graph_generator.generator.heuristic;

import com.google.common.collect.ImmutableSet;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.edbt.summerschool.simple_graph_generator.generator.Generator;
import org.edbt.summerschool.simple_graph_generator.generator.SelectionStrategy;

import java.util.Iterator;
import java.util.Set;

/**
 * Description of Class
 *
 * @author hugofirth
 */
public class GreedyGenerator implements Generator {

    private Iterable<Integer> degreeSubSequence;
    private int numTriangles;
    private final SelectionStrategy selectionStrategy = new DegreeFirstSelectionStrategy();

    public GreedyGenerator(Iterable<Integer> degreeSubSequence, int numTriangles) {
        this.degreeSubSequence = degreeSubSequence;
        this.numTriangles = numTriangles;
    }

    @Override
    public Graph call() throws Exception {

        // create empty graph
        Graph g = new TinkerGraph();

        int totalDegreeDeficit = 0;
        int unfinishedNodes = 0;
        boolean minimalDegreeDeficit = false;
        int position = 0;

        for (int degree : degreeSubSequence) {

            Vertex v = g.addVertex(null);

            v.setProperty("degreeDeficit", degree);
            v.setProperty("position", position++);

            totalDegreeDeficit += degree;
            if (degree > 0)
                unfinishedNodes++;
        }


        for(Set<Vertex> candidateEdge: selectionStrategy.getCandidateIterable(g, 0)){
            //TODO: Use OptimisationVector to track improvement, but not to make any decisions (I think)
            //TODO: Modify selection strategy to take a required number of Triangles so that it can actually target a given CC
            createEdge(candidateEdge);
        }
        return null;
    }

    /**
     * Creates the set of all edges between the given vertices.
     *
     *  In the case of GreedyGenerator#createEdges, each candidateSet is a single pair of vertices.
     *  This method therefore creates the single potential edge the candidateSet represents.
     *
     * @param candidateEdge The single edge to be created
     */
    private void createEdge(Set<Vertex> candidateEdge) {
        Iterator<Vertex> vertices = candidateEdge.iterator();
        //Ok to iterate unchecked as is using DegreeFirstSelectionStrategy
        // which returns ImmutableSet with guaranteed order
        addEdgeAndUpdateDeficit(vertices.next(), vertices.next());
    }

    private Edge addEdgeAndUpdateDeficit(Vertex v, Vertex w) {

        // create new edge
        Edge e = v.addEdge("",w);
        // update the degree deficit
        v.setProperty("degreeDeficit", (int) v.getProperty("degreeDeficit") - 1);
        w.setProperty("degreeDeficit", (int) w.getProperty("degreeDeficit") - 1);
        return e;
    }
}
