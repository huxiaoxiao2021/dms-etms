package com.jd.bluedragon.distribution.external.service;

import com.jd.ql.dms.common.domain.DictionaryInfoModel;

import java.util.List;

/**
 * 空铁基础服务接口
 * <p>
 * Created by lixin39 on 2018/5/28.
 */
public interface DmsArBaseService {

    /**
     * 空铁项目：登录获取字典信息
     *
     * @return
     */
    List<DictionaryInfoModel> getARCommonDictionaryInfo();

}
