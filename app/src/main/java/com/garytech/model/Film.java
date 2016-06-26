package com.garytech.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Root(name = "item")
@Namespace(prefix = "torrent",reference = "https://kat.ch.wr/content/xmlns/0.1/")
public class Film extends RealmObject{

    @Element(name = "title")
    @PrimaryKey
    String mTitle;

    @Element(name = "magnetURI")
    String mMagnetUri;

    @Element(name = "guid")
    String mInfo;

    public Film() {
    }

    public String getTitle() {
        return mTitle;
    }

    public String getmMagnetUri() {
        return mMagnetUri;
    }

    public String getInfo() {
        return mInfo;
    }


}
