package be.hepl.benbear.accounting_db;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

import java.sql.Date;

@DBTable("staff")
public class Staff {

    @PrimaryKey
    private final int staffId;
    private final String lastName;
    private final String firstName;
    private final String login;
    private final String password;
    private final String internalEmail;
    private final String internalPhoneNumber;
    private final String allocation;
    private final String duty;
    private final float payScale;
    private final Date hireDate;
    private final String accountNumber;

    public Staff(int staffId, String lastName, String firstName, String login, String password, String internalEmail, String internalPhoneNumber, String allocation, String duty, float payScale, Date hireDate, String accountNumber) {
        this.staffId = staffId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.login = login;
        this.password = password;
        this.internalEmail = internalEmail;
        this.internalPhoneNumber = internalPhoneNumber;
        this.allocation = allocation;
        this.duty = duty;
        this.payScale = payScale;
        this.hireDate = hireDate;
        this.accountNumber = accountNumber;
    }

    public int getStaffId() {
        return staffId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getInternalEmail() {
        return internalEmail;
    }

    public String getInternal_phoneNumber() {
        return internalPhoneNumber;
    }

    public String getInternalPhoneNumber() {
        return internalPhoneNumber;
    }

    public String getAllocation() {
        return allocation;
    }

    public String getDuty() {
        return duty;
    }

    public float getPayScale() {
        return payScale;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

}
