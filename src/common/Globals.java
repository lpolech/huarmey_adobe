package common;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Globals {
    public static String WALL_NAME_ELEMENTS_PARTS = "_";
    public static String CLASS_ATTR_NAME = "wall";
    public static String COMMENT_ATTR_NAME = "comment";
    public static String CSV_SEPARATOR = ";";
    public static int INDEX_OF_CLASS_ATTRIBUTE = 0;
    public static int INDEX_OF_LENGTH_ATTRIBUTE = 1;
    public static int INDEX_OF_WIDTH_ATTRIBUTE = 2;
    public static int INDEX_OF_HEIGHT_ATTRIBUTE = 3;
    public static int INDEX_OF_BRICK_NUMBER_ATTRIBUTE = 4;
    public static int INDEX_OF_COMMENT_ATTRIBUTE = 5;
    
    public static Path createNewFile(String fileName) {
        Path path = Paths.get(fileName);
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e1) {
            System.err.println("Cannot delete or create " + fileName + " file.");
            e1.printStackTrace();
        }
        return path;
    }
    
    public static boolean createNewFileAndFillContent(String fileName, String[] fileContent)
    {
        Path path = createNewFile(fileName);
        try (FileWriter fw = new FileWriter(path.toFile()))
        {
            for(int i = 0; i < fileContent.length; i++)
            {
                fw.append(fileContent[i]);
            }
        } 
        catch (IOException e) {
            System.err.println("DistanceMatrix.save : Error while filling content of " + fileName + " file.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean createNewFileAndFillContent(String fileName,
            String fileContent) {
        Path path = createNewFile(fileName);
        try (FileWriter fw = new FileWriter(path.toFile()))
        {
            fw.append(fileContent);
        } 
        catch (IOException e) {
            System.err.println("DistanceMatrix.save : Error while filling content of " + fileName + " file.");
            e.printStackTrace();
            return false;
        }
        return true;
        
    }

    public static boolean isDoubleExtremelyNearZero(double number)
    {
        return Math.abs(number) <= 0.0000000001;
    }
}
