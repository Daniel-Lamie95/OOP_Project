public class Caregiver extend user  {
    private Patient patient;
    public Caregiver(String name,String id,String email,String password){
        super ( name, id,email, password);
        this.Patient=patient;
    }

    public Patient getPatient() {
        return Patient;
    }

    public void setPatient(Patient patient) {
        this.Patient = patient;
    }
    public boolean addPatient(Patient p){
        if(this.Patient != null){
            System.out.println("you already have patient");
            return false;}
        else{ this.Patient=p;
            System.out.println("patient added succesfuly!");
            return true;}

    }
    public boolean deletPatient(Patient p){
        this.Patient=null;
    }
    public boolean editPatient(String name, String stage) {
        if (thi.patient == null)
            return false;

        this.patient.setName(name);
        this.patient.setStage(stage);
        return true;
    }

    public boolean deleteMemory(String  id) {
        if (this.patient == null)
            return false;
        return this.patient.deleteMemory(id);
    }
    public boolean addMemory(Memory m) {
        if (this.patient == null)
            return false;
        return this.patient.addMemory(m);
    }
    public boolean findMemory(String id) {
        if (this.patient == null)
            return false;
        return this.patient.findMemory(id);
    }
    public boolean editMemory(String id,String name,string description ) {
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

    public Reminder findReminder(String id) {
        if (patient == null) return null;
        return patient.findReminder(id);
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
    public String toString() {
        return "Caregiver{" +
                "patient=" + patient +
                '}';
    }
}
