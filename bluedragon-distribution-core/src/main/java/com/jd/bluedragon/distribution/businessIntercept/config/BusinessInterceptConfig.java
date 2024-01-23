package com.jd.bluedragon.distribution.businessIntercept.config;

import com.jd.bluedragon.distribution.businessIntercept.dto.BusinessInterceptReport;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 拦截报表配置
 *
 * @author fanggang7
 * @time 2024-01-21 14:24:04 周日
 */
@Data
public class BusinessInterceptConfig {

    /**
     * 设备类型
     */
    private Map<String, String> deviceTypeConfig = new LinkedHashMap<>();

    /**
     * 设备子类型
     */
    private Map<String, Map<String, String>> deviceSubTypeConfig = new LinkedHashMap<>();

    /**
     * 操作节点
     */
    private Map<String, String> operateNodeConfig = new LinkedHashMap<>();

    /**
     * 操作子节点
     */
    private Map<String, Map<String, String>> operateSubNodeConfig = new LinkedHashMap<>();

    /**
     * 拦截类型
     */
    private Map<String, String> interceptTypeNodeConfig = new LinkedHashMap<>();

    /**
     * 拦截提示码和设备组合对应拦截类型关系
     */
    private Map<String, Map<String, List<String>>> deviceTypeAssocInterceptTypeAndCodeConfig = new LinkedHashMap<>();

    /**
     * 操作拦截后应对节点
     */
    private Map<String, String> disposeNodeConfig = new LinkedHashMap<>();

    /**
     * 拦截后处理节点与拦截类型对应关系
     */
    private Map<String, List<String>> interceptTypeAssocDisposeNodeConfig = new LinkedHashMap<>();

    // 记录拦截code与拦截类型关系
    private Map<String, String> interceptCodeAssocTypeMap = null;

    /**
     * 0重量拦截类型
     */
    public final static Integer WITHOUT_WEIGHT_INTERCEPT_TYPE = 10002;

    /**
     * 需要换单的拦截类型
     */
    private List<Integer> needExchangeNewWaybillInterceptTypeList = new ArrayList<>();

    /**
     * 根据拦截返回码判断是否需要处理
     *
     * @param businessInterceptReport 拦截内容
     * @return 是否需要处理结果
     * @author fanggang7
     * @time 2020-12-11 09:29:58 周五
     */
    public boolean judgeNeedHandle(BusinessInterceptReport businessInterceptReport) {
        if(Objects.isNull(this.deviceTypeAssocInterceptTypeAndCodeConfig)){
            return false;
        }
        if(Objects.isNull(this.operateNodeConfig)){
            return false;
        }
        String operateNodeStr = businessInterceptReport.getOperateNode() + "";
        if(!this.operateNodeConfig.containsKey(operateNodeStr)){
            return false;
        }
        // 按设备类型获取对应返回码和拦截类型对应关系
        Map<String, List<String>> interceptTypeAssocDisposeNodeConfig = this.deviceTypeAssocInterceptTypeAndCodeConfig.get(businessInterceptReport.getDeviceType() + "");
        if(Objects.isNull(interceptTypeAssocDisposeNodeConfig)){
            return false;
        }
        // 所有拦截提示码集合
        List<Integer> allInterceptNodeList = new ArrayList<>();
        for (String key : interceptTypeAssocDisposeNodeConfig.keySet()) {
            allInterceptNodeList.addAll(
                    interceptTypeAssocDisposeNodeConfig.get(key)
                            .stream()
                            .map(Integer::parseInt)
                            .collect(Collectors.toSet()));
        }
        if(!allInterceptNodeList.contains(businessInterceptReport.getInterceptCode())){
            return false;
        }
        // 如果子操作类型不为空，再判断子操作类型是否有配置
        if(businessInterceptReport.getOperateSubNode() != null){
            if(this.operateSubNodeConfig == null) {
                return false;
            }
            Map<String, String> matchOperateNodeMap = this.operateSubNodeConfig.get(operateNodeStr);
            if(matchOperateNodeMap == null) {
                return false;
            }
            return matchOperateNodeMap.containsKey(businessInterceptReport.getOperateSubNode() + "");
        }
        return true;
    }

    /**
     * 根据操作节点获取操作节点名称
     * @param operateNode 操作节点
     * @return 操作节点名称
     */
    public String getOperateNodeNameByOperateNode(Integer operateNode){
        return this.getOperateNodeNameByOperateNode(operateNode + "");
    }

    /**
     * 根据操作节点获取操作节点名称
     * @param operateNodeStr 操作节点
     * @return 操作节点名称
     */
    private String getOperateNodeNameByOperateNode(String operateNodeStr){
        if(this.operateNodeConfig == null || operateNodeStr == null){
            return null;
        }
        return this.operateNodeConfig.get(operateNodeStr);
    }

    /**
     * 根据操作节点和子节点获取操作子节点名称
     * @param operateNode 操作节点
     * @param operateSubNode 子操作节点
     * @return 操作节点名称
     */
    public String getOperateSubNodeNameByOperateNodeAndSubNode(Integer operateNode, Integer operateSubNode){
        return this.getOperateSubNodeNameByOperateNodeAndSubNode(operateNode + "", operateSubNode + "");
    }

    /**
     * 根据操作节点和子节点获取操作子节点名称
     * @param operateNodeStr 操作节点
     * @param operateSubNodeStr 子操作节点
     * @return 操作节点名称
     */
    private String getOperateSubNodeNameByOperateNodeAndSubNode(String operateNodeStr, String operateSubNodeStr){
        if(this.operateNodeConfig == null || operateNodeStr == null || operateSubNodeStr == null){
            return null;
        }
        Map<String, String> operateSubNodeMap = this.operateSubNodeConfig.get(operateNodeStr);
        if(operateSubNodeMap == null){
            return null;
        }
        return operateSubNodeMap.get(operateSubNodeStr);
    }

    /**
     * 根据配置，拦截类型 - 处理节点 关系，得到要更新的拦截类型数据范围
     * @param disposeNode 处理节点
     * @return 需要处理的拦截类型列表
     * @author fanggang7
     * @time 2024-01-21 14:42:12 周日
     */
    public List<Integer> getNeedHandleInterceptTypeList(Integer disposeNode){
        List<Integer> needHandleInterceptTypeList = new ArrayList<>();
        // 根据配置，拦截类型 - 处理节点 关系，得到要更新的拦截类型数据范围
        Map<String, List<String>> interceptTypeAssocDisposeNodeConfigMap = this.getInterceptTypeAssocDisposeNodeConfig();
        for (String interceptTypeStr : interceptTypeAssocDisposeNodeConfigMap.keySet()) {
            List<String> disposeNodes = interceptTypeAssocDisposeNodeConfigMap.get(interceptTypeStr);
            if(disposeNodes.contains(disposeNode.toString())){
                needHandleInterceptTypeList.add(Integer.parseInt(interceptTypeStr));
            }
        }
        return needHandleInterceptTypeList;
    }

    /**
     * 根据拦截类型，得到可以处理的拦截节点
     * @param interceptType 拦截类型
     * @return 需要处理的拦截类型列表
     * @author fanggang7
     * @time 2024-01-21 14:42:12 周日
     */
    public List<Integer> getDisposeNodeListByInterceptType(Integer interceptType){
        List<Integer> needHandleInterceptTypeList = new ArrayList<>();
        // 根据配置，拦截类型 - 处理节点 关系，得到要更新的拦截类型数据范围
        Map<String, List<String>> interceptTypeAssocDisposeNodeConfigMap = this.getInterceptTypeAssocDisposeNodeConfig();
        final List<String> disposeNodeStrList = interceptTypeAssocDisposeNodeConfigMap.get(interceptType.toString());
        return disposeNodeStrList.stream().map(Integer::parseInt).collect(Collectors.toList());
    }
}
