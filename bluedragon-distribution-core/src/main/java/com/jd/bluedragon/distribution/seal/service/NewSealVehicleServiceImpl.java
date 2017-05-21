package com.jd.bluedragon.distribution.seal.service;

import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.etms.vos.ws.VosBusinessWS;
import com.jd.etms.vos.ws.VosQueryWS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("newSealVehicleService")
public class NewSealVehicleServiceImpl implements NewSealVehicleService {
	@Autowired
	private VosQueryWS vosQueryWS;

	@Autowired
	private VosBusinessWS vosBusinessWS;

	@Override
	public CommonDto<String> seal(List<SealCarDto> sealCars) {
		CommonDto<String> sealCarInfo = vosBusinessWS.doSealCar(sealCars);
		return sealCarInfo;
	}

	@Override
	public CommonDto<PageDto<SealCarDto>> findSealInfo(SealCarDto request,PageDto<SealCarDto> pageDto) {
		CommonDto<PageDto<SealCarDto>> sealCarInfo = vosQueryWS.querySealCarPage(request,pageDto);
//		CommonDto<PageDto<SealCarDto>> sealCarInfo = new CommonDto<PageDto<SealCarDto>>();
//		sealCarInfo.setCode(1);
//		sealCarInfo.setMessage("成功");
//
//		PageDto<SealCarDto> pageDto1 = new PageDto<SealCarDto>();
//
//		List<SealCarDto> sealCarDtos = new ArrayList<SealCarDto>();
//		for(int i = 0;i<2;i++){
//			SealCarDto sealCarDto = new SealCarDto();
//			sealCarDto.setEndSiteId(i);
//			sealCarDto.setEndOrgName("北京"+i);
//			sealCarDto.setEndOrgCode(""+i);
//			sealCarDto.setStartSiteId(i);
//			sealCarDto.setCreateTime(new Date());
//			sealCarDtos.add(sealCarDto);
//		}
//
//		pageDto1.setResult(sealCarDtos);
//
//		sealCarInfo.setData(pageDto1);
		return sealCarInfo;
	}

	@Override
	public CommonDto<String> unseal(List<SealCarDto> sealCars) {
		CommonDto<String> sealCarInfo = vosBusinessWS.doDesealCar(sealCars);
		return sealCarInfo;
	}
}
