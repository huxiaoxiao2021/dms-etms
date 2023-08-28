package com.jd.bluedragon.distribution.external.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.bagException.dao.CollectionBagExceptionReportDao;
import com.jd.bluedragon.distribution.bagException.domain.CollectionBagExceptionReport;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao.DiscardedPackageStorageTempDao;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao.DiscardedWaybillStorageTempDao;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedPackageStorageTemp;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedWaybillStorageTemp;
import com.jd.bluedragon.distribution.exceptionReport.billException.dao.ExpressBillExceptionReportDao;
import com.jd.bluedragon.distribution.exceptionReport.billException.domain.ExpressBillExceptionReport;
import com.jd.bluedragon.distribution.external.service.OrgSwitchProvinceBrushJsfService;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigDto;
import com.jd.bluedragon.distribution.funcSwitchConfig.dao.FuncSwitchConfigDao;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.dao.MerchantWeightAndVolumeWhiteListDao;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeDetail;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jdl.basic.common.contants.Constants;
import com.jdl.basic.common.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 大区切换省区刷数接口
 *
 * @author hujiping
 * @date 2023/8/28 10:31 AM
 */
@Service("orgSwitchProvinceBrushJsfService")
public class OrgSwitchProvinceBrushJsfServiceImpl implements OrgSwitchProvinceBrushJsfService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private MerchantWeightAndVolumeWhiteListDao merchantWeightAndVolumeWhiteListDao;

    @Autowired
    private FuncSwitchConfigDao funcSwitchConfigDao;

    @Autowired
    private ExpressBillExceptionReportDao expressBillExceptionReportDao;

    @Autowired
    private DiscardedWaybillStorageTempDao discardedWaybillStorageTempDao;

    @Autowired
    private DiscardedPackageStorageTempDao discardedPackageStorageTempDao;

    @Autowired
    private CollectionBagExceptionReportDao collectionBagExceptionReportDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public void merchantWeightWhiteBrush(Integer startId) {
        // 起始id
        int offsetId = startId;
        int updatedCount = 0; // 更新数量
        int loopCount = 0; // 循环次数
        while (loopCount < 1000) {
            List<MerchantWeightAndVolumeDetail> singleList = merchantWeightAndVolumeWhiteListDao.brushQueryAllByPage(offsetId);
            if(CollectionUtils.isEmpty(singleList)){
                break;
            }
            List<MerchantWeightAndVolumeDetail> list = Lists.newArrayList();
            for (MerchantWeightAndVolumeDetail item : singleList) {
                if(item.getOperateSiteCode() == null){
                    continue;
                }
                BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(item.getOperateSiteCode());
                if(baseSite == null){
                    continue;
                }
                MerchantWeightAndVolumeDetail updateItem = new MerchantWeightAndVolumeDetail();
                updateItem.setId(item.getId());
                updateItem.setOperateProvinceAgencyCode(StringUtils.isEmpty(baseSite.getProvinceAgencyCode()) ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyCode() );
                updateItem.setOperateProvinceAgencyName(StringUtils.isEmpty(baseSite.getProvinceAgencyName()) ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyName());
                updateItem.setOperateAreaHubCode(StringUtils.isEmpty(baseSite.getAreaCode()) ? Constants.EMPTY_FILL : baseSite.getAreaCode());
                updateItem.setOperateAreaHubName(StringUtils.isEmpty(baseSite.getAreaName()) ? Constants.EMPTY_FILL : baseSite.getAreaName());
                list.add(updateItem);
            }

            MerchantWeightAndVolumeDetail merchantWeightAndVolumeDetail = singleList.stream().max((a, b) -> (int) (a.getId() - b.getId())).get();
            offsetId = merchantWeightAndVolumeDetail.getId().intValue();

            if(CollectionUtils.isEmpty(list)){
                continue;
            }
            int singleCount = merchantWeightAndVolumeWhiteListDao.brushUpdateById(list);
            updatedCount += singleCount;

            loopCount ++;
        }
        if(logger.isInfoEnabled()){
            logger.info("merchant_weight_white_list 表省区刷数:{}", updatedCount);
        }
    }

    @Override
    public void funcSwitchConfigBrush(Integer startId) {
        // 起始id
        int offsetId = startId;
        int updatedCount = 0; // 更新数量
        int loopCount = 0; // 循环次数
        while (loopCount < 1000) {
            List<FuncSwitchConfigDto> singleList = funcSwitchConfigDao.brushQueryAllByPage(offsetId);
            if(CollectionUtils.isEmpty(singleList)){
                break;
            }
            List<FuncSwitchConfigDto> list = Lists.newArrayList();
            for (FuncSwitchConfigDto item : singleList) {
                if(item.getSiteCode() == null){
                    continue;
                }
                BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(item.getSiteCode());
                if(baseSite == null){
                    continue;
                }
                FuncSwitchConfigDto updateItem = new FuncSwitchConfigDto();
                updateItem.setId(item.getId());
                updateItem.setProvinceAgencyCode(StringUtils.isEmpty(baseSite.getProvinceAgencyCode()) ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyCode() );
                updateItem.setProvinceAgencyName(StringUtils.isEmpty(baseSite.getProvinceAgencyName()) ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyName());
                updateItem.setAreaHubCode(StringUtils.isEmpty(baseSite.getAreaCode()) ? Constants.EMPTY_FILL : baseSite.getAreaCode());
                updateItem.setAreaHubName(StringUtils.isEmpty(baseSite.getAreaName()) ? Constants.EMPTY_FILL : baseSite.getAreaName());
                list.add(updateItem);
            }

            FuncSwitchConfigDto funcSwitchConfigDto = singleList.stream().max((a, b) -> (int) (a.getId() - b.getId())).get();
            offsetId = funcSwitchConfigDto.getId().intValue();

            if(CollectionUtils.isEmpty(list)){
                continue;
            }
            int singleCount = funcSwitchConfigDao.brushUpdateById(list);
            updatedCount += singleCount;

            loopCount ++;
        }
        if(logger.isInfoEnabled()){
            logger.info("func_switch_config 表省区刷数:{}", updatedCount);
        }
    }

    @Override
    public void expressBillExceptionReportBrush(Integer startId) {
        // 起始id
        int offsetId = startId;
        int updatedCount = 0; // 更新数量
        int loopCount = 0; // 循环次数
        while (loopCount < 1000) {
            List<ExpressBillExceptionReport> singleList = expressBillExceptionReportDao.brushQueryAllByPage(offsetId);
            if(CollectionUtils.isEmpty(singleList)){
                break;
            }
            List<ExpressBillExceptionReport> list = Lists.newArrayList();
            for (ExpressBillExceptionReport item : singleList) {
                if(item.getSiteCode() == null){
                    continue;
                }
                BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(item.getSiteCode());
                if(baseSite == null){
                    continue;
                }
                ExpressBillExceptionReport updateItem = new ExpressBillExceptionReport();
                updateItem.setId(item.getId());
                updateItem.setProvinceAgencyCode(StringUtils.isEmpty(baseSite.getProvinceAgencyCode()) ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyCode() );
                updateItem.setProvinceAgencyName(StringUtils.isEmpty(baseSite.getProvinceAgencyName()) ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyName());
                updateItem.setAreaHubCode(StringUtils.isEmpty(baseSite.getAreaCode()) ? Constants.EMPTY_FILL : baseSite.getAreaCode());
                updateItem.setAreaHubName(StringUtils.isEmpty(baseSite.getAreaName()) ? Constants.EMPTY_FILL : baseSite.getAreaName());
                list.add(updateItem);
            }

            ExpressBillExceptionReport expressBillExceptionReport = singleList.stream().max((a, b) -> (int) (a.getId() - b.getId())).get();
            offsetId = expressBillExceptionReport.getId().intValue();

            if(CollectionUtils.isEmpty(list)){
                continue;
            }
            int singleCount = expressBillExceptionReportDao.brushUpdateById(list);
            updatedCount += singleCount;

            loopCount ++;
        }
        if(logger.isInfoEnabled()){
            logger.info("express_bill_exception_report 表省区刷数:{}", updatedCount);
        }
    }

    @Override
    public void discardedWaybillStorageBrush(Integer startId) {
        // 起始id
        int offsetId = startId;
        int updatedCount = 0; // 更新数量
        int loopCount = 0; // 循环次数
        while (loopCount < 1000) {
            List<DiscardedWaybillStorageTemp> singleList = discardedWaybillStorageTempDao.brushQueryAllByPage(offsetId);
            if(CollectionUtils.isEmpty(singleList)){
                break;
            }
            List<DiscardedWaybillStorageTemp> list = Lists.newArrayList();
            for (DiscardedWaybillStorageTemp item : singleList) {
                if(item.getSiteCode() == null){
                    continue;
                }
                BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(item.getSiteCode());
                if(baseSite == null){
                    continue;
                }
                DiscardedWaybillStorageTemp updateItem = new DiscardedWaybillStorageTemp();
                updateItem.setId(item.getId());
                updateItem.setProvinceAgencyCode(StringUtils.isEmpty(baseSite.getProvinceAgencyCode()) ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyCode() );
                updateItem.setProvinceAgencyName(StringUtils.isEmpty(baseSite.getProvinceAgencyName()) ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyName());
                updateItem.setAreaHubCode(StringUtils.isEmpty(baseSite.getAreaCode()) ? Constants.EMPTY_FILL : baseSite.getAreaCode());
                updateItem.setAreaHubName(StringUtils.isEmpty(baseSite.getAreaName()) ? Constants.EMPTY_FILL : baseSite.getAreaName());
                list.add(updateItem);
            }

            DiscardedWaybillStorageTemp discardedWaybillStorageTemp = singleList.stream().max((a, b) -> (int) (a.getId() - b.getId())).get();
            offsetId = discardedWaybillStorageTemp.getId().intValue();

            if(CollectionUtils.isEmpty(list)){
                continue;
            }
            int singleCount = discardedWaybillStorageTempDao.brushUpdateById(list);
            updatedCount += singleCount;

            loopCount ++;
        }
        if(logger.isInfoEnabled()){
            logger.info("discarded_waybill_storage_temp 表省区刷数:{}", updatedCount);
        }
    }

    @Override
    public void discardedPackageStorageBrush(Integer startId) {
        // 起始id
        int offsetId = startId;
        int updatedCount = 0; // 更新数量
        int loopCount = 0; // 循环次数
        while (loopCount < 1000) {
            List<DiscardedPackageStorageTemp> singleList = discardedPackageStorageTempDao.brushQueryAllByPage(offsetId);
            if(CollectionUtils.isEmpty(singleList)){
                break;
            }
            List<DiscardedPackageStorageTemp> list = Lists.newArrayList();
            for (DiscardedPackageStorageTemp item : singleList) {
                if(item.getSiteCode() == null){
                    continue;
                }
                BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(item.getSiteCode());
                if(baseSite == null){
                    continue;
                }
                DiscardedPackageStorageTemp updateItem = new DiscardedPackageStorageTemp();
                updateItem.setId(item.getId());
                updateItem.setProvinceAgencyCode(StringUtils.isEmpty(baseSite.getProvinceAgencyCode()) ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyCode() );
                updateItem.setProvinceAgencyName(StringUtils.isEmpty(baseSite.getProvinceAgencyName()) ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyName());
                updateItem.setAreaHubCode(StringUtils.isEmpty(baseSite.getAreaCode()) ? Constants.EMPTY_FILL : baseSite.getAreaCode());
                updateItem.setAreaHubName(StringUtils.isEmpty(baseSite.getAreaName()) ? Constants.EMPTY_FILL : baseSite.getAreaName());
                list.add(updateItem);
            }

            DiscardedPackageStorageTemp discardedPackageStorageTemp = singleList.stream().max((a, b) -> (int) (a.getId() - b.getId())).get();
            offsetId = discardedPackageStorageTemp.getId().intValue();

            if(CollectionUtils.isEmpty(list)){
                continue;
            }
            int singleCount = discardedPackageStorageTempDao.brushUpdateById(list);
            updatedCount += singleCount;

            loopCount ++;
        }
        if(logger.isInfoEnabled()){
            logger.info("discarded_package_storage_temp 表省区刷数:{}", updatedCount);
        }
    }

    @Override
    public void collectionBagExceptionBrush(Integer startId) {
        // 起始id
        int offsetId = startId;
        int updatedCount = 0; // 更新数量
        int loopCount = 0; // 循环次数
        while (loopCount < 1000) {
            List<CollectionBagExceptionReport> singleList = collectionBagExceptionReportDao.brushQueryAllByPage(offsetId);
            if(CollectionUtils.isEmpty(singleList)){
                break;
            }
            List<CollectionBagExceptionReport> list = Lists.newArrayList();
            for (CollectionBagExceptionReport item : singleList) {
                if(item.getSiteCode() == null){
                    continue;
                }
                BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(item.getSiteCode());
                if(baseSite == null){
                    continue;
                }
                CollectionBagExceptionReport updateItem = new CollectionBagExceptionReport();
                updateItem.setId(item.getId());
                updateItem.setProvinceAgencyCode(StringUtils.isEmpty(baseSite.getProvinceAgencyCode()) ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyCode() );
                updateItem.setProvinceAgencyName(StringUtils.isEmpty(baseSite.getProvinceAgencyName()) ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyName());
                updateItem.setAreaHubCode(StringUtils.isEmpty(baseSite.getAreaCode()) ? Constants.EMPTY_FILL : baseSite.getAreaCode());
                updateItem.setAreaHubName(StringUtils.isEmpty(baseSite.getAreaName()) ? Constants.EMPTY_FILL : baseSite.getAreaName());
                list.add(updateItem);
            }

            CollectionBagExceptionReport collectionBagExceptionReport = singleList.stream().max((a, b) -> (int) (a.getId() - b.getId())).get();
            offsetId = collectionBagExceptionReport.getId().intValue();

            if(CollectionUtils.isEmpty(list)){
                continue;
            }
            int singleCount = collectionBagExceptionReportDao.brushUpdateById(list);
            updatedCount += singleCount;

            loopCount ++;
        }
        if(logger.isInfoEnabled()){
            logger.info("collection_bag_exception_report 表省区刷数:{}", updatedCount);
        }
    }
}
