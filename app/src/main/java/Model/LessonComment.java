package Model;

import java.io.Serializable;

public class LessonComment implements Serializable {

    private String content;
    private String createdAt;
    private String idCourse;
    private String idLesson;
    private String idParent;
    private UserAccount idUser;
    private String image;
    private String pageType;
    private String ID;

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(String idCourse) {
        this.idCourse = idCourse;
    }

    public String getIdLesson() {
        return idLesson;
    }

    public void setIdLesson(String idLesson) {
        this.idLesson = idLesson;
    }

    public String getIdParent() {
        return idParent;
    }

    public void setIdParent(String idParent) {
        this.idParent = idParent;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public UserAccount getIdUser() {
        return idUser;
    }

    public void setIdUser(UserAccount idUser) {
        this.idUser = idUser;
    }
}
