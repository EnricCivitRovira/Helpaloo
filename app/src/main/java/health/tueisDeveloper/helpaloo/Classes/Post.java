package health.tueisDeveloper.helpaloo.Classes;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Post {

        private String userId;
        private String title;
        private String description;
        private String prize;
        private String time;
        private int status;
        private String route;
        private String postId;
        private String postNameUser;
        private String postSurnameUser;
        private double latitude;
        private double longitude;
        public Post() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Post(String postId, String userId, String title, String description, String prize, String time, String route, String postNameUser, String postSurnameUser, double latitude, double longitude) {
            this.postId = postId;
            this.userId = userId;
            this.title = title;
            this.description = description;
            this.prize = prize;
            this.time = time;
            this.status = 0;
            if (route == null) {
                this.route = "";
            }else{
                this.route = route;
            }
            this.postNameUser = postNameUser;
            this.postSurnameUser = postSurnameUser;
            this.latitude = latitude;
            this.longitude = longitude;
        }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @NonNull
    @Override
    public String toString() {
        return "Post{" +
                "userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", prize='" + prize + '\'' +
                ", time='" + time + '\'' +
                ", status=" + status +
                ", route='" + route + '\'' +
                ", postId='" + postId + '\'' +
                ", postNameUser='" + postNameUser + '\'' +
                ", postSurnameUser='" + postSurnameUser + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", posts=" + posts +
                '}';
    }

    private ArrayList<Post> posts;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostNameUser() {
        return postNameUser;
    }

    public void setPostNameUser(String postNameUser) {
        this.postNameUser = postNameUser;
    }

    public String getPostSurnameUser() {
        return postSurnameUser;
    }

    public void setPostSurnameUser(String postSurnameUser) {
        this.postSurnameUser = postSurnameUser;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }
}
