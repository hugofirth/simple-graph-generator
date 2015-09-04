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


import com.tinkerpop.blueprints.*;

import org.edbt.summerschool.simple_graph_generator.generator.Generator;

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
    private final DegreeFirstSelectionStrategy selectionStrategy = new DegreeFirstSelectionStrategy();
    private IndexableGraph graph;

    public GreedyGenerator(Iterable<Integer> degreeSubSequence, int numTriangles, IndexableGraph graph) {
        this.degreeSubSequence = degreeSubSequence;
        this.numTriangles = numTriangles;
        this.graph = graph;
    }

    @Override
    public Graph call() throws Exception {

        Index<Vertex> unfinished = graph.createIndex("unfinished", Vertex.class);
        selectionStrategy.setUnfinishedIndex(unfinished);

        int position = 0;

        for (int degree : degreeSubSequence) {

            Vertex v = graph.addVertex(null);

            v.setProperty("degreeDeficit", degree);
            v.setProperty("position", position++);

        }

        for(Set<Vertex> candidateEdge: selectionStrategy.getCandidateIterable(graph, numTriangles)){
            //TODO: Use OptimisationVector to track improvement, but not to make any decisions (I think)
            createEdge(candidateEdge);
        }
        return graph;
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
        addEdge(vertices.next(), vertices.next());
    }

    private Edge addEdge(Vertex v, Vertex w) {
        // create new edge
        return v.addEdge("",w);
    }
}
