package be.hepl.benbear.pidep;

public abstract class Packet {

    public enum Id {
        Login,
        LoginResponse,
        GetContainerDescriptiveStatistic,
        GetContainerDescriptiveStatisticResponse,
        GetContainerPerDestinationGraph,
        GetContainerPerDestinationGraphResponse,
        GetContainerPerDestinationPerQuarterGraph,
        GetContainerPerDestinationPerQuarterGraphResponse,
    }

    private final Id id;

    protected Packet(Id id) {
        this.id = id;
    }

    public Id getId() {
        return id;
    }

}
