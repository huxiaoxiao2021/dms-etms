package com.jd.bluedragon.distribution.barcode.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.core.base.OmcGoodManager;
import com.jd.bluedragon.distribution.barcode.domain.DmsBarCode;
import com.jd.bluedragon.utils.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangchunqing
 * @Description: 69码
 * @date 2018年07月12日 16时:14分
 */
@Service("barcodeService")
public class BarcodeServiceImpl implements BarcodeService {

    private static final String BARCODE_SPLITER_QUERY = "\n";
    private static final String BARCODE_SPLITER_EXPORT = "\r\n";
    @Autowired
    private OmcGoodManager omcGoodManager;

    @Override
    public List<DmsBarCode> query(DmsBarCode barCode) {

        List<DmsBarCode> result = Lists.newArrayList();
        if (barCode == null || barCode.getBarcode() == null) {
            return result;
        }
        /**
         *  前台输入例如
         *  6905321911667
         *  6928136815586
         *  6932543703367
         *  点查询传过来的换行是\n 点导出传过来的是\r\n  为了兼容，所以先把\r\n替换成\n
         */
        String[] barcodes = barCode.getBarcode().replace(BARCODE_SPLITER_EXPORT,BARCODE_SPLITER_QUERY).split(BARCODE_SPLITER_QUERY);

        if (barcodes.length > 0) {
            for (String barcode : barcodes) {
                if (StringHelper.isEmpty(barcode)){
                    continue;
                }
//                if (barcode.startsWith("69")) {
                    result.addAll(omcGoodManager.getBaseAndSpecInfo(barcode.trim()));
//                } else {
//                    DmsBarCode dmsBarCode = new DmsBarCode();
//                    dmsBarCode.setBarcode(barcode);
//                    dmsBarCode.setProductName("编码录入格式不正确");
//                    result.add(dmsBarCode);
//                }
            }
        }
        return result;
    }

    @Override
    public List<List<Object>> export(DmsBarCode barCode) {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        List<Object> heads = new ArrayList<Object>();

        //添加表头
        heads.add("SKU");
        heads.add("69码");
        heads.add("商品名称");
        resList.add(heads);

        List<DmsBarCode> data = query(barCode);
        if (data != null && data.size() > 0) {
            for (DmsBarCode barCode1 : data) {
                List<Object> body = Lists.newArrayList();
                body.add(barCode1.getSkuId());
                body.add(barCode1.getBarcode());
                body.add(barCode1.getProductName());
                resList.add(body);
            }
        }
        return resList;
    }
}
