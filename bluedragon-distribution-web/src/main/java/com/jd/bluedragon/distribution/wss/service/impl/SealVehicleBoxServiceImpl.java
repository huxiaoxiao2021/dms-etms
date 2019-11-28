package com.jd.bluedragon.distribution.wss.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.seal.domain.SealVehicle;
import com.jd.bluedragon.distribution.seal.service.SealBoxService;
import com.jd.bluedragon.distribution.seal.service.SealVehicleService;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.distribution.wss.dto.SealBoxDto;
import com.jd.bluedragon.distribution.wss.dto.SealVehicleDto;
import com.jd.bluedragon.distribution.wss.service.SealVehicleBoxService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.ObjectMapHelper;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-8-13 下午03:02:51
 * 
 *             封箱封车WSS服务实现
 */
public class SealVehicleBoxServiceImpl implements SealVehicleBoxService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SealVehicleService sealVehicleService;

	@Autowired
	private SealBoxService sealBoxService;

	@Override
	public BaseEntity<Map<String, Integer>> batchAddSealBox(
			List<SealBoxDto> sealBoxList) {
		BaseEntity<Map<String, Integer>> baseEntity = new BaseEntity<Map<String, Integer>>();
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		if (sealBoxList == null || sealBoxList.isEmpty()) {
			baseEntity.setCode(BaseEntity.CODE_PARAM_ERROR);
			baseEntity.setMessage(BaseEntity.MESSAGE_PARAM_ERROR);
			return baseEntity;
		}
		if (!BusinessHelper.checkIntNumRange(sealBoxList.size())) {
			// 封箱 Size 超过规定大小，不处理
			baseEntity.setCode(BaseEntity.CODE_SIZE_ERROR);
			baseEntity.setMessage(BaseEntity.MESSAGE_SIZE_ERROR);
			this.log.warn("batchAddSealBox-->批量保存封箱信息 传入数组或集合大小超过限制");
			return baseEntity;
		}
		try {
			int resultCount = 0;
			for (SealBoxDto sealBoxDto : sealBoxList) {
				SealBox sealBox = new SealBox();
				try {
					sealBox = this.toSealBox(sealBoxDto);
				} catch (Exception e) {
					this.log.error("batchAddSealBox-->批量保存封箱信息 复制封车对象异常：{}", JsonHelper.toJson(sealBoxDto), e);
				}
				//一般不会为空
				if(sealBox == null){
					continue;
				}
				if (checkSealBox(sealBox)) {
					try {
						int tempResult = this.sealBoxService
								.addSealBox(sealBox);
						if (tempResult == 1) {
							resultMap.put(sealBox.getCode(),
									BaseEntity.CODE_SUCCESS);
							resultCount++;
							continue;
						}

					} catch (Exception e) {
						this.log.error("batchAddSealBox-->批量保存封箱信息 调用服务异常：{}",JsonHelper.toJson(sealBox), e);
						baseEntity.setCode(BaseEntity.CODE_SERVICE_ERROR);
						baseEntity.setMessage(BaseEntity.MESSAGE_SERVICE_ERROR);
						// 记录异常相关数据：
					}
					resultMap.put(sealBox.getCode(),
							BaseEntity.CODE_SERVICE_ERROR);
				} else {
					// 参数有误
					resultMap.put(sealBox.getCode(),
							BaseEntity.CODE_PARAM_ERROR);
				}
			}
			this.log.debug("batchAddSealBox-->批量保存封箱信息 调用服务成功，应该执行数据为【{}】，成功数据为【{}】",sealBoxList.size(),resultCount);
			baseEntity.setCode(BaseEntity.CODE_SUCCESS);
			baseEntity.setMessage(BaseEntity.MESSAGE_SUCCESS);
			baseEntity.setData(resultMap);
		} catch (Exception e) {
			this.log.error("batchAddSealBox-->批量保存封箱信息 调用服务异常:{}",JsonHelper.toJson(sealBoxList), e);
			baseEntity.setCode(BaseEntity.CODE_SERVICE_ERROR);
			baseEntity.setMessage(BaseEntity.MESSAGE_SERVICE_ERROR);
		}
		return baseEntity;
	}

	@Override
	public BaseEntity<Map<String, Integer>> batchAddSealVehicle(
			List<SealVehicleDto> sealVehicleList) {
		BaseEntity<Map<String, Integer>> baseEntity = new BaseEntity<Map<String, Integer>>();
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		if (sealVehicleList == null || sealVehicleList.isEmpty()) {
			baseEntity.setCode(BaseEntity.CODE_PARAM_ERROR);
			baseEntity.setMessage(BaseEntity.MESSAGE_PARAM_ERROR);
			return baseEntity;
		}
		if (BusinessHelper.checkIntNumNotInRange(sealVehicleList.size())) {
			// 封车 Size 超过规定大小，不处理
			baseEntity.setCode(BaseEntity.CODE_SIZE_ERROR);
			baseEntity.setMessage(BaseEntity.MESSAGE_SIZE_ERROR);
			this.log.warn("batchAddSealVehicle WSS服务-->批量保存封车信息 传入数组或集合大小超过限制");
			return baseEntity;
		}
		try {
			int resultCount = 0;
			for (SealVehicleDto sealVehicleDto : sealVehicleList) {
				SealVehicle sealVehicle = new SealVehicle();
				try {
					sealVehicle = this.toSealVehicle(sealVehicleDto);
				} catch (Exception e) {
					this.log.error("batchAddSealVehicle WSS服务-->批量增加封车信息 复制封车对象异常：{}",JsonHelper.toJson(sealVehicleDto), e);
				}
				if(sealVehicle == null){
				    continue;
                }
				if (checkSealVehicle(sealVehicle, true)) {
					try {
						int tempResult = this.sealVehicleService
								.addSealVehicle(sealVehicle);
						if (tempResult == Constants.RESULT_SUCCESS) {
							resultMap.put(sealVehicle.getCode(),
									BaseEntity.CODE_SUCCESS);
							resultCount++;
							continue;
						}

					} catch (Exception e) {
						this.log.error("batchAddSealVehicle WSS服务-->批量增加封车信息 调用服务异常:{}",JsonHelper.toJson(sealVehicle), e);
						baseEntity.setCode(BaseEntity.CODE_SERVICE_ERROR);
						baseEntity.setMessage(BaseEntity.MESSAGE_SERVICE_ERROR);
						// 记录异常相关数据：
					}
					resultMap.put(sealVehicle.getCode(),
							BaseEntity.CODE_SUCCESS_NO);
				} else {
					// 参数有误
					resultMap.put(sealVehicle.getCode(),
							BaseEntity.CODE_PARAM_ERROR);
				}
			}
			this.log.debug("batchAddSealVehicle WSS服务-->批量增加封车信息 调用服务成功，应该执行数据为【{}】，成功数据为【{}】",sealVehicleList.size(),resultCount);
			baseEntity.setCode(BaseEntity.CODE_SUCCESS);
			baseEntity.setMessage(BaseEntity.MESSAGE_SUCCESS);
			baseEntity.setData(resultMap);
		} catch (Exception e) {
			this.log.error("batchAddSealVehicle WSS服务-->批量增加封车信息 调用服务异常：{}",JsonHelper.toJson(sealVehicleList), e);
			baseEntity.setCode(BaseEntity.CODE_SERVICE_ERROR);
			baseEntity.setMessage(BaseEntity.MESSAGE_SERVICE_ERROR);
		}
		return baseEntity;
	}

	@Override
	public BaseEntity<Map<String, Integer>> batchUpdateSealVehicle(
			List<SealVehicleDto> sealVehicleList) {
		BaseEntity<Map<String, Integer>> baseEntity = new BaseEntity<Map<String, Integer>>();
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		if (sealVehicleList == null || sealVehicleList.isEmpty()) {
			baseEntity.setCode(BaseEntity.CODE_PARAM_ERROR);
			baseEntity.setMessage(BaseEntity.MESSAGE_PARAM_ERROR);
			return baseEntity;
		}
		if (BusinessHelper.checkIntNumNotInRange(sealVehicleList.size())) {
			// 封车 Size 超过规定大小，不处理
			baseEntity.setCode(BaseEntity.CODE_SIZE_ERROR);
			baseEntity.setMessage(BaseEntity.MESSAGE_SIZE_ERROR);
			this.log.warn("batchUpdateSealVehicle WSS服务-->批量验证并保存解封车信息 传入数组或集合大小超过限制");
			return baseEntity;
		}
		try {
			int resultCount = 0;
			for (SealVehicleDto sealVehicleDto : sealVehicleList) {
				SealVehicle sealVehicle = new SealVehicle();
				try {
					sealVehicle = this.toSealVehicle(sealVehicleDto);
				} catch (Exception e) {
					this.log.error("batchUpdateSealVehicle WSS服务-->批量验证并保存解封车信息 复制封车对象异常：{}",JsonHelper.toJson(sealVehicleDto),e);
				}
				if(sealVehicle == null){
				    continue;
                }
				if (checkSealVehicle(sealVehicle, false)) {
					try {
						int tempResult = this.sealVehicleService
								.updateSealVehicle(sealVehicle);
						if (tempResult == Constants.RESULT_SUCCESS) {
							resultMap.put(sealVehicle.getCode(),
									BaseEntity.CODE_SUCCESS);
							resultCount++;
							continue;
						} else {
							this.log.warn("batchUpdateSealVehicle WSS服务-->批量验证并保存解封车信息失败，封车信息不存在，封车号【{}】", sealVehicle.getCode());
							resultMap.put(sealVehicle.getCode(), BaseEntity.CODE_SUCCESS_NO);
							resultCount++;
							continue;
						}

					} catch (Exception e) {
						this.log.error("batchUpdateSealVehicle WSS服务-->批量验证并保存解封车信息 调用服务异常：{}",JsonHelper.toJson(sealVehicle), e);
						baseEntity.setCode(BaseEntity.CODE_SERVICE_ERROR);
						baseEntity.setMessage(BaseEntity.MESSAGE_SERVICE_ERROR);
						// 记录异常相关数据：
					}
					resultMap.put(sealVehicle.getCode(),
							BaseEntity.CODE_SUCCESS_NO);
				} else {
					// 参数有误
					resultMap.put(sealVehicle.getCode(),
							BaseEntity.CODE_PARAM_ERROR);
				}
			}
			log.debug("batchUpdateSealVehicle WSS服务-->批量验证并保存解封车信息 调用服务成功，应该执行数据为【{}】，成功数据为【{}】",sealVehicleList.size(),resultCount);
			baseEntity.setCode(BaseEntity.CODE_SUCCESS);
			baseEntity.setMessage(BaseEntity.MESSAGE_SUCCESS);
			baseEntity.setData(resultMap);
		} catch (Exception e) {
			this.log.error("batchUpdateSealVehicle WSS服务-->批量验证并保存解封车信息 调用服务异常：{}",JsonHelper.toJson(sealVehicleList), e);
			baseEntity.setCode(BaseEntity.CODE_SERVICE_ERROR);
			baseEntity.setMessage(BaseEntity.MESSAGE_SERVICE_ERROR);
		}
		return baseEntity;
	}

	/**
	 * 验证封箱对象参数
	 * 
	 * @param sealBox
	 * @return
	 */
	private boolean checkSealBox(SealBox sealBox) {
		if (sealBox == null) {
			this.log.warn("checkSealBox-->sealBox is null");
			return false;
		}
		if (StringUtils.isBlank(sealBox.getCode())
				|| StringUtils.isBlank(sealBox.getBoxCode())
				|| sealBox.getCreateSiteCode() == null
				|| sealBox.getCreateUserCode() == null
				|| StringUtils.isBlank(sealBox.getCreateUser())) {
			this.log.warn("checkSealBox-->param error，参数： {}", ObjectMapHelper.makeObject2Map(sealBox));
			return false;
		}
		// 补全操作时间
		if (sealBox.getCreateTime() == null) {
			sealBox.setCreateTime(new Date());
		}
		return true;
	}

	/**
	 * 验证封车对象参数
	 * 
	 * @param sealVehicle
	 * @return
	 */
	private boolean checkSealVehicle(SealVehicle sealVehicle,
			boolean isSealVehicle) {
		if (sealVehicle == null) {
			this.log.warn("checkSealVehicle-->sealVehicle is null");
			return false;
		}
		if (StringUtils.isBlank(sealVehicle.getCode())
				|| StringUtils.isBlank(sealVehicle.getVehicleCode())) {
			this.log.warn("checkSealVehicle-->param error，参数：{}", ObjectMapHelper.makeObject2Map(sealVehicle));
			return false;
		}
		if (isSealVehicle) {
			// 验证封车参数
			if (sealVehicle.getCreateSiteCode() == null
					|| sealVehicle.getCreateUserCode() == null
					|| StringUtils.isBlank(sealVehicle.getCreateUser())) {
				this.log.warn("checkSealVehicle-->param error create，参数：{}", ObjectMapHelper.makeObject2Map(sealVehicle));
				return false;
			}
			// 补全操作时间
			if (sealVehicle.getCreateTime() == null) {
				sealVehicle.setCreateTime(new Date());
			}
		} else {
			// 验证解封车参数
			if (sealVehicle.getReceiveSiteCode() == null
					|| sealVehicle.getUpdateUserCode() == null
					|| StringUtils.isBlank(sealVehicle.getUpdateUser())) {
				this.log.warn("checkSealVehicle-->param error receive，参数：{}",ObjectMapHelper.makeObject2Map(sealVehicle));
				return false;
			}
			// 补全操作时间
			if (sealVehicle.getUpdateTime() == null) {
				sealVehicle.setUpdateTime(new Date());
			}
		}
		return true;
	}

	private SealBox toSealBox(SealBoxDto sealBoxDto) {
		if (sealBoxDto == null) {
			return null;
		}
		SealBox sealBox = new SealBox();
		sealBox.setCode(sealBoxDto.getCode());
		sealBox.setBoxCode(sealBoxDto.getBoxCode());
		sealBox.setCreateSiteCode(sealBoxDto.getCreateSiteCode());
		sealBox.setCreateUserCode(sealBoxDto.getCreateUserCode());
		sealBox.setCreateUser(sealBoxDto.getCreateUser());
		sealBox.setCreateTime(sealBoxDto.getCreateTime());

		sealBox.setReceiveSiteCode(sealBoxDto.getReceiveSiteCode());
		sealBox.setUpdateUserCode(sealBoxDto.getUpdateUserCode());
		sealBox.setUpdateUser(sealBoxDto.getUpdateUser());
		sealBox.setUpdateTime(sealBoxDto.getUpdateTime());
		return sealBox;
	}

	private SealVehicle toSealVehicle(SealVehicleDto sealVehicleDto) {
		if (sealVehicleDto == null) {
			return null;
		}
		SealVehicle sealVehicle = new SealVehicle();
		sealVehicle.setCode(sealVehicleDto.getCode());
		sealVehicle.setVehicleCode(sealVehicleDto.getVehicleCode());
		sealVehicle.setDriverCode(sealVehicleDto.getDriverCode());
		sealVehicle.setDriver(sealVehicleDto.getDriver());
		sealVehicle.setCreateSiteCode(sealVehicleDto.getCreateSiteCode());
		sealVehicle.setCreateUserCode(sealVehicleDto.getCreateUserCode());
		sealVehicle.setCreateUser(sealVehicleDto.getCreateUser());
		sealVehicle.setCreateTime(sealVehicleDto.getCreateTime());

		sealVehicle.setReceiveSiteCode(sealVehicleDto.getReceiveSiteCode());
		sealVehicle.setUpdateUserCode(sealVehicleDto.getUpdateUserCode());
		sealVehicle.setUpdateUser(sealVehicleDto.getUpdateUser());
		sealVehicle.setUpdateTime(sealVehicleDto.getUpdateTime());
		return sealVehicle;
	}
}