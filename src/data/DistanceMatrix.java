package data;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import common.Globals;

public class DistanceMatrix {
    private String[] matrixElements;
    private double[][] distanceMatrix;
    protected int numberOfElements;
    
    public DistanceMatrix(int numberOfElements, String[] matrixElements)
    {
        if(numberOfElements != matrixElements.length)
        {
            System.err.println("BricksDistanceMatrix Constructor: Number of bricks (" + numberOfElements 
                    + ") is different than number of elements in matrix (" + matrixElements.length + ").");
            System.exit(1);
        }
        this.numberOfElements = numberOfElements;
        this.distanceMatrix = new double[numberOfElements][numberOfElements];
        this.matrixElements = matrixElements.clone();
        Arrays.sort(matrixElements);
    }
    
    protected int findElementNumber(String elementName)
    {
        return Arrays.binarySearch(matrixElements, elementName);
    }
    
    public void setElement(String e1, String e2, double value)
    {
        int row = findElementNumber(e1);
        int column = findElementNumber(e2);
        
        if(row < 0 || column < 0 || row > numberOfElements || column > numberOfElements)
        {
            System.err.println("DistanceMatrix.setElement: Cannot find row and/or column elements for " 
                    + e1 + ", " + e2 + ". Found row: " + row + ", column: " + column);
        }
        
        distanceMatrix[row][column] = value;
    }

    public void setElement(int rowNumber, int columnNumber, double value)
    {
        distanceMatrix[rowNumber][columnNumber] = value;
    }

    public double getElement(String name1, String name2)
    {
        return distanceMatrix[findElementNumber(name1)][findElementNumber(name2)];
    }
    
    public double getElement(int rowNumber, int columnNumber)
    {
        return distanceMatrix[rowNumber][columnNumber];
    }
    
    public double[] getRow(String name)
    {
        return distanceMatrix[findElementNumber(name)];
    }
    
    public double[] getRow(int rowNumber)
    {
        return distanceMatrix[rowNumber];
    }
    
    public void save(String fileName)
    {
        Path path = Globals.createNewFile(fileName);
        
        try (FileWriter fw = new FileWriter(path.toFile())){
            for(int i = 0; i < numberOfElements; i++)
            {
                fw.append(Globals.CSV_SEPARATOR + matrixElements[i]);
            }
            fw.append("\n");
            
            for(int i = 0; i < numberOfElements; i++)
            {
                fw.append(matrixElements[i]);
                for(int j = 0; j < numberOfElements; j++)
                {
                    fw.append(Globals.CSV_SEPARATOR + distanceMatrix[i][j]);
                }
                fw.append("\n");
            }
        } catch (IOException e) {
            System.err.println("DistanceMatrix.save : Error while filling content of " + fileName + " file.");
            e.printStackTrace();
        }
        
    }

    public String[] getMatrixElements() {
        return matrixElements;
    }
}
