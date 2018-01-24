package com.jd.bluedragon.distribution.receive.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.base.service.KvIndexService;
import com.jd.bluedragon.distribution.receive.dao.ArReceiveDao;
import com.jd.bluedragon.distribution.receive.domain.ArReceive;
import com.jd.bluedragon.distribution.receive.service.ArReceiveService;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.transport.domain.ArSendCode;
import com.jd.bluedragon.distribution.transport.domain.ArSendCodeCondition;
import com.jd.bluedragon.distribution.transport.service.ArSendCodeService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
*
* @ClassName: ArReceiveServiceImpl
* @Description: 空铁提货表--Service接口实现
* @author wuyoude
* @date 2018年01月15日 22:51:31
*
*/
@Service("arReceiveService")
public class ArReceiveServiceImpl extends BaseService<ArReceive> implements ArReceiveService {

	private static final Logger log = Logger.getLogger(ArReceiveServiceImpl.class);

	@Autowired
	@Qualifier("arReceiveDao")
	private ArReceiveDao arReceiveDao;
	
	@Autowired
	private SendMDao sendMDao;
	
	@Autowired
	private KvIndexService kvIndexService;
	
	@Autowired
	private ArSendCodeService arSendCodeService;
	
	@Override
	public Dao<ArReceive> getDao() {
		return this.arReceiveDao;
	}
	/**
	 * 是否包含唯一键
	 * @param e
	 * @return
	 */
	protected boolean hasUniqueKey(ArReceive arReceive){
		if(arReceive.getSendRegisterId() != null && arReceive.getBoxCode() != null){
			return true;
		}
		return false;
	}
	/**
	 * 1、先根据barcode查询有操作记录的站点信息
	 * 2、查询操作站点对应的send_m数据
	 */
	@Override
	public ArSendCode getLastArSendCodeByBarcode(String barcode) {
		if(StringHelper.isEmpty(barcode)){
			return null;
		}
		List<Integer> siteCodes = kvIndexService.queryCreateSiteCodesByKey(barcode);
		if(siteCodes!=null && !siteCodes.isEmpty()){
			for(Integer siteCode:siteCodes){
				SendM sendMParam = new SendM();
				sendMParam.setBoxCode(barcode);
				sendMParam.setCreateSiteCode(siteCode);
				List<SendM> sendMs = sendMDao.findSendMByBoxCode(sendMParam);
				/**
				 * 按操作时间降序排序
				 */
				Collections.sort(sendMs, new Comparator<SendM>(){
					@Override
					public int compare(SendM o1, SendM o2) {
						if(o1 != null && o2 != null){
							return ObjectHelper.compare(o1.getOperateTime(), o2.getOperateTime());
						}else{
							return ObjectHelper.compare(o1, o2);
						}
					}
				});
				if(sendMs!=null && !sendMs.isEmpty()){
					for(SendM sendM: sendMs){
						if(StringHelper.isNotEmpty(sendM.getSendCode())){
							ArSendCodeCondition pagerCondition = new ArSendCodeCondition();
							pagerCondition.setSendCode(sendM.getSendCode());
							PagerResult<ArSendCode> rest = arSendCodeService.queryByPagerCondition(pagerCondition);
							if(rest.getTotal()>0){
								Collections.sort(rest.getRows(), new Comparator<ArSendCode>(){
									@Override
									public int compare(ArSendCode o1, ArSendCode o2) {
										if(o1 != null && o2 != null){
											return DateHelper.compare(o1.getTs(), o2.getTs());
										}else{
											return ObjectHelper.compare(o1, o2);
										}
									}
								});
								return rest.getRows().get(0);
							}
						}
					}
				}
			}
		}
		return null;
	}

}
