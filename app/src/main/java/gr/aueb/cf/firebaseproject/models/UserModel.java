package gr.aueb.cf.firebaseproject.models;

public class UserModel {
    private String id;
    private String email;
    private String username;
    private String dateStamp;
    private String imageUrl;

    public UserModel() {}

    public UserModel(String id, String email, String username, String dateStamp, String imageUrl) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.dateStamp = dateStamp;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(String dateStamp) {
        this.dateStamp = dateStamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
