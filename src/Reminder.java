import java.time.LocalDateTime;
public class Reminder extends PatientInfo{

    private String description;
    private LocalDateTime dateOfReminder;
    private Boolean isDone = false;

    public Reminder(String Id , String Name, String description, LocalDateTime dateOfReminder) {
        super(Id , Name);
        this.description = description;
        this.dateOfReminder = dateOfReminder;
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
        LocalDateTime now = LocalDateTime.now();

        if (!isDone && now.isAfter(dateOfReminder)) {
            System.out.println("Reminder:" + description + "(for" + getName() +")");
        }
    }
}
