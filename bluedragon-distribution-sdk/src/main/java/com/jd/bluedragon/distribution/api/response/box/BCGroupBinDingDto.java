package com.jd.bluedragon.distribution.api.response.box;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/3/17 20:33
 */
public class BCGroupBinDingDto implements Serializable {

    private static final long serialVersionUID = -1L;


    private List<GroupBoxDto> binDingList;

    private List<GroupBoxDto> noBingDingList;

    public List<GroupBoxDto> getBinDingList() {
        return binDingList;
    }

    public void setBinDingList(List<GroupBoxDto> binDingList) {
        this.binDingList = binDingList;
    }

    public List<GroupBoxDto> getNoBingDingList() {
        return noBingDingList;
    }

    public void setNoBingDingList(List<GroupBoxDto> noBingDingList) {
        this.noBingDingList = noBingDingList;
    }
}
    
