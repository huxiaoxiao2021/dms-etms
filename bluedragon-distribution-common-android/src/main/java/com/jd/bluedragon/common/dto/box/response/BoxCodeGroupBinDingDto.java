package com.jd.bluedragon.common.dto.box.response;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: liming522
 * @Description: 用于判断一组箱号中未绑定的循环集包袋
 * @Date: create in 2021/3/11 19:50
 */
public class BoxCodeGroupBinDingDto implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 当前箱号绑定的集包袋号
     */
    private String binDingMaterialCode;

    /**
     * 同组有效箱号总数量
     */
    private Integer groupTotal;

    /**
     * 同组的箱信息(BC类型和非BC类型均有)
     */
    private List<BoxDto> groupList;

    /**
     * 绑定了循环集包袋的BC箱号集合(只有BC类型)
     */
    private List<BoxDto> binDingList;

    /**
     * 没绑定循环集包袋的BC箱号集合(只有BC类型)
     */
    private List<BoxDto>  noBinDingList;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getGroupTotal() {
        return groupTotal;
    }

    public void setGroupTotal(Integer groupTotal) {
        this.groupTotal = groupTotal;
    }

    public String getBinDingMaterialCode() {
        return binDingMaterialCode;
    }

    public void setBinDingMaterialCode(String binDingMaterialCode) {
        this.binDingMaterialCode = binDingMaterialCode;
    }


    public List<BoxDto> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<BoxDto> groupList) {
        this.groupList = groupList;
    }

    public List<BoxDto> getBinDingList() {
        return binDingList;
    }

    public void setBinDingList(List<BoxDto> binDingList) {
        this.binDingList = binDingList;
    }

    public List<BoxDto> getNoBinDingList() {
        return noBinDingList;
    }

    public void setNoBinDingList(List<BoxDto> noBinDingList) {
        this.noBinDingList = noBinDingList;
    }
}
    
