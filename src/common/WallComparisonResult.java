package common;

public class WallComparisonResult {
    private double comparisionValue;
    private WallsBricksCount numberOfUsedBricks;

    public WallComparisonResult(double comparisionValue, int numberOfUsedBricksFromW1, int numberOfUsedBricksFromW2) {
        this.comparisionValue = comparisionValue;
        this.numberOfUsedBricks = new WallsBricksCount(numberOfUsedBricksFromW1, numberOfUsedBricksFromW2);
    }

    public double getComparisionValue() {
        return comparisionValue;
    }

    public void setComparisionValue(double comparisionValue) {
        this.comparisionValue = comparisionValue;
    }

    public int getNumberOfUsedBricksFromW1() {
        return this.numberOfUsedBricks.getW1BricksCount();
    }

    public void setNumberOfUsedBricksFromW1(int numberOfUsedBricksFromW1) {
        this.numberOfUsedBricks.setW1BricksCount(numberOfUsedBricksFromW1);
    }

    public int getNumberOfUsedBricksFromW2() {
        return this.numberOfUsedBricks.getW2BricksCount();
    }

    public void setNumberOfUsedBricksFromW2(int numberOfUsedBricksFromW2) {
        this.numberOfUsedBricks.setW2BricksCount(numberOfUsedBricksFromW2);
    }
}
