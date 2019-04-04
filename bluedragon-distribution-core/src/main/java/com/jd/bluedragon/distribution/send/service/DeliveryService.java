package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.domain.SendThreeDetail;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;

import java.util.List;

public interface DeliveryService {

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
     * 一车一单发货数据落库，写相关的异步任务
     *
     * @param domain
     */
    void packageSend(SendBizSourceEnum sourceEnum, SendM domain);

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
     * 查询箱号发货记录
     *
     * @param boxCode 箱号
     * @return 发货记录列表
     */
    List<SendM> getSendMListByBoxCode(String boxCode);

    Integer add(SendDetail sendDetail);

    Integer update(SendDetail sendDetail);

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
     * 查找需要更改运单状态的数据
     *
     * @param
     */
    boolean updatewaybillCodeMessage(Task task) throws Exception;

    /**
     * 查找需要回传预分配数据
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
     * @param
     */
    ThreeDeliveryResponse dellCancelDeliveryMessage(SendM tSendM, boolean needSendMQ);

    /**
     * 生成发货数据处理
     *
     * @param source    发货源
     * @param sendMList 发货相关数据
     * @return
     */
    DeliveryResponse dellDeliveryMessage(SendBizSourceEnum source, List<SendM> sendMList);

    /**
     * 电子标签批量发货处理
     *
     * @param sendM 发货相关数据
     */
    DeliveryResponse dealWithSendBatch(SendM sendM);

    /**
     * 发货主表数据查询，验证是否重复发货
     *
     * @param SendM 发货相关数据
     */
    DeliveryResponse findSendMByBoxCode(SendM tSendM, boolean isTransferSend);

    /**
     * 通过运单号来判断是否发货
     *
     * @param tSendDatail 运单号/分拣中心/目的地
     */
    boolean checkSend(SendDetail tSendDatail);

    /**
     * 通过包裹号和分拣中心ID获得目的地站点ID
     *
     * @param
     */
    SendDetail getSendSiteID(String packbarCode, Integer sitecode);

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
     * 校验滑道号
     * @param waybillCode 运单号
     * @param packageCode 包裹号
     * @return true 滑道号正确，false 滑道号不正确
     */
    public boolean checkCrossCode(String waybillCode,String packageCode);

    /**
     * 快运发货差异查询
     */
    ThreeDeliveryResponse differentialQuery(List<SendM> sendMList, Integer queryType);

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
    List<SendDetail> findWaybillStatus(List<String> queryCondition);

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
    SendDetail measureRetrieve(SendDetail sendDetail);

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
     * 自动分拣批量发货处理
     *
     * @param sendMList 发货相关数据
     */
    DeliveryResponse autoBatchSend(List<SendM> sendMList);

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
     * 执行取消上次发货逻辑
     *
     * @param domain
     */
    ThreeDeliveryResponse cancelLastSend(SendM domain);

    /**
     * 根据箱号查询箱号的运单号
     *
     * @param boxCode
     * @return
     */
    List<String> getWaybillCodesByBoxCodeAndFetchNum(String boxCode, Integer fetchNum);
}
