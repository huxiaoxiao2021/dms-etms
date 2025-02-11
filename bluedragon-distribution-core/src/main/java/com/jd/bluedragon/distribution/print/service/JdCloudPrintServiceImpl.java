package com.jd.bluedragon.distribution.print.service;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

import com.jd.bluedragon.distribution.jss.oss.AmazonS3ClientWrapper;
import com.jd.bluedragon.distribution.jss.oss.AmazonS3ClientWrapper;
import com.jd.bluedragon.distribution.exception.jss.JssStorageException;
import com.jd.jss.http.Scheme;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintOssConfig;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintOutputConfig;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintOutputMsgItem;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintRequest;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintResponse;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jss.JingdongStorageService;
import com.jd.ql.dms.print.engine.toolkit.IPrintPdfHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

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
	 * 设置默认字符集
	 */
	private static final String DEFAULT_CHARSET_STR = "UTF-8";
	
	private static final String PDF_CONTENT_TYPE = "application/pdf";
	/**
	 * 设置默认字符集
	 */
	private static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_STR);
    /**
     * pdf输出路径日期格式
     */
    private static final String PDF_OUT_PATH_DATE_FOMART = "yyyyMM";
    /**
     * 云打印pdf输出文件oss配置
     */
    @Autowired
    @Qualifier("pdfPrintOssConfig")
    private JdCloudPrintOssConfig pdfPrintOssConfig;
    /**
     * 云打印pdf输出文件oss配置
     */
    @Autowired
    @Qualifier("pdfPrintAmazonConfig")
    private JdCloudPrintOssConfig pdfPrintAmazonConfig;
    
    /**
     * 本地pdf输出文件oss
     */
    @Autowired
    @Qualifier("pdfOutJssStorage")
    private JingdongStorageService pdfOutJssStorage;    

	@Autowired
	@Qualifier("dmswebAmazonS3ClientWrapper")
	private AmazonS3ClientWrapper dmswebAmazonS3ClientWrapper;

	@Value("${ossFolder:dms-print-pdfs}")
	private String ossFolder;

	/**
	 * 需要进行过滤的https域名
	 */
	@Value("#{'${jss.httpsSet}'.split(',')}")
    private Set<String> httpsSet;
	/**
	 * pdfOutJssStorage配置的endpoint
	 */
	@Value("${print.out.config.jss.endpoint}")
	private String printOutJssEndpoint;
    /**
     * pdf生成服务
     */
    @Autowired
    private IPrintPdfHelper printPdfHelper;
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
    
    private static String PDF_FOLDER = "dms-print-pdfs";
	/**
	 * 获取默认的pdf请求
	 * @return
	 */
	public <M> JdCloudPrintRequest<M> getDefaultPdfRequest(){
        return getDefaultPdfRequest(false);
	}
	/**
	 * 获取默认的pdf请求
	 * @return
	 */
	public <M> JdCloudPrintRequest<M> getDefaultPdfRequest(Boolean useAmazon){
    	JdCloudPrintRequest<M> jdCloudPrintRequest = new JdCloudPrintRequest<M>();
        jdCloudPrintRequest.setSys(Constants.SYS_CODE_DMS);
        Date printDate =  new Date();
        jdCloudPrintRequest.setTime(printDate.getTime());
        jdCloudPrintRequest.setUseAmazon(useAmazon);
        JdCloudPrintOutputConfig jdCloudPrintOutputConfig = new JdCloudPrintOutputConfig();
        if(Boolean.TRUE.equals(useAmazon)) {
        	jdCloudPrintOutputConfig.setOss(pdfPrintAmazonConfig);
        	jdCloudPrintOutputConfig.setPath(PDF_FOLDER+"/"+DateHelper.formatDate(printDate, PDF_OUT_PATH_DATE_FOMART));
        }else {
        	jdCloudPrintOutputConfig.setOss(pdfPrintOssConfig);
        	jdCloudPrintOutputConfig.setPath(DateHelper.formatDate(printDate, PDF_OUT_PATH_DATE_FOMART));
        }
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
		CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY
				+ "jdCloudPrint");
		long startTime = System.currentTimeMillis();
		String body = "";
		log.info("开始调用云打印,req:{}", jdCloudPrintRequest.getOrderNum());
		try {
			HttpClient httpClient = new HttpClient();
			PostMethod method = new PostMethod(jdCloudIdcPrintUrl);
			method.addRequestHeader("Content-type", REST_CONTENT_TYPE);
			method.addRequestHeader("Accept", REST_CONTENT_TYPE);
			method.setRequestEntity(new StringRequestEntity(JsonHelper.toJson(jdCloudPrintRequest),
					REST_CONTENT_TYPE,
					DEFAULT_CHARSET_STR));
			int statusCode = httpClient.executeMethod(method);
			if (statusCode == HttpStatus.OK.value()) {
				body = method.getResponseBodyAsString();
				List<JdCloudPrintResponse> list = JsonHelper.jsonToList(body,
						JdCloudPrintResponse.class);
				if (list != null) {
					printResult.setData(list);
					printResult.toSuccess();
					//生成下载外链
					this.generateDownloadUrl(jdCloudPrintRequest, printResult);					
				} else {
					printResult.toFail("调用云打印IDC服务失败！");
				}
			}
		} catch (Exception e) {
			Profiler.functionError(callerInfo);
			log.error("调用云打印IDC服务失败！req:{}",JsonHelper.toJson(jdCloudPrintRequest), e);
			printResult.toError("调用云打印IDC服务失败！");
		} finally {
			Profiler.registerInfoEnd(callerInfo);
		}
        log.info("调用云打印结束,cost:{}ms,resp:{}",(System.currentTimeMillis() - startTime),body);
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
			File pdfFile = null;
			OutputStream outputStream = null;
			InputStream inputStream = null;
		try {
			pdfFile = new File(localPdfTempPath+"/"+pdfFileName);
			outputStream = new FileOutputStream(pdfFile);
			inputStream = new FileInputStream(pdfFile);
			this.printPdfHelper.generatePdf(outputStream, jdCloudPrintRequest.getTemplate(), 0, 0, 0, (List<Map<String,String>>)jdCloudPrintRequest.getModel());
			List<JdCloudPrintOutputMsgItem> outputMsgItems = new ArrayList<JdCloudPrintOutputMsgItem>();
			JdCloudPrintOutputMsgItem outputMsgItem = new JdCloudPrintOutputMsgItem();
			
			if(Boolean.TRUE.equals(jdCloudPrintRequest.getUseAmazon())) {
				String url = dmswebAmazonS3ClientWrapper.putObjectThenGetOutNetUrl(inputStream, "", jssPdfPath,PDF_CONTENT_TYPE, pdfFile.length(),DateHelper.THREE_MONTH_DAYS);
				outputMsgItem.setPath(jssPdfPath);
				outputMsgItem.setUrl(url);
			}else {
				pdfOutJssStorage.bucket(pdfPrintOssConfig.getBucket()).object(jssPdfPath).entity(pdfFile).put();
				outputMsgItem.setPath(jssPdfPath);
				URI uri;
				String url = null;
				if (httpsSet.contains(printOutJssEndpoint)){
					uri = pdfOutJssStorage.bucket(pdfPrintOssConfig.getBucket()).object(jssPdfPath)
							.presignedUrlProtocol(Scheme.HTTPS).generatePresignedUrl();
				}
				else{
					uri = pdfOutJssStorage.bucket(pdfPrintOssConfig.getBucket()).object(jssPdfPath)
							.generatePresignedUrl();
				}
				if(uri != null){
					url = uri.toString();
				}
				outputMsgItem.setUrl(url);
			}
			if(outputMsgItem.getUrl() != null){
				List<JdCloudPrintResponse> printResponses = new ArrayList<JdCloudPrintResponse>();
				JdCloudPrintResponse printResponse = new JdCloudPrintResponse();
				
				printResponse.setStatus(JdCloudPrintResponse.STATUS_SUC);
				printResponse.setOutputType(jdCloudPrintRequest.getOutputConfig().get(0).getType());				
				printResponses.add(printResponse);
				printResult.setData(printResponses);
				printResult.toSuccess();
				outputMsgItems.add(outputMsgItem);
				printResponse.setOutputMsgItems(outputMsgItems);
			}else{
				printResult.toFail("jss生成外链失败！");
			}
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
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("inputStream关闭失败", e);
				}
			}			
			if(pdfFile != null){
				try {
					pdfFile.delete();
				} catch (Exception e) {
					log.error("pdfFile.delete()失败", e);
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
        				&& jdCloudPrintResponse.getOutputMsgItems() != null
        				&& jdCloudPrintResponse.getOutputMsgItems().size()>0){
    				result.setData(jdCloudPrintResponse.getOutputMsgItems().get(0).getUrl());
    				result.toSuccess();
        		}else{
        			result.toFail("生成pdf文件失败！");
        		}
        	}
        }else{
        	result.toFail(printResult.getMessageCode(), printResult.getMessage());
        }
		return result;
	}
	private <M> void generateDownloadUrl(JdCloudPrintRequest<M> jdCloudPrintRequest,JdResult<List<JdCloudPrintResponse>> printResult) {
		if(jdCloudPrintRequest == null) {
			return;
		}
		if(printResult == null 
				|| !printResult.isSucceed()
				|| CollectionUtils.isEmpty(printResult.getData()) ) {
			return;
		}
		JdCloudPrintResponse printData = printResult.getData().get(0);
		if(CollectionUtils.isEmpty(printData.getOutputMsg())) {
			return;
		}
		String outputMsg = printData.getOutputMsg().get(0);
		List<JdCloudPrintOutputMsgItem> outputMsgItems = new ArrayList<JdCloudPrintOutputMsgItem>();
		JdCloudPrintOutputMsgItem outputMsgItem = new JdCloudPrintOutputMsgItem();
		String url = null;
		//生成外链接
		if(Boolean.TRUE.equals(jdCloudPrintRequest.getUseAmazon())) {
			outputMsgItem = JsonHelper.fromJson(outputMsg, JdCloudPrintOutputMsgItem.class);
			url = this.dmswebAmazonS3ClientWrapper.generatePresignedOuterNetUrl(DateHelper.THREE_MONTH_DAYS, "", outputMsgItem.getPath());
			outputMsgItem.setUrl(url);
		}else {
			URI uri;
			if (httpsSet.contains(printOutJssEndpoint)){
				uri = pdfOutJssStorage.bucket(pdfPrintOssConfig.getBucket()).object(outputMsg)
						.presignedUrlProtocol(Scheme.HTTPS).generatePresignedUrl();
			}
			else{
				uri = pdfOutJssStorage.bucket(pdfPrintOssConfig.getBucket()).object(outputMsg)
						.generatePresignedUrl();
			}
			if(uri != null){
				url = uri.toString();
			}
			outputMsgItem.setPath(outputMsg);
			outputMsgItem.setUrl(url);
		}
		if(outputMsgItem.getUrl() != null){
			outputMsgItems.add(outputMsgItem);
			printData.setOutputMsgItems(outputMsgItems);
		}else{
			printResult.toFail("jss生成外链失败！");
		}
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
