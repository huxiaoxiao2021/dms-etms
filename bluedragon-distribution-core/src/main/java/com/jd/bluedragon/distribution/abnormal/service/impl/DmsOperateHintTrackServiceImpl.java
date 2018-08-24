package com.jd.bluedragon.distribution.abnormal.service.impl;

import com.jd.bluedragon.distribution.abnormal.dao.DmsOperateHintTrackDao;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintTrack;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintTrackService;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by xumei3 on 2018/7/26.
 */
@Service("dmsOperateHintTrackService")
public class DmsOperateHintTrackServiceImpl extends BaseService<DmsOperateHintTrack> implements DmsOperateHintTrackService {

    @Autowired
    @Qualifier("dmsOperateHintTrackDao")
    private DmsOperateHintTrackDao dmsOperateHintTrackDao;

    @Override
    public Dao<DmsOperateHintTrack> getDao() {
        return this.dmsOperateHintTrackDao;
    }

    /**
     * 将提示信息存储到dms_operate_hint_track表
     * @param dmsOperateHintTrack
     * @return
     */
    public boolean save(DmsOperateHintTrack dmsOperateHintTrack){
        return dmsOperateHintTrackDao.insert(dmsOperateHintTrack);
    }

    /**
     * 根据运单号和创建加急提示的分拣中心编码
     * 获取第一条显示提示的记录
     * 判断逻辑：
     * waybillCode相同，而且提示的分拣中心编码不等于创建提示的分拣中心的编码
     * @param waybillCode
     * @param dmsSiteCode
     * @return
     */
    public DmsOperateHintTrack queryFirstTrack(String waybillCode,Integer dmsSiteCode){
        return dmsOperateHintTrackDao.queryFirstTrack(waybillCode,dmsSiteCode);
    }
}
