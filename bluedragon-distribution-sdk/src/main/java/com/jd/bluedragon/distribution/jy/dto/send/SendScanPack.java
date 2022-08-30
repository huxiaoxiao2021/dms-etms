package com.jd.bluedragon.distribution.jy.dto.send;


import com.jd.bluedragon.distribution.jy.dto.JyLabelOption;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SendScanBarCode
 * @Description
 * @Author wyh
 * @Date 2022/5/19 17:57
 **/
public class SendScanPack implements Serializable {

    private static final long serialVersionUID = -9041548101278236982L;

    /**
     * 单号
     */
    private String barCode;

    /**
     * 单号标签集合
     */
    private List<JyLabelOption> tags;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public List<JyLabelOption> getTags() {
        return tags;
    }

    public void setTags(List<JyLabelOption> tags) {
        this.tags = tags;
    }
}
