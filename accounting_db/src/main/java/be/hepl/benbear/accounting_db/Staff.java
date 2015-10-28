package be.hepl.benbear.accounting_db;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

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

    public Staff(int staffId, String lastName, String firstName, String login, String password, String internalEmail, String internalPhoneNumber) {
        this.staffId = staffId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.login = login;
        this.password = password;
        this.internalEmail = internalEmail;
        this.internalPhoneNumber = internalPhoneNumber;
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

}
