package com.jd.bluedragon.distribution.send.service;

import java.util.List;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.send.domain.*;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;

public interface DeliveryService {


    /**
     * 原包发货
     * @param domain 发货对象
     * @return Map.Entiry<code,message> 改到SendResult
     */
    SendResult packageSend(SendM domain,boolean isForceSend);

    /**
     * 推送发货状态数据至运单系统[写WORKER]
     * @param domain 发货对象
     * @return
     */
    int pushStatusTask(SendM domain);

    /**
     * 查询箱号发货记录
     * @param boxCode 箱号
     * @return 发货记录列表
     */
    List<SendM> getSendMListByBoxCode(String boxCode);


    Integer add(SendDetail sendDetail);

    Integer update(SendDetail sendDetail);

    void saveOrUpdate(SendDetail sendDetail);

    /** 通过创建站点、包裹号码或运单号码、业务类型，判断其分拣记录能否被取消 */
    Boolean canCancel(SendDetail sendDetail);

    /** 通过创建站点、业务类型、模糊匹配的包裹号，判断其分拣记录能否被取消 */
    Boolean canCancelFuzzy(SendDetail parseSendDetail);
    
    /**
     * 查找需要更改运单状态的数据
     * 
     * @param
     */
    public boolean updatewaybillCodeMessage(Task task) throws Exception;

    /**
     * 查找需要回传预分配数据
     * 
     * @param
     */
    public boolean findSendwaybillMessage(Task task) throws Exception;

    /**
     * 运单状态接口调用，更新运单回传之后状态
     * 
     * @param
     */
    public boolean updateWaybillStatus(List<SendDetail> sendDatailArray);

    /**
     * 取消发货处理
     * 
     * @param
     */
    public ThreeDeliveryResponse dellCancelDeliveryMessage(SendM tSendM);

    /**
     * 生成发货数据处理
     * 
     * @param tDeliveryMessageJsonList 发货相关数据
     */
    public DeliveryResponse dellDeliveryMessage(List<SendM> sendMList);
    
    /**
     * 电子标签批量发货处理
     * 
     * @param tDeliveryMessageJsonList 发货相关数据
     */
    public DeliveryResponse dealWithSendBatch(SendM sendM);

    /**
     * 发货主表数据查询，验证是否重复发货
     * 
     * @param SendM 发货相关数据
     */
    public DeliveryResponse findSendMByBoxCode(SendM tSendM , boolean flage);

    /**
     * 通过运单号来判断是否发货
     * 
     * @param tSendDatail 运单号/分拣中心/目的地
     */
    public boolean checkSend(SendDetail tSendDatail);

    /**
     * 通过包裹号和分拣中心ID获得目的地站点ID
     * 
     * @param 
     */
    public SendDetail getSendSiteID(String packbarCode, Integer sitecode);

    /**
     * 根据包裹号、箱号、创建站点（分拣中心）、接收站点来判断
     * send_type=30表示三方
     * is_cancel=0表示未取消分拣或者发货
     * @param sendDetail
     * @return
     */
	boolean checkSendByPackage(SendDetail sendDetail);

	/** 通过操作站点编号、箱号，查询对应分拣信息dms报表 */
	public List<SendDetail> findOrder(SendDetail sendDetail);
	
	/** 三方发货不全验证 */
	public List<SendThreeDetail> checkThreePackage(List<SendM> sendMList);

	/** 三方接口*/
	public List<SendDetail> findDeliveryPackageBySite(SendDetail sendDetail);
	
	/** 三方接口 */
	public List<SendDetail> findDeliveryPackageByCode(SendDetail sendDetail);
	
	/**
     * 回传DMC数据处理
     * 
     * @param tSendDatail 运单号/分拣中心/目的地
     */
	public boolean sendMTooldtms(List<SendM> tSendMList);
	
	/**
     * 回传运单状态数据后补状态
     */
	public List<SendDetail> findWaybillStatus(List<String> queryCondition);
	
	/**
	 * 根据条件获取sendM
	 * @param sendM
	 */
	public List<SendM> queryCountByBox(SendM sendM);
	
	/**
	 * 补全包裹重量
	 */
	public SendDetail measureRetrieve(SendDetail sendDetail);
	
	/**
	 * 一单多件包裹不全验证
	 * @param boxCode
	 * @param createSiteCode
	 * @param receiveSiteCode
	 * @return
	 */
	public List<SendThreeDetail> checkSortingDiff(String boxCode,
			Integer createSiteCode, Integer receiveSiteCode);
	
	/**
	 * 一单多件包裹不全补全
	 * @param boxCode
	 * @param createSiteCode
	 * @param receiveSiteCode
	 * @return
	 */
	public Integer appendPackageNum(String boxCode,
			Integer createSiteCode, Integer receiveSiteCode);
	/**
	 * 取消发货m表
	 * @return
	 */
	public boolean cancelSendM(SendM tSendM);
	/**
	 * 取消发货d表批量
	 * @return
	 */
	public boolean cancelSendDatailByBox(List<SendDetail> tlist);
	/**
	 * 取消发货d表包裹
	 * @return
	 */
	public boolean cancelSendDatailByPackage(SendDetail tSendDetail);
	
	/**
	 * 运单信息
	 * @return
	 */
	public List<BigWaybillDto> getWaillCodeListMessge(WChoice queryWChoice, List<String> waybillCodes);
	
	/**
	 * 如果是中转发货的类型新建任务补发包裹明细
	 * @return
	 */
	public boolean transitSend(SendM tSendM);

    /**
     * 判断是当前发货是否为中转发货
     * @param domain 发货对象
     * @return 中转发货TRUE  非中转 FALSE
     */
    public boolean isTransferSend(SendM domain);

    /**
     * 推送中转发货任务至数据库
     * @param domain
     */
    public void pushTransferSendTask(SendM domain);
	
	 /**
     * 查找需要回传预分配数据
     * 
     * @param
     */
    public boolean findTransitSend(Task task) throws Exception;
    
    /**
     * 查找需要回传预分配数据
     * 
     * @param
     */
    public List<SendDetail> getSendByBox(String boxCode);
    
    /**
	 * 包裹信息
	 * @return
	 */
	public void getAllList(List<SendM> sendMList, List<SendDetail> allList);
	
	/**
	 * 一单多件包裹不全验证
	 * @param boxCode
	 * @param createSiteCode
	 * @param receiveSiteCode
	 * @return
	 */
	public List<SendDetail> queryBySendCodeAndSendType(String sendCode,
			Integer sendType);
	
	Integer cancelDelivery(SendDetail sendDetail);
	
	/**
     * 自动分拣批量发货处理
     * 
     * @param tDeliveryMessageJsonList 发货相关数据
     */
    public DeliveryResponse autoBatchSend(List<SendM> sendMList);
}
