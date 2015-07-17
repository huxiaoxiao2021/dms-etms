package com.jd.bluedragon.distribution.popAbnormal.service;

import java.util.List;

import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormalDetail;


/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-21 下午02:40:42
 * 
 *             POP收货差异订单Service New
 */
public interface PopAbnormalDetailService {

	public PopAbnormalDetail findByObj(PopAbnormalDetail popAbnormalDetail);
	
	public int add(PopAbnormalDetail popAbnormalDetail);

	public List<PopAbnormalDetail> findListByAbnormalId(Long abnormalId);
}
