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

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import org.edbt.summerschool.simple_graph_generator.generator.OptimisationVector;
import org.edbt.summerschool.simple_graph_generator.generator.SelectionStrategy;


import java.util.*;

/**
 * Description of Class
 *
 * @author hugofirth
 */
public class DegreeFirstSelectionStrategy implements SelectionStrategy {
    @Override
    public Iterable<Set<Vertex>> getCandidateIterable(Graph g) {
        List<Vertex> vertices = new ArrayList<>();

        int degreeSum = 0;

        for(Vertex v : g.getVertices()) {
            vertices.add(v);
            degreeSum += (int) v.getProperty("degreeDeficit");
        }

        Collections.sort(vertices, (Vertex l, Vertex r) -> {
            Integer lDefecit = l.getProperty("degreeDeficit");
            Integer rDeficit = r.getProperty("degreeDeficit");
            return lDefecit.compareTo(rDeficit);
        });

        //Potential loss of precision is not a problem as degree sum *MUST* be even
        int numEdges = degreeSum/2;

        int edgesCreated = 0;

        List<Set<Vertex>> edgeCandidates = new ArrayList<>();

        //Iterate the sorted vertices
        for(int i = 0; i<vertices.size(); i++){
            int degreeDeficit = vertices.get(i).getProperty("degreeDeficit");
            for(int j = 1; j<=degreeDeficit; j++){
                Set<Vertex> potentialEdge = new HashSet<>();
                potentialEdge.add(vertices.get(i));
                potentialEdge.add(vertices.get(i+j));
                edgeCandidates.add(potentialEdge);
            }
        }

        //TODO: Think about triangle counting and skipping elements in the degreeSequence. This should be a way to keep triangles down.

        return edgeCandidates;
    }

    @Override
    public boolean handleNoOptimalFound(Graph g, OptimisationVector optVector) {
        return false;
    }
}

//Rather than checking triangles - can we count them by construction. So 1->2, 1->3, 2->3. At the lowest level yes we can.
//Keep track of triangle memberships in edges.