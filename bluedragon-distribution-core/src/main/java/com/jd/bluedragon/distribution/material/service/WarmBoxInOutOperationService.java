package com.jd.bluedragon.distribution.material.service;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialRelation;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanQuery;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanVO;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

/**
 * @ClassName WarmBoxInOutOperationService
 * @Description
 * @Author wyh
 * @Date 2020/3/16 13:43
 **/
public interface WarmBoxInOutOperationService extends MaterialOperationService {

    JdResult<List<DmsMaterialRelation>> listMaterialRelations(String receiveCode);

    PagerResult<RecycleMaterialScanVO> queryByPagerCondition(RecycleMaterialScanQuery query);
}
