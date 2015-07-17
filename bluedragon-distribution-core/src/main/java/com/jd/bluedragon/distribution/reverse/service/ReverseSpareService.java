package com.jd.bluedragon.distribution.reverse.service;

import java.util.List;

import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-11-28 下午05:17:07
 * 
 *             逆向备件库按商品退货分拣处理服务
 */
public interface ReverseSpareService {

	/**
	 * 批量增加分拣记录
	 * 
	 * @param sortings
	 * @return
	 */
	public int batchAddSorting(List<Sorting> sortings,
			List<ReverseSpare> reverseSpares);
	
	/**
	 * 批量增加备件商品信息
	 * 
	 * @param reverseSpares
	 * @return
	 */
	public int batchAddOrUpdate(List<ReverseSpare> reverseSpares);
	
	public Sorting querySortingBySpareCode(Sorting sorting);
	
	public ReverseSpare queryBySpareCode(String spareCode);

	List<ReverseSpare> queryByWayBillCode(String waybillCode,String sendCode);
	
	List<ReverseSpare> queryBySpareTranCode(String spareTranCode);

}
