package com.jd.bluedragon.core.jsf.wlLbs.dto.fenceForWeb;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * description
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-12-12 16:02:37 周一
 */
@Data
public class FenceData implements Serializable {

    private static final long serialVersionUID = 8420252431532323969L;

    private List<Feature> features;

    private String type;

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
