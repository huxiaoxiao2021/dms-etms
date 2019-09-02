package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;

/**
 * 
 * @ClassName: JdCloudPrintOutputConfig
 * @Description: 云打印输出配置
 * @author: wuyoude
 * @date: 2019年8月14日 下午4:25:00
 *
 */
public class JdCloudPrintOutputConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 输出方式配置可选的值，0(Printer，输出到打印机)，1(File，输出到本地文件)，2(OSS，输出到对象存储)
	 */
	private Integer type = 2;
	/**
	 * 文件格式，0(BMP)，3(GIF)，4(JPEG)，5(PNG)，7(PDF)
	 */
	private Integer format = 7;
	/**
	 * oss相对路径
	 */
	private String path;
	/**
	 * oss配置
	 */
	private JdCloudPrintOssConfig oss;
	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * @return the format
	 */
	public Integer getFormat() {
		return format;
	}
	/**
	 * @param format the format to set
	 */
	public void setFormat(Integer format) {
		this.format = format;
	}
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * @return the oss
	 */
	public JdCloudPrintOssConfig getOss() {
		return oss;
	}
	/**
	 * @param oss the oss to set
	 */
	public void setOss(JdCloudPrintOssConfig oss) {
		this.oss = oss;
	}
}
