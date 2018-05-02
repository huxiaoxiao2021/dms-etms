package com.jd.bluedragon.distribution.testCore.base;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @ClassName: EntityUtil
 * @Description: 生产一个对象
 * @author: wuyoude
 * @date: 2017年5月2日 下午3:28:18
 *
 */
public class EntityUtil {
	public static Logger log = LoggerFactory.getLogger(EntityUtil.class);
	/**
	 * 对象最大初始化深度
	 */
	private static int MAX_DEPTH = 5;
	private static Set<Class> FILTER_CLAZZS = new HashSet<Class>();
	private static Set<Class> COMMON_CLAZZS = new HashSet<Class>();
	private static int INT_VAL_MAX_DEFAULT = 50;
	private static int MAX_INTEGER_VAL = INT_VAL_MAX_DEFAULT;
	/**
	 * 对象列表个数
	 */
	private static int LIST_SIZE_VAL = 5;
	private static Random random = new Random();
	
	static{
		init();
	}
	public static void init() {
		Class[] filters = {Class.class,Object.class};
		Class[] commons = {boolean.class,Boolean.class,int.class,Integer.class,long.class,Long.class,float.class,Float.class,double.class,Double.class,String.class,java.util.Date.class,java.sql.Date.class,java.sql.Timestamp.class,java.sql.Time.class};
		for(Class clazz:filters){
			FILTER_CLAZZS.add(clazz);
		}
		for(Class clazz:commons){
			COMMON_CLAZZS.add(clazz);
		}
	}
	public static <T> T getInstance(Class<T> clazz) {
		return getInstance(clazz,MAX_DEPTH);
	}
	/**
	 * 获取一个默认对象，数值类型随机取值，字符串填"字段名",日期为当前日期,
	 * 数字型字段随机值
	 * @param obj
	 * @return
	 */
	public static <T> T getInstance(Class<T> clazz,int depth) {
		T obj = null;
		Date defaultDate = new Date();
		if (clazz != null && !FILTER_CLAZZS.contains(clazz)) {
			try {
				String defaultStr = "";
				if(COMMON_CLAZZS.contains(clazz)){
					return  getCommonObject(clazz,defaultStr,defaultDate);
				}
				obj = clazz.newInstance();
				Class supClazz = clazz.getSuperclass();
				if(!COMMON_CLAZZS.contains(supClazz)&&!supClazz.equals(Object.class)){
					Field[] supperFields = supClazz.getDeclaredFields();
					fillFields(obj,supperFields,defaultStr,defaultDate,depth);
				}
				Field[] fields = clazz.getDeclaredFields();
				fillFields(obj,fields,defaultStr,defaultDate,depth);
				} catch (Exception  e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return obj;
	}
	/**
	 * 填充对象属性
	 * @param obj
	 * @param fields
	 * @param defaultStr
	 * @param defaultDate
	 * @param depth
	 */
	public static void fillFields(Object obj,Field[] fields,String defaultStr,Date defaultDate,int depth){
		for(Field f : fields){
			defaultStr = f.getName();
			try {
				if(COMMON_CLAZZS.contains(f.getType())){
					BeanUtils.setProperty(obj, f.getName(), getCommonObject(f.getType(),defaultStr,defaultDate));
				} else if(f.getType().equals(java.util.List.class)){
				    ParameterizedType pt = (ParameterizedType)f.getGenericType(); 
					BeanUtils.setProperty(obj, f.getName(), getInstanceList((Class)pt.getActualTypeArguments()[0],LIST_SIZE_VAL));
				} else if(f.getType().equals(java.util.Map.class)){
//				    ParameterizedType pt = (ParameterizedType)f.getGenericType(); 
//					BeanUtils.setProperty(obj, f.getName(), getInstanceList((Class)pt.getActualTypeArguments()[0],LIST_SIZE_VAL));
				} else if (depth >0){
					BeanUtils.setProperty(obj, f.getName(), getInstance(f.getType(),depth-1));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 获取指定长度的实例列表
	 * @param clazz
	 * @param size
	 * @return
	 */
	public static List<Object> getInstanceList(Class clazz,int size) {
		List list = new ArrayList();
		for(int i=1;i<=size;i++){
			list.add(getInstance(clazz));
		}
		return list;
	}
	/**
	 * 获取指定长度的实例列表
	 * @param clazz
	 * @param depth - 对象初始化深度
	 * @param size
	 * @return
	 */
	public static List<Object> getInstanceList(Class clazz,int depth,int size) {
		List list = new ArrayList();
		for(int i=1;i<=size;i++){
			list.add(getInstance(clazz,depth));
		}
		return list;
	}
	/**
	 * 基本类型
	 * @return
	 */
	public static <T> T getCommonObject(Class<T> clazz,String defaultStr,java.util.Date defaultDate){
		Object obj = null;
		if(defaultStr == null){
			defaultStr = "str";
		}
		if(defaultDate == null){
			defaultDate = new java.util.Date();
		}
		if(clazz.equals(boolean.class)||clazz.equals(Boolean.class)){
			obj = random.nextBoolean();
		}if(clazz.equals(byte.class)||clazz.equals(Byte.class)){
			obj = (byte)random.nextInt(128);
		} else if(clazz.equals(int.class)||clazz.equals(Integer.class)){
			obj = random.nextInt(MAX_INTEGER_VAL);
		} else if(clazz.equals(long.class)||clazz.equals(Long.class)){
			obj = random.nextInt(MAX_INTEGER_VAL);
		} else if(clazz.equals(float.class)||clazz.equals(Float.class)){
			obj = random.nextInt(MAX_INTEGER_VAL)+random.nextInt(100)/100.0;
		} else if(clazz.equals(double.class)||clazz.equals(Double.class)){
			obj = random.nextInt(MAX_INTEGER_VAL)+random.nextInt(100)/100.0;;
		} else if(clazz.equals(String.class)){
			obj = defaultStr;
		}  else if(clazz.equals(java.util.Date.class)){
			obj = new java.util.Date();
		} else if(clazz.equals(java.sql.Date.class)){
			obj = new java.sql.Date(defaultDate.getTime());
		} else if(clazz.equals(java.sql.Timestamp.class)){
			obj = new java.sql.Timestamp(defaultDate.getTime());
		} else if(clazz.equals(java.sql.Time.class)){
			obj = new java.sql.Time(defaultDate.getTime());
		}
		return (T)obj;
	}
	   @SuppressWarnings("unchecked")
	    public static Class<Object> getSuperClassGenricType(final Class clazz, final int index) {
	    	
	    	//返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
	        Type genType = clazz.getGenericSuperclass();

	        if (!(genType instanceof ParameterizedType)) {
	           return Object.class;
	        }
	        //返回表示此类型实际类型参数的 Type 对象的数组。
	        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

	        if (index >= params.length || index < 0) {
	                     return Object.class;
	        }
	        if (!(params[index] instanceof Class)) {
	              return Object.class;
	        }
	        log.info(params[index].toString());
	        return (Class) params[index];
	    }
	   /**
	    * 拷贝属性值
	    * @param srcObj
	    * @param desObj
	    * @param replaceFlg 是否覆盖已有值
	    */
	    public static void copyProperties(Object srcObj,Object desObj, boolean replaceFlg){
			Field[] fields = srcObj.getClass().getDeclaredFields();
			String fieldName = null;
			Object newVal = null;
			Object oldVal = null;
			for(Field f : fields){
				if(!f.getType().equals(String.class)){
//					continue;
				}
				try {
					fieldName = f.getName();
					newVal = BeanUtils.getProperty(srcObj, fieldName);
					oldVal = BeanUtils.getProperty(desObj, fieldName);
					if(newVal!=null){
						if(oldVal!=null && !replaceFlg){
							continue;
						}
						BeanUtils.setProperty(desObj, fieldName, newVal);
					}
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    }
		/**
		 * 
		 * @param dbName
		 * @return
		 */
		public static String formatJavaName(String dbName){
			StringBuffer sf = new StringBuffer();
			int count = 0;
			char before = 'a';
			for(char c: dbName.toLowerCase().toCharArray()){
				count++;
				if(c!='_'){
					if(before=='_' && count>2){
						sf.append(Character.toUpperCase(c));
					}else{
						sf.append(c);
					}
				}
				before = c;
			}
			return sf.toString();
		}
}