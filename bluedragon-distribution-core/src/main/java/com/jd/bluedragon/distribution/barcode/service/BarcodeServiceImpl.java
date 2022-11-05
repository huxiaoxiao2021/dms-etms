package com.jd.bluedragon.distribution.barcode.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.OmcGoodManager;
import com.jd.bluedragon.distribution.barcode.domain.DmsBarCode;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tangchunqing
 * @Description: 69码
 * @date 2018年07月12日 16时:14分
 */
@Service("barcodeService")
public class BarcodeServiceImpl implements BarcodeService {

    private final Logger log = LoggerFactory.getLogger(BarcodeServiceImpl.class);

    private static final String BARCODE_SPLITER_QUERY = "\n";
    private static final String BARCODE_SPLITER_EXPORT = "\r\n";
    @Autowired
    private OmcGoodManager omcGoodManager;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    @Override
    public List<DmsBarCode> query(DmsBarCode barCode) {
        /**
         *  前台输入例如
         *  6905321911667
         *  6928136815586
         *  6932543703367
         *  点查询传过来的换行是\n 点导出传过来的是\r\n  为了兼容，所以先把\r\n替换成\n
         */
        String[] barcodes = barCode.getBarcode().replace(BARCODE_SPLITER_EXPORT,BARCODE_SPLITER_QUERY).split(BARCODE_SPLITER_QUERY);

        return query(barcodes);
    }

    @Override
    public List<DmsBarCode> query(String[] barCodes) {
        List<DmsBarCode> result = Lists.newArrayList();

        if (barCodes.length > 0) {
            for (String barcode : barCodes) {
                if (StringHelper.isEmpty(barcode)){
                    continue;
                }
                result.addAll(omcGoodManager.getBaseAndSpecInfo(barcode.trim()));
            }
        }
        return result;
    }

    /**
     * 导出数据查询
     * @param barCode
     * @param bufferedWriter
     */
    @Override
    public void export(DmsBarCode barCode, BufferedWriter bufferedWriter) {
        try {
            long start = System.currentTimeMillis();
            // 写入表头
            Map<String, String> headerMap = getHeaderMap();
            CsvExporterUtils.writeTitleOfCsv(headerMap, bufferedWriter, headerMap.values().size());
            List<DmsBarCode> data = query(barCode);

            // 输出至excel
            CsvExporterUtils.writeCsvByPage(bufferedWriter, headerMap, data);
            long end = System.currentTimeMillis();
            exportConcurrencyLimitService.addBusinessLog(JsonHelper.toJson(barCode), ExportConcurrencyLimitEnum.DMS_BAR_CODE_REPORT.getName(),end-start,data.size());
        }catch (Exception e){
            log.error("69码查询商品名称导出异常",e);
        }
    }

    private Map<String, String> getHeaderMap() {
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("skuId","SKU");
        headerMap.put("barcode","69码");
        headerMap.put("productName","商品名称");
        return headerMap;
    }
}
