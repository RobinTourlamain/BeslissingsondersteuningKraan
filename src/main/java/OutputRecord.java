public class OutputRecord {
    public int craneId;
    public int containerId;
    public double pickupTime;
    public double endTime;
    public double pickupPosX;
    public double pickupPosY;
    public double endPosX;
    public double endPosY;
    public Action action;

    public OutputRecord(int craneId, int containerId, double pickupTime, double endTime, double pickupPosX, double pickupPosY, double endPosX, double endPosY, Action action) {
        this.craneId = craneId;
        this.containerId = containerId;
        this.pickupTime = pickupTime;
        this.endTime = endTime;
        this.pickupPosX = pickupPosX;
        this.pickupPosY = pickupPosY;
        this.endPosX = endPosX;
        this.endPosY = endPosY;
        this.action = action;
    }
}
