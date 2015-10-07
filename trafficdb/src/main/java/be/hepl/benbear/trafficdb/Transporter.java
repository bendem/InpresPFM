package be.hepl.benbear.trafficdb;

public class Transporter {

    private final String transporterId;
    private final int companyId;
    private final String info;

    public Transporter(String transporterId, int companyId, String info) {
        this.transporterId = transporterId;
        this.companyId = companyId;
        this.info = info;
    }

    public String getTransporterId() {
        return transporterId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getInfo() {
        return info;
    }

}
