//初始化
$(document).ready(main);

// 全局变量
var taskTableList = null;

function main() {

	// 查询按钮提交处理
	$('#queryBtn').click(function() {
		onQueryBtnClick(1);
	});

	// 初始化不可变的组件
	initComp();
}

function initComp() {
	$("#siteNo").enable(false);
	$("#machineCode").enable(false);
	$("#sortMode").enable(false);
	$("#name").enable(false);
}

function onQueryBtnClick(pageNo) {
	var params = getParams();
	params.pageNo = pageNo;
	doQueryCrossSorting(params);
}

function getParams() {
	var params = {};
	params.schemeId = $.trim($("#schemeId").val()); //方案id
	params.siteNo = $.trim($("#siteNo").val()); //分拣中心
	params.boxSiteCode = $.trim($("#boxSiteCode").val());	//站点
	params.chuteCode1 = $.trim($("#chuteCode").val()); //物理滑槽
	//alert(params.siteNo + "" +params.boxSiteCode + "" +params.chuteCode1);
	return params;
}

// 查询请求
function doQueryCrossSorting(params) {
	var url = $("#contextPath").val() + "/autosorting/sortSchemeDetail/list";
	CommonClient.postJson(url, params, function(data) {
		if (data == undefined || data == null) {
			jQuery.messager.alert('提示:','HTTP请求无数据返回！','info');
			return;
		}
		if (data.code == 200) {
			var pager = data.data;
			var dataList = pager.data;
			var temp = "";
			for (var i = 0; i < dataList.length; i++) {
				temp += "<tr class='a2' style=''>";

				temp += "<td>" + (dataList[i].chuteCode1) + "</td>";
				siteNo = dataList[i].siteNo;
				temp += "<td>" + (dataList[i].siteCode) + "</td>";
				temp += "<td>" + (dataList[i].boxSiteCode) + "</td>";
				temp += "<td>" + (dataList[i].pkgLabelName) + "</td>";
				temp += "<td>" + (dataList[i].currChuteCode) + "</td>";
				temp += "<td>" + (dataList[i].yn == 1 ? '激活' : '未激活') + "</td>";
				temp += "<td>" + (dataList[i].receFlag == 1 ? '接收' : '未接收') + "</td>";
				temp += "<td>" + (dataList[i].receTime) + "</td>";

				temp += "</tr>";
			}
			$("#paperTable tbody").html(temp);
			$("#pageNo").val(pager.pageNo); // 当前页码
			// 添加分页显示
			$("#pager").html(PageBar.getHtml("onQueryBtnClick", pager.totalSize, pager.pageNo, pager.totalNo));
		} else {
			siteNo = null;
			jQuery.messager.alert('提示:', data.message, 'info');
		}
	});
}

function getData(value) {
	if (value == null || value == "") {
		return "";
	} else {
		return value;
	}
}

// -------------分页控制-----------------
function goFirstPage() {
	var params = getParams();
	params.pageNo = 1;
	// params.pageSize = $.trim($("#pageSize").val());
	if (checkParams(params)) {
		return false;
	}
	doQueryCrossSorting(params);
}

function goPrevPage() {
	var params = getParams();
	var pageNo = getNum("pageNo");
	params.pageNo = pageNo <= 1 ? 1 : pageNo - 1;
	if (checkParams(params)) {
		return false;
	}
	doQueryCrossSorting(params);
}

function goNextPage() {
	var params = getParams();
	var pageNo = getNum("pageNo");
	var totalNo = getNum("totalNo");
	params.pageNo = pageNo >= totalNo ? pageNo : pageNo + 1;
	if (checkParams(params)) {
		return false;
	}
	doQueryCrossSorting(params);
}

function goTailPage() {
	var params = getParams();
	params.pageNo = getNum("totalNo");
	if (checkParams(params)) {
		return false;
	}
	doQueryCrossSorting(params);
}
// -------------分页控制-----------------

function getNum(str) {
	var num = $.trim($("#" + str).val());
	if (num == undefined || num == null || num == "") {
		return 0;
	} else {
		return parseInt(num);
	}
}

// ----------------------

function getDateString(millis) {
	if (null == millis) {
		return "";
	}
	var date = new Date();
	date.setTime(millis);
	return date.format('yyyy-MM-dd HH:mm:ss');
}

Date.prototype.format = function(f) {
	var o = {
		"M+" : this.getMonth() + 1, // month
		"d+" : this.getDate(), // day
		"H+" : this.getHours(), // hour
		"m+" : this.getMinutes(), // minute
		"s+" : this.getSeconds(), // second
		"q+" : Math.floor((this.getMonth() + 3) / 3), // quarter
		"S" : this.getMilliseconds()
	// millisecond
	}
	if (/(y+)/.test(f))
		f = f.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(f))
			f = f.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
	return f
}

//获取多余字符,保证分拣中间对齐显示
function getMaxLen(dmsList){
	var maxLen = 0;
	for (var i = 0; i < dmsList.length; i++) {
		var siteCode = dmsList[i].siteCode;
		if(siteCode != null && siteCode.toString() != "" && siteCode.toString().length > maxLen){
			maxLen = siteCode.toString().length;
		}	
	}
}
function getBlanks(siteCode, maxLen){
	var blank = "_";
	if(siteCode == null || siteCode.toString() == ""){
		return blank;
	}
	var gap = maxLen - siteCode.toString().length;
	for(var i = 0; i < gap; i++){
		blank += "_";
	}
	return blank;
}