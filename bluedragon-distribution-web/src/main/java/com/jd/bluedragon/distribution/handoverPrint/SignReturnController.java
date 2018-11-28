package com.jd.bluedragon.distribution.handoverPrint;

import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.signAndReturn.SignReturnService;
import com.jd.bluedragon.distribution.signAndReturn.domain.SignReturnPrintM;
import com.jd.bluedragon.distribution.signReturn.SignReturnCondition;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @ClassName: SignReturnController
 * @Description: 签单返回合单交接单打印
 * @author: hujiping
 * @date: 2018/11/23 13:54
 */
@Controller
@RequestMapping("signReturn")
public class SignReturnController extends DmsBaseController {

    private static final Log logger = LogFactory.getLog(SignReturnController.class);

    @Autowired
    private SignReturnService signReturnService;


    /**
     * 返回主页面
     * @return
     */
    @RequestMapping(value = "/toIndex")
    public String toIndex(){
        return "/signReturn/signReturnPrint";
    }

    /**
     * 查询
     * @return
     */
    @RequestMapping(value = "/query")
    public @ResponseBody PagerResult<SignReturnPrintM> query(@RequestBody SignReturnCondition condition){
        /*PagerResult<SignReturnPrintM> result = new PagerResult<SignReturnPrintM>();
        List<SignReturnPrintM> list = new ArrayList<SignReturnPrintM>();
        SignReturnPrintM signReturn = new SignReturnPrintM();
        signReturn.setBusiId(1);
        signReturn.setBusiName("京东");
        signReturn.setMergeCount(new Random().nextInt(1000));
        signReturn.setMergedWaybillCode("VA123321123");
        signReturn.setOperateTime(new Date());
        signReturn.setOperateUser("张三");
        signReturn.setOrgId("1234");
        signReturn.setReturnCycle("每周一次");
        if(((new Random().nextInt(2)+1)/2 == 0)){
            list.add(signReturn);
        }
        result.setRows(list);
        result.setTotal(list.size());*/
        PagerResult<SignReturnPrintM> result = signReturnService.getListByWaybillCode(condition);
        return result;
    }

    /**
     * 查询明细
     * @return
     */
    @RequestMapping(value = "/listData")
    public @ResponseBody PagerResult<SignReturnPrintM> listData(@RequestBody SignReturnCondition condition){
        /*PagerResult<SignReturnPrintM> result = new PagerResult<SignReturnPrintM>();
        List<SignReturnPrintM> list = new ArrayList<SignReturnPrintM>();
        SignReturnPrintM signReturn = new SignReturnPrintM();
        signReturn.setWaybillCode("VA00042113141");
        signReturn.setDeliveredTime(new Date());
        list.add(signReturn);
        for(int i=0;i<new Random().nextInt(5);i++){
            list.add(signReturn);
        }
        result.setRows(list);
        result.setTotal(list.size());
        return result;*/
        PagerResult<SignReturnPrintM> result = signReturnService.getListByWaybillCode(condition);
        return result;
    }

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    public ModelAndView toExport(SignReturnCondition condition, HttpServletResponse response, Model model){

        this.logger.info("导出签单返回合单打印交接单");
        try{
            signReturnService.toExport(condition,response);
        }catch (Exception e){
            this.logger.error("导出失败!");
        }finally {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("errorMsg","导出失败!");
            return modelAndView;
        }
    }

    /**
     * 跳转打印页面
     * @return
     */
    @RequestMapping(value = "/toPrint")
    public String toPrint(SignReturnCondition condition,Model model){

        model.addAttribute("waybillCode","waybillCode");
        model.addAttribute("waybillCodeInMerged","waybillCodeInMerged");
        return "/signReturn/signReturnPrintInfo";
    }

    /**
     * 打印明细
     * */
    @ResponseBody
    @RequestMapping(value = "/printInfo")
    public String printInfo(@QueryParam("waybillCode")String waybillCode,
                            @QueryParam("waybillCodeInMerged")String waybillCodeInMerged,Model model){

        SignReturnPrintM signReturn = new SignReturnPrintM();
        signReturn.setBusiId(1);
        signReturn.setBusiName("京东");
        signReturn.setMergeCount(new Random().nextInt(1000));
        signReturn.setMergedWaybillCode("VA123321123");
        signReturn.setOperateTime(new Date());
        signReturn.setOperateUser("张三");
        signReturn.setOrgId("1234");
        signReturn.setReturnCycle("每周一次");
        Map<String,Date> map = new HashMap<String, Date>();
        map.put("VA66669769281",new Date());
        map.put("VA66669769282",new Date());
        map.put("VA66669769283",new Date());
        map.put("VA66669769284",new Date());
        signReturn.setMap(map);
        String json = JsonHelper.toJson(signReturn);
        return json;
    }

}
