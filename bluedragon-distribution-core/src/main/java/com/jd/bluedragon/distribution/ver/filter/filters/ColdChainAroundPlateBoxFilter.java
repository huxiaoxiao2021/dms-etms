package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.box.constants.BoxTypeEnum;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tp.common.utils.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 冷链围板箱过滤
 * 如果箱号是医药直发类型(围板箱)，则校验线路是否是已配置的冷链直发线路
 *
 * @author: wangjianle
 * @date: 2022/04/27 11:27
 */
public class ColdChainAroundPlateBoxFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("do ColdChainAroundPlateBoxFilter process waybillCode[{}]，box[{}]",request.getWaybillCode(),request.getBoxCode());
        // 识别箱号类型是否为医药直发类型
        Box box = request.getBox();
        if (Objects.isNull(box) || !BoxTypeEnum.TYPE_MS.getCode().equalsIgnoreCase(box.getType())) {
            chain.doFilter(request, chain);
            return;
        }
        // 获取运单预分拣站点
        Integer siteCode = request.getWaybillCache().getSiteCode();
        // 运单预分拣站点对应的分拣中心
        BaseStaffSiteOrgDto waybillSite = baseMajorManager.getBaseSiteBySiteId(siteCode);
        // 校验对应分拣中心与目的地是否一致，如果不一致则提示异常
        if (Objects.isNull(box.getReceiveSiteCode()) || Objects.isNull(waybillSite) || !box.getReceiveSiteCode().equals(waybillSite.getDmsId())) {
            throw new SortingCheckException(SortingResponse.CODE_39001,
                    HintService.getHintWithFuncModule(HintCodeConstants.RECEIVE_SITE_AND_DESTINATION_DIFFERENCE, request.getFuncModule()));
        }
        // 如果一致则继续校验线路是否是已配置的冷链直发线路（冷链直发线路通过调用冷链系统服务实现）
        if (!baseMajorManager.validateDirectlySentLine(box.getCreateSiteCode(),box.getReceiveSiteCode())) {
            throw new SortingCheckException(SortingResponse.CODE_AROUND_PLATE_BOX_ERROR,
                    HintService.getHintWithFuncModule(HintCodeConstants.MISSING_ROUTER, request.getFuncModule()));
        }
        chain.doFilter(request, chain);
    }
}
