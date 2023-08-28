package com.jd.bluedragon.distribution.external.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.bagException.dao.CollectionBagExceptionReportDao;
import com.jd.bluedragon.distribution.bagException.domain.CollectionBagExceptionReport;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao.DiscardedPackageStorageTempDao;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao.DiscardedWaybillStorageTempDao;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedPackageStorageTemp;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedWaybillStorageTemp;
import com.jd.bluedragon.distribution.dock.dao.DockBaseInfoDao;
import com.jd.bluedragon.distribution.dock.domain.DockBaseInfoPo;
import com.jd.bluedragon.distribution.exceptionReport.billException.dao.ExpressBillExceptionReportDao;
import com.jd.bluedragon.distribution.exceptionReport.billException.domain.ExpressBillExceptionReport;
import com.jd.bluedragon.distribution.external.service.OrgSwitchProvinceBrushJsfService;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigDto;
import com.jd.bluedragon.distribution.funcSwitchConfig.dao.FuncSwitchConfigDao;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.dao.MerchantWeightAndVolumeWhiteListDao;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeDetail;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
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
    private DockBaseInfoDao dockBaseInfoDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public void merchantWeightWhiteBrush(Integer startId) {
        // 起始id
        int offsetId = startId;
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
                if(StringUtils.isEmpty(baseSite.getProvinceAgencyCode()) && StringUtils.isEmpty(baseSite.getAreaCode())){
                    continue;
                }
                MerchantWeightAndVolumeDetail updateItem = new MerchantWeightAndVolumeDetail();
                updateItem.setId(item.getId());
                updateItem.setOperateProvinceAgencyCode(baseSite.getProvinceAgencyCode());
                updateItem.setOperateProvinceAgencyName(baseSite.getProvinceAgencyName());
                updateItem.setOperateAreaHubCode(baseSite.getAreaCode());
                updateItem.setOperateAreaHubName(baseSite.getAreaName());
                list.add(updateItem);
            }

            MerchantWeightAndVolumeDetail merchantWeightAndVolumeDetail = singleList.stream().max((a, b) -> (int) (a.getId() - b.getId())).get();
            offsetId = merchantWeightAndVolumeDetail.getId().intValue();

            if(CollectionUtils.isEmpty(list)){
                continue;
            }
            list.forEach(item -> {
                merchantWeightAndVolumeWhiteListDao.brushUpdateById(item);
            });

            loopCount ++;
        }
    }

    @Override
    public void funcSwitchConfigBrush(Integer startId) {
        // 起始id
        int offsetId = startId;
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
                if(StringUtils.isEmpty(baseSite.getProvinceAgencyCode()) && StringUtils.isEmpty(baseSite.getAreaCode())){
                    continue;
                }
                FuncSwitchConfigDto updateItem = new FuncSwitchConfigDto();
                updateItem.setId(item.getId());
                updateItem.setProvinceAgencyCode(baseSite.getProvinceAgencyCode());
                updateItem.setProvinceAgencyName(baseSite.getProvinceAgencyName());
                updateItem.setAreaHubCode(baseSite.getAreaCode());
                updateItem.setAreaHubName(baseSite.getAreaName());
                list.add(updateItem);
            }

            FuncSwitchConfigDto funcSwitchConfigDto = singleList.stream().max((a, b) -> (int) (a.getId() - b.getId())).get();
            offsetId = funcSwitchConfigDto.getId().intValue();

            if(CollectionUtils.isEmpty(list)){
                continue;
            }
            list.forEach(item -> {
                funcSwitchConfigDao.brushUpdateById(item);
            });

            loopCount ++;
        }
    }

    @Override
    public void expressBillExceptionReportBrush(Integer startId) {
        // 起始id
        int offsetId = startId;
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
                if(StringUtils.isEmpty(baseSite.getProvinceAgencyCode()) && StringUtils.isEmpty(baseSite.getAreaCode())){
                    continue;
                }
                ExpressBillExceptionReport updateItem = new ExpressBillExceptionReport();
                updateItem.setId(item.getId());
                updateItem.setProvinceAgencyCode(baseSite.getProvinceAgencyCode());
                updateItem.setProvinceAgencyName(baseSite.getProvinceAgencyName());
                updateItem.setAreaHubCode(baseSite.getAreaCode());
                updateItem.setAreaHubName(baseSite.getAreaName());
                list.add(updateItem);
            }

            ExpressBillExceptionReport expressBillExceptionReport = singleList.stream().max((a, b) -> (int) (a.getId() - b.getId())).get();
            offsetId = expressBillExceptionReport.getId().intValue();

            if(CollectionUtils.isEmpty(list)){
                continue;
            }
            list.forEach(item -> {
                expressBillExceptionReportDao.brushUpdateById(item);
            });

            loopCount ++;
        }
    }

    @Override
    public void discardedWaybillStorageBrush(Integer startId) {
        // 起始id
        int offsetId = startId;
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
                if(StringUtils.isEmpty(baseSite.getProvinceAgencyCode()) && StringUtils.isEmpty(baseSite.getAreaCode())){
                    continue;
                }
                DiscardedWaybillStorageTemp updateItem = new DiscardedWaybillStorageTemp();
                updateItem.setId(item.getId());
                updateItem.setProvinceAgencyCode(baseSite.getProvinceAgencyCode());
                updateItem.setProvinceAgencyName(baseSite.getProvinceAgencyName());
                updateItem.setAreaHubCode(baseSite.getAreaCode());
                updateItem.setAreaHubName(baseSite.getAreaName());
                list.add(updateItem);
            }

            DiscardedWaybillStorageTemp discardedWaybillStorageTemp = singleList.stream().max((a, b) -> (int) (a.getId() - b.getId())).get();
            offsetId = discardedWaybillStorageTemp.getId().intValue();

            if(CollectionUtils.isEmpty(list)){
                continue;
            }
            list.forEach(item -> {
                discardedWaybillStorageTempDao.brushUpdateById(item);
            });

            loopCount ++;
        }
    }

    @Override
    public void discardedPackageStorageBrush(Integer startId) {
        // 起始id
        int offsetId = startId;
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
                if(StringUtils.isEmpty(baseSite.getProvinceAgencyCode()) && StringUtils.isEmpty(baseSite.getAreaCode())){
                    continue;
                }
                DiscardedPackageStorageTemp updateItem = new DiscardedPackageStorageTemp();
                updateItem.setId(item.getId());
                updateItem.setProvinceAgencyCode(baseSite.getProvinceAgencyCode());
                updateItem.setProvinceAgencyName(baseSite.getProvinceAgencyName());
                updateItem.setAreaHubCode(baseSite.getAreaCode());
                updateItem.setAreaHubName(baseSite.getAreaName());
                list.add(updateItem);
            }

            DiscardedPackageStorageTemp discardedPackageStorageTemp = singleList.stream().max((a, b) -> (int) (a.getId() - b.getId())).get();
            offsetId = discardedPackageStorageTemp.getId().intValue();

            if(CollectionUtils.isEmpty(list)){
                continue;
            }
            list.forEach(item -> {
                discardedPackageStorageTempDao.brushUpdateById(item);
            });

            loopCount ++;
        }
    }

    @Override
    public void collectionBagExceptionBrush(Integer startId) {
        // 起始id
        int offsetId = startId;
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
                if(StringUtils.isEmpty(baseSite.getProvinceAgencyCode()) && StringUtils.isEmpty(baseSite.getAreaCode())){
                    continue;
                }
                CollectionBagExceptionReport updateItem = new CollectionBagExceptionReport();
                updateItem.setId(item.getId());
                updateItem.setProvinceAgencyCode(baseSite.getProvinceAgencyCode());
                updateItem.setProvinceAgencyName(baseSite.getProvinceAgencyName());
                updateItem.setAreaHubCode(baseSite.getAreaCode());
                updateItem.setAreaHubName(baseSite.getAreaName());
                list.add(updateItem);
            }

            CollectionBagExceptionReport collectionBagExceptionReport = singleList.stream().max((a, b) -> (int) (a.getId() - b.getId())).get();
            offsetId = collectionBagExceptionReport.getId().intValue();

            if(CollectionUtils.isEmpty(list)){
                continue;
            }
            list.forEach(item -> {
                collectionBagExceptionReportDao.brushUpdateById(item);
            });
            
            loopCount ++;
        }
    }

    @Override
    public void dockBaseInfoBrush(Integer startId) {
        // 起始id
        int offsetId = startId;
        int loopCount = 0; // 循环次数
        while (loopCount < 1000) {
            List<DockBaseInfoPo> singleList = dockBaseInfoDao.brushQueryAllByPage(offsetId);
            if(CollectionUtils.isEmpty(singleList)){
                break;
            }
            List<DockBaseInfoPo> list = Lists.newArrayList();
            for (DockBaseInfoPo item : singleList) {
                if(item.getSiteCode() == null){
                    continue;
                }
                BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(item.getSiteCode());
                if(baseSite == null){
                    continue;
                }
                if(StringUtils.isEmpty(baseSite.getProvinceAgencyCode()) && StringUtils.isEmpty(baseSite.getAreaCode())){
                    continue;
                }
                DockBaseInfoPo updateItem = new DockBaseInfoPo();
                updateItem.setId(item.getId());
                updateItem.setProvinceAgencyCode(baseSite.getProvinceAgencyCode());
                updateItem.setProvinceAgencyName(baseSite.getProvinceAgencyName());
                updateItem.setAreaHubCode(baseSite.getAreaCode());
                updateItem.setAreaHubName(baseSite.getAreaName());
                list.add(updateItem);
            }

            DockBaseInfoPo dockBaseInfoPo = singleList.stream().max((a, b) -> (int) (a.getId() - b.getId())).get();
            offsetId = dockBaseInfoPo.getId().intValue();

            if(CollectionUtils.isEmpty(list)){
                continue;
            }
            list.forEach(item -> {
                dockBaseInfoDao.brushUpdateById(item);
            });

            loopCount ++;
        }
    }
}
