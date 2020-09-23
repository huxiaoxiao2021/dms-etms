package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BoxHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dudong
 * @date 2016/2/29
 */
public class FBoxFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BoxService boxService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("do filter process...");
        if (BusinessUtil.isBoxcode(request.getBoxCode())) {

            //校验操作人分拣中心与箱号始发地是否一致
            if(!BoxHelper.isTheSameSiteWithOprator(request)){
                throw new SortingCheckException(SortingResponse.CODE_29010,
                        SortingResponse.MESSAGE_29010);
            }

            if(this.statusValidation(request.getBox())){
                throw new SortingCheckException(SortingResponse.CODE_29011,
                        SortingResponse.MESSAGE_29011);
            }

            if (BoxHelper.isOrdinaryForWarehouse(request.getBox())) {
                throw new SortingCheckException(SortingResponse.CODE_29004,
                        SortingResponse.MESSAGE_29004);
            } else if (BoxHelper.isLuxuryForWarehouse(request.getBox())) {
                throw new SortingCheckException(SortingResponse.CODE_29008,
                        SortingResponse.MESSAGE_29008);
            } else if (BoxHelper.isOrdinaryForAfterSale(request.getBox())) {
                throw new SortingCheckException(SortingResponse.CODE_29005,
                        SortingResponse.MESSAGE_29005);
            } else if (BoxHelper.isLuxuryForAfterSale(request.getBox())) {
                throw new SortingCheckException(SortingResponse.CODE_29009,
                        SortingResponse.MESSAGE_29009);
            }
        }

        chain.doFilter(request, chain);

    }

    private boolean statusValidation(Box box) {
        if (box != null) {
            return boxService.checkBoxIsSent(box.getCode(), box.getCreateSiteCode());
        }
        return false;
    }
}
