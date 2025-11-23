import java.util.UUID;
public class Person {
    private UUID id;
    private String name;
    public Person(String name, UUID id) {
        this.id = UUID.randomUUID();
        this.name = name;
    }
    public Person(String name) {
        this.id = UUID.randomUUID();
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