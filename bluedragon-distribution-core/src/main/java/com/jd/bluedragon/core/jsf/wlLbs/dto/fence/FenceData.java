
package com.jd.bluedragon.core.jsf.wlLbs.dto.fence;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FenceData implements Serializable {

    private static final long serialVersionUID = 3352260973307489714L;
    private List<String> commonFenceGeoJson;
    private Boolean showBtnFlag;
    private List<TransFenceInfoVo> transFenceInfoVoList;

}
