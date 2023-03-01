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
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

    private static String FORM_FILE_ELEMENT_NAME = "attachment";

    @POST
    @Path("/uploadfile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public InvokeResult<String> uploadfile(MultipartFormDataInput formDataInputs){
        log.info("上传文件开始");
        InvokeResult<String> invokeResult = new InvokeResult<>();
        Map<String, List<InputPart>> uploadForm = formDataInputs.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get(FORM_FILE_ELEMENT_NAME);
        if(CollectionUtils.isEmpty(inputParts)){
            invokeResult.error("上传文件内容为空表单name："+FORM_FILE_ELEMENT_NAME);
            return invokeResult;
        }
        for (InputPart inputPart : inputParts) {
            try {
                InputStream inputStream = inputPart.getBody(InputStream.class,null);
                String fileName = getFileNameByFileInputPart(inputPart);
                log.info("上传文件名称fileName[{}]",fileName);
                amazonS3ClientWrapper.putObjectWithFlow("",inputStream,fileName);
            } catch (IOException e) {
                log.error("上传文件报错",e);
            }
        }
        invokeResult.success();
        log.info("上传文件结束");
        return invokeResult;
    }


    @POST
    @Path("/downloadFile")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response downloadFile(FileRequest fileRequest) {
        log.info("下载文件-开始fileRequest[{}]", JsonHelper.toJson(fileRequest));
        Response.ResponseBuilder response = null;
        if(StringUtils.isEmpty(fileRequest.getBucketName()) || StringUtils.isEmpty(fileRequest.getFileName())){
            log.error("下载文件报错-参数校验不通过fileRequest[{}]", JsonHelper.toJson(fileRequest));
            response = Response.status(Response.Status.BAD_REQUEST);
            return response.build();
        }
        S3Object s3Object = amazonS3ClientWrapper.getObject(fileRequest.getBucketName(),fileRequest.getFileName());

        if(s3Object == null){
            log.error("下载文件报错-文件不存在fileName[{}]",fileRequest.getFileName());
            response = Response.status(Response.Status.NOT_FOUND);
            return response.build();

        }
        response = Response.ok(s3Object.getObjectContent());
        response.header("Content-Disposition", "attachment;filename=" + fileRequest.getFileName());
        return response.build();
    }

    @POST
    @Path("/listFiles")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public InvokeResult<List<String>> listFiles(FileRequest fileRequest) {
        InvokeResult<List<String>> invokeResult = new InvokeResult<>();
        invokeResult.success();
        List<String> listFiles = amazonS3ClientWrapper.listObjects("",fileRequest.getFileNamePrefix(),1000,null);
        invokeResult.setData(listFiles);
        return invokeResult;
    }

    @POST
    @Path("/deleteFile")
    @JProfiler(jKey = "DMS.WEB.StorageResource.putaway", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public InvokeResult<Boolean> deleteFile(FileRequest fileRequest){
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.success();
        amazonS3ClientWrapper.deleteObject("",fileRequest.getFileName());
        return result;
    }

    public static String getFileNameByFileInputPart(InputPart filePart) {
        String[] contentDispositionHeader = filePart.getHeaders().getFirst("Content-Disposition").split(";");
        for (String fileName : contentDispositionHeader) {
            if ((fileName.trim().startsWith("filename"))) {
                String[] tmp = fileName.split("=");
                String fileNameStr = tmp[1].trim().replace("\"", "");
                return fileNameStr;
            }
        }
        return null;
    }
}
