public class Media {

    private int id;
    private String filePath;    // path to photo or video file
    private String mediaType;   // "image", "video"

    public Media(int id, String filePath, String mediaType) {
        this.id = id;
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
}

