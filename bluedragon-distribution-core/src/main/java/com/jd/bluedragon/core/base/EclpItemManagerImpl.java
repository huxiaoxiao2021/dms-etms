package com.jd.bluedragon.core.base;

import com.jd.bluedragon.utils.JsonHelper;
import com.jd.kom.ext.service.OrderExtendService;
import com.jd.kom.ext.service.domain.request.SoNoItemRequest;
import com.jd.kom.ext.service.domain.response.ItemInfo;
import com.jd.kom.ext.service.domain.response.KomResponse;
import com.jd.kom.ext.service.domain.response.SoNoItemResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年05月11日 18时:35分
 */
@Service("eclpItemManager")
public class EclpItemManagerImpl implements EclpItemManager {
    private static final Logger logger = Logger.getLogger(EclpItemManagerImpl.class);
    @Autowired
    OrderExtendService orderExtendService;

    @Override
    public List<ItemInfo> getltemBySoNo(String soNo) {
        SoNoItemRequest soNoItemRequest = new SoNoItemRequest();
        soNoItemRequest.setSoNo(soNo);
        soNoItemRequest.setTid(new Date().getTime());
        KomResponse<SoNoItemResponse> komResponse;
        try {
            komResponse = orderExtendService.getItemBySoNo(soNoItemRequest);
        } catch (Exception e) {
            logger.error("EclpItemManagerImpl-getltemBySoNo 调用失败", e);
            return null;
        }
        if (komResponse == null || komResponse.getResultCode() < 1 || komResponse.getData() == null) {
            logger.warn("EclpItemManagerImpl-getltemBySoNo 查询失败，查询结果：" + JsonHelper.toJson(komResponse) + ",参数：" + JsonHelper.toJson(soNoItemRequest));
            return null;
        }
        return komResponse.getData().getItemInfoList();
    }
}
