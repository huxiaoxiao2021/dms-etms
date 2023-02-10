package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;
import java.util.List;

public class CTTGroupDataResp implements Serializable {
    private static final long serialVersionUID = -3755876943736249853L;
    private List<CTTGroupDto> cttGroupDtolist;

    public List<CTTGroupDto> getCttGroupDtolist() {
        return cttGroupDtolist;
    }

    public void setCttGroupDtolist(List<CTTGroupDto> cttGroupDtolist) {
        this.cttGroupDtolist = cttGroupDtolist;
    }
}
