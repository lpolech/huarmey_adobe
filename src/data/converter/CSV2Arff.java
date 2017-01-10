package data.converter;

import java.io.File;
import java.nio.file.Files;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToString;

import common.Globals;

public class CSV2Arff {
    /**
     * takes 2 arguments: - CSV input file - ARFF output file
     */
    public static void main(String[] args) throws Exception {
        args = new String[]
                {
                    "adobe-data-30_10.csv",
                    "adobe-data-30_10.arff"
                };
        if (args.length != 2) {
            System.out.println("\nUsage: CSV2Arff <input.csv> <output.arff>\n");
            System.exit(1);
        }

        // load CSV
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(args[0]));
        Instances data = loader.getDataSet();

        //change comment type to string from nominal
        NominalToString ntsFilter = new NominalToString();
        String parameter = String.valueOf(Globals.INDEX_OF_COMMENT_ATTRIBUTE+1);
        ntsFilter.setAttributeIndexes(parameter);
        ntsFilter.setInputFormat(data);
        data = Filter.useFilter(data, ntsFilter);
        data.setClass(data.attribute(Globals.INDEX_OF_CLASS_ATTRIBUTE));
        
        // save ARFF
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        File outputFile = new File(args[1]);
        Files.deleteIfExists(outputFile.toPath());
        saver.setFile(outputFile);
        saver.writeBatch();
    }
}