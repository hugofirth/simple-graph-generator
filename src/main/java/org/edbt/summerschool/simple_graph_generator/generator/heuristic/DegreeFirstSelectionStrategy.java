/**
 * simple-graph-generator
 * <p/>
 * Copyright (c) 2015 Jonny Daenen, Hugo Firth & Bas Ketsman
 * Email: <me@hugofirth.com/>
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.edbt.summerschool.simple_graph_generator.generator.heuristic;

import com.google.common.collect.*;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.edbt.summerschool.simple_graph_generator.generator.OptimisationVector;
import org.edbt.summerschool.simple_graph_generator.generator.SelectionStrategy;


import java.util.*;

/**
 * Description of Class
 *
 * @author hugofirth
 */
public final class DegreeFirstSelectionStrategy implements SelectionStrategy {

    public Index<Vertex> getUnfinishedIndex() {
        return unfinished;
    }

    public void setUnfinishedIndex(Index<Vertex> unfinished) {
        this.unfinished = unfinished;
    }

    private Index<Vertex> unfinished;

    @Override
    public Iterable<Set<Vertex>> getCandidateIterable(Graph g, int numTriangles) {
        List<Vertex> vertices = new ArrayList<>();

        for(Vertex v : g.getVertices()) {
            vertices.add(v);
        }

        Collections.sort(vertices, (Vertex l, Vertex r) -> {
            Integer lDeficit = l.getProperty("degreeDeficit");
            Integer rDeficit = r.getProperty("degreeDeficit");
            return rDeficit.compareTo(lDeficit);
        });

        int trianglesCreated = 0;

        List<Set<Vertex>> edgeCandidates = new ArrayList<>();
        SetMultimap<Integer, Integer> edgesSoFar = HashMultimap.create();

        //Iterate the sorted vertices
        for(int i = 0; i<vertices.size(); i++){
            int degreeDeficit = vertices.get(i).getProperty("degreeDeficit");
            int stepCount = degreeDeficit;

            for(int k = 1; k<=stepCount; k++){
                if(i+k < vertices.size()) {
                    int potentialTriangles = Sets.intersection(edgesSoFar.get(i), edgesSoFar.get(i + k)).size();
                    int kDeficit = vertices.get(i + k).getProperty("degreeDeficit");

                    if (kDeficit > 0 && (trianglesCreated + potentialTriangles <= numTriangles)) {
                        ImmutableSet<Vertex> potentialEdge = ImmutableSet.of(vertices.get(i), vertices.get(i + k));
                        edgeCandidates.add(potentialEdge);
                        edgesSoFar.put(i + k, i);
                        vertices.get(i + k).setProperty("degreeDeficit", kDeficit - 1);
                        vertices.get(i).setProperty("degreeDeficit", degreeDeficit - 1);
                    } else stepCount++;
                } else if(unfinished != null) {
                    //This is bad :'( . Time constraints - sorry
                    unfinished.put("unfinished", true, vertices.get(i));
                    vertices.get(i).setProperty("unfinished", true);
                }
            }
            edgesSoFar.removeAll(i);
        }
        return edgeCandidates;
    }

    @Override
    public boolean handleNoOptimalFound(Graph g, OptimisationVector optVector) {
        //Not necessary to use.
        return false;
    }
}

//Rather than checking triangles - can we count them by construction. So 1->2, 1->3, 2->3. At the lowest level yes we can.
//Keep track of triangle memberships in edges.