import java.util.*;
import java.util.UUID;
public class Patient extends User implements Searchable{
    private String patientStage;
    private ArrayList<Relative> relatives;
    private ArrayList<Memory> memories;
    private ArrayList<Reminder> reminders;

    public Patient() {
        super();
    }

    public Patient(String name, String email, String password, String patientStage) {
        super(name, email, password);
        this.patientStage = patientStage;
        this.relatives = new ArrayList<>();
        this.memories = new ArrayList<>();
        this.reminders = new ArrayList<>();
    }

    public Patient(String name, UUID id, String email, String password, String patientStage) {
        super(name, id, email, password);
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

    @Override
    public Memory findMemory(UUID id) {
        for (Memory m : memories)
            if (m.getId().equals(id))
                return m;
        return null;
    }

    @Override
    public Reminder findReminder(UUID id) {
        for (Reminder r : reminders)
            if (r.getId().equals(id))
                return r;
        return null;
    }

    @Override
    public Relative findRelative(UUID id) {
        for (Relative rel : relatives)
            if (rel.getId().equals(id))
                return rel;
        return null;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientStage='" + patientStage + '\'' +
                ", relatives=" + relatives +
                ", memories=" + memories +
                '}';
    }
}