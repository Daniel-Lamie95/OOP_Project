import java.io.Serializable;
import java.util.*;
import java.util.UUID;

public class Patient extends User implements Searchable, Serializable {
    private static final long serialVersionUID = 1L;
    private String patientStage;
    private ArrayList<Relative> relatives;
    private ArrayList<Memory> memories;
    private ArrayList<Reminder> reminders;



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

    public void addMemories(Memory m) {
        memories.add(m);
    }

    public void addRelatives(Relative re) {
        relatives.add(re);
    }

    public void addReminder(Reminder r) {
        reminders.add(r);
    }

    public void deleteReminder(String name) {
        reminders.removeIf(r -> r.getName().equals(name));
    }

    public void deleteMemory(String name) {
        memories.removeIf(m -> m.getName().equals(name));
    }

    public void deleteRelative(String name) {
        relatives.removeIf(re -> re.getName().equals(name));
    }

    @Override
    public Memory findMemory(String memoryName) {
        for (Memory m : getMemories())
            if (m.getName().equals(memoryName))
                return m;
        return null;
    }

    @Override
    public Reminder findReminder(String name) {
        for (Reminder r : getReminders())
            if (r.getName().equals(name))
                return r;
        return null;
    }

    @Override
    public Relative findRelative(String name) {
        for (Relative rel : getRelatives())
            if (rel.getName().equals(name))
                return rel;
        return null;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientStage='" + patientStage + '\'' +
                ", relatives=" + relatives +
                ", memories=" + memories +
                ", reminders=" + reminders +
                '}';
    }
}