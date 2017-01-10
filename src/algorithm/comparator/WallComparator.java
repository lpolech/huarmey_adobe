package algorithm.comparator;

import data.DistanceMatrix;
import data.Wall;

public interface WallComparator {
    public double compare(Wall w1, Wall w2, DistanceMatrix bricksDM);
}
