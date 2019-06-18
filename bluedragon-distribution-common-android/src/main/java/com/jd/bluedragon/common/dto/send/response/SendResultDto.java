package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;

/**
 * SendResultDto
 * 批量一车一单发货返回
 * @author jiaowenqiang
 * @date 2019/6/18
 */
public class SendResultDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 操作结果【1：发货成功  2：发货失败  4：需要用户确认  6: 发货成功，但是有警告性提示信息展示】
     */
    private Integer key;

    /**
     * 提示信息
     */
    private String value;

    /**
     * 实体构造方法
     */
    public SendResultDto(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "SendResultDto{" +
                "key=" + key +
                ", value='" + value + '\'' +
                '}';
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
