package com.jd.bluedragon.distribution.wss.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.departure.domain.SendBox;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.seal.domain.SealVehicle;
import com.jd.bluedragon.distribution.seal.service.SealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.wss.dto.*;
import com.jd.bluedragon.distribution.wss.service.DistributionWssService;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class DistributionWssServiceImpl implements DistributionWssService {

	private final static Logger log = LoggerFactory.getLogger(DistributionWssServiceImpl.class);

	@Autowired
	DepartureService departureService;

	@Autowired
	private SealVehicleService sealVehicleService;

	@Autowired
	DeliveryService deliveryService;

	/**
	 * 1 按照批次查 2 按照箱号查
	 */
	public List<BoxSummaryDto> getBoxSummary(String code, Integer type, Integer siteCode) {

		List<BoxSummaryDto> dtos = new ArrayList<BoxSummaryDto>();
		if (code == null || type != 1 && type != 2) {
			return dtos;
		}
		List<SendBox> sendBoxs = new ArrayList<SendBox>();
		try {
			if (type == 1) {
				// 按照批次查
				sendBoxs = departureService.getSendInfo(code);
			} else if (type == 2) {
				// 按照箱号查
				sendBoxs = departureService.getSendBoxInfo(code, siteCode);
			}
			if (sendBoxs == null || sendBoxs.size() == 0) {
				return dtos;
			} else {
				Map<String, List<SendBox>> boxMap = new HashMap<String, List<SendBox>>();
				for (SendBox sendBox : sendBoxs) {
					if (boxMap.containsKey(sendBox.getBoxCode())) {
						List<SendBox> boxList = boxMap.get(sendBox.getBoxCode());
						boxList.add(sendBox);
						boxMap.put(sendBox.getBoxCode(), boxList);
					} else {
						List<SendBox> boxList = new ArrayList<SendBox>();
						boxList.add(sendBox);
						boxMap.put(sendBox.getBoxCode(), boxList);
					}
				}
				Iterator<Map.Entry<String, List<SendBox>>> it = boxMap.entrySet().iterator();
				while (it.hasNext()) {
					BoxSummaryDto dto = new BoxSummaryDto();
					Map.Entry<String, List<SendBox>> entity = it.next();
					List<SendBox> packages = entity.getValue();
					String boxCode = entity.getKey();
					Set<String> waybillMap = new HashSet<String>();
					int packageNum = 0;
					String sendCode = null;
					String sendUser = null;
					for (SendBox sendBox : packages) {
						waybillMap.add(sendBox.getWaybillCode());
						packageNum++;
						sendCode = sendBox.getSendCode();
						sendUser = sendBox.getSendUser();
					}
					dto.setBoxCode(boxCode);
					dto.setPackagebarNum(packageNum);
					dto.setWaybillNum(waybillMap.size());
					dto.setSendCode(sendCode);
					dto.setSendUser(sendUser);
					dtos.add(dto);
				}
			}
		} catch (Exception e) {
			DistributionWssServiceImpl.log.error("获得箱子列表时失败【getBoxSummary】: " + e);
		}
		return dtos;
	}

	/**
	 * 1 按照批次查 2 按照箱号查
	 */
	public List<PackageSummaryDto> getPackageSummary(String code, Integer type, Integer siteCode) {

		List<PackageSummaryDto> dtos = new ArrayList<PackageSummaryDto>();
		if (code == null || type != 1 && type != 2) {
			return dtos;
		}
		try {
			List<SendBox> sendBoxs = new ArrayList<SendBox>();
			if (type == 1) {
				// 按照批次查
				sendBoxs = departureService.getSendInfo(code);
			} else if (type == 2) {
				// 按照箱号查
				sendBoxs = departureService.getSendBoxInfo(code, siteCode);
			}
			// if (sendBoxs == null || sendBoxs.size() == 0) {
			if (sendBoxs != null && sendBoxs.size() > 0) {
				for (SendBox sendBox : sendBoxs) {
					dtos.add(toPackageSummary(sendBox));
				}
			}
		} catch (Exception e) {
			DistributionWssServiceImpl.log.error("获得包裹列表时失败【PackageSummaryDto】: " + e);
		}
		return dtos;
	}

	@Override
    public PageDto<PackageSummaryDto> queryPageSendInfoByBatchCode(PageDto pageDto, String batchCode){
        PageDto<SendBox> pageDto2 = departureService.queryPageSendInfoByBatchCode(pageDto,batchCode);
        List<SendBox> list = pageDto2.getResult();
        PageDto<PackageSummaryDto> resultPage = new PageDto<>(pageDto.getCurrentPage(),pageDto.getPageSize());
        if(CollectionUtils.isEmpty(list)){
            return resultPage;
        }
        resultPage.setTotalRow(pageDto2.getTotalRow());
        List<PackageSummaryDto> resultList = Lists.newArrayList();
        for(SendBox sendBox : list){
            PackageSummaryDto dto = new PackageSummaryDto();
            dto.setSendCode(sendBox.getSendCode());
            dto.setBoxCode(sendBox.getBoxCode());
            dto.setWaybillCode(sendBox.getWaybillCode());
            dto.setSendUser(sendBox.getSendUser());
            dto.setSendTime(sendBox.getSendTime());
            dto.setPackagebarcode(sendBox.getPackageBarcode());
            resultList.add(dto);
        }
        resultPage.setResult(resultList);
        return resultPage;
    }
	
	/**
	 * 1 按照发车号查询 2 按照三方运单号查询
	 */
	public List<DepartureWaybillDto> getWaybillsByDeparture(String code,
			Integer type) {
		List<DepartureWaybillDto> dtos = new ArrayList<DepartureWaybillDto>();

		if (StringHelper.isEmpty(code) || (type == null)) {
			log.warn("查询参数不正确：code {},type {}",code, type);
			return dtos;
		}

		if (type != 1 && type != 2) {
			log.warn("查询类型不支持:{}", type);
			return dtos;
		}

		try {
			List<SendDetail> sendDetails = departureService
					.getWaybillsByDeparture(code, type);
			if (sendDetails != null) {
				for (SendDetail sendDetail : sendDetails) {
					DepartureWaybillDto dto = new DepartureWaybillDto();
					if (sendDetail != null
							&& sendDetail.getWaybillCode() != null)
						dto.setWaybillCode(sendDetail.getWaybillCode());
					dtos.add(dto);
				}
			}
		} catch (Exception e) {
			log.error("根据发车号或者三方运单号查询明细失败: ", e);
		}
		return dtos;
	}

	private PackageSummaryDto toPackageSummary(SendBox sendBox) {
		PackageSummaryDto packageSummaryDto = new PackageSummaryDto();
		packageSummaryDto.setBoxCode(sendBox.getBoxCode());
		packageSummaryDto.setPackagebarcode(sendBox.getPackageBarcode());
		packageSummaryDto.setSendCode(sendBox.getSendCode());
		packageSummaryDto.setSendTime(sendBox.getSendTime());
		packageSummaryDto.setSendUser(sendBox.getSendUser());
		packageSummaryDto.setWaybillCode(sendBox.getWaybillCode());
		return packageSummaryDto;
	}

	/**
	 * 通过封车号查询封车信息
	 *
	 * @param sealCode
	 * @return
	 */
	@Override
	public SealVehicleSummaryDto findSealByCodeSummary(String sealCode) {
		try {
			return toSealCodeSummary(sealVehicleService.findBySealCode(sealCode));
		} catch (Exception e) {
			DistributionWssServiceImpl.log.error(
			        "获取解封车信息失败【SealVehicleSummaryDto】：" , e);
			return new SealVehicleSummaryDto();
		}
	}

	private SealVehicleSummaryDto toSealCodeSummary(SealVehicle sealVehicle) {
		SealVehicleSummaryDto sealVehicleSummaryDto = new SealVehicleSummaryDto();
		sealVehicleSummaryDto.setCode(sealVehicle.getCode());
		sealVehicleSummaryDto.setCreateTime(sealVehicle.getCreateTime());
		sealVehicleSummaryDto.setCreateUser(sealVehicle.getCreateUser());
		sealVehicleSummaryDto.setCreateUserCode(sealVehicle.getCreateUserCode());
		sealVehicleSummaryDto.setDriver(sealVehicle.getDriver());
		try {
			sealVehicleSummaryDto.setDriverCode(Integer.valueOf(sealVehicle.getDriverCode()));
		} catch (Exception e) {
			sealVehicleSummaryDto.setDriverCode(0);
		}
		sealVehicleSummaryDto.setId(sealVehicle.getId());
		sealVehicleSummaryDto.setVehicleCode(sealVehicle.getVehicleCode());
		return sealVehicleSummaryDto;
	}

	public List<WaybillCodeSummatyDto> findDeliveryPackageBySiteSummary(int siteid, Date startTime,
	        Date endTime) {
		List<WaybillCodeSummatyDto> dtos = new ArrayList<WaybillCodeSummatyDto>();
		if (startTime == null || endTime == null) {
			return dtos;
		}
		try {
			SendDetail sendDetail = new SendDetail();
			sendDetail.setReceiveSiteCode(siteid);
			sendDetail.setOperateTime(startTime);
			sendDetail.setUpdateTime(endTime);
			List<SendDetail> tOrderList = deliveryService.findDeliveryPackageBySite(sendDetail);
			Map<String, Integer> tMap = new HashMap<String, Integer>();
			if (tOrderList != null && !tOrderList.isEmpty()) {
				for (SendDetail send : tOrderList) {
					tMap.put(send.getWaybillCode(), getPackageNum(send.getPackageBarcode()));
				}
				Iterator<Map.Entry<String, Integer>> it = tMap.entrySet().iterator();
				while (it.hasNext()) {
					WaybillCodeSummatyDto dto = new WaybillCodeSummatyDto();
					Map.Entry<String, Integer> entity = it.next();
					Integer packages = entity.getValue();
					String waybillcode = entity.getKey();
					dto.setPackagebarNum(packages);
					dto.setWaybillCode(waybillcode);
					dtos.add(dto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return dtos;
		}
		return dtos;
	}

	public int getPackageNum(String packageBarcode) {
		int sum = 1;
		if (packageBarcode.indexOf("S") > 0 && packageBarcode.indexOf("H") > 0) {
			sum = Integer.valueOf(packageBarcode.substring(packageBarcode.indexOf("S") + 1,
			        packageBarcode.indexOf("H")));
		} else if (packageBarcode.indexOf("-") > 0
		        && (packageBarcode.split("-").length == 3 || packageBarcode.split("-").length == 4)) {
			sum = Integer.valueOf(packageBarcode.split("-")[2]);
		}
		return sum;
	}

	public List<WaybillCodeSummatyDto> findDeliveryPackageByCodeSummary(int siteid,
	        String waybillCode) {
		List<WaybillCodeSummatyDto> dtos = new ArrayList<WaybillCodeSummatyDto>();
		if (waybillCode == null) {
			return dtos;
		}
		try {
			SendDetail sendDetail = new SendDetail();
			sendDetail.setReceiveSiteCode(siteid);
			sendDetail.setWaybillCode(waybillCode);
			List<SendDetail> tOrderList = deliveryService.findDeliveryPackageByCode(sendDetail);
			Map<String, Integer> tMap = new HashMap<String, Integer>();
			if (tOrderList != null && !tOrderList.isEmpty()) {
				for (SendDetail send : tOrderList) {
					tMap.put(send.getWaybillCode(), getPackageNum(send.getPackageBarcode()));
				}
				Iterator<Map.Entry<String, Integer>> it = tMap.entrySet().iterator();
				while (it.hasNext()) {
					WaybillCodeSummatyDto dto = new WaybillCodeSummatyDto();
					Map.Entry<String, Integer> entity = it.next();
					Integer packages = entity.getValue();
					String waybillcode = entity.getKey();
					dto.setPackagebarNum(packages);
					dto.setWaybillCode(waybillcode);
					dtos.add(dto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return dtos;
		}
		return dtos;
	}
}
