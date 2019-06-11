package com.jd.bluedragon.distribution.stash.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.stash.domain.EMGGoodsInfoDto;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.eclp.common.util.EclpNoUtil;
import com.jd.eclp.core.ApiResponse;
import com.jd.eclp.master.goods.domain.Goods;
import com.jd.eclp.master.goods.domain.GoodsFull;
import com.jd.eclp.master.goods.service.DownstreamGoodsService;
import com.jd.eclp.master.goods.service.GoodsService;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.SkuPackRelationDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * <P>
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/6/10
 */
@Service("packageStashService")
public class PackageStashServiceImpl implements PackageStashService{

    private static final Logger LOGGER = LoggerFactory.getLogger(PackageStashServiceImpl.class);

    @Autowired
    private DownstreamGoodsService downstreamGoodsService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Override
    public List<String> genEMGCodeByNum(Integer codeNum) {
        LOGGER.debug("将调用生成EMG码的接口，获取数量为：{}", codeNum);
        if (null == codeNum || codeNum <= 0 || codeNum >= 100) {
            LOGGER.warn("生成EMG条码的数量非法，方法直接退出。数量为：{}，不在区间(0,100]中", codeNum);
            return Collections.emptyList();
        }
        CallerInfo callerInfo = Profiler
                .registerInfo("DMS.WEB.PackageStashService.genEMGCodeByNum", Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
            ApiResponse<List<String>> response = downstreamGoodsService.getReservedGoodsNos(null,codeNum);
            LOGGER.debug("获取EMG条码，参数为{}，结果为：{}",codeNum, JsonHelper.toJson(response));
            if (null != response && response.isSuccess()) {
                return response.getData();
            } else {
                LOGGER.error("获取一定数量的EMG条码调用ECLP接口失败，入参{}，出参{}",codeNum, JsonHelper.toJson(response));
            }
        } catch (Exception e) {
            Profiler.functionError(callerInfo);
            LOGGER.error("获取一定数量的EMG条码调用ECLP接口异常，入参{}",codeNum, e);
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return Collections.emptyList();
    }

    @Override
    public EMGGoodsInfoDto getGoodsInfoByEMGCode(String emgCode) {
        LOGGER.debug("根据EMG码获取商品信息，实参为：{}",emgCode);
        if (StringHelper.isEmpty(emgCode) || !emgCode.startsWith(emgCode)) {
            LOGGER.warn("根据EMG条码获取商品信息，参数错误，方法退出，参数为：{}",emgCode);
            return null;
        }
        EMGGoodsInfoDto resultDto = new EMGGoodsInfoDto();//打印返回结果

        CallerInfo callerInfo = Profiler
                .registerInfo("DMS.WEB.PackageStashService.getGoodsInfoByEMGCode",Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            /* 将EMG码的前缀去掉得到商品的主键ID */
            long goodsId = EclpNoUtil.reverseNo2Id(emgCode, Constants.EMG_CODE_PREFIX);
            /* 根据商品的主键ID查询商品的信息 */
            GoodsFull goodsFull = goodsService.getGoods(goodsId);
            LOGGER.debug("根据EMG条码获取商品信息，入参：{}，出参：{}",emgCode, JsonHelper.toJson(goodsFull));
            if (null != goodsFull && null != goodsFull.getGoods()) {
                Goods goods = goodsFull.getGoods();
                resultDto.setDeptName(goods.getDeptName());//货主姓名
                resultDto.setDeptNo(goods.getDeptNo());//货主编号
                resultDto.setGoodsName(goods.getGoodsName());//商品名称
                resultDto.setGoodsNo(goods.getGoodsNo());//商品编码
                resultDto.setEmgCode(emgCode);//EMG条码
            } else  {
                LOGGER.warn("根据EMG码获取商品信息失败，入参：{}，出参：{}",emgCode, JsonHelper.toJson(goodsFull));
            }

            /* 查询EMG和包裹号的关系，截止到2019-6-10 18:29:26是一一对应关系 */
            BaseEntity<SkuPackRelationDto> waybillEntity = waybillQueryManager.getSkuPackRelation(emgCode);
            LOGGER.debug("根据EMG码获取SKU与包裹号的关系，入参：{}，出参：{}",emgCode, JsonHelper.toJson(waybillEntity));
            if (null != waybillEntity && waybillEntity.getResultCode() == Constants.INTERFACE_CALL_SUCCESS
                    && null != waybillEntity.getData()) {
                SkuPackRelationDto skuPackRelationDto = waybillEntity.getData();
                resultDto.setPackageCode(skuPackRelationDto.getPackageCode());//包裹号
                resultDto.setWaybillCode(WaybillUtil.getWaybillCode(skuPackRelationDto.getSku()));//运单号
            } else {
                LOGGER.warn("根据EMG码获取SKU与包裹号的关系失败，入参：{}，出参：{}",emgCode, JsonHelper.toJson(waybillEntity));
            }

            /* 根据运单号查询运单信息，获取商家订单号 */
            BaseEntity<BigWaybillDto> waybillDtoBaseEntity = waybillQueryManager
                    .getDataByChoice(resultDto.getWaybillCode(),true,false,false,false);
            LOGGER.debug("根据运单号获取运单waybillC信息，参数为:{}，结果为:{}",resultDto.getWaybillCode(),
                    JsonHelper.toJson(waybillDtoBaseEntity));

            if (null != waybillDtoBaseEntity && waybillDtoBaseEntity.getResultCode() == Constants.INTERFACE_CALL_SUCCESS
                    && waybillDtoBaseEntity.getData() != null && waybillDtoBaseEntity.getData().getWaybill() != null) {
                Waybill waybill = waybillDtoBaseEntity.getData().getWaybill();
                resultDto.setBusinessOrderCode(waybill.getBusiOrderCode());//商家订单号
            } else {
                LOGGER.warn("根据运单号查询运单waybillC信息失败，运单号：{}，EMG码：{}",resultDto.getWaybillCode(), emgCode);
            }

        } catch (Exception e) {
            Profiler.functionError(callerInfo);
            LOGGER.error("根据EMG获取商品信息调用ECLP接口异常，参数为：{}", emgCode, e);
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }

        return resultDto;
    }
}
