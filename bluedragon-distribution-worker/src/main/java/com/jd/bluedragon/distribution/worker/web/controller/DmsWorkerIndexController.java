package com.jd.bluedragon.distribution.worker.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.asynbuffer.AsynBufferTaskManager;
import com.jd.bluedragon.distribution.command.JdResult;

/**
 * 
 * @ClassName: DmsWorkerIndexController
 * @Description: worker主页面
 * @author: wuyoude
 * @date: 2020年3月13日 上午11:23:34
 *
 */
@Controller
public class DmsWorkerIndexController extends DmsBaseWorkerController{
	@Autowired
	private AsynBufferTaskManager asynBufferTaskManager;
	
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String welcomePage() {
        return "index";
    }
    @RequestMapping(value = "/server/check", method = RequestMethod.GET)
    @ResponseBody
    public String checkServer() {
    	return checkResult().getCode().toString();
    }
    @RequestMapping(value = "/server/checkResult", method = RequestMethod.GET)
    @ResponseBody
    public JdResult<List<String>> checkResult() {
    	JdResult<List<String>> result = new JdResult<List<String>>();
    	result.toSuccess();
    	result.setData(new ArrayList<String>());
    	if(asynBufferTaskManager.isActive()){
    		result.getData().add("异步缓冲任务启动正常！");
    	}else{
    		result.getData().add("异步缓冲任务启动失败！");
    		result.toFail();
    	}
    	return result;
    }
}