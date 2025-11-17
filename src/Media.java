public class Media {
    private static int nextId = 1;
    private final int id;
    private String filePath;    // path to photo or video file
    private String mediaType;  // "image", "video"
    private String description =""; // optional description

    public Media(String filePath, String mediaType) {
        this.id = nextId++;
        this.filePath = filePath;
        this.mediaType = mediaType;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public static int getNextId() {
        return nextId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMediaType() {
        return mediaType;
    }

    // Setters
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

