package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LessonItem implements Serializable {
   private String ID;
   private String title;
   private String video;
   private String IDCourse;
   private String popUpID;
   private ArrayList<String> documents;
   private boolean isCompleted;
   private int order;

   public LessonItem() {}

   public LessonItem(String ID, String title, String video, String IDCourse,
                     String popUpID, ArrayList<String> documents, boolean isCompleted, int order) {
       this.ID = ID;
       this.title = title;
       this.video = video;
       this.IDCourse = IDCourse;
       this.popUpID = popUpID;
       this.documents = documents;
       this.isCompleted = isCompleted;
       this.order = order;
   }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getIDCourse() {
        return IDCourse;
    }

    public void setIDCourse(String IDCourse) {
        this.IDCourse = IDCourse;
    }

    public String getPopUpID() {
        return popUpID;
    }

    public void setPopUpID(String popUpID) {
        this.popUpID = popUpID;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public ArrayList<String> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<String> documents) {
        this.documents = documents;
    }
}
