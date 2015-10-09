package be.hepl.benbear.trafficdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

@DBTable("users")
public class User {

    @PrimaryKey
    private final int userId;
    private final String username;
    private final String password;

    public User(int userId, String username, String password) {
        this.userId =  userId;
        this.username = username;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
