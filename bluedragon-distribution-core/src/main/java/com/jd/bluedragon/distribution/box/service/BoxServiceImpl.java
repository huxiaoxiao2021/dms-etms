package com.jd.bluedragon.distribution.box.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.box.dao.BoxDao;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxStatusEnum;
import com.jd.bluedragon.distribution.box.domain.BoxSystemTypeEnum;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.utils.*;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

@Service("boxService")
public class BoxServiceImpl implements BoxService {

    private final Log logger = LogFactory.getLog(this.getClass());

    private static final String separator = "$";

    private static final String siteType = "1024";

    private static final int timeout = 86400;

	private static final String BOX_STATUS_REDIS_QUERY_SWITCH = PropertiesHelper.newInstance().getValue("box.status.redis.query.switch");

    @Autowired
    private BoxDao boxDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private IGenerateObjectId genObjectId;

	@Autowired
	RedisManager redisManager;

	@Autowired
	BaseMinorManager baseMinorManager;

	@Autowired
	private SysConfigService sysConfigService;

	@Autowired
	@Qualifier("jimdbCacheService")
	private CacheService jimdbCacheService;

	@Autowired
	@Qualifier("redisBoxSequenceGen")
	private JimdbSequenceGen redisBoxSequenceGen;

	@Autowired
	private SendMDao sendMDao;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer add(Box box) {
        Assert.notNull(box, "box must not be null");
        return this.boxDao.add(BoxDao.namespace, box);
    }

    @JProfiler(jKey = "DMSWEB.BoxService.batchAdd",mState = {JProEnum.TP})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<Box> batchAdd(Box param) {
    	List<Box> boxes = Lists.newArrayList();
        String boxCodePrefix = this.generateBoxCodePrefix(param);

        for (Integer loop = 0; loop < param.getQuantity(); loop++) {
            String boxCodeSuffix = StringHelper.padZero(this.genObjectId.getObjectId(this
                    .generateKey(param)));
            Box box = new Box();
            BeanHelper.copyProperties(box, param);
			if (param.getPredictSendTime()!=null){
				box.setPredictSendTime(param.getPredictSendTime());
			}
            box.setCode(boxCodePrefix + boxCodeSuffix);
            boxes.add(box);

            this.add(box);
            try {
				//写入箱号之后添加缓存
            	//key箱号
            	box.setStatus(1);
				redisManager.setex(box.getCode(), timeout,
						JsonHelper.toJson(box));
			} catch (Exception e) {
				this.logger.error("打印箱号写入缓存失败",e);
			}
        }

        return boxes;
    }

	/**
	 * 位数	描述及取值
		 1-2	2位前缀（2位大写字母）
		 3-4	2位箱号标识，固定10
		 5-6	2位生产标识 01-打印客户端生成箱号 02-自动分拣机箱号
		 7-12	6位日期（yyMMdd）
		 13	    1位序列号生产模式 1-redis 2-db
		 14	    1位随机数（0-9）随机数
	     15-22	8位序列号 seqNum
		 23-24	2位校验位 checkNum = seqNum % 31
		 示例：
		 BC1001181227231000000121
	 * @param param
	 * @param systemType
	 * @return
	 */
	@JProfiler(jKey = "DMSWEB.BoxService.batchAddNew",mState = {JProEnum.TP})
	public List<Box> batchAddNew(Box param,BoxSystemTypeEnum systemType) {
		List<Box> boxes = Lists.newArrayList();
		String boxCodePrefix = null;
		long[] seqNos = new long[0];
		boolean dbOpen = isOpenDB();
		try{
			boxCodePrefix= this.generateBoxCodePrefixNew(param,systemType,dbOpen);
			seqNos = generateBoxCodeSeqNoNew(param,boxCodePrefix, param.getQuantity(),dbOpen);
		}catch (Exception e){
			logger.error("箱号生成序列号异常",e);
			if(!dbOpen){
				//redis 异常
				boxCodePrefix= this.generateBoxCodePrefixNew(param,systemType,true);
				seqNos = generateBoxCodeSeqNoNew(param,boxCodePrefix, param.getQuantity(),true);
			}
		}
		for(long seqNo :seqNos){
			if(seqNo >= 100000000){
				throw new RuntimeException("箱号序列号超限"+boxCodePrefix);
			}
			Box box = new Box();
			BeanHelper.copyProperties(box, param);
			box.setCode(boxCodePrefix +RandomUtils.generateString(1)+ StringHelper.padZero(seqNo) + StringHelper.padZero((seqNo % 31),2));
			boxes.add(box);
			this.add(box);

			try {
				//写入箱号之后添加缓存
				//key箱号
				box.setStatus(1);
				redisManager.setex(box.getCode(), timeout,
						JsonHelper.toJson(box));
			} catch (Exception e) {
				this.logger.error("打印箱号写入缓存失败",e);
			}
		}

		return boxes;
	}

	/**
	 * 新箱号开头规则
	 *
	 * 	 1-2	2位前缀（2位大写字母）
		 3-4	2位箱号标识，固定10
		 5-6	2位生产标识 01-打印客户端生成箱号 02-自动分拣机箱号
		 7-12	6位日期（yyMMdd）
		 13-14	2位随机数（0-99）随机数

	 * @param box
	 * @return
	 */

	private String generateBoxCodePrefixNew(Box box, BoxSystemTypeEnum systemType,boolean isDB) {
		StringBuilder preFix = new StringBuilder();
		return preFix.append(box.getType())
				.append("10").append(systemType.getCode())
				.append(DateHelper.formatDate(new Date(),"yyMMdd"))
				.append(isDB?"2":"1").toString();
	}

	/**
	 * 生成序列号
	 * @param key
	 * @param count
	 * @return
	 */
	private long[] generateBoxCodeSeqNoNew(Box param,String key,int count,boolean isDB) {
		if(isDB){
			//数据库模式
			long[] seqNo = new long[param.getQuantity()];
			for (Integer loop = 0; loop < param.getQuantity(); loop++) {
				seqNo[loop] = this.genObjectId.getObjectId(key);
			}
			return seqNo;
		}else{
			//redis模式
			return redisBoxSequenceGen.batchedGen(key,count);
		}
	}


	private String generateBoxCodePrefix(Box box) {

		String createSiteDms = null;
		String receiveSiteDms = null;

		// 对于始发站点和商家相同的ID特殊处理
		if (box.getStatuses() != null && siteType.equals(box.getStatuses())) {
			this.logger.info("商家站点打印箱号始发站");
			BasicTraderInfoDTO dto = baseMinorManager.getBaseTraderById(box
					.getCreateSiteCode());
			if (dto == null || dto.getTraderCode() == null) {
				this.logger.error("创建站点或接收站点信息为空.-始发站商家接口"
						+ box.getCreateSiteCode());
				BaseStaffSiteOrgDto createSite = this.baseMajorManager
						.getBaseSiteBySiteId(box.getCreateSiteCode());
				if (createSite == null || createSite.getDmsSiteCode() == null) {
					this.logger.error("创建站点或接收站点信息为空.-始发站站点接口"
							+ box.getCreateSiteCode());
					return null;
				} else
					createSiteDms = createSite.getDmsSiteCode();
			} else {

				createSiteDms = dto.getTraderCode();
			}
		} else {
			BaseStaffSiteOrgDto createSite = this.baseMajorManager
					.getBaseSiteBySiteId(box.getCreateSiteCode());
			if (createSite == null || createSite.getDmsSiteCode() == null) {
				this.logger.error("创建站点或接收站点信息为空.-始发站站点接口"
						+ box.getCreateSiteCode());
				return null;
			} else
				createSiteDms = createSite.getDmsSiteCode();
		}

		// 对于目的站点和商家相同的ID特殊处理
		if (box.getUpdateUser() != null && siteType.equals(box.getUpdateUser())) {
			this.logger.info("商家站点打印箱号目的站");
			BasicTraderInfoDTO dto = baseMinorManager.getBaseTraderById(box
					.getReceiveSiteCode());
			if (dto == null || dto.getTraderCode() == null) {
				this.logger.error("创建站点或接收站点信息为空.-目的站商家接口"
						+ box.getReceiveSiteCode());
				BaseStaffSiteOrgDto receiveSite = this.baseMajorManager
						.getBaseSiteBySiteId(box.getReceiveSiteCode());
				if (receiveSite == null || receiveSite.getDmsSiteCode() == null) {
					this.logger.error("创建站点或接收站点信息为空.-始发站站点接口"
							+ box.getReceiveSiteCode());
					return null;
				} else
					receiveSiteDms = receiveSite.getDmsSiteCode();
			} else {
				receiveSiteDms = dto.getTraderCode();
			}
		} else {
			BaseStaffSiteOrgDto receiveSite = this.baseMajorManager
					.getBaseSiteBySiteId(box.getReceiveSiteCode());
			if (receiveSite == null || receiveSite.getDmsSiteCode() == null) {
				this.logger.error("创建站点或接收站点信息为空.-始发站站点接口"
						+ box.getReceiveSiteCode());
				return null;
			} else
				receiveSiteDms = receiveSite.getDmsSiteCode();
		}
		box.setUpdateUser(null);
		box.setStatuses(null);
		if (createSiteDms == null || receiveSiteDms == null) {
			this.logger.error("创建站点或接收站点信息为空.-站点接口" + box.getReceiveSiteCode()
					+ "and" + box.getCreateSiteCode());
			return null;
		}

		return box.getType() + createSiteDms + receiveSiteDms;
	}

    private String generateKey(Box box) {
        return box.getType() + separator + box.getCreateSiteCode() + separator
                + box.getReceiveSiteCode();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer updateVolumeByCode(Box box) {
        this.delboxCodeCache(box.getCode());
        return this.boxDao.updateVolumeByCode(box);
    }
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer updateStatusByCodes(Box box) {
        return this.boxDao.updateStatusByCodes(box);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Box findBoxByCode(String code) {
		Assert.notNull(code, "code must not be null");

		try {
			// 取出缓存
			// key箱号
			String boxJson = redisManager.getCache(code);
			Box box = null;

			if (boxJson != null && !boxJson.isEmpty()) {
				box = JsonHelper.fromJson(boxJson, Box.class);
				if (box != null) {
					this.logger.info("findBoxByCode缓存命中箱号为" + code);
					//如果箱号 目的地 始发地不为空的时候
					if (box.getCode() != null && box.getCreateSiteCode() != null
							&& box.getReceiveSiteCode() != null) {
						this.logger.info("通过redis缓存获取箱号信息成功(userRedisQueryBox),箱号信息为："+JsonHelper.toJson(box));
						return box;
					}
				} else {
					this.logger.info("findBoxByCode没有缓存命中箱号为" + code);
				}
			} else {
				this.logger.info("findBoxByCode缓存命中,但是消息为null,箱号为" + code);
			}


		} catch (Exception e) {
			this.logger.error("findBoxByCode获取缓存箱号失败，箱号为" + code, e);
		}

		Box box = this.boxDao.findBoxByCode(code);
		if(box != null){
			this.logger.info("通过数据库获取箱号信息成功(userMysqlQueryBox),箱号信息为："+JsonHelper.toJson(box));
		}

		return box;
	}

    public Box findBoxByBoxCode(Box box) {
        Assert.notNull(box, "box must not be null");
        return this.boxDao.findBoxByBoxCode(box);
    }

    public List<Box> findBoxesBySite(Box box) {
        Assert.notNull(box, "box must not be null");
        return this.boxDao.findBoxesBySite(box);
    }

    public List<Box> findBoxes(Box box) {
        Assert.notNull(box, "box must not be null");
        return this.boxDao.findBoxes(box);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer reprint(Box box) {
        Assert.notNull(box.getUpdateUserCode(), "box updateUsercode must not be null");
        Assert.notNull(box.getUpdateUser(), "box updateUser must not be null");
        Assert.notNull(box.getCode(), "box code must not be null");
        return this.boxDao.reprint(box);
    }

	@Override
	public Box findBoxCacheByCode(String boxCode) {

		Assert.notNull(boxCode, "boxCode must not be null");

		try {
			// 取出缓存
			// key箱号
			String boxJson = redisManager.getCache(boxCode);
			Box box = null;

			if (boxJson != null && !boxJson.isEmpty()) {
				box = JsonHelper.fromJson(boxJson, Box.class);
				if (box != null) {
					this.logger.info("findBoxByCode缓存命中箱号为" + boxCode);
					if (box.getCode() != null && box.getCreateSiteCode() != null
							&& box.getReceiveSiteCode() != null) {
						return box;
					}
				}
			}

		} catch (Exception e) {
			this.logger.error("findBoxByCode获取缓存箱号失败，箱号为" + boxCode, e);
		}

		return null;

	}

	public Long delboxCodeCache(String boxCode) {
		Long resulte = 0L;
		try {
			resulte = redisManager.del(boxCode);
		} catch (Exception e) {
			this.logger.error("delboxCodeCache删除缓存失败，箱号为" + boxCode, e);
		}
		return resulte;
	}

    @Transactional(propagation=Propagation.REQUIRED,readOnly = false)
    @Override
    public Integer batchUpdateStatus(List<String> boxCodes, Integer boxStatus) {
        if (null == boxCodes || boxCodes.size() <= 0) {
            return 0;
        }
        try {
            return splitPerOneThousand(boxCodes, boxStatus);
        } catch (Exception ex) {
            logger.error("更新箱号状态失败", ex);
            return 0;
        }
    }

    private Integer splitPerOneThousand(List<String> boxCodes, Integer boxStatus) {
        int totalSize = boxCodes.size();
        int fromIndex = 0;
        int perSize = 1000;
        int effectSize = 0;
        for (;;) {
            if (fromIndex + perSize >= totalSize) {
                Box box = new Box();
                box.setCode("('" + StringHelper.join(boxCodes.subList(fromIndex, totalSize), "','") + "')");
                box.setStatus(boxStatus);
                effectSize += boxDao.batchUpdateStatus(box);
                break;
            } else {
                Box box = new Box();
                box.setCode("('" + StringHelper.join(boxCodes.subList(fromIndex, fromIndex + perSize), "','") + "')");
                box.setStatus(boxStatus);
                effectSize += boxDao.batchUpdateStatus(box);
                fromIndex = fromIndex + perSize;
            }
        }
        return effectSize;
    }

    @Override
	@JProfiler(jKey = "DMSWEB.BoxServiceImpl.updateBoxStatusRedis", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public Boolean updateBoxStatusRedis(String boxCode, Integer operateSiteCode, Integer boxStatus) {
		Boolean result = false;
		if (StringHelper.isNotEmpty(boxCode) && operateSiteCode != null && BusinessHelper.isBoxcode(boxCode)) {
			CallerInfo info = null;
			try {
				info = Profiler.registerInfo("DMSWEB.BoxServiceImpl.updateBoxStatusRedis.is.box",Constants.UMP_APP_NAME_DMSWEB, false, true);
				String redisKey = CacheKeyConstants.CACHE_KEY_BOX_STATUS + Constants.SEPARATOR_HYPHEN + boxCode + Constants.SEPARATOR_HYPHEN + operateSiteCode;
				//更新缓存，缓存两小时
				result = jimdbCacheService.setEx(redisKey, boxStatus, 2 * Constants.TIME_SECONDS_ONE_HOUR);
				if (result) {
					logger.info(MessageFormat.format("箱号：{0}更新状态成功，操作站点编号：{1}, 状态为：{2}", boxCode, operateSiteCode, BoxStatusEnum.getEnumMap().get(boxStatus)));
				} else {
					logger.warn(MessageFormat.format("箱号：{0}更新状态失败，操作站点编号：{1}, 状态为：{2}", boxCode, operateSiteCode, BoxStatusEnum.getEnumMap().get(boxStatus)));
				}
			} catch (Exception e) {
				logger.error(MessageFormat.format("箱号：{0}，操作站点编号：{1}，更新箱号状态缓存失败！", boxCode, operateSiteCode), e);
			}
			finally {
				Profiler.registerInfoEnd(info);
			}
		}
		return result;
	}

	@Override
	@JProfiler(jKey = "DMSWEB.BoxServiceImpl.getBoxStatusFromRedis", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public Integer getBoxStatusFromRedis(String boxCode, Integer operateSiteCode) {
		Integer result = null;
		try {
			if (StringHelper.isNotEmpty(boxCode) && operateSiteCode != null && BusinessHelper.isBoxcode(boxCode)) {
				String redisKey = CacheKeyConstants.CACHE_KEY_BOX_STATUS + Constants.SEPARATOR_HYPHEN + boxCode + Constants.SEPARATOR_HYPHEN + operateSiteCode;
				String value = jimdbCacheService.get(redisKey);
				if (StringHelper.isNotEmpty(value)) {
					CallerInfo info = Profiler.registerInfo("DMSWEB.BoxServiceImpl.getBoxStatusFromRedis.redis.exist",Constants.UMP_APP_NAME_DMSWEB, false, true);
					result = Integer.parseInt(value);
					Profiler.registerInfoEnd(info);
					logger.info(MessageFormat.format("箱号状态缓存命中，箱号：{0}，操作站点编号：{1}，状态为：{2}", boxCode, operateSiteCode, BoxStatusEnum.getEnumMap().get(result)));
				} else {
					logger.info(MessageFormat.format("箱号状态缓存未命中，箱号：{0}，操作站点编号：{1}，需查库确认！", boxCode, operateSiteCode));
				}
			}
		} catch (Exception e) {
			logger.error(MessageFormat.format("箱号：{0}，操作站点编号：{1}，获取箱号状态缓存失败！", boxCode, operateSiteCode), e);
		}
		return result;
	}

	@Override
	@JProfiler(jKey = "DMSWEB.BoxServiceImpl.checkBoxIsSent", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public Boolean checkBoxIsSent(String boxCode, Integer operateSiteCode) {
		Boolean result = false;
		Integer boxStatus = null;
		try {
			if ("1".equals(BOX_STATUS_REDIS_QUERY_SWITCH)) {
				boxStatus = this.getBoxStatusFromRedis(boxCode, operateSiteCode);
			}
			if (boxStatus != null) {
				if (BoxStatusEnum.SENT_STATUS.getCode().equals(boxStatus)) {
					logger.info(MessageFormat.format("箱号状态缓存命中，箱号：{0} 在站点编号为：{1}时已发货！", boxCode, operateSiteCode));
					result = true;
				}
			} else {
				SendM sendM = new SendM();
				sendM.setBoxCode(boxCode);
				sendM.setCreateSiteCode(operateSiteCode);
				List<SendM> sendMList = sendMDao.findSendMByBoxCode(sendM);

				//sendm不为空，说明已发货，否则视为初始状态
				if (sendMList != null && ! sendMList.isEmpty()) {
					logger.info(MessageFormat.format("查询SendM表成功，箱号：{0} 在站点编号为：{1}时已发货！", boxCode, operateSiteCode));
					//更新箱号状态缓存为已发货
					this.updateBoxStatusRedis(sendM.getBoxCode(), sendM.getCreateSiteCode(), BoxStatusEnum.SENT_STATUS.getCode());
					result = true;
				} else {
					logger.info(MessageFormat.format("查询SendM表成功，箱号：{0} 在站点编号为：{1}时未发货！", boxCode, operateSiteCode));
					//更新箱号状态缓存为初始状态
					this.updateBoxStatusRedis(sendM.getBoxCode(), sendM.getCreateSiteCode(), BoxStatusEnum.INIT_STATUS.getCode());
				}
			}
		} catch (Exception e) {
			result = null;
			logger.error(MessageFormat.format("箱号：{0}，操作站点编号：{1}，获取箱号校验箱号是否发货失败！", boxCode, operateSiteCode), e);
		}

		return result;
	}

	/**
	 * box.seq.db.switch
	 * 等于1 开启DB模式
	 * 不维护 或者 等于0 采用redis模式
	 * @return
	 */
	public boolean isOpenDB(){
		try {
			List<SysConfig> sysConfigs = sysConfigService.getListByConfigName("box.seq.db.switch");
			if (null == sysConfigs || sysConfigs.size() <= 0) {
				return false;
			} else {
				if(sysConfigs.get(0).getConfigContent()==null){
					return false;
				}
				return sysConfigs.get(0).getConfigContent().equals("1");
			}
		} catch (Throwable ex) {
			return false;
		}
	}

	public static void main(String[] args) {
        Box box = new Box();
        box.setCode("BC010F002010Y04200062058");
        box.setType("BC");
        box.setCreateSiteCode(910);
        box.setCreateSiteName("北京马驹桥分拣中心");
        box.setCreateUser("dudong");
        box.setCreateUserCode(11535);
        box.setReceiveSiteCode(21);
        box.setReceiveSiteName("潘家园站");
        box.setStatus(5);
        box.setLength(11f);
        box.setWidth(12f);
        box.setHeight(13f);
        System.out.println(com.jd.bluedragon.utils.JsonHelper.toJson(box));
    }
}
