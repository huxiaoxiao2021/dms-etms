package com.jd.bluedragon.distribution.operateMonitor.service.impl;

import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.operateMonitor.domain.OperateMonitor;
import com.jd.bluedragon.distribution.operateMonitor.service.OperateMonitorService;
import com.jd.bluedragon.distribution.receive.service.ReceiveService;
import com.jd.bluedragon.distribution.send.dao.SendDatailReadDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 分拣中心操作监控serviceImpl
 */
@Service("operateMonitorService")
public class OperateMonitorServiceImpl implements OperateMonitorService {

    @Autowired
    private KvIndexDao kvIndexDao;

    @Autowired
    ReceiveService receiveService;

    @Autowired
    private InspectionDao inspectionDao;

    @Autowired
    private SortingDao sortingDao;

    @Autowired
    private SendDatailReadDao sendDatailReadDao;

    /**
     * 根据包裹号查询包裹的相关数据
     * @param packageCode
     * @return
     */
    @Override
    public List<OperateMonitor> queryOperateMonitorByPackageCode(String packageCode) {

        List<OperateMonitor> result = new ArrayList<OperateMonitor>();

        List<Integer> createSites =  getCreateSitesByPackageCode(packageCode);

        List<Inspection> inspections = getInspectionByPackageCode(packageCode, createSites);
        List<Sorting> sortings = getSortingByPackageCode(packageCode, createSites);
        List<SendDetail> sendDetails = getSendDetailByPackageCode(packageCode, createSites);

        result.addAll(convertInspection2OperateMonitor(inspections));
        result.addAll(convertSorting2OperateMonitor(sortings));
        result.addAll(convertSendDetail2OperateMonitor(sendDetails));

        sortOperateMonitors(result);

        return result;
    }

    /**
     * 根据包裹号和操作站点查询验货数据
     * @param packageCode
     * @param createSites
     * @return
     */
    private List<Inspection> getInspectionByPackageCode(String packageCode, List<Integer> createSites){
        List<Inspection> result = new ArrayList<Inspection>();
        Inspection condition = new Inspection();
        condition.setPackageBarcode(packageCode);

        for (Integer createSite : createSites) {
            condition.setCreateSiteCode(createSite);
            result.addAll(inspectionDao.queryByCondition(condition));
        }

        return result;
    }

    /**
     * 根据包裹号和操作站点查询分拣数据
     * @param packageCode
     * @param createSites
     * @return
     */
    private List<Sorting> getSortingByPackageCode(String packageCode, List<Integer> createSites){
        List<Sorting> result = new ArrayList<Sorting>();
        Sorting condition = new Sorting();
        condition.setPackageCode(packageCode);

        for (Integer createSite : createSites) {
            condition.setCreateSiteCode(createSite);
            result.addAll(sortingDao.findByPackageCode(condition));
        }

        return result;
    }

    /**
     * 根据包裹号和操作站点查询发货明细数据
     * @param packageCode
     * @param createSites
     * @return
     */
    private List<SendDetail> getSendDetailByPackageCode(String packageCode, List<Integer> createSites){
        List<SendDetail> result = new ArrayList<SendDetail>();
        SendDetail condition = new SendDetail();
        condition.setPackageBarcode(packageCode);

        for (Integer createSite : createSites) {
            condition.setCreateSiteCode(createSite);
            result.addAll(sendDatailReadDao.findSendByPackageCode(packageCode, createSite));
        }

        return result;
    }

    /**
     * 根据包裹号查询拆分键
     * @param packageCode
     * @return
     */
    private List<Integer> getCreateSitesByPackageCode(String packageCode){
        return kvIndexDao.queryCreateSiteCodesByKey(packageCode);
    }

    /**
     * 数据转换：验货数据转为OperateMonitor
     * @param inspections
     * @return
     */
    private List<OperateMonitor> convertInspection2OperateMonitor(List<Inspection> inspections){
        List<OperateMonitor> result = new ArrayList<OperateMonitor>();
        for (Inspection inspection : inspections){
            OperateMonitor om = new OperateMonitor();
            om.setPackageCode(inspection.getPackageBarcode());
            om.setWaybillCode(inspection.getWaybillCode());
            om.setBoxCode(inspection.getBoxCode());
            om.setCreateSiteCode(inspection.getReceiveSiteCode());
            om.setReceiveSiteCode(inspection.getReceiveSiteCode());
            om.setCreateUser(inspection.getCreateUser());
            om.setOperateTime(inspection.getOperateTime());
            om.setCreateTime(inspection.getCreateTime());
            om.setStatus(inspection.getStatus());
            om.setOpreteType(MONITOR_TYPE_INSPECTION);
            result.add(om);
        }
        return result;
    }

    /**
     * 数据转换：分拣数据转为OperateMonitor
     * @param sortings
     * @return
     */
    private List<OperateMonitor> convertSorting2OperateMonitor(List<Sorting> sortings){
        List<OperateMonitor> result = new ArrayList<OperateMonitor>();
        for (Sorting sorting : sortings){
            OperateMonitor om = new OperateMonitor();
            om.setPackageCode(sorting.getPackageCode());
            om.setWaybillCode(sorting.getWaybillCode());
            om.setBoxCode(sorting.getBoxCode());
            om.setCreateSiteCode(sorting.getReceiveSiteCode());
            om.setReceiveSiteCode(sorting.getReceiveSiteCode());
            om.setCreateUser(sorting.getCreateUser());
            om.setOperateTime(sorting.getOperateTime());
            om.setCreateTime(sorting.getCreateTime());
            om.setStatus(sorting.getStatus());
            om.setIsCancel(sorting.getIsCancel());
            om.setOpreteType(MONITOR_TYPE_SORTING);
            result.add(om);
        }
        return result;
    }

    /**
     * 数据转换：发货数据转为OperateMonitor
     * @param sendDetails
     * @return
     */
    private List<OperateMonitor> convertSendDetail2OperateMonitor(List<SendDetail> sendDetails){
        List<OperateMonitor> result = new ArrayList<OperateMonitor>();
        for (SendDetail sendDetail : sendDetails){
            OperateMonitor om = new OperateMonitor();
            om.setPackageCode(sendDetail.getPackageBarcode());
            om.setWaybillCode(sendDetail.getWaybillCode());
            om.setBoxCode(sendDetail.getBoxCode());
            om.setSendCode(sendDetail.getSendCode());
            om.setCreateSiteCode(sendDetail.getReceiveSiteCode());
            om.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
            om.setCreateUser(sendDetail.getCreateUser());
            om.setOperateTime(sendDetail.getOperateTime());
            om.setCreateTime(sendDetail.getCreateTime());
            om.setStatus(sendDetail.getStatus());
            om.setIsCancel(sendDetail.getIsCancel());
            om.setOpreteType(MONITOR_TYPE_SEND);
            result.add(om);
        }
        return result;
    }

    /**
     * 按实操时间排序
     * @param SealCarDtos
     */
    private void sortOperateMonitors(List<OperateMonitor> data){
        Collections.sort(data, new Comparator<OperateMonitor>() {
            @Override
            public int compare(OperateMonitor dto1, OperateMonitor dto2) {
                return dto1.getOperateTime().compareTo(dto2.getOperateTime());
            }
        });
    }
}
