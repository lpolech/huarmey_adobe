package algorithm;

import java.util.Arrays;

import common.Globals;

import common.WallsBricksCount;
import data.DistanceMatrix;
import data.WallDistanceMatrix;

public abstract class AbstractRanking {
    static public boolean parseAndSaveEachElementRanking(DistanceMatrix fullRanking, String outputFile)
    {
        String[] eachElementRowRanking = new String[fullRanking.getMatrixElements().length];
        for(int i = 0; i < fullRanking.getMatrixElements().length; i++)
        {
            String consideredElement = fullRanking.getMatrixElements()[i];
            eachElementRowRanking[i] = consideredElement;
            if(fullRanking instanceof WallDistanceMatrix)
                eachElementRowRanking[i] += "(b:"
                        + ((WallDistanceMatrix)fullRanking).getWallNumberOfBricks(consideredElement)
                        + ")";
            eachElementRowRanking[i] += Globals.CSV_SEPARATOR;

            String[] elementRankingNames = fullRanking.getMatrixElements().clone();
            double[] consideredElementRow = fullRanking.getRow(consideredElement).clone();
            Arrays.sort(consideredElementRow);//niestety sortuje w sposob ROSNACY, wiec bede rozkminial ten ranking od konca
            
            for(int j = consideredElementRow.length-1; j >= 0 && consideredElementRow[j] != 0.0d; j--)
            {
                double value = consideredElementRow[j];
                boolean found = false;
                for(int k = 0; k < elementRankingNames.length && !found; k++)
                {
                    String secondElement = elementRankingNames[k];
                    if(secondElement != null && k != i)
                    {
                        double similarityValue = fullRanking.getElement(consideredElement, secondElement);
                        if(value == similarityValue)
                        {
                            elementRankingNames[k] = null;//wskazanie, ze ten element juz zostal wykorzystany
                            eachElementRowRanking[i] += secondElement;
                            if(fullRanking instanceof WallDistanceMatrix)
                            {
                                WallsBricksCount numberOfUsedBricks =
                                        ((WallDistanceMatrix) fullRanking).getWallsBrickCount(consideredElement, secondElement);
                                eachElementRowRanking[i] += "(" + numberOfUsedBricks.getW1BricksCount() + "vs"
                                        + numberOfUsedBricks.getW2BricksCount() + ")";
                            }
                            eachElementRowRanking[i] += Globals.CSV_SEPARATOR + value + Globals.CSV_SEPARATOR;
                            found  = true;
                        }
                    }
                }
            }
            eachElementRowRanking[i] += "\n";
        }
        return Globals.createNewFileAndFillContent(outputFile, eachElementRowRanking);
    }
}
