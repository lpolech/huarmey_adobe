package algorithm.comparator.wall;

import common.Globals;

import weka.core.Instances;
import data.DistanceMatrix;
import data.Wall;
import algorithm.comparator.WallComparator;

public class AvgSimilaritiesCounting implements WallComparator {

    @Override
    public double compare(Wall w1, Wall w2, DistanceMatrix bricksDM) {
        Instances w1Bricks = w1.getBricks();
        Instances w2Bricks = w2.getBricks();

        int w1NumberOfBricks = w1Bricks.numInstances();
        int w2NumberOfBricks = w2Bricks.numInstances();
        
        double countedSimilarities = 0.0d;
        for(int i = 0; i < w1NumberOfBricks; i++)//porownywanie kazdy z kazdym nie koniecznie moze byc najelpszym wyjsciem bo jezeli w scianie byly 3 formy o ekstremalnie roznych          
        {//rozmiarach to wtedy to bedzie bez sensu.. moze lepiej dla kazdej cegly wybierac najbardziej przystajaca do niej? NIESTETY FORMY MOGA SIE DUZO ROZNIC OD SIEBIE
            String brick1name = w1Bricks.instance(i).stringValue(Globals.INDEX_OF_CLASS_ATTRIBUTE) + "_" + (int)w1Bricks.instance(i).value(Globals.INDEX_OF_BRICK_NUMBER_ATTRIBUTE);
            for(int j = 0; j < w2NumberOfBricks; j++)
            {
                String brick2name = w2Bricks.instance(j).stringValue(Globals.INDEX_OF_CLASS_ATTRIBUTE) + "_" + (int)w2Bricks.instance(j).value(Globals.INDEX_OF_BRICK_NUMBER_ATTRIBUTE);
                countedSimilarities += bricksDM.getElement(brick1name, brick2name);
            }
        }
        return countedSimilarities/(double)(w1NumberOfBricks*w2NumberOfBricks);//usredniam wyniki, zeby wyeliminowac to, ze mniej pomiarow - lepszy wynik, normalizacja!
    }

}
