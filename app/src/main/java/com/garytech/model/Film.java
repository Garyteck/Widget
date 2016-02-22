package com.garytech.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Film {

    @Element(name = "title")
    String mTitle;

    public Film() {
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
