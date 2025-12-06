import java.io.Serializable;
import java.util.UUID;
public abstract class Person implements Serializable {
    private UUID id;
    private String name;

    public Person() {
        this.id=UUID.randomUUID();
    }

    public Person(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public Person(String name, UUID id) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id.toString();
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}