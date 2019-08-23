package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.consumable.service.DmsConsumableRelationService;
import com.jd.bluedragon.distribution.consumable.service.PackingConsumableInfoService;
import com.jd.bluedragon.distribution.external.service.DmsPackingConsumableService;
import com.jd.bluedragon.distribution.packingconsumable.domain.DmsPackingConsumableInfo;
import com.jd.bluedragon.distribution.packingconsumable.domain.PackingConsumableBaseInfo;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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

    @Autowired
    private PackingConsumableInfoService packingConsumableInfoService;

    @Override
    @JProfiler(jKey = "DMSWEB.DmsPackingConsumableServiceImpl.getPackingConsumableInfoByDmsId", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
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
            //集合不为空则更新字段为支持包装耗材
            if (packingConsumableBaseInfoList != null && packingConsumableBaseInfoList.size() > 0) {
                dmsPackingConsumableInfo.setSupportStatus(Constants.DMS_SUPPORT_PACKING_STATUS);
            } else {
                dmsPackingConsumableInfo.setSupportStatus(Constants.DMS_NOT_SUPPORT_PACKING_STATUS);
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
    @JProfiler(jKey = "DMSWEB.DmsPackingConsumableServiceImpl.getPackingConsumableInfoByCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdResponse<PackingConsumableBaseInfo> getPackingConsumableInfoByCode(String consumableCode) {
        JdResponse<PackingConsumableBaseInfo> jdResponse = new JdResponse<PackingConsumableBaseInfo>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);

        if (consumableCode == null) {
            logger.warn("获取耗材信息失败：编号不能为null");
            jdResponse.setCode(JdResponse.CODE_FAIL);
            jdResponse.setMessage("获取耗材信息失败：编号不能为null");
            return jdResponse;
        }
        try {
            PackingConsumableBaseInfo packingConsumableBaseInfo = packingConsumableInfoService.getPackingConsumableInfoByCode(consumableCode);
            jdResponse.setData(packingConsumableBaseInfo);
        } catch (Exception e) {
            logger.error("获取耗材信息失败", e);
            jdResponse.setCode(JdResponse.CODE_ERROR);
            jdResponse.setMessage("获取耗材信息失败");
        }

        return jdResponse;
    }
}
