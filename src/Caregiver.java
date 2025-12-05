import java.util.UUID;
public class Caregiver extends User implements Searchable{
    private Patient patient;

    public Caregiver(){
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

   public void deletPatient(Patient p){
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
    }

    public boolean addMemory(Memory m) {
        if (this.patient == null)
            return false;
        return this.patient.addMemory(m);
    }


    public boolean editMemory(String id,String name,String description ) {
        Memory m = findMemory(id);
        if (m == null) return false;
        m.setName(name);
        m.setDescription(description);
        return true;
    }

    public boolean addReminder(Reminder r) {
        if (patient == null) return false;
        return patient.addReminder(r);
    }


   public boolean deleteReminder(String id) {
        if (patient == null) return false;
        return patient.deleteReminder(id);
    }

   public boolean editReminder(String id, String name, String description) {
        Reminder r = findReminder(id);
        if (r == null) return false;
        r.setName(name);
        r.setDescription(description);
        return true;
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
        return "Caregiver{" +
                "patient=" + patient +
                '}';
    }


}

