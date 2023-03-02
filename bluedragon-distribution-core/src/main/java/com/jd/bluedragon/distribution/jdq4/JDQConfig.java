package com.jd.bluedragon.distribution.jdq4;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class JDQConfig {

    private String username;

    private String domain;

    private String password;

    private String groupId;

    private String topic;

    public JDQConfig(String username, String domain, String password, String groupId, String topic) {
        this.username = username;
        this.domain = domain;
        this.password = password;
        this.groupId = groupId;
        this.topic = topic;
    }
}
