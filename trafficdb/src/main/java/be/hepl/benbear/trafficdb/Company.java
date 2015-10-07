package be.hepl.benbear.trafficdb;

public class Company {

    private final int companyId;
    private final String name;
    private final String mail;
    private final String phone;
    private final String address;

    public Company(int companyId, String name, String mail, String phone, String address) {
        this.companyId = companyId;
        this.name = name;
        this.mail = mail;
        this.phone = phone;
        this.address = address;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

}
