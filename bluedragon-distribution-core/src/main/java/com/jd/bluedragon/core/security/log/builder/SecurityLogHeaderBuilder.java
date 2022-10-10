package com.jd.bluedragon.core.security.log.builder;

import com.jd.securitylog.entity.Head;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.security.log
 * @ClassName: SecurityLogHeaderBuilder
 * @Description: 长城日志接入文档：请见head部分：
 * @link https://cf.jd.com/pages/viewpage.action?pageId=584535857
 * @Author： wuzuxiang
 * @CreateDate 2022/9/2 16:29
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class SecurityLogHeaderBuilder {

    private Head head = new Head();

    /**
     * 本日志格式的版本号。必填；
     * 当前版本号为V1.0；
     * @param version
     * @return
     */
    public SecurityLogHeaderBuilder version(String version) {
        this.head.setVersion(version);
        return this;
    }

    /**
     * 系统英文名称；必填；与jone或jdos一致。
     * jone系统名称，对应系统信息中的系统英文名称字段。
     * jdos系统名称，对应基本信息中的所属系统字段。
     * @param systemName
     * @return
     */
    public SecurityLogHeaderBuilder systemName(String systemName) {
        this.head.setSystemName(systemName);
        return this;
    }

    /**
     * 应用英文名称； 必填；
     * 必须和jone或jdos中应用名称一致。
     * jone应用名称，对应基本信息中的应用英文名称字段。
     * jdos应用名称，对应应用信息中的应用标识字段。
     * @param appName
     * @return
     */
    public SecurityLogHeaderBuilder appName(String appName) {
        this.head.setAppName(appName);
        return this;
    }

    /**
     * 接口名称，必填；根据实际情况填写访问路径，如http填写url， jsf填写对外暴露的方法名等
     * @param interfaceName
     * @return
     */
    public SecurityLogHeaderBuilder interfaceName(String interfaceName) {
        this.head.setInterfaceName(interfaceName);
        return this;
    }

    /**
     * 唯一ID； 保证同一个应用是唯一的。
     * 当接口分为多个记录返回同一个查询信息时，
     * 可通过唯一ID的方式做关联。
     * 如目前接口中没有，请生成uuid填写。
     * @param uuid
     * @return
     */
    public SecurityLogHeaderBuilder uuid(String uuid) {
        this.head.setUuid(uuid);
        return this;
    }

    /**
     * 操作时间（北京时间），数值型.；必填；
     * 单位：秒（10位）；
     * @param time
     * @return
     */
    public SecurityLogHeaderBuilder time(Long time) {
        this.head.setTime(time);
        return this;
    }

    /**
     * 应用部署机器ip，点分制；必填；
     * @param serverIp
     * @return
     */
    public SecurityLogHeaderBuilder serverIp(String serverIp) {
        this.head.setServerIp(serverIp);
        return this;
    }

    /**
     * 客户端ip,点分制；
     * 客户端ip须为访问业务系统的用户实际IP，不能是127.0.0.1、serverIp。
     * @param clientIp
     * @return
     */
    public SecurityLogHeaderBuilder clientIp(String clientIp) {
        this.head.setClientIp(clientIp);
        return this;
    }

    /**
     * 账号类型；必填；1：erp  2：passport  3：selfCreated（业务系统自建闭环账号体系） 4: JSF调用者  5: 定时任务  6: 商家账号
     * @param accountType
     * @return
     */
    public SecurityLogHeaderBuilder accountType(Integer accountType) {
        this.head.setAccountType(accountType);
        return this;
    }

    /**
     * 实施操作行为产生日志的“账号对象”（登陆业务系统的账号）。
     * 接入日志的应用通常上报的accountName为”员工ERP“或”京东passport的pin“，其他情况请先与技术支持同学咨询：
     * accountType为1时：accountName为”员工ERP“；
     * accountType为2时：accountName为"京东passport的pin"；
     * accountType为3时：accountName为selfCreated（未使用集团统一账号，自建的账号）；
     * accountType为4时: accountName为"JSF调用者"（如果是JSF调用，填写调用方应用名称）；
     * accountType为5时: accountName为"定时任务名称"；
     * accountType为6时: accountName为”商家账号名称“
     *
     * @param accountName
     * @return
     */
    public SecurityLogHeaderBuilder accountName(String accountName) {
        this.head.setAccountName(accountName);
        return this;
    }

    /**
     * 具体操作行为：必填；
     * 0：添加
     * 1：删除
     * 2：更新
     * 3：查询-无明文敏感信息显示（敏感信息脱敏显示或无敏感信息输出）
     * 4：导出-无明文敏感信息显示（敏感信息脱敏显示或无敏感信息输出）
     * 5：查询-有明文敏感信息显示（点击查看为敏感信息明文或记录列表包含敏感信息明文）
     * 6：导入
     * 7:导出-有明文敏感信息显示（导出文件中包含敏感信息明文）
     * 8：打印-有明文敏感信息显示
     * 9：打印-无明文敏感信息显示
     * 10：打印预览-有明文敏感信息显示
     * 11：打印预览-无明文敏感信息显示
     * @param op
     * @return
     */
    public SecurityLogHeaderBuilder op(Integer op) {
        this.head.setOp(op);
        return this;
    }

    public Head build() {
        return this.head;
    }

}
