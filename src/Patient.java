import java.util.*;
import java.util.UUID;
public class Patient extends User {
    private String patientStage;
    private ArrayList<Relative> relatives;
    private ArrayList<Memory> memories;
    //private ArrayList<Reminder> reminders;

    public Patient(String name, String email, String password, String patientStage) {
        super(name, email, password);
        this.patientStage = patientStage;
        this.relatives = new ArrayList<>();
        this.memories = new ArrayList<>();
     //   this.reminders = new ArrayList<>();
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

  //  public ArrayList<Reminder> getReminders() {
   //     return reminders;
    //}
}
//
   /* public Memory findMemory (UUID Id){
        for (Memory m: memories) {
            if (m.getId().equals(Id)) {
                return m;
            }
        return null;
    }

    public Reminder findReminder (UUID Id){
        for (Reminder r: reminders) {
            if (r.getId().equals(Id)) {
                return r;
            }
        return null;
    }
    public Relative findRelative (UUID Id){
          for (Relative re: relatives) {
            if (re.getId().equals(Id)) {
                return re;
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






