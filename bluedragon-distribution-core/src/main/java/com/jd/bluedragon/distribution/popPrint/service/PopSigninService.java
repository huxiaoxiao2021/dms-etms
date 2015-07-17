package com.jd.bluedragon.distribution.popPrint.service;

import java.util.List;

import com.jd.bluedragon.distribution.api.request.InspectionPOPRequest;
import com.jd.bluedragon.distribution.popPrint.domain.PopSignin;
import com.jd.bluedragon.distribution.popPrint.dto.PopSigninDto;
/**
 * 
* 类描述： POP签收
* 创建者： libin
* 项目名称： bluedragon-distribution-core
* 创建时间： 2013-1-24 下午2:42:02
* 版本号： v1.0
 */
public interface PopSigninService {
	public int insert(PopSignin popSignin);
	public int insert(InspectionPOPRequest popRequest);
	public int update(PopSignin popSignin);
	public List<PopSignin> getPopSigninList(PopSigninDto popSigninDto);
	public int getCount(PopSigninDto popSigninDto);
}
