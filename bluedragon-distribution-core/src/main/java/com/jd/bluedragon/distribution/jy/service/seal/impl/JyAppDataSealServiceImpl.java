package com.jd.bluedragon.distribution.jy.service.seal.impl;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.utils.jddl.DmsJddlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.common.dto.seal.request.SealVehicleReq;
import com.jd.bluedragon.common.dto.seal.response.JyAppDataSealSendCodeVo;
import com.jd.bluedragon.common.dto.seal.response.JyAppDataSealVo;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dao.seal.JyAppDataSealCodeDao;
import com.jd.bluedragon.distribution.jy.dao.seal.JyAppDataSealDao;
import com.jd.bluedragon.distribution.jy.dao.seal.JyAppDataSealSendCodeDao;
import com.jd.bluedragon.distribution.jy.dto.seal.JyAppDataSeal;
import com.jd.bluedragon.distribution.jy.dto.seal.JyAppDataSealCode;
import com.jd.bluedragon.distribution.jy.dto.seal.JyAppDataSealSendCode;
import com.jd.bluedragon.distribution.jy.service.seal.JyAppDataSealService;
import com.jd.bluedragon.distribution.sendCode.DMSSendCodeJSFService;
import com.jd.bluedragon.distribution.sendCode.domain.HugeSendCodeEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.common.dto.base.request.User;

/**
 * @ClassName: JyAppDataSealServiceImpl
 * @Description: 作业app-封车主页面数据表--Service接口实现
 * @author wuyoude
 * @date 2022年09月27日 15:38:11
 *
 */
@Service("jyAppDataSealService")
public class JyAppDataSealServiceImpl implements JyAppDataSealService {

	private static final Logger logger = LoggerFactory.getLogger(JyAppDataSealServiceImpl.class);

	@Autowired
	@Qualifier("jyAppDataSealDao")
	private JyAppDataSealDao jyAppDataSealDao;
	
	@Autowired
	@Qualifier("jyAppDataSealSendCodeDao")
	private JyAppDataSealSendCodeDao jyAppDataSealSendCodeDao;
	
	@Autowired
	@Qualifier("jyAppDataSealCodeDao")
	private JyAppDataSealCodeDao jyAppDataSealCodeDao;
	
	@Autowired
	@Qualifier("dmsSendCodeJSFService")
	DMSSendCodeJSFService dmsSendCodeJSFService;

	@Override
	public JyAppDataSealVo loadSavedPageData(String sendVehicleDetailBizId) {
		JyAppDataSeal sealData = jyAppDataSealDao.queryByDetailBizId(sendVehicleDetailBizId);
		if(sealData == null) {
			return null;
		}
		JyAppDataSealVo pageData = new JyAppDataSealVo();
		pageData.setItemSimpleCode(sealData.getItemSimpleCode());
		pageData.setTransportCode(sealData.getTransportCode());
		pageData.setPalletCount(sealData.getPalletCount());
		pageData.setWeight(sealData.getWeight());
		pageData.setVolume(sealData.getVolume());
		pageData.setSealCodeList(jyAppDataSealCodeDao.querySealCodeList(sendVehicleDetailBizId));
		pageData.setSendCodeList(loadSendCodeList(sendVehicleDetailBizId));
		return pageData;
	}
	/**
	 * 加载批次信息
	 * @param sendVehicleDetailBizId
	 * @return
	 */
	private List<JyAppDataSealSendCodeVo> loadSendCodeList(String sendVehicleDetailBizId){
		List<String> sendCodeList = jyAppDataSealSendCodeDao.querySendCodeList(sendVehicleDetailBizId);
		List<JyAppDataSealSendCodeVo> sendCodeVoList = new ArrayList<JyAppDataSealSendCodeVo>();
		//加载重量、体积信息
		if(CollectionUtils.isNotEmpty(sendCodeList)) {
			com.jd.bluedragon.distribution.jsf.domain.InvokeResult<Map<String,HugeSendCodeEntity>> weightAndVolumeInfo = dmsSendCodeJSFService.queryWeightAndVolumeInfoBySendCodes(sendCodeList);
			boolean needSetWeightAndVolume = false;
			if(weightAndVolumeInfo != null
					&& weightAndVolumeInfo.getData()!= null
					&& !weightAndVolumeInfo.getData().isEmpty()) {
				needSetWeightAndVolume = true;
			}
			for(String sendCode:sendCodeList) {
				JyAppDataSealSendCodeVo vo = new JyAppDataSealSendCodeVo();
				vo.setSendCode(sendCode);
				if(needSetWeightAndVolume) {
					HugeSendCodeEntity weightVo = weightAndVolumeInfo.getData().get(sendCode);
					if(weightVo != null) {
						if(weightVo.getVolume() != null) {
							vo.setVolume(new BigDecimal(weightVo.getVolume()));
						}else {
							vo.setVolume(new BigDecimal(0));
						}
						if(weightVo.getWeight() != null) {
							vo.setWeight(new BigDecimal(weightVo.getWeight()));
						}else {
							vo.setWeight(new BigDecimal(0));
						}
					}
				}
				sendCodeVoList.add(vo);
			}
		}
		return sendCodeVoList;
	}
	@Override
	@Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public InvokeResult<Boolean> savePageData(SealVehicleReq sealVehicleReq) {
		logger.info("jy保存封车数据,saveSealVehicle:{}",JsonHelper.toJson(sealVehicleReq));
		String detailBizId = sealVehicleReq.getSendVehicleDetailBizId();
		JyAppDataSeal oldData = jyAppDataSealDao.queryByDetailBizId(detailBizId);
		boolean isSaved = false;
		if(oldData != null) {
			isSaved = true;
		}
        JyAppDataSeal sealData = new JyAppDataSeal();
        sealData.setSendDetailBizId(detailBizId);
        sealData.setSendVehicleBizId(sealVehicleReq.getSendVehicleBizId());
        sealData.setItemSimpleCode(sealVehicleReq.getItemSimpleCode());
        sealData.setTransportCode(sealVehicleReq.getTransportCode());
        sealData.setPalletCount(sealVehicleReq.getPalletCount());
        if(sealVehicleReq.getVolume() != null) {
        	sealData.setVolume(new BigDecimal(sealVehicleReq.getVolume()));
        }
        if(sealVehicleReq.getWeight() != null) {
        	sealData.setWeight(new BigDecimal(sealVehicleReq.getWeight()));
        }
        User operUser = sealVehicleReq.getUser();
        String operUserErp = "";
        String operUserName = "";
        Date operTime = new Date();
        if(operUser != null) {
        	operUserErp = operUser.getUserErp();
        	operUserName = operUser.getUserName();
        }
        List<JyAppDataSealCode> sealCodes = new ArrayList<JyAppDataSealCode>();
        List<JyAppDataSealSendCode> sendCodes = new ArrayList<JyAppDataSealSendCode>();
        if(CollectionUtils.isNotEmpty(sealVehicleReq.getScannedSealCodes())) {
        	for(String sealCode : sealVehicleReq.getScannedSealCodes()) {
        		JyAppDataSealCode sealCodeData = new JyAppDataSealCode();
        		sealCodeData.setSendDetailBizId(detailBizId);
        		sealCodeData.setSealCode(sealCode);
        		sealCodeData.setCreateTime(operTime);
        		sealCodes.add(sealCodeData);
        	}
        }
        if(CollectionUtils.isNotEmpty(sealVehicleReq.getScannedBatchCodes())) {
        	for(String sendCode : sealVehicleReq.getScannedBatchCodes()) {
        		JyAppDataSealSendCode sealCodeData = new JyAppDataSealSendCode();
        		sealCodeData.setSendDetailBizId(detailBizId);
        		sealCodeData.setSendCode(sendCode);
        		sealCodeData.setCreateTime(operTime);
        		sendCodes.add(sealCodeData);
        	}
        }
        if(isSaved){
        	sealData.setId(oldData.getId());
        	sealData.setUpdateTime(operTime);
        	sealData.setUpdateUserErp(operUserErp);
        	sealData.setUpdateUserName(operUserName);
        	jyAppDataSealDao.updateById(sealData);
        	//已保存，删除旧批次
        	jyAppDataSealCodeDao.deleteByDetailBizId(detailBizId);
        	jyAppDataSealSendCodeDao.deleteByDetailBizId(detailBizId);
        }else {
        	sealData.setCreateTime(operTime);
        	sealData.setCreateUserErp(operUserErp);
        	sealData.setCreateUserName(operUserName);
        	jyAppDataSealDao.insert(sealData);
        }
        /**
         * 明细保存sealCodes
         */
        if(CollectionUtils.isNotEmpty(sealCodes)) {
        	jyAppDataSealCodeDao.batchInsert(sealCodes);
        }
        /**
         * 明细保存sendCodes
         */
        if(CollectionUtils.isNotEmpty(sendCodes)) {
        	jyAppDataSealSendCodeDao.batchInsert(sendCodes);
        }
        return new InvokeResult<Boolean>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
	}
}
