package com.jd.bluedragon.distribution.consumer.exportLog;

import IceInternal.Ex;
import com.google.common.io.FileBackedOutputStream;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableDetailDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelation;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.bluedragon.distribution.exportlog.domain.ExportLog;
import com.jd.bluedragon.distribution.exportlog.service.ExportLogService;
import com.jd.bluedragon.distribution.financialForKA.domain.KaCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.domain.WaybillCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.service.WaybillCodeCheckService;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.enums.ExportLogStateEnum;
import com.jd.bluedragon.utils.CsvExportUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.fastjson.JSONObject;
import com.jd.jmq.common.message.Message;
import com.jd.ldop.utils.CollectionUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author liuchunhe1
 * @Description:运单号校验导出
 */
@Service("waybilCodeCheckExportLogConsumer")
public class WaybilCodeCheckExportLogConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillCodeCheckService waybillCodeCheckService;

    @Autowired
    private ExportLogService exportLogService;
    @Autowired
    private JssService jssService;

    @Value("${jss.waybillcheck.export.zip.bucket}")
    private String bucket;
    public static List<Object> heads = new ArrayList<Object>();

    static {
        //添加表头
        heads.add("运单号");
        heads.add("比较单号");
        heads.add("商家编码");
        heads.add("商家名称");
        heads.add("操作站点");
        heads.add("操作站点名称");
        heads.add("校验结果");
        heads.add("操作人ERP");
        heads.add("操作时间");
    }

    @JProfiler(jKey = "waybilCodeCheckExportLogConsumer.consume", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public void consume(Message message) throws Exception {

        if(StringHelper.isEmpty(message.getBusinessId())) {
            this.log.warn("waybilCodeCheckExportLogConsumer consume -->消息中没有运单号：{}", message.getText());
            return;
        }
        log.debug("waybilCodeCheckExportLogConsumer consume --> 消息Body为【{}】", message.getText());
        if(message == null || "".equals(message.getText()) || null == message.getText()) {
            this.log.warn("waybilCodeCheckExportLogConsumer consume -->消息为空");
            return;
        }
        if(!JsonHelper.isJsonString(message.getText())) {
            log.warn("waybilCodeCheckExportLogConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        KaCodeCheckCondition kaCodeCheckCondition = JsonHelper.fromJson(message.getText(), KaCodeCheckCondition.class);
        if(kaCodeCheckCondition == null) {
            this.log.warn("waybilCodeCheckExportLogConsumer consume -->消息转换对象失败：{}", message.getText());
            return;
        }
        try {
            ExportLog exportLog = new ExportLog();
            exportLog.setExportCode(message.getBusinessId());
            exportLog.setStatus(ExportLogStateEnum.DOING.getValue());
            exportLogService.update(exportLog);
            List<List<Object>> exportDataList = waybillCodeCheckService.getExportData(kaCodeCheckCondition);
            uploadJss(exportDataList, message.getBusinessId());
            ExportLog exportLogSuccess = new ExportLog();
            exportLogSuccess.setExportCode(message.getBusinessId());
            exportLogSuccess.setStatus(ExportLogStateEnum.SUCCESS.getValue());
            exportLogService.update(exportLogSuccess);
        }catch (Exception ex){
            ExportLog exportLogSuccess = new ExportLog();
            exportLogSuccess.setExportCode(message.getBusinessId());
            exportLogSuccess.setStatus(ExportLogStateEnum.FAIL.getValue());
            String errorMessage=ex.getMessage();
            if(StringUtils.isNotBlank(ex.getMessage())){
                if(ex.getMessage().length()>2000){
                    errorMessage=ex.getMessage().substring(0,2000);
                }
            }
            exportLogSuccess.setMessage(errorMessage);
            exportLogService.update(exportLogSuccess);
            this.log.error("waybilCodeCheckExportLogConsumer consume -->message.businessId:",message.getBusinessId(),ex);
            throw ex;
        }
    }

    /**
     * 将多个csv转换成ByteArrayOutputStream
     *
     * @param exportDataList
     * @param exportCode
     * @return
     * @throws Exception
     */
    private ByteArrayOutputStream mutiCsvToByteArrayOutputStream(List<List<Object>> exportDataList, String exportCode) throws Exception {
        Integer perSheetNum = 50000;
        Integer csvCount = exportDataList.size() / perSheetNum + (exportDataList.size() % perSheetNum == 0 ? 0 : 1);
        InputStream fis = null;
        ZipOutputStream zipOut = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            zipOut = new ZipOutputStream(bos);
            for(Integer i = 0; i < csvCount; i++) {
                int startIndex = i * perSheetNum;
                int endIndex = (i + 1) * perSheetNum;
                if(endIndex >= exportDataList.size()) {
                    endIndex = exportDataList.size();
                }
                List<List<Object>> subContent = new ArrayList<List<Object>>();
                subContent.add(0, heads);
                subContent.addAll(exportDataList.subList(startIndex, endIndex));
                String content = buildCsvString(subContent);
                if(!StringUtils.isBlank(content)) {
                    fis = new ByteArrayInputStream(content.getBytes());
                    zipFile(fis, zipOut, exportCode + "-" + (i + 1) + ".csv");
                }
            }
            return bos;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                bos.flush();
                bos.close();
                fis.close();
                zipOut.finish();
                zipOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 上传zip到jss云存储
     *
     * @param exportDataList
     * @param exportCode
     * @throws Exception
     */
    private void uploadJss(List<List<Object>> exportDataList, String exportCode) throws Exception {
        ByteArrayOutputStream bos = mutiCsvToByteArrayOutputStream(exportDataList, exportCode);
        InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
//        boolean hasBucketKey = jssService.hasBucket(bucket);
//        if(!hasBucketKey) {
//            jssService.createBucket(bucket);
//        }
        jssService.uploadFile(bucket, exportCode, bos.toByteArray().length, inputStream);
    }

    /**
     * 根据输入的文件与输出流对文件进行打包
     */
    public static void zipFile(InputStream inputStream, ZipOutputStream ouputStream, String fileName) {
        BufferedInputStream bins = null;
        try {
            bins = new BufferedInputStream(inputStream, 1024);
            ZipEntry entry = new ZipEntry(fileName);
            ouputStream.putNextEntry(entry);
            // 向压缩文件中输出数据
            int length;
            byte[] buffer = new byte[1024];
            while((length = bins.read(buffer)) != -1) {
                ouputStream.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭创建的流对象
            try {
                bins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 构建csv文本
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected static String buildCsvString(List<List<Object>> contentList) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        if(!CollectionUtils.isEmpty(contentList)) {
            for(List<Object> list : contentList) {
                for(Object item : list) {
                    stringBuilder.append(item).append(",");
                }
                stringBuilder.append("\r\n");
            }
        }
        return stringBuilder.toString();

    }

}
