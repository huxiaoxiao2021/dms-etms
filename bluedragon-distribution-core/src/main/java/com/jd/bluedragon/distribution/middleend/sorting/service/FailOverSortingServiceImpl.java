package com.jd.bluedragon.distribution.middleend.sorting.service;

import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.middleend.sorting.domain.SortingObjectExtend;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("failOverSortingService")
public class FailOverSortingServiceImpl extends BaseSortingService implements ISortingService{
    @Autowired
    private MiddleEndSortingService middleEndSortingService;

    @Autowired
    WaybillPackageManager waybillPackageManager;

    @Autowired
    private SortingDao sortingDao;

    /**
     * 分拣的核心操作：1写sorting表；2发分拣的全称跟踪；3逆向的发送退货一百分和快退MQ
     * 可降级：优先调中台的接口进行操作，中台接口失败会再调分拣中心自己的操作
     * 注意：即使中台成功了，也要写自己的sorting表
     * @param sorting
     * @return
     */
    public boolean coreSorting(SortingObjectExtend sorting){
        try {
            //调中台的service
            if (middleEndSortingService.coreSorting(sorting)) {
                //拆成包裹维度
                saveOrUpdate(sorting.getDmsSorting());
            } else {
                //找到按运单处理的方法
                saveOrUpdate(sorting.getDmsSorting());
                dmsSortingService.addSortingAdditionalTask(sorting.getDmsSorting());
                dmsSortingService.notifyBlocker(sorting.getDmsSorting());
                dmsSortingService.backwardSendMQ(sorting.getDmsSorting());
            }
            return true;
        }catch (Exception e){
            logger.error("FailOverSortingServiceImpl.coreSorting异常.参数:" + JSON.toJSONString(sorting),e);
            return false;
        }
    }

    /**
     * 写分拣中心自己的sorting表
     * 如果是按运单维度分拣的，要拆成包裹维度再写表
     * @param dmsSorting
     */
    private void saveOrUpdate(Sorting dmsSorting) {
        Sorting dmsSortingCopy = new Sorting();
        BeanUtils.copyProperties(dmsSorting,dmsSortingCopy);

        if (StringUtils.isNotBlank(dmsSortingCopy.getPackageCode())) {
            dmsSortingService.fillSortingIfPickup(dmsSortingCopy);
            dmsSortingService.saveOrUpdate(dmsSortingCopy);
        } else {
            //调运单接口获取包裹信息，转换成包裹维度
             String waybillCode = dmsSortingCopy.getWaybillCode();
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillE(false);
            wChoice.setQueryWaybillM(false);
            wChoice.setQueryPackList(true);


            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                List<DeliveryPackageD> packageList = baseEntity.getData().getPackageList();
                for (DeliveryPackageD deliveryPackageD : packageList) {
                    dmsSortingCopy.setPackageCode(deliveryPackageD.getPackageBarcode());
                    dmsSortingService.fillSortingIfPickup(dmsSortingCopy);
                    dmsSortingService.saveOrUpdate(dmsSortingCopy);
                }
            }
        }
    }

    /**
     * 取消分拣
     * @param sorting
     */
    public SortingResponse cancelSorting(Sorting sorting){
        //调中台取消分拣的接口，
        SortingResponse response = middleEndSortingService.cancelSorting(sorting);
        if (SortingResponse.CODE_OK.equals(response.getCode())) {
            //成功需要更新自己的sorting表
            afterSortingCancel(sorting);
        }else{
            //调分拣中心自己的接口
            return dmsSortingService.doCancelSorting(sorting);
        }
        return SortingResponse.ok();
    }


    /**
     * 取消分拣成功后的操作
     * @param dmsSorting
     */
    private void afterSortingCancel(Sorting dmsSorting){
        List<Sorting> dmsSortingList = new ArrayList<>();
        if (StringUtils.isNotBlank(dmsSorting.getBoxCode())) {
            dmsSortingList.addAll(dmsSortingService.findByBoxCode(dmsSorting));
        } else {
            dmsSortingList.add(dmsSorting);
        }

        for(Sorting dmsSortingItem : dmsSortingList){
            dmsSortingItem.setOperateTime(dmsSorting.getOperateTime());
            dmsSortingItem.setUpdateUserCode(dmsSorting.getUpdateUserCode());
            dmsSortingItem.setUpdateUser(dmsSorting.getUpdateUser());

            sortingDao.canCancel2(dmsSortingItem);
        }
    }
}
