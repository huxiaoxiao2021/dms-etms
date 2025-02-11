package com.jd.bluedragon.distribution.send.domain;

import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.utils.converter.BeanConverter;

/**
 * 发货任务
 * Created by wangtingwei on 2015/3/20.
 */
public class SendTaskBody extends SendM{

    /**
     * 任务处理类别【1：按批次号；2：按箱号】
     */
    private Integer handleCategory;

    /**
     * 是否是通过按运单模式触发执行的
     */
    private boolean byWaybillFlag;


    public Integer getHandleCategory() {
        return handleCategory;
    }

    public boolean getByWaybillFlag() {
        return byWaybillFlag;
    }

    public void setByWaybillFlag(boolean byWaybillFlag) {
        this.byWaybillFlag = byWaybillFlag;
    }

    public void setHandleCategory(Integer handleCategory) {
        this.handleCategory = handleCategory;
    }

    public  void copyFromParent(SendM domain) {
        if (domain == null) {
            return ;
        }
        this.setSendMId(domain.getSendMId());
        this.setSendCode(domain.getSendCode());
        this.setThirdWaybillCode(domain.getThirdWaybillCode());
        this.setBoxCode(domain.getBoxCode());
        this.setTurnoverBoxCode(domain.getTurnoverBoxCode());
        this.setSendUser(domain.getSendUser());
        this.setSendUserCode(domain.getSendUserCode());
        this.setCreateSiteCode(domain.getCreateSiteCode());
        this.setReceiveSiteCode(domain.getReceiveSiteCode());
        this.setCarCode(domain.getCarCode());
        this.setSendType(domain.getSendType());
        this.setCreateUser(domain.getCreateUser());
        this.setCreateUserCode(domain.getCreateUserCode());
        this.setOperateTime(domain.getOperateTime());
        this.setCreateTime(domain.getCreateTime());
        this.setUpdateUserCode(domain.getUpdateUserCode());
        this.setUpdaterUser(domain.getUpdaterUser());
        this.setUpdateTime(domain.getUpdateTime());
        this.setShieldsCarId(domain.getShieldsCarId());
        this.setYn(domain.getYn());
        this.setSendmStatus(domain.getSendmStatus());
        this.setExcuteCount(domain.getExcuteCount());
        this.setExcuteTime(domain.getExcuteTime());
        this.setTransporttype(domain.getTransporttype());
        this.setBoardCode(domain.getBoardCode());
        this.setBizSource(domain.getBizSource());
        OperatorData operatorData = BeanConverter.convertToOperatorData(domain);
        this.setOperatorTypeCode(operatorData.getOperatorTypeCode());
        this.setOperatorId(operatorData.getOperatorId());
        this.setOperatorData(operatorData);
    }
}
