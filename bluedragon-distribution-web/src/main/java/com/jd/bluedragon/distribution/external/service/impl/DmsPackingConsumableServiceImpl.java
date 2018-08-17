package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.distribution.consumable.service.DmsConsumableRelationService;
import com.jd.bluedragon.distribution.external.service.DmsPackingConsumableService;
import com.jd.bluedragon.distribution.packingconsumable.domain.DmsPackingConsumableInfo;
import com.jd.bluedragon.distribution.packingconsumable.domain.PackingConsumableBaseInfo;
import com.jd.ql.dms.common.domain.JdResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by hanjiaxing1 on 2018/8/14.
 */
@Component
public class DmsPackingConsumableServiceImpl implements DmsPackingConsumableService {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private DmsConsumableRelationService dmsConsumableRelationService;

    @Override
    public JdResponse<DmsPackingConsumableInfo> getPackingConsumableInfoByDmsId(Integer dmsId) {

        JdResponse<DmsPackingConsumableInfo> jdResponse = new JdResponse<DmsPackingConsumableInfo>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);

        if (dmsId == null) {
            logger.warn("获取耗材信息失败：分拣中心编号不能为null");
            jdResponse.setCode(JdResponse.CODE_FAIL);
            jdResponse.setMessage("获取耗材信息失败：分拣中心编号不能为null");
            return jdResponse;
        }
        DmsPackingConsumableInfo dmsPackingConsumableInfo = new DmsPackingConsumableInfo();
        try {
            List<PackingConsumableBaseInfo> packingConsumableBaseInfoList = dmsConsumableRelationService.getPackingConsumableInfoByDmsId(dmsId);

            dmsPackingConsumableInfo.setDmsId(dmsId);
            dmsPackingConsumableInfo.setPackingConsumableBaseInfoList(packingConsumableBaseInfoList);
            if (packingConsumableBaseInfoList != null && packingConsumableBaseInfoList.size() > 0) {
                dmsPackingConsumableInfo.setSupportStatus(1);
            } else {
                dmsPackingConsumableInfo.setSupportStatus(0);
            }
            jdResponse.setData(dmsPackingConsumableInfo);
        } catch (Exception e) {
            logger.error("获取耗材信息失败", e);
            jdResponse.setCode(JdResponse.CODE_ERROR);
            jdResponse.setMessage("获取耗材信息失败");
        }

        return jdResponse;
    }

    @Override
    public JdResponse<PackingConsumableBaseInfo> getPackingConsumableInfoByCode(String consumableCode) {
        return null;
    }
}
