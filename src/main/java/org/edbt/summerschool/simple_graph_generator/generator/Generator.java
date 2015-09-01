package org.edbt.summerschool.simple_graph_generator.generator;

import com.tinkerpop.blueprints.Graph;

import java.util.concurrent.Callable;

/**
 * Created by jonny on 01/09/15.
 */
public interface Generator extends Callable<Graph> {}
