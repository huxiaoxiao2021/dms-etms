package com.jd.bluedragon.distribution.internal.service;

import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.*;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;

import java.util.List;

/**
 * @author dudong
 * @date 2016/1/4
 */
public interface DmsInternalService {

    /***
     * 生成箱号包括路由信息
     * @see Ver BoxClient#get
     * @see Web BoxResource#get
     * @param boxCode
     * @return
     */
    public BoxResponse getBox(String boxCode);

    /***
     * 获取基础资料数据字典
     * @see Ver DatadictClient#get
     * @see Web BaseResource#getOrignalBackBusIds
     * @param parentID
     * @param nodeLevel
     * @param typeGroup
     * @return
     */
    public DatadictResponse getDatadict(Integer parentID, Integer nodeLevel,Integer typeGroup);


    /***
     * 根据站点编码获取站点
     * @see Ver DmsVerBaseClient#get
     * @see Web BaseResource#getSite
     * @param code
     * @return
     */
    public BaseResponse getSiteByCode(String code);

    /***
     * 添加任务Task
     * @see Ver DmsWebBaseClient#post
     * @see Web TaskResource#add
     * @param request
     * @return
     */
    public TaskResponse addTask(TaskRequest request);

    /***
     * 获取自提柜所属站点
     * @see Ver DmsWebBaseClient#getselfD
     * @see Web BaseResource#getselfD
     * @param code
     * @return
     */
    public BaseResponse getBelongSiteCode(Integer code);

    /***
     * 获取大全表里面站点对应的分拣中心
     * @see Ver DmsWebBaseClient#getTargetDmsCenter
     * @see Web WaybillResource#getTargetDmsCenter
     * @param startDmsCode
     * @param siteCode
     * @return
     */
    public BaseResponse getTargetDmsCenter(Integer startDmsCode, Integer siteCode);


    /***
     * 获取运单最后一次返调度信息
     * @see Ver DmsWebBaseClient#queryLastScheduleSite
     * @see Web ReassignWaybillResource#queryLastScheduleSite
     * @param packageCode
     * @return
     */
    public BaseResponse getLastScheduleSite(String packageCode);


    /***
     * 根据运单号获取逆向收货信息
     * @see Ver DmsWebBaseClient#reverseReceive
     * @see Web AuditResource#getReverseReceive
     * @param waybillCode
     * @return
     */
    public ReverseReceiveResponse getReverseReceive(String waybillCode);

    /***
     * 根据订单号获取报损信息
     * @see Ver DmsWebBaseClient#getLossResult
     * @see Web LossProductResource#getLossOrderProducts
     * @param orderId
     * @return
     */
    public LossProductResponse getLossOrderProducts(Long orderId);


    /***
     * 根据配置名称获取配置信息
     * @see Ver DmsWebBaseClient#getSwtichStatus
     * @see Web BaseResource#getSwitchStatus
     * @param conName
     * @return
     */
    public SysConfigResponse getSwitchStatus(String conName);


    /***
     * 自动分拣机生成箱号信息
     * @see Ver DmsWebBaseClient#posSmartBoxes
     * @see Web BoxResource#create
     * @param request
     * @return
     */
    public InvokeResult<AutoSortingBoxResult> createAutoSortingBox(BoxRequest request);


    /***
     * 根据运单号获取运单预分拣站点
     * @see Ver DmsWebBaseClient#getPreseparateSite
     * @see Web PreseparateWaybillResource#getPreseparateSiteId
     * @param waybillCode
     * @return
     */
    public InvokeResult<Integer> getPreseparateSiteId(String waybillCode);

    /***
     * 根据运单号获取Pop打印信息
     * @see Ver PopPrintClient#getByWaybillCode
     * @see Web PopPrintResource#findByWaybillCode
     * @param waybillCode
     * @return
     */
    public PopPrintResponse getPopPrintByWaybillCode(String waybillCode);


}
