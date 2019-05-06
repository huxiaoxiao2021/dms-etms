package com.jd.bluedragon.distribution.sorting.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.sorting.domain.SortingVO;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 运单分拣业务类
 */
@Service("sortingWaybillService")
public class SortingWaybillServiceImpl extends SortingCommonSerivce{

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    public static int WAYBILL_SPLIT_NUM = 100;

    @Override
    public boolean doSorting(SortingVO sorting) {
        if (sorting.getIsCancel().equals(getSortingService().SORTING_CANCEL_NORMAL)) {

            //补验货 提前 防止拆分任务也去补验货任务
            if(isNeedInspection(sorting)){
                b2bPushInspection(sorting);
            }

            BaseEntity<BigWaybillDto> waybillDtoBaseEntity = sorting.getWaybillDtoBaseEntity();
            BaseStaffSiteOrgDto createSite = sorting.getCreateSite();
            int pakcageListSize = sorting.getPackageListSize();
            //每页容量
            int pageSize = uccPropertyConfiguration.getWaybillSplitPageSize()==0?WAYBILL_SPLIT_NUM:uccPropertyConfiguration.getWaybillSplitPageSize();
            //计算总页数
            if(pakcageListSize>0){
                int pagesSize = Double.valueOf(Math.floor(pakcageListSize/pageSize)).intValue();
                if(pakcageListSize%pageSize!=0){
                    pagesSize++;
                }
                sorting.setWaybillDtoBaseEntity(null);
                sorting.setCreateSite(null);

                //分批拆任务
                SortingVO sortingTurnBody = new SortingVO();
                BeanUtils.copyProperties(sorting,sortingTurnBody);
                //复制完后在将运单对象赋值回去
                sorting.setWaybillDtoBaseEntity(waybillDtoBaseEntity);
                sorting.setCreateSite(createSite);

                sortingTurnBody.setSortingType(SortingVO.SORTING_TYPE_WAYBILL_SPLIT);
                for (int i = 0; i < pagesSize; i++) {
                    //发送拆分任务
                    sortingTurnBody.setPageNo(i+1);
                    sortingTurnBody.setPageSize(pageSize);
                    if(i==pagesSize-1){
                        //最后一条拆分任务
                        sortingTurnBody.setLastTurn(true);
                    }
                    splitSorting(sortingTurnBody);
                }
            }else{
                logger.error("运单分拣包裹数据为空"+JsonHelper.toJson(sorting));
            }


        }else if(sorting.getIsCancel().equals(getSortingService().SORTING_CANCEL)){
            // 取消分拣 离线任务时执行 （正常取消分拣直接走rest接口）
            getSortingService().canCancel(sorting);
        }

        return true;
    }

    @Override
    public boolean isNeedInspection(SortingVO sorting) {
        BaseStaffSiteOrgDto createSite = sorting.getCreateSite();
        //B网建箱自动触发验货全程跟踪
        if (createSite==null || Constants.B2B_SITE_TYPE!=createSite.getSubType()){
            return false;
        }
        Inspection inspectionQ=new Inspection();
        inspectionQ.setWaybillCode(sorting.getWaybillCode());
        inspectionQ.setCreateSiteCode(sorting.getCreateSiteCode());
        inspectionQ.setYn(Integer.valueOf(1));

        Integer inspectionList = inspectionDao.queryCountByConditionOfSolePackage(inspectionQ);
        if(inspectionList!=null && inspectionList.intValue() > 0){

            if(sorting.getPackageListSize() == inspectionList.intValue()){
                //如果已经验过货  就不用补了
                sorting.setNeedInspection(false);
                return false;
            }else if(sorting.getPackageListSize() > inspectionList.intValue()){
                //验货不全 本次不补，通过运单转包裹的拆分任务去补
                logger.warn("运单分拣时部分包裹存在验货"+sorting.getWaybillCode());
                return false;
            }
        }
        //运单维度已经补过了 运单转包裹维度的就不需要在补了
        sorting.setNeedInspection(false);
        return true;
    }


    private void splitSorting(SortingVO sorting){
        Task task = new Task();

        task.setType(Task.TASK_TYPE_SORTING_SPLIT);
        task.setTableName(Task.getTableName(Task.TASK_TYPE_SORTING_SPLIT));
        task.setSequenceName(Task.getSequenceName(Task.getTableName(Task.TASK_TYPE_SORTING_SPLIT)));
        task.setBoxCode(sorting.getBoxCode());
        task.setCreateSiteCode(sorting.getCreateSiteCode());
        task.setKeyword1(String.valueOf(sorting.getCreateSiteCode()));
        task.setKeyword2(sorting.getPackageCode());
        task.setOwnSign(BusinessHelper.getOwnSign());
        task.setBody(JsonHelper.toJson(sorting));

        taskService.add(task);
    }
}
