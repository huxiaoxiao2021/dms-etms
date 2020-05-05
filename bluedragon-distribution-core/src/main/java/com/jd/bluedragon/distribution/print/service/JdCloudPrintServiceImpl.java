package com.jd.bluedragon.distribution.print.service;

import com.jcloud.jss.http.Method;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintOssConfig;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintOutputConfig;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintRequest;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintResponse;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jss.JingdongStorageService;
import com.jd.ql.dms.print.engine.toolkit.IPrintPdfHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: JdCloudPrintServiceImpl
 * @Description: 云打印服务接口实现
 * @author: wuyoude
 * @date: 2019年8月14日 下午6:04:37
 *
 */
@Service
public class JdCloudPrintServiceImpl implements JdCloudPrintService {
	private static final Logger log = LoggerFactory.getLogger(JdCloudPrintServiceImpl.class);
	/**
	 * 监控key
	 */
	private static final String UMP_KEY = UmpConstants.UMP_KEY_BASE+"service.JdCloudPrintServiceImpl.";
	/**
	 * rest请求content-type
	 */
	private static final String REST_CONTENT_TYPE = "application/json; charset=UTF-8";
    /**
     * pdf输出路径日期格式
     */
    private static final String PDF_OUT_PATH_DATE_FOMART = "yyyyMM";
    /**
     * 云打印pdf输出文件oss配置
     */
    @Autowired
    private JdCloudPrintOssConfig pdfPrintOssConfig;
    /**
     * 本地pdf输出文件oss
     */
    @Autowired
    @Qualifier("pdfOutJssStorage")
    private JingdongStorageService pdfOutJssStorage;
    /**
     * pdf生成服务
     */
    @Autowired
    private IPrintPdfHelper printPdfHelper;
    @Autowired
    private SysConfigService sysConfigService;
    /**
     * 云打印url
     */
    @Value("${print.out.config.jdCloudIdcPrintUrl}")
    private String jdCloudIdcPrintUrl;
    /**
     * 临时存储生成的pdf文件
     */
    @Value("${print.out.config.localPdfTempPath}")
    private String localPdfTempPath;
    /**
     * 用于存储临时目录检查变量
     */
    private boolean localPdfTempPathExist = false;
    /**
     * 使用云打印开关
     */
    private boolean useJdCloudPrint = false;
	/**
	 * 获取默认的pdf请求
	 * @return
	 */
	public <M> JdCloudPrintRequest<M> getDefaultPdfRequest(){
    	JdCloudPrintRequest<M> jdCloudPrintRequest = new JdCloudPrintRequest<M>();
        jdCloudPrintRequest.setSys(Constants.SYS_CODE_DMS);
        Date printDate =  new Date();
        jdCloudPrintRequest.setTime(printDate.getTime());
        JdCloudPrintOutputConfig jdCloudPrintOutputConfig = new JdCloudPrintOutputConfig();
        jdCloudPrintOutputConfig.setOss(pdfPrintOssConfig);
        jdCloudPrintOutputConfig.setPath(DateHelper.formatDate(printDate, PDF_OUT_PATH_DATE_FOMART));
        List<JdCloudPrintOutputConfig> outputConfig = new ArrayList<JdCloudPrintOutputConfig>();
        outputConfig.add(jdCloudPrintOutputConfig);
        jdCloudPrintRequest.setOutputConfig(outputConfig);
        return jdCloudPrintRequest;
	}
	/**
	 * 调用京东云打印
	 * @param jdCloudPrintRequest
	 * @return
	 */
	@Override
	public <M> JdResult<List<JdCloudPrintResponse>> jdCloudPrint(JdCloudPrintRequest<M> jdCloudPrintRequest) {
		JdResult<List<JdCloudPrintResponse>> printResult = checkRequestParams(jdCloudPrintRequest);
		if(!printResult.isSucceed()){
			return printResult;
		}
		//开关未开启或者全局开关开启未开启，调用本地生成
		if(!useJdCloudPrint){
			return this.localPdfPrint(jdCloudPrintRequest);
		}
		CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY+"jdCloudPrint");
		RestTemplate template = new RestTemplate();
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", REST_CONTENT_TYPE);
		header.add("Accept", REST_CONTENT_TYPE);
		HttpEntity<String> formEntity = new HttpEntity<String>(JsonHelper.toJson(jdCloudPrintRequest), header);
		log.info("开始调用云打印,req:{}", jdCloudPrintRequest.getOrderNum());
		long startTime = System.currentTimeMillis();
		ResponseEntity<String> responseEntity = template.postForEntity(jdCloudIdcPrintUrl, formEntity, String.class);
		if (responseEntity != null && HttpStatus.OK.equals(responseEntity.getStatusCode())) {
			List<JdCloudPrintResponse> list = JsonHelper.jsonToList(responseEntity.getBody(), JdCloudPrintResponse.class);
			if(list != null){
				printResult.setData(list);
				printResult.toSuccess();
			}else{
				printResult.toFail("调用云打印IDC服务失败！");
			}
		}else{
			printResult.toError("调用云打印IDC服务失败！");
		}
		log.info("调用云打印结束,cost:{}ms,resp:{}",(System.currentTimeMillis() - startTime), JsonHelper.toJson(responseEntity));
		Profiler.registerInfoEnd(callerInfo);
		return printResult;
	}
	/**
	 * 本地生成pdf
	 * @param jdCloudPrintRequest
	 * @return
	 */
	private <M> JdResult<List<JdCloudPrintResponse>> localPdfPrint(JdCloudPrintRequest<M> jdCloudPrintRequest) {
		CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY+"localPdfPrint");
		JdResult<List<JdCloudPrintResponse>> printResult = new JdResult<List<JdCloudPrintResponse>>();
		log.info("开始生成pdf,req:{}", jdCloudPrintRequest.getOrderNum());
		long startTime = System.currentTimeMillis();

			String pdfFileName = jdCloudPrintRequest.getOrderNum()+"-"+System.currentTimeMillis()+".pdf";
			String jssPdfPath = jdCloudPrintRequest.getOutputConfig().get(0).getPath()+"/"+pdfFileName;
			if(!localPdfTempPathExist){
				checkAndCreateTempPath();
			}
			File pdfFile = new File(localPdfTempPath+"/"+pdfFileName);
			OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(pdfFile);
			this.printPdfHelper.generatePdf(outputStream, jdCloudPrintRequest.getTemplate(), 0, 0, 0, (List<Map<String,String>>)jdCloudPrintRequest.getModel());
			pdfOutJssStorage.bucket(pdfPrintOssConfig.getBucket()).object(jssPdfPath).entity(pdfFile).put();
			pdfFile.deleteOnExit();
			List<JdCloudPrintResponse> printResponses = new ArrayList<JdCloudPrintResponse>();
			JdCloudPrintResponse printResponse = new JdCloudPrintResponse();
			List<String> outputMsg = new ArrayList<String>();
			outputMsg.add(jssPdfPath);
			printResponse.setStatus(JdCloudPrintResponse.STATUS_SUC);
			printResponse.setOutputType(jdCloudPrintRequest.getOutputConfig().get(0).getType());
			printResponse.setOutputMsg(outputMsg);
			printResponses.add(printResponse);
			printResult.setData(printResponses);
			printResult.toSuccess();
		} catch (Throwable e) {
			Profiler.functionError(callerInfo);
			log.error("本地生成pdf失败！", e);
			printResult.toError("生成pdf失败！");
		}finally{
			Profiler.registerInfoEnd(callerInfo);
			if(outputStream != null){
				try {
					outputStream.close();
				} catch (IOException e) {
					log.error("OutPutStream关闭失败", e);
				}
			}
		}
		log.info("生成pdf结束,cost:{}ms,resp:{}",(System.currentTimeMillis() - startTime), JsonHelper.toJson(printResult));
		return printResult;
	}
	@Override
	public <M> JdResult<String> printPdfAndReturnWebUrl(JdCloudPrintRequest<M> jdCloudPrintRequest) {
		JdResult<String> result = new JdResult<String>();
		JdResult<List<JdCloudPrintResponse>> printResult = jdCloudPrint(jdCloudPrintRequest);
        if(printResult.isSucceed()){
        	List<JdCloudPrintResponse> pdfList = printResult.getData();
        	if(pdfList.size()>0){
        		JdCloudPrintResponse jdCloudPrintResponse = pdfList.get(0);
        		if(JdCloudPrintResponse.STATUS_SUC.equals(jdCloudPrintResponse.getStatus())
        				&& jdCloudPrintResponse.getOutputMsg() != null
        				&& jdCloudPrintResponse.getOutputMsg().size()>0){
        			String pdfPath = jdCloudPrintResponse.getOutputMsg().get(0);
        			//生成外链接
        			URI uri = pdfOutJssStorage.bucket(pdfPrintOssConfig.getBucket()).object(pdfPath).generatePresignedUrl();
        			if(uri != null){
        				result.setData(uri.getPath());
        			}else{
        				result.toFail("jss生成外链失败！");
        			}
        		}else{
        			result.toFail("生成pdf文件失败！");
        		}
        	}
        }else{
        	result.toFail(printResult.getMessageCode(), printResult.getMessage());
        }
		return result;
	}
	/**
	 * 打印参数验证
	 * @param jdCloudPrintRequest
	 * @return
	 */
	private <M> JdResult<List<JdCloudPrintResponse>> checkRequestParams(JdCloudPrintRequest<M> jdCloudPrintRequest){
		JdResult<List<JdCloudPrintResponse>> checkResult = new JdResult<List<JdCloudPrintResponse>>();
		if(jdCloudPrintRequest == null){
			checkResult.toFail("云打印请求参数不能为空！");
			return checkResult;
		}
		if(StringHelper.isAnyEmpty(jdCloudPrintRequest.getTemplate(),jdCloudPrintRequest.getTemplateVer())){
			checkResult.toFail("云打印请求参数模板及版本号不能为空！");
			return checkResult;
		}
		if(StringHelper.isEmpty(jdCloudPrintRequest.getOrderNum())){
			checkResult.toFail("云打印请求参数orderNum不能为空！");
			return checkResult;
		}
		checkResult.toSuccess();
		return checkResult;
	}
	/**
	 * 检查并创建临时目录
	 */
	private synchronized void checkAndCreateTempPath(){
		if(!localPdfTempPathExist){
			File pdfPath = new File(localPdfTempPath);
			if(!pdfPath.exists()){
				pdfPath.mkdirs();
			}
			localPdfTempPathExist = true;
		}
	}
	/**
	 * @return the useJdCloudPrint
	 */
	public boolean isUseJdCloudPrint() {
		return useJdCloudPrint;
	}
	/**
	 * @param useJdCloudPrint the useJdCloudPrint to set
	 */
	public void setUseJdCloudPrint(boolean useJdCloudPrint) {
		this.useJdCloudPrint = useJdCloudPrint;
	}
}
