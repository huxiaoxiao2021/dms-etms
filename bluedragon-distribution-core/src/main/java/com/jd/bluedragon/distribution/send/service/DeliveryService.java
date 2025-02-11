package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.distribution.api.request.ColdChainDeliveryRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.response.CheckBeforeSendResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.domain.SendThreeDetail;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;

import java.util.List;

public interface DeliveryService {

    JdResult<CheckBeforeSendResponse> checkBeforeSend(DeliveryRequest deliveryRequest);

    DeliveryResponse doCheckDeliveryInfo(String boxCode,
                                         Integer siteCode,
                                         Integer receiveSiteCode,
                                         Integer businessType,
                                         Integer opType);


    DeliveryResponse checkThreeDelivery(DeliveryRequest request,Integer flag);

    DeliveryResponse sendDeliveryInfoForKY(List<DeliveryRequest> request,SendBizSourceEnum sourceEnum);

    DeliveryResponse coldChainSendDelivery(List<ColdChainDeliveryRequest> request,SendBizSourceEnum sourceEnum,boolean checkSealCar);
    /**
     * 有校验且有多次发货取消上次发货逻辑的一车一单发货
     *
     * @param bizSource        业务来源
     * @param domain
     * @param isForceSend      是否强制发货
     * @param isCancelLastSend 是否取消上次发货
     * @return Map.Entiry<code                                                                                                                               ,                                                                                                                               message> 改到SendResult
     */
    SendResult packageSend(SendBizSourceEnum bizSource, SendM domain, boolean isForceSend, boolean isCancelLastSend);

    /**
     * 有校验的一车一单发货(无取消上次发货业务逻辑)
     *
     * @param bizSource   业务来源
     * @param domain
     * @param isForceSend 是否强制发货
     * @return
     */
    SendResult packageSend(SendBizSourceEnum bizSource, SendM domain, boolean isForceSend);

    /**
     * 按运单批量发货,校验数据并写入异步任务:运单下的包裹数量需大于 100
     * @param domain 发货数据
     */
    SendResult packageSendByWaybill(SendM domain, Boolean isForceSend, Boolean isCancelLastSend);

    /**
     * 一车一单发货数据落库，写相关的异步任务
     *
     * @param domain
     */
    void packageSend(SendBizSourceEnum sourceEnum, SendM domain);

    /**
     * 按运单发货异步任务处理
     */
    void doPackageSendByWaybill(SendM domain);

    /**
     * 箱子已发货则返回已发货批次号
     * @param domain
     */
    String getSendedCode(SendM domain);

    /**
     * 推分拣任务
     *
     * @param domain
     */
    void pushSorting(SendM domain);

    /**
     * 一车一单离线发货
     *
     * @param sourceEnum
     * @param domain
     * @return
     */
    SendResult offlinePackageSend(SendBizSourceEnum sourceEnum, SendM domain);

    /**
     * 组板发货，写组板发货任务
     *
     * @param domain
     * @return
     */
    SendResult boardSend(SendM domain, boolean isForceSend);

    /**
     * 封装校验板号是否已发货服务
     * @param domain
     * @return
     */
    public boolean checkSendByBoard(SendM domain);

    /**
     * 龙门架自动发货原包发货，去掉原有的分拣发货拦截验证
     *
     * @param domain 发货对象
     * @return Map.Entiry<code   ,   message> 改到SendResult
     */
    SendResult autoPackageSend(SendM domain, UploadData uploadData);

    /**
     * 推送发货状态数据至运单系统[写WORKER]
     *
     * @param domain 发货对象
     * @return
     */
    int pushStatusTask(SendM domain);

    /**
     * 推送发货状态数据至运单系统[写WORKER] 仅在按运单维度处理时使用
     * @param domain
     * @return
     */
    int pushStatusTaskWithByWaybillFlag(SendM domain);
    /**
     * 推组板发货任务
     * @param domain
     * @return
     */
    boolean pushBoardSendTask(SendM domain,Integer taskType);

    /**
     * 写组板发货任务完成，调用TC修改板状态为发货
     */
    public void changeBoardStatusSend(String boardCode, SendM domain);

    SendResult checkBoard(String boardCode, SendM domain,Boolean isForceSend);
    /**
     * 查询箱号发货记录
     *
     * @param boxCode 箱号
     * @return 发货记录列表
     */
    List<SendM> getSendMListByBoxCode(String boxCode);

    Integer add(SendDetail sendDetail);

    Integer update(SendDetail sendDetail);

    /**
     * 重置发货明细数据状态
     * @param sendDetail
     * @return
     */
    Integer updateCancel(SendDetail sendDetail);
    void saveOrUpdate(SendDetail sendDetail);

    /**
     * 通过创建站点、包裹号码或运单号码、业务类型，判断其分拣记录能否被取消
     */
    Boolean canCancel(SendDetail sendDetail);

    /**
     * 通过创建站点、包裹号码或运单号码、业务类型，判断其分拣记录能否被取消  不再判断正向还是逆向业务  added by zhanglei
     */
    Boolean canCancel2(SendDetail sendDetail);

    /**
     * 通过创建站点、业务类型、模糊匹配的包裹号，判断其分拣记录能否被取消
     */
    Boolean canCancelFuzzy(SendDetail parseSendDetail);

    /**
     * 推送通知消息
     * @param sdm
     */
    void deliverGoodsNoticeMQ(SendM sdm);

    /**
     * 查找需要更改运单状态的数据
     *
     * @param
     */
    boolean updatewaybillCodeMessage(Task task) throws Exception;

    /**
     * 查找需要回传预分配数据 && 变更箱号（集包）任务状态
     *
     * @param
     */
    boolean findSendwaybillMessage(Task task) throws Exception;

    /**
     * 运单状态接口调用，更新运单回传之后状态
     *
     * @param
     */
    boolean updateWaybillStatus(List<SendDetail> sendDatailArray);

    /**
     * 取消发货处理
     *
     * @param tSendM
     * @param needSendMQ
     * @return
     */
    ThreeDeliveryResponse dellCancelDeliveryMessageWithServerTime(SendM tSendM, boolean needSendMQ);

    /**
     * 取消发货处理
     *
     * @param tSendM
     * @param needSendMQ
     * @return
     */
    ThreeDeliveryResponse dellCancelDeliveryMessageWithOperateTime(SendM tSendM, boolean needSendMQ);

    /**
     * 取消发货处理
     *
     * @param
     */
    ThreeDeliveryResponse dellCancelDeliveryMessage(SendM tSendM, boolean needSendMQ);

    /**
     * 取消发货校验封车业务
     * 1、已解封车不允许取消发货
     * 2、封车超过一小时不允许取消发货
     * 3、取消发货后批次内货物不允许为空
     * @param tSendM
     * @return
     */
    DeliveryResponse dellCancelDeliveryCheckSealCar(SendM tSendM);

    /**
     * 生成发货数据处理
     *
     * @param source    发货源
     * @param sendMList 发货相关数据
     * @return
     */
    DeliveryResponse dellDeliveryMessage(SendBizSourceEnum source, List<SendM> sendMList);

    /**
     * 生成发货数据处理（带有操作锁）
     *
     * @param source    发货源
     * @param sendMList 发货相关数据
     * @return
     */
    DeliveryResponse dellDeliveryMessageWithLock(SendBizSourceEnum source, List<SendM> sendMList);

    /**
     * 电子标签批量发货处理
     *
     * @param sendM 发货相关数据
     */
    DeliveryResponse dealWithSendBatch(SendM sendM);

    /**
     * 发货主表数据查询，验证是否重复发货
     *
     * @param tSendM 发货相关数据
     */
    DeliveryResponse findSendMByBoxCode(SendM tSendM, boolean isTransferSend, Integer opType);

    /**
     * 通过运单号来判断是否发货
     *
     * @param tSendDatail 运单号/分拣中心/目的地
     */
    boolean checkSend(SendDetail tSendDatail);

    /**
     * 根据包裹号、箱号、创建站点（分拣中心）、接收站点来判断
     * send_type=30表示三方
     * is_cancel=0表示未取消分拣或者发货
     *
     * @param sendDetail
     * @return
     */
    boolean checkSendByPackage(SendDetail sendDetail);

    /**
     * 通过操作站点编号、箱号，查询对应分拣信息dms报表
     */
    List<SendDetail> findOrder(SendDetail sendDetail);

    /**
     * 三方发货不全验证
     */
    ThreeDeliveryResponse checkThreePackage(List<SendM> sendMList);

    /**
     * 快运发货不全验证
     */
    ThreeDeliveryResponse checkThreePackageForKY(List<SendM> sendMList);

    /**
     * 快运发货差异查询
     */
    ThreeDeliveryResponse differentialQuery(List<SendM> sendMList, Integer queryType);

    /**
     * 按箱路由校验
     * @param queryPara
     * @return
     */
    DeliveryResponse checkRouterForCBox(SendM queryPara);

    /**
     * 快运发货路由信息验证
     */
    DeliveryResponse checkRouterForKY(SendM sendm, Integer flag);

    /**
     * 三方接口
     */
    List<SendDetail> findDeliveryPackageBySite(SendDetail sendDetail);

    /**
     * 三方接口
     */
    List<SendDetail> findDeliveryPackageByCode(SendDetail sendDetail);

    /**
     * 回传DMC数据处理
     *
     * @param tSendMList 运单号/分拣中心/目的地
     */
    boolean sendMTooldtms(List<SendM> tSendMList);

    /**
     * 回传运单状态数据后补状态
     */
    List<SendDetail> findWaybillStatus(List<Long> queryCondition);

    List<SendDetail> queryBySendCodeAndSiteCode(String sendCode, Integer createSiteCode, Integer receiveSiteCode, Integer senddStatus);

    /**
     * 根据条件获取sendM
     *
     * @param sendM
     */
    List<SendM> queryCountByBox(SendM sendM);

    /**
     * 补全包裹重量
     */
    SendDetail measureRetrieve(SendDetail sendDetail, DeliveryPackageD packageD);

    /**
     * 一单多件包裹不全验证
     *
     * @param boxCode
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    List<SendThreeDetail> checkSortingDiff(String boxCode, Integer createSiteCode, Integer receiveSiteCode);

    /**
     * 一单多件包裹不全补全
     *
     * @param boxCode
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    Integer appendPackageNum(String boxCode, Integer createSiteCode, Integer receiveSiteCode);

    /**
     * 取消发货m表
     *
     * @return
     */
    boolean cancelSendM(SendM tSendM);

    /**
     * 取消发货d表批量
     *
     * @return
     */
    boolean cancelSendDatailByBox(SendM tSendM);

    /**
     * 取消发货d表包裹
     *
     * @return
     */
    boolean cancelSendDatailByPackage(SendDetail tSendDetail);

    /**
     * 运单信息
     *
     * @return
     */
    List<BigWaybillDto> getWaillCodeListMessge(WChoice queryWChoice, List<String> waybillCodes);

    /**
     * 如果是中转发货的类型新建任务补发包裹明细
     *
     * @return
     */
    boolean transitSend(SendM tSendM);

    /**
     * 批量插入中转任务
     * @param sendMList
     * @return
     */
    boolean batchTransitSend(List<SendM> sendMList);

    /**
     * 判断是当前发货是否为中转发货
     *
     * @param domain 发货对象
     * @return 中转发货TRUE  非中转 FALSE
     */
    boolean isTransferSend(SendM domain);

    /**
     * 推送中转发货任务至数据库
     *
     * @param domain
     */
    void pushTransferSendTask(SendM domain);

    /**
     * 查找需要回传预分配数据
     *
     * @param
     */
    boolean findTransitSend(Task task) throws Exception;

    /**
     * 查找需要回传预分配数据（isCancel=1状态的数据）
     *
     * @param
     */
    List<SendDetail> getCancelSendByBox(String boxCode);

    /**
     * 根据箱号查询sendD的数据（isCancel=0状态的数据）
     *
     * @param
     */
    List<SendDetail> getSendDetailsByBoxCode(String boxCode);

    List<SendDetail> getSendDetailByBoxAndCreateAndReceiveSiteCode(String boxCode, Integer createSiteCode, Integer receiveSiteCode);

    /**
     * 包裹信息
     *
     * @return
     */
    void getAllList(List<SendM> sendMList, List<SendDetail> allList);

    /**
     * 一单多件包裹不全验证
     *
     * @param sendCode
     * @param sendType
     * @return
     */
    List<SendDetail> queryBySendCodeAndSendType(String sendCode, Integer sendType);

    Integer cancelDelivery(SendDetail sendDetail);

    /**
     * 发送dms_send_detail发货明细MQ
     *
     * @param task
     */
    boolean sendDetailMQ(Task task);

    /**
     * 处理组板发货任务
     *
     * @param task 任务实体
     * @return
     */
    boolean doBoardDelivery(Task task);

    /**
     * 处理按运单发货任务
     */
    boolean doWaybillSendDelivery(Task task);

    boolean doSendByWaybillSplitTask(Task task);

    /**
     * 按板取消发货任务
     *
     * @param task 任务实体
     * @return
     */
    boolean doBoardDeliveryCancel(Task task);

    /**
     * 查询发货记录
     *
     * @param sendCode        批次号
     * @param createSiteCode  始发分拣中心
     * @param receiveSiteCode 目的分拣中心
     * @return
     */
    List<SendM> getSendMBySendCodeAndSiteCode(String sendCode, Integer createSiteCode, Integer receiveSiteCode);


    /**
     * 快运发货金鹏订单拦截提示
     *
     * @param siteCode    站点id
     * @param waybillCode 运单号
     * @return
     */
    DeliveryResponse dealJpWaybill(Integer siteCode, String waybillCode);

    /**
     * 取消上次发货
     * @param domain
     */
    void doCancelLastSend(SendM domain);

    /**
     * 执行取消上次发货逻辑
     *
     * @param domain
     */
    ThreeDeliveryResponse cancelLastSend(SendM domain);

    /**
     * 自动取消组板
     * @param domain
     */
    void autoBoardCombinationCancel(SendM domain);

    /**
     * 加急提示消息
     * @param domain
     */
    void sendDmsOperateHintTrackMQ(SendM domain);

    /**
     * 根据箱号查询箱号的运单号
     *
     * @param boxCode
     * @return
     */
    List<String> getWaybillCodesByBoxCodeAndFetchNum(String boxCode, Integer fetchNum);

    /**
     * 校验批次创建时间
     * @param sendCode
     * @return
     */
    boolean checkSendCodeIsOld(String sendCode);

    /**
     * 校验批次是否封车
     * @param sendCode
     * @return
     */
    boolean checkSendCodeIsSealed(String sendCode);

    /**
     * 批量处理BC箱号绑定的WJ箱号的发货逻辑
     * @param bizSource
     * @param BCSendM
     * @return
     */
    SendResult dealFileBoxSingleCarSend(SendBizSourceEnum bizSource, SendM BCSendM);

    /**
     * 老发货处理WJ发货逻辑
     * @param source
     * @param sendMList
     * @param fileSendMList
     * @return
     */
    DeliveryResponse dealFileBoxBatchSending(SendBizSourceEnum source, List<SendM> sendMList, List<SendM> fileSendMList);

    /**
     * 插入发货任务
     * @param sendM
     */
    void addTaskSend(SendM sendM);

    /**
     * 发货核心逻辑
     * @param source
     * @param sendMList
     */
    void deliveryCoreLogic(Integer source, List<SendM> sendMList);

    /**
     * 按运单发货是否在处理中
     * @param sendM
     * @return
     */
    boolean isSendByWaybillProcessing(SendM sendM);

    /**
     * 初始化拦截链校验实体
     * @param domain
     * @return
     */
    SortingCheck getSortingCheck(SendM domain);

    /**
     * 校验是否已经发货
     * @param domain
     * @param result
     * @return
     */
    boolean multiSendVerification(SendM domain, SendResult result);

    boolean packageSendByRealWaybill(SendM domain, Boolean isCancelLastSend, SendResult result);

    /**
     * 按运单发货任务
     * @param domain
     * @param taskType
     */
    void pushWaybillSendTask(SendM domain,Integer taskType);

    /**
     * 运单锁
     * @param waybillCode
     * @param createSiteCode
     * @param totalPackNum
     * @return
     */
    boolean lockWaybillSend(String waybillCode, Integer createSiteCode,int totalPackNum);

    /**
     * 运单解锁
     * @param waybillCode
     * @param createSiteCode
     */
    void unlockWaybillSend(String waybillCode, Integer createSiteCode);
}
