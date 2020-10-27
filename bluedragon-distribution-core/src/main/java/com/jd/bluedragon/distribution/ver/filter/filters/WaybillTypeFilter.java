package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.cache.CacheService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 运单类型拦截
 * 1、是否是移动仓内配单(waybill_sign 第14位等于5，表示为移动仓内配单)
 */
public class WaybillTypeFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String REDIS_KEY_PREFIX_WAYBILL_TYPE_IN_BOX = "waybillTypeInBox_";

    /**
     * 箱号内存储的运单类型过期时间
     */
    private static final Long EXPIRE_TIME_WAYBILL_TYPE_IN_BOX = 2 * 24 * 60 * 60 * 1000L;
    /**
     * 普通运单类型（非移动仓内配）
     **/
    private static final Integer WAYBILL_TYPE_COMMON = 1;

    /**
     * 移动仓内配运单类型
     **/
    private static final Integer WAYBILL_TYPE_MOVING_WAREHOUSE_INNER = 2;


    /**
     * 半退运单类型
     **/
    private static final Integer WAYBILL_TYPE_PART_REVERSE = 4;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("WaybillTypeFilter start...");
        //拿到箱号，查缓存拿箱号的类型
        if (request.getBox() != null && StringUtils.isNotBlank(request.getBox().getCode())) {
            Integer waybillType = 0; //运单类型
            if (request.getWaybillCache() != null && request.getWaybillCache().getWaybillSign() != null &&
                    BusinessUtil.isSignChar(request.getWaybillCache().getWaybillSign(), 14, '5')) {
                waybillType = WAYBILL_TYPE_MOVING_WAREHOUSE_INNER;
            } else if(request.getReceiveSite() != null && BusinessHelper.isWMSBySiteType(request.getReceiveSite().getType())
                    && BusinessUtil.isPartReverse(request.getWaybillCache().getWaybillSign())){
                waybillType = WAYBILL_TYPE_PART_REVERSE;
            }else {
                waybillType = WAYBILL_TYPE_COMMON;
            }

            //查缓存查看箱号内的运单类型
            Integer boxWaybillType = this.getWaybillTypeInBoxFromRedis(request.getBox().getCode());
            logger.info("从缓存中获取的箱号[" + request.getBox().getCode() + "]内的运单类型为" + boxWaybillType);
            if (boxWaybillType == null || boxWaybillType == 0) {
                //没有命中缓存，说明这是该箱扫的第一单，将箱子可以装的运单类型写入缓存（移动仓内配单/非移动仓内配单），写入缓存
                this.setWaybillTypeInBoxToRedis(request.getBox().getCode(), waybillType);
            } else {
                //如果缓存中有，那么比较缓存中取出的类型是否和运单waybillSign判断得到的类型一致，如果不一致则提示
                if (WAYBILL_TYPE_COMMON.equals(boxWaybillType) && WAYBILL_TYPE_MOVING_WAREHOUSE_INNER.equals(waybillType)) {
                    //箱内装的是普通运单，当前运单为移动仓内配单
                    throw new SortingCheckException(SortingResponse.CODE_29408, SortingResponse.MESSAGE_29408);
                } else if (WAYBILL_TYPE_MOVING_WAREHOUSE_INNER.equals(boxWaybillType) && WAYBILL_TYPE_COMMON.equals(waybillType)) {
                    //箱内装的是移动仓内配单，当前运单为普通运单
                    throw new SortingCheckException(SortingResponse.CODE_29407, SortingResponse.MESSAGE_29407);
                }else if (request.getBusinessType() != null && request.getBusinessType() == Constants.BUSSINESS_TYPE_REVERSE){
                    //只有逆向的时候考虑半退问题
                    if(WAYBILL_TYPE_COMMON.equals(boxWaybillType) && WAYBILL_TYPE_PART_REVERSE.equals(waybillType)) {
                    //箱内装的是普通运单，当前运单为半退
                    throw new SortingCheckException(SortingResponse.CODE_29416, SortingResponse.MESSAGE_29416);
                    } else if (WAYBILL_TYPE_PART_REVERSE.equals(boxWaybillType) && WAYBILL_TYPE_COMMON.equals(waybillType)) {
                        //箱内装的是半退单，当前运单为普通运单
                        throw new SortingCheckException(SortingResponse.CODE_29415, SortingResponse.MESSAGE_29415);

                    }
                }
            }
        }

        chain.doFilter(request, chain);
    }

    /**
     * 从缓存中取出箱号内装的运单类型
     *
     * @param boxCode
     */
    private Integer getWaybillTypeInBoxFromRedis(String boxCode){
        String redisKey = REDIS_KEY_PREFIX_WAYBILL_TYPE_IN_BOX + boxCode;
        String boxTypeStr = jimdbCacheService.get(redisKey);
        //转换成Integer类型
        if(StringHelper.isNumberic(boxTypeStr)){
            return Integer.parseInt(boxTypeStr);
        }
        return null;
    }

    /**
     * 将箱号内装的运单类型存储到redis中
     *
     * @param boxCode
     * @param type
     */
    private void setWaybillTypeInBoxToRedis(String boxCode, Integer type){
        String redisKey = REDIS_KEY_PREFIX_WAYBILL_TYPE_IN_BOX + boxCode;
        jimdbCacheService.setEx(redisKey, String.valueOf(type), EXPIRE_TIME_WAYBILL_TYPE_IN_BOX);
    }
}
