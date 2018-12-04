package com.jd.bluedragon.distribution.arAbnormal;

import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.api.response.ArAbnormalResponse;
import org.springframework.stereotype.Service;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月30日 15时:41分
 */
@Service("arAbnormalService")
public class ArAbnormalServiceImpl implements ArAbnormalService {
    public ArAbnormalResponse pushArAbnormal(ArAbnormalRequest arAbnormalRequest) {
        ArAbnormalResponse response = new ArAbnormalResponse();
        if (arAbnormalRequest.getPackageCode()==null){
            response.setCode(ArAbnormalResponse.CODE_NODATA);
            response.setMessage(ArAbnormalResponse.MESSAGE_NODATA);
            return response;
        }
        return response;
    }
}
