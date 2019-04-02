package com.jd.bluedragon.distribution.send.domain;

/**
 * 发货任务
 * Created by wangtingwei on 2015/3/20.
 */
public class SendTaskBody extends SendM{

    /**
     * 任务处理类别【1：按批次号；2：按箱号】
     */
    private Integer handleCategory;

    public Integer getHandleCategory() {
        return handleCategory;
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
    }
}
