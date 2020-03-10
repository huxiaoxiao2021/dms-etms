package com.jd.bluedragon.distribution.material.dao;

import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialRelation;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanQuery;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanVO;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MaterialRelationDao extends Dao<DmsMaterialRelation> {

    boolean insert(DmsMaterialRelation record);

    int batchInsertOnDuplicate(@Param("list") List<DmsMaterialRelation> relations);

    List<DmsMaterialRelation> listRelationsByReceiveCode(String receiveCode);

    int deleteByReceiveCode(String receiveCode);

    PagerResult<RecycleMaterialScanVO> queryReceiveAndSend(RecycleMaterialScanQuery query);
}