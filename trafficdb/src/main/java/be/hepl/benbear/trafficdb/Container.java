package be.hepl.benbear.trafficdb;

public class Container {

    private final String containerId;
    private final int companyId;
    private final String contentType;
    private final String dangers;

    public Container(String containerId, int companyId, String contentType, String dangers) {
        this.containerId = containerId;
        this.companyId = companyId;
        this.contentType = contentType;
        this.dangers = dangers;
    }

    public String getContainerId() {
        return containerId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getContentType() {
        return contentType;
    }

    public String getDangers() {
        return dangers;
    }

}
