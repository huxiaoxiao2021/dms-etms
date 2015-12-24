package com.jd.bluedragon.distribution.popPrint.service.impl;

import com.jd.bluedragon.distribution.api.request.InspectionPOPRequest;
import com.jd.bluedragon.distribution.popPrint.dao.PopSigninDao;
import com.jd.bluedragon.distribution.popPrint.domain.PopSignin;
import com.jd.bluedragon.distribution.popPrint.dto.PopSigninDto;
import com.jd.bluedragon.distribution.popPrint.service.PopSigninService;
import com.jd.bluedragon.utils.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class PopSigninServiceImpl implements PopSigninService {
	@Autowired
	private PopSigninDao popSigninDao;

	@Override
	public int insert(PopSignin popSignin) {
		return this.popSigninDao.insert(popSignin);
	}
	@Override
	public int insert(InspectionPOPRequest popRequest){
		PopSignin popSignin = new PopSignin();
		popSignin.setCreateSiteCode(popRequest.getSiteCode());
		popSignin.setCreateSiteName(popRequest.getSiteName());
		popSignin.setCreateUser(popRequest.getUserName());
		popSignin.setCreateUserCode(popRequest.getUserCode());
		popSignin.setExpressCode(popRequest.getExpressCode());
		popSignin.setExpressName(popRequest.getExpressName());
		Date operateTime =DateHelper.parseDate(popRequest.getOperateTime(), "yyyy-MM-dd HH:mm:ss");
		popSignin.setOperateTime(operateTime);
		popSignin.setQueueNo(popRequest.getQueueNo());
		popSignin.setThirdWaybillCode(popRequest.getBoxCode());
		 int n =this.popSigninDao.update(popSignin);
		 if(n<1){
			n= this.popSigninDao.insert(popSignin);
		 }
		 return n;
		
	}

	@Override
	public List<PopSignin> getPopSigninList(PopSigninDto popSigninDto) {
		return this.popSigninDao.getPopSigninList(popSigninDto);
	}
	@Override
	public int update(PopSignin popSignin) {
		return this.popSigninDao.update(popSignin);
	}
	@Override
	public int getCount(PopSigninDto popSigninDto) {
		return this.popSigninDao.getCount(popSigninDto);
	}

}
