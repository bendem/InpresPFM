package be.hepl.benbear.pidep;

public class GetContainerDescriptiveStatisticResponsePacket extends Packet {

    private final float average;
    private final float[] modes;
    private final float median;
    private final float std;

    public GetContainerDescriptiveStatisticResponsePacket(float average, float[] modes, float median, float std) {
        super(Id.GetContainerDescriptiveStatisticResponse);
        this.average = average;
        this.modes = modes;
        this.median = median;
        this.std = std;
    }

    public float getAverage() {
        return average;
    }

    public float[] getModes() {
        return modes;
    }

    public float getMedian() {
        return median;
    }

    public float getStd() {
        return std;
    }

}
