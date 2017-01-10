package algorithm.comparator;

import common.WallComparisonResult;
import data.DistanceMatrix;
import data.Wall;

public interface WallComparator {
    public WallComparisonResult compare(Wall w1, Wall w2, DistanceMatrix bricksDM);
}
