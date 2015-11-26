package be.hepl.benbear.pidep;

public class GetStatInferHomogeneityTestResponsePacket extends Packet {
    private final boolean significant;
    private final double pValue;

    public GetStatInferHomogeneityTestResponsePacket(boolean significant, double pValue) {
        super(Id.GetStatInferHomogeneityTestResponsePacket);
        this.significant = significant;
        this.pValue = pValue;
    }

    public boolean isSignificant() {
        return significant;
    }

    public double getpValue() {
        return pValue;
    }
}
