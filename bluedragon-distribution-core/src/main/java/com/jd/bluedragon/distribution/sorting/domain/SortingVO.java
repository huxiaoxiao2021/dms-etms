package com.jd.bluedragon.distribution.sorting.domain;

import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 分拣VO对象
 */
public class SortingVO extends Sorting {

    /**
     * 分拣操作类型  1 包裹  2 运单  3运单转包裹
     */
    public static int SORTING_TYPE_PACK = 1;
    public static int SORTING_TYPE_WAYBILL = 2;
    public static int SORTING_TYPE_WAYBILL_SPLIT = 3;


    public SortingVO(){

    }

    public SortingVO(Task task) {
        Sorting sorting = toSorting(task);
        if(sorting!=null){
            BeanUtils.copyProperties(sorting,this);
            //区分运单包裹
            if(getSortingType()==0){
                //未指定分拣模式时根据以下判断
                if (StringHelper.isEmpty(sorting.getPackageCode())) {
                    // 按运单分拣
                    setSortingType(SORTING_TYPE_WAYBILL);
                }else{
                    setSortingType(SORTING_TYPE_PACK);
                }
            }

        }
    }





    public boolean isPackSorting(){
        return this.sortingType == SORTING_TYPE_PACK;
    }

    public boolean isWaybillSorting(){
        return this.sortingType == SORTING_TYPE_WAYBILL;

    }

    public boolean isWaybillSplitSorting(){
        return this.sortingType == SORTING_TYPE_WAYBILL_SPLIT;

    }
    private int sortingType;

    private int pageNo;

    private int pageSize;
    //专为 运单拆分任务使用 是否需要补验货
    private boolean needInspection = true;
    /**
     * 是否为运单转换任务的最后一条
     */
    private boolean lastTurn = false;

    private BaseEntity<BigWaybillDto> waybillDtoBaseEntity;

    private BaseStaffSiteOrgDto createSite;

    public int getSortingType() {
        return sortingType;
    }

    public void setSortingType(int sortingType) {
        this.sortingType = sortingType;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean getNeedInspection() {
        return needInspection;
    }

    public void setNeedInspection(boolean needInspection) {
        this.needInspection = needInspection;
    }

    public BaseEntity<BigWaybillDto> getWaybillDtoBaseEntity() {
        return waybillDtoBaseEntity;
    }

    public BigWaybillDto getWaybillDto() {
        if(waybillDtoBaseEntity!=null){
            return waybillDtoBaseEntity.getData();
        }
        return null;
    }

    public Waybill getWaybill() {
        if(waybillDtoBaseEntity!=null && waybillDtoBaseEntity.getData()!=null){
            return waybillDtoBaseEntity.getData().getWaybill();
        }
        return null;
    }

    public List<DeliveryPackageD> getPackageList() {
        if(waybillDtoBaseEntity!=null && waybillDtoBaseEntity.getData()!=null  ){
            return waybillDtoBaseEntity.getData().getPackageList();
        }
        return null;
    }

    public BaseStaffSiteOrgDto getCreateSite() {
        return createSite;
    }

    public void setCreateSite(BaseStaffSiteOrgDto createSite) {
        this.createSite = createSite;
    }

    /**
     * 获取包裹总数
     * @return
     */
    public int getPackageListSize(){
        BaseEntity<BigWaybillDto> waybillDtoBaseEntity = getWaybillDtoBaseEntity();
        if(waybillDtoBaseEntity!=null && waybillDtoBaseEntity.getData()!=null
                && waybillDtoBaseEntity.getData().getWaybill()!=null
                && waybillDtoBaseEntity.getData().getWaybill().getGoodNumber()!=null) {
            return waybillDtoBaseEntity.getData().getWaybill().getGoodNumber();
        }
        return 0;
    }

    public void setWaybillDtoBaseEntity(BaseEntity<BigWaybillDto> waybillDtoBaseEntity) {
        this.waybillDtoBaseEntity = waybillDtoBaseEntity;
    }

    private SortingVO toSorting(Task task) {
        String body = task.getBody().substring(1, task.getBody().length() - 1);
        SortingRequest request = JsonHelper.jsonToArray(body, SortingRequest.class);
        if (request != null) {
            Sorting sorting = Sorting.toSorting(request);
            sorting.setStatus(Sorting.STATUS_DONE);// 运单回传状态默认为1，以后可以去掉
            SortingVO sortingVO = new SortingVO();
            BeanUtils.copyProperties(sorting,sortingVO);
            return sortingVO;
        }else{
            SortingVO sortingVO = JsonHelper.fromJson(task.getBody(), SortingVO.class);
            if(sortingVO != null){
                sortingVO.setStatus(Sorting.STATUS_DONE);
                return sortingVO;
            }
        }
        return null;
    }

    public boolean getLastTurn() {
        return lastTurn;
    }

    public void setLastTurn(boolean lastTurn) {
        this.lastTurn = lastTurn;
    }
}
