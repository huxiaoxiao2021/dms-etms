package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.barcode.domain.DmsBarCode;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.omc.jsf.OmcGoodsService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author tangchunqing
 * @Description: omc 查询商品接口
 * @date 2018年07月16日 16时:45分
 */
@Service("omcGoodManager")
public class OmcGoodManagerImpl implements OmcGoodManager {
    private static final Logger logger = Logger.getLogger(OmcGoodManagerImpl.class);
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
    public DmsBarCode getBaseAndSpecInfo(String code) {
        DmsBarCode dmsBarCode = new DmsBarCode();
        dmsBarCode.setBarcode(code);
        try {
            String goodStr = omcGoodService.getBaseAndSpecInfo(code, OmcGoodsService.IS_SKU);
            if (StringHelper.isNotEmpty(goodStr)) {
                JSONObject goodObj = JSONObject.fromObject(goodStr);
                if (goodObj != null && !goodObj.isNullObject()) {
                    JSONArray baseAttrArr = goodObj.getJSONArray(BASEATTR);
                    if (baseAttrArr != null && !baseAttrArr.isEmpty()) {
                        JSONObject baseAttr = JSONObject.fromObject(baseAttrArr.get(0));
                        if (baseAttr != null && !baseAttr.isNullObject()) {
                            dmsBarCode.setSkuId(baseAttr.getString(SKUID));
                            dmsBarCode.setProductName(baseAttr.getString(NAME));
                        } else {
                            logger.error("69码查询失败,基本信息有问题2,code=" + code + ",goodStr=" + goodStr);
                        }
                    } else {
                        logger.error("69码查询失败,基本信息有问题1,code=" + code + ",goodStr=" + goodStr);
                    }
                } else {
                    logger.error("69码查询失败,明细解析有问题,code=" + code + ",goodStr=" + goodStr);
                }
            } else {
                logger.error("69码查询失败,未查到商品,code=" + code);
            }
        } catch (Exception e) {
            logger.error("69码查询失败,code=" + code, e);
        }
        return dmsBarCode;
    }
}
