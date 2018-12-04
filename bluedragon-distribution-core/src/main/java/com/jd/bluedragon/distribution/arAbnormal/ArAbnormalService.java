package com.jd.bluedragon.distribution.arAbnormal;

import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.api.response.ArAbnormalResponse;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月30日 15时:41分
 */
public interface ArAbnormalService {
    public ArAbnormalResponse pushArAbnormal(ArAbnormalRequest arAbnormalRequest);
}
