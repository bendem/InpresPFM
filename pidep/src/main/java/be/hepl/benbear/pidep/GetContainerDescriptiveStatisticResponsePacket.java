package be.hepl.benbear.pidep;

public class GetContainerDescriptiveStatisticResponsePacket extends Packet {

    private final double average;
    private final double[] modes;
    private final double median;
    private final double std;

    public GetContainerDescriptiveStatisticResponsePacket(double average, double[] modes, double median, double std) {
        super(Id.GetContainerDescriptiveStatisticResponse);
        this.average = average;
        this.modes = modes;
        this.median = median;
        this.std = std;
    }

    public double getAverage() {
        return average;
    }

    public double[] getModes() {
        return modes;
    }

    public double getMedian() {
        return median;
    }

    public double getStd() {
        return std;
    }

}
