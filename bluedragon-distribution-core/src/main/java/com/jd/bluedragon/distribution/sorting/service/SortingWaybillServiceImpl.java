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
import org.apache.commons.lang.StringUtils;
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
            if( StringUtils.isBlank(sorting.getWaybillCode())){
                log.warn("运单分拣运单号为空:{}", JsonHelper.toJson(sorting));
                return true;
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
                log.warn("运单分拣包裹数据为空:{}", JsonHelper.toJson(sorting));
            }


        }else if(sorting.getIsCancel().equals(getSortingService().SORTING_CANCEL)){
            // 取消分拣 离线任务时执行 （正常取消分拣直接走rest接口）
            getSortingService().canCancel(sorting);
        }

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
