package com.jd.bluedragon.distribution.consumer.send.dto;

import com.jd.bluedragon.distribution.send.domain.SendDetailMessage;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

import java.io.Serializable;

/**
 * 发货明细消费上下文
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-20 20:08:49 周六
 */
public class SendDetailContext implements Serializable {

    private static final long serialVersionUID = -8874752477697863601L;

    private SendDetailMessage sendDetail;

    private BaseStaffSiteOrgDto operateUserStaffDto;

    private BaseStaffSiteOrgDto createSiteDto;

    private BaseStaffSiteOrgDto receiveSiteDto;

    public SendDetailMessage getSendDetail() {
        return sendDetail;
    }

    public void setSendDetail(SendDetailMessage sendDetail) {
        this.sendDetail = sendDetail;
    }

    public BaseStaffSiteOrgDto getOperateUserStaffDto() {
        return operateUserStaffDto;
    }

    public void setOperateUserStaffDto(BaseStaffSiteOrgDto operateUserStaffDto) {
        this.operateUserStaffDto = operateUserStaffDto;
    }

    public BaseStaffSiteOrgDto getCreateSiteDto() {
        return createSiteDto;
    }

    public void setCreateSiteDto(BaseStaffSiteOrgDto createSiteDto) {
        this.createSiteDto = createSiteDto;
    }

    public BaseStaffSiteOrgDto getReceiveSiteDto() {
        return receiveSiteDto;
    }

    public void setReceiveSiteDto(BaseStaffSiteOrgDto receiveSiteDto) {
        this.receiveSiteDto = receiveSiteDto;
    }
}
