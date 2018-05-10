package com.jd.bluedragon.distribution.external.api;

import com.jd.bluedragon.distribution.api.response.BaseResponse;

/**
 * <p>
 * Created by lixin39 on 2018/5/9.
 */
public interface DmsBaseApi {

    /**
     * 获取始发网点详细信息
     *
     * @param code
     * @return
     */
    BaseResponse getSite(String code);

}
