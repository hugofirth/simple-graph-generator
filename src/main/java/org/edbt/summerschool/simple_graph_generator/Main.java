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
package org.edbt.summerschool.simple_graph_generator;

import com.google.common.primitives.Ints;
import com.sun.tools.javac.util.List;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;
import org.apache.commons.cli.*;
import org.edbt.summerschool.simple_graph_generator.generator.Strategies;
import org.edbt.summerschool.simple_graph_generator.generator.StrategyFactory;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Main class constitutes the entry point to the program
 *
 * @author hugofirth
 */
public class Main {


    private static Options options = new Options();
    private static ExecutorService workThread = Executors.newSingleThreadExecutor();

    /**
     * The main method - entry point to the program.
     *
     * @param args  The string arguments provided to the program upon execution.
     */
    public static void main(final String[] args)
    {
        displayBlankLines(1, System.out);
        displayHeader(System.out);
        displayBlankLines(1, System.out);
        if (args.length < 1)
        {
            System.out.println("-- USAGE --");

            printHelp(constructOptions(), 80, "", "", 2, 1, true, System.out);
        }
        else
        {
            useParser(args);
        }
        System.exit(0);
    }

    /**
     * Apply Apache Commons CLI GnuParser to command-line arguments.
     *
     * @param commandLineArguments Command-line arguments to be processed with Gnu-style parser.
     */
    public static void useParser(final String[] commandLineArguments)
    {
        final CommandLineParser cmdLineGnuParser = new GnuParser();

        final Options gnuOptions = constructOptions();
        CommandLine commandLine;
        try
        {
            commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);

            displayBlankLines(1, System.out);

            if (commandLine.hasOption("help") || commandLine.hasOption('h') )
            {
                printHelp(constructOptions(), 80, "--- HELP ---", "", 2, 1, true, System.out);
            }
            else if(!(commandLine.hasOption("input") || commandLine.hasOption('i')))
            {
                throw new ParseException("You must specify an input file containing a sequence of vertex degrees!");
            }
            else if(!(commandLine.hasOption("destination") || commandLine.hasOption('d')))
            {
                throw new ParseException("You must specify an output file destination for the generated graph!");
            }
            else if(!(commandLine.hasOption("clustering") || commandLine.hasOption('c')))
            {
                throw new ParseException("You must specify a desired clustering coefficient for the generated graph!");
            }
            else
            {
                //TODO: Read in the degreeSequence (naively initially) dump in an iterable and pass to Strategy


                int [] seq = {1, 4, 4, 4, 4, 2, 1, 4, 1, 2, 2, 3, 1, 3, 2, 2, 2};
//                int [] seq = {11,11,11,11,11,11,11,11,11,11,11,11};
                Iterable<Integer> seqList = Ints.asList(seq);
                displayBlankLines(1, System.out);

                Double ccoeff = Double.parseDouble(commandLine.getOptionValue("clustering"));

                Future<Graph> result = workThread.submit(StrategyFactory.createStrategy(Strategies.SIMPLE, seqList, ccoeff));
                long startTime = System.currentTimeMillis();
                while(!result.isDone())
                {
                    System.out.print(".");
                    Thread.sleep(1000);
                }
                long elapsedTime = System.currentTimeMillis()-startTime;


                Graph generated = result.get();
                System.out.println(generated.toString());
                //TODO: Write generated out to a file using an accepted format.
                displayBlankLines(2, System.out);
                OutputStream out = new FileOutputStream(new File(commandLine.getOptionValue("destination")));

                GraphMLWriter.outputGraph(generated, out);
                System.out.println("[ Success! Took: "+elapsedTime+" ms in total]");
            }
        }
        catch (ParseException e)
        {
            System.err.println("Encountered exception while parsing the provided arguments:\n"+e.getMessage());
            System.exit(1);
        }
        //TODO: replace with more specific Exceptions
        catch(Exception e)
        {
            e.printStackTrace();
            System.err.println("Encountered a problem generating the graph:\n"+e.toString());
            System.exit(1);
        }
    }

    /**
     * Construct and provide GNU-compatible Options.
     *
     * @return Options expected from command-line of GNU form.
     */
    public static Options constructOptions()
    {
        final Map<String, Option> optionsMap = new HashMap<>();

        optionsMap.put("help",
                new Option("h", "help", false, "Displays help information for the Simple Graph Generator."));
        optionsMap.put("input",
                new Option("i", "input", true, "The input file containing a vertex degree per newline."));
        optionsMap.put("destination",
                new Option("d", "destination", true, "The desired destination for the generated graph file."));
        optionsMap.put("clustering",
                new Option("c", "clustering", true, "The required clustering coefficient for the generated graph."));

        final Options options = new Options();
        for(Option o : optionsMap.values())
        {
            options.addOption(o);
        }
        return options;
    }

    /**
     * Display command-line arguments without processing them in any further way.
     *
     * @param commandLineArguments Command-line arguments to be displayed.
     */
    public static void displayProvidedCliArguments(final String[] commandLineArguments, final OutputStream out)
    {
        final StringBuilder builder = new StringBuilder();
        for ( final String argument : commandLineArguments )
        {
            builder.append(argument).append(" ");
        }
        try
        {
            out.write((builder.toString() + "\n").getBytes());
        }
        catch (IOException ioEx)
        {
            System.err.println("WARNING: Exception encountered trying to write to OutputStream:\n"+ ioEx.getMessage());
        }
    }

    /**
     * Display example application header.
     *
     * @out OutputStream to which header should be written.
     */
    public static void displayHeader(final OutputStream out)
    {
        final String header =
                "[ Thanks for using the Simple Graph Generator: a tool for generating graphs which conform to a given " +
                        "degree sequence and clustering coefficient. ]\n[ Created by Jonny Daenen, Hugo Firth & Bas " +
                        "Ketsman and licensed under the GNU Affero General Public License, version 3. ]";
        try
        {
            out.write(header.getBytes());
        }
        catch (IOException ioEx)
        {
            System.err.println("WARNING: Exception encountered trying to write to OutputStream:\n"+ ioEx.getMessage());
        }
    }

    /**
     * Write the provided number of blank lines to the provided OutputStream.
     *
     * @param numberBlankLines Number of blank lines to write.
     * @param out OutputStream to which to write the blank lines.
     */
    public static void displayBlankLines(final int numberBlankLines, final OutputStream out)
    {
        try
        {
            for (int i=0; i<numberBlankLines; ++i)
            {
                out.write("\n".getBytes());
            }
        }
        catch (IOException ioEx)
        {
            System.err.println("WARNING: Exception encountered trying to write to OutputStream:\n"+ ioEx.getMessage());
        }
    }


    /**
     * Write "help" to the provided OutputStream.
     */
    public static void printHelp(
            final Options options,
            final int printedRowWidth,
            final String header,
            final String footer,
            final int spacesBeforeOption,
            final int spacesBeforeOptionDescription,
            final boolean displayUsage,
            final OutputStream out)
    {
        final String commandLineSyntax = "java -jar SimpleGraphGenerator.jar";
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(
                writer,
                printedRowWidth,
                commandLineSyntax,
                header,
                options,
                spacesBeforeOption,
                spacesBeforeOptionDescription,
                footer,
                displayUsage);
        writer.flush();
    }
}
