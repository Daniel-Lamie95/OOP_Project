import java.util.UUID;
public class Caregiver extends User implements Searchable{
    private Patient patient;

    public Caregiver(){
        super();
    }

    public Caregiver(String name, String email, String password) {
        super(name, email, password);
        this.patient = null;
    }

    public Caregiver(String name,UUID id, String email, String password) {
        super(name, id, email, password);
        this.patient = null;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public boolean addPatient(Patient p){
        if(this.patient != null){
            System.out.println("you already have patient");
            return false;}
        else{ this.patient=p;
            System.out.println("patient added successfully");
            return true;}
    }

    public void deletePatient(Patient p){
        this.patient=null;
    }

    public boolean editPatient(String name, String stage) {
        if (this.patient == null)
            return false;
        this.patient.setName(name);
        return true;
    }

    public boolean deleteMemory(String  id) {
        if (this.patient == null)
            return false;
        else {
            this.patient.deleteMemory(id);
            return true;
        }

    }

    public boolean addMemory(Memory m) {
        if (this.patient == null)
            return false;
        else{
            this.patient.addMemories(m);
            return true;
        }

    }


    public void editMemory(String memoryName,String newMemoryName,String description ) throws Exception {
        if (this.patient == null)
            throw new Exception("patient is null");
        else {
            Memory m = findMemory(memoryName);
            if (m == null)
                throw new Exception("memories not found");
            m.setName(newMemoryName);
            m.setDescription(description);
        }
    }

    public boolean addReminder(Reminder r) {
        if (patient == null) return false;
        else {
            patient.addReminder(r);
            return true;
        }
    }


    public boolean deleteReminder(String id) {
        if (patient == null) return false;
        else {
            patient.deleteReminder(id);
            return true;
        }
    }

    public boolean editReminder(String oldName, String newName, String description) {
        Reminder r = findReminder(oldName);
        if (r == null) return false;
        r.setName(newName);
        r.setDescription(description);
        return true;
    }

    @Override
    public Memory findMemory(String memoryName) {
        for (Memory m :  patient.getMemories())
            if (m.getName().equals(memoryName))
                return m;
        return null;
    }

    @Override
    public Reminder findReminder(String name) {

        for (Reminder r : patient.getReminders())
            if (r.getName().equals(name))
                return r;
        return null;
    }

    @Override
    public Relative findRelative(String name) {
        for (Relative rel : patient.getRelatives())
            if (rel.getName().equals(name))
                return rel;
        return null;
    }

    @Override
    public String toString() {
        return "Caregiver{" +
                "patient=" + patient +
                '}';
    }


}

