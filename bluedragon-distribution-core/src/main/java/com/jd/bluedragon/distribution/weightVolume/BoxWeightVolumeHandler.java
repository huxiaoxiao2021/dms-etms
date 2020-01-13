package com.jd.bluedragon.distribution.weightVolume;

import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.third.domain.ThirdBoxDetail;
import com.jd.bluedragon.distribution.third.service.ThirdBoxDetailService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BeanHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jd.bluedragon.Constants.TENANT_CODE_ECONOMIC;

/**
 * <p>
 *     按箱称重的处理类
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
@Service("boxWeightVolumeHandler")
public class BoxWeightVolumeHandler extends AbstractWeightVolumeHandler {

    @Autowired
    private BoxService boxService;

    @Autowired
    private SortingService sortingService;

    @Autowired
    private ThirdBoxDetailService thirdBoxDetailService;

    @Autowired
    private WeightVolumeHandlerStrategy weightVolumeHandlerStrategy;

    @Override
    protected boolean checkWeightVolumeParam(WeightVolumeEntity entity) {
        if (super.checkWeightVolumeParam(entity)) {
            return BusinessUtil.isBoxcode(entity.getBarCode());
        }
        return Boolean.FALSE;
    }

    @Override
    protected void handlerWeighVolume(WeightVolumeEntity entity) {
        /* 处理称重对象 */
        entity.setBoxCode(entity.getBarCode());
        if (entity.getWidth() != null && entity.getLength() != null && entity.getHeight() != null) {
            entity.setVolume(entity.getWidth() * entity.getLength() * entity.getHeight());
        }

        /* 获取箱号的信息 */
        Box box = boxService.findBoxByCode(entity.getBoxCode());
        if (box == null) {
            logger.error("根据容器号{}获取容器信息失败",entity.getBoxCode());
            return;
        }

        /* 存储箱号的运单信息，保留不重复项 */
        Set<String> waybillList = new HashSet<>();

        /* 从始发分拣的明细表中获取箱号的明细 */
        Sorting sortingCondition = new Sorting();
        sortingCondition.setBoxCode(entity.getBoxCode());
        sortingCondition.setCreateSiteCode(box.getCreateSiteCode());
        List<Sorting> sortings = sortingService.findByBoxCode(sortingCondition);
        if (sortings != null && !sortings.isEmpty()) {
            for (Sorting sorting : sortings) {
                String waybillCode = WaybillUtil.isWaybillCode(sorting.getWaybillCode())?
                        sorting.getWaybillCode() : WaybillUtil.getWaybillCode(sorting.getPackageCode());
                if (waybillList.contains(waybillCode)) {
                    continue;
                }
                /* 如果SET集合中不包含该运动号则添加 */
                waybillList.add(waybillCode);
            }
        }

        /* 从始发交接明细中获取箱号的明细 */
        if (waybillList.isEmpty()) {
            List<ThirdBoxDetail> thirdBoxDetails = thirdBoxDetailService.queryByBoxCode(TENANT_CODE_ECONOMIC,box.getCreateSiteCode(),entity.getBoxCode());
            if (thirdBoxDetails != null && !thirdBoxDetails.isEmpty()) {
                for (ThirdBoxDetail thirdBoxDetail : thirdBoxDetails) {
                    String waybillCode = WaybillUtil.isWaybillCode(thirdBoxDetail.getWaybillCode())?
                            thirdBoxDetail.getWaybillCode() : WaybillUtil.getWaybillCode(thirdBoxDetail.getPackageCode());
                    if (waybillList.contains(waybillCode)) {
                        continue;
                    }
                    /* 如果SET集合中不包含该运动号则添加 */
                    waybillList.add(waybillCode);
                }
            }
        }

        if (waybillList.isEmpty()) {
            logger.warn("获取此箱号的装箱明细数据失败：{}",entity.getBarCode());
            return;
        }

        Double itemWeight = entity.getWeight() == null? null :  entity.getWeight() / waybillList.size();
        Double itemLength = entity.getLength() == null? null : entity.getLength() / waybillList.size();
        Double itemWidth = entity.getWidth() == null? null : entity.getWidth() / waybillList.size();
        Double itemHeight = entity.getHeight() == null? null : entity.getHeight() / waybillList.size();
        Double itemVolume = entity.getVolume() == null? null : entity.getVolume() / waybillList.size();
        /* 循环处理箱明细 */
        for (String waybillCode : waybillList) {
            WeightVolumeEntity itemEntity = new WeightVolumeEntity();
            BeanHelper.copyProperties(itemEntity,entity);
            itemEntity.setWaybillCode(waybillCode);
            itemEntity.setVolume(itemVolume);
            itemEntity.setWeight(itemWeight);
            itemEntity.setLength(itemLength);
            itemEntity.setWidth(itemWidth);
            itemEntity.setHeight(itemHeight);
            /* 这个地方的handoverFlag设置为false，可以放到entity对象中，传过来 */
            weightVolumeHandlerStrategy.doHandler(itemEntity);
        }

    }
}
