package algorithm;

import java.util.Arrays;

import weka.core.Instance;
import weka.core.Instances;
import algorithm.comparator.BrickComparator;

import common.Globals;

import data.DistanceMatrix;

public class BrickRanking extends AbstractRanking {
    static public DistanceMatrix computeDistanceMatrix(BrickComparator comparator, Instances bricks, String outputFile)
    {
        DistanceMatrix dm = prepareDistanceMatrix(bricks);
        
        for(int i = 0; i < bricks.numInstances(); i++)
        {
            for(int j = 0; j < bricks.numInstances(); j++)
            {
                dm.setElement(bricks.instance(i).stringValue(Globals.INDEX_OF_CLASS_ATTRIBUTE) + "_"
                                + (int)bricks.instance(i).value(Globals.INDEX_OF_BRICK_NUMBER_ATTRIBUTE),
                              bricks.instance(j).stringValue(Globals.INDEX_OF_CLASS_ATTRIBUTE) + "_"
                                + (int)bricks.instance(j).value(Globals.INDEX_OF_BRICK_NUMBER_ATTRIBUTE),
                              comparator.compare(bricks.instance(i), bricks.instance(j)));
            }
        }
        
        dm.save(outputFile);
        return dm;
    }

    static private DistanceMatrix prepareDistanceMatrix(Instances bricks)
    {
        String[] matrixHeader = new String[bricks.numInstances()];
        for(int i = 0; i < bricks.numInstances(); i++)
        {
            Instance brick = bricks.instance(i);
            matrixHeader[i] = brick.stringValue(Globals.INDEX_OF_CLASS_ATTRIBUTE) + "_"
                    + (int)brick.value(Globals.INDEX_OF_BRICK_NUMBER_ATTRIBUTE);
        }
        
        Arrays.sort(matrixHeader);
        return new DistanceMatrix(bricks.numInstances(), matrixHeader);
    }
}
