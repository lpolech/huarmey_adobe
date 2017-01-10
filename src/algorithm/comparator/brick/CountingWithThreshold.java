package algorithm.comparator.brick;

import common.Globals;
import data.Parameters;
import weka.core.Instance;
import algorithm.comparator.BrickComparator;
import algorithm.comparator.EBrickComparators;

public class CountingWithThreshold implements BrickComparator{
    private static double EXPANDED_UNCERTAINTY_FACTOR = 2.0;
    private static double COMPARISION_EPSYLON = 0.00000001;
    private double measurementsAccuracy;
    private double percentagePossibleBrickShrink;
    
    public CountingWithThreshold(double measurementsAccuracy, double percentageBrickMinShrink,
                                 double percentageBrickMaxShrink) {
        this.measurementsAccuracy = measurementsAccuracy;
        this.percentagePossibleBrickShrink = percentageBrickMaxShrink-percentageBrickMinShrink;//zakladam, ze kazdy
        // pomiar jest juz po tym minimalnym skurczu i potrzebuje tylko miec mozliwe rozszerzenie tego zkurczu
    }
    
    @Override
    public double compare(Instance i1, Instance i2) {
        double score = 0.0;
        
        if(!i1.isMissing(Globals.INDEX_OF_LENGTH_ATTRIBUTE) && !i2.isMissing(Globals.INDEX_OF_LENGTH_ATTRIBUTE))
        {
            double i1Length = i1.value(Globals.INDEX_OF_LENGTH_ATTRIBUTE);
            double i2Length = i2.value(Globals.INDEX_OF_LENGTH_ATTRIBUTE);
            
            if(measurementsAreEqual(i1Length, i2Length, measurementsAccuracy,
                    percentagePossibleBrickShrink, EXPANDED_UNCERTAINTY_FACTOR,COMPARISION_EPSYLON))
            {
                score += 1.0;
            }
        }
        
        if(!i1.isMissing(Globals.INDEX_OF_WIDTH_ATTRIBUTE) && !i2.isMissing(Globals.INDEX_OF_WIDTH_ATTRIBUTE))
        {
            double i1Width = i1.value(Globals.INDEX_OF_WIDTH_ATTRIBUTE);
            double i2Width = i2.value(Globals.INDEX_OF_WIDTH_ATTRIBUTE);

            if(measurementsAreEqual(i1Width, i2Width, measurementsAccuracy, 
                    percentagePossibleBrickShrink, EXPANDED_UNCERTAINTY_FACTOR, COMPARISION_EPSYLON))
            {
                score += 1.0;
            }
        }
        
        if(!i1.isMissing(Globals.INDEX_OF_HEIGHT_ATTRIBUTE) && !i2.isMissing(Globals.INDEX_OF_HEIGHT_ATTRIBUTE))
        {
            double i1Height = i1.value(Globals.INDEX_OF_HEIGHT_ATTRIBUTE);
            double i2Height = i2.value(Globals.INDEX_OF_HEIGHT_ATTRIBUTE);
            
            if(measurementsAreEqual(i1Height, i2Height, measurementsAccuracy, 
                    percentagePossibleBrickShrink, EXPANDED_UNCERTAINTY_FACTOR, COMPARISION_EPSYLON))
            {
                score += 1.0;
            }
        }
        
        return score/3.0;//normalizacja, co prawda ta sama cegla z sama soba nie zawsze bedzie miala wartosci 1
        // (gdy np. nie wszystkie wymiary beda)
    }

    private boolean measurementsAreEqual(double i1Measure, double i2Measure,
            double measurementsAccuracy,
            double percentagePossibleBrickShrink,
            double EXPANDED_UNCERTAINTY_FACTOR, double COMPARISION_EPSYLON) {
        
        double measureDifference = Math.abs(i1Measure - i2Measure);
        
        if(Parameters.getBrickComparator() == EBrickComparators.COUNTING_WITH_THRESHOLD_EXPANDED_UNCERTAINTY)
        {
            double expandedUncertainty = computeExpandedUncertainty(i1Measure, i2Measure, measurementsAccuracy,
                    percentagePossibleBrickShrink, EXPANDED_UNCERTAINTY_FACTOR);
            
            return measureDifference < (expandedUncertainty + COMPARISION_EPSYLON);
        }
        else if(Parameters.getBrickComparator() == EBrickComparators.COUNTING_WITH_THRESHOLD_SIMPLE_AVG_COMPARISION)
        {
            double measureAvg = (i1Measure + i2Measure)/2.0;//zakladam, ze obie cegly sa z tej samej formy, biorac ich
            // usrednuiona dlugosc do tego, aby obliczyc mozliwy skurcz
            //double measureAvg = Math.max(i1Measure + i2Measure);//podejscie ktore bedzie najwiecej akceptowac.
            // Zaklada ono, ze dluzsza cegla NIE ULEGLA skurczeniu i pacza sie, czy ktorsza zawiera sie w przedziale
            double acceptanceThreshold = measurementsAccuracy + (measureAvg*percentagePossibleBrickShrink);
            return measureDifference <= acceptanceThreshold + COMPARISION_EPSYLON;//uwzgledniony skurcz
        }
        return false;
    }

    private double computeExpandedUncertainty(double i1Value, double i2Value, double measurementsAccuracy, 
            double percentageShrink, double expandedUncertaintyFactor) 
    {
        double i1StandardUncertainty = measurementsAccuracy + i1Value*percentageShrink;//laczna "dokladnosc" pomiary FORMY dla celgy
        double i2StandardUncertainty = measurementsAccuracy + i2Value*percentageShrink;//laczna "dokladnosc" pomiary FORMY dla celgy
        double cumulativeUncertainty = Math.sqrt(Math.pow(i1StandardUncertainty, 2) + Math.pow(i2StandardUncertainty, 2));//przenoszenie niepewnosci
        return cumulativeUncertainty*expandedUncertaintyFactor;//niepewnosc rozszerzona
    }

}
