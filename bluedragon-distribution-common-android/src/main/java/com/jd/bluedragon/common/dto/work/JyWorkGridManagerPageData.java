package com.jd.bluedragon.common.dto.work;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
/**
 * @ClassName: JyWorkGridManagerPageData
 * @Description: 任务管理-分页查询实体
 * @author wuyoude
 * @date 2023年05月30日 11:01:53
 *
 */
public class JyWorkGridManagerPageData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Map<Integer,JyWorkGridManagerCountData> statusCount;

	private Map<String,JyWorkGridManagerCountData> statusNum;
	
	private List<JyWorkGridManagerData> dataList;
	//客户端 列表刷新时间(秒)
	private Integer refreshSecond = 1000;

	public List<JyWorkGridManagerData> getDataList() {
		return dataList;
	}

	public void setDataList(List<JyWorkGridManagerData> dataList) {
		this.dataList = dataList;
	}

	public Map<Integer, JyWorkGridManagerCountData> getStatusCount() {
		return statusCount;
	}

	public void setStatusCount(Map<Integer, JyWorkGridManagerCountData> statusCount) {
		this.statusCount = statusCount;
	}

	public Integer getRefreshSecond() {
		return refreshSecond;
	}

	public void setRefreshSecond(Integer refreshSecond) {
		this.refreshSecond = refreshSecond;
	}

	public Map<String, JyWorkGridManagerCountData> getStatusNum() {
		return statusNum;
	}

	public void setStatusNum(Map<String, JyWorkGridManagerCountData> statusNum) {
		this.statusNum = statusNum;
	}
}
