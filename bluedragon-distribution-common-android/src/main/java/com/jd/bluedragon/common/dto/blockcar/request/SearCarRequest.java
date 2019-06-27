package com.jd.bluedragon.common.dto.blockcar.request;

import java.io.Serializable;
import java.util.List;

/**
 * SearCarRequest
 * 封车请求
 * @author jiaowenqiang
 * @date 2019/6/25
 */
public class SearCarRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<SearCarDto> searCarDtoList;

    public List<SearCarDto> getSearCarDtoList() {
        return searCarDtoList;
    }

    public void setSearCarDtoList(List<SearCarDto> searCarDtoList) {
        this.searCarDtoList = searCarDtoList;
    }
}
