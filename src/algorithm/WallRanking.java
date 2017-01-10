package algorithm;

import java.util.Arrays;
import java.util.HashMap;

import data.WallDistanceMatrix;
import weka.core.Instances;
import algorithm.comparator.WallComparator;
import data.DistanceMatrix;
import data.Wall;

public class WallRanking extends AbstractRanking {
    static public DistanceMatrix computeFullRanking(WallComparator comparator, DistanceMatrix bricksDM, Instances bricks, String outputFile)
    {
        Wall walls[] = createWalls(bricks);
        WallDistanceMatrix dm = prepareDistanceMatrix(walls);
        
        for(int i = 0; i < walls.length; i++)
        {
            for(int j = 0; j < walls.length; j++)
            {
                dm.setElement(walls[i].getWallName(),
                        walls[j].getWallName(), 
                        comparator.compare(walls[i], walls[j], bricksDM));
            }
        }
        
        dm.save(outputFile);
        return dm;
    }

    static private Wall[] createWalls(Instances bricks) {
        int numberOfWalls = bricks.numClasses();
        Wall walls[] = new Wall[numberOfWalls];
        for(int i = 0; i < numberOfWalls; i++)
        {
            walls[i] = new Wall(bricks, bricks.numInstances()/numberOfWalls);
        }
        
        for(int i = 0; i < bricks.numInstances(); i++)
        {
            int wallNumber = (int) bricks.instance(i).classValue();
            walls[wallNumber].addBrick(bricks.instance(i));
        }
        return walls;
    }
    
    static private WallDistanceMatrix prepareDistanceMatrix(Wall[] walls)
    {
        String[] matrixHeader = new String[walls.length];
        HashMap<String, Integer> wallWithNumberOfBricks = new HashMap<>(walls.length);
        for(int i = 0; i < walls.length; i++)
        {
            matrixHeader[i] = walls[i].getWallName();
            wallWithNumberOfBricks.put(matrixHeader[i], walls[i].getBricks().numInstances());
        }
        
        Arrays.sort(matrixHeader);
        return new WallDistanceMatrix(walls.length, matrixHeader, wallWithNumberOfBricks);
    }
}
