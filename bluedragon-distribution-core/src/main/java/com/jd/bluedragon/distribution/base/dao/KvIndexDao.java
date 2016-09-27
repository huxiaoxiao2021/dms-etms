package com.jd.bluedragon.distribution.base.dao;

import java.text.MessageFormat;
import java.util.*;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.base.domain.KvIndex;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KvIndexDao extends BaseDao<KvIndex> {

    private static final Log LOGGER= LogFactory.getLog(KvIndexDao.class);

	public static final String namespace = KvIndexDao.class.getName();



    private List<String> queryByKeyword(String keyword) {
		return this.getSqlSession().selectList(namespace + ".queryByKeyword", keyword);
	}

    /**
     * 获取创建站点索引值
     * @param keyword 关键字
     * @return
     */
    public List<Integer> queryCreateSiteCodesByKey(String keyword){
        if(StringUtils.isBlank(keyword)){
            return new ArrayList<Integer>(0);
        }
        keyword=keyword.trim().toUpperCase();

        List<String> values= this.getSqlSession().selectList(namespace + ".queryByKeyword", keyword);
        if(null==values||values.size()==0){
            return new ArrayList<Integer>(0);
        }else{
            Set<Integer> sets=new HashSet<Integer>(values.size());
            for (String item:values){
                try {
                    sets.add(NumberUtils.createInteger(item));
                }catch (Throwable throwable){
                    LOGGER.error(MessageFormat.format("分库索引值转成为数字失败keyword:{0}", keyword), throwable);
                }
            }
            return new ArrayList<Integer>(sets);
        }
    }

    public Integer add(KvIndex entity) {
        if(null==entity||StringUtils.isBlank(entity.getKeyword())||StringUtils.isBlank(entity.getValue())){
            if(LOGGER.isWarnEnabled()){
                LOGGER.warn(MessageFormat.format("索引对象有空值{0}",JsonHelper.toJson(entity)));
            }
            return 0;
        }
        entity.setKeyword(entity.getKeyword().trim().toUpperCase());
        entity.setValue(entity.getValue().trim().toUpperCase());
        if(entity.getKeyword().length()>50){
            if(LOGGER.isWarnEnabled()){
                LOGGER.warn(MessageFormat.format("KEY={0}值超过50，截断为50",entity.getKeyword()));
            }
            entity.setKeyword(entity.getKeyword().substring(0,50));
        }
        if (entity.getValue().length() > 50) {
            if(LOGGER.isWarnEnabled()){
                LOGGER.warn(MessageFormat.format("VALUE={0}值超过50，截断为50",entity.getValue()));
            }
            entity.setValue(entity.getValue().substring(0,50));
        }
        return super.add(namespace, entity);
    }

    public static void main(String[] args) {
        Set<Integer> sets=new HashSet<Integer>();
        sets.add(Integer.valueOf(3));
        sets.add(Integer.valueOf(3));
        sets.add(Integer.valueOf(123412134));

        sets.add(Integer.valueOf(123412134));
        System.out.println(JsonHelper.toJson(sets));
    }
}