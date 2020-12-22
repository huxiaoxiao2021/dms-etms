package com.jd.bluedragon.distribution.box.dao;

import com.jd.bluedragon.distribution.box.domain.BoxRelation;
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
}
