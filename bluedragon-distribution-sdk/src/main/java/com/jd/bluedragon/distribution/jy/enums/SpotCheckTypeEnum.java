package com.jd.bluedragon.distribution.jy.enums;

/**
 * 抽检类型
 * @author wuyoude
 *
 */
public enum SpotCheckTypeEnum {

	DIRECT(1,"定向抽检"),
    RANDOM(2, "随机抽检");

    private Integer code;

    private String name;

     SpotCheckTypeEnum() {
    }

     SpotCheckTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    public static boolean containsCode(Integer code){
         for(SpotCheckTypeEnum reportTypeEnum : SpotCheckTypeEnum.values()){
             if(reportTypeEnum.getCode().equals(code)){
                 return true;
             }
         }
         return false;
    }
    public static String getNameByCode(Integer code){
         for(SpotCheckTypeEnum reportTypeEnum : SpotCheckTypeEnum.values()){
        	 if(reportTypeEnum.getCode().equals(code)){
                 return reportTypeEnum.getName();
             }
         }
         return "";
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
