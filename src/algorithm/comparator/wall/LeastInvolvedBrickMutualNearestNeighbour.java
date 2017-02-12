package algorithm.comparator.wall;

import algorithm.comparator.WallComparator;
import common.Globals;
import common.WallComparisonResult;
import data.DistanceMatrix;
import data.Wall;

import java.util.*;

public class LeastInvolvedBrickMutualNearestNeighbour extends MutualNearestNeighbor implements WallComparator {
    private boolean pairBricksWhenThereIsNoMNN = false;

    public LeastInvolvedBrickMutualNearestNeighbour(boolean pairBricksWhenThereIsNoMNN) {
        this.pairBricksWhenThereIsNoMNN = pairBricksWhenThereIsNoMNN;
    }

    @Override
    public WallComparisonResult compare(Wall w1, Wall w2, DistanceMatrix bricksDM) {
        ArrayList<AbstractMap.SimpleEntry<String[], Double>> MNN = new ArrayList<>();
        Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> w1Rankings
                = computeBrickRanking(w1, w2, bricksDM);
        Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> w2Rankings
                = computeBrickRanking(w2, w1, bricksDM);
        ArrayList<AbstractMap.SimpleEntry<String[], Double>> nearestNeighbours = null;


        while(!(nearestNeighbours = getNearestNeighboursForAllBricks(w1Rankings, w2Rankings)).isEmpty()) {
            ArrayList<AbstractMap.SimpleEntry<String[], Double>> redundantMNN =
                    getAllMutualNearestNeighboursInDescendingOrder(nearestNeighbours);
            if(redundantMNN.isEmpty()) {
                String msg = "There are no more MNN between walls: " + w1.getWallName() + " and " + w2.getWallName()
                        + ". The number of found MNN is: " + MNN.size() + ".";
                if(MNN.isEmpty()) {
                    msg += "============================THERE ARE NO MNN! DEEP INVESTIGATION IS ADVISED!";
                }
                System.err.println(msg);
                break;
            }
            ArrayList<AbstractMap.SimpleEntry<String[], Double>> chosenMNN =
                    chooseFinalMutualNearestNeighbours(redundantMNN);
            MNN.addAll(chosenMNN);
            updateRankings(w1Rankings, w2Rankings, chosenMNN);
        }

        return getCopmarisionResult(MNN, Math.min(w1.getBricks().numInstances(), w2.getBricks().numInstances()));
    }

    private void updateRankings(Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> w1Rankings,
                                Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> w2Rankings,
                                ArrayList<AbstractMap.SimpleEntry<String[], Double>> chosenMNN) {
        for (AbstractMap.SimpleEntry<String[], Double> elem : chosenMNN) {
            String[] bricks = elem.getKey();
            updateRankingsWithMatch(w1Rankings, w2Rankings, bricks[0], bricks[1]);
        }
    }

    private WallComparisonResult getCopmarisionResult(ArrayList<AbstractMap.SimpleEntry<String[], Double>> mnn,
                                                      int numberOfPossiblePairs) {
        double comparisonValue = 0;
        int numberOfPairs = mnn.size();
        for(AbstractMap.SimpleEntry<String[], Double> entry: mnn) {
            comparisonValue += entry.getValue();
        }
        return new WallComparisonResult(comparisonValue/numberOfPossiblePairs, numberOfPairs, numberOfPairs);
    }

    private ArrayList<AbstractMap.SimpleEntry<String[], Double>> chooseFinalMutualNearestNeighbours(
            ArrayList<AbstractMap.SimpleEntry<String[], Double>> redundantMNN) {
        ArrayList<AbstractMap.SimpleEntry<String[], Double>> chosenMNNs = new ArrayList<>();
        ArrayList<AbstractMap.SimpleEntry<String[], Double>> MNNwithTheHighestValue = getMNNwithTheHighestSimValue(redundantMNN);
        while(!MNNwithTheHighestValue.isEmpty()) {
            //LIB heuristic
            chooseLeastInvolvedBrick(chosenMNNs, MNNwithTheHighestValue);
        }
        return chosenMNNs;
    }

    private void chooseLeastInvolvedBrick(ArrayList<AbstractMap.SimpleEntry<String[], Double>> chosenMNNs,
                                          ArrayList<AbstractMap.SimpleEntry<String[], Double>> MNNwithTheHighestValue) {
        String leastInvolvedBrick = getNameOfChosenLeastInvolvedBrick(MNNwithTheHighestValue);
        String leastInvolvedBrickPairSecondBrick = determineMNNsecondBrickAndMoveMNNfromCollection(chosenMNNs,
                MNNwithTheHighestValue, leastInvolvedBrick);
        removeInvalidPairsFromCollection(MNNwithTheHighestValue, leastInvolvedBrick, leastInvolvedBrickPairSecondBrick);
    }

    private ArrayList<AbstractMap.SimpleEntry<String[], Double>> getMNNwithTheHighestSimValue(
            ArrayList<AbstractMap.SimpleEntry<String[], Double>> redundantMNN) {
        ArrayList<AbstractMap.SimpleEntry<String[], Double>> MNNwithTheHighestValue = new ArrayList<>();
        double theHighestSimilarityValue = redundantMNN.get(0).getValue();
        for (int i = 0; i < redundantMNN.size(); i++) {
            AbstractMap.SimpleEntry<String[], Double> mnn = redundantMNN.get(i);
            if (mnn.getValue() == theHighestSimilarityValue) {
                MNNwithTheHighestValue.add(mnn);
            } else break;
        }
        return MNNwithTheHighestValue;
    }

    private String getNameOfChosenLeastInvolvedBrick(
            ArrayList<AbstractMap.SimpleEntry<String[], Double>> MNNwithTheHighestValue) {
        HashMap<String, Integer> histogramOfBrickInvolvements = getHistogramOfBricksInvolvement(MNNwithTheHighestValue);
        String leastInvolvedBrick = "";
        int minCount = Integer.MAX_VALUE;
        LinkedList<Map.Entry<String, Integer>> elementsList = new LinkedList<>(histogramOfBrickInvolvements.entrySet());
        Collections.shuffle(elementsList);//there could be several bricks with min count, so shuffle gives me
        // a randomness in choosing a specific one (the first found)
        for (Map.Entry<String, Integer> elem : elementsList) {
            if (elem.getValue() < minCount) {
                minCount = elem.getValue();
                leastInvolvedBrick = elem.getKey();
            }
        }
        return leastInvolvedBrick;
    }

    private HashMap<String, Integer> getHistogramOfBricksInvolvement(
            ArrayList<AbstractMap.SimpleEntry<String[], Double>> MNNwithTheHighestValue) {
        HashMap<String, Integer> brickWithNumberOfPairsThatItIsInvolved = new HashMap<>();
        for (int i = 0; i < MNNwithTheHighestValue.size(); i++) {
            String[] pair = MNNwithTheHighestValue.get(i).getKey();
            incrementElementOrInitialise(brickWithNumberOfPairsThatItIsInvolved, pair[0]);
            incrementElementOrInitialise(brickWithNumberOfPairsThatItIsInvolved, pair[1]);
        }
        return brickWithNumberOfPairsThatItIsInvolved;
    }

    private void removeInvalidPairsFromCollection(
            ArrayList<AbstractMap.SimpleEntry<String[], Double>> MNNwithTheHighestValue, String leastInvolvedBrick,
            String leastInvolvedBrickPairSecondBrick) {
        for (int i = MNNwithTheHighestValue.size()-1; i >= 0; i--) {//iteraring from the last one to first because the
            // intention is to remove objects
            AbstractMap.SimpleEntry<String[], Double> pair = MNNwithTheHighestValue.get(i);
            if (pairInvolveAnyBrickFromChosenMNN(leastInvolvedBrick, leastInvolvedBrickPairSecondBrick, pair)) {
                MNNwithTheHighestValue.remove(i);
            }
        }
    }

    private boolean pairInvolveAnyBrickFromChosenMNN(String leastInvolvedBrick, String leastInvolvedBrickPairSecondBrick,
                                                     AbstractMap.SimpleEntry<String[], Double> pair) {
        return pair.getKey()[0].equals(leastInvolvedBrick)
                || pair.getKey()[1].equals(leastInvolvedBrick)
                || pair.getKey()[0].equals(leastInvolvedBrickPairSecondBrick)
                || pair.getKey()[1].equals(leastInvolvedBrickPairSecondBrick);
    }

    private String determineMNNsecondBrickAndMoveMNNfromCollection(
            ArrayList<AbstractMap.SimpleEntry<String[], Double>> chosenMNNs,
            ArrayList<AbstractMap.SimpleEntry<String[], Double>> MNNwithTheHighestValue, String leastInvolvedBrick) {
        AbstractMap.SimpleEntry<String[], Double> chosenPair = chooseFinalMNN(MNNwithTheHighestValue, leastInvolvedBrick);
        String leastInvolvedBrickPairSecondBrick = chosenPair.getKey()[chosenPair.getKey()[0].equals(leastInvolvedBrick)? 1:0];
        moveElement(MNNwithTheHighestValue, chosenMNNs, chosenPair);
        return leastInvolvedBrickPairSecondBrick;
    }

    private AbstractMap.SimpleEntry<String[], Double> chooseFinalMNN(
            ArrayList<AbstractMap.SimpleEntry<String[], Double>> MNNwithTheHighestValue, String leastInvolvedBrick) {
        AbstractMap.SimpleEntry<String[], Double> returnPair = null;
        Collections.shuffle(MNNwithTheHighestValue);//least involved brick can be in relation with several other bricks
        // that is why this shuffle gives a randomness in the way which pair will be chosen (the last found)
        for(AbstractMap.SimpleEntry<String[], Double> pair: MNNwithTheHighestValue) {
            if(pair.getKey()[0].equals(leastInvolvedBrick) && pair.getKey()[1].equals(leastInvolvedBrick)) {//heuristic
                //that prefers creation of pairs involving two the same bricks. It works only when the same walls are compared
                return pair;
            }
            else if (pair.getKey()[0].equals(leastInvolvedBrick) || pair.getKey()[1].equals(leastInvolvedBrick)) {
               returnPair = pair;
            }
        }
        return returnPair;
    }

    private void insertElementInDecreasingSimilarityOrder(ArrayList<AbstractMap.SimpleEntry<String[], Double>> possibleMNN,
                                                          AbstractMap.SimpleEntry<String[], Double> element) {
        double elemSim = element.getValue();
        boolean added = false;
        for(int i = 0; i < possibleMNN.size() && !added; i++) {
            double rankingSim = possibleMNN.get(i).getValue();
            if(elemSim >= rankingSim) {// decreasing order
                possibleMNN.add(i, element);
                added = true;
            }
        }
        if(!added) {
            possibleMNN.add(possibleMNN.size(), element);
        }
    }

    private void incrementElementOrInitialise(HashMap<String, Integer> map, String elem) {
        if(map.containsKey(elem)) {
            map.put(elem, map.get(elem) + 1);
        }
        else {
            map.put(elem, 1);
        }
    }

    private void moveElement(ArrayList<AbstractMap.SimpleEntry<String[], Double>> source,
                             ArrayList<AbstractMap.SimpleEntry<String[], Double>> destination,
                             AbstractMap.SimpleEntry<String[], Double> elementToMove) {
        destination.add(elementToMove);
        source.remove(elementToMove);
    }

    private ArrayList<AbstractMap.SimpleEntry<String[], Double>> getNearestNeighboursForAllBricks(
            Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> w1Rankings,
            Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> w2Rankings) {
        ArrayList<AbstractMap.SimpleEntry<String[], Double>> nearestNeighbours = new ArrayList<>();
        insertNearestNeighboursFromRanking(w1Rankings, nearestNeighbours);
        insertNearestNeighboursFromRanking(w2Rankings, nearestNeighbours);
        return nearestNeighbours;
    }

    private ArrayList<AbstractMap.SimpleEntry<String[], Double>> getAllMutualNearestNeighboursInDescendingOrder(
            ArrayList<AbstractMap.SimpleEntry<String[], Double>> possibleMNN) {
        ArrayList<AbstractMap.SimpleEntry<String[], Double>> mutualNearestNeighbours = new ArrayList<>();
        for(int i = 0; i < possibleMNN.size(); i++) {
            String[] firstPair = possibleMNN.get(i).getKey();
            for(int j = 0; j < possibleMNN.size(); j++) {
                String[] secondPair = possibleMNN.get(j).getKey();
                if(i != j && arePairsMutualNeighbours(firstPair, secondPair)) {
                    insertElementInDecreasingSimilarityOrder(mutualNearestNeighbours, possibleMNN.get(i));
                }
            }
        }
        return mutualNearestNeighbours;
    }

    private boolean arePairsMutualNeighbours(String[] firstPair, String[] secondPair) {
        return firstPair[0].equals(secondPair[1]) && firstPair[1].equals(secondPair[0]);
    }

    private void insertNearestNeighboursFromRanking(Hashtable<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> rankings,
                                                    ArrayList<AbstractMap.SimpleEntry<String[], Double>> possibleMNN) {
        for(Map.Entry<String, ArrayList<AbstractMap.SimpleEntry<String, Double>>> ranking: rankings.entrySet()) {
            String rankingBrick = ranking.getKey();
            if(!ranking.getValue().isEmpty()) {
                double highestSimilarityValue = ranking.getValue().get(0).getValue();
                if(!Globals.isDoubleExtremelyNearZero(highestSimilarityValue)) {
                    for (String nearestNeighbourName : getBrickNamesWithTheHighestSimilarityValue(ranking.getValue())) {
                        possibleMNN.add(getNearestNeighbourPair(rankingBrick, nearestNeighbourName, highestSimilarityValue));
                    }
                }
            }
        }
    }

    private AbstractMap.SimpleEntry<String[], Double> getNearestNeighbourPair(String brickName,
                                                                              String nearestNeighbourName,
                                                                              double similarity) {
        String[] match = new String[2];
        match[0] = brickName;
        match[1] = nearestNeighbourName;
        return new AbstractMap.SimpleEntry<>(match, similarity);
    }
}
