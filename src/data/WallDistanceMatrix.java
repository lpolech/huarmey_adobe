package data;

import common.WallComparisonResult;
import common.WallsBricksCount;

import java.util.HashMap;

public class WallDistanceMatrix extends DistanceMatrix {
    private HashMap<String, Integer> wallWithNumberOfBricks;
    private WallsBricksCount[][] w1w2BricksUsedInComparision;

    public WallDistanceMatrix(int numberOfElements, String[] matrixElements,
                              HashMap<String, Integer> wallWithNumberOfBricks)
    {
        super(numberOfElements, matrixElements);
        this.wallWithNumberOfBricks = wallWithNumberOfBricks;
        w1w2BricksUsedInComparision = initializeMatrixOfBricksUsedInComparision(numberOfElements);
    }

    private WallsBricksCount[][] initializeMatrixOfBricksUsedInComparision(int numberOfElements) {
        WallsBricksCount[][] initializedMatrix = new WallsBricksCount[numberOfElements][numberOfElements];

        for(int i = 0; i <initializedMatrix.length; i++) {
            for(int j = 0; j < initializedMatrix[i].length; j++) {
                initializedMatrix[i][j] = new WallsBricksCount();
            }
        }
        return initializedMatrix;
    }

    public void setElement(String e1, String e2, WallComparisonResult comparision)
    {
        setElement(e1, e2, comparision.getComparisionValue());
        setBricksUsedElement(e1, e2, comparision);
    }

    protected void setBricksUsedElement(String e1, String e2, WallComparisonResult comparision)
    {
        int row = findElementNumber(e1);
        int column = findElementNumber(e2);

        if(row < 0 || column < 0 || row > numberOfElements || column > numberOfElements)
        {
            System.err.println("setBricksUsedElement.setElement: Cannot find row and/or column elements for "
                    + e1 + ", " + e2 + ". Found row: " + row + ", column: " + column);
        }

        w1w2BricksUsedInComparision[row][column].setW1BricksCount(comparision.getNumberOfUsedBricksFromW1());
        w1w2BricksUsedInComparision[row][column].setW2BricksCount(comparision.getNumberOfUsedBricksFromW2());
    }

    public int getWallNumberOfBricks(String wallName)
    {
        return wallWithNumberOfBricks.get(wallName);
    }

    public WallsBricksCount getWallsBrickCount(String wall1Name, String wall2Name)
    {
        return w1w2BricksUsedInComparision[findElementNumber(wall1Name)][findElementNumber(wall2Name)];
    }

}
