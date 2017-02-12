package runner;

import java.io.File;

import algorithm.comparator.wall.LeastInvolvedBrickMutualNearestNeighbour;
import algorithm.comparator.wall.MutualNearestNeighbor;
import common.CmdLineParser;
import weka.core.Instances;
import algorithm.BrickRanking;
import algorithm.WallRanking;
import algorithm.comparator.BrickComparator;
import algorithm.comparator.EBrickComparators;
import algorithm.comparator.EWallComparators;
import algorithm.comparator.WallComparator;
import algorithm.comparator.brick.CountingWithThreshold;
import algorithm.comparator.wall.AvgSimilaritiesCounting;
import algorithm.comparator.wall.BrickPairMatchingWithoutReturn;
import data.DistanceMatrix;
import data.Loader;
import data.Parameters;

public class CommandLineRun {

    public static void main(String[] args) 
    {
        long startTime = System.currentTimeMillis();
        // TODO: mozna by w porownywaniu scian DOBIERAC odpowiednie cegly do siebie, np. odejmowac od siebie wymiary,
        // nastepnie wybrac ta pare cegiel, ktora ma najmniejsza roznice (nie wiem jeszcze jak rozkminic, jezeli jedna
        // cegla ma 3 wymiary, a druga ma tylko 2, moze zwiekszyc o 1/3 obliczonej roznicy? Albo obliczyc podobinestwo
        // jedynie na podstawie tych wymiarow, ktore sa obecne), w ten sposob dobrac NAJBLIZSZE sobie cegly (NAJBLIZSZY
        // WZAJEMNY SASIAD) i ich roznice uzyc do oceny podobienstwa danych scian. W ten sposob mozna za pomoca roznic
        // miedzy najblizszymi ceglami obliczyc podobienstwo miedzy scianami. Jezeli brakuje jakiegos wymiaru, to mozna
        // go aproksymowac za pomoca sredniej danego wymiaru danej sciany, albo srednia z danych, albo KNN znalezc
        // najblizsza cegle na podstawie istniejacych wymiarow,
        
        // obecne podjscie do podobienstwa scian rozwaza kazde pary cegiel, dzieki czemu na pewno nic nie pominiemy,
        // wada jest to, ze jezeli w scianie jest duzo form o rozniacych sie wartosciach, to ta miara bedzie obnizala
        // podobienstwo takiej sciany...
        
        //mozna na podstawie distance matrix wykorzystac jakies metody klasteryzacji
        args = new String[]{
                "-i",
//                "proba.arff",
                "adobe-data-27_02_17.arff",
//                "noMNN.arff",
//                "poprawka_adobe-data-07_01.arff",
                "-o",
                "adobe-data-27_02_17_mutual_nearest_neighbour_a1",
                "-cwtsac",
//              "-bpmwr",
//                "-asc",
//                "-mnn",
                "-libmnn",
                "-v",
                "-a",
                "1.0",
                "-s1",
                "0.0",
                "-s2",
                "0.0"
        };
        
        CmdLineParser parser = new CmdLineParser();
        parser.parse(args);
        
        if(Parameters.isVerbose())
        {
            System.out.println(Parameters.printParameters());
            System.out.print("Loading data... ");
        }
        
        Instances bricks = Loader.load(Parameters.getInputDataFilePath(), true);
        Parameters.saveParametersToOutputFolder();
        
        if(Parameters.isVerbose())
        {
            System.out.print("Done.\n");
            System.out.print("Computing brick distance matrix... ");
        }
        
        BrickComparator brickComparator = null;
        if(Parameters.getBrickComparator() == EBrickComparators.COUNTING_WITH_THRESHOLD_EXPANDED_UNCERTAINTY 
                || Parameters.getBrickComparator() == EBrickComparators.COUNTING_WITH_THRESHOLD_SIMPLE_AVG_COMPARISION)
        {
            brickComparator = new CountingWithThreshold(Parameters.getMeasurementsAccuracy(), 
                    Parameters.getPercentageMinBrickShrink(), Parameters.getPercentageMaxBrickShrink());
        }
        DistanceMatrix bricksRanking = BrickRanking.computeDistanceMatrix(brickComparator, bricks,
                Parameters.getOutputFolder() + File.separator + "brickRanking.csv");
        
        if(Parameters.isVerbose())
        {
            System.out.print("Done.\n");
            System.out.print("Parsing and saving each brick ranking...");
        }
        
        BrickRanking.parseAndSaveEachElementRanking(bricksRanking, Parameters.getOutputFolder() + File.separator
                + "eachBrickRanking.csv");
        
        if(Parameters.isVerbose())
        {
            System.out.print("Done.\n");
            System.out.print("Computing wall distance matrix... ");
        }
        
        WallComparator wallComparator = null;
        if(Parameters.getWallComparator() == EWallComparators.AVG_SIMILARITIES_COUNTING)
        {
            wallComparator = new AvgSimilaritiesCounting();
        }
        else if(Parameters.getWallComparator() == EWallComparators.BEST_PAIR_MATCHING)
        {
            wallComparator = new BrickPairMatchingWithoutReturn();
        }
        else if(Parameters.getWallComparator() == EWallComparators.MUTUAL_NEAREST_NEIGHBOUR)
        {
            wallComparator = new MutualNearestNeighbor();
        }
        else if(Parameters.getWallComparator() == EWallComparators.LEAST_INVOLVED_BRICK_MUTUAL_NEAREST_NEIGHBOUR)
        {
            wallComparator = new LeastInvolvedBrickMutualNearestNeighbour(false);
        }

        DistanceMatrix wallsRanking = WallRanking.computeFullRanking(wallComparator, bricksRanking, bricks,
                Parameters.getOutputFolder() + File.separator + "wallRanking.csv");
        
        if(Parameters.isVerbose())
        {
            System.out.print("Done.\n");
            System.out.print("Parsing and saving each wall ranking...");
        }
        
        WallRanking.parseAndSaveEachElementRanking(wallsRanking, Parameters.getOutputFolder() + File.separator
                + "eachWallRanking.csv");
        
        if(Parameters.isVerbose())
        {   
            System.out.print("Done.\n");
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Elapsed time: " + elapsedTime/(60.0d*1000.0d) + "min");
        }
    }

}
