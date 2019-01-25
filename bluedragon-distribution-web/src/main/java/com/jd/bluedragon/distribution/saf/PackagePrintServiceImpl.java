package com.jd.bluedragon.distribution.saf;

import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdCommandService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.distribution.print.domain.PrintPackageImage;
import com.jd.bluedragon.distribution.print.request.PackagePrintRequest;
import com.jd.bluedragon.distribution.print.service.PackagePrintService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.fastjson.JSONObject;
import com.jd.ql.dms.print.engine.TemplateEngine;
import com.jd.ql.dms.print.engine.TemplateFactory;
import com.jd.ql.dms.print.engine.toolkit.JPGBase64Encoder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.*;

/**
 * B网营业厅打印JSF接口
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2019年01月24日 13时:43分
 */
public class PackagePrintServiceImpl implements PackagePrintService {

    @Autowired
    @Qualifier("jsonCommandService")
    private JdCommandService jdCommandService;

    @Autowired
    private TemplateFactory templateFactory;

    private static Logger logger = Logger.getLogger(PackagePrintServiceImpl.class);

    @Override
    public JdResult<Map<String, Object>> getPrintInfo(JdCommand<String> printRequest) {
        logger.info("查询包裹信息参数：" + JsonHelper.toJson(printRequest));
        JdResult<Map<String, Object>> result = new JdResult<Map<String, Object>>();
        result.toSuccess();
        //TODO 校验systemCode和secretKey是否匹配
        String commandResult = jdCommandService.execute(JsonHelper.toJson(printRequest));
        logger.info("查询包裹信息结果：" + commandResult);
        JdResult jdResult = JsonHelper.fromJson(commandResult, JdResult.class);
        String data = JSONObject.parseObject(commandResult).getString("data");
        Map map = JsonHelper.json2MapNormal(data);

        result.setCode(jdResult.getCode());
        result.setMessage(jdResult.getMessage());
        result.setMessageCode(jdResult.getMessageCode());
        result.setData(map);
        return result;
    }

    @Override
    public JdResult<List<PrintPackageImage>> generateImage(JdCommand<String> printRequest) {
        logger.info("获取图片列表参数：" + JsonHelper.toJson(printRequest));
        JdResult<List<PrintPackageImage>> result = new JdResult<List<PrintPackageImage>>();
        JdResult<Map<String, Object>> data = getPrintInfo(printRequest);
        logger.info("获取图片列表之打印信息查询结果：" + JsonHelper.toJson(data));
        if(data == null || !data.isSucceed()){
            result.setCode(data.getCode());
            result.setMessage(data.getMessage());
            result.setMessageCode(data.getMessageCode());
            return result;
        }
        if(data.getData() == null || data.getData().isEmpty()){
            result.toError("查不到包裹信息!");
            return result;
        }
        JPGBase64Encoder encoder = new JPGBase64Encoder();
        List<Map<String, String>> printData = convertPrintMap(data.getData());
        result.setData(new ArrayList<PrintPackageImage>(printData.size()));
        for(Map<String, String> map : printData){
            TemplateEngine engine = null;
            String packageTagStr = null;
            PackagePrintRequest request = JsonHelper.fromJson(printRequest.getData(), PackagePrintRequest.class);
            try{
                engine = templateFactory.buildEngine(request.getTemplateName(), request.getTemplateVersion());
                engine.SetParameters(map);
                long startTime = System.currentTimeMillis();
                BufferedImage image = engine.GenerateImage(false, request.getDpiX(), request.getDpiY());
                logger.info(MessageFormat.format("生成标签时间为{0}ms", System.currentTimeMillis() - startTime));
                PrintPackageImage packageImage = new PrintPackageImage();
                packageImage.setImageBase64(encoder.encode(image, request.getDpiX(), request.getDpiY()));
                packageImage.setPackageCode(map.get("packageCode"));
                result.getData().add(packageImage);
            }catch (Throwable e){
                logger.error("打印服务异常，参数：" + JsonHelper.toJson(map), e);
                result.toError("打印服务异常:" + e.getMessage());
                break;
            }
        }
        return result;
    }

    @Override
    public JdResult<String> generatePdf(JdCommand<String> printRequest) {
        return null;
    }

    /**
     * 信息转换为字符串
     * @param data
     * @return
     */
    private List<Map<String, String>> convertPrintMap(Map<String, Object> data){

        List<PrintPackage> printPackages = Arrays.asList(JsonHelper.jsonToArray(JsonHelper.toJson(data.get("packList")), PrintPackage[].class));

        List<Map<String, String>> printData = new ArrayList<Map<String, String>>(printPackages.size());

        for(PrintPackage printPackage: printPackages){
            Map<String, String> map = new HashMap<String, String>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (!"packList".equals(entry.getKey()) && entry.getValue() != null) {
                    map.put(entry.getKey(), entry.getValue().toString());
                }

                map.put("packageCode", printPackage.getPackageCode());
                map.put("packageWeight", printPackage.getPackageWeight());
                map.put("packSerial", printPackage.getPackageIndex());
                if(printPackage.getWeight() != null ){
                    map.put("weight", printPackage.getWeight().toString());
                }
            }
            printData.add(map);

        }
        return printData;
    }
}
