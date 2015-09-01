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

import org.apache.commons.cli.*;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Main class constitutes the entry point to the program
 *
 * @author hugofirth
 */
public class Main {


    private static Options options = new Options();

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
            else if(!(commandLine.hasOption("size") || commandLine.hasOption('s')))
            {
                throw new ParseException("You must specify the size of the graph to be generated!");
            }
            else if(!(commandLine.hasOption("output") || commandLine.hasOption('o')))
            {
                throw new ParseException("You must specify an output file destination for the generated graph!");
            }
            else if(!(commandLine.hasOption("clustering") || commandLine.hasOption('c')))
            {
                throw new ParseException("You must specify a desired clustering coefficient for the generated graph!");
            }
            else
            {
                //TODO: Call down to the Reader class which should return a future so that we can display output while
                // generator is running. The reader itself should read in the degree file as fast as possible and chunk
                // it up into partitions to start the generation.
//                displayBlankLines(1, System.out);
//
//                Future<Pair<Long, Long>> result = e.export(gdb);
//                long startTime = System.currentTimeMillis();
//                while(!result.isDone())
//                {
//                    System.out.print(".");
//                    Thread.sleep(1000);
//                }
//                long elapsedTime = System.currentTimeMillis()-startTime;
//
//
//                Pair<Long, Long> stats = result.get();
//                displayBlankLines(2, System.out);
//                System.out.println("[ Success! Took: "+elapsedTime+" ms in total]");
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

        optionsMap.put("help", new Option("h", "help", false, "Displays help information for the Simple Graph Generator."));
        optionsMap.put("size", new Option("s", "size", true, "The size (number of vertices) of the graph to be generated."));
        optionsMap.put("output", new Option("o", "output", true, "The desired destination for the generated graph file."));
        optionsMap.put("clustering", new Option("c", "clustering", true, "The required clustering coefficient for the generated graph."));

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
