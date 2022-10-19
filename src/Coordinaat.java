public class Coordinaat {
    private Container container;
    private int x;
    private int y;
    private int z;

    Coordinaat(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }



    int getX(){
        return x;
    }
    int getY(){
        return y;
    }
    int getZ(){
        return z;
    }

    Container getContainer() {
        return container;
    }

    void setContainer(Container container) {
        this.container = container;
    }
}
