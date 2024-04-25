package com.jd.bluedragon.distribution.box.dao;

import com.jd.bluedragon.distribution.box.domain.BoxRelation;
import com.jd.bluedragon.distribution.box.domain.BoxRelationQ;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 * @ClassName BoxRelationDao
 * @Description
 * @Author wyh
 * @Date 2020/12/14 16:09
 **/
public interface BoxRelationDao extends Dao<BoxRelation> {

    List<BoxRelation> queryBoxRelation(BoxRelation relation);

    int updateByUniqKey(BoxRelation relation);

    int countByBoxCode(BoxRelation relation);

    int countByQuery(BoxRelationQ query);

    List<BoxRelation> getByBoxCode(String boxCode);

    /**
     * 根据子箱号获取父箱号
     * @param boxCode
     * @return
     */
    List<BoxRelation> getByRelationCode(String boxCode);

    List<BoxRelation> brushQueryAllByPage(Integer startId);

    int brushUpdateById(BoxRelation detail);

}
