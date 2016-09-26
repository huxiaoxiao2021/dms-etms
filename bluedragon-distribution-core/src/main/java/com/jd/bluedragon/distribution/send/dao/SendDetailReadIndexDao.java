package com.jd.bluedragon.distribution.send.dao;

import com.jd.bluedragon.distribution.api.response.SendBoxDetailResponse;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dudong on 2016/9/26.
 */
public class SendDetailReadIndexDao extends SendDatailReadDao{

    @Override
    public List<SendDetail> findUpdatewaybillCodeMessage(List<String> queryCondition) {
        return super.findUpdatewaybillCodeMessage(queryCondition);
    }

    @Override
    public List<String> findWaybillByBoxCode(String boxCode) {  //// FIXME: 2016/9/26
        return super.findWaybillByBoxCode(boxCode);
    }

    @Override
    public List<SendBoxDetailResponse> findSendBoxByWaybillCode(String waybillCode) {  //// FIXME: 2016/9/26
        return super.findSendBoxByWaybillCode(waybillCode);
    }

    @Override
    public List<SendDetail> findBySendCodeAndDmsCode(Map<String, Object> params) {
        return super.findBySendCodeAndDmsCode(params);
    }
}
