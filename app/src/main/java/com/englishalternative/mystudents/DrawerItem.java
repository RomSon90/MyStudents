package com.englishalternative.mystudents;

/**
 * Created by Roman on 21.04.2017.
 * This item is shown on drawer list
 */

public class DrawerItem {

    int imageResourceId;
    String text;

    public DrawerItem(int imageResourceId, String text) {
        super();
        this.imageResourceId = imageResourceId;
        this.text = text;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getText() {
        return text;
    }

}
