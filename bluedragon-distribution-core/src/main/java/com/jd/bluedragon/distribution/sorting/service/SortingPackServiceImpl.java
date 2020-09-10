package com.jd.bluedragon.distribution.sorting.service;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.inspection.dao.InspectionECDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.domain.SortingVO;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 包裹分拣业务类
 */
@Service("sortingPackService")
public class SortingPackServiceImpl extends SortingCommonSerivce{




    @Override
    public boolean doSorting(SortingVO sorting) {
        if(sorting.getIsCancel().equals(SortingService.SORTING_CANCEL_NORMAL)){
            //分拣
            getSortingService().fillSortingIfPickup(sorting);

            saveOrUpdate(sorting);

            saveOrUpdateInspectionEC(sorting);

            List<SendDetail> sendDList = new ArrayList<>();

            sendDList.add(getSortingService().addSendDetail(sorting));
            //补发货
            getSortingService().fixSendDAndSendTrack(sorting, sendDList);

        }else if (sorting.getIsCancel().equals(SortingService.SORTING_CANCEL)) {
            // 取消分拣
            getSortingService().canCancel(sorting);
        }
        return true;
    }

    private void saveOrUpdate(Sorting sorting) {
        if (Constants.NO_MATCH_DATA == getSortingService().update(sorting).intValue()) {
            getSortingService().add(sorting);
        }
    }

    /**
     * 验货异常比对表插入数据
     *
     * @param sorting
     */
    private void saveOrUpdateInspectionEC(Sorting sorting) {//FIXME:包装构建方法

        if (Constants.BUSSINESS_TYPE_THIRD_PARTY != sorting.getType()) {
            return;
        }

        InspectionEC inspectionECSel = new InspectionEC.Builder(sorting.getPackageCode(), sorting.getCreateSiteCode())
                .boxCode(sorting.getBoxCode()).receiveSiteCode(sorting.getReceiveSiteCode())
                .inspectionType(sorting.getType()).yn(1).build();
        List<InspectionEC> preInspectionEC = this.inspectionECDao.selectSelective(inspectionECSel);
        if (!preInspectionEC.isEmpty()
                && InspectionEC.INSPECTION_EXCEPTION_STATUS_HANDLED <= preInspectionEC.get(0).getStatus()) {
            this.log.info("包裹已经异常比较，再次分拣时不操作三方异常比对记录，包裹号：{}", sorting.getPackageCode());
            return;
        }

        InspectionEC inspectionEC = new InspectionEC.Builder(sorting.getPackageCode(), sorting.getCreateSiteCode())
                .boxCode(sorting.getBoxCode()).receiveSiteCode(sorting.getReceiveSiteCode())
                .waybillCode(sorting.getWaybillCode()).inspectionECType(InspectionEC.INSPECTIONEC_TYPE_MORE)
                .status(InspectionEC.INSPECTION_EXCEPTION_STATUS_HANDLED).inspectionType(sorting.getType())
                .createUser(sorting.getUpdateUser()).createUserCode(sorting.getUpdateUserCode())
                .createTime(sorting.getUpdateTime()).updateUser(sorting.getUpdateUser())
                .updateUserCode(sorting.getUpdateUserCode())
                .updateTime(null == sorting.getUpdateTime() ? new Date() : sorting.getUpdateTime()).build();

        if (!preInspectionEC.isEmpty()
                && preInspectionEC.get(0).getInspectionECType() == InspectionEC.INSPECTIONEC_TYPE_MORE) {
            this.inspectionECDao.updateOne(inspectionEC);
        } else if (preInspectionEC.isEmpty()) {
            // insert表示该记录还不存在，验货时没有插入，在此验货异常类型为少验
            inspectionEC.setInspectionECType(InspectionEC.INSPECTIONEC_TYPE_LESS);
            inspectionEC.setStatus(InspectionEC.INSPECTION_EXCEPTION_STATUS_UNHANDLED);
            inspectionEC.setCreateTime(sorting.getOperateTime());
            this.inspectionECDao.add(InspectionECDao.namespace, inspectionEC);
        }
    }

}
