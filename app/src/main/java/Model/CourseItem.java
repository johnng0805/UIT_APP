package Model;

import java.io.Serializable;

public class CourseItem implements Serializable {

    //---Information---
    private String url;
    private String title;
    private String description;
    private String author;
    private String authorID;

    private String categoryName;
    private String categoryID;
    private String ID;

    //---Price---
    private String fee;
    private float price;
    private float discount;

    //---Rating---
    private float rating;
    private String ranking;
    private float totalVote;

    //---Progress---
    private String goal;
    private int percent;
    private String updateTime;
    private String createAt;

    public CourseItem() {}

    public CourseItem(String url, String title, String author, String ID, float price, float discount) {
        this.url = url;
        this.title = title;
        this.author = author;
        this.ID = ID;
        this.price = price;
        this.discount = discount;
    }

    public CourseItem(String url, String title, String fee, float rating) {
        this.url = url;
        this.title = title;
        this.fee = fee;
        this.rating = rating;
    }

    public CourseItem(String url, String title, String fee, String author,
                      float rating, float price, float discount, float totalVote) {
        this.url = url;
        this.title = title;
        this.fee = fee;
        this.author = author;
        this.rating = rating;
        this.price = price;
        this.discount = discount;
        this.totalVote = totalVote;
    }

    public CourseItem(String url, String title, String fee, String author,
                      float rating, float price, float discount, float totalVote,
                      String goal, String description) {
        this.url = url;
        this.title = title;
        this.fee = fee;
        this.author = author;
        this.rating = rating;
        this.price = price;
        this.discount = discount;
        this.totalVote = totalVote;
        this.goal = goal;
        this.description = description;
    }

    public CourseItem(String url, String title, String fee, String author,
                      float rating, float price, float discount, float totalVote,
                      String goal, String description, String ID, String categoryName,
                      String categoryID, String ranking, String updateTime) {
        this.url = url;
        this.title = title;
        this.fee = fee;
        this.author = author;
        this.rating = rating;
        this.price = price;
        this.discount = discount;
        this.totalVote = totalVote;
        this.goal = goal;
        this.description = description;
        this.ID = ID;
        this.categoryName = categoryName;
        this.categoryID = categoryID;
        this.ranking = ranking;
        this.updateTime = updateTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public float getTotalVote() {
        return totalVote;
    }

    public void setTotalVote(float totalVote) {
        this.totalVote = totalVote;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
