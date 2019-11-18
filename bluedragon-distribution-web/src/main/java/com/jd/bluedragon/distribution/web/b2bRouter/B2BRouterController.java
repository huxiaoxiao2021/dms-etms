package com.jd.bluedragon.distribution.web.b2bRouter;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.B2BRouterRequest;
import com.jd.bluedragon.distribution.api.response.B2BRouterResponse;
import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouter;
import com.jd.bluedragon.distribution.b2bRouter.service.B2BRouterService;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.uim.annotation.Authorization;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Date;
import java.util.List;


/**
 * Created by xumei3 on 2018/2/24.
 */
@Controller
@RequestMapping("/b2bRouter")
public class B2BRouterController {
    private static final Logger logger = Logger.getLogger(B2BRouterController.class);

    /**
     * 路由线路中网点最多个数
     */
    private static final Integer SITE_NUM_MAX = 7;

    @Autowired
    B2BRouterService b2bRouterService;

    /**
     * 跳转到主界面
     * @return
     */
    @Authorization(Constants.DMS_WEB_EXPRESS_B2BROUTER_R)
    @RequestMapping("/index")
    public String index() {
        return "b2bRouter/list";
    }

    @Authorization(Constants.DMS_WEB_EXPRESS_B2BROUTER_R)
    @RequestMapping("/toAdd")
    public String toAdd(Model model) {
        return "b2bRouter/add";
    }

    /**
     * 校验参数是否正确
     * @param router
     * @return
     */
    @Authorization(Constants.DMS_WEB_EXPRESS_B2BROUTER_R)
    @ResponseBody
    @RequestMapping("/check")
    public B2BRouterResponse<Integer> check(B2BRouter router) {
        B2BRouterResponse<Integer> b2bRouterResponse = new B2BRouterResponse<Integer>();
        b2bRouterResponse.setCode(B2BRouterResponse.CODE_NORMAL);
        try {
            if (router != null) {
                String errString = b2bRouterService.verifyRouterAddParam(router);
                if(StringHelper.isNotEmpty(errString)){
                    b2bRouterResponse.setCode(B2BRouterResponse.CODE_EXCEPTION);
                    b2bRouterResponse.setMessage(errString);
                    return b2bRouterResponse;
                }

                // 校验该路线是否已经存在
                Integer id = b2bRouterService.isHasRouter(router);
                if ( id!= null) {
                    b2bRouterResponse.setData(id);
                    b2bRouterResponse.setCode(B2BRouterResponse.CODE_WARN);
                    b2bRouterResponse.setMessage("已存在" + router.getSiteNameFullLine() + "的路由，是否更新?");
                    return b2bRouterResponse;
                }
            }
        } catch (Exception e) {
            b2bRouterResponse.setCode(B2BRouterResponse.CODE_EXCEPTION);
            b2bRouterResponse.setMessage("检查是否存在异常，请检查数据是否选择正确");
            logger.error("执行B网路由配置检查是否存在操作异常：", e);
        }
        return b2bRouterResponse;
    }

    /**
     * 执行路由配置表的【新增】操作
     * @param router
     * @param model
     * @return
     */
    @Authorization(Constants.DMS_WEB_EXPRESS_B2BROUTER_R)
    @ResponseBody
    @RequestMapping("/doAdd")
    public B2BRouterResponse<String> doAdd(B2BRouter router, Model model) {
        if(router == null){
            throw new RuntimeException("新增B网路由，路由参数为空.");
        }
        B2BRouterResponse<String> b2bRouterResponse = new B2BRouterResponse<String>();
        b2bRouterResponse.setCode(B2BRouterResponse.CODE_NORMAL);
        try {
            //获取当前操作人信息
            String userAccount = "demo";
            String userName = "demo";
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();

            if (erpUser != null) {
                userAccount = erpUser.getUserCode();
                userName = erpUser.getUserName();
            }
            router.setYn(1);
            router.setOperatorUserErp(userAccount);
            router.setOperatorUserName(userName);

            //设置操作时间
            router.setCreateTime(new Date());
            router.setUpdateTime(new Date());

            if(b2bRouterService.addRouter(router)) {
                logger.info("用户帐号【" + userAccount + "】，姓名【" + userName + "】执行添加B网路由操作");
            }else{
                logger.error("执行B网路由添加操作失败");
                b2bRouterResponse.setCode(B2BRouterResponse.CODE_EXCEPTION);
                b2bRouterResponse.setMessage("执行B网路由添加操作失败");
            }
        } catch (Exception e) {
            b2bRouterResponse.setCode(B2BRouterResponse.CODE_EXCEPTION);
            b2bRouterResponse.setMessage("添加时异常，请检查数据是否选择正确");
            logger.error("执行B网路由添加操作异常：", e);
        }
        return b2bRouterResponse;
    }

    /**
     * 根据查询条件查找路由信息
     * @param b2bRouterRequest
     * @param pager
     * @return
     */
    @Authorization(Constants.DMS_WEB_EXPRESS_B2BROUTER_R)
    @RequestMapping("/query")
    @ResponseBody
    public B2BRouterResponse<Pager<List<B2BRouter>>> query(B2BRouterRequest b2bRouterRequest, Pager<List<B2BRouter>> pager) {
        B2BRouterResponse<Pager<List<B2BRouter>>> b2bRouterResponse = new B2BRouterResponse<Pager<List<B2BRouter>>>();
        try {
            if(StringHelper.isEmpty(b2bRouterRequest.getOriginalSiteName())){
                b2bRouterRequest.setOriginalSiteCode(null);
            }
            if(StringHelper.isEmpty(b2bRouterRequest.getDestinationSiteName())){
                b2bRouterRequest.setDestinationSiteCode(null);
            }

            List<B2BRouter> resultList = b2bRouterService.queryByCondition(b2bRouterRequest, pager);

            // 设置分页对象
            if (pager == null) {
                pager = new Pager<List<B2BRouter>>(Pager.DEFAULT_PAGE_NO);
            }

            pager.setData(resultList);
            b2bRouterResponse.setData(pager);
            b2bRouterResponse.setCode(B2BRouterResponse.CODE_NORMAL);

        } catch (Exception e) {
            logger.error("根据查询条件获取路由信息失败.",e);
            b2bRouterResponse.setCode(B2BRouterResponse.CODE_EXCEPTION);
            b2bRouterResponse.setData(null);
            b2bRouterResponse.setMessage("获取信息失败："+e.getMessage());
        }
        return b2bRouterResponse;
    }

    @Authorization(Constants.DMS_WEB_EXPRESS_B2BROUTER_R)
    @RequestMapping("/toEdit")
    public String toEdit(B2BRouterRequest b2bRouterRequest,Integer id, Model model) {
        try {
            B2BRouter router = b2bRouterService.getRouterById(b2bRouterRequest.getId());
            if(router != null){
                String siteCodeFullLine = router.getSiteIdFullLine();
                String [] transferSiteCodes = null;
                if(siteCodeFullLine != null){
                    transferSiteCodes = siteCodeFullLine.split("-");

                    int length = transferSiteCodes.length;

                    model.addAttribute("id",router.getId());
                    model.addAttribute("originalSiteCode", router.getOriginalSiteCode());
                    model.addAttribute("originalSiteName", b2bRouterService.getB2BSiteNameByCode(router.getOriginalSiteCode()));

                    model.addAttribute("destinationSiteType", router.getDestinationSiteType());
                    model.addAttribute("destinationSiteCode", router.getDestinationSiteCode());
                    model.addAttribute("destinationSiteName", b2bRouterService.getB2BSiteNameByCode(router.getDestinationSiteCode()));

                    if(length > SITE_NUM_MAX -5){
                        Integer siteCode= Integer.parseInt(transferSiteCodes[1]);
                        model.addAttribute("transferOneSiteCode", siteCode);
                        model.addAttribute("transferOneSiteName", b2bRouterService.getB2BSiteNameByCode(siteCode));
                    }
                    if(length >SITE_NUM_MAX - 4){
                        Integer siteCode= Integer.parseInt(transferSiteCodes[2]);
                        model.addAttribute("transferTwoSiteCode", siteCode);
                        model.addAttribute("transferTwoSiteName", b2bRouterService.getB2BSiteNameByCode(siteCode));
                    }
                    if(length >SITE_NUM_MAX - 3){
                        Integer siteCode= Integer.parseInt(transferSiteCodes[3]);
                        model.addAttribute("transferThreeSiteCode", siteCode);
                        model.addAttribute("transferThreeSiteName", b2bRouterService.getB2BSiteNameByCode(siteCode));
                    }
                    if(length >SITE_NUM_MAX - 2){
                        Integer siteCode= Integer.parseInt(transferSiteCodes[4]);
                        model.addAttribute("transferFourSiteCode", siteCode);
                        model.addAttribute("transferFourSiteName", b2bRouterService.getB2BSiteNameByCode(siteCode));
                    }
                    if(length >SITE_NUM_MAX -1){
                        Integer siteCode= Integer.parseInt(transferSiteCodes[5]);
                        model.addAttribute("transferFiveSiteName", siteCode);
                        model.addAttribute("transferFiveSiteCode", b2bRouterService.getB2BSiteNameByCode(siteCode));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("进入B网路由配置修改页面异常：", e);
        }
        return "b2bRouter/add";
    }

    @Authorization(Constants.DMS_WEB_EXPRESS_B2BROUTER_R)
    @ResponseBody
    @RequestMapping("/doUpdate")
    public B2BRouterResponse<String> doUpdate(B2BRouter router, Model model) {
        B2BRouterResponse<String> b2bRouterResponse = new B2BRouterResponse<String>();
        b2bRouterResponse.setCode(B2BRouterResponse.CODE_NORMAL);
        try {
            String userAccount = "demo";
            String userName = "demo";
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                userAccount = erpUser.getUserCode();
                userName = erpUser.getUserName();
            }

            //更新操作人信息和操作时间信息
            router.setOperatorUserErp(userAccount);
            router.setOperatorUserName(userName);
            router.setUpdateTime(new Date());

            //更新表数据
            b2bRouterService.updateRouter(router);
            logger.info("用户帐号【" + userAccount + "】，姓名【" + userName + "】执行修改B网路由操作");
        } catch (Exception e) {
            b2bRouterResponse.setCode(B2BRouterResponse.CODE_EXCEPTION);
            b2bRouterResponse.setMessage("添加时异常，请检查数据是否选择正确");
            logger.error("执行修改B网路由操作异常：", e);
        }
        return b2bRouterResponse;
    }

    @Authorization(Constants.DMS_WEB_EXPRESS_B2BROUTER_R)
    @ResponseBody
    @RequestMapping("/delete")
    public B2BRouterResponse<String>  delete(@RequestBody List<Integer> idList) {
        B2BRouterResponse<String> b2bRouterResponse = new B2BRouterResponse<String>();
        b2bRouterResponse.setCode(B2BRouterResponse.CODE_NORMAL);
        try {
            String userAccount = "demo";
            String userName = "demo";
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();

            if (erpUser != null) {
                userAccount = erpUser.getUserCode();
                userName = erpUser.getUserName();
            }
            if (idList != null && idList.size() >0) {
                for(Integer id : idList) {
                    B2BRouter router = new B2BRouter();
                    router.setId(id);
                    router.setOperatorUserErp(userAccount);
                    router.setOperatorUserName(userName);
                    router.setUpdateTime(new Date());
                    router.setYn(0);

                    b2bRouterService.deleteById(router);
                }
            }
        } catch (Exception e) {
            logger.error("删除时异常：", e);
            b2bRouterResponse.setCode(B2BRouterResponse.CODE_EXCEPTION);
            b2bRouterResponse.setMessage("删除时异常，请检查数据是否选择正确");
            logger.error("执行删除B网路由操作异常：", e);
        }
        return b2bRouterResponse;
    }

    @Authorization(Constants.DMS_WEB_EXPRESS_B2BROUTER_R)
    @RequestMapping("/toImport")
    public String toImport(Model model) {
        return "b2bRouter/import_data";
    }

    @Authorization(Constants.DMS_WEB_EXPRESS_B2BROUTER_R)
    @RequestMapping(value = "/uploadExcel", method = RequestMethod.POST)
    public String uploadExcel(Model model, MultipartHttpServletRequest request) {
        logger.debug("uploadExcelFile begin...");
        try {
            String userAccount = "demo";
            String userName = "demo";
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();

            if (erpUser != null) {
                userAccount = erpUser.getUserCode();
                userName = erpUser.getUserName();
            }

            MultipartFile file = request.getFile("pitchExcel");
            String fileName = file.getOriginalFilename();

            String errorString = null;
            int type = 0;
            if (fileName.endsWith("xls") || fileName.endsWith("XLS")) {
                type = 1;
            } else {
                type = 2;
            }

            //解析excel表格中的信息
            DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(type);
            List<B2BRouter> dataList = null;
            try {
                dataList = dataResolver.resolver(file.getInputStream(), B2BRouter.class, new PropertiesMetaDataFactory("/excel/b2bRouter.properties"));
                if (dataList != null && dataList.size() > 0) {
                    if (dataList.size() > 1000) {
                        errorString = "导入数据超出1000条";
                        model.addAttribute("excelFile", errorString);
                        return "b2bRouter/import_data";
                    }

                    for (B2BRouter router : dataList) {
                        router.setOriginalSiteType(1);
                        router.setOperatorUserErp(userAccount);
                        router.setOperatorUserName(userName);
                        router.setCreateTime(new Date());
                        router.setUpdateTime(new Date());
                        router.setYn(1);
                        errorString = b2bRouterService.verifyRouterImportParam(router);

                        if (StringHelper.isNotEmpty(errorString)) {
                            model.addAttribute("excelFile", errorString);
                            return "b2bRouter/import_data";
                        }
                    }

                    errorString = b2bRouterService.handleRouterBatch(dataList);
                    if (errorString == null || errorString.equals("")) {
                        model.addAttribute("excelFile", "导入成功！");
                    } else {
                        model.addAttribute("excelFile", errorString);
                        return "b2bRouter/import_data";
                    }
                } else {
                    errorString = "导入数据过多或者异常，请检查excel数据";
                    model.addAttribute("excelFile", errorString);
                    return "b2bRouter/import_data";
                }

            } catch (Exception e) {
                if (e instanceof IllegalArgumentException) {
                    errorString = e.getMessage();
                } else {
                    logger.error("导入异常信息：", e);
                    errorString = "导入出现异常";
                }
                model.addAttribute("excelFile", errorString);
                return "b2bRouter/import_data";
            }
        } catch (Exception e) {
            logger.error("执行uploadExcelFile异常" + e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
        return "b2bRouter/import_data";
    }
}
