package com.jd.bluedragon.distribution.rest.filesupport;

import com.amazonaws.services.s3.model.S3Object;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.FileMetadata;
import com.jd.bluedragon.distribution.api.request.FileRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jss.oss.AmazonS3ClientWrapper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @program: ql-dms-distribution
 * @description: 仅提供打印组件、打印客户端下载标签使用，标签设计器下载使用，其他场景禁止使用
 * @author: xumigen
 * @create: 2023-02-28 19:35
 **/

@Component
@Path(Constants.REST_URL)
public class LabelPrintFileResource {
    private static Logger log = LoggerFactory.getLogger(LabelPrintFileResource.class);
    @Autowired
    @Qualifier("labelprintAmazonS3ClientWrapper")
    private AmazonS3ClientWrapper labelprintAmazonS3ClientWrapper;

    @Value("${fileModifySecretKey}")
    protected String fileModifySecretKey;

    /**
     * 表单中的附件属性名称
     */
    private static final String FORM_FILE_ELEMENT_NAME = "attachment";

    /**
     * 表单中的 bucketName属性名称
     */
    private static final String FORM_FOLDER_ELEMENT_NAME = "folder";

    /**
     * 表单中文件名称属性名
     */
    private static final String FORM_FILENAME_ELEMENT_NAME = "fileName";

    /**
     * 调用方系统名称
     */
    private static final String FORM_SOURCESYSNAME_ELEMENT_NAME = "sourceSysName";

    /**
     * 表单中读写权限secretKey
     */
    private static final String FORM_SECRETKEY_ELEMENT_NAME = "secretKey";

    /**
     * 如果上传文件的名称包含中文，需要客户端对内容做编码，编码方式：
     * URLEncoder.encode("你好", "utf-8")
     * 获取值的时候已经做了解码 URLDecoder.decode("", "UTF-8")
     * @param formDataInputs
     * @return
     */
    @POST
    @Path("/uploadfile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public InvokeResult<String> uploadfile(MultipartFormDataInput formDataInputs){
        CallerInfo info = Profiler.registerInfo("DMS.WEB.FileResource.uploadfile", Constants.UMP_APP_NAME_DMSWEB, false, true);
        InvokeResult<String> invokeResult = new InvokeResult<>();
        log.info("上传文件开始");
        String folder = null;
        String fileName = null;
        String secretKey = null;
        String sourceSysName = null;
        try {
            Map<String, List<InputPart>> uploadForm = formDataInputs.getFormDataMap();
            folder = getElementValue(uploadForm,FORM_FOLDER_ELEMENT_NAME);
            fileName = getElementValue(uploadForm,FORM_FILENAME_ELEMENT_NAME);
            secretKey = getElementValue(uploadForm,FORM_SECRETKEY_ELEMENT_NAME);
            sourceSysName = getElementValue(uploadForm,FORM_SOURCESYSNAME_ELEMENT_NAME);
            log.info("上传文件-folder[{}]fileName[{}]secretKey[{}]sourceSysName[{}]", folder,fileName,secretKey,sourceSysName);
            FileRequest fileRequest = new FileRequest();
            fileRequest.setSecretKey(secretKey);
            fileRequest.setSourceSysName(sourceSysName);
            InvokeResult<Boolean> result = this.checkParams(fileRequest);
            if(!result.codeSuccess()) {
                log.error("上传文件-参数校验不通过fileRequest[{}]", JsonHelper.toJson(fileRequest));
                invokeResult.error(result.getMessage());
                return invokeResult;
            }
            if(StringUtils.isEmpty(secretKey)){
                log.error("上传文件-secretKey不能为空");
                invokeResult.error("上传文件secretKey不能为空");
                return invokeResult;
            }
            if(StringUtils.isEmpty(folder)){
                log.error("上传文件-文件夹folder内容为空");
                invokeResult.error("上传文件内容为空表单name："+FORM_FOLDER_ELEMENT_NAME);
                return invokeResult;
            }
            if(StringUtils.isEmpty(fileName)){
                log.error("上传文件-文件夹folder内容为空");
                invokeResult.error("上传文件内容为空表单name："+FORM_FILENAME_ELEMENT_NAME);
                return invokeResult;
            }
            if(StringUtils.isEmpty(sourceSysName)){
                log.error("上传文件-sourceSysName内容为空");
                invokeResult.error("上传文件内容为空表单name："+FORM_SOURCESYSNAME_ELEMENT_NAME);
                return invokeResult;
            }
            List<InputPart> inputParts = uploadForm.get(FORM_FILE_ELEMENT_NAME);
            if(CollectionUtils.isEmpty(inputParts)){
                log.error("上传文件folder[{}]附件内容为空",FORM_FOLDER_ELEMENT_NAME);
                invokeResult.error("上传文件内容为空表单属性name："+FORM_FILE_ELEMENT_NAME);
                return invokeResult;
            }
            InputStream inputStream = inputParts.get(0).getBody(InputStream.class,null);
            MediaType mediaType = inputParts.get(0).getMediaType();
            labelprintAmazonS3ClientWrapper.putObjectWithFlow(inputStream,mediaType.toString(),folder,fileName);
            invokeResult.success();
            log.info("上传文件结束folder[{}]fileName[{}]",folder,fileName);
            return invokeResult;
        } catch (Exception e) {
            log.error("上传文件报错fileName[{}]folder[{}]",fileName,folder,e);
            Profiler.functionError(info);
            invokeResult.error("上传文件报错");
            return invokeResult;
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }



    @POST
    @Path("/downloadFile")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response downloadFile(FileRequest fileRequest) {
        log.info("下载文件-开始fileRequest[{}]", JsonHelper.toJson(fileRequest));
        Response.ResponseBuilder response = null;
        InvokeResult<Boolean> result = this.checkParams(fileRequest);
        if(!result.codeSuccess()) {
            log.error("下载文件报错-参数校验不通过fileRequest[{}]", JsonHelper.toJson(fileRequest));
            response = Response.status(Response.Status.BAD_REQUEST);
            return response.build();
        }
        CallerInfo info = Profiler.registerInfo("DMS.WEB.FileResource.downloadFile", Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
            log.info("下载文件-fileRequest[{}]", JsonHelper.toJson(fileRequest));
            if(StringUtils.isEmpty(fileRequest.getFolder()) || StringUtils.isEmpty(fileRequest.getFileName())|| StringUtils.isEmpty(fileRequest.getSourceSysName())){
                log.error("下载文件报错-参数校验不通过fileRequest[{}]", JsonHelper.toJson(fileRequest));
                response = Response.status(Response.Status.BAD_REQUEST);
                return response.build();
            }
            S3Object s3Object = labelprintAmazonS3ClientWrapper.getObject(fileRequest.getFolder(),fileRequest.getFileName());
            if(s3Object == null){
                log.error("下载文件报错-文件不存在fileName[{}]",fileRequest.getFileName());
                response = Response.status(Response.Status.NO_CONTENT);
                return response.build();

            }
            response = Response.ok(s3Object.getObjectContent());
            response.header("Content-Disposition", "attachment;filename=" + URLDecoder.decode(fileRequest.getFileName(), "UTF-8"));
            response.type(s3Object.getObjectMetadata().getContentType());
            return response.build();
        } catch (Exception e) {
            log.error("下载文件报错fileRequest[{}]", JsonHelper.toJson(fileRequest),e);
            Profiler.functionError(info);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return response.build();
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
    @POST
    @Path("/downloadLabelFile")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response downloadLabelFile(FileRequest fileRequest) {
        log.info("下载文件-开始fileRequest[{}]", JsonHelper.toJson(fileRequest));
        Response.ResponseBuilder response = null;
        InvokeResult<Boolean> result = this.checkParams(fileRequest);
        if(!result.codeSuccess()) {
            log.error("下载文件报错-参数校验不通过fileRequest[{}]", JsonHelper.toJson(fileRequest));
            response = Response.status(Response.Status.BAD_REQUEST);
            return response.build();
        }
        long startTime = System.currentTimeMillis();
        S3Object s3Object = null;
        boolean needClose = false;
        CallerInfo info = Profiler.registerInfo("DMS.WEB.FileResource.downloadLabelFile", Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
            log.info("下载文件-fileRequest[{}]", JsonHelper.toJson(fileRequest));
            if(StringUtils.isEmpty(fileRequest.getFolder()) || StringUtils.isEmpty(fileRequest.getFileName())|| StringUtils.isEmpty(fileRequest.getSourceSysName())){
                log.error("下载文件报错-参数校验不通过fileRequest[{}]", JsonHelper.toJson(fileRequest));
                response = Response.status(Response.Status.BAD_REQUEST);
                return response.build();
            }
            FileMetadata remoteMeta = labelprintAmazonS3ClientWrapper.getObjectMetadataWithCache(fileRequest.getFolder(),fileRequest.getFileName());
            if(!FileMetadata.needDownload(fileRequest.getLocalMeta(), remoteMeta)) {
            	response = Response.ok();
            	response.header("file-meta-data", JsonHelper.toJson(remoteMeta));
            	return response.build();
            }
            s3Object = labelprintAmazonS3ClientWrapper.getObjectWithUncheck(fileRequest.getFolder(),fileRequest.getFileName());
            if(s3Object == null){
                log.error("下载文件报错-文件不存在fileName[{}]",fileRequest.getFileName());
                response = Response.status(Response.Status.NO_CONTENT);
                return response.build();
            }
            long endTime = System.currentTimeMillis();
            //请求超时处理
            if(fileRequest.getTimeOut() != null && (endTime - startTime) > fileRequest.getTimeOut()) {
                log.error("本次下载文件超时[{}]",fileRequest.getFileName());
                response = Response.status(Response.Status.BAD_REQUEST);
                needClose = true;
                return response.build();
            }
            StreamingOutput output = new FileStreamingOutput(s3Object);
            response = Response.ok(output);
            response.header("file-meta-data", JsonHelper.toJson(remoteMeta));
            response.header("Content-Disposition", "attachment;filename=" + URLDecoder.decode(fileRequest.getFileName(), "UTF-8"));
            response.type(s3Object.getObjectMetadata().getContentType());
            return response.build();
        } catch (Exception e) {
            log.error("下载文件报错fileRequest[{}]", JsonHelper.toJson(fileRequest),e);
            Profiler.functionError(info);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return response.build();
        } finally {
        	if(needClose && s3Object != null) {
        		try {
        			s3Object.close();
        			log.info("s3Object.close-suc");
        		} catch (Exception e) {
        			log.error("s3Object.close-error",e);
        		}
        	}
            Profiler.registerInfoEnd(info);
        }
    }
    @POST
    @Path("/listFiles")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public InvokeResult<List<String>> listFiles(FileRequest fileRequest) {
        InvokeResult<List<String>> invokeResult = new InvokeResult<>();
        log.info("查看文件列表-参数校验不通过fileRequest[{}]", JsonHelper.toJson(fileRequest));
        if(StringUtils.isEmpty(fileRequest.getFolder()) || StringUtils.isEmpty(fileRequest.getFileName())){
            log.warn("查看文件列表报错-参数校验不通过fileRequest[{}]",  JsonHelper.toJson(fileRequest));
            invokeResult.error("查看文件列表报错-参数校验不通过folder、sourceSysName不能为空");
            return invokeResult;
        }
        invokeResult.success();
        List<String> listFiles = labelprintAmazonS3ClientWrapper.listObjects(fileRequest.getFolder(),fileRequest.getFileNamePrefix(),1000,null);
        invokeResult.setData(listFiles);
        return invokeResult;
    }

    @POST
    @Path("/deleteFile")
    @JProfiler(jKey = "DMS.WEB.FileResource.deleteFile", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public InvokeResult<Boolean> deleteFile(FileRequest fileRequest){
        log.info("删除文件-fileRequest[{}]", JsonHelper.toJson(fileRequest));
        InvokeResult<Boolean> result = this.checkParams(fileRequest);
        if(!result.codeSuccess()) {
        	return result;
        }
        if(StringUtils.isEmpty(fileRequest.getFolder()) || StringUtils.isEmpty(fileRequest.getFileName()) || StringUtils.isEmpty(fileRequest.getFileName())){
            log.warn("删除文件报错-参数校验不通过fileRequest[{}]", JsonHelper.toJson(fileRequest));
            result.error("删除文件报错-参数校验不通过fileName、folder、sourceSysName不能为空");
            return result;
        }
        if(StringUtils.isEmpty(fileRequest.getSecretKey())){
            log.error("删除文件-secretKey不能为空");
            result.error("删除文件secretKey不能为空："+FORM_FOLDER_ELEMENT_NAME);
            return result;
        }
        if(!Objects.equals(fileRequest.getSecretKey(),fileModifySecretKey)){
            log.error("删除文件-secretKey错误");
            result.error("删除文件secretKey错误："+fileRequest.getSecretKey());
            return result;
        }

        result.success();
        labelprintAmazonS3ClientWrapper.deleteObject(fileRequest.getFolder(),fileRequest.getFileName());
        return result;
    }

    private static String getElementValue(Map<String, List<InputPart>> uploadForm,String elementName) throws IOException{
        List<InputPart> inputPartList = uploadForm.get(elementName);
        if(CollectionUtils.isEmpty(inputPartList)){
            return null;
        }
        String value = inputPartList.get(0).getBodyAsString().trim().replace("\"", "");
        return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
    }
    /**
     * 密码生成
     * @param fileRequest
     * @return
     */
    @POST
    @Path("/getSecretKey")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public InvokeResult<String> getSecretKey(FileRequest fileRequest) {
        InvokeResult<String> result = new InvokeResult<>();
        if(fileRequest == null) {
        	result.error("请求参数不能为空！");
        	return result;
        }
        if(StringUtils.isBlank(fileRequest.getSourceSysName())) {
        	result.error("sourceSysName参数不能为空！");
        	return result;
        }
        if(!Objects.equals(fileRequest.getSecretKey(),fileModifySecretKey)) {
        	result.error("secretKey参数有误！");
        	return result;
        }
        result.success();
        result.setData(generateSecretKey(fileRequest));
        return result;
    }
    private InvokeResult<Boolean> checkParams(FileRequest fileRequest) {
    	InvokeResult<Boolean> result = new InvokeResult<>();
        if(fileRequest == null) {
        	result.error("请求参数不能为空！");
        	return result;
        }
        if(StringUtils.isBlank(fileRequest.getSourceSysName())) {
        	result.error("sourceSysName参数不能为空！");
        	return result;
        }
        if(!Objects.equals(fileRequest.getSecretKey(),generateSecretKey(fileRequest))) {
        	result.error("secretKey参数有误！");
        	return result;
        }
        result.success();
        return result;
    }
    private String generateSecretKey(FileRequest fileRequest) {
    	return Md5Helper.getMd5(fileRequest.getSourceSysName()+fileModifySecretKey);
    }
    public static class FileStreamingOutput implements StreamingOutput{
    	private S3Object s3Object;
    	
    	public FileStreamingOutput(S3Object s3Object){
    		this.s3Object = s3Object;
    	}
        @Override
        public void write(OutputStream oos) {
        	if(s3Object == null) {
        		return;
        	}
            int c = 0;
            byte[] buf = new byte[1024];
            try {
				while ((c = s3Object.getObjectContent().read(buf, 0, buf.length)) > 0) {
				    oos.write(buf, 0, c);
				    oos.flush();
				}
			} catch (IOException e) {
				log.error("s3Object.write-error",e);
			}finally {
	        	if(s3Object != null) {
	        		try {
	        			s3Object.close();
	        			log.info("s3Object.close-suc");
	        		} catch (Exception e) {
	        			log.error("s3Object.close-error",e);
	        		}
	        	}
			}
        }
    	
    }
}
