package Model;

import java.io.Serializable;

public class DocumentItem implements Serializable {
    private String name;

    public DocumentItem() {}

    public DocumentItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
