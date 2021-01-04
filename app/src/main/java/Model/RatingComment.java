package Model;

import java.io.Serializable;

public class RatingComment implements Serializable {

    String userName;
    String commentContent;
    String avatar;
    Float numStar;

    public RatingComment() {}

    public RatingComment(String userName, String commentContent, Float numStart, String avatar) {
        this.userName = userName;
        this.commentContent = commentContent;
        this.numStar = numStart;
        this.avatar = avatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public Float getNumStar() {
        return numStar;
    }

    public void setNumStar(Float numStar) {
        this.numStar = numStar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
