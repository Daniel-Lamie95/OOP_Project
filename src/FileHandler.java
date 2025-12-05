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
}
