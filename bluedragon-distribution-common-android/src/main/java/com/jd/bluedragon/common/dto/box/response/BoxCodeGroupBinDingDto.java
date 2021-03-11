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
     * 没绑定循环集包袋的BC箱号集合
     */
    private List<BoxDto> noBinDingList;

    /**
     * 绑定循环集包袋的BC箱号集合
     */
    private List<BoxDto> binDingList;

    /**
     * 同组箱号总数量
     */
    private Integer groupTotal;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<BoxDto> getNoBinDingList() {
        return noBinDingList;
    }

    public void setNoBinDingList(List<BoxDto> noBinDingList) {
        this.noBinDingList = noBinDingList;
    }

    public List<BoxDto> getBinDingList() {
        return binDingList;
    }

    public void setBinDingList(List<BoxDto> binDingList) {
        this.binDingList = binDingList;
    }

    public Integer getGroupTotal() {
        return groupTotal;
    }

    public void setGroupTotal(Integer groupTotal) {
        this.groupTotal = groupTotal;
    }
}
    
