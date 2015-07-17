/**
 * 验证是否数字字符
 * @param value
 * @return
 * */
function checkNumStr(value){
    return /^\d+$/.test(value);
}

/**
 * 验证是否为空
 * @param value
 * @return
 */
function checkNull(value) {
	if (trimStr(value) == '') {
		return false;
	}
	return true;
}

/**
 * 验证是否为数字，含小数
 * @param obj
 * @return
 */
function checkNum(obj) {
	var valueStr = '';
	if (typeof(obj) == 'string') {
		valueStr = obj;
	} else if (typeof(obj) == 'object') {
		valueStr = obj.value;
	}
	if (trimStr(valueStr) == '') {
		return false;
	}
	var patternStr = new RegExp("^[1-9]+[0-9]*$", "g");
	return patternStr.test(valueStr);
}

/**
* 去除首尾空格
*/
function trimStr(text) {
	return (text || "").replace(/^\s+|\s+$/g, "");
}


/**
 * 检测两个日期不超过48小时、开始日期不大于结束日期
 * @param startDate 开始日期
 * @param endDate   结束日期
 * @returns 1，大于48小时，2，开始日期大于结束日期
 */
function checkDate(startDate,endDate){
var inteval=1000*60*60*48;
  var  str1=startDate.toString();
  var  str2=endDate.toString();
       str1 =  str1.replace(/-/g,"/");
	   str2 =  str2.replace(/-/g,"/");
  var oDate1 = new Date(str1);
  var oDate2 = new Date(str2);
  if(oDate1.getTime()>oDate2.getTime()){
  return 2;
  }
  if((oDate2.getTime()-oDate1.getTime())>inteval){
     return 1;
  }
  return 0;
}