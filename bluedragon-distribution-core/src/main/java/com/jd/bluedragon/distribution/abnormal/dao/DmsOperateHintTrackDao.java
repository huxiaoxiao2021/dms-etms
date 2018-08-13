package com.jd.bluedragon.distribution.abnormal.dao;

import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintTrack;
import com.jd.ql.dms.common.web.mvc.api.Dao;

/**
 * Created by xumei3 on 2018/7/26.
 */
public interface DmsOperateHintTrackDao extends Dao<DmsOperateHintTrack> {
    /**
     * 根据运单号和创建加急提示的分拣中心编码
     * 获取第一条显示提示的记录
     * 判断逻辑：
     * waybillCode相同，而且提示的分拣中心编码不等于创建提示的分拣中心的编码
     * @param waybillCode
     * @param dmsSiteCode
     * @return
     */
    DmsOperateHintTrack queryFirstTrack(String waybillCode,Integer dmsSiteCode);
}
