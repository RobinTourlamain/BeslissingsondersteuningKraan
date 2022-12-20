public class OutputRecord {
    public int craneid;
    public int cid;
    public double ptime;
    public double etime;
    public double pposx;
    public double pposy;
    public double eposx;
    public double eposy;

    public OutputRecord(){}

    public OutputRecord(int craneid, int cid, double ptime, double etime, double pposx, double pposy, double eposx, double eposy) {
        this.craneid = craneid;
        this.cid = cid;
        this.ptime = ptime;
        this.etime = etime;
        this.pposx = pposx;
        this.pposy = pposy;
        this.eposx = eposx;
        this.eposy = eposy;
    }
}
