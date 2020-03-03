package com.jd.bluedragon.distribution.sms.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.sms.domain.SMSDto;
import com.jd.bluedragon.distribution.sms.service.SmsConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 短信配置实现
 *
 * @author: hujiping
 * @date: 2020/2/26 16:34
 */
public class SmsConfigServiceServiceImpl implements SmsConfigService {

    private static final Logger log = LoggerFactory.getLogger(SmsConfigServiceServiceImpl.class);

    /**
     * 冷链卡班根据区域获取短信配置对象
     * @param orgId 区域ID
     * @return
     */
    @Override
    public SMSDto getSMSConstantsByOrgId(Integer orgId) {
        switch (orgId){
            case Constants.EAST_CHINA_ORG_ID:
                return eastChinaColdChainSMSDto;
            case Constants.SOUTH_WEST_ORG_ID:
                return southWestColdChainSMSDto;
            case Constants.NORTH_CHINA_ORG_ID:
                return northChinaColdChainSMSDto;
            case Constants.SOUTH_CHINA_ORG_ID:
                return southChinaColdChainSMSDto;
            case Constants.CENTRAL_CHINA_ORG_ID:
                return centralChinaColdChainSMSDto;
            case Constants.NORTH_EAST_ORG_ID:
                return northEastColdChainSMSDto;
            case Constants.NORTH_WEST_ORG_ID:
                return northWestColdChainSMSDto;
            default:
                log.warn("区域ID:{}不属于七大区",orgId);
                return null;
        }
    }

    private SMSDto eastChinaColdChainSMSDto;
    private SMSDto southWestColdChainSMSDto;
    private SMSDto northChinaColdChainSMSDto;
    private SMSDto southChinaColdChainSMSDto;
    private SMSDto centralChinaColdChainSMSDto;
    private SMSDto northEastColdChainSMSDto;
    private SMSDto northWestColdChainSMSDto;

    public SMSDto getEastChinaColdChainSMSDto() {
        return eastChinaColdChainSMSDto;
    }

    public void setEastChinaColdChainSMSDto(SMSDto eastChinaColdChainSMSDto) {
        this.eastChinaColdChainSMSDto = eastChinaColdChainSMSDto;
    }

    public SMSDto getSouthWestColdChainSMSDto() {
        return southWestColdChainSMSDto;
    }

    public void setSouthWestColdChainSMSDto(SMSDto southWestColdChainSMSDto) {
        this.southWestColdChainSMSDto = southWestColdChainSMSDto;
    }

    public SMSDto getNorthChinaColdChainSMSDto() {
        return northChinaColdChainSMSDto;
    }

    public void setNorthChinaColdChainSMSDto(SMSDto northChinaColdChainSMSDto) {
        this.northChinaColdChainSMSDto = northChinaColdChainSMSDto;
    }

    public SMSDto getSouthChinaColdChainSMSDto() {
        return southChinaColdChainSMSDto;
    }

    public void setSouthChinaColdChainSMSDto(SMSDto southChinaColdChainSMSDto) {
        this.southChinaColdChainSMSDto = southChinaColdChainSMSDto;
    }

    public SMSDto getCentralChinaColdChainSMSDto() {
        return centralChinaColdChainSMSDto;
    }

    public void setCentralChinaColdChainSMSDto(SMSDto centralChinaColdChainSMSDto) {
        this.centralChinaColdChainSMSDto = centralChinaColdChainSMSDto;
    }

    public SMSDto getNorthEastColdChainSMSDto() {
        return northEastColdChainSMSDto;
    }

    public void setNorthEastColdChainSMSDto(SMSDto northEastColdChainSMSDto) {
        this.northEastColdChainSMSDto = northEastColdChainSMSDto;
    }

    public SMSDto getNorthWestColdChainSMSDto() {
        return northWestColdChainSMSDto;
    }

    public void setNorthWestColdChainSMSDto(SMSDto northWestColdChainSMSDto) {
        this.northWestColdChainSMSDto = northWestColdChainSMSDto;
    }
}
