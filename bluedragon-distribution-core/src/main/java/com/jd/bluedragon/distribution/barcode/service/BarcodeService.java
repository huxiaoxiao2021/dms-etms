package com.jd.bluedragon.distribution.barcode.service;

import com.jd.bluedragon.distribution.barcode.domain.BarCode;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年07月12日 16时:14分
 */
public interface BarcodeService {
    List<BarCode> query(BarCode barCode);
}
