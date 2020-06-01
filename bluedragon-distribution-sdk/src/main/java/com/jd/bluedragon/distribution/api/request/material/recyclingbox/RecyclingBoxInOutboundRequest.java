package com.jd.bluedragon.distribution.api.request.material.recyclingbox;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.util.List;

/**
 * @author lijie
 * @date 2020/5/26 16:53
 */
public class RecyclingBoxInOutboundRequest extends JdRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 封签号集合
     */
    private List<String> tagNos;

    /**
     * 用户ERP
     */
    private String userErp;

    public List<String> getTagNos() {
        return tagNos;
    }

    public void setTagNos(List<String> tagNos) {
        this.tagNos = tagNos;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }
}
