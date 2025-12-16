import java.io.Serializable;
import java.util.UUID;
import java.time.LocalDateTime;


public class Reminder extends PatientInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String description;
    private LocalDateTime dateOfReminder;
    private Boolean isDone = false;

    public Reminder(UUID id , String name, String description, LocalDateTime dateOfReminder) {
        super(id , name);
        this.description = description;
        this.dateOfReminder = dateOfReminder;
    }

    @Override
    public String getInfoSummary() {
        return  getName() + "(for " + getDescription()+ ") on " + dateOfReminder.toString();
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDateTime getDate() {
        return dateOfReminder;
    }
    public void setDate(LocalDateTime date) {
        this.dateOfReminder = date;
    }
    public Boolean getDone() {
        return isDone;
    }
    public void setDone(Boolean done) {
        isDone = done;
    }
    public void checkStatus() {
        if (isDone) {
            System.out.println("This reminder is completed.");
        } else {
            System.out.println("This reminder isn't done yet.");
        }
    }
    public void notifyIfDue() {

        if (dateOfReminder == null) return;
        LocalDateTime now = LocalDateTime.now();

        if(!isDone && now.isAfter(dateOfReminder)) {
            System.out.println("Reminder:" + description + "(for" + getName() +")");
        }
    }

}