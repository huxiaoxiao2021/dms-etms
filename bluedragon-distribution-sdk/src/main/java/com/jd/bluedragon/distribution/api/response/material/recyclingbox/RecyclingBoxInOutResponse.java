package com.jd.bluedragon.distribution.api.response.material.recyclingbox;

import java.io.Serializable;
import java.util.List;

/**
 * @author lijie
 * @date 2020/5/26 17:06
 */
public class RecyclingBoxInOutResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 封签号集合
     */
    private List<String> tagNos;

    public List<String> getTagNos() {
        return tagNos;
    }

    public void setTagNos(List<String> tagNos) {
        this.tagNos = tagNos;
    }
}
