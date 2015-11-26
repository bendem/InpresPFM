package be.hepl.benbear.pidep;

public class GetStatInferAnovaTestResponsePacket extends Packet {
    private final boolean significant;
    private final double pValue;

    public GetStatInferAnovaTestResponsePacket(boolean significant, double pValue) {
        super(Id.GetStatInferAnovaTestResponse);
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
