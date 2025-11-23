import java.util.*;
import java.util.UUID;
public class Patient extends User {
    private String patientStage;
    private ArrayList<Relative> relatives;
    private ArrayList<Memory> memories;
    private ArrayList<Reminder> reminders;

    public Patient(String name, UUID Id, String email, String password, String patientStage) {
        super(name, Id, email, password);
        this.patientStage = patientStage;
        this.relatives = new ArrayList<>();
        this.memories = new ArrayList<>();
        this.reminders = new ArrayList<>();
    }

    public String getPatientStage() {
        return patientStage;
    }

    public void setPatientStage(String patientStage) {
        this.patientStage = patientStage;
    }

    public ArrayList<Relative> getRelatives() {
        return relatives;
    }

    public ArrayList<Memory> getMemories() {
        return memories;
    }

    public ArrayList<Reminder> getReminders() {
        return reminders;
    }
}
   /* public Memory searchMemory (UUID Id){
        for (int i=0;i<memories.size();i++) {
            Memory m =memories.get(i);
            if (m.getId().equals(Id)) {
                return m;
            }
        }
        return null;
    }
    public Reminder findReminder (String Id){
        ArrayList<Reminder> reminders;
        for(Reminder r:reminders){

        }

        return null;
    }
    public Relative findRelative (UUID Id){
        for (int i=0;i<relatives.size();i++) {
            Relative rel =relatives.get(i);
            if (rel.getId().equals(Id)) {
                return rel;
            }
        }
        return null;
    }
    @Override
    public String toString() {
        return "Patient { " +
                "id=" + getId() +
                ", name=" + getName() +
                ", email=" + getEmail() +
                ", patientStage=" + patientStage +
                ", relatives=" + relatives +
                ", memories=" + memories +
                ", reminders=" + reminders +
                " }";
    }

}
*/






