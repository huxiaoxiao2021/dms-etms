package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/8/16
 * @Description: 发货不齐处理提交返回对象
 */
public class IncompleteSendResp implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 全部成功标识
     */
    private Boolean data;

    /**
     * 失败包裹列表集合
     */
    private List<String> failPackList;


    public List<String> getFailPackList() {
        return failPackList;
    }

    public void setFailPackList(List<String> failPackList) {
        this.failPackList = failPackList;
    }

    public Boolean getData() {
        return data;
    }

    public void setData(Boolean data) {
        this.data = data;
    }
}
