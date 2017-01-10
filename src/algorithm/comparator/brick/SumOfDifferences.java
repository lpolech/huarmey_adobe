package algorithm.comparator.brick;

import weka.core.Instance;
import algorithm.comparator.BrickComparator;

import common.Globals;

public class SumOfDifferences implements BrickComparator {
//Problem z ta miara polega na tym, ze PROMUJE cegly, ktore maja mniej wymiarow niz wiecej..
    @Override
    public double compare(Instance i1, Instance i2) {
        double sumOfDifferences = 0;
        double i1Value = (i1.isMissing(Globals.INDEX_OF_LENGTH_ATTRIBUTE)? 0.0 : i1.value(Globals.INDEX_OF_LENGTH_ATTRIBUTE));
        double i2Value = (i2.isMissing(Globals.INDEX_OF_LENGTH_ATTRIBUTE)? 0.0 : i2.value(Globals.INDEX_OF_LENGTH_ATTRIBUTE));
        sumOfDifferences += Math.abs(i1Value - i2Value);
                
        i1Value = (i1.isMissing(Globals.INDEX_OF_WIDTH_ATTRIBUTE)? 0.0 : i1.value(Globals.INDEX_OF_WIDTH_ATTRIBUTE));
        i2Value = (i2.isMissing(Globals.INDEX_OF_WIDTH_ATTRIBUTE)? 0.0 : i2.value(Globals.INDEX_OF_WIDTH_ATTRIBUTE));
        sumOfDifferences += Math.abs(i1Value - i2Value);
        
        i1Value = (i1.isMissing(Globals.INDEX_OF_HEIGHT_ATTRIBUTE)? 0.0 : i1.value(Globals.INDEX_OF_HEIGHT_ATTRIBUTE));
        i2Value = (i2.isMissing(Globals.INDEX_OF_HEIGHT_ATTRIBUTE)? 0.0 : i2.value(Globals.INDEX_OF_HEIGHT_ATTRIBUTE));
        sumOfDifferences += Math.abs(i1Value - i2Value);
//      sumOfDifferences += Math.abs(b1.getLength() - b2.getLength());
//      sumOfDifferences += Math.abs(b1.getWidth() - b2.getWidth());
//      sumOfDifferences += Math.abs(b1.getHeight() - b2.getHeight());
        return sumOfDifferences;
    }

}
