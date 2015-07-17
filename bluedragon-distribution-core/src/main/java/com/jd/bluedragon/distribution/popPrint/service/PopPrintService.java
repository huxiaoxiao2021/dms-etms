package com.jd.bluedragon.distribution.popPrint.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-14 下午07:48:58
 *
 * POP打印记录Service
 */
public interface PopPrintService {

	/**
	 * 根据运单号查询打印记录
	 * @param waybillCode
	 * @return
	 */
	public PopPrint findByWaybillCode(String waybillCode);

	/**
	 * 获取站点打印交接清单信息
	 * @param map
	 * @return
	 */
	public List<PopPrint> findSitePrintDetail(Map<String,Object> map);

	/**
	 * 获取站点交接清单数量
	 * @param map
	 * @return
	 */
	public Integer findSitePrintDetailCount(Map<String,Object> map);
	
	/**
	 * 根据运单号查询运单所有打印记录
	 * @param waybillCode
	 * @return
	 */
	public List<PopPrint> findAllByWaybillCode(String waybillCode);
	
	/**
	 * 按条数查询需要补全收货数据
	 * @param paramMap
	 * @return List<PopPrint>
	 */
	public List<PopPrint> findLimitListNoReceive(Map<String, Object> paramMap);
	
	/**
	 * 增加POP打印记录
	 * @param popPrint
	 * @return
	 */
	public int add(PopPrint popPrint);
	
	/**
	 * 根据运单号、操作人更新打印日志
	 * @param popPrint
	 * @return
	 */
	public int updateByWaybillCode(PopPrint popPrint);

	public int updateByWaybillOrPack(PopPrint popPrint);
}
