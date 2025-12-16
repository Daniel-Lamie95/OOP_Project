/*import java.io.Serializable;
import java.util.ArrayList;
public class PatientService extends UserService implements Serializable {

    public PatientService(ArrayList<User> users) {
        super(users);
    }

    @Override
    public void signUp(User user) {
        Patient patient = (Patient) user;
        if (user.getName() == null || user.getName().isEmpty() || user.getEmail() == null || user.getEmail().isEmpty()
                || !user.getEmail().contains("@") || user.getPassword() == null || user.getPassword().length() < 6
                || patient.getPatientStage() == null || patient.getPatientStage().isEmpty()) {
            System.out.println("invalid");
            return;
        }
        boolean added = addUser(user);
        if (!added) {
            System.out.println("email already exists");
        }
    }
}

 */
