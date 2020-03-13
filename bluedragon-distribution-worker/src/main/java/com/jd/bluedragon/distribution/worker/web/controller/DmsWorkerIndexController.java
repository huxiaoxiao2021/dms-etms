package com.jd.bluedragon.distribution.worker.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String welcomePage() {
        return "index";
    }
}