package com.jd.bluedragon.distribution.receive.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckCondition;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckResult;
import com.jd.bluedragon.distribution.receive.service.ReceiveWeightCheckService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: ReceiveWeightCheckController
 * @Description: 揽收重量校验统计--Controller实现
 * @author: hujiping
 * @date: 2019/2/26 20:58
 */
@Controller
@RequestMapping("receive")
public class ReceiveWeightCheckController extends DmsBaseController {

    private static final Log logger = LogFactory.getLog(ReceiveWeightCheckController.class);

    private static final String CENTER_CONTEXT = "/services/upload/image";

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private ReceiveWeightCheckService receiveWeightCheckService;

    /**
     * 返回主页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_RECEIVEWEIGHTCHECK_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        String userCode = "";
        Long createSiteCode = new Long(-1);
        Integer orgId = new Integer(-1);
        if(erpUser != null){
            userCode = erpUser.getUserCode();
            BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
            if (bssod!=null && bssod.getSiteType() == 64) {
                createSiteCode = new Long(bssod.getSiteCode());
                orgId = bssod.getOrgId();
            }
        }
        model.addAttribute("orgId",orgId).addAttribute("createSiteCode",createSiteCode);
        return "/receive/receiveWeightCheck";
    }

    @Authorization(Constants.DMS_WEB_SORTING_RECEIVEWEIGHTCHECK_R)
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<ReceiveWeightCheckResult> listData(@RequestBody ReceiveWeightCheckCondition condition){

        PagerResult<ReceiveWeightCheckResult> result = receiveWeightCheckService.queryByCondition(condition);
        return result;
    }

    /**
     * 导出
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_RECEIVEWEIGHTCHECK_R)
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    public ModelAndView toExport(ReceiveWeightCheckCondition condition, Model model) {

        this.logger.info("导出揽收重量校验统计表");
        try{
            List<List<Object>> resultList = receiveWeightCheckService.getExportData(condition);
            model.addAttribute("filename", "揽收重量统计校验表.xls");
            model.addAttribute("sheetname", "揽收重量统计校验结果");
            model.addAttribute("contents", resultList);
            return new ModelAndView(new DefaultExcelView(), model.asMap());
        }catch (Exception e){
            this.logger.error("导出揽收重量统计校验表失败:" + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 跳转上传页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_RECEIVEWEIGHTCHECK_R)
    @RequestMapping("/toUpload")
    public String toUpload(@QueryParam("waybillCode")String waybillCode,
                           @QueryParam("packageCode")String packageCode,Model model) {
        model.addAttribute("waybillCode",waybillCode);
        model.addAttribute("packageCode",packageCode);

        return "receive/excessPictureUpload";
    }

    /**
     * 上传超标图片
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_RECEIVEWEIGHTCHECK_R)
    @RequestMapping(value = "/uploadExcessPicture", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult uploadExcessPicture(Model model,MultipartHttpServletRequest request) {

        InvokeResult result = new InvokeResult();
        MultipartFile fileField = request.getFile("fileField");
        MultipartFile[] images = new MultipartFile[]{fileField};
        String fileName = fileField.getOriginalFilename();

        /*ImageParams imageParams = new ImageParams();
        imageParams.setImage(images);
        imageParams.setOperateTime(new Long[]{new Date().getTime()});
        imageParams.setMachineCode("");
        imageParams.setSiteCode(-1);


        //提交参数设置
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        try {
            ByteArrayResource contentsAsResource = new ByteArrayResource(fileField.getBytes()){
                public String getFilename(){
                    return "fileName";
                }
            };
            map.add("image", contentsAsResource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.add("operateTime", new Long[]{new Date().getTime()});
        map.add("machineCode", "hjp");
        map.add("siteCode", -1);*/

        String realPath = request.getSession().getServletContext().getRealPath("/static/images/areaError.png");
        File file = new File(realPath);
        FileSystemResource resource = new FileSystemResource(file);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("image", new FileSystemResource[]{resource});
        param.add("operateTime",new Long[]{new Date().getTime()});
        param.add("machineCode","hjp");
        param.add("siteCode",-1);

        String url = PropertiesHelper.newInstance().getValue("DMSAUTOMATIC_ADDRESS") + CENTER_CONTEXT;

        RestTemplate restTemplate = new RestTemplate();
        try{

            result = restTemplate.postForObject(url, param, InvokeResult.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(result == null){
            return new InvokeResult();
        }
        return result;

    }

    /**
     * 上传超标图片
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_RECEIVEWEIGHTCHECK_R)
    @RequestMapping(value = "/uploadExcessPicture1", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult uploadExcessPicture1(@RequestParam("image") MultipartFile[] images,
                                             @RequestParam("operateTime") Long[] operateTimes,
                                             @RequestParam("machineCode") String machineCode,
                                             @RequestParam("siteCode") Integer siteCode,
                                             HttpServletResponse response,
                                             HttpServletRequest request) {

        InvokeResult result = new InvokeResult();

        return null;

    }


}
