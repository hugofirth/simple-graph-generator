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
package org.edbt.summerschool.simple_graph_generator.generator.simple;

import com.tinkerpop.blueprints.Graph;
import org.edbt.summerschool.simple_graph_generator.generator.SelectionStrategy;
import org.edbt.summerschool.simple_graph_generator.generator.Strategy;

/**
 * Description of Class
 *
 * @author hugofirth
 */
public class SimpleStrategy implements Strategy {

    private final SelectionStrategy selectionStrategy;
    private Iterable<Integer> degreeSequence;
    private double clusteringCoefficient;

    public SimpleStrategy(Iterable<Integer> degreeSequence, double clusteringCoefficient) {
        this.degreeSequence = degreeSequence;
        this.clusteringCoefficient = clusteringCoefficient;
        this.selectionStrategy = new SimpleSelectionStrategy();
    }

    public SimpleStrategy(Iterable<Integer> degreeSequence, double clusteringCoefficient, SelectionStrategy selectionStrategy) {
        this.degreeSequence = degreeSequence;
        this.clusteringCoefficient = clusteringCoefficient;
        this.selectionStrategy = selectionStrategy;
    }

    @Override
    public Graph call() throws Exception {
        int numTriangles = Strategy.numTriangles(degreeSequence, clusteringCoefficient);
//        System.out.println("Number of triangles required: " + numTriangles);
        SimpleGenerator generator = new SimpleGenerator(degreeSequence, numTriangles,selectionStrategy);
        return generator.call();
    }
}
