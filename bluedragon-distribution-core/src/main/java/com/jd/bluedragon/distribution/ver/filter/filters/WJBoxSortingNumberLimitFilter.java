package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;

/**
 * @ClassName WJBoxSortingNumberLimitFilter
 * @Description WJ箱号集包数量限制
 * @Author wyh
 * @Date 2020/12/16 15:19
 **/
public class WJBoxSortingNumberLimitFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WJBoxSortingNumberLimitFilter.class);

    @Autowired
    private UccPropertyConfiguration uccConfiguration;

    @Autowired
    private SortingService sortingService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        // WJ开头的箱号校验集包数量
        if (StringUtils.isNotBlank(request.getBoxCode()) && request.getBoxCode().startsWith(Box.TYPE_WJ)) {

            this.limitNumCheck(request);

        }

        chain.doFilter(request, chain);
    }

    private void limitNumCheck(FilterContext request) throws SortingCheckException {

        int boxLimitNum = uccConfiguration.getWJPackageNumberLimit();
        if (boxLimitNum <= 0) {
            return;
        }

        int curWaybillPackNum = request.getPackageNum() != null ? request.getPackageNum() : 0;
        int boxSortingNum = sortingService.findBoxPack(request.getBox().getCreateSiteCode(), request.getBoxCode());

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("校验WJ箱号集包数量限制. box:{}, packNum:{}, boxSortingNum:{}, boxLimitNum:{}",
                    request.getBoxCode(), curWaybillPackNum, boxSortingNum, boxLimitNum);
        }

        if (curWaybillPackNum > boxLimitNum) {
            throw new SortingCheckException(SortingResponse.CODE_29602, MessageFormat.format(SortingResponse.MESSAGE_29602_WAYBILL, boxLimitNum));
        }
        else if (boxSortingNum + curWaybillPackNum > boxLimitNum) {
            throw new SortingCheckException(SortingResponse.CODE_29602, MessageFormat.format(SortingResponse.MESSAGE_29602, boxLimitNum));
        }
    }
}
