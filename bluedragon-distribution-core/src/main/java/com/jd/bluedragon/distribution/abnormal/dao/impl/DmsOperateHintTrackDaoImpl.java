package com.jd.bluedragon.distribution.abnormal.dao.impl;

import com.jd.bluedragon.distribution.abnormal.dao.DmsOperateHintTrackDao;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintTrack;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xumei3 on 2018/7/26.
 */
@Repository("dmsOperateHintTrackDao")
public class DmsOperateHintTrackDaoImpl extends BaseDao<DmsOperateHintTrack> implements DmsOperateHintTrackDao {
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
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("waybillCode",waybillCode);
        param.put("dmsSiteCode",dmsSiteCode);
        return 	this.sqlSession.selectOne(getNameSpace()+".queryFirstTrack", param);
    }

}
