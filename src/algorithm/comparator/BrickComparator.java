package algorithm.comparator;

import weka.core.Instance;

public interface BrickComparator {
    public double compare(Instance i1, Instance i2);
}
