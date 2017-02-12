package algorithm.comparator.wall;


import algorithm.comparator.WallComparator;
import common.Globals;
import common.WallComparisonResult;
import data.DistanceMatrix;
import data.Wall;
import weka.core.Instance;

import java.util.*;

public class MutualNearestNeighbor implements WallComparator {
    @Override
    public WallComparisonResult compare(Wall w1, Wall w2, DistanceMatrix bricksDM) {
        //we can match wall with little number of bricks with wall with larger (because the smaller wall has,
        // potentially, less hypotheses
        Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> w1Rankings
                = computeBrickRanking(w1, w2, bricksDM);
        Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> w2Rankings
                = computeBrickRanking(w2, w1, bricksDM);

        double overallSimilarity = 0;
        int numberOfCreatedPairs = 0;
        boolean pairedAtLeastOneElement = true;

        while(pairedAtLeastOneElement) {
            pairedAtLeastOneElement = false;
            ArrayList<String> w1BrickNames = getBrickNames(w1Rankings);
            for(int i = 0; i < w1BrickNames.size() && itIsPossibleToCreateAPair(w1Rankings, w2Rankings); i++) {
                String w1BrickName = w1BrickNames.get(i);
                double theHighestSimilarityValue = w1Rankings.get(w1BrickName).get(0).getValue();
                LinkedList<String> w1NearestNeighbours = getBrickNamesWithTheHighestSimilarityValue(w1Rankings.get(w1BrickName));
                Collections.shuffle(w1NearestNeighbours);

                for(String nearestNeighbour: w1NearestNeighbours) {
                    LinkedList<String> w2NearestNeighbours =
                            getBrickNamesWithTheHighestSimilarityValue(w2Rankings.get(nearestNeighbour));

                    if (w2NearestNeighbours.contains(w1BrickName)) {
                        overallSimilarity += theHighestSimilarityValue;
                        numberOfCreatedPairs++;
                        pairedAtLeastOneElement = true;
                        updateRankingsWithMatch(w1Rankings, w2Rankings, w1BrickName, nearestNeighbour);
                        break;
                    }
                }
            }
        }

        return new WallComparisonResult(overallSimilarity/numberOfCreatedPairs, numberOfCreatedPairs, numberOfCreatedPairs);
        //one can further look for brick pairs
    }

    protected void updateRankingsWithMatch(Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> w1Rankings,
                                           Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> w2Rankings,
                                           String firstBrickName, String secondBrickName) {
        if(w1Rankings.containsKey(firstBrickName) && w2Rankings.containsKey(secondBrickName)) {
            w1Rankings.remove(firstBrickName);
            w2Rankings.remove(secondBrickName);
            removeBrickFromRankings(w1Rankings, secondBrickName);
            removeBrickFromRankings(w2Rankings, firstBrickName);
        }
        else if(w1Rankings.containsKey(secondBrickName) && w2Rankings.containsKey(firstBrickName)) {
            w1Rankings.remove(secondBrickName);
            w2Rankings.remove(firstBrickName);
            removeBrickFromRankings(w1Rankings, firstBrickName);
            removeBrickFromRankings(w2Rankings, secondBrickName);
        }
        else {
            System.err.println("Cannot find either " + firstBrickName + " and/or " + secondBrickName
                    + " in w1Rankings and w2Rankings!");
        }
    }

    private boolean itIsPossibleToCreateAPair(Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> w1Rankings,
                                              Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> w2Rankings) {
        return !w1Rankings.isEmpty() && !w2Rankings.isEmpty();
    }

    protected LinkedList<String> getBrickNamesWithTheHighestSimilarityValue(ArrayList<AbstractMap.SimpleEntry<String,Double>> bricks) {
        LinkedList<String> returnBricks = new LinkedList<>();
        double theHighestSimilarity = bricks.get(0).getValue();

        boolean elementFound = true;
        for(int i = 0; i < bricks.size() && elementFound; i++) {
            if(bricks.get(i).getValue() == theHighestSimilarity) {
                returnBricks.add(bricks.get(i).getKey());
            }
            else {
                elementFound = false;
            }
        }
        return returnBricks;
    }

    private ArrayList<String> getBrickNames(Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> rankings) {
        ArrayList<String> wallBrickNames = new ArrayList<>();
        for(Enumeration<String> e = rankings.keys(); e.hasMoreElements();) {
            wallBrickNames.add(e.nextElement());
        }
        return wallBrickNames;
    }

    protected void removeBrickFromRankings(Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> rankings,
                                        String brickName) {
        for(ArrayList<AbstractMap.SimpleEntry<String, Double>> ranking: rankings.values()) {
            for(AbstractMap.SimpleEntry<String, Double> elem: ranking) {
                if(elem.getKey().equals(brickName)) {
                    ranking.remove(elem);
                    break;
                }
            }
        }
    }

    protected Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>>
                        computeBrickRanking(Wall w1, Wall w2, DistanceMatrix bricksDM) {
        Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> brickRankings
                = new Hashtable<>(w1.getBricks().numInstances(), 1.0f);

        for(int i = 0; i < w1.getBricks().numInstances(); i++) {
            Instance w1Brick = w1.getBricks().instance(i);
            String w1BrickName = getBrickName(w1Brick);
            ArrayList<AbstractMap.SimpleEntry<String, Double>> brick1RankingElements =
                    new ArrayList<>(w2.getBricks().numInstances());

            for(int j = 0; j < w2.getBricks().numInstances(); j++) {
                Instance w2Brick = w2.getBricks().instance(j);
                String w2BrickName = getBrickName(w2Brick);

                double similarity = bricksDM.getElement(w1BrickName, w2BrickName);
                insertElementInDecreasingSimilarityOrder(brick1RankingElements,
                        new AbstractMap.SimpleEntry<>(w2BrickName, similarity));
            }
            brickRankings.put(w1BrickName, brick1RankingElements);
        }
        return brickRankings;
    }

    private String getBrickName(Instance brick) {
        return brick.stringValue(Globals.INDEX_OF_CLASS_ATTRIBUTE) + "_"
                + (int) brick.value(Globals.INDEX_OF_BRICK_NUMBER_ATTRIBUTE);
    }

    private void insertElementInDecreasingSimilarityOrder(ArrayList<AbstractMap.SimpleEntry<String, Double>> ranking,
                                                          AbstractMap.SimpleEntry<String, Double> element) {
        double elemSim = element.getValue();
        boolean added = false;

        for(int i = 0; i < ranking.size() && !added; i++) {
            double rankingSim = ranking.get(i).getValue();
            if(elemSim >= rankingSim) {// decreasing order
                ranking.add(i, element);
                added = true;
            }
        }

        if(!added) {
            ranking.add(ranking.size(), element);
        }
    }
}
