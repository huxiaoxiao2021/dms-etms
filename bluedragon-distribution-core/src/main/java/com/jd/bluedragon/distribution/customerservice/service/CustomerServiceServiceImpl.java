package com.jd.bluedragon.distribution.customerservice.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.box.dao.BoxDao;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.dto.BaseTradeInfoDto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service("customerServiceService")
public class CustomerServiceServiceImpl implements CustomerServiceService {

    private final Log logger = LogFactory.getLog(this.getClass());

    private static final String separator = "$";
    
    private static final String siteType = "1024";
    
    private static final int timeout = 172800;

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

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer add(Box box) {
        Assert.notNull(box, "box must not be null");
        return this.boxDao.add(BoxDao.namespace, box);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<Box> batchAdd(Box param) {
    	List<Box> boxes = Lists.newArrayList();
        String boxCodePrefix = this.generateBoxCodePrefix(param);

        for (Integer loop = 0; loop < param.getQuantity(); loop++) {
            String boxCodeSuffix = StringHelper.padZero(this.genObjectId.getObjectId(this
                    .generateKey(param)));
            Box box = new Box();
            BeanHelper.copyProperties(box, param);
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
					.getBaseSiteBySiteId(box
							.getCreateSiteCode());
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
						.getBaseSiteBySiteId(box
								.getReceiveSiteCode());
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
					.getBaseSiteBySiteId(box
							.getReceiveSiteCode());
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

		return this.boxDao.findBoxByCode(code);
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
}
