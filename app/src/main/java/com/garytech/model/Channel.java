package com.garytech.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "channel")
public class Channel {


    @ElementList(inline=true)
    List<Film> mFilmList;

    public Channel() {
    }


    public List<Film> getFilmList() {
        return mFilmList;
    }

}
