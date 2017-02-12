package data;

import common.Globals;

import weka.core.Instance;
import weka.core.Instances;

public class Wall {
    private Instances bricks;
    
    public Wall(Instances headerInfo, int initialCapacity)
    {
        bricks = new Instances(headerInfo, initialCapacity);
    }
    
    public Wall(Instances bricks)
    {
        this.bricks = bricks;
    }
    
    public void addBrick(Instance brick)
    {
        bricks.add(brick);
    }
    
    public Instances getBricks()
    {
        return bricks;
    }
    
    public String getWallName()
    {
        String wallName = null;
        if(bricks.numInstances() < 1)
        {
            System.err.println("Wall.getWallName() there is " + bricks.numInstances() + " so can't get wall name!");
        }
        else
        {
            wallName = bricks.firstInstance().stringValue(Globals.INDEX_OF_CLASS_ATTRIBUTE);
            for(int i = 0; i < bricks.numInstances(); i++)
            {
                if(!bricks.instance(i).stringValue(Globals.INDEX_OF_CLASS_ATTRIBUTE).equals(wallName))
                {
                    System.err.println("Wall::getWallName() there are bricks from more than one wall! Reference name: "
                            + wallName + " and found additional wall: "
                            + bricks.instance(i).stringValue(Globals.INDEX_OF_CLASS_ATTRIBUTE));
                }
            }
        }
        return wallName;
    }

    public void delete(String brickName) {
        String brickNumber = brickName.split("_")[1];
        for(int i = 0; i < bricks.numInstances(); i++) {
            if(brickNumber.equals(bricks.instance(i).stringValue(Globals.INDEX_OF_BRICK_NUMBER_ATTRIBUTE))) {
                bricks.delete(i);
                return;
            }
        }
    }
}
