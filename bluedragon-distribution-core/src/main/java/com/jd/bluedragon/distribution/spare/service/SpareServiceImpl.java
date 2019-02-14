package com.jd.bluedragon.distribution.spare.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.RdWmsStoreService;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.distribution.api.request.SpareRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spare.dao.SpareDao;
import com.jd.bluedragon.distribution.spare.domain.Spare;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("spareService")
public class SpareServiceImpl implements SpareService {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	private static final String separator = "$";
    @Autowired
    private RdWmsStoreService rdWmsStoreService;
	@Autowired
	private SpareDao spareDao;
	
	@Autowired
	private IGenerateObjectId genObjectId;
	
	private Integer add(Spare spare) {
		Assert.notNull(spare, "spare must not be null");
		return this.spareDao.add(SpareDao.namespace, spare);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer update(Spare spare) {
		return this.spareDao.update(SpareDao.namespace, spare);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public synchronized List<Spare> print(Spare spare) {
		return this.batchAdd(spare);
	}
	
	private List<Spare> batchAdd(Spare spare) {
		List<Spare> spares = new ArrayList<Spare>();
		String spareCodePrefix = this.generateSpareCodePrefix(spare);
		
		this.logger.info("备件条码前缀为：" + spareCodePrefix);

		for (Integer loop = 0; loop < spare.getQuantity(); loop++) {
			String spareCodeSuffix = StringHelper.padZero(this.genObjectId.getObjectId(this
					.generateKey(spare)));
			spare.setCode(spareCodePrefix + spareCodeSuffix);
			spares.add(new Spare(spareCodePrefix + spareCodeSuffix));
			this.add(spare);
		}

		return spares;
	}
	
	private String generateSpareCodePrefix(Spare spare) {
		return spare.getType() + DateHelper.formatDate(new Date(), Constants.DATE_FORMAT1);
	}
	
	private String generateKey(Spare spare) {
		return spare.getType() + SpareServiceImpl.separator + spare.getType()
				+ SpareServiceImpl.separator
				+ DateHelper.formatDate(new Date(), Constants.DATE_FORMAT1);
	}
	
	public Spare findBySpareCode(String code) {
		Assert.notNull(code, "code must not be null");
		return this.spareDao.findBySpareCode(code);
	}
	
	public List<Spare> findSpares(Spare spare) {
		Assert.notNull(spare, "spare must not be null");
		return this.spareDao.findSpares(spare);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer reprint(Spare spare) {
		Assert.notNull(spare.getUpdateUserCode(), "spare update user code must not be null");
		Assert.notNull(spare.getUpdateUser(), "spare update user must not be null");
		Assert.notNull(spare.getCode(), "spare code must not be null");
		spare.setStatus(Spare.STATUS_USED);
		return this.update(spare);
	}
    /**
     * 批量生成备件条码
     * @param spareReq
     * @return
     */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public InvokeResult<List<Spare>> genCodes(SpareRequest spareReq) {
		InvokeResult<List<Spare>> rest = new InvokeResult<List<Spare>>();
        Spare spare = new Spare();
        spare.setQuantity(spareReq.getQuantity());
        spare.setCreateUser(spareReq.getUserName());
        spare.setCreateUserCode(spareReq.getUserCode());
        spare.setTimes(Spare.DEFAULT_TIMES);
        spare.setStatus(Spare.STATUS_DEFAULT);
        //获取备件库条码前缀
        InvokeResult<String> storeTag = rdWmsStoreService
        		.getOrgStoreTag(spareReq.getOrgId(),spareReq.getStoreSiteId(), spareReq.getStoreId());
        //获取备件库条码前缀失败或返回为空,使用默认条码前缀
        if(InvokeResult.RESULT_SUCCESS_CODE == storeTag.getCode()
        		&& StringHelper.isNotEmpty(storeTag.getData())){
        	spare.setType(storeTag.getData());
        }else{
        	spare.setType(Constants.SPARE_CODE_PREFIX_DEFAULT);
        	logger.warn("备件库条码前缀获取为空，设置默认值为‘null’"+storeTag.getMessage());
        }
        List<Spare> spareCodes = this.batchAdd(spare);
        rest.setData(spareCodes);
        return rest;
	}
}
