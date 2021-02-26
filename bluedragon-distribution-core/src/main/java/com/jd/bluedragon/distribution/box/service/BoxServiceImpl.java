package com.jd.bluedragon.distribution.box.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.distribution.api.request.box.BoxReq;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.box.dao.BoxDao;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxStatusEnum;
import com.jd.bluedragon.distribution.box.domain.BoxSystemTypeEnum;
import com.jd.bluedragon.distribution.external.constants.OpBoxNodeEnum;
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
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("boxService")
public class BoxServiceImpl implements BoxService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String separator = "$";

    private static final String siteType = "1024";

    private static final int timeout = 86400;

    private static final String BOX_STATUS_REDIS_QUERY_SWITCH = PropertiesHelper.newInstance().getValue("box.status.redis.query.switch");

    private static final String prefixOfCache = "DMS_BOX_SERVICE_";

	public static final String prefixOfLock = "DMS_BOX_SERVICE_LOCK_";

	public static final Integer LOCK_TTL = 2;

    @Autowired
    private BoxDao boxDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private IGenerateObjectId genObjectId;

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
	@Qualifier(value = "changeBoxStatusLogProducer")
	private DefaultJMQProducer changeBoxStatusLogProducer;

	@Autowired
	private UccPropertyConfiguration uccPropertyConfiguration;

    public Integer add(Box box) {
        Assert.notNull(box, "box must not be null");
        //持久化
		Integer result = this.boxDao.add(BoxDao.namespace, box);
        //缓存
		Boolean isCatched = jimdbCacheService.setEx(getCacheKey(box.getCode()),JsonHelper.toJson(box), timeout);
		if (!isCatched){
			log.warn("box cache fail. the boxCode is " + box.getCode());
		}
        return result;
    }

    @JProfiler(jKey = "DMSWEB.BoxService.batchAdd",mState = {JProEnum.TP})
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
		return batchAddNewFromDMS(param, systemType);
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

    public Integer updateVolumeByCode(Box box) {
        this.delboxCodeCache(box.getCode());
        return this.boxDao.updateVolumeByCode(box);
    }

	@JProfiler(jKey = "DMSWEB.BoxService.findBoxByCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
	public Box findBoxByCode(String code) {
		Assert.notNull(code, "code must not be null");
		Box result = null;

		String boxJson = jimdbCacheService.get(getCacheKey(code));
		if (StringUtils.isNotEmpty(boxJson)){
			result = JsonHelper.fromJson(boxJson,Box.class);
			return result;
		}
		result = this.boxDao.findBoxByCode(code);
		if (null == result){
			return result;
		}
		Boolean isCatched = jimdbCacheService.setEx(getCacheKey(result.getCode()),JsonHelper.toJson(result), timeout);
		if (!isCatched){
			log.warn("box cache fail. the boxCode is " + result.getCode());
		}
		return result;
	}

    public Box findBoxByBoxCode(Box box) {
        Assert.notNull(box, "box must not be null");
        return this.boxDao.findBoxByBoxCode(box);
    }

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
			String boxJson = jimdbCacheService.get(getCacheKey(boxCode));
			Box box = null;
			if (StringUtils.isEmpty(boxJson)){
				return box;
			}
			box = JsonHelper.fromJson(boxJson, Box.class);
			return box;
		} catch (Exception e) {
			this.log.error("findBoxByCode获取缓存箱号失败，箱号为:{}" , boxCode, e);
		}
		return null;

	}

	public Long delboxCodeCache(String boxCode) {
		Long resulte = 0L;
		try {
			resulte = jimdbCacheService.del(getCacheKey(boxCode)) ? 1L : 0L;
		} catch (Exception e) {
			this.log.error("delboxCodeCache删除缓存失败，箱号为:{}" , boxCode, e);
		}
		return resulte;
	}

	@Deprecated
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

		}
		return result;
	}

	@Deprecated
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
        CallerInfo info = Profiler.registerInfo("DMSWEB.BoxServiceImpl.checkBoxIsSent",Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
			if (uccPropertyConfiguration.getCheckBoxSendedSwitchOn()){
				result = oldCheckBoxIsSent(boxCode,operateSiteCode);
			}else{
				result = newCheckBoxIsSent(boxCode);
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
	 * 老逻辑 - 判断箱是否已发货
	 * @param boxCode
	 * @param operateSiteCode
	 * @return
	 */
	@Deprecated
	private Boolean oldCheckBoxIsSent(String boxCode, Integer operateSiteCode){
		Boolean result = Boolean.FALSE;
		Integer boxStatus = null;
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
		return result;
	}

	private Boolean newCheckBoxIsSent(String boxCode){
		Boolean result = Boolean.FALSE;
		//获取箱状态信息
		Box box = this.findBoxByCode(boxCode);
		if (null == box){
			return Boolean.FALSE;
		}
		result = com.jd.bluedragon.distribution.external.constants.BoxStatusEnum.CLOSE.getStatus().equals(box.getStatus())
				&& OpBoxNodeEnum.SEND.equals(box.getLastNodeType()) ? Boolean.TRUE : Boolean.FALSE;
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


	/**
	 * 更新箱状态
	 * @param boxReq
	 * @return
	 */
	@Override
	@JProfiler(jKey = "DMSWEB.BoxServiceImpl.updateBoxStatus", mState = {JProEnum.TP, JProEnum.FunctionError})
	public Boolean updateBoxStatus(BoxReq boxReq) {
		Boolean result = Boolean.FALSE;
		try{
			Box boxSaved = findBoxByCode(boxReq.getBoxCode());
			if (null == boxSaved){
				return Boolean.FALSE;
			}
			boxSaved.setStatus(boxReq.getBoxStatus());
			boxSaved.setLastNodeType(boxReq.getOpNodeCode());
			try{
				if (lock(boxReq.getBoxCode())){
					//更新数据库状态
					Box box = new Box();
					box.setCode(boxReq.getBoxCode());
					box.setStatus(boxReq.getBoxStatus());
					box.setUpdateTime(boxReq.getOpTime());
					box.setLastNodeType(boxReq.getOpNodeCode());
					this.boxDao.updateBoxStatus(box);
				}else{
					log.warn("updateBoxStatus获取锁失败，boxCode={}",boxReq.getBoxCode());
				}
			}finally {
				unLock(boxReq.getBoxCode());
			}
			//删除缓存
			delboxCodeCache(boxReq.getBoxCode());
			//更新老的缓存
			updateBoxStatusRedis(boxReq.getBoxCode(),boxReq.getOpSiteCode(), com.jd.bluedragon.distribution.external.constants.BoxStatusEnum.CLOSE.getStatus().equals(boxReq.getBoxStatus())?
					BoxStatusEnum.SENT_STATUS.getCode():BoxStatusEnum.INIT_STATUS.getCode(),boxReq.getOpErp());
			//记录流水
			changeBoxStatusLogProducer.sendOnFailPersistent(boxReq.getBoxCode(),JsonHelper.toJson(boxReq));
			result = Boolean.TRUE;
		}catch (Exception ex){
			log.error("updateBoxStatus has error. The error is " +ex.getMessage(),ex);
		}
		return result;
	}


	/**
	 * 获取锁
	 * @param boxCode
	 * @return
	 */
	private boolean lock(String boxCode) {
		String lockKey = getLockCashKey(boxCode);
		log.info("开始获取锁lockKey={}", lockKey);
		try {
			for(int i =0;i < 4;i++){
				if (jimdbCacheService.setNx(lockKey, StringUtils.EMPTY, LOCK_TTL, TimeUnit.SECONDS)){
					return Boolean.TRUE;
				}else{
					Thread.sleep(100);
				}
			}
		} catch (Exception e) {
			log.error("箱状态修改Lock异常:boxCode={},e=", boxCode , e);
			jimdbCacheService.del(lockKey);
		}
		return Boolean.FALSE;
	}

	private void unLock(String boxCode) {
		try {
			String lockKey = getLockCashKey(boxCode);
			jimdbCacheService.del(lockKey);
		} catch (Exception e) {
			log.error("箱状态修改unLock异常:boxCode={},e=", boxCode, e);
		}
	}

	private String getLockCashKey(String boxCode){
		return  prefixOfLock + boxCode;
	}

	/**
	 * 构造缓存的key
	 * @param code
	 * @return
	 */
	private String getCacheKey(String code){
		return prefixOfCache + code;
	}
}
