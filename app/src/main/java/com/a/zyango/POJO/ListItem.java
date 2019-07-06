package com.a.zyango.POJO;

public class ListItem {
    int icon_id;
    String title;
    String value;
    int editable_id;

    public ListItem(int icon_id, String title, String value, int editable_id) {
        this.icon_id = icon_id;
        this.title = title;
        this.value = value;
        this.editable_id = editable_id;
    }

    public int getIcon_id() {
        return icon_id;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public int getEditable_id() {
        return editable_id;
    }
}
