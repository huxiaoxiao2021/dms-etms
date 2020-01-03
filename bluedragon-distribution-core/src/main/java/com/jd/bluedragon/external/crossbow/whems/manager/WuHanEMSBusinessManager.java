package com.jd.bluedragon.external.crossbow.whems.manager;

import com.jd.bluedragon.external.crossbow.AbstractCrossbowManager;
import com.jd.bluedragon.external.crossbow.whems.domain.*;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.XmlHelper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * <p>
 *     武汉邮政面单信息推送业务
 *
 * @link https://cf.jd.com/pages/viewpage.action?pageId=232565358
 *
 * @author wuzuxiang
 * @since 2019/12/18
 **/
public class WuHanEMSBusinessManager extends AbstractCrossbowManager
        <WuHanEMSWaybillEntityRequest, WuHanEMSResponse> {

    private String actionCode;

    private String partnerCode;

    private String productProviderID;

    private String secretKey;

    /**
     * 组装武汉邮政的请求实体
     * @param condition 相应的条件 为 WuHanEMSWaybillEntityDto... 数组 一个或者多个
     * @return
     */
    @Override
    protected WuHanEMSWaybillEntityRequest getMyRequestBody(Object condition) {
        WuHanEMSWaybillEntityRequest request = new WuHanEMSWaybillEntityRequest();
        request.setActionCode(actionCode);
        request.setPartnerCode(partnerCode);
        request.setProductProviderID(productProviderID);

        WuHanEMSWaybillEntityListDto listDto = new WuHanEMSWaybillEntityListDto();
        listDto.setOrderShipList(Collections.singletonList((WuHanEMSWaybillEntityDto) condition));

        WuHanEMSWaybillEntityOrderDto orderDto = new WuHanEMSWaybillEntityOrderDto();
        orderDto.setOrderShipList(listDto);

        request.setPlaintextData(orderDto);
        String bodyXml = XmlHelper.toXml(orderDto, WuHanEMSWaybillEntityOrderDto.class);

        request.setValidationData(
                new String(
                        new Base64().encode(
                                DigestUtils.md5Hex((
                                        (StringHelper.isNotEmpty(bodyXml)?
                                                bodyXml.substring(bodyXml.indexOf("?>") + 2) : "") + secretKey))
                                        .getBytes(StandardCharsets.UTF_8)),
                        StandardCharsets.UTF_8));
        return request;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public void setProductProviderID(String productProviderID) {
        this.productProviderID = productProviderID;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

}
