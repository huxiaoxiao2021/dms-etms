package com.jd.bluedragon.distribution.jy.service.seal.impl;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.dto.seal.request.SealVehicleReq;
import com.jd.bluedragon.common.dto.seal.response.JyAppDataSealVo;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dao.seal.JyAppDataSealCodeDao;
import com.jd.bluedragon.distribution.jy.dao.seal.JyAppDataSealDao;
import com.jd.bluedragon.distribution.jy.dao.seal.JyAppDataSealSendCodeDao;
import com.jd.bluedragon.distribution.jy.dto.seal.JyAppDataSeal;
import com.jd.bluedragon.distribution.jy.dto.seal.JyAppDataSealCode;
import com.jd.bluedragon.distribution.jy.dto.seal.JyAppDataSealSendCode;
import com.jd.bluedragon.distribution.jy.service.seal.JyAppDataSealService;
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
		pageData.setSealCodeList(jyAppDataSealSendCodeDao.querySendCodeList(sendVehicleDetailBizId));
		return pageData;
	}

	@Override
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
        sealData.setVolume(sealVehicleReq.getVolume());
        sealData.setWeight(sealVehicleReq.getWeight());
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
        if(CollectionUtils.isNotEmpty(sealVehicleReq.getSealCodes())) {
        	for(String sealCode : sealVehicleReq.getSealCodes()) {
        		JyAppDataSealCode sealCodeData = new JyAppDataSealCode();
        		sealCodeData.setSendDetailBizId(detailBizId);
        		sealCodeData.setSealCode(sealCode);
        		sealCodeData.setCreateTime(operTime);
        		sealCodes.add(sealCodeData);
        	}
        }
        if(CollectionUtils.isNotEmpty(sealVehicleReq.getBatchCodes())) {
        	for(String sendCode : sealVehicleReq.getBatchCodes()) {
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
