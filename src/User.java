import java.util.UUID;
public class User extends Person {
    private String email;
    private String password;

    public User(String name, String email, String password) {
        super(name);
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

   /* public boolean login(String email, String password)
    {
        if (this.email.equals(email) && this.password.equals(password)) {
            System.out.println("Login successful! Welcome " + getName());
            return true;
        }
        else
        {
            System.out.println("Invalid email or password!");
            return false;
        }
    }
    public static User signUp(String email, String password, String name)
    {
        if (email == null || email.isEmpty() || password == null || password.isEmpty())
        {
            System.out.println("Email and password cannot be empty!");
            return null;
        }
        if (password.length() < 6)
        {
            System.out.println("Password must be at least 6 characters long!");
            return null;
        }
        User newUser = new User(name,id,email, password);
        System.out.println("Sign up successful! Account created for " + name);
        return newUser;
    }

}
//
*
/
    */