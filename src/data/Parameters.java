package data;

import java.io.File;
import java.nio.file.Path;

import common.Globals;
import algorithm.comparator.EBrickComparators;
import algorithm.comparator.EWallComparators;

public class Parameters {
    private static Path inputDataFilePath;
    private static Path outputFolder;
    private static double measurementsAccuracy;
    private static double percentageMinBrickShrink = -1.0;
    private static double percentageMaxBrickShrink = Double.MAX_VALUE;
    private static boolean verbose;
    private static EBrickComparators brickComparator;
    private static EWallComparators wallComparator;
    
    private Parameters()
    {
    }

    public static String getInputDataFilePath() {
        return inputDataFilePath.toString();
    }

    public static void setInputDataFilePath(Path inputDataFilePath) {
        Parameters.inputDataFilePath = inputDataFilePath;
    }

    public static String getOutputFolder() {
        return outputFolder.toString();
    }

    public static void setOutputFolder(Path outputFolder) {
        Parameters.outputFolder = outputFolder;
    }

    public static double getMeasurementsAccuracy() {
        return measurementsAccuracy;
    }

    public static void setMeasurementsAccuracy(double measurementsAccuracy) {
        Parameters.measurementsAccuracy = measurementsAccuracy;
    }

    public static double getPercentageMinBrickShrink() {
        return percentageMinBrickShrink;
    }

    public static void setPercentageMinBrickShrink(double percentageMinBrickShrink) {
        if(percentageMaxBrickShrink < percentageMinBrickShrink)
        {
            System.err.println("Parameters.setPercentageMinBrickShrink(...) Setting MINIMUM value is GREATER than "
                    + "already saved MAXIMUM value. percentageMaxBrickShrink=" + percentageMaxBrickShrink
                    + " percentageMinBrickShrink:" + percentageMinBrickShrink);
            System.exit(1);
        }
        Parameters.percentageMinBrickShrink = percentageMinBrickShrink;
    }
    
    public static double getPercentageMaxBrickShrink() {
        return percentageMaxBrickShrink;
    }
    
    public static void setPercentageMaxBrickShrink(
            double percentageMaxBrickShrink) {
        if(percentageMaxBrickShrink < percentageMinBrickShrink)
        {
            System.err.println("Parameters.getPercentageMaxBrickShrink(...) Setting MAXIMUM value is LESS than already saved MINIMUM value."
                    + " percentageMaxBrickShrink=" + percentageMaxBrickShrink + " percentageMinBrickShrink:" + percentageMinBrickShrink);
            System.exit(1);
        }
        Parameters.percentageMaxBrickShrink = percentageMaxBrickShrink;
    }

    public static boolean isVerbose() {
        return verbose;
    }

    public static void setVerbose(boolean verbose) {
        Parameters.verbose = verbose;
    }

    public static EBrickComparators getBrickComparator() {
        return brickComparator;
    }

    public static void setBrickComparator(EBrickComparators brickComparator) {
        Parameters.brickComparator = brickComparator;
    }

    public static EWallComparators getWallComparator() {
        return wallComparator;
    }

    public static void setWallComparator(EWallComparators wallComparator) {
        Parameters.wallComparator = wallComparator;
    }

    public static String printParameters()
    {
        String parameterResults = "Parameters:\n";
        parameterResults += "\tInput file: " + Parameters.getInputDataFilePath() + "\n";
        parameterResults += "\tOutput folder: " + Parameters.getOutputFolder() + "\n";
        parameterResults += "\tMeasurements accuracy: " + Parameters.getMeasurementsAccuracy() + "\n";
        parameterResults += "\tPercentage MIN shrink: " + Parameters.getPercentageMinBrickShrink() + "\n";
        parameterResults += "\tPercentage MAX shrink: " + Parameters.getPercentageMaxBrickShrink() + "\n";
        parameterResults += "\tBrick comparator method: " + Parameters.getBrickComparator() + "\n";
        parameterResults += "\tWall comparator method: " + Parameters.getWallComparator() + "\n";
        return parameterResults;
    }

    public static void saveParametersToOutputFolder() {
        Globals.createNewFileAndFillContent(getOutputFolder() + File.separator + "parameters.txt", printParameters());      
    }
}
