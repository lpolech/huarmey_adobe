package data;

import common.Globals;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Loader {
    static public Instances load(String filePath, boolean preprocessData) {
        Instances data = null;
        DataSource source = null;
        try {
            source = new DataSource(filePath);
            data = source.getDataSet();
        } catch (Exception e) {
            System.err.println("Problem while loading " + filePath + " file.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }
        data.setClassIndex(0);// first attribute is a class

        if (preprocessData)
            data = preprocess(data);
        
        return data;
    }

    static public Instances preprocess(Instances data) {
        Attribute classAtr = data.attribute(Globals.CLASS_ATTR_NAME);
        data.sort(classAtr);
        //TODO: posortowac dane w alfabetycznym porzadku po nazwie klasy
        return data;
    }
}
