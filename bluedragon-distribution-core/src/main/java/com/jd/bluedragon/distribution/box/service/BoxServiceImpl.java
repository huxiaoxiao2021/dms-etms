package com.jd.bluedragon.distribution.box.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.hint.constants.HintArgsConstants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.request.base.OperateUser;
import com.jd.bluedragon.distribution.api.request.base.RequestProfile;
import com.jd.bluedragon.distribution.api.request.box.BoxReq;
import com.jd.bluedragon.distribution.api.request.box.BoxTypeReq;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.api.response.box.BoxTypeDto;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.box.constants.BoxMaterialBindFlagEnum;
import com.jd.bluedragon.distribution.box.constants.BoxSubTypeEnum;
import com.jd.bluedragon.distribution.box.constants.BoxTypeEnum;
import com.jd.bluedragon.distribution.box.constants.BoxTypeV2Enum;
import com.jd.bluedragon.distribution.box.dao.BoxDao;
import com.jd.bluedragon.distribution.box.domain.*;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBoxResult;
import com.jd.bluedragon.distribution.crossbox.service.CrossBoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;
import com.jd.bluedragon.distribution.cyclebox.service.BoxMaterialRelationService;
import com.jd.bluedragon.distribution.external.constants.OpBoxNodeEnum;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.dms.utils.RecycleBasketTypeEnum;
import com.jd.bluedragon.utils.*;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.basic.domain.BaseResult;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.domain.PsStoreInfo;
import com.jd.ql.basic.domain.SortCrossDetail;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
	private static final String[] REPLACE_CHARS = { "分拣中心", "分拨中心", "中转场", "中转站" };

	private static final String DB_TABLE_NAME = "box";

	@Autowired
	private SequenceGenAdaptor sequenceGenAdaptor;
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
	DmsConfigManager dmsConfigManager;
    @Autowired
    private CrossBoxService crossBoxService;

    @Resource(name = "stableBoxPrefixSystem")
    private Set<String> stableBoxPrefixSystem;

    @Resource(name = "stableBoxPrefixType")
    private Set<String> stableBoxPrefixType;

    @Autowired
	private SiteService siteService;

	@Autowired
	private BasicPrimaryWS basicPrimaryWS;
	@Value("${box.addBatch.size:20}")
	private Integer boxAddBatchSize;

	@Autowired
	@Qualifier(value = "boxFirstPrintProducer")
	private DefaultJMQProducer boxFirstPrintProducer;

    @Resource(name="sortingBoxSubTypeMap")
    private Map<String,String> sortingBoxSubTypeMap;
    @Resource(name="siteBoxSubTypeMap")
    private Map<String,String> siteBoxSubTypeMap;

    @Autowired
    private BoxMaterialRelationService boxMaterialRelationService;

	@Autowired
	BoxRelationService boxRelationService;

    public Integer add(Box box) {
        Assert.notNull(box, "box must not be null");
        //持久化
        if(box.getBoxSubType() == null){
            box.setBoxSubType(Constants.EMPTY_FILL);
        }
		Integer result = this.boxDao.add(BoxDao.namespace, box);
        //缓存
		Boolean isCatched = jimdbCacheService.setEx(getCacheKey(box.getCode()),JsonHelper.toJson(box), timeout);
		if (!isCatched){
			log.warn("box cache fail. the boxCode is " + box.getCode());
		}
        return result;
    }

    /**
     * @return 箱号ID
     */
    @Override
    public long newBoxId() {
        return sequenceGenAdaptor.newId(DB_TABLE_NAME);
    }

	private Integer add(List<Box> boxes) {
		if (CollectionUtils.isEmpty(boxes)) {
			return 0;
		}

		// 生成主键ID
		for (Box box : boxes) {
			box.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
			if(box.getBoxSubType() == null){
				box.setBoxSubType(Constants.EMPTY_FILL);
			}
		}

		List<List<Box>> list = Lists.partition(boxes, boxAddBatchSize);
		Integer result = 0;
		for (List<Box> boxList : list) {
			//持久化
			Integer succCount = this.boxDao.addBatch(boxList);
			result += succCount;
		}
		//缓存
		for (Box box : boxes) {
			Boolean isCatched = jimdbCacheService.setEx(getCacheKey(box.getCode()),JsonHelper.toJson(box), timeout);
			if (!isCatched){
				log.warn("box cache fail. the boxCode is " + box.getCode());
			}
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
			if(box.getBoxSubType() == null){
				box.setBoxSubType(Constants.EMPTY_FILL);
			}
            box.setCode(boxCodePrefix + boxCodeSuffix);
            boxes.add(box);

        }
		this.add(boxes);
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
	public List<Box> batchAddNew(Box param, String systemType) {
		return batchAddNewFromDMS(param, systemType);
	}
    /**
     * 使用中台生产箱号
     * @param param 参数
     * @param systemType 类型
     * @return 箱号集合
     */
	private List<Box> batchAddNewFromDMS(Box param, String systemType) {
        List<Box> boxes = Lists.newArrayList();
		List<String> codes = generateCode(param, systemType, null);
		for(String code :codes){
			Box box = new Box();
			BeanHelper.copyProperties(box, param);
			box.setCode(code);
			box.setBoxSource(systemType);
			boxes.add(box);
		}
		this.add(boxes);
		return boxes;
	}

	private List<String> generateCode(Box param, String systemType, RecycleBasketTypeEnum typeEnum){
		String boxCodePrefix = null;
		long[] seqNos = new long[0];
		boolean dbOpen = isOpenDB();
		try{
			if (Objects.equals(BoxTypeEnum.RECYCLE_BASKET.getCode(),param.getType())) {
				boxCodePrefix= this.generateRecycleMaterialPrefixNew(systemType, typeEnum);
			}else {
				boxCodePrefix= this.generateBoxCodePrefixNew(param,systemType,dbOpen);
			}
			seqNos = generateBoxCodeSeqNoNew(param,boxCodePrefix, param.getQuantity(),dbOpen);
		}catch (Exception e){
			log.error("箱号生成序列号异常",e);
			if(!dbOpen){
				//redis 异常
				if (Objects.equals(BoxTypeEnum.RECYCLE_BASKET.getCode(),param.getType())) {
					boxCodePrefix= this.generateRecycleMaterialPrefixNew(systemType, typeEnum);
				}else {
					boxCodePrefix = this.generateBoxCodePrefixNew(param, systemType, true);
				}
				seqNos = generateBoxCodeSeqNoNew(param,boxCodePrefix, param.getQuantity(),true);
			}
		}
		if(seqNos.length == 0){
			throw new RuntimeException("箱号生成序列号异常" + boxCodePrefix);
		}
		List<String> codes = new ArrayList<>(seqNos.length);
		for(long seqNo :seqNos) {
			if (seqNo >= 100000000) {
				throw new RuntimeException("箱号序列号超限" + boxCodePrefix);
			}
			codes.add(boxCodePrefix +RandomUtils.generateString(1)+ StringHelper.padZero(seqNo) + StringHelper.padZero((seqNo % 31),2));
		}
		return codes;
	}

	@Override
	public List<String> generateRecycleBasketCode(int quantity, RecycleBasketTypeEnum typeEnum){
		Box param = new Box();
		param.setType(BoxTypeEnum.RECYCLE_BASKET.getCode());
		param.setQuantity(quantity);
		return generateCode(param, BoxSystemTypeEnum.PRINT_CLIENT.getCode(), typeEnum);
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

	private String generateBoxCodePrefixNew(Box box, String systemType,boolean isDB) {
		StringBuilder preFix = new StringBuilder();
		if (!this.genStableBoxPrefix(box, systemType)) {
			preFix.append(box.getType());
		}
		else {
		    // 箱号固定BC开头，不再根据类型区域不同的前缀
            preFix.append(BoxTypeEnum.TYPE_BC.getCode());
        }
		return preFix.append("10").append(systemType)
				.append(DateHelper.formatDate(new Date(),"yyMMdd"))
				.append(isDB?"2":"1").toString();
	}

	private String generateRecycleMaterialPrefixNew(String systemType, RecycleBasketTypeEnum typeEnum) {
		StringBuilder preFix = new StringBuilder();
		return preFix.append(BoxTypeEnum.RECYCLE_BASKET.getCode())
				.append("10").append(systemType)
				.append(DateHelper.formatDate(new Date(),"yyMMdd"))
				.append(typeEnum.getCode()).toString();
	}

    /**
     * 判断是否生成固定前缀的箱号
     * @param box
     * @param systemType
     * @return
     */
	private boolean genStableBoxPrefix(Box box, String systemType) {
	    if (CollectionUtils.isEmpty(stableBoxPrefixSystem) || CollectionUtils.isEmpty(stableBoxPrefixType)) {
	        return false;
        }
        return stableBoxPrefixSystem.contains(systemType) || stableBoxPrefixType.contains(box.getType());
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
		String boxJson = null;
		try{
			boxJson = jimdbCacheService.get(getCacheKey(code));
		}catch (Exception e){
			log.error("com.jd.bluedragon.distribution.box.service.BoxServiceImpl.findBoxByCode get error!,{}",code,e);
		}
		if (StringUtils.isNotEmpty(boxJson)){
			result = JsonHelper.fromJson(boxJson,Box.class);
			return result;
		}
		result = this.boxDao.findBoxByCode(code);
		if (null == result){
			return result;
		}
		try{
			Boolean isCatched = jimdbCacheService.setEx(getCacheKey(result.getCode()),JsonHelper.toJson(result), timeout);
			if (!isCatched){
				log.warn("box cache fail. the boxCode is " + result.getCode());
			}
		}catch (Exception e){
			log.error("com.jd.bluedragon.distribution.box.service.BoxServiceImpl.findBoxByCode setEx error!,{}",code,e);
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
			if (dmsConfigManager.getPropertyConfig().getCheckBoxSendedSwitchOn()){
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
				&& OpBoxNodeEnum.SEND.getNodeCode().equals(box.getLastNodeType()) ? Boolean.TRUE : Boolean.FALSE;
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

    @Override
    @JProfiler(jKey = "DMSWEB.BoxServiceImpl.commonGenBox", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BoxResponse commonGenBox(BoxRequest request, String systemType, boolean isNew) {
        Assert.notNull(request, "request must not be null");
        Assert.notNull(request.getType(), "request type must not be null");
        Assert.notNull(request.getReceiveSiteCode(), "request receiveSiteCode must not be null");
        Assert.notNull(request.getCreateSiteCode(), "request createSiteCode must not be null");
        Assert.notNull(request.getQuantity(), "request quantity must not be null");
        if (Box.BOX_TRANSPORT_TYPE_CITY.equals(request.getTransportType())) {
            Assert.notNull(request.getPredictSendTime(), "request predictSendTime must not be null");
        }
		this.log.info("BoxRequest's {}", request.toString());
		BoxResponse response = this.ok();
		// 调用冷链接口，校验是否配置了此路径
		if (BoxTypeEnum.TYPE_MS.getCode().equalsIgnoreCase(request.getType()) && !baseMajorManager.validateDirectlySentLine(request.getCreateSiteCode(),request.getReceiveSiteCode())) {
			Map<String, String> hintArgs = Maps.newHashMap();
			hintArgs.put(HintArgsConstants.ARG_FIRST, request.getCreateSiteName());
			hintArgs.put(HintArgsConstants.ARG_SECOND, request.getReceiveSiteName());
			response.setCode(SortingResponse.CODE_29461);
			response.setMessage(HintService.getHint(HintCodeConstants.CODE_COLD_CHAIN_SITE_NO_ROUTE, hintArgs));
			return response;
		}
        // 先生成路由信息
        // 获得路由信息创建站点与目的站点之间，用于标签打印，方便站点人员确认下一站发往哪
        CrossBoxResult<String[]> routInfoRes = null;
        try {
            routInfoRes = crossBoxService.getBoxRouter(request.getCreateSiteCode(), request.getReceiveSiteCode(), request.getPredictSendTime(), request.getTransportType());
            if (routInfoRes != null) {
                this.log.info("BasicSaf getCrossDmsBox RouterInfo:{} ResultCode:{} Message:{}"
                        ,routInfoRes.getData(), routInfoRes.getResultCode(), routInfoRes.getMessage());
                if (log.isInfoEnabled()) {
                    this.log.info("调用跨箱号中转获取箱号路由:{}", com.jd.bluedragon.utils.JsonHelper.toJson(routInfoRes));
                }
                if (CrossBoxResult.SUCCESS == routInfoRes.getResultCode() && routInfoRes.getData() != null && routInfoRes.getData().length == 2) {
                    // 没超过5个站点，用这个选择模板打印
                    response.setRouterInfo(routInfoRes.getData()[0].split("\\-\\-"));
                    // 超过5个站点，打印系统直接用他打印
                    response.setRouterText(routInfoRes.getData()[0].replace("--", "-"));
                }
            } else {
                log.warn("获得站点路由信息结果为空,参数信息：{}", com.jd.bluedragon.utils.JsonHelper.toJson(request));
            }
        } catch (Exception e) {
            this.log.error("获得站点路由信息失败： ", e);
        }
        // 生成箱号
        List<Box> availableBoxes;
        if (isNew) {
            availableBoxes = this.batchAddNew(this.toBoxWithRouter(request, routInfoRes), systemType);
        } else {
            availableBoxes = this.batchAdd(this.toBoxWithRouter(request, routInfoRes));
        }
        response.setBoxCodes(StringHelper.join(availableBoxes, "getCode", Constants.SEPARATOR_COMMA));

		// 打印客户端创建 并且是首次打印 推送箱号打印消息
		if (BoxSystemTypeEnum.PRINT_CLIENT.getCode().equals(systemType)) {
			pushBoxPrintMq(availableBoxes);
		}

        this.buildBoxPrintInfo(request.getCreateSiteCode(), request.getReceiveSiteCode(), response);
        return response;
    }


	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.BoxServiceImpl.genBoxWithoutSiteInfo", mState = {JProEnum.TP})
	public BoxResponse genBoxWithoutSiteInfo(BoxRequest request) {
		checkBoxRequerst(request);
		BoxResponse response = new BoxResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		List<Box> boxes = batchAddNew(toBox(request), request.getSystemType());
		assembleBoxCodes(boxes, response);
		return response;
	}

	private static void assembleBoxCodes(List<Box> boxes, BoxResponse response) {
		if (CollectionUtils.isNotEmpty(boxes)){
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i< boxes.size(); i++){
				if (ObjectHelper.isNotNull(boxes.get(i)) && ObjectHelper.isNotNull(boxes.get(i).getCode())) {
					sb.append(boxes.get(i).getCode());
				}
				if (i< boxes.size()-1) {
					sb.append(Constants.SEPARATOR_COMMA);
				}
			}
			response.setBoxCodes(sb.toString());
		}
	}

	private static void checkBoxRequerst(BoxRequest request) {
		Assert.notNull(request, "request must not be null");
		Assert.notNull(request.getType(), "request type must not be null");
		Assert.notNull(request.getQuantity(), "request quantity must not be null");
		if (Box.BOX_TRANSPORT_TYPE_CITY.equals(request.getTransportType())) {
			Assert.notNull(request.getPredictSendTime(), "request predictSendTime must not be null");
		}
	}

	private void pushBoxPrintMq(List<Box> availableBoxes) {
		if (CollectionUtils.isEmpty(availableBoxes)) {
			return;
		}
		try{
			for (Box box : availableBoxes) {
				log.info("推送箱号mq:{}",JsonHelper.toJson(box));
				boxFirstPrintProducer.sendOnFailPersistent(box.getCode(), JsonHelper.toJson(box));
			}
		}catch (Exception e) {
			log.info("推送箱号打印消息失败，消息体{}",JsonHelper.toJson(availableBoxes));
		}
	}

	/**
     * 构建目的地打印属性
     *
     * @param createSiteCode
     * @param receiveSiteCode
     * @param response
     */
    private void buildBoxPrintInfo(Integer createSiteCode, Integer receiveSiteCode, BoxResponse response) {
        CrossPackageTagNew crossPackageTag = baseMinorManager.queryNonDmsSiteCrossPackageTagForPrint(receiveSiteCode, createSiteCode);
        if (crossPackageTag != null) {
            response.setDestinationCrossCode(crossPackageTag.getDestinationCrossCode());
            response.setDestinationTabletrolleyCode(crossPackageTag.getDestinationTabletrolleyCode());
        }
        if (null != response) {
            UUID.randomUUID();
			response.setBoxTypes(BoxTypeEnum.getMap());
            response.setBoxSubTypes(BoxSubTypeEnum.ENUM_MAP);
		}

    }

    private BoxResponse ok() {
        return new BoxResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    }

    /**
     * 对象转换，包括路由信息
     * @param request
     * @param crossBoxResult
     * @return
     */
    private Box toBoxWithRouter(BoxRequest request, CrossBoxResult<String[]> crossBoxResult){
        Box box = toBox(request);
        if (crossBoxResult != null && CrossBoxResult.SUCCESS==crossBoxResult.getResultCode() && crossBoxResult.getData()!=null && crossBoxResult.getData().length==2){
            box.setRouterName(crossBoxResult.getData()[0]);
            box.setRouter(crossBoxResult.getData()[1]);
        }
        return box;
    }

    private Box toBox(BoxRequest request) {
        Box box = new Box();
        box.setType(request.getType());
        box.setBoxSubType(request.getSubType());
        box.setQuantity(request.getQuantity());
        box.setCreateSiteCode(request.getCreateSiteCode());
        box.setCreateSiteName(request.getCreateSiteName());
        box.setReceiveSiteCode(request.getReceiveSiteCode());
        box.setReceiveSiteName(request.getReceiveSiteName());
        box.setCreateUser(request.getUserName());
        box.setCreateUserCode(request.getUserCode());
        box.setTransportType(request.getTransportType());
        box.setMixBoxType(request.getMixBoxType());
        box.setPredictSendTime(request.getPredictSendTime());
        //临时占用字段处理站点商家重复
        box.setStatuses(request.getCreateSiteType());
        box.setUpdateUser(request.getReceiveSiteType());
        //设置状态和当前节点
        box.setStatus(com.jd.bluedragon.distribution.external.constants.BoxStatusEnum.OPEN.getStatus());
        box.setLastNodeType(OpBoxNodeEnum.PRINTBOXCODE.getNodeCode());
        return box;
    }

	/**
	 * 计算滑道号和笼车号
	 * @param router
	 * @throws Exception
	 */
    @Override
	public void computeRouter(List<Map.Entry<Integer, String>> router){
    	if(CollectionUtils.isEmpty(router)){
    		return;
		}
		CallerInfo info = Profiler.registerInfo("DMSWEB.BoxServiceImpl.computeRouter",Constants.UMP_APP_NAME_DMSWEB, false, true);
		try {
			Site site = siteService.get(router.get(router.size() - 1).getKey());
			for (int i = router.size() - 2; i >= 0; --i) {
				String port = "";
				//倒数第二个分拣中心 到目的地 ，而且箱号目的地非分拣中心 查询滑道号
				if (0 == i && site != null && !Integer.valueOf(64).equals(site.getType())) {
					CrossPackageTagNew packageTagNew = baseMinorManager.queryNonDmsSiteCrossPackageTagForPrint(
							router.get(router.size() - 1).getKey(),router.get(router.size() - 2).getKey());
					if(packageTagNew != null){
						port = packageTagNew.getOriginalCrossCode() + "-" + packageTagNew.getOriginalTabletrolleyCode();
						//箱号目的地 拼接目的滑道号和笼车号
						String destPort = packageTagNew.getDestinationCrossCode() + "-" + packageTagNew.getDestinationTabletrolleyCode();

						String destSiteName = router.get(router.size() - 1).getValue() + "_" + destPort;
						Map.Entry<Integer, String> dest =  router.get(router.size() - 1);
						dest.setValue(destSiteName);

					}
				} else {
					SortCrossDetail sortCrossDetail = basicPrimaryWS.getCrossCodeDetailByDmsID(
							router.get(router.size()- i - 2).getKey(),
							router.get(router.size()- i - 1).getKey().toString());
					if(sortCrossDetail != null){
						port = sortCrossDetail.getCrossCode() + "-" + sortCrossDetail.getTabletrolleyCode();
					}
				}
				Map.Entry<Integer, String> dest = router.get(router.size() - i - 2);
				 String siteName = router.get(router.size() - i - 2).getValue() + "_" + port;
				dest.setValue(siteName);
			}
		} catch (Exception e) {
			log.error("箱号创建计算道口号笼车号异常");
			Profiler.functionError(info);
		} finally {
			Profiler.registerInfoEnd(info);
		}
	}

    /**
     * 查询箱类型
     *
     * @param boxTypeReq 查询箱类型入参
     * @return 箱号类型列表
     * @author fanggang7
     * @time 2023-10-24 14:14:24 周二
     */
    @Override
    @JProfiler(jKey = "DMSWEB.BoxService.getBoxTypeList",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public Result<List<BoxTypeDto>> getBoxTypeList(BoxTypeReq boxTypeReq) {
        if(log.isInfoEnabled()){
            log.info("BoxServiceImpl.getBoxTypeList param {}", JsonHelper.toJson(boxTypeReq));
        }
        Result<List<BoxTypeDto>> result = Result.success();
        List<BoxTypeDto> typeDtoList = new ArrayList<>();
        result.setData(typeDtoList);
        try {
            final Result<Void> checkResult = this.checkParam4GetBoxTypeList(boxTypeReq);
            if (checkResult.isFail()) {
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }

            final BaseStaffSiteOrgDto createSiteInfo = baseMajorManager.getBaseSiteBySiteId(boxTypeReq.getOperateUser().getSiteCode());
            //营业部,自营京东派 人员使用部分箱型
            if (dmsConfigManager.getPropertyConfig().getTerminalSiteTypeListForBoxType().contains(createSiteInfo.getSubType())){
                typeDtoList.addAll(this.genBoxTypeDtoListByMap(siteBoxSubTypeMap));
                return result;
            }

            // 分拣类型场地看到全量箱号类型
            typeDtoList.addAll(this.genBoxTypeDtoListByMap(sortingBoxSubTypeMap));
        } catch (Exception e) {
            log.error("BoxServiceImpl.getBoxTypeList exception {}", JsonHelper.toJson(boxTypeReq), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private Result<Void> checkParam4GetBoxTypeList(BoxTypeReq boxTypeReq) {
        Result<Void> checkResult = Result.success();
        final OperateUser operateUser = boxTypeReq.getOperateUser();
        if (operateUser == null) {
            return checkResult.toFail("参数错误，operateUser不能为空");
        }
        if (operateUser.getSiteCode() == null) {
            return checkResult.toFail("参数错误，operateUser.siteCode不能为空");
        }
        if (StringUtils.isBlank(operateUser.getUserCode())) {
            return checkResult.toFail("参数错误，operateUser.userCode不能为空");
        }

        final RequestProfile requestProfile = boxTypeReq.getRequestProfile();
        if (requestProfile == null) {
            return checkResult.toFail("参数错误，requestProfile不能为空");
        }

        return checkResult;
    }

    private List<BoxTypeDto> genBoxTypeDtoListByMap(Map<String,String> boxTypeMap){
        List<BoxTypeDto> typeDtoList = new ArrayList<>();
        for (String code : boxTypeMap.keySet()) {
            final BoxSubTypeEnum boxSubTypeEnum = BoxSubTypeEnum.getFromCode(code);
            if (boxSubTypeEnum == null) {
                continue;
            }
            final BoxTypeDto boxTypeDto = new BoxTypeDto();
            boxTypeDto.setTypeCode(boxSubTypeEnum.getParentTypeCode());
            boxTypeDto.setSubTypeCode(code);
            boxTypeDto.setName(String.format("%s-%s", boxSubTypeEnum.getName(), boxSubTypeEnum.getParentTypeCode()));
            typeDtoList.add(boxTypeDto);
        }
        return typeDtoList;
    }
	@Override
	public boolean checkCollectPackageIfReleasedForSite(Integer orgId, Integer siteCode) {
		if ((checkIfReleasedForOrg(orgId)) || checkIfReleasedForSite(siteCode)){
			return true;
		}
		return false;
	}

	private boolean checkIfReleasedForOrg(Integer orgId) {
		if (ObjectHelper.isNotNull(dmsConfigManager.getPropertyConfig().getCollectPackageOrgForbiddenList()) && ObjectHelper.isNotNull(orgId)){
			if ("*".equals(dmsConfigManager.getPropertyConfig().getCollectPackageOrgForbiddenList())){
				return true;
			}
			List<Integer> orgForbiddenList = buildOrgForbiddenList(dmsConfigManager.getPropertyConfig().getCollectPackageOrgForbiddenList());
			if (CollectionUtils.isNotEmpty(orgForbiddenList) && orgForbiddenList.contains(orgId)){
				return true;
			}
		}
		return false;
	}

	private boolean checkIfReleasedForSite(Integer siteCode) {
		if (ObjectHelper.isNotNull(dmsConfigManager.getPropertyConfig().getCollectPackageSiteForbiddenList()) && ObjectHelper.isNotNull(siteCode)){
			List<Integer> siteForbiddenList = buildSiteForbiddenList(dmsConfigManager.getPropertyConfig().getCollectPackageSiteForbiddenList());
			if (CollectionUtils.isNotEmpty(siteForbiddenList) && siteForbiddenList.contains(siteCode)){
				return true;
			}
		}
		return false;
	}

	private List<Integer> buildOrgForbiddenList(String collectPackageOrgForbiddenList) {
		List<Integer> list =new ArrayList<>();
		if (collectPackageOrgForbiddenList.contains(",")){
			list = Arrays.asList(collectPackageOrgForbiddenList.split(",")).stream().map(s -> Integer.valueOf(s)).collect(Collectors.toList());
		}
		else {
			list.add(Integer.valueOf(collectPackageOrgForbiddenList));
		}
		return list;
	}

    private List<Integer> buildSiteForbiddenList(String collectPackageSiteForbiddenList) {
        List<Integer> list =new ArrayList<>();
        if (collectPackageSiteForbiddenList.contains(",")){
            list = Arrays.asList(collectPackageSiteForbiddenList.split(",")).stream().map(s -> Integer.valueOf(s)).collect(Collectors.toList());
        }
        else {
            list.add(Integer.valueOf(collectPackageSiteForbiddenList));
        }
        return list;
    }



	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.BoxServiceImpl.updateBox", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BoxResponse updateBox(UpdateBoxReq request) {
		BoxResponse response = new BoxResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		if (checkBoxIfCanUpdate(request,response)){
			execUpdateBox(request,response);
		}
        upsertBoxMaterialRelation4WmsBoxUsageInline(request);
		return response;
	}

    private void upsertBoxMaterialRelation4WmsBoxUsageInline(UpdateBoxReq request){
        // 绑定物资关系
        StoreBoxDetail storeBoxDetail = new StoreBoxDetail();
        storeBoxDetail.setBoxCode(request.getBoxCode());
        storeBoxDetail.setMaterialCode(request.getMaterialCode());
        storeBoxDetail.setCreateSiteCode(request.getCreateSiteCode());
        storeBoxDetail.setOperateUserErp(request.getUserErp());
        storeBoxDetail.setOperateUserErp(request.getUserErp());
        final Result<Boolean> upsertBoxMaterialRelation4WmsBoxUsageResult = upsertBoxMaterialRelation4WmsBoxUsage(storeBoxDetail);
        if (!upsertBoxMaterialRelation4WmsBoxUsageResult.isSuccess()) {
            log.error("updateBox upsertBoxMaterialRelation4WmsBoxUsageInline upsertBoxMaterialRelation4WmsBoxUsage fail {} {}", JsonHelper.toJson(upsertBoxMaterialRelation4WmsBoxUsageResult), JsonHelper.toJson(request));
            // throw new RuntimeException("处理箱号绑定物资失败！");
        }
    }

    /**
     * 更新箱号绑定物资关系
     * @param request 请求入参
     * @return 处理结果
     * @author fanggang7
     * @time 2024-02-24 13:00:58 周六
     */
    public Result<Boolean> upsertBoxMaterialRelation4WmsBoxUsage(StoreBoxDetail request) {
        Result<Boolean> result = Result.success();
        try {
            if(StringUtils.isBlank(request.getMaterialCode())){
                return result;
            }
            // 增加保存箱号绑定物资
            final BoxMaterialRelation boxMaterialRelation = new BoxMaterialRelation();
            boxMaterialRelation.setBoxCode(request.getBoxCode());
            boxMaterialRelation.setMaterialCode(request.getMaterialCode());
            boxMaterialRelation.setSiteCode(request.getCreateSiteCode());
            boxMaterialRelation.setOperatorErp(request.getOperateUserErp());
            boxMaterialRelation.setBindFlag(BoxMaterialBindFlagEnum.BIND.getCode());
            return boxMaterialRelationService.upsertBoxMaterialRelationBind(boxMaterialRelation);
        } catch (Exception e) {
            result.toFail("系统异常");
            log.error("BoxServiceImpl.upsertBoxMaterialRelation4WmsBoxUsage {}", JsonHelper.toJson(request), e);
        }
        return result;
    }

	/**
	 * 校验箱号信息是否能更新
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean checkBoxIfCanUpdate(UpdateBoxReq request, BoxResponse response) {
		Box box =boxDao.findBoxByCode(request.getBoxCode());
		if (ObjectHelper.isEmpty(box)){
			response.toError("未找到对应的箱号！");
			return false;
		}
		request.setBoxId(box.getId());

		if (ObjectHelper.isEmpty(request.getCreateSiteCode())){
			BaseResult<PsStoreInfo>  result =basicPrimaryWS.getStoreByCky2Id(request.getStoreInfo().getStoreType(),request.getStoreInfo().getCky2(),request.getStoreInfo().getStoreId(),"dms");
			if (ObjectHelper.isEmpty(result) || ObjectHelper.isEmpty(result.getData())
					|| ObjectHelper.isEmpty(result.getData().getDmsSiteId()) || ObjectHelper.isEmpty(result.getData().getDmsStoreName())){
				response.toError("未获取到仓对应的基础资料场地信息！");
				return false;
			}
			request.setCreateSiteCode(result.getData().getDmsSiteId());
			request.setCreateSiteName(result.getData().getDmsStoreName());
		}

		if (checkBoxIsSent(request.getBoxCode(),request.getCreateSiteCode())){
			response.toError("该箱号已经发货，禁止变更！");
			return false;
		}


		if (ObjectHelper.isEmpty(request.getReceiveSiteName())){
			BaseStaffSiteOrgDto baseStaffSiteOrgDto =basicPrimaryWS.getBaseSiteBySiteId(request.getReceiveSiteCode());
			if (ObjectHelper.isEmpty(baseStaffSiteOrgDto) || ObjectHelper.isEmpty(baseStaffSiteOrgDto.getSiteName())){
				response.toError("未获取到目的场地信息！");
				return false;
			}
			request.setReceiveSiteName(baseStaffSiteOrgDto.getSiteName());
		}

		return true;
	}

	/**
	 * 执行箱号信息变更
	 * @param request
	 * @param response
	 */
	private void execUpdateBox(UpdateBoxReq request, BoxResponse response) {
		Box box =new Box();
		box.setId(request.getBoxId());
		box.setCode(request.getBoxCode());
		box.setCreateSiteCode(request.getCreateSiteCode());
		box.setCreateSiteName(request.getCreateSiteName());
		box.setReceiveSiteCode(request.getReceiveSiteCode());
		box.setReceiveSiteName(request.getReceiveSiteName());
		box.setCreateUser(request.getUserName());
		box.setCreateUserCode(request.getUserCode());
		box.setTransportType(request.getTransportType());
		box.setMixBoxType(request.getMixBoxType());

		int rs =boxDao.updateById(box);
		if (rs >0){
			if (syncCacheDataFail(response, box)) {
				return;
			}
			response.toSucceed("更新箱号信息成功！");
			return;
		}
		response.toError("更新箱号信息失败！");
	}

	private boolean syncCacheDataFail(BoxResponse response, Box box) {
		boolean success =jimdbCacheService.del(getCacheKey(box.getCode()));
		if (!success){
			boolean isCatched = jimdbCacheService.setEx(getCacheKey(box.getCode()),JsonHelper.toJson(box), timeout);
			if (!isCatched){
				log.warn("box cache fail. the boxCode is " + box.getCode());
				response.toError("更新箱号失败！");
				return true;
			}
		}
		return false;
	}


	@Override
	public List<Box> listAllDescendantsByParentBox(Box parent) {
		return listAllDescendantsByParentBox(parent,null);
	}

	public List<Box> listAllDescendantsByParentBox(Box parent,Integer level) {
		if (ObjectHelper.isEmpty(level)){
			level = 0;
		}
		if (level >= Constants.BOX_NESTED_MAX_DEPTH){
			return Collections.emptyList();
		}
		if (ObjectHelper.isEmpty(parent) || ObjectHelper.isEmpty(parent.getCode())) {
			return Collections.emptyList();
		}
		InvokeResult<List<BoxRelation>>  rs = boxRelationService.getRelationsByBoxCode(parent.getCode());
		if (ObjectHelper.isEmpty(rs) || !rs.codeSuccess() || CollectionUtils.isEmpty(rs.getData())){
			return Collections.emptyList();
		}

		List<Box> boxList =assembleBoxList(rs.getData());
		for (Box box : boxList){
			box.setChildren(listAllDescendantsByParentBox(box,level+1));
		}
		return boxList;
	}

	private List<Box> assembleBoxList(List<BoxRelation> boxRelationList) {
		List<Box> boxes = boxRelationList.stream().map(boxRelation ->
		{
			Box box =new  Box();
			box.setCode(boxRelation.getRelationBoxCode());
			box.setType(BoxTypeV2Enum.getFromCode(boxRelation.getRelationBoxCode().substring(0,2)).getCode());
			return box;
		}).collect(Collectors.toList());
		return boxes;
	}

	@Override
	public List<Box> listSonBoxesByParentBox(Box parent) {
		if (ObjectHelper.isEmpty(parent) || ObjectHelper.isEmpty(parent.getCode())) {
			return Collections.emptyList();
		}
		InvokeResult<List<BoxRelation>>  rs = boxRelationService.getRelationsByBoxCode(parent.getCode());
		if (ObjectHelper.isEmpty(rs) || !rs.codeSuccess() || CollectionUtils.isEmpty(rs.getData())){
			return Collections.emptyList();
		}

		List<Box> boxList =assembleBoxList(rs.getData());
		return boxList;
	}

    /**
     * 获取父级箱号
     * @param box 查询参数
     * @return 父级箱号列表
     * @author fanggang7
     * @time 2024-03-10 09:44:33 周日
     */
    @Override
    public List<Box> listAllParentBox(Box box){
        return listAllParentBox(box, null);
    }

    /**
     * 获取父级箱号
     * @param box 查询参数
     * @param level 嵌套层数
     * @return 父级箱号列表
     * @author fanggang7
     * @time 2024-03-10 09:44:33 周日
     */
    public List<Box> listAllParentBox(Box box, Integer level) {
        if (ObjectHelper.isEmpty(level)){
            level = 0;
        }
        if (level >= Constants.BOX_NESTED_MAX_DEPTH){
            return Collections.emptyList();
        }
        if (ObjectHelper.isEmpty(box) || ObjectHelper.isEmpty(box.getCode())) {
            return Collections.emptyList();
        }
        final BoxRelation boxRelationParam = new BoxRelation();
        boxRelationParam.setRelationBoxCode(box.getCode());
        InvokeResult<List<BoxRelation>> rs = boxRelationService.queryBoxRelation(boxRelationParam);
        if (ObjectHelper.isEmpty(rs) || !rs.codeSuccess() || CollectionUtils.isEmpty(rs.getData())) {
            return Collections.emptyList();
        }

        List<Box> boxList = assembleBoxList(rs.getData());
        for (Box boxItem : boxList) {
            boxItem.setParent(listAllParentBox(box, level + 1));
        }
        return boxList;
    }

	@Override
	public boolean saveBoxBindRelation(BoxBindDto containerBindDto) {
		return false;
	}
}
