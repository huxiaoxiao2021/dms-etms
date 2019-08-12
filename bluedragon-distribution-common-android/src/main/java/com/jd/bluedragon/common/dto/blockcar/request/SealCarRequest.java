package com.jd.bluedragon.common.dto.blockcar.request;

import java.io.Serializable;
import java.util.List;

/**
 * SealCarRequest
 * 封车请求
 * @author jiaowenqiang
 * @date 2019/6/25
 */
public class SealCarRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<SealCarDto> sealCarDtoList;

    @Override
    public String toString() {
        return "SealCarRequest{" +
                "sealCarDtoList=" + sealCarDtoList +
                '}';
    }

    public List<SealCarDto> getSealCarDtoList() {
        return sealCarDtoList;
    }

    public void setSealCarDtoList(List<SealCarDto> sealCarDtoList) {
        this.sealCarDtoList = sealCarDtoList;
    }
}
