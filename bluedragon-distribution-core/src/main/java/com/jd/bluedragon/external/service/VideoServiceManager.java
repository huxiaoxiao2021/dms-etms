package com.jd.bluedragon.external.service;

import com.jd.bluedragon.common.dto.video.request.VideoUploadRequest;
import com.jd.bluedragon.common.dto.video.response.VideoUploadInfo;

import java.util.Map;

/**
 * 视频相关接口
 */
public interface VideoServiceManager {

	/**
	 * 获取视频上传信息
	 */
	VideoUploadInfo getVideoUploadUrl(Map<String,Object> params);

	/**
	 * 根据videoId删除视频
	 */
	void deleteVideo(Long videoId);

}
