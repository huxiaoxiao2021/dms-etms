package com.jd.bluedragon.external.service.impl;

import com.jd.bluedragon.KvIndexConstants;
import com.jd.bluedragon.distribution.api.response.box.BoxDto;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.external.service.DmsExternalReadService;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分拣额外只读服务接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-12-09 15:40:10 周六
 */
@Service("dmsCoreExternalReadService")
public class DmsExternalReadServiceImpl implements DmsExternalReadService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private KvIndexDao kvIndexDao;

    @Autowired
    private BoxService boxService;

    /**
     * 根据包裹号查询箱号数据
     *
     * @param packageCode 包裹号
     * @return 箱号数据
     * @author fanggang7
     * @time 2023-12-09 15:30:44 周六
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.getBoxInfoByPackageCode", mState = {JProEnum.TP})
    public Result<BoxDto> getBoxInfoByPackageCode(String packageCode) {
        log.info("DmsExternalReadServiceImpl.getBoxInfoByPackageCode param {}", packageCode);
        final Result<BoxDto> result = Result.success();
        try {
            final String kvKey = String.format(KvIndexConstants.KEY_PACKAGE_BOX_ASSOCIATION, packageCode);
            final String boxCodeExistStr = kvIndexDao.queryRecentOneByKeyword(kvKey);
            if (StringUtils.isNotBlank(boxCodeExistStr)) {
                final Box box = boxService.findBoxByCode(boxCodeExistStr);
                if (box == null) {
                    return result;
                }
                final BoxDto boxDto = new BoxDto();
                BeanUtils.copyProperties(box, boxDto);
                result.setData(boxDto);
            }
        } catch (Exception e) {
            log.error("DmsExternalReadServiceImpl.getBoxInfoByPackageCode exception ", e);
            result.toFail("系统异常");
        }
        return result;
    }
}
