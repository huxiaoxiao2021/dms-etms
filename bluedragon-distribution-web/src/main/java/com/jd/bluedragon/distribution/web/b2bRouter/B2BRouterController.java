package com.jd.bluedragon.distribution.web.b2bRouter;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.B2BRouterRequest;
import com.jd.bluedragon.distribution.api.response.B2BRouterResponse;
import com.jd.bluedragon.distribution.api.response.CrossBoxResponse;
import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouter;
import com.jd.bluedragon.distribution.b2bRouter.service.B2BRouterService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;


/**
 * Created by xumei3 on 2018/2/24.
 */
@Controller
@RequestMapping("/b2bRouter")
public class B2BRouterController {
    private static final Logger logger = Logger.getLogger(B2BRouterController.class);

    private static final Integer TRANSFER_SITE_MAX = 7;

    @Autowired
    B2BRouterService b2bRouterService;

    /**
     * 跳转到主界面
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        return "b2bRouter/list";
    }

    @RequestMapping("/toAdd")
    public String toAdd(Model model) {
        return "b2bRouter/add";
    }

    /**
     * 校验参数是否正确
     * @param router
     * @return
     */
    @ResponseBody
    @RequestMapping("/check")
    public B2BRouterResponse<String> check(B2BRouter router) {
        B2BRouterResponse<String> b2bRouterResponse = new B2BRouterResponse<String>();
        try {
            if (router != null) {
                //始发网点和目的网点不为空校验
                if(StringHelper.isEmpty(router.getOriginalSiteName()) || StringHelper.isEmpty(router.getDestinationSiteName())){
                    b2bRouterResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
                    b2bRouterResponse.setMessage("始发网点、目的网点不能为空！");
                    return b2bRouterResponse;
                }
                //网点名称校验
                if(StringHelper.isEmpty(router.getSiteIdFullLine()) || StringHelper.isEmpty(router.getSiteNameFullLine())
                        || router.getSiteIdFullLine().trim().equals("-") || router.getSiteNameFullLine().trim().equals("-")){
                    b2bRouterResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
                    b2bRouterResponse.setMessage("B网路由插入失败，请清除缓存，然后重试！");
                    return b2bRouterResponse;
                }

                String [] siteCodes = router.getSiteIdFullLine().split("-");
                String [] siteNames = router.getSiteNameFullLine().split("-");

                if(siteCodes.length != siteNames.length){
                    b2bRouterResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
                    b2bRouterResponse.setMessage("B网路由插入失败，请清除缓存，然后重试！");
                    return b2bRouterResponse;
                }

                for(int i= 0;i<siteCodes.length;i++){
                    String name = siteNames[i];
                    Integer id = Integer.parseInt(siteCodes[i]);
                    if (!b2bRouterService.B2BSiteNameVertify(id,name)) {
                        b2bRouterResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
                        b2bRouterResponse.setMessage("分拣中心[" + name + "]不存在");
                        return b2bRouterResponse;
                    }
                }
                // 校验该路线是否已经存在
                if (b2bRouterService.isHasRouter(router)) {
                    b2bRouterResponse.setCode(B2BRouterResponse.CODE_WARN);
                    b2bRouterResponse.setMessage("已存在" + router.getSiteNameFullLine() + "的路由，是否更新?");
                    return b2bRouterResponse;
                }
            }
            b2bRouterResponse.setCode(B2BRouterResponse.CODE_NORMAL);
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
    @ResponseBody
    @RequestMapping("/doAdd")
    public B2BRouterResponse<String> doAdd(B2BRouter router, Model model) {
        B2BRouterResponse<String> b2bRouterResponse = new B2BRouterResponse<String>();
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
                b2bRouterResponse.setCode(B2BRouterResponse.CODE_NORMAL);
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
    @RequestMapping("/query")
    @ResponseBody
    public B2BRouterResponse<Pager<List<B2BRouter>>> query(B2BRouterRequest b2bRouterRequest, Pager<List<B2BRouter>> pager) {
        B2BRouterResponse<Pager<List<B2BRouter>>> b2bRouterResponse = new B2BRouterResponse<Pager<List<B2BRouter>>>();
        try {
            List<B2BRouter> resultList = b2bRouterService.queryByCondition(b2bRouterRequest, pager);

            // 设置分页对象
            if (pager == null) {
                pager = new Pager<List<B2BRouter>>(Pager.DEFAULT_PAGE_NO);
            }

            pager.setData(resultList);
            b2bRouterResponse.setData(pager);
            b2bRouterResponse.setCode(B2BRouterResponse.CODE_NORMAL);

        } catch (Exception e) {
            b2bRouterResponse.setCode(B2BRouterResponse.CODE_EXCEPTION);
            b2bRouterResponse.setData(null);
            b2bRouterResponse.setMessage("根据查询条件获取路由信息失败："+e.getMessage());
        }
        return b2bRouterResponse;
    }

    @RequestMapping("/toEdit")
    public String toEdit(B2BRouterRequest b2bRouterRequest, Model model) {
//        HashMap queryInfo = new HashMap();/** 组装查询条件 **/
//        try{
//            queryInfo.put("originateOrg",b2bRouterRequest.getOriginateOrg());
//            queryInfo.put("originateOrgName", URLDecoder.decode(b2bRouterRequest.getOriginateOrgName(),"UTF-8"));
//            queryInfo.put("originalDmsName", URLDecoder.decode(b2bRouterRequest.getOriginalDmsName(),"UTF-8"));
//            queryInfo.put("updateOperatorName",URLDecoder.decode(b2bRouterRequest.getUpdateOperatorName(),"UTF-8"));
//            queryInfo.put("destinationOrg",b2bRouterRequest.getDestinationOrg());
//            queryInfo.put("destinationOrgName",URLDecoder.decode(b2bRouterRequest.getDestinationOrgName(),"UTF-8"));
//            queryInfo.put("destinationDmsName",URLDecoder.decode(b2bRouterRequest.getDestinationDmsName(),"UTF-8"));
//            queryInfo.put("startDate",b2bRouterRequest.getStartDate());
//            queryInfo.put("endDate",b2bRouterRequest.getEndDate());
//            queryInfo.put("transferOrg",b2bRouterRequest.getTransferOrg());
//            queryInfo.put("transferOrgName",URLDecoder.decode(b2bRouterRequest.getTransferOrgName(),"UTF-8"));
//            queryInfo.put("transferName",URLDecoder.decode(b2bRouterRequest.getTransferName(),"UTF-8"));
//            queryInfo.put("yn",b2bRouterRequest.getYn());
//        }catch(UnsupportedEncodingException e){
//            logger.error("对查询条件中的汉字解码失败：",e);
//        }
        try {
            B2BRouter router = b2bRouterService.getRouterById(b2bRouterRequest.getId());
            if(router == null){
                return "";
            }else{
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

                    if(length > TRANSFER_SITE_MAX -5){
                        Integer siteCode= Integer.parseInt(transferSiteCodes[1]);
                        model.addAttribute("transOneSiteCode", siteCode);
                        model.addAttribute("transOneSiteName", b2bRouterService.getB2BSiteNameByCode(siteCode));
                    }
                    if(length >TRANSFER_SITE_MAX - 4){
                        Integer siteCode= Integer.parseInt(transferSiteCodes[2]);
                        model.addAttribute("transTwoSiteCode", siteCode);
                        model.addAttribute("transTwoSiteName", b2bRouterService.getB2BSiteNameByCode(siteCode));
                    }
                    if(length >TRANSFER_SITE_MAX - 3){
                        Integer siteCode= Integer.parseInt(transferSiteCodes[3]);
                        model.addAttribute("transThreeSiteCode", siteCode);
                        model.addAttribute("transThreeSiteName", b2bRouterService.getB2BSiteNameByCode(siteCode));
                    }
                    if(length >TRANSFER_SITE_MAX - 2){
                        Integer siteCode= Integer.parseInt(transferSiteCodes[4]);
                        model.addAttribute("transFourSiteCode", siteCode);
                        model.addAttribute("transFourSiteName", b2bRouterService.getB2BSiteNameByCode(siteCode));
                    }
                    if(length >TRANSFER_SITE_MAX -1){
                        Integer siteCode= Integer.parseInt(transferSiteCodes[5]);
                        model.addAttribute("transFiveSiteName", siteCode);
                        model.addAttribute("transFiveSiteCode", b2bRouterService.getB2BSiteNameByCode(siteCode));
                    }
                }
            }
//
//            model.addAttribute("queryInfo",queryInfo);/** 渲染出查询参数 **/
        } catch (Exception e) {
            logger.error("进入B网路由配置修改页面异常：", e);
        }
        return "b2bRouter/add";
    }

    @ResponseBody
    @RequestMapping("/doUpdate")
    public B2BRouterResponse<String> doUpdate(B2BRouter router, Model model) {
        B2BRouterResponse<String> b2bRouterResponse = new B2BRouterResponse<String>();
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

            b2bRouterService.updateRotuer(router);
            logger.info("用户帐号【" + userAccount + "】，姓名【" + userName + "】执行修改B网路由操作");

            b2bRouterResponse.setCode(CrossBoxResponse.CODE_NORMAL);
        } catch (Exception e) {
            b2bRouterResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
            b2bRouterResponse.setMessage("添加时异常，请检查数据是否选择正确");
            logger.error("执行修改B网路由操作异常：", e);
        }
        return b2bRouterResponse;
    }

    @RequestMapping("/delete")
    public String delete(Integer id) {
        try {
            String userAccount = "demo";
            String userName = "demo";
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();

            if (erpUser != null) {
                userAccount = erpUser.getUserCode();
                userName = erpUser.getUserName();
            }
            if (id != null) {
                B2BRouter router = new B2BRouter();
                router.setId(id);
                router.setOperatorUserErp(userAccount);
                router.setOperatorUserName(userName);
                router.setUpdateTime(new Date());
                router.setYn(0);

                b2bRouterService.deleteById(router);
            }
        } catch (Exception e) {
            logger.error("删除时异常：", e);
        }
        return "redirect:index";
    }

}
