package com.jd.bluedragon.distribution.material.service;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialRelation;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanQuery;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanVO;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

/**
 * @ClassName MaterialOperationService
 * @Description
 * @Author wyh
 * @Date 2020/2/21 11:42
 **/
public interface MaterialOperationService {

    /**
     * @Description 根据收货方式编号获得绑定关系
     * @param [receiveCode]
     * @Author wyh
     * @Date 2020/2/21 14:08
     * @return com.jd.bluedragon.distribution.command.JdResult<java.util.List<com.jd.bluedragon.distribution.material.domain.DmsMaterialRelation>>
     **/
    JdResult<List<DmsMaterialRelation>> listMaterialRelations(String receiveCode);

    /**
     * @Description 批量新增或更新发货记录，每次发货增加发货流水
     * @param [materialSends]
     * @Author wyh
     * @Date 2020/2/21 14:40
     * @return com.jd.bluedragon.distribution.command.JdResult<java.lang.Boolean>
     **/
    JdResult<Boolean> saveMaterialSend(List<DmsMaterialSend> materialSends);

    /**
     * @Description 批量新增或更新收货记录，每次收货增加收货流水
     * @param [materialReceives]
     * @Author wyh
     * @Date 2020/2/21 14:41
     * @return com.jd.bluedragon.distribution.command.JdResult<java.lang.Boolean>
     **/
    JdResult<Boolean> saveMaterialReceive(List<DmsMaterialReceive> materialReceives);

    /**
     * @Description 循环物资扫描查询
     * @param [query]
     * @Author wyh
     * @Date 2020/2/28 14:07
     * @return com.jd.ql.dms.common.web.mvc.api.PagerResult<com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanVO>
     **/
    PagerResult<RecycleMaterialScanVO> queryByPagerCondition(RecycleMaterialScanQuery query);
}
