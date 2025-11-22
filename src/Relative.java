import java.util.ArrayList;
import java.util.List;
public class Relative {
    private int rel_id;
    private String name;
    private String relationship;
    private String description;
    private String phoneNumber;
    private String email;
    private String gender;
    private String address;
    private String birthday;
    private String photoPath;
    private List<Media> mediaList = new ArrayList<>();
    private static int num_of_relatives = 0;

    public Relative(int id, String name, String relationship, String phoneNumber, String gender) {
        this.rel_id = id;
        this.name = name;
        this.relationship = relationship;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        num_of_relatives++;
    }

    public int getRel_id() {
        return rel_id;
    }

    public void setRel_id(int rel_id) {
        this.rel_id = rel_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public List<Media> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<Media> mediaList) {
        this.mediaList = mediaList;
    }
    
    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
    
    public int getNum_of_relatives() {
        return num_of_relatives;
    }

    @Override
    public String toString() {
        return "Relatives [rel_id=" + rel_id + ", name=" + name + ", relationship=" + relationship + 
               ", description=" + description + ", phoneNumber=" + phoneNumber + ", email=" + email +
                ", gender=" + gender                                    
                + ", address=" + address + ", birthday=" + birthday +
             ", mediaList=" + mediaList + "]";
    }         

}            
