package com.jd.bluedragon.common.dto.comboard.response;


import com.jd.bluedragon.common.dto.comboard.request.ComboardDetailDto;
import java.io.Serializable;
import java.util.List;

public class HaveScanStatisticsResp implements Serializable {
    private static final long serialVersionUID = 6211348983763778535L;
    List<HaveScanDto> haveScanDtoList;

    public List<HaveScanDto> getHaveScanDtoList() {
        return haveScanDtoList;
    }

    public void setHaveScanDtoList(
        List<HaveScanDto> haveScanDtoList) {
        this.haveScanDtoList = haveScanDtoList;
    }
}
