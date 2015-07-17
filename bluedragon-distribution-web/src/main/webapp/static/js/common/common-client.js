/**
 * 通用数据客户端
 * @author suihonghua
 * @returns {CommonClient}
 */
function CommonClient(){}

CommonClient.prototype = new Object();
//上下文路径
CommonClient.contextPath = "";

/**
 * Ajax[POST] 异步数据请求
 * @author suihonghua
 * @param url	[String] 请求url
 * @param param	[Object] 参数
 * @param successFunction [Function] 成功回调函数
 */
CommonClient.post = function(url,param,successFunction){
	CommonClient.ajax("POST", url, param, successFunction);
};

/**
 * Ajax[GET] 异步数据请求
 * @author suihonghua
 * @param url	[String] 请求url
 * @param param	[Object] 参数
 * @param successFunction [Function] 成功回调函数
 */
CommonClient.get = function(url,param,successFunction){
	CommonClient.ajax("GET", url, param, successFunction);
};

/**
 * Ajax 异步数据请求
 * @author suihonghua
 * @param type	[String] e.g.:"POST" or "GET" 
 * @param url	[String] 请求url
 * @param param	[Object] 参数
 * @param successFunction [Function] 成功回调函数
 */
CommonClient.ajax = function(type,url,param,successFunction){
	jQuery.ajax({
		type: type,
		url: CommonClient.contextPath + url,
		data: param,
		contentType: "application/x-www-form-urlencoded; charset=utf-8",
		beforeSend: function(jqXHR, settings){
			$.blockUI({ message:"<span class='pl20 icon-loading'>正在处理,请稍后...</span>"});
		},
		success: successFunction,
		error: function(jqXHR, textStatus, errorThrown){
			alert("Error:status["+jqXHR.status+"],statusText["+ jqXHR.statusText +"]");
		},
		complete: function(jqXHR, textStatus){
			$.unblockUI();
		}
	});
};

