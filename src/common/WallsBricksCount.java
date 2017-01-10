package common;

public class WallsBricksCount {
    private int w1BricksCount;
    private int w2BricksCount;

    public WallsBricksCount() {
        this(0, 0);
    }

    public WallsBricksCount(int w1BricksCount, int w2BricksCount) {
        this.w1BricksCount = w1BricksCount;
        this.w2BricksCount = w2BricksCount;
    }

    public int getW1BricksCount() {
        return w1BricksCount;
    }

    public void setW1BricksCount(int w1BricksCount) {
        this.w1BricksCount = w1BricksCount;
    }

    public int getW2BricksCount() {
        return w2BricksCount;
    }

    public void setW2BricksCount(int w2BricksCount) {
        this.w2BricksCount = w2BricksCount;
    }
}
