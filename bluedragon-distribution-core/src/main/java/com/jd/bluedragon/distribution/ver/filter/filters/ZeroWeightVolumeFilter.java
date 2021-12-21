package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.funcSwitchConfig.TraderMoldTypeEnum;
import com.jd.bluedragon.distribution.packageWeighting.PackageWeightingService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.ldop.basic.dto.BasicTraderNeccesaryInfoDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * 称重量方拦截
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/12/20
 * @Description:
 */
public class ZeroWeightVolumeFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(ZeroWeightVolumeFilter.class);


    @Autowired
    private WaybillCacheService waybillCacheService;

    @Autowired
    PackageWeightingService packageWeightingService;

    @Autowired
    private BaseMinorManager baseMinorManager;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        //依赖数据获取
        String waybillCode = request.getWaybillCode();
        String waybillSign = request.getWaybillCache().getWaybillSign();
        String packageCode = request.getPackageCode();
        String customerCode = request.getWaybillCache().getCustomerCode();

        Boolean interceptFlag = Boolean.FALSE;
        //经济网
        if(BusinessUtil.isEconomicNetValidateWeightVolume(waybillCode,waybillSign)){
            logger.info("经济网场景无重量拦截,waybillCode={},packageCode={}",waybillCode,packageCode);
            if(!packageWeightingService.weightVolumeValidate(waybillCode, packageCode)){
                throw new SortingCheckException(SortingResponse.CODE_29403, HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_WITHOUT_WEIGHT_OR_VOLUME, request.getFuncModule()));
            }
        }else{

            //非经济网
            //获取拦截场景
            ZeroWeightVolumeCheckType checkType = needCheckWeightAndVolume(waybillCode,waybillSign,customerCode);
            if(!ZeroWeightVolumeCheckType.NOT_CHECK.equals(checkType)){
                //获取称重流水数据
                if (!packageWeightingService.weightVolumeValidate(waybillCode, packageCode)) {
                    logger.info("本地库未查到重量体积，调用运单接口检查,waybillCode={},packageCode={}",waybillCode,packageCode);
                    //称重流水未获取到时需要从运单接口查  数据没有下放的极端情况下
                    WaybillCache waybillNoCache = waybillCacheService.getNoCache(waybillCode);
                    if (waybillNoCache != null) {
                        logger.info("调用运单接口检查称重量方数据,waybillCode={},againW={},againV={}",waybillCode,waybillNoCache.getAgainWeight(),waybillNoCache.getSpareColumn2());
                        //判断运单上重量体积（复重：AGAIN_WEIGHT、复量方SPARE_COLUMN2）是否同时存在（非空，>0）
                        if (waybillNoCache.getAgainWeight() == null || waybillNoCache.getAgainWeight() <= 0
                                || StringUtils.isEmpty(waybillNoCache.getSpareColumn2()) || Double.parseDouble(waybillNoCache.getSpareColumn2()) <= 0) {
                            //校验商品重量和商品量方
                            if(ZeroWeightVolumeCheckType.CHECK_GOOD_OR_AGAIN_WEIGHT_VOLUME.equals(checkType)){
                                if (waybillNoCache.getWeight() == null || waybillNoCache.getWeight() <= 0
                                        || waybillNoCache.getVolume() == null || waybillNoCache.getVolume()  <= 0) {
                                    logger.info("调用运单接口检查称重量方数据,waybillCode={},w={},v={}",waybillCode,waybillNoCache.getWeight(),waybillNoCache.getVolume());
                                    logger.info("无下单称重量方数据，需要拦截，运单号{}",waybillCode);
                                    interceptFlag = Boolean.TRUE;
                                }
                            }else{
                                logger.info("无复重数据，需要拦截，运单号{}",waybillCode);
                                interceptFlag = Boolean.TRUE;
                            }
                        }
                    }else{
                        logger.info("调用运单接口检查称重量方数据,未获取到运单信息,waybillCode={}",waybillCode);
                    }
                }
                if(interceptFlag){
                    /* C网提示，B网拦截 */
                    if(BusinessUtil.isSignChar(waybillSign, 40, '0')){
                        throw new SortingCheckException(SortingResponse.CODE_39128, HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_WITHOUT_WEIGHT_WHEN_BOXING, request.getFuncModule()));
                    }else {
                        throw new SortingCheckException(SortingResponse.CODE_29403, HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_WITHOUT_WEIGHT_OR_VOLUME, request.getFuncModule()));
                    }
                }

            }

        }


    }

    private ZeroWeightVolumeCheckType needCheckWeightAndVolume(String waybillCode,String waybillSign,String customerCode){

        /*******************************     外单      *************************************/

        /*******************************     纯配外单      *************************************/

        if(BusinessHelper.isAllPureOutWaybill(waybillSign)){

            /*************纯配外单 统一不拦截场景开始******************/

            //逆向不拦截
            if (!BusinessUtil.isForeignForwardAndWaybillMarkForward(waybillSign)) {
                logger.info("纯配外单 逆向场景，不需要拦截，运单号{}",waybillCode);

                return ZeroWeightVolumeCheckType.NOT_CHECK;
            }
            if (WaybillUtil.isReturnCode(waybillCode)) {
                logger.info("纯配外单 返单场景，不需要拦截，运单号{}",waybillCode);
                return ZeroWeightVolumeCheckType.NOT_CHECK;
            }
            //退货取件
            if (!BusinessUtil.isSignChar(waybillSign, 17, '0')) {
                logger.info("纯配外单 逆向退货取件场景，不需要拦截，运单号{}",waybillCode);
                return ZeroWeightVolumeCheckType.NOT_CHECK;
            }
            //内部商家不拦截
            if(StringUtils.isNotBlank(customerCode)){
                BasicTraderNeccesaryInfoDTO basicTraderNeccesaryInfoDTO = baseMinorManager.getBaseTraderNeccesaryInfo(customerCode);
                //traderMold  内部商家类型编码
                if(basicTraderNeccesaryInfoDTO == null || basicTraderNeccesaryInfoDTO.getTraderMold()==null || basicTraderNeccesaryInfoDTO.getTraderMold().equals(TraderMoldTypeEnum.inside_type.getCode())){
                    logger.info("纯配外单 内部商家，不需要拦截，运单号{}",waybillCode);
                    return ZeroWeightVolumeCheckType.NOT_CHECK;
                }
            }

            /*************纯配外单 统一不拦截场景结束******************/

            /*************纯配外单 统一拦截场景开始******************/
            //运费临时欠款
            if(BusinessUtil.isTemporaryArrearsWaybill(waybillSign)){
                logger.info("运费临时欠款，需要拦截，运单号{}",waybillCode);
                return ZeroWeightVolumeCheckType.CHECK_AGAIN_WEIGHT_VOLUME;
            }

            /*************纯配外单 统一拦截场景结束******************/

            if(BusinessUtil.isCInternet(waybillSign)){
                /*************纯配外单 C网 统一拦截场景开始******************/

                //C网的信任商家拦截
                if(BusinessHelper.isTrust(waybillSign)){
                    logger.info("纯配外单 C网 信任商家，需要拦截，运单号{}",waybillCode);
                    return ZeroWeightVolumeCheckType.CHECK_GOOD_OR_AGAIN_WEIGHT_VOLUME;
                }

                //特殊商家类型 且  个性化运单场景
                if (BusinessUtil.isSignChar(waybillSign, 32, '0')
                        || BusinessUtil.isSignChar(waybillSign, 32, '2')
                        || BusinessUtil.isSignChar(waybillSign, 32, 'K')
                        || BusinessUtil.isSignChar(waybillSign, 32, 'Y')

                    &&
                        BusinessUtil.isSignChar(waybillSign, 24, '0')
                                || BusinessUtil.isSignChar(waybillSign, 24, '1')
                                || BusinessUtil.isSignChar(waybillSign, 24, '3')
                    ) {
                    logger.info("纯配外单 C网 特殊商家类型 且 个性化运单场景，需要拦截，运单号{}",waybillCode);
                    return ZeroWeightVolumeCheckType.CHECK_AGAIN_WEIGHT_VOLUME;
                }
                /*************纯配外单 C网 统一拦截场景结束******************/
            }else{
                /*************纯配外单 非C网 统一不拦截场景开始******************/
                //非C网信任商家不拦截
                if(BusinessHelper.isTrust(waybillSign)){
                    logger.info("纯配外单 非C网 信任商家场景，不需要拦截，运单号{}",waybillCode);
                    return ZeroWeightVolumeCheckType.NOT_CHECK;
                }
                //不需要称重
                if(BusinessUtil.isNoNeedWeight(waybillSign)){
                    logger.info("纯配外单 非C网 不需要称重场景，不需要拦截，运单号{}",waybillCode);
                    return ZeroWeightVolumeCheckType.NOT_CHECK;
                }

                /*************纯配外单 非C网 统一不拦截场景结束******************/


                /*************纯配外单 非C网 统一拦截场景开始******************/
                //快运零担
                if(BusinessUtil.isSignChar(waybillSign, 40, '2')){
                    logger.info("纯配外单 非C网 快运零担场景，需要拦截，运单号{}",waybillCode);
                    return ZeroWeightVolumeCheckType.CHECK_AGAIN_WEIGHT_VOLUME;
                }

                /*************纯配外单 非C网 统一拦截场景结束******************/

            }

        }

        /*******************************     纯配外单 结束      *************************************/

        /*******************************     外单 结束    *************************************/
        logger.info("任何条件未匹配到，不需要拦截，运单号{}",waybillCode);
        return ZeroWeightVolumeCheckType.NOT_CHECK;
    }

    enum ZeroWeightVolumeCheckType{
        //不校验
        NOT_CHECK,
        //校验复重
        CHECK_AGAIN_WEIGHT,
        //校验复重和复量方
        CHECK_AGAIN_WEIGHT_VOLUME,
        //校验复重和复量方  或者  商品重量和商品量方
        CHECK_GOOD_OR_AGAIN_WEIGHT_VOLUME;

    }
}
