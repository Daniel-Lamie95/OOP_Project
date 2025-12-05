
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Relative extends Person implements Serializable {
    private String relationship;
    private String description;
    private String phoneNumber;
    private String email;
    private String gender;
    private String address;
    private String photoPath;
    private List<Media> mediaList = new ArrayList<>();
    private static int num_of_relatives = 0;

    private LocalDate birthday;

    public Relative() {
        super();
    }

    public Relative(String name, String relationship, String phoneNumber, String gender) {
        super(name);
        this.relationship = relationship;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        num_of_relatives++;
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

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public List<Media> getMediaList() {
        return mediaList;
    }

    public void addMedia(Media m) {
        if (m != null) {
            mediaList.add(m);
        }
    }

    public boolean removeMedia(Media m) {
        return mediaList.remove(m);
    }

    public void clearMedia() {
        mediaList.clear();
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        if(photoPath == null || photoPath.trim().isEmpty()) {
            throw new IllegalArgumentException("photoPath must not be null or empty");
        }
        this.photoPath = photoPath;
    }
    
    public int getNum_of_relatives() {
        return num_of_relatives;
    }

    @Override
    public String toString() {
        return "Relative{" + super.toString()+
                "relationship='" + relationship + '\'' +
                ", description='" + description + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", mediaList=" + mediaList +
                ", birthday=" + birthday +
                '}';
    }
}
