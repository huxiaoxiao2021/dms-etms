/**
 * 时间处理工具类
 * @author suihonghua
 * 
 * @returns {DateUtil}
 */
function DateUtil(){}
DateUtil.prototype = new Object();

//DateUtil.prototype.length = 1;

/**
 * 格式化日期时间
 * 
 * @param d Date实例
 * @returns string 格式如：2001-01-01 01:01:01
 * 
 * @author suihonghua
 */
DateUtil.formatDateTime = function(date){
	return this.format(date, "yyyy-MM-dd HH:mm:ss");
};

/**
 * 格式化日期
 * 
 * @param d Date实例
 * @returns string 格式如：2001-01-01
 * 
 * @author suihonghua
 */
DateUtil.formatDate = function(date){
	return this.format(date, "yyyy-MM-dd");
};

/**
 * 格式化时间
 * 
 * @param d Date实例
 * @returns string 格式如：01:01:01
 * 
 * @author suihonghua
 */
DateUtil.formatTime = function(date){
	return this.format(date, "HH:mm:ss");
};

/**
 * 时间格式化
 * e.g.
 * 	var testDate = new Date( 1320336000000 );//这里必须是整数，毫秒 
 * 
 *	var testStr1 = DateUtil.format(testDate, "yyyy年MM月dd日HH小时mm分ss秒"); 
 *	var testStr2 = DateUtil.format(testDate, "yyyy-MM-dd HH:mm:ss.S");
 *  var testStr3 = DateUtil.format(testDate, "yyyy-MM-dd HH:mm:ss");
 *	alert(testStr1);
 *  alert(testStr2);
 *  alert(testStr3);
 * 
 * @param date
 * @param format
 * @returns
 * 
 * @author suihonghua
 */
DateUtil.format = function(date,format){
	try {
		//yyyy年MM月dd日 HH小时mm分ss秒
		var o = { 
			"M+" : date.getMonth()+1, //month 
			"d+" : date.getDate(), //day 
			"H+" : date.getHours(), //hour 
			"m+" : date.getMinutes(), //minute 
			"s+" : date.getSeconds(), //second 
			"q+" : Math.floor((date.getMonth()+3)/3), //quarter 
			"S" : date.getMilliseconds() //millisecond 
		};

		if(/(y+)/.test(format)) { 
			format = format.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length)); 
		}
		for(var k in o) { 
			if(new RegExp("("+ k +")").test(format)){ 
				format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length)); 
			} 
		} 
		return format; 
	} catch (e) {
		return "";
	}
};

/**
 * 解析为Data类型(目前string只支持标准格式)
 * 
 * @param val 参数(int or string)
 * @returns Date
 * 
 * @author suihonghua
 */
DateUtil.parse = function(val){
    if(typeof(val) == "number") {
	    return new Date(val);
    }
    else if(typeof(val) == "string") {
        return new Date(Date.parse(val.replace(/-/g,   "/")));
    }
	return null;
};


/**
 * 字符串工具类
 * 
 * @author suihonghua
 * 
 * @returns {StringUtil}
 * 
 */
function StringUtil(){}
StringUtil.prototype = new Object();

//StringUtil.prototype.length = 1;

StringUtil.nullToEmpty = function(str){
	return str == null ? "" : str ;
};

StringUtil.trim = function(str){
	return str.replace(/(^[\s]*)|([\s]*$)/g, ""); 
};  

StringUtil.trimLeft = function(str){  
	return str.replace(/(^[\s]*)/g, "");
};

StringUtil.trimRight = function(str){  
	return str.replace(/([\s]*$)/g, "");
};

/**
 * Map实现类(详细使用请阅读代码，非Java原版Map)
 * 
 * @author suihonghua
 * 
 * @returns {Map}
 * 
 */
function Map(){
	this.container = new Object();
}

Map.prototype.putAll = function(map){ 
	var keyset = map.keySet();
	for ( var i = 0; i < keyset.length; i++) {
		var key = keyset[i];
		this.put(key, map.get(key));
	}
};

Map.prototype.put = function(key, value){ 
	this.container[key] = value; 
};

Map.prototype.get = function(key){ 
	return this.container[key]; 
};

Map.prototype.keySet = function() { 
	var keyset = [];
	for (var key in this.container) { 
		keyset.push(key); 
	} 
	return keyset; 
};

Map.prototype.size = function() { 
	var keyset = this.keySet();
	return keyset.length; 
};

Map.prototype.containsKey = function(key) { 
	for (var _key in this.container) { 
		if(_key == key){
			return true;
		}
	}
	return false;
};

Map.prototype.toString = function(){
	var str = "";
	for (var key in this.container) { 
		str += key + ":" + this.container[key] + ",";
	} 
	if(str.length > 0){
		str = str.substring(0, str.length-1);
	}
	str = "{" + str + "}";
	return str; 
}; 

Map.valueOf = function(obj){
	var map = new Map();
	for (var key in obj) { 
		map.put(key, obj[key]);
	} 
	return map ;
};




