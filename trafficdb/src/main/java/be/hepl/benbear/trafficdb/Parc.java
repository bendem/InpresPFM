package be.hepl.benbear.trafficdb;

public class Parc {

    private final int x;
    private final int y;
    private final String containerId;

    public Parc(int x, int y, String containerId) {
        this.x = x;
        this.y = y;
        this.containerId = containerId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getContainerId() {
        return containerId;
    }

}
