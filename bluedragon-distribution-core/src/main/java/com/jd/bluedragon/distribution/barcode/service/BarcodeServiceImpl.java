package com.jd.bluedragon.distribution.barcode.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.barcode.domain.BarCode;
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
    @Override
    public List<BarCode> query(BarCode barCode) {

        List<BarCode> list=new ArrayList<BarCode>();
        for (int i=0; i<100;i++){
            BarCode barCode1=new BarCode();
            barCode1.setBarcode(i+"");
            barCode1.setProductName("哈哈"+i);
            barCode1.setSkuId("111"+i);
            list.add(barCode1);
        }
        return list;
    }

    @Override
    public List<List<Object>> export(BarCode barCode) {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        List<Object> heads = new ArrayList<Object>();

        //添加表头
        heads.add("69码");
        heads.add("SKU");
        heads.add("商品名称");
        resList.add(heads);

        List<BarCode> data=query(barCode);
        if (data != null && data.size() > 0) {
            for (BarCode barCode1 : data) {
                List<Object> body = Lists.newArrayList();
                body.add(barCode1.getBarcode());
                body.add(barCode1.getSkuId());
                body.add(barCode1.getProductName());
                resList.add(body);
            }
        }
        return resList;
    }
}
