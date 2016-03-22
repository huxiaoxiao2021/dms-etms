package com.jd.bluedragon.core.message;

/**
 * Created by wangtingwei on 2016/3/22.
 */

import java.io.Serializable;

public class MessageDto implements Serializable {
    public static final int TYPE_QUEUE = 1;
    public static final int TYPE_TOPIC = 2;
    private String destination;
    private int type;
    private String name;
    private String id;
    private String systemId;
    private String content;

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }


    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


