package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.barcode.domain.DmsBarCode;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.omc.jsf.OmcGoodsService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: omc 查询商品接口
 * @date 2018年07月16日 16时:45分
 */
@Service("omcGoodManager")
public class OmcGoodManagerImpl implements OmcGoodManager {
    private static final Logger log = LoggerFactory.getLogger(OmcGoodManagerImpl.class);
    public static String BASEATTR = "baseAttr";
    public static String SKUID = "skuId";
    public static String NAME = "name";
    @Autowired
    private OmcGoodsService omcGoodService;

    /**
     * 通过编码获取商品基本信息和扩展属性信息
     *
     * @param code 商品编码
     * @return
     */
    public List<DmsBarCode> getBaseAndSpecInfo(String code) {
        List<DmsBarCode> list = Lists.newArrayList();

        try {
            String goodStr = omcGoodService.getBaseAndSpecInfo(code, OmcGoodsService.IS_UPC);
            if (StringHelper.isNotEmpty(goodStr)) {
                //用他推荐的 net.sf.json 解析json串，不要替换哟
                JSONObject goodObj = JSONObject.fromObject(goodStr);
                if (goodObj != null && !goodObj.isNullObject()) {
                    JSONArray baseAttrArr = goodObj.getJSONArray(BASEATTR);
                    if (baseAttrArr != null && !baseAttrArr.isEmpty()) {
                        for (int i = 0; i < baseAttrArr.size(); i++) {
                            JSONObject baseAttr = JSONObject.fromObject(baseAttrArr.get(i));
                            if (baseAttr != null && !baseAttr.isNullObject()) {
                                DmsBarCode dmsBarCode = new DmsBarCode();
                                dmsBarCode.setBarcode(code);
                                dmsBarCode.setSkuId(baseAttr.getString(SKUID));
                                dmsBarCode.setProductName(baseAttr.getString(NAME));
                                list.add(dmsBarCode);
                            } else {
                                log.warn("69码查询失败,基本信息有问题2,code= {},goodStr={}" ,code, goodStr);
                            }
                        }
                    } else {
                        log.warn("69码查询无结果,基本信息有问题1,code={},goodStr={}" ,code, goodStr);
                    }
                } else {
                    log.warn("69码查询失败,明细解析有问题,code={},goodStr={}" ,code, goodStr);
                }
            } else {
                log.warn("69码查询失败,未查到商品,code={}" , code);
            }
        } catch (Exception e) {
            log.error("69码查询失败,code={}" , code, e);
        }
        if (list.isEmpty()){
            DmsBarCode dmsBarCode = new DmsBarCode();
            dmsBarCode.setBarcode(code);
            list.add(dmsBarCode);
        }
        return list;
    }
}
