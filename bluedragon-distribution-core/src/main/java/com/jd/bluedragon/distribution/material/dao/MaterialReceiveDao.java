package com.jd.bluedragon.distribution.material.dao;

import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanQuery;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanVO;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MaterialReceiveDao extends Dao<DmsMaterialReceive> {

    int batchInsertOnDuplicate(List<DmsMaterialReceive> list);

    int deleteByReceiveCode(String receiveCode, Long createSiteCode);
}