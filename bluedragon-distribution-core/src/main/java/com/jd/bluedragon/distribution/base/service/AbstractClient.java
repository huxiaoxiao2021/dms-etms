package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.BasePdaUserDto;
import com.jd.bluedragon.distribution.base.domain.PdaStaff;
import com.jd.bluedragon.distribution.base.service.impl.BaseServiceImpl;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: erp登录校验结果抽象处理逻辑
 * @author: lql
 * @date: 2020/8/17 13:20
 **/
public abstract class AbstractClient extends AbstractBaseUserService implements LoginClientService{

    private ErpValidateService erpValidateService;

    /** 日志 */
    private Logger log = LoggerFactory.getLogger(BaseServiceImpl.class);

    /**
     * 账号密码是否存在
     *
     * @param String
     *            erpcode erp账号
     * @param String
     *            password erp密码
     *
     * @param loginVersion 登录接口的版本号
     *
     * @return StaffDto 是否登录成功
     */
    @Override
    public PdaStaff login(String erpcode, String password, ClientInfo clientInfo, Byte loginVersion) {
        /** 调用基础信息接口查询PDA用户 */
        // 测试接口代码 baseMinorServiceProxy.getServerDate() 取服务器时间
        BasePdaUserDto pdadata = null;
        try {
            pdadata = userLogin(erpcode, password, clientInfo, loginVersion);
        } catch (Exception e) {
            log.error("调用baseMinorServiceProxy.pdaUserLogin接口出现异常！erpcode:{}",erpcode, e);
        }
        return getPdaStaff(pdadata);
    }

    private PdaStaff getPdaStaff(BasePdaUserDto pdadata) {
        /** 验证结果 */
        PdaStaff result = new PdaStaff();
        if (pdadata == null) {
            /** 返回空数据 */
            // 设置错误标示和错误信息
            result.setError(true);
            result.setErrormsg("基础信息服务异常");
            // 返回结果
            return result;
        }
        /** 错误标示 */
        Integer errorcode = pdadata.getErrorCode();
        /** 根据返回的错误标示进行处理 */
        switch (errorcode) {
            case 1110: //无可用验证方式(命中风险检查)
            case 1100: //需要验证(命中风险检查)
            case 105: //ip在黑名单中
            case 100://系统繁忙
            case 14://账号已经注销
            case 8://因安全原因账号被锁
            case 7://未验证的企业用户
            case 6://密码错误
            case 3: //密码错误超过十次pin被锁
            case 2://用户不存在
            case 0: //获取基础资料数据失败
            case -4: //用户名或密码为空
            case -3://获取基础账号JSF数据失败
            case -2://登录异常
            case -1:
                /** errorcode=-1表示验证失败 */
                // 设置错误标示和错误信息
                result.setError(true);
                result.setErrormsg(pdadata.getMessage());
                // 返回结果
                return result;
            case 1:
                /** errorcode=1表示验证成功，返回用户ID和名称，分拣中心ID和名称 */
                result.setError(false);
                // 用户ID
                result.setStaffId(pdadata.getStaffId());
                // 用户名称
                result.setStaffName(pdadata.getStaffName());
                // 分拣中心ID
                result.setSiteId(pdadata.getSiteId());
                // 分拣中心名称
                result.setSiteName(pdadata.getSiteName());
                // 机构ID
                result.setOrganizationId(pdadata.getOrganizationId());
                // 机构名称
                result.setOrganizationName(pdadata.getOrganizationName());
                // DMS编码
                result.setDmsCod(pdadata.getDmsCode());
                // 站点类型
                result.setSiteType(pdadata.getSiteType());
                // 站点子类型
                result.setSubType(pdadata.getSubType());
                // 返回结果
                return result;
            default:
                /** 未知状态 */
                // 设置错误标示和错误信息
                result.setError(true);
                result.setErrormsg("调用服务出现未知状态.错误信息为[" + pdadata.getMessage() + "]");
                // 返回结果集
                return result;
        }
    }

    /**
     * 用户登录包括自营和3PL
     * <pre>
     *    如果是自营
     *    1. 先调人事接口验证用户名和密码
     *    2. 验证通过后，根据员工id调基础资料接口获取员工信息
     *    如果是三方的
     *    1. 先调用户组接口验证京东账号和密码
     *    2. 验证通过后，根据京东账号调基础资料接口获取员工信息
     * </pre>
     *
     * @param userid
     * @param password
     * @return
     */
    public com.jd.bluedragon.distribution.base.domain.BasePdaUserDto userLogin(String userid, String password, ClientInfo clientInfo, Byte loginVersion){
        BasePdaUserDto pdadata = new BasePdaUserDto();
        if (log.isInfoEnabled()){
            log.info("用户登录新接口，用户名 {}" , userid);
        }
        if (StringHelper.isEmpty(userid) || StringHelper.isEmpty(password)) {
            pdadata.setErrorCode(Constants.PDA_USER_EMPTY);
            pdadata.setMessage(Constants.PDA_USER_EMPTY_MSG);
            return pdadata;
        }
        userid = userid.toLowerCase();
        return erpValidateService.pdaUserLogin(userid, password, clientInfo, loginVersion);
    }


    @Override
    public void selectErpValidateService(String erpAccount) {
        String userid = erpAccount.toLowerCase();
        // 3PL登录
        if (userid.contains(Constants.PDA_THIRDPL_TYPE)) {
            setErpValidateService(setThirdErpValidateService());
        }else{
            //自营登录
            setErpValidateService(setOwnErpValidateService());
        }
    }

    public ErpValidateService getErpValidateService() {
        return erpValidateService;
    }

    public void setErpValidateService(ErpValidateService erpValidateService) {
        this.erpValidateService = erpValidateService;
    }



    /***
     * @description:设置自营验证服务
     * @return: com.jd.bluedragon.distribution.base.service.ErpValidateService
     * @author: lql
     * @date: 2020/8/21 11:42
     **/
    protected abstract ErpValidateService setOwnErpValidateService();

    /**
     * @description:设置三方验证服务
     * @return: com.jd.bluedragon.distribution.base.service.ErpValidateService
     * @author: lql
     * @date: 2020/8/21 11:42
     **/
    protected abstract ErpValidateService setThirdErpValidateService();


}
