package com.jd.bluedragon.utils;

import com.jd.bluedragon.utils.security.DesSecurityComponent;
import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class HttpParser {
	private final static Logger logger = Logger.getLogger(HttpParser.class);
	private DesSecurityComponent desSecurityComponent;
	
	/**
	 * 解压缩当前串
	 * @param request
	 * @return
	 */
	public String unGZip(HttpServletRequest request){
		String xml = "";
		try {
			String contentType = request.getContentType();
			int length = request.getContentLength();
			ServletInputStream inputStream;
			inputStream = request.getInputStream();
			byte[] buffer = new byte[length];
			DataInputStream dataStream = new DataInputStream(inputStream);
			dataStream.readFully(buffer);
			dataStream.close();
			if (contentType.equalsIgnoreCase("application/zip")) {
				buffer = ZipUtil.unGZip(buffer);
				xml = new String(buffer, "UTF-8");
			} else {
				xml = new String(buffer, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException异常：", e);
		} catch (IOException e) {
			logger.error("unGZip异常：", e);
		}
		return xml;
	}
	
	/**
	 * 压缩
	 * @param result
	 * @param response
	 * @return
	 */
	public byte[] gZip(String result, HttpServletResponse response){
		byte[] bytes = null;
		try {
			bytes = result.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException异常：", e);
		}
		bytes = ZipUtil.gZip(bytes);
		try {
			response.setContentLength(bytes.length);
			FileCopyUtils.copy(bytes, response.getOutputStream());
		} catch (IOException e) {
			logger.error("gZip异常：", e);
		}
		return bytes;
	}
	
	/**
	 * 有解密，解压的解析方法
	 * @param request
	 * @return
	 */
	public String parse(HttpServletRequest request) {
		String xml = "";
		try {
			String contentType = request.getContentType();
			int length = request.getContentLength();
			ServletInputStream inputStream;
			inputStream = request.getInputStream();
			byte[] buffer = new byte[length];
			DataInputStream dataStream = new DataInputStream(inputStream);
			dataStream.readFully(buffer);
			dataStream.close();

			if (contentType.equalsIgnoreCase("application/zip")) {
				buffer = this.desSecurityComponent.decryptBytes(buffer);
				buffer = ZipUtil.unGZip(buffer);
				xml = new String(buffer, "UTF-8");
			} else {
				xml = new String(buffer, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("parse出现异常：", e);
		} catch (IOException e) {
			logger.error("parseIO异常：", e);
		}
		return xml;
	}
	
	/**
	 * 加密，压缩的解析方法
	 * @param result
	 * @param response
	 * @return
	 */
	public byte[] parse(String result, HttpServletResponse response){
		byte[] bytes = null;
		try {
			bytes = result.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("parse 出现异常：", e);
		}
		bytes = ZipUtil.gZip(bytes);		
		bytes = this.desSecurityComponent.encryptBytes(bytes);
		try {
			response.setContentLength(bytes.length);
			FileCopyUtils.copy(bytes, response.getOutputStream());
		} catch (IOException e) {
			logger.error("parse IO 异常：", e);
		}
		return bytes;
	}
	
	/**
	 * 通过默认的密钥进行解密，解压缩
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public String parseWithDefaultKey(HttpServletRequest request)
			throws IOException, UnsupportedEncodingException {
		String xml = "";
		String contentType = request.getContentType();
		int length = request.getContentLength();
		ServletInputStream inputStream = request.getInputStream();
		byte[] buffer = new byte[length];
		DataInputStream dataStream = new DataInputStream(inputStream);
		dataStream.readFully(buffer);
		dataStream.close();

		if (contentType.equalsIgnoreCase("application/zip")) {
			buffer = this.desSecurityComponent.decryptBytesWithDefaultKey(buffer);
			buffer = ZipUtil.unGZip(buffer);
			xml = new String(buffer, "UTF-8");
		} else {
			xml = new String(buffer, "UTF-8");
		}
		return xml;
	}
	
	/**
	 * 通过默认密钥进行加密，压缩。
	 * @param response
	 * @param resultXml
	 * @return
	 */
	public byte[] parseWithDefaultKey(HttpServletResponse response,
                                      String resultXml) {
		byte[] bytes=null;
		try {
			bytes = resultXml.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("parseWithDefaultKey异常：", e);
		}
		bytes = ZipUtil.gZip(bytes);
		bytes = this.desSecurityComponent.encryptBytesWithDefaultKey(bytes);
		try {
			response.setContentLength(bytes.length);
			FileCopyUtils.copy(bytes, response.getOutputStream());
		} catch (IOException e) {
			logger.error("parseWithDefaultKey异常：", e);
		}
		return bytes;
	}

	public DesSecurityComponent getDesSecurityComponent() {
		return desSecurityComponent;
	}

	public void setDesSecurityComponent(DesSecurityComponent desSecurityComponent) {
		this.desSecurityComponent = desSecurityComponent;
	}
	
}
