package Model;

import android.provider.ContactsContract;

import java.io.Serializable;

public class CategoryItem implements Serializable {

    private String Name;
    private String ID;
    private String Image;

    public CategoryItem(String Name, String ID, String Image) {
        this.Name = Name;
        this.ID = ID;
        this.Image = Image;
    }

    public CategoryItem(String Name) {
        this.Name = Name;
    }

    public CategoryItem(String Name, String ID) {
        this.Name = Name;
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
