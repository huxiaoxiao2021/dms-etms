package com.jd.bluedragon.common.dto.integral.response;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/26
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
import java.util.List;

public class JyRuleDescriptionDTO {
    private String title;
    private List<String> context;

    public JyRuleDescriptionDTO() {
    }

    public String getTitle() {
        return this.title;
    }

    public List<String> getContext() {
        return this.context;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContext(List<String> context) {
        this.context = context;
    }

    public String toString() {
        return "JyRuleDescriptionDTO(title=" + this.getTitle() + ", context=" + this.getContext() + ")";
    }
}

