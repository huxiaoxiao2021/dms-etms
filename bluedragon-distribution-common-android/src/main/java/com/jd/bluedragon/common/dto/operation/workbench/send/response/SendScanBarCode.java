package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SendScanBarCode
 * @Description
 * @Author wyh
 * @Date 2022/5/19 17:57
 **/
public class SendScanBarCode implements Serializable {

    private static final long serialVersionUID = -9041548101278236982L;

    /**
     * 单号
     */
    private String barCode;

    /**
     * 单号标签集合
     */
    private List<LabelOption> tags;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public List<LabelOption> getTags() {
        return tags;
    }

    public void setTags(List<LabelOption> tags) {
        this.tags = tags;
    }
}
