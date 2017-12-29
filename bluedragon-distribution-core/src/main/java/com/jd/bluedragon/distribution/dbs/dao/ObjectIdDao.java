package com.jd.bluedragon.distribution.dbs.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.dbs.domain.ObjectId;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dudong on 2016/9/19.
 */
public class ObjectIdDao extends BaseDao<ObjectId>{
    public Integer updateFirstIdByName(String objectName, Integer count) {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("objectName",objectName);
        param.put("count",count);
        return this.getSqlSession().update(getSqlId("updateFirstIdByName"),param);
    }

    public int insertObjectId(String objectName, Integer firstId) {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("objectName",objectName);
        param.put("firstId",firstId);
        return this.getSqlSession().insert(getSqlId("insertObjectId"),param);
    }


    public Integer selectFirstIdByName(String objectName) {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("objectName",objectName);
        return this.getSqlSession().selectOne(getSqlId("selectFirstIdByName"),param);
    }
    /**
     * 根据序列号的key值和当前Id值，更新数据
     * @param objectName 序号Key值
     * @param currId 当前Id
     * @param count 增加的个数
     * @return
     */
    public int updateFirstIdByNameAndCurrId(String objectName, Integer currId,Integer count) {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("objectName",objectName);
        param.put("currId",currId);
        param.put("count",count);
        return this.getSqlSession().update(getSqlId("updateFirstIdByNameAndCurrId"),param);
    }
    private String getSqlId(String sqlId) {
        return this.getClass().getName() + "." + sqlId;
    }
}
