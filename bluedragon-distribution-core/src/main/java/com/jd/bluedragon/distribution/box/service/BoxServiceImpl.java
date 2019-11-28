package com.jd.bluedragon.distribution.box.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.ContainerManager;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.box.dao.BoxDao;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxStatusEnum;
import com.jd.bluedragon.distribution.box.domain.BoxSystemTypeEnum;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.RandomUtils;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service("boxService")
public class BoxServiceImpl implements BoxService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

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

    @Autowired
    private SiteService siteService;

	@Autowired
	private ContainerManager containerManager;

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
				this.log.error("打印箱号写入缓存失败：{}",box.getCode(),e);
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
	@JProfiler(jKey = "DMSWEB.BoxService.batchAddNew",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
	public List<Box> batchAddNew(Box param,BoxSystemTypeEnum systemType) {
		List<Box> boxes = null;

		if(isCreateBoxFromSSC()){
            CallerInfo callerInfoFromSSC = Profiler.registerInfo("DMSWEB.BoxService.batchAddNewFromSSC", Constants.UMP_APP_NAME_DMSWEB,false,true);
            try{
                Set<Integer> siteSet = siteService.getBoxFromSSCAllowedList();
                //未配置站点列表时默认全部开启，否则取配置的列表
                if(siteSet.isEmpty() || siteSet.contains(param.getCreateSiteCode())){
                    //通过中台生产箱号
                    boxes = batchAddNewFromSSC(param, systemType);
                }
            }catch (Exception e){
                Profiler.functionError(callerInfoFromSSC);
                log.error("通过中台创建箱号失败{}" , JsonHelper.toJson(param), e);
            }finally {
                Profiler.registerInfoEnd(callerInfoFromSSC);
            }
        }
		//原有分拣逻辑生产箱号作为兜底方案，当箱号集合为空时，有该逻辑重新生成
        if(boxes == null || boxes.isEmpty()){
            //通过原有分拣逻辑生产箱号
            CallerInfo callerInfoFromDMS = Profiler.registerInfo("DMSWEB.BoxService.batchAddNewFromDMS", Constants.UMP_APP_NAME_DMSWEB,false,true);
            boxes = batchAddNewFromDMS(param, systemType);
            Profiler.registerInfoEnd(callerInfoFromDMS);
        }
		return boxes;
	}

    /**
     * 是否启用中台创建箱号开关
     * @return boolean
     */
	private boolean isCreateBoxFromSSC(){
        boolean isCreateBoxFromSSC = false;
        try{
            SysConfig config = sysConfigService.findConfigContentByConfigName(Constants.CREATE_BOX_FROM_SSC_SWITCH);
            if(config != null && Constants.STRING_FLG_TRUE.equals(config.getConfigContent())){
                isCreateBoxFromSSC = true;
            }
        }catch (Exception e){
            log.error("查询箱号创建是否使用中台异常", e);
        }

        return isCreateBoxFromSSC;
    }

    /**
     * 是否启用中台查询箱号开关
     * @return boolean
     */
	private boolean isFindBoxFromSSC(){
        boolean isFindBoxFromSSC = false;
        try{
            SysConfig config = sysConfigService.findConfigContentByConfigName(Constants.FIND_BOX_FROM_SSC_SWITCH);
            if(config != null && Constants.STRING_FLG_TRUE.equals(config.getConfigContent())){
				isFindBoxFromSSC = true;
            }
        }catch (Exception e){
            log.error("查询箱号查询是否使用中台异常", e);
        }

        return isFindBoxFromSSC;
    }

    /**
     * 使用中台生产箱号
     * @param param 参数
     * @param systemType 类型
     * @return 箱号集合
     */
	private List<Box> batchAddNewFromDMS(Box param,BoxSystemTypeEnum systemType) {
        List<Box> boxes = Lists.newArrayList();
		String boxCodePrefix = null;
		long[] seqNos = new long[0];
		boolean dbOpen = isOpenDB();
		try{
			boxCodePrefix= this.generateBoxCodePrefixNew(param,systemType,dbOpen);
			seqNos = generateBoxCodeSeqNoNew(param,boxCodePrefix, param.getQuantity(),dbOpen);
		}catch (Exception e){
			log.error("箱号生成序列号异常",e);
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
				this.log.error("打印箱号写入缓存失败",e);
			}
		}

		return boxes;
	}

    /**
     * 使用中台生产箱号
     * @param param 参数
     * @param systemType 类型
     * @return 箱号集合
     */
    private List<Box> batchAddNewFromSSC(Box param,BoxSystemTypeEnum systemType) {
        List<Box> boxes = Lists.newArrayList();
        try{
            boxes = containerManager.createContainers(param, systemType);
            for(Box box : boxes){
                this.add(box);
                try {
                    //写入箱号之后添加缓存,key为箱号
                    redisManager.setex(box.getCode(), timeout, JsonHelper.toJson(box));
                } catch (Exception e) {
                    this.log.error("打印箱号写入缓存失败",e);
                }
            }
        }catch (Exception e){
            log.error("通过中台生产箱号失败：{}" , JsonHelper.toJson(param), e);
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
			this.log.info("商家站点打印箱号始发站");
			BasicTraderInfoDTO dto = baseMinorManager.getBaseTraderById(box
					.getCreateSiteCode());
			if (dto == null || dto.getTraderCode() == null) {
				this.log.warn("创建站点或接收站点信息为空.-始发站商家接口:{}", box.getCreateSiteCode());
				BaseStaffSiteOrgDto createSite = this.baseMajorManager
						.getBaseSiteBySiteId(box.getCreateSiteCode());
				if (createSite == null || createSite.getDmsSiteCode() == null) {
					this.log.warn("创建站点或接收站点信息为空.-始发站站点接口:{}", box.getCreateSiteCode());
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
				this.log.warn("创建站点或接收站点信息为空.-始发站站点接口:{}",box.getCreateSiteCode());
				return null;
			} else
				createSiteDms = createSite.getDmsSiteCode();
		}

		// 对于目的站点和商家相同的ID特殊处理
		if (box.getUpdateUser() != null && siteType.equals(box.getUpdateUser())) {
			this.log.info("商家站点打印箱号目的站");
			BasicTraderInfoDTO dto = baseMinorManager.getBaseTraderById(box
					.getReceiveSiteCode());
			if (dto == null || dto.getTraderCode() == null) {
				this.log.warn("创建站点或接收站点信息为空.-目的站商家接口:{}", box.getReceiveSiteCode());
				BaseStaffSiteOrgDto receiveSite = this.baseMajorManager
						.getBaseSiteBySiteId(box.getReceiveSiteCode());
				if (receiveSite == null || receiveSite.getDmsSiteCode() == null) {
					this.log.warn("创建站点或接收站点信息为空.-始发站站点接口:{}", box.getReceiveSiteCode());
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
				this.log.warn("创建站点或接收站点信息为空.-始发站站点接口:{}", box.getReceiveSiteCode());
				return null;
			} else
				receiveSiteDms = receiveSite.getDmsSiteCode();
		}
		box.setUpdateUser(null);
		box.setStatuses(null);
		if (createSiteDms == null || receiveSiteDms == null) {
			this.log.warn("创建站点或接收站点信息为空.-站点接口{} and {}", box.getReceiveSiteCode(), box.getCreateSiteCode());
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

	@JProfiler(jKey = "DMSWEB.BoxService.findBoxByCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
	public Box findBoxByCode(String code) {
		Assert.notNull(code, "code must not be null");

		Box box = null;

		//通过中台查询箱号
        if(isFindBoxFromSSC()){
            try{
                box = containerManager.findBoxByCode(code);
            }catch (Exception e){
                log.error("通过中台查询箱号失败：{}" , code, e);
            }
        }

        //当箱号为空时，从本系统再查一遍
        if(box == null || StringUtils.isEmpty(box.getCode())){
            box = findBoxByCodeFromDMS(code);
        }

		return box;
	}

	private Box findBoxByCodeFromDMS(String code) {
		Assert.notNull(code, "code must not be null");

		try {
			// 取出缓存
			// key箱号
			String boxJson = redisManager.getCache(code);
			Box box = null;

			if (boxJson != null && !boxJson.isEmpty()) {
				box = JsonHelper.fromJson(boxJson, Box.class);
				if (box != null) {
					this.log.info("findBoxByCode缓存命中箱号为:{}" , code);
					//如果箱号 目的地 始发地不为空的时候
					if (box.getCode() != null && box.getCreateSiteCode() != null
							&& box.getReceiveSiteCode() != null) {
						this.log.info("通过redis缓存获取箱号信息成功(userRedisQueryBox),箱号信息为：{}",box.getCode());
						return box;
					}
				} else {
					this.log.info("findBoxByCode没有缓存命中箱号为:{}" , code);
				}
			} else {
				this.log.info("findBoxByCode缓存命中,但是消息为null,箱号为:{}" , code);
			}


		} catch (Exception e) {
			this.log.error("findBoxByCode获取缓存箱号失败，箱号为:{}" , code, e);
		}

		Box box = this.boxDao.findBoxByCode(code);
		if(box != null){
			this.log.info("通过数据库获取箱号信息成功(userMysqlQueryBox),箱号信息为：{}",JsonHelper.toJson(box));
		}

		return box;
	}

    public Box findBoxByBoxCode(Box box) {
        Assert.notNull(box, "box must not be null");
        return this.boxDao.findBoxByBoxCode(box);
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
					this.log.info("findBoxByCode缓存命中箱号为:{}" , boxCode);
					if (box.getCode() != null && box.getCreateSiteCode() != null
							&& box.getReceiveSiteCode() != null) {
						return box;
					}
				}
			}

		} catch (Exception e) {
			this.log.error("findBoxByCode获取缓存箱号失败，箱号为:{}" , boxCode, e);
		}

		return null;

	}

	public Long delboxCodeCache(String boxCode) {
		Long resulte = 0L;
		try {
			resulte = redisManager.del(boxCode);
		} catch (Exception e) {
			this.log.error("delboxCodeCache删除缓存失败，箱号为:{}" , boxCode, e);
		}
		return resulte;
	}

    @Override
	@JProfiler(jKey = "DMSWEB.BoxServiceImpl.updateBoxStatusRedis", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public Boolean updateBoxStatusRedis(String boxCode, Integer operateSiteCode, Integer boxStatus, String userErp) {
		Boolean result = false;
		if (StringHelper.isNotEmpty(boxCode) && operateSiteCode != null && BusinessHelper.isBoxcode(boxCode)) {
			CallerInfo info = null;
			try {
				info = Profiler.registerInfo("DMSWEB.BoxServiceImpl.updateBoxStatusRedis.is.box",Constants.UMP_APP_NAME_DMSWEB, false, true);
				String redisKey = CacheKeyConstants.CACHE_KEY_BOX_STATUS + Constants.SEPARATOR_HYPHEN + boxCode + Constants.SEPARATOR_HYPHEN + operateSiteCode;
				//更新缓存，缓存两小时
				result = jimdbCacheService.setEx(redisKey, boxStatus, 2 * Constants.TIME_SECONDS_ONE_HOUR);
				if (result) {
					log.info("箱号：{}更新状态成功，操作站点编号：{}, 状态为：{}", boxCode, operateSiteCode, BoxStatusEnum.getEnumMap().get(boxStatus));
				} else {
					log.warn("箱号：{}更新状态失败，操作站点编号：{}, 状态为：{}", boxCode, operateSiteCode, BoxStatusEnum.getEnumMap().get(boxStatus));
				}
			} catch (Exception e) {
				Profiler.functionError(info);
				log.error("箱号：{}，操作站点编号：{}，更新箱号状态缓存失败！", boxCode, operateSiteCode, e);
			}
			finally {
				Profiler.registerInfoEnd(info);
			}

			if(boxStatus == BoxStatusEnum.SENT_STATUS.getCode() || boxStatus == BoxStatusEnum.CANCELED_STATUS.getCode()){
                updateBoxStatusSSC(boxCode, operateSiteCode, boxStatus, userErp);
            }
		}
		return result;
	}

    /**
     * 更新容器状态
     * @param boxCode 容器号
     * @param operateSiteCode 操作场地ID
     * @param boxStatus 状态
     * @param userErp 更新人erp
     */
	private void updateBoxStatusSSC(String boxCode, Integer operateSiteCode, Integer boxStatus, String userErp){
	    try{
            if(isCreateBoxFromSSC()) {
                Set<Integer> siteSet = siteService.getBoxFromSSCAllowedList();
                //未配置站点列表时默认全部开启，否则取配置的列表
                if (siteSet.isEmpty() || siteSet.contains(operateSiteCode)) {
                    if(boxStatus == BoxStatusEnum.SENT_STATUS.getCode()){
                        containerManager.updateBoxSend(boxCode,userErp,userErp,operateSiteCode);
                    }else if(boxStatus == BoxStatusEnum.CANCELED_STATUS.getCode()){
						containerManager.updateBoxCancelSend(boxCode, userErp, userErp, operateSiteCode);
                    }
                }
            }
        }catch (Exception e){
            String msg = "";
	        if(boxStatus == BoxStatusEnum.SENT_STATUS.getCode()){
	            msg = "发货";
            }else if(boxStatus == BoxStatusEnum.CANCELED_STATUS.getCode()){
                msg = "取消发货";
            }
            log.error("更新中台容器状态为{}异常：{}" ,msg, boxCode, e);
        }

    }

	@Override
	public Integer getBoxStatusFromRedis(String boxCode, Integer operateSiteCode) {
		Integer result = null;
        CallerInfo info = Profiler.registerInfo("DMSWEB.BoxServiceImpl.getBoxStatusFromRedis",Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
            if (StringHelper.isNotEmpty(boxCode) && operateSiteCode != null && BusinessHelper.isBoxcode(boxCode)) {
                String redisKey = CacheKeyConstants.CACHE_KEY_BOX_STATUS + Constants.SEPARATOR_HYPHEN + boxCode + Constants.SEPARATOR_HYPHEN + operateSiteCode;
                String value = jimdbCacheService.get(redisKey);
                if (StringHelper.isNotEmpty(value)) {
					result = Integer.parseInt(value);
					log.info("箱号状态缓存命中，箱号：{}，操作站点编号：{}，状态为：{}", boxCode, operateSiteCode, BoxStatusEnum.getEnumMap().get(result));
				} else {
					log.info("箱号状态缓存未命中，箱号：{}，操作站点编号：{}，需查库确认！", boxCode, operateSiteCode);
				}
			}
		} catch (Exception e) {
            Profiler.functionError(info);
			log.error("箱号：{}，操作站点编号：{}，获取箱号状态缓存失败！", boxCode, operateSiteCode, e);
		}finally {
            Profiler.registerInfoEnd(info);
        }
		return result;
	}

	@Override
	public Boolean checkBoxIsSent(String boxCode, Integer operateSiteCode) {
		Boolean result = false;
		Integer boxStatus = null;
        CallerInfo info = Profiler.registerInfo("DMSWEB.BoxServiceImpl.checkBoxIsSent",Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
			if ("1".equals(BOX_STATUS_REDIS_QUERY_SWITCH)) {
				boxStatus = this.getBoxStatusFromRedis(boxCode, operateSiteCode);
			}
			if (boxStatus != null) {
				if (BoxStatusEnum.SENT_STATUS.getCode().equals(boxStatus)) {
					log.info("箱号状态缓存命中，箱号：{} 在站点编号为：{}时已发货！", boxCode, operateSiteCode);
					result = true;
				}
			} else {
				SendM sendM = new SendM();
				sendM.setBoxCode(boxCode);
				sendM.setCreateSiteCode(operateSiteCode);
				List<SendM> sendMList = sendMDao.findSendMByBoxCode(sendM);

				//sendm不为空，说明已发货，否则视为初始状态
				if (sendMList != null && ! sendMList.isEmpty()) {
					log.info("查询SendM表成功，箱号：{} 在站点编号为：{}时已发货！", boxCode, operateSiteCode);
					//更新箱号状态缓存为已发货
					this.updateBoxStatusRedis(sendM.getBoxCode(), sendM.getCreateSiteCode(), BoxStatusEnum.SENT_STATUS.getCode(), sendMList.get(0).getCreateUser());
					result = true;
				} else {
					log.info("查询SendM表成功，箱号：{} 在站点编号为：{}时未发货！", boxCode, operateSiteCode);
					//更新箱号状态缓存为初始状态
					this.updateBoxStatusRedis(sendM.getBoxCode(), sendM.getCreateSiteCode(), BoxStatusEnum.INIT_STATUS.getCode(), null);
				}
			}
		} catch (Exception e) {
            Profiler.functionError(info);
			result = null;
			log.error("箱号：{}，操作站点编号：{}，获取箱号校验箱号是否发货失败！", boxCode, operateSiteCode, e);
		}finally {
            Profiler.registerInfoEnd(info);
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
