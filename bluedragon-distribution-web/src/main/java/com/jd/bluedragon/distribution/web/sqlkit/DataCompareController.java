package com.jd.bluedragon.distribution.web.sqlkit;

import com.google.common.base.Joiner;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.sqlkit.domain.DataCompareDto;
import com.jd.bluedragon.utils.MysqlHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dataCompare")
public class DataCompareController {

	private final Logger log = LoggerFactory.getLogger(DataCompareController.class);
    private static int LIMIT_ALLOW = 100;

    @RequestMapping("/comparePage")
    public String toView(Model model) {
        return "sqlkit/comparePage";
    }


    @RequestMapping(value = "/compare", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Map<String,Object>> compare(DataCompareDto dataCompareDto){
        InvokeResult<Map<String,Object>> result = new InvokeResult<>();
        result.success();
        try {
            String primaryKey = MysqlHelper.getPrimaryKey(dataCompareDto.getSql());
            String sqlBeforeWhere = MysqlHelper.getSqlBeforeWhere(dataCompareDto.getSql());
            String limit = MysqlHelper.getLimit(dataCompareDto.getSql());
            if(primaryKey == null || sqlBeforeWhere == null || limit == null){
                result.parameterError("sql格式错误");
                return result;
            }
            if(LIMIT_ALLOW < Integer.parseInt(limit)){
                result.parameterError("limit大于"+LIMIT_ALLOW);
                return result;
            }
            String oldJdbc = "jdbc:mysql://"+dataCompareDto.getOldUrl()+"?characterEncoding=UTF-8";
            String newJdbc = "jdbc:mysql://"+dataCompareDto.getNewUrl()+"?characterEncoding=UTF-8";
            List<LinkedHashMap<String, String>> oldDataList = MysqlHelper.executeQuery(oldJdbc,dataCompareDto.getOldUserName(),
                    dataCompareDto.getOldPassword(),dataCompareDto.getSql());
            if(CollectionUtils.isEmpty(oldDataList)){
                result.parameterError("数据为空");
                return result;
            }

            List<String> fields = new ArrayList<>();
            Iterator<String> keySet = oldDataList.get(0).keySet().iterator();
            while (keySet.hasNext()){
                fields.add(keySet.next());
            }
            List<Long> ids = new ArrayList<>();
            for(LinkedHashMap<String, String> item:oldDataList){
                String id= item.get(primaryKey.toUpperCase());
                if(id == null){
                    id = item.get(primaryKey.toLowerCase());
                }
                ids.add(Long.parseLong(id));
            }
            StringBuilder newSql = new StringBuilder();
            newSql.append(sqlBeforeWhere).append(" where ").append(primaryKey).append(" in (").append(Joiner.on(",").join(ids)).append(")");
            //根据查询id 拼接sql 查询新库
            List<LinkedHashMap<String, String>> newDataList = MysqlHelper.executeQuery(newJdbc,dataCompareDto.getNewUserName(),
                    dataCompareDto.getNewPassword(),newSql.toString());
            List<List<String>> mergeList = getMergeLists(primaryKey, oldDataList, newDataList);
            Map<String,Object> resultMap = new HashMap<>(2);
            resultMap.put("fields",fields);
            resultMap.put("mergeList",mergeList);
            result.setData(resultMap);
        } catch (Exception e) {
            log.error("查询报错",e);
            result.parameterError(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 把两个库的数据 进行整理成list；结构为：
     * id data
     * 1  data1
     * 1  data1
     * 2  data2
     * 2  data2
     * @param primaryKey
     * @param oldDataList
     * @param newDataList
     * @return
     */
    private List<List<String>> getMergeLists(String primaryKey, List<LinkedHashMap<String, String>> oldDataList, List<LinkedHashMap<String, String>> newDataList) {
        Map<String,List<String>> oldMap = getDataList(primaryKey, oldDataList);
        Map<String,List<String>> newMap = getDataList(primaryKey, newDataList);
        List<List<String>> mergeList = new LinkedList<>();
        for(Map.Entry<String,List<String>> item : oldMap.entrySet()){
            mergeList.add(item.getValue());
            mergeList.add(newMap.get(item.getKey()) == null?new ArrayList<String>():newMap.get(item.getKey()));
        }
        return mergeList;
    }

    /**
     * 数据返回的原始数据转换成 map<id,list<每列数据>>
     * @param primaryKey
     * @param oldDataList
     * @return
     */
    private Map<String,List<String>> getDataList(String primaryKey, List<LinkedHashMap<String, String>> oldDataList) {
        Map<String,List<String>> oldMap = new HashMap<>();
        for(LinkedHashMap<String, String> item:oldDataList){
            String id= item.get(primaryKey.toUpperCase());
            if(id == null){
                id = item.get(primaryKey.toLowerCase());
            }
            oldMap.put(id,new ArrayList<>(item.values()));
        }
        return oldMap;
    }
}
