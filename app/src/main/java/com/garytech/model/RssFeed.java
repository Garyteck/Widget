package com.garytech.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "rss")
public class RssFeed {


    @Element(name ="channel")
    Channel channel;

    public RssFeed() {
    }

    public Channel getChannel() {
        return channel;
    }
}
