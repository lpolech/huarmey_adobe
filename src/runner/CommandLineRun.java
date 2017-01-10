package runner;

import java.io.File;

import common.CmdLineParser;
import weka.attributeSelection.BestFirst;
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
        //TODO: mo�na by w porownywaniu scian DOBIERAC odpowiednie ecgly do siebie, np. odejmowac od siebie wymiary, nastepnie wybrac ta pare cegiel, ktora ma najmniejsza roznice
        //(nie wiem jeszcze jak rozkminic, jezeli jedna cegla ma 3 wymiary, a druga ma tylko 2, moze zwiekszyc o 1/3 obliczonej roznicy?), w ten sposob dobrac NAJBLIZSZE sobie cegly
        //i ich roznice uzyc do oceny podobienstwa danych scian. W ten sposob mozna za pomoca roznic miedzy najblizszymi ceglami obliczyc podobienstwo miedzy scianami.
        //jezeli brakuje jakiegos wymiaru, to mozna go aproksymowac za pomoca sredniej danego wymiaru danej sciany, albo srednia z danych, albo KNN znalezc najblizsza cegle na podstawie
        //istniejacych wymiarow, 
        
        //obecne podjscie do podobienstwa scian rozwaza kazde paqry cegiel, dzieki czemu na pewno nic nie pominiemy, wada jest to, ze jezeli w scianie jest duzo form o rozniacych sie wartosciach,
        //to ta miara bedzie obnizala podobienstwo takiej sciany..,.
        
        //mozna na podstawie distance matrix wykorzystac jakies metody klasteryzacji
//      args = new String[]{
//              "-i",
//              "adobe-data-02-07.arff",
//              "-o",
//              "MAMAbpmwr",
//              "-cwtsac",
//              "-bpmwr",
//              "-v",
//              "-a",
//              "0.5",
//              "-s1",
//              "0.02",
//              "-s2",
//              "0.1"
//      };
        
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
            System.out.print("Computing brick ranking... ");
        }
        
        BrickComparator brickComparator = null;
        if(Parameters.getBrickComparator() == EBrickComparators.COUNTING_WITH_THRESHOLD_EXPANDED_UNCERTAINTY 
                || Parameters.getBrickComparator() == EBrickComparators.COUNTING_WITH_THRESHOLD_SIMPLE_AVG_COMPARISION)
        {
            brickComparator = new CountingWithThreshold(Parameters.getMeasurementsAccuracy(), 
                    Parameters.getPercentageMinBrickShrink(), Parameters.getPercentageMaxBrickShrink());
        }
        DistanceMatrix bricksRanking = BrickRanking.computeFullRanking(brickComparator, bricks, Parameters.getOutputFolder() + File.separator + "brickRanking.csv");
        
        if(Parameters.isVerbose())
        {
            System.out.print("Done.\n");
            System.out.print("Computing each brick ranking...");
        }
        
        BrickRanking.computeEachElementRanking(bricksRanking, Parameters.getOutputFolder() + File.separator + "eachBrickRanking.csv");
        
        if(Parameters.isVerbose())
        {
            System.out.print("Done.\n");
            System.out.print("Computing wall ranking... ");
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
        DistanceMatrix wallsRanking = WallRanking.computeFullRanking(wallComparator, bricksRanking, bricks, Parameters.getOutputFolder() + File.separator + "wallRanking.csv");
        
        if(Parameters.isVerbose())
        {
            System.out.print("Done.\n");
            System.out.print("Computing each wall ranking...");
        }
        
        WallRanking.computeEachElementRanking(wallsRanking, Parameters.getOutputFolder() + File.separator + "eachWallRanking.csv");
        
        if(Parameters.isVerbose())
        {   
            System.out.print("Done.\n");
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Elapsed time: " + elapsedTime/(60.0d*1000.0d) + "min");
        }
    }

}
