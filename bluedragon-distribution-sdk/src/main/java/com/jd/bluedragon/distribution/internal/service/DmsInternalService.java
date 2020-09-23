package com.jd.bluedragon.distribution.internal.service;

import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.AutoSortingBoxResult;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.api.response.DatadictResponse;
import com.jd.bluedragon.distribution.api.response.LossProductResponse;
import com.jd.bluedragon.distribution.api.response.PopPrintResponse;
import com.jd.bluedragon.distribution.api.response.ReverseReceiveResponse;
import com.jd.bluedragon.distribution.api.response.SysConfigResponse;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigDto;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;

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

    /**
     *  根据三方-合作站点获取三方-合作站点所属自营站点
     * @see Ver DmsWebBaseClient#getThreePartnerD
     * @see Web BaseResource#getThreePartnerD
     *  @param code 三方-合作站点ID
     *  @return 三方-合作站点所属自营站点信息
     */
    public BaseResponse getThreePartnerBelongSiteCode(Integer code);

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
     * 根据运单号获取报损信息
     * @return
     */
    public LossProductResponse getLossOrderProductsByWaybillCode(String waybillCode);

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


    /***
     * 用户登录接口
     * @see Web BaseResource#login
     * @param request
     * @return
     */
    public BaseResponse login(String userName, String passwd);

    /**
     * 查询运单是否已经确认耗材
     * @see ver DmsWebBaseClient#isConsumableConfirmed
     * @param waybillCode
     * @return
     */
    public Boolean isConsumableConfirmed(String waybillCode);

    /**
     * 查询运单是否可以进行逆向操作
     * @see Ver DmsWebBaseClient#isReverseOperationAllowed
     * @param waybillCode 运单号
     * @param siteCode 操作站点
     * @return true:可以操作逆向操作 false:反之
     */
    public InvokeResult<Boolean> isReverseOperationAllowed(String waybillCode, Integer siteCode);

    /**
     * 查询运单是否可以进行逆向操作
     * @see Ver DmsWebBaseClient#isBoxSent
     * @param boxCode 箱号
     * @param siteCode 操作站点
     * @return true:已发货 false:未发货或取消发货
     */
    public Boolean isBoxSent(String boxCode, Integer siteCode);

    /**
     * 预分拣 - 分拣中心查询商家是否可以走B网车队
     * @param siteCode:当前场地code
     * @param vendorId：商家ID
     * @param waybillSign
     * @return
     */
    InvokeResult<Boolean> dispatchToExpress(Integer siteCode, Integer vendorId, String waybillSign);


    /**
     * 加盟商运单是否已交接
     * <p>
     *
     * @param waybillCode 运单号
     * @return
     */
    BaseEntity<Boolean> allianceBusiDelivered(String waybillCode);

    /**
     * 查询运单是否操作过异常处理
     *
     * @param waybillCode 运单号
     * @param siteCode 操作站点
     * @return true:操作过 false:反之
     */
    InvokeResult<Boolean> isTreatedAbnormal(String waybillCode, Integer siteCode);
    /***
     * 获取箱号已分拣的包裹数量
     * @param boxCode 必填项：箱号编码
     * @param createSiteCode 非必填：箱号始发分拣中心
     * @return
     */
    JdResult<Integer> getSortingNumberInBox(String boxCode,Integer createSiteCode);

    /**
     * 查询符合条件的功能开关配置
     * @param funcSwitchConfigDto
     * @return
     */
    InvokeResult<List<FuncSwitchConfigDto>> getFuncSwitchConfigs(FuncSwitchConfigDto funcSwitchConfigDto);
}
