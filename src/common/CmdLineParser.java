package common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import algorithm.comparator.EBrickComparators;
import algorithm.comparator.EWallComparators;
import data.Parameters;


public class CmdLineParser {
    private CommandLineParser parser;
    private CommandLine cmd;
    private Options options;
    private HelpFormatter helpText;

    public CmdLineParser() {
        parser = new BasicParser();
        options = new Options();
        helpText = new HelpFormatter();
        createOptions();
    }
    
    @SuppressWarnings("static-access")
    private void createOptions()
    {
        Option input = OptionBuilder.withArgName("file")
                .hasArgs(1)
                .isRequired(false)
                .withDescription("path to file with input ARFF data")
                .withLongOpt("input")
                .create('i');
        
        Option output = OptionBuilder.withArgName("path")
                .hasArgs(1)
                .isRequired(false)
                .withDescription("path where to store output - *.csv files showing results (default is path to *.jar file)")
                .withLongOpt("output")
                .create('o');
        
        Option accuracy = OptionBuilder.withArgName("accuracy")
                .hasArgs(1)
                .isRequired(false)
                .withDescription("measurement accuracy in measurement units (default is 0.5)")
                .withLongOpt("accuracy")
                .create('a');
        
        Option minShrink = OptionBuilder.withArgName("min-shrink")
                .hasArgs(1)
                .withArgName("min-shrink")
                .isRequired(false)
                .withDescription("percentage indicating minimum brick shrink (default is 0.0)")
                .withLongOpt("min-shrink")
                .create("s1");
        
        Option maxShrink = OptionBuilder.withArgName("max-shrink")
                .hasArgs(1)
                .withArgName("max-shrink")
                .isRequired(false)
                .withDescription("percentage indicating maximum brick shrink (default is 0.0)")
                .withLongOpt("max-shrink")
                .create("s2");
                
        Option help = OptionBuilder.withDescription("prints this message")
                .hasArg(false)
                .isRequired(false)
                .withLongOpt("help")
                .create('h');
        
        options.addOption("v", "verbose", false, "verbose program execution");
        options.addOption("cwtsac", "use-cwtsac", false, "use Counting With Threshold And Simple Avg Comparision algorithm to compare bricks counting dimensions which fits "
                + "within specified threshold (a + X*%(maxShrink - minShrink))");
        options.addOption("cwteu", "use-cwteu", false, "use Counting With Threshold And Extended Uncertainty Factor algorithm to compare bricks counting dimensions which fits "
                + "within specified threshold computer using expanded uncertainty");
        
        options.addOption("bpmwr", "use-bpmwr", false, "use Brick Pair Matching Without Return algorithm to compare bricks creating a best maching pairs of bricks. Bricks" +
                " used in a particular pair is no longer considered to nex pair while comparing two certain walls");
        options.addOption("asc", "use-ascc", false, "use Avg Similarities Counting algorithm to compare walls by averaging sum of brick counts "
                + "- this is the only implemented algorithm for bricks");
                
        options.addOption(input);
        options.addOption(output);
        options.addOption(accuracy);
        options.addOption(maxShrink);
        options.addOption(minShrink);
        options.addOption(help);
    }
    
    public void parse(String[] args)
    {
        try {
            cmd = parser.parse(options, args);
        }
        catch( ParseException exp ) {
            System.err.println(exp.getMessage());
            System.exit(1);
        }
        if(cmd.hasOption('h') || cmd.hasOption("help") || args.length == 0)
        {
            viewHelp();
            System.exit(0);
        }
        else
        {
            parseParameters();
        }
    }

    private void parseParameters() {
        Parameters.setInputDataFilePath(parseInputFile());
        Parameters.setOutputFolder(parseOutputFile());
        
        double measurementsAccuracy = 0.5d;
        if(cmd.hasOption('a'))
        {
            measurementsAccuracy = parsePositiveDoubleParameter(cmd.getOptionValue('a'),
                    "Measurements accuracy should be an positive"
                    + " double value!");
        }       
        Parameters.setMeasurementsAccuracy(measurementsAccuracy);
        
        double percentageBrickMinShrink = 0.0d;
        if(cmd.hasOption("s1"))
        {
            percentageBrickMinShrink = parsePositiveDoubleParameter(cmd.getOptionValue("s1"),
                    "Percentage MINIMUM brick shrink should be an positive"
                    + " double value!");
        }       
        Parameters.setPercentageMinBrickShrink(percentageBrickMinShrink);
        
        double percentageBrickMaxShrink = 0.0d;
        if(cmd.hasOption("s2"))
        {
            percentageBrickMaxShrink = parsePositiveDoubleParameter(cmd.getOptionValue("s2"),
                    "Percentage MAXIMUM brick shrink should be an positive"
                    + " double value!");
        }       
        Parameters.setPercentageMaxBrickShrink(percentageBrickMaxShrink);
        
        Parameters.setVerbose(cmd.hasOption('v'));
        
        Parameters.setBrickComparator(parseBrickComparator());
        
        Parameters.setWallComparator(parseWallComparator());
    }

    private EBrickComparators parseBrickComparator() {
        if(cmd.hasOption("cwteu"))
        {
            return EBrickComparators.COUNTING_WITH_THRESHOLD_EXPANDED_UNCERTAINTY;
        }
        else if(cmd.hasOption("cwtsac"))
        {
            return EBrickComparators.COUNTING_WITH_THRESHOLD_SIMPLE_AVG_COMPARISION;
        }
        System.err.println("No brick comparision method was set!");
        System.exit(1);
        return null;
    }

    private EWallComparators parseWallComparator() {
        if(cmd.hasOption("asc"))
        {
            return EWallComparators.AVG_SIMILARITIES_COUNTING;
        }
        else if(cmd.hasOption("bpmwr"))
        {
            return EWallComparators.BEST_PAIR_MATCHING;
        }
        System.err.println("No wall comparision method was set!");
        System.exit(1);
        return null;
    }
    
    private int parsePositiveIntegerParameter(String parsedOptionValue, String invalidArgMsg) {
        int parsedValue = -1;
        try
        {
            parsedValue = Integer.valueOf(parsedOptionValue);
            if(parsedValue < 0)
            {
                throw new NumberFormatException();
            }
        }
        catch(NumberFormatException e)
        {
            System.err.println("'" + parsedOptionValue + "' " + invalidArgMsg
                        + " " + e.getMessage());
            System.exit(-1);
        }
        return parsedValue;
    }
    
    private int parseIntegerParameter(String parsedOptionValue, String invalidArgMsg) {
        int parsedValue = -1;
        try
        {
            parsedValue = Integer.valueOf(parsedOptionValue);
        }
        catch(NumberFormatException e)
        {
            System.err.println("'" + parsedOptionValue + "' " + invalidArgMsg
                        + " " + e.getMessage());
            System.exit(-1);
        }
        return parsedValue;
    }
    
    private double parsePositiveDoubleParameter(String parsedOptionValue, String invalidArgMsg) {
        double parsedValue = -1;
        try
        {
            parsedValue = Double.valueOf(parsedOptionValue);
            if(parsedValue < 0.0d)
            {
                throw new NumberFormatException();
            }
        }
        catch(NumberFormatException e)
        {
            System.err.println("'" + parsedOptionValue + "' " + invalidArgMsg
                        + " " + e.getMessage());
            System.exit(-1);
        }
        return parsedValue;
    }
    
    private double parseDoubleParameter(String parsedOptionValue, String invalidArgMsg) {
        double parsedValue = -1;
        try
        {
            parsedValue = Double.valueOf(parsedOptionValue);
        }
        catch(NumberFormatException e)
        {
            System.err.println("'" + parsedOptionValue + "' " + invalidArgMsg
                        + " " + e.getMessage());
            System.exit(-1);
        }
        return parsedValue;
    }

    private void viewHelp()
    {        
        helpText.printHelp( "AdobeBrick_V0.1", options );
    }   
    
    private Path parseInputFile()
    {
        File inputFile = null;
        if(cmd.hasOption('i'))
        {
            inputFile = new File(cmd.getOptionValue('i'));
            if(!inputFile.exists() || inputFile.isDirectory())
            {
                System.err.println("Input file shoud be existing file!");
                System.exit(1);
            }
        }
        else
        {
            System.err.println("No input file specified! Use -i option.");
            System.exit(1);
        }
        return inputFile.toPath();
    }
    
    private Path parseOutputFile() 
    {
        File outputFolder = null;
        if(cmd.hasOption('o'))
        {
            String outputFolderName = cmd.getOptionValue('o');
            outputFolder = new File(outputFolderName);
            if(outputFolder.isFile())
            {
                System.err.println(outputFolderName + " should be an path to directory!");
                System.exit(1);
            }
            if(!outputFolder.exists())
            {
                System.out.println(outputFolderName + " doesn't exist, creating folder.");
                try {
                    Files.createDirectories(outputFolder.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            System.err.println("No output file specified! Use -o option.");
            System.exit(1);
        }
        return outputFolder.toPath();
    }
}

