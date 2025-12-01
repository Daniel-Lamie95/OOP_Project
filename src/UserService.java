import java.util.ArrayList;
public class UserService {
    private ArrayList<User> users = new ArrayList<>();

   /*public boolean signUp(User newUser) {
        if (newUser.getName() == null || newUser.getName().isEmpty() ||
                newUser.getEmail() == null || newUser.getEmail().isEmpty() ||
                newUser.getPassword() == null || newUser.getPassword().length() < 6) {
            return false;
        }
        for (User u : users) {
            if (u.getEmail().equals(newUser.getEmail())) {
                return false;
            }
        }
        users.add(newUser);
        return true;
    }

    public User login(String email, String password) {
        for (User u : users) {
            if (u.checkInfo(email, password)) {
                return u;
            }
        }
        return null;
    }

    public ArrayList<User> getUsers() {
        return users;
    }
*/
}
