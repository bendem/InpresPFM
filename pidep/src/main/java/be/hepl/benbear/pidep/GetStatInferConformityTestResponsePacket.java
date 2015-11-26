package be.hepl.benbear.pidep;

public class GetStatInferConformityTestResponsePacket extends Packet {

    private final boolean significant;
    private final double pValue;

    public GetStatInferConformityTestResponsePacket(boolean significant, double pValue) {
        super(Id.GetStatInferConformityTestResponsePacket);
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
