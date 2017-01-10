package algorithm.comparator.wall;

import java.util.ArrayList;

import weka.core.Instance;
import weka.core.Instances;

import common.Globals;

import data.DistanceMatrix;
import data.Wall;
import algorithm.comparator.WallComparator;

public class BrickPairMatchingWithoutReturn implements WallComparator {
//TODO: w obecnej formie ta klasa jest dostosowana jedynie do podobienstwa cegiel CountingWithThreshold!
    //ZAKLADAM, ze nie ma cegly tylko z 1 wymiarem
    @Override
    public double compare(Wall w1, Wall w2, DistanceMatrix bricksDM) {
        Instances w1Bricks = w1.getBricks();
        Instances w2Bricks = w2.getBricks();

        int w1NumberOfBricks = w1Bricks.numInstances();
        int w2NumberOfBricks = w2Bricks.numInstances();
        
        double countedSimilarities = 0.0d;
        ArrayList<Integer> usedBricks = new ArrayList<Integer>();
        for(int i = 0; i < w1NumberOfBricks; i++)//porownywanie kazdy z kazdym nie koniecznie moze byc najelpszym wyjsciem bo jezeli w scianie byly 3 formy o ekstremalnie roznych          
        {//rozmiarach to wtedy to bedzie bez sensu.. moze lepiej dla kazdej cegly wybierac najbardziej przystajaca do niej? NIESTETY FORMY MOGA SIE DUZO ROZNIC OD SIEBIE
            Instance brick1 = w1Bricks.instance(i);
            String brick1name = brick1.stringValue(Globals.INDEX_OF_CLASS_ATTRIBUTE) + "_" + (int)w1Bricks.instance(i).value(Globals.INDEX_OF_BRICK_NUMBER_ATTRIBUTE);
            double bestBrickSim = 0;
            int bestBrickIndex = Integer.MIN_VALUE;
            boolean pairFound = false;
            for(int j = 0; j < w2NumberOfBricks && !pairFound; j++)
            {
                if(!usedBricks.contains(j))//this brick is not used yet TODO: zrobic binarysearch!
                {
                    Instance brick2 = w2Bricks.instance(j);
                    String brick2name = brick2.stringValue(Globals.INDEX_OF_CLASS_ATTRIBUTE) + "_" + (int)w2Bricks.instance(j).value(Globals.INDEX_OF_BRICK_NUMBER_ATTRIBUTE);
                    double brickSim = bricksDM.getElement(brick1name, brick2name);
                    if(brickSim >= 0.9999999d)//podobienstwo na 100%
                    {//pairFound!
                        usedBricks.add(j);
                        pairFound = true;
                        countedSimilarities += 1.0d;
                    }
                    else if(brickSim >= 0.6666666d && brickSim < 0.9999999d && brickSim > bestBrickSim)
                    {
                        if(brick1.isMissing(Globals.INDEX_OF_HEIGHT_ATTRIBUTE) || brick1.isMissing(Globals.INDEX_OF_LENGTH_ATTRIBUTE) 
                                || brick1.isMissing(Globals.INDEX_OF_WIDTH_ATTRIBUTE)
                                || brick2.isMissing(Globals.INDEX_OF_HEIGHT_ATTRIBUTE) || brick2.isMissing(Globals.INDEX_OF_LENGTH_ATTRIBUTE)
                                || brick2.isMissing(Globals.INDEX_OF_WIDTH_ATTRIBUTE))
                        {
                            bestBrickIndex = j;
                            bestBrickSim = brickSim;
                        }
                    }
                }
            }
            if(!pairFound)//did not find perfect match
            {
                if(bestBrickIndex != Integer.MIN_VALUE)//have best fitting match
                {
                    usedBricks.add(bestBrickIndex);
                    countedSimilarities += 1.0d;
                }
            }
        }
        return countedSimilarities/(double)Math.min(w1NumberOfBricks, w2NumberOfBricks);//normalisation bymaximum number of possible pairs
    }

}
