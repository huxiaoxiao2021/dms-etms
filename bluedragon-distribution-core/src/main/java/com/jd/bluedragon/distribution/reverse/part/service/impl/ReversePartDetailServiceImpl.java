package com.jd.bluedragon.distribution.reverse.part.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.reverse.part.domain.ReversePartDetailCondition;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.reverse.part.domain.ReversePartDetail;
import com.jd.bluedragon.distribution.reverse.part.dao.ReversePartDetailDao;
import com.jd.bluedragon.distribution.reverse.part.service.ReversePartDetailService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ReversePartDetailServiceImpl
 * @Description: 半退明细表--Service接口实现
 * @author wuyoude
 * @date 2019年02月12日 11:40:45
 *
 */
@Service("reversePartDetailService")
public class ReversePartDetailServiceImpl extends BaseService<ReversePartDetail> implements ReversePartDetailService {



	@Autowired
	@Qualifier("reversePartDetailDao")
	private ReversePartDetailDao reversePartDetailDao;

	@Autowired
	private SendDatailDao sendDatailDao;

	@Autowired
	private WaybillQueryManager waybillQueryManager;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Override
	public Dao<ReversePartDetail> getDao() {
		return this.reversePartDetailDao;
	}

	/**
	 * 获取累计发货记录
	 *
	 * @param waybillCode
	 * @param createSiteCode
	 * @return
	 */
	@Override
	public List<ReversePartDetail> queryAllSendPack(String waybillCode, String createSiteCode) {
		ReversePartDetailCondition query = new ReversePartDetailCondition();

		if(StringUtils.isBlank(waybillCode) || StringUtils.isBlank(createSiteCode) || !SerialRuleUtil.isMatchNumeric(createSiteCode)){
			return null;
		}
		query.setWaybillCode(waybillCode);
		query.setCreateSiteCode(Integer.valueOf(createSiteCode));

		return reversePartDetailDao.queryByParam(query);
	}


	/**
	 * 获取未发货包裹记录
	 *
	 * @param waybillCode
	 * @param createSiteCode
	 * @return
	 */
	@Override
	public List<String> queryNoSendPack(String waybillCode, String createSiteCode) {
		List<String> noNoSendPacks = new ArrayList<String>();

		//先获取包裹总数
		BaseEntity<BigWaybillDto> baseEntity =  waybillQueryManager.getDataByChoice(waybillCode,false,false,false,true);

		if(baseEntity.getData()==null || baseEntity.getData().getPackageList()==null ||  baseEntity.getData().getPackageList().size() == 0){
			logger.error("获取包裹数据为空"+waybillCode);
			return null;
		}

		//数组中等于1的代表已经发货 。剩余则为未发货的
		int[] packs = new int[baseEntity.getData().getPackageList().size()];

		List<ReversePartDetail> allSendPack = queryAllSendPack(waybillCode,createSiteCode);
		if(allSendPack!=null){
			for(ReversePartDetail rd : allSendPack){
				int cur = WaybillUtil.getCurrentPackageNum(rd.getPackNo());
				packs[cur-1] = 1;
			}
		}

		int i = 1;
		for(int pack : packs){
			if(pack != 1){
				noNoSendPacks.add("-"+i+"-");
			}
			i++;
		}
		return noNoSendPacks;
	}

	@Override
	public int queryByParamCount(ReversePartDetailCondition reversePartDetailCondition) {
		return reversePartDetailDao.queryByParamCount(reversePartDetailCondition);
	}

	/**
	 * 组装查询条件
	 * <p>
	 * 1.如果有批次号根据批次号获取已发货的运单号
	 *
	 * @param reversePartDetailCondition
	 */
	@Override
	public boolean makeQuery(ReversePartDetailCondition reversePartDetailCondition) {
		//如果输入批次号，则获取批次下所有发货的运单号
		if(StringUtils.isNotBlank(reversePartDetailCondition.getSendCode())){
			SendDetail query = new SendDetail();
			query.setSendCode(reversePartDetailCondition.getSendCode());
			List<SendDetail> result =sendDatailDao.queryBySendCodeAndSendType(query);
			if(result.isEmpty()){
				//不需要继续查询
				return false;
			}

			List<String> waybillCodes = new ArrayList<String>();
			for(SendDetail sd : result){
				if(!waybillCodes.contains(sd.getWaybillCode())){
					waybillCodes.add(sd.getWaybillCode());
				}
			}


		}

		return true;
	}

	/**
	 * 批量插入半退明细
	 *
	 * @param list
	 * @return
	 */
	@Override
	public boolean batchInsert(List<ReversePartDetail> list) {

		return getDao().batchInsert(list);
	}

	/**
	 * 获取导出数据
	 *
	 * @param reversePartDetailCondition
	 * @return
	 */
	@Override
	public List<List<Object>> getExportData(ReversePartDetailCondition reversePartDetailCondition) {


		List<List<Object>> resList = new ArrayList<List<Object>>();

		List<Object> heads = new ArrayList<Object>();

		heads.add("批次号");
		heads.add("发货机构");
		heads.add("发货时间");
		heads.add("运单号");
		heads.add("本次发货包裹号");
		heads.add("本次发货包裹数");

		resList.add(heads);

		//获取查询数据

		List<ReversePartDetail> queryResult =  reversePartDetailDao.queryByParam(reversePartDetailCondition);

		Map<String,List<ReversePartDetail>> resultWaybillMap = new HashMap<String, List<ReversePartDetail>>();
		if(queryResult!=null && !queryResult.isEmpty()){
			for(ReversePartDetail rpd :queryResult){
				// 批次号加有运单号组成唯一标识
				String key = rpd.getSendCode()+"@"+rpd.getWaybillCode();
				if(resultWaybillMap.containsKey(key)){
					resultWaybillMap.get(key).add(rpd);
				}else{
					List<ReversePartDetail> list = new ArrayList<ReversePartDetail>();
					list.add(rpd);
					resultWaybillMap.put(key,list);
				}
			}
		}

		for(String key : resultWaybillMap.keySet()){

			List<Object> body = new ArrayList<Object>();

			List<ReversePartDetail>  reversePartDetails = resultWaybillMap.get(key);

			String sendCode = key.split("@")[0];
			String waybillCode = key.split("@")[1];

			String sendTime = DateHelper.formatDate(reversePartDetails.get(0).getSendTime(), Constants.DATE_TIME_FORMAT);
			String siteName = reversePartDetails.get(0).getCreateSiteName();
			//组装具体内容
			body.add(sendCode);
			body.add(siteName);
			body.add(sendTime);
			body.add(waybillCode);

			StringBuilder sb = new StringBuilder();
			for(ReversePartDetail rpd : reversePartDetails){
				sb.append(rpd.getPackNo()+"|");
			}
			sb.replace(sb.length()-1,sb.length(),"");

			body.add(sb.toString());

			body.add(reversePartDetails.size());

			resList.add(body);

		}

		return resList;
	}

	public int updateReceiveTime(ReversePartDetail reversePartDetail){
		return reversePartDetailDao.updateReceiveTime(reversePartDetail);
	}

	/**
	 * 取消发货
	 * 同步发货数据
	 *
	 * @param tSendM
	 */
	@Override
	public boolean cancelPartSend(SendM tSendM) {
		try{
			Integer receiveSiteCode = tSendM.getReceiveSiteCode();
			if(receiveSiteCode == null){
				if(tSendM.getSendCode() == null){
					return true;
				}
				receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(tSendM.getSendCode());
			}

			BaseStaffSiteOrgDto receiveSite = baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);

			if(receiveSite !=null){
				String wms_type = PropertiesHelper.newInstance().getValue("wms_type");//仓储
				if(receiveSite.getSiteType().toString().equals(wms_type)){

					if(WaybillUtil.isPackageCode(tSendM.getBoxCode())){
						//
						ReversePartDetail param = new ReversePartDetail();
						param.setPackNo(tSendM.getBoxCode());
						param.setReceiveSiteCode(receiveSiteCode);
						reversePartDetailDao.updateForCancelSend(param);

					}else{
						return false;
					}

				}
			}

		}catch (Exception e){
			logger.error("取消发货更新半退明细异常"+ JsonHelper.toJson(tSendM),e);
		}
		return  true;
	}

}
