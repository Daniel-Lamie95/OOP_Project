import java.io.Serializable;
import java.util.UUID;

public abstract class PatientInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;

    public PatientInfo(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract String getInfoSummary();

    @Override
    public String toString() {
        return "PatientInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
