import java.util.ArrayList;
public abstract class UserService{
    protected ArrayList<User> users = new ArrayList<>();

    public UserService(ArrayList<User> users) {
        this.users = users;
    }
    public abstract void  signUp(User user);

   public boolean addUser(User newUser) {
       if (newUser == null){
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

}
