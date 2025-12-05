import java.io.Serializable;
import java.util.UUID;

public abstract class User extends Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;

    public User() {
    }

    public User(String name, String email, String password) {
        super(name);
        this.email = email;
        this.password = password;
    }

    public User(String name, UUID id, String email, String password) {
        super(name, id);
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
