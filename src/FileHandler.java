import java.io.*;
import java.util.ArrayList;

public class FileHandler {
    private static final String ACCOUNTS_FILE = "accounts.txt";

    public void saveAccounts(ArrayList<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ACCOUNTS_FILE))) {
            oos.writeObject(users);
            System.out.println("Accounts saved to " + ACCOUNTS_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<User> loadAccounts() {
        File f = new File(ACCOUNTS_FILE);
        if (!f.exists()) {
            System.out.println("No accounts file found, returning empty list.");
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (ArrayList<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public void addAccount(User user) {
        ArrayList<User> users = loadAccounts();
        users.add(user);
        saveAccounts(users);
    }


    public boolean updateAccountByEmail(String email, User updatedUser){
        if (email == null) return false;
        ArrayList<User> users = loadAccounts();
        for(int i = 0; i<users.size(); i++) {
            User u = users.get(i);
            if (u != null && u.getEmail() != null && u.getEmail().equals(email)) {
                users.set(i, updatedUser);
                saveAccounts(users);
                return true;
            }
        }
        return false;
    }

    public boolean deleteAccountByEmail(String email){
        if (email == null) return false;
        ArrayList<User> users = loadAccounts();
        for(int i = 0; i<users.size(); i++) {
            User u = users.get(i);
            if (u != null && u.getEmail() != null && u.getEmail().equals(email)) {
                users.remove(i);
                saveAccounts(users);
                return true;
            }
        }
        return false;
    }

}
