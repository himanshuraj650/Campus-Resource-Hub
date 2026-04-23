package campus;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String username;
    private final String password;
    private final String fullName;
    private final String campusId;

    private int karma;
    private String campusName = "Cgc University";

    public User(String username, String password, String fullName, String campusId) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.campusId = campusId;
        this.karma = 10;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getCampusId() { return campusId; }
    public String getCampusName() { return campusName; }
    public void setCampusName(String nm) { this.campusName = nm; }
    public int getKarma() { return karma; }
    public void setKarma(int karma) { this.karma = karma; }

    @Override
    public String toString() { return fullName + " (@" + username + ")"; }
}
