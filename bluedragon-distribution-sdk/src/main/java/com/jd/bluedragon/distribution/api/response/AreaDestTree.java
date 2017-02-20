package com.jd.bluedragon.distribution.api.response;

import java.util.List;

/**
 * 区域目的地树结构
 * <p>
 * Created by lixin39 on 2016/12/12.
 */
public class AreaDestTree {

    /**
     * 节点名称
     */
    private String text;

    /**
     * 节点id
     */
    private Integer id;

    /**
     * 节点标签显示内容
     */
    private List<String> tags;

    /**
     * 子节点
     */
    private List<AreaDestTree> nodes;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<AreaDestTree> getNodes() {
        return nodes;
    }

    public void setNodes(List<AreaDestTree> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "AreaDestTree{" +
                "text='" + text + '\'' +
                ", id='" + id + '\'' +
                ", tags=" + tags +
                ", nodes=" + nodes +
                '}';
    }
}
