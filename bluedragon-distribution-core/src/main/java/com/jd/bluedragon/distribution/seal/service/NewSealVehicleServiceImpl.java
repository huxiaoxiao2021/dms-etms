package com.jd.bluedragon.distribution.seal.service;

import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.etms.vos.ws.VosBusinessWS;
import com.jd.etms.vos.ws.VosQueryWS;
import com.jd.etms.vts.dto.VtsTransportResourceDto;
import com.jd.etms.vts.ws.VtsQueryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("newSealVehicleService")
public class NewSealVehicleServiceImpl implements NewSealVehicleService {
	@Autowired
	private VosQueryWS vosQueryWS;

	@Autowired
	private VosBusinessWS vosBusinessWS;

	@Autowired
	private VtsQueryWS vtsQueryWS;

	@Autowired
	private SendMDao sendMDao;

	@Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.seal", mState = {JProEnum.TP})
	public CommonDto<String> seal(List<SealCarDto> sealCars) {
		CommonDto<String> sealCarInfo = vosBusinessWS.doSealCar(sealCars);
		return sealCarInfo;
	}

	@Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.findSealInfo", mState = {JProEnum.TP})
	public CommonDto<PageDto<SealCarDto>> findSealInfo(SealCarDto request,PageDto<SealCarDto> pageDto) {
		CommonDto<PageDto<SealCarDto>> sealCarInfo = vosQueryWS.querySealCarPage(request,pageDto);
		return sealCarInfo;
	}

	@Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.unseal", mState = {JProEnum.TP})
	public CommonDto<String> unseal(List<SealCarDto> sealCars) {
		CommonDto<String> sealCarInfo = vosBusinessWS.doDesealCar(sealCars);
		return sealCarInfo;
	}

	@Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.isBatchCodeHasSealed", mState = {JProEnum.TP})
	public CommonDto<Boolean> isBatchCodeHasSealed(String batchCode) {
		CommonDto<Boolean> isSealed = vosQueryWS.isBatchCodeHasSealed(batchCode);
		return isSealed;
	}

	@Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vts.getTransportResourceByTransCode", mState = {JProEnum.TP})
	public com.jd.etms.vts.dto.CommonDto<VtsTransportResourceDto> getTransportResourceByTransCode(String batchCode) {
		com.jd.etms.vts.dto.CommonDto<VtsTransportResourceDto> dto = vtsQueryWS.getTransportResourceByTransCode(batchCode);
		return dto;
	}

	public boolean checkSendIsExsite( String sendCode) {
		boolean result = true;
		SendM sendM = sendMDao.selectOneBySiteAndSendCode(null, sendCode);
		// 查询不到该批次
		if (sendM == null) {
			result = false;
		}
		return result;
	}
}
