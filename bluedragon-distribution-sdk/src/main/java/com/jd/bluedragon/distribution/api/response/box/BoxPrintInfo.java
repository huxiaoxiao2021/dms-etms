package com.jd.bluedragon.distribution.api.response.box;

/**
 * 打印箱号返回数据
 * @author fanggang7
 * @time 2023-10-25 10:20:44 周三
 */
public class BoxPrintInfo extends BoxPrintBaseInfo{

    private static final long serialVersionUID = 7338400646291264936L;

    private String boxCode;

    public BoxPrintInfo() {
    }

    public String getBoxCode() {
        return boxCode;
    }

    public BoxPrintInfo setBoxCode(String boxCode) {
        this.boxCode = boxCode;
        return this;
    }
}
