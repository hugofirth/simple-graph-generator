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

import com.google.common.collect.Iterables;
import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.ElementHelper;
import org.edbt.summerschool.simple_graph_generator.generator.Strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


/**
 * Description of Class
 *
 * @author hugofirth
 */
public class WindowedStrategy implements Strategy {

    private Iterable<Integer> degreeSequence;
    private double clusteringCoefficient;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    protected final CompletionService<Graph> completionService = new ExecutorCompletionService<Graph>(threadPool);

    public WindowedStrategy(Iterable<Integer> degreeSequence, double clusteringCoefficient) {
        this.degreeSequence = degreeSequence;
        this.clusteringCoefficient = clusteringCoefficient;
    }



    @Override
    public Graph call() throws Exception {
        //Phase 1
        //Split up the degreeSequence into 16 chunks.
        //Naive count of Iterable, could be avoided if we were told size of degreeSeq in advance
        int numVertices = 0;
        for(Integer d : degreeSequence) {
            numVertices ++;
        }


        List<List<Integer>> chunks = new ArrayList<>();
        for(int i = 0; i<16; i++){
            chunks.add(new ArrayList<>());
        }

        int i = 0;
        for(Integer d : degreeSequence) {
            chunks.get(i/16).add(d);
            i++;
        }

        //Work out numTriangles for each Chunk
        int chunkNumTriangles = Strategy.numTriangles(degreeSequence, clusteringCoefficient)/16;

        int remainingFutures = 0;
        for(List<Integer> chunk : chunks){
            IndexableGraph subGraph = new TinkerGraph();
            completionService.submit(new GreedyGenerator(chunk, chunkNumTriangles, subGraph));
            remainingFutures++;
        }

        List<Graph> generatedSubGraphs = new ArrayList<>();

        while(remainingFutures>0) {
            Future<Graph> resultFuture = completionService.take();
            generatedSubGraphs.add(resultFuture.get());
        }

        //Phase 2
        List<Vertex> unfinishedVertices = new ArrayList<>();
        for(Graph subGraph: generatedSubGraphs){
            //Badness :( TODO: refactor horrible mess
            Index<Vertex> idx = ((IndexableGraph) subGraph).getIndex("unfinished", Vertex.class);
            Iterables.addAll(unfinishedVertices, idx.get("unfinished", true));
        }

        TinkerGraph parentGraph = new TinkerGraph();
        Index<Vertex> unfinished = parentGraph.createIndex("unfinished", Vertex.class);
        int subGraphId = 1;
        for(Graph subGraph: generatedSubGraphs){
            for(Vertex v: subGraph.getVertices()){
                Vertex parentVertex = parentGraph.addVertex(subGraphId+"-"+v.getId());
                if((boolean) v.getProperty("unfinished")){
                    unfinished.put("unfinished", true, parentVertex);
                    ElementHelper.copyProperties(v, parentVertex);
                }
            }

            for(Edge e: subGraph.getEdges()){
                Vertex out = parentGraph.getVertex(subGraphId+"-"+e.getVertex(Direction.OUT).getId());
                Vertex in = parentGraph.getVertex(subGraphId + "-" + e.getVertex(Direction.IN).getId());
                parentGraph.addEdge(subGraphId+"-"+e.getId(), out, in, "");
            }
        }

        Iterable<Vertex> unfinishedVertex = unfinished.get("unfinished", true);
        Graph mergeGraph = new TinkerGraph();
        for(Vertex v: unfinishedVertex){
            mergeGraph.addVertex(v.getId());
        }
        //Run merging Generator
        mergeGraph = new MergingGenerator(mergeGraph).call();

        //Add mergeGraph edges to parent Graph
        for(Edge e: mergeGraph.getEdges()){
            Vertex out = parentGraph.getVertex(e.getVertex(Direction.OUT).getId());
            Vertex in = parentGraph.getVertex(e.getVertex(Direction.IN).getId());
            parentGraph.addEdge("0-"+e.getId(), out, in, "");
        }

        //Return ParentGraph
        return parentGraph;
    }
}
