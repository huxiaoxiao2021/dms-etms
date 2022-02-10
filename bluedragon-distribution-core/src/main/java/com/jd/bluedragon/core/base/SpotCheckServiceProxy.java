package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDetailMQ;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 抽检实现代理
 *
 * @author hujiping
 * @date 2022/2/10 2:25 PM
 */
@Service
public class SpotCheckServiceProxy {

    @Autowired
    private ReportExternalManager reportExternalManager;

    @Autowired
    @Qualifier("spotCheckDetailProducer")
    private DefaultJMQProducer spotCheckDetailProducer;

    /**
     * 新增或修改代理
     *  1、新增或修改 2、对外发抽检明细MQ
     * @param dto
     */
    public void insertOrUpdateProxyPrevious(WeightVolumeCollectDto dto){
        Boolean isSuccess = reportExternalManager.insertOrUpdateForWeightVolume(dto);
        if(isSuccess){
            SpotCheckDetailMQ spotCheckDetailMQ = new SpotCheckDetailMQ();
            spotCheckDetailMQ.setReviewDate(dto.getReviewDate() == null ? null : dto.getReviewDate().getTime());
            spotCheckDetailMQ.setWaybillCode(dto.getWaybillCode());
            spotCheckDetailMQ.setPackageCode(dto.getPackageCode());
            spotCheckDetailMQ.setMerchantCode(dto.getMerchantCode());
            spotCheckDetailMQ.setMerchantName(dto.getBusiName());
            spotCheckDetailMQ.setIsTrustMerchant(dto.getIsTrustBusi());
            spotCheckDetailMQ.setVolumeRate(dto.getVolumeRate());
            spotCheckDetailMQ.setReviewSource(SpotCheckSourceFromEnum.analysisCodeFromName(dto.getFromSource()));
            spotCheckDetailMQ.setReviewOrgCode(dto.getReviewOrgCode());
            spotCheckDetailMQ.setReviewOrgName(dto.getReviewOrgName());
            spotCheckDetailMQ.setReviewSiteCode(dto.getReviewSiteCode());
            spotCheckDetailMQ.setReviewSiteName(dto.getReviewSiteName());
            spotCheckDetailMQ.setReviewUserErp(dto.getReviewErp());
            spotCheckDetailMQ.setReviewWeight(dto.getReviewWeight());
            spotCheckDetailMQ.setReviewVolume(dto.getReviewVolume());
            spotCheckDetailMQ.setContrastSource(dto.getContrastSourceFrom());
            spotCheckDetailMQ.setContrastOrgCode(dto.getBillingOrgCode());
            spotCheckDetailMQ.setContrastOrgName(dto.getBillingOrgName());
            spotCheckDetailMQ.setContrastAreaCode(dto.getBillingDeptCodeStr());
            spotCheckDetailMQ.setContrastAreaName(dto.getBillingDeptName());
            spotCheckDetailMQ.setContrastSiteCode(dto.getBillingCompanyCode());
            spotCheckDetailMQ.setContrastSiteName(dto.getBillingCompany());
            spotCheckDetailMQ.setContrastStaffAccount(dto.getBillingErp());
            spotCheckDetailMQ.setContrastDutyType(dto.getDutyType());
            spotCheckDetailMQ.setDiffWeight(dto.getWeightDiff() == null ? null : Double.parseDouble(dto.getWeightDiff()));
            spotCheckDetailMQ.setDiffStandard(dto.getDiffStandard());
            spotCheckDetailMQ.setIsHasPicture(dto.getIsHasPicture());
            spotCheckDetailMQ.setPictureAddress(dto.getPictureAddress());
            spotCheckDetailMQ.setBusinessType(dto.getSpotCheckType());
            spotCheckDetailMQ.setDimensionType(dto.getIsWaybillSpotCheck());
            spotCheckDetailMQ.setRecordType(dto.getRecordType());
            spotCheckDetailMQ.setIsMultiPack(dto.getMultiplePackage());
            spotCheckDetailMQ.setIsGatherTogether(dto.getFullCollect());
            spotCheckDetailMQ.setIsExcess(dto.getIsExcess());
            spotCheckDetailMQ.setIsIssueDownstream(dto.getIssueDownstream());
            spotCheckDetailMQ.setUpdateTime(System.currentTimeMillis());
            spotCheckDetailMQ.setTs(System.currentTimeMillis());
            spotCheckDetailMQ.setYn(Constants.YN_YES);
            spotCheckDetailProducer.sendOnFailPersistent(spotCheckDetailMQ.getWaybillCode(), JsonHelper.toJson(spotCheckDetailMQ));
        }
    }
}
