package com.jd.bluedragon.core.print;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.amazonaws.services.s3.model.S3Object;
import com.jd.bluedragon.distribution.jss.oss.AmazonS3ClientWrapper;
import com.jd.ql.dms.print.engine.AbstractLabelFileLoader;
import com.jd.ql.dms.print.engine.LabelFileLoaderFactory;
import com.jd.ql.dms.print.engine.api.LabelFileLoader;

/**
 * 
 * @ClassName: DmsAmazonS3LabelFileLoader
 * @Description: 通过AmazonS3方式从分拣系统加载文件
 * @author: wuyoude
 * @date: 2018年7月19日 下午5:05:12
 *
 */
public class DmsAmazonS3LabelFileLoader extends AbstractLabelFileLoader implements LabelFileLoader{
	
    private static Logger logger = LoggerFactory.getLogger(DmsAmazonS3LabelFileLoader.class);
	
	public DmsAmazonS3LabelFileLoader(){
		LabelFileLoaderFactory.register("dmsAmazon", this);
	}
	
    @Autowired
    @Qualifier("labelprintAmazonS3ClientWrapper")
    private AmazonS3ClientWrapper labelprintAmazonS3ClientWrapper;	
	
	@Override
	public String loadRemoteTextFile(String filePath, String fileName) {
		String content = null;
        try {
    		if(logger.isDebugEnabled()) {
    			logger.debug("dmsAmazon-downloadFile：filePath="+filePath+",fileName="+fileName);
    		}
        	S3Object response = labelprintAmazonS3ClientWrapper.getObject(filePath,fileName);
            if (response != null) {
                // 获取响应实体
            	InputStreamReader ins = new InputStreamReader(response.getObjectContent());
            	StringBuffer sf = new StringBuffer();
            	int size = 0;
            	char[] buff = new char[128];
            	while((size = ins.read(buff))  > 0) {
            		sf.append(buff,0,size);
            	}
            	content = sf.toString();
            }else {
                logger.warn("加载远程文件失败！filePath="+filePath+",fileName="+fileName);
            }
        } catch (IOException e) {
            logger.error("加载远程文件异常！filePath="+filePath+",fileName="+fileName,e);
        }finally {
        }
		return content;		
	}
	@Override
	public BufferedImage loadRemoteImageFile(String filePath, String fileName) {
        BufferedImage image = null;
        try {
    		if(logger.isDebugEnabled()) {
    			logger.debug("dmsAmazon-downloadFile：filePath="+filePath+",fileName="+fileName);
    		}
        	S3Object response = labelprintAmazonS3ClientWrapper.getObject(filePath,fileName);
            if (response != null) {
                // 获取响应实体
                image = ImageIO.read(response.getObjectContent());
            }else {
                logger.warn("加载远程文件失败！filePath="+filePath+",fileName="+fileName);
            }
        } catch (IOException e) {
            logger.error("加载远程文件异常！filePath="+filePath+",fileName="+fileName,e);
        }finally {
        }
		return image;
	}
}
