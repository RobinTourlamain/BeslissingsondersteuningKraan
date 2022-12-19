public class Crane {
    public int id;
    public float x;
    public float y;
    public int xMin;
    public int xMax;
    public int yMin;
    public int yMax;
    public final int speedX;
    public final int speedY;


    public Crane(int id, float x, float y, int xMin, int xMax, int yMin, int yMax, int speedX, int speedY) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.speedX = speedX;
        this.speedY = speedY;
    }

}

