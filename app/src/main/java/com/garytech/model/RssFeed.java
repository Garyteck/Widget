package com.garytech.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "channel")
public class RssFeed {


    @ElementList(name = "item")
    List<Film> mFilmList;

    public RssFeed() {
    }


    public List<Film> getFilmList() {
        return mFilmList;
    }

    public void setFilmList(List<Film> filmList) {
        mFilmList = filmList;
    }
}
