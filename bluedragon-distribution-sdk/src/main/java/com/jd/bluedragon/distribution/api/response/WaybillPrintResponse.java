package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.print.domain.CloudPrintDocument;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;

import java.util.List;

/**
 * 
 * @ClassName: WaybillPrintResponse
 * @Description: 包裹标签打印请求返回实体
 * @author: wuyoude
 * @date: 2018年1月25日 下午4:44:43
 *
 */
public class WaybillPrintResponse extends PrintWaybill{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 使用云打印
	 */
	private boolean useCloudPrint;

	/**
	 * 云打印本地打印组件需要的完整的打印数据
	 */
	private List<CloudPrintDocument> cloudPrintDocuments;

	public boolean getUseCloudPrint() {
		return useCloudPrint;
	}

	public void setUseCloudPrint(boolean useCloudPrint) {
		this.useCloudPrint = useCloudPrint;
	}

	public List<CloudPrintDocument> getCloudPrintDocuments() {
		return cloudPrintDocuments;
	}

	public void setCloudPrintDocuments(List<CloudPrintDocument> cloudPrintDocuments) {
		this.cloudPrintDocuments = cloudPrintDocuments;
	}
}
