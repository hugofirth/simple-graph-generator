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
package org.edbt.summerschool.simple_graph_generator.generator;

/**
 * Description of Class
 *
 * @author hugofirth
 */
public final class StrategyFactory {

    //Do not allow anyone to create instances of the Factory
    private StrategyFactory() {}

    public static Strategy createStrategy(Strategies type,
                                          Iterable<Integer> degreeSequence,
                                          double clusteringCoefficient) {

        if(type == Strategies.CONCURRENT) {
            //TODO: Implement the concurrent Strategy
            throw new UnsupportedOperationException("The concurrent generator strategy has not been implemented yet!");
        } else {
            return new SimpleStrategy(degreeSequence, clusteringCoefficient);
        }
    }
}