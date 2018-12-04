package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.api.response.ArAbnormalResponse;
import com.jd.ql.dms.common.domain.DictionaryInfoModel;

import java.util.List;

/**
 * 空铁基础服务接口
 * <p>
 * Created by lixin39 on 2018/5/28.
 */
public interface DmsArBaseService {

    /**
     * 空铁项目：登录获取字典信息,由于物流网关不支持无参方法，故通过该方法跳转
     *
     * @param arg 任意值
     * @return
     */
    List<DictionaryInfoModel> getARCommonDictionaryInfo(String arg);
}
