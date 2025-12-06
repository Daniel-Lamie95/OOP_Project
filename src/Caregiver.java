import java.util.UUID;
import java.io.Serializable;
public class Caregiver extends User implements Searchable, Serializable{
    private static final long serialVersionUID = 1L;
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

    public void addPatient(Patient p) throws Exception {
        if(this.patient != null){
            throw new Exception("caregiver already has a patient");}
        else {
            this.patient = p;
        }
    }

    public void deletePatient(String name) throws Exception{
        if (patient == null)
            throw new Exception("no patient was found");
        else {
            patient=null;
        }
    }

    public void editPatient(String name, String patientStage)  throws Exception{
        if (this.patient == null)
            throw new Exception("no patient was found");
        else {
            this.patient.setName(name);
            this.patient.setPatientStage(patientStage);
        }
    }

    public void addMemory(Memory m) throws Exception{
        if (this.patient == null)
            throw new Exception("no patient was found");
        else{
            this.patient.addMemories(m);
        }
    }

    public void deleteMemory(String name) throws Exception {
        if (this.patient == null)
            throw new Exception("no patient was found");
        else {
            Memory m = findMemory(name);
            if (m == null)
                throw new Exception("no memory was found");
            else {
                this.patient.deleteMemory(name);
            }
        }
    }

    public void editMemory(String memoryName,String newMemoryName,String description ) throws Exception {
        if (this.patient == null)
            throw new Exception("no patient was found");
        else {
            Memory m = findMemory(memoryName);
            if (m == null)
                throw new Exception("no memory was found");
            else {
                m.setName(newMemoryName);
                m.setDescription(description);
            }
        }
    }

    public void addReminder(Reminder r) throws Exception {
        if (patient == null)
            throw new Exception("no patient was found");
        else {
            patient.addReminder(r);
        }
    }

    public void deleteReminder(String name) throws Exception{
        if (patient == null)
            throw new Exception("no patient was found");
        else {
            Reminder r = findReminder(name);
            if (r == null)
                throw new Exception("no reminder was found");
            else {
                patient.deleteReminder(name);
            }
        }
    }

    public void editReminder(String oldName, String newName, String description) throws Exception {
        if (this.patient == null)
            throw new Exception("no patient was found");
        else{
            Reminder r = findReminder(oldName);
            if(r == null)
                throw new Exception("no reminder was found");
            else {
                r.setName(newName);
                r.setDescription(description);
            }
        }
    }

    public void addRelative(Relative re) throws Exception{
            if (patient == null)
                throw new Exception("no patient was found");
            else {
                patient.addRelatives(re);
            }
    }

    public void deleteRelative(String name) throws Exception{
        if (patient == null)
            throw new Exception("no patient was found");
        else {
            Relative re = findRelative(name);
            if (re == null)
                throw new Exception("no relative was found");
            else {
                patient.deleteRelative(name);
            }
        }
    }

    public void editRelative( String name,String relationship, String description, String phoneNumber,
                              String email, String gender, String address, String photoPath) throws Exception{
        if (this.patient == null)
            throw new Exception("no patient was found");
        else{
            Relative re=findRelative(name);
            if (re == null)
                throw new Exception("no relative was found");
            else{
                re.setRelationship(relationship);
                re.setDescription(description);
                re.setPhoneNumber(phoneNumber);
                re.setEmail(email);
                re.setGender(gender);
                re.setAddress(address);
                re.setPhotoPath(photoPath);
            }
        }
    }

    @Override
    public Memory findMemory(String memoryName) {
        if (patient == null || patient.getMemories() == null)
            return null;
        for (Memory m :  patient.getMemories())
            if (m.getName().equals(memoryName))
                return m;
        return null;
    }

    @Override
    public Reminder findReminder(String name) {
        if (patient == null || patient.getReminders() == null)
            return null;
        for (Reminder r : patient.getReminders())
            if (r.getName().equals(name))
                return r;
        return null;
    }

    @Override
    public Relative findRelative(String name) {
        if (patient == null || patient.getRelatives() == null)
            return null;
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

