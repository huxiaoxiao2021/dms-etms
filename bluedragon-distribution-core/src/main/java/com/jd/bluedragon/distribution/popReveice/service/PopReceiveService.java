package com.jd.bluedragon.distribution.popReveice.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popReveice.domain.PopReceive;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-8-21 下午08:51:51
 *
 * POP收货处理服务接口
 */
public interface PopReceiveService {
	
	/**
	 * 根据运单号查询打印信息
	 * @param waybillCode
	 * @return
	 */
	public PopPrint findPopPrint(String waybillCode);
	
	/**
	 * 补全订单收货信息
	 * @param waybillCode
	 * @return
	 */
	public int saveRecevie(Inspection inspection);
	
	public List<PopReceive> findListByParamMap(Map<String, Object> paramMap);
	
	/**
	 * 根据防重码查询POP托寄收货信息是否存在
	 * 
	 * @param fingerPrint
	 * @return
	 */
	public PopReceive findByFingerPrint(String fingerPrint);
	
	public int addReceive(PopReceive popReceive);
	/**
	 * 查询数据列表，支持分页查询
	 * @param map
	 * @return
	 */
	public List<PopReceive> findPopReceiveList(Map<String,Object> map);
	/**
	 * 获取数据总条数
	 * @param map
	 * @return
	 */
	public int count(Map<String,Object> map);

}
