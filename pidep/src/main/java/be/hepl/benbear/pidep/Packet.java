package be.hepl.benbear.pidep;

public abstract class Packet {

    public enum Id {
        Error,
        Login,
        LoginResponse,
        GetContainerDescriptiveStatistic,
        GetContainerDescriptiveStatisticResponse,
        GetContainerPerDestinationGraph,
        GetContainerPerDestinationGraphResponse,
        GetContainerPerDestinationPerQuarterGraph,
        GetContainerPerDestinationPerQuarterGraphResponse,
        GetStatInferConformityTestPacket,
        GetStatInferConformityTestResponsePacket,
        GetStatInferHomogeneityTestPacket,
        GetStatInferHomogeneityTestResponsePacket,
        GetStatInferAnovaTestPacket,
        GetStatInferAnovaTestResponsePacket
    }

    private final Id id;

    protected Packet(Id id) {
        this.id = id;
    }

    public Id getId() {
        return id;
    }

}
