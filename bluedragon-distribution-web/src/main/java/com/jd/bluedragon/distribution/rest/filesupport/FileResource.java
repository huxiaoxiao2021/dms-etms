package com.jd.bluedragon.distribution.rest.filesupport;

import com.amazonaws.services.s3.model.S3Object;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.exception.StorageException;
import com.jd.bluedragon.distribution.api.request.FileRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jss.oss.AmazonS3ClientWrapper;
import com.jd.bluedragon.distribution.rest.storage.StorageResource;
import com.jd.bluedragon.distribution.storage.domain.PutawayDTO;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.CharArrayBuffer;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @program: ql-dms-distribution
 * @description: 客户端文件上传下载服务
 * @author: xumigen
 * @create: 2023-02-28 19:35
 **/

@Component
@Path(Constants.REST_URL)
public class FileResource {
    private static Logger log = LoggerFactory.getLogger(FileResource.class);
    @Autowired
    private AmazonS3ClientWrapper amazonS3ClientWrapper;

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
        String fileName = null;
        String folder = null;
        try {
            Map<String, List<InputPart>> uploadForm = formDataInputs.getFormDataMap();
            folder = getElementValue(uploadForm,FORM_FOLDER_ELEMENT_NAME);
            fileName = getElementValue(uploadForm,FORM_FILENAME_ELEMENT_NAME);
            if(StringUtils.isEmpty(folder)){
                log.info("上传文件-文件夹folder内容为空");
                invokeResult.error("上传文件内容为空表单name："+FORM_FOLDER_ELEMENT_NAME);
                return invokeResult;
            }
            if(StringUtils.isEmpty(fileName)){
                log.info("上传文件-文件夹folder内容为空");
                invokeResult.error("上传文件内容为空表单name："+FORM_FOLDER_ELEMENT_NAME);
                return invokeResult;
            }
            List<InputPart> inputParts = uploadForm.get(FORM_FILE_ELEMENT_NAME);
            if(CollectionUtils.isEmpty(inputParts)){
                log.info("上传文件folder[{}]附件内容为空",folder);
                invokeResult.error("上传文件内容为空表单name："+FORM_FILE_ELEMENT_NAME);
                return invokeResult;
            }
            InputStream inputStream = inputParts.get(0).getBody(InputStream.class,null);
            MediaType mediaType = inputParts.get(0).getMediaType();
            amazonS3ClientWrapper.putObjectWithFlow(inputStream,mediaType.toString(),folder,fileName);
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
        CallerInfo info = Profiler.registerInfo("DMS.WEB.FileResource.downloadFile", Constants.UMP_APP_NAME_DMSWEB, false, true);
        Response.ResponseBuilder response = null;
        try {
            if(StringUtils.isEmpty(fileRequest.getFolder()) || StringUtils.isEmpty(fileRequest.getFileName())){
                log.error("下载文件报错-参数校验不通过fileRequest[{}]", JsonHelper.toJson(fileRequest));
                response = Response.status(Response.Status.BAD_REQUEST);
                return response.build();
            }
            S3Object s3Object = amazonS3ClientWrapper.getObject(fileRequest.getFolder(),fileRequest.getFileName());
            if(s3Object == null){
                log.error("下载文件报错-文件不存在fileName[{}]",fileRequest.getFileName());
                response = Response.status(Response.Status.NOT_FOUND);
                return response.build();

            }
            response = Response.ok(s3Object.getObjectContent());
            response.header("Content-Disposition", "attachment;filename=" + URLDecoder.decode(fileRequest.getFileName(), "UTF-8"));
            response.type(s3Object.getObjectMetadata().getContentType());
            return response.build();
        } catch (Exception e) {
            log.error("上传文件报错fileRequest[{}]", JsonHelper.toJson(fileRequest),e);
            Profiler.functionError(info);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return response.build();
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    @POST
    @Path("/listFiles")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public InvokeResult<List<String>> listFiles(FileRequest fileRequest) {
        InvokeResult<List<String>> invokeResult = new InvokeResult<>();
        if(StringUtils.isEmpty(fileRequest.getFolder())){
            log.error("下载文件报错-参数校验不通过fileRequest[{}]", JsonHelper.toJson(fileRequest));
            invokeResult.error("下载文件报错-参数校验不通过folder不能为空");
            return invokeResult;
        }
        invokeResult.success();
        List<String> listFiles = amazonS3ClientWrapper.listObjects(fileRequest.getFolder(),fileRequest.getFileNamePrefix(),1000,null);
        invokeResult.setData(listFiles);
        return invokeResult;
    }

    @POST
    @Path("/deleteFile")
    @JProfiler(jKey = "DMS.WEB.FileResource.deleteFile", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public InvokeResult<Boolean> deleteFile(FileRequest fileRequest){
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        if(StringUtils.isEmpty(fileRequest.getFolder()) || StringUtils.isEmpty(fileRequest.getFileName())){
            log.error("下载文件报错-参数校验不通过fileRequest[{}]", JsonHelper.toJson(fileRequest));
            result.error("下载文件报错-参数校验不通过fileName、folder不能为空");
            return result;
        }
        result.success();
        amazonS3ClientWrapper.deleteObject(fileRequest.getFolder(),fileRequest.getFileName());
        return result;
    }

    public static String getElementValue(Map<String, List<InputPart>> uploadForm,String elementName) throws IOException{
        List<InputPart> inputPartList = uploadForm.get(elementName);
        if(CollectionUtils.isEmpty(inputPartList)){
            return null;
        }
        String value = inputPartList.get(0).getBodyAsString().trim().replace("\"", "");
        return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
    }
}
