package com.jd.bluedragon.external.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.video.response.VideoUploadInfo;
import com.jd.bluedragon.external.service.VideoServiceManager;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.vd.api.service.VODService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class VideoServiceManagerImpl implements VideoServiceManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String CODE = "code";

	private static final String MESS = "mess";

	private static final String DATA = "data";

	@Autowired
	private VODService vodService;


	@JProfiler(jKey = "DmsWeb.VideoServiceManagerImpl.getVideoUploadUrl",
			jAppName = Constants.UMP_APP_NAME_DMSWEB,
			mState = {JProEnum.TP,JProEnum.FunctionError})
	@Override
	public VideoUploadInfo getVideoUploadUrl(Map<String, Object> params) {
		try {
			String uploadInfo = vodService.getUploadUrlMap(params);
			logger.info("getVideoUploadUrl|获取视频上传信息:params={},result={}", JSONObject.toJSONString(params), uploadInfo);
			JSONObject jsonObject = JSONObject.parseObject(uploadInfo);
			Integer code = jsonObject.getInteger(CODE);
			if (Constants.NUMBER_ZERO.equals(code)) {
				JSONObject dataObject = jsonObject.getJSONObject(DATA);
                return dataObject.toJavaObject(VideoUploadInfo.class);
			}
			logger.warn("getVideoUploadUrl|获取视频上传信息失败:params={},result={}", JSONObject.toJSONString(params), uploadInfo);
		} catch (Exception e) {
			logger.error("getVideoUploadUrl|获取视频上传信息出现异常:params={},e=", JSONObject.toJSONString(params), e);
		}
		return null;
	}

	@JProfiler(jKey = "DmsWeb.getVideoUploadUrl.deleteVideo",
			jAppName = Constants.UMP_APP_NAME_DMSWEB,
			mState = {JProEnum.TP,JProEnum.FunctionError})
	@Override
	public void deleteVideo(Long videoId) {
		try {
			String result = vodService.deleteVideo(videoId);
			JSONObject jsonObject = JSONObject.parseObject(result);
			Integer code = jsonObject.getInteger(CODE);
			if (Constants.NUMBER_ZERO.equals(code)) {
				logger.info("deleteVideo|根据videoId={}删除视频成功", videoId);
			} else {
				String message = jsonObject.getString(MESS);
				logger.warn("deleteVideo|根据videoId={}删除视频失败:message={}", videoId, message);
			}
		} catch (Exception e) {
			logger.error("deleteVideo|根据videoId={}删除视频出现异常:e=", videoId, e);
		}
	}
}
