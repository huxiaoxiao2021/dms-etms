//初始化
$(document).ready(main);

// 全局变量
var taskTableList = null;

function main() {

	// 查询按钮提交处理
	$('#queryBtn').click(function() {
		onQueryBtnClick(1);
	});

	// 导入按钮
	$("#loadInBtn").click(function(){
		importExcel();
	});

    // 导出按钮
    $("#loadOutBtn").click(function(){
       exportExcel();
    })

    // 下载模版
    $("#downloadModelBtn").click(function(){
       goDownModel();
    })

	// 加载所有的分拣中心
	initDms();
}

// 实现与机构联动
function initCreateDmsList() {
	// select DISTINCT task_type from task_weight;
	var orgId = $.trim($("#orgList").val());
	var url = $("#contextPath").val() + "/services/bases/dms/" + orgId;
	if (orgId == "") {
		return;
	}
	$.getJSON(url, function(data) {
		var dmsList = data;
		if (data == undefined || data == null) {
			jQuery.messager.alert('提示:', "HTTP请求无数据返回！", 'info');
			return;
		}
		if (dmsList.length > 0 && dmsList[0].code == 200) {// 200:normal
			loadDmsList(dmsList, "dmsList");
		} else if (dmsList.length > 0 && dmsList[0].code == 404) {// 404:
			$('#createDmsList').html("");
			jQuery.messager.alert('提示:', "获取分拣中心列表为空！", 'info');
		} else if (dmsList.length > 0 && dmsList[0].code == 20000) {// 20000:error
			$('#createDmsList').html("");
			jQuery.messager.alert('提示:', "获取分拣中心列表失败！", 'info');
		} else {
			$('#createDmsList').html("");
			jQuery.messager.alert('提示:', "数据异常！", 'info');
		}
	});
}

function initDms() {
	var url = $("#contextPath").val() + "/services/bases/dms";
	$.getJSON(url, function(data) {
		var dmsList = data;
		if (data == undefined || data == null) {
			jQuery.messager.alert('提示:', "HTTP请求无数据返回！", 'info');
			return;
		}
		if (dmsList.length > 0 && dmsList[0].code == 200) {// 200:normal
			loadDmsList(dmsList, "destinationDmsList");
		} else if (dmsList.length > 0 && dmsList[0].code == 404) {// 404:
			jQuery.messager.alert('提示:', "获取分拣中心列表为空！", 'info');
		} else if (dmsList.length > 0 && dmsList[0].code == 20000) {// 20000:error
			jQuery.messager.alert('提示:', "获取分拣中心列表为空！", 'info');
		} else {
			jQuery.messager.alert('提示:', "数据异常！", 'info');
		}
	});
}

function loadDmsList(dmsList, selectId) {
	dmsList.sort(function(a, b) {
		if(a.siteCode != null && a.siteCode != "" && b.siteCode != null && b.siteCode != ""){
			return a.siteCode.toString().substring(0, 1) > b.siteCode.toString().substring(0, 1) ? 1 : -1;
		}
	});
	var dmsObj = $('#' + selectId);
	$('#createDmsList').html("");
	var optionList = "";
	optionList += "<option value='' selected='selected'></option>";
	for (var i = 0; i < dmsList.length; i++) {
		optionList += "<option value='" + dmsList[i].siteCode + "'>" + dmsList[i].siteCode + " " + dmsList[i].siteName + "</option>";
	}
	dmsObj.append(optionList);
	$("#paperTable tbody").html("");
}

function onQueryBtnClick(pageNo) {
	var params = getParams();
	params.pageNo = pageNo;
	if (!checkParams(params)) {
		jQuery.messager.alert('提示:','发货时间，批次号，分拣中心及审批状态，至少选择一个!','info'); 
		return false;
	}
	doQuery(params);
}

function checkParams(params) {
	if (null == params) {
		return false;
	}
	if (params.orgId == "" && params.createDmsCode == "" && params.destinationDmsCode == "" && params.createUserName == "") {
		return false;
	}
	return true;
}

function getParams() {
	var params = {};
	params.orgId = $.trim($("#orgList").val());
	params.createDmsCode = $.trim($("#createDmsList").val());
	params.destinationDmsCode = $.trim($("#destinationDmsList").val());
	params.createUserName = $.trim($("#createUserName").val());
	params.type = $.trim($("#typeList").val());
	return params;
}

// 查询请求
function doQuery(params) {
	var url = $("#contextPath").val() + "/loadBill/list";
	CommonClient.post(url, params, function(data) {
		if (data == undefined || data == null) {
			jQuery.messager.alert('提示:','HTTP请求无数据返回！','info');
			return;
		}
		if (data.code == 1) {
			// alert(JSON.stringify(data));
			var pager = data.data;
			var dataList = pager.data;
			var temp = "";
			for (var i = 0; i < dataList.length; i++) {
				temp += "<tr class='a2' style=''>";
				temp += "<td>" + (i + 1) + "</td>";
				temp += "<td>" + (dataList[i].createDmsCode) + "</td>";
				temp += "<td>" + (dataList[i].createDmsName) + "</td>";
				temp += "<td>" + (dataList[i].destinationDmsCode) + "</td>";
				temp += "<td>" + (dataList[i].destinationDmsName) + "</td>";
				var type = dataList[i].type;
				if (type == 10) {
					temp += "<td>建包</td>";
				} else if (type == 20) {
					temp += "<td>发货</td>";
				} else {
					temp += "<td>非法</td>";
				}
				temp += "<td>" + (dataList[i].mixDmsCode) + "</td>";
				temp += "<td>" + (dataList[i].mixDmsName) + "</td>";
				temp += "<td>" + (dataList[i].createUserName) + "</td>";
				temp += "<td>" + (getDateString(getData(dataList[i].createTime))) + "</td>";
				temp += "<td>" + "<input type='button' value='删除' onclick='crossSortingDelete(" + dataList[i].id + ")'>" + "</td>";
				temp += "</tr>";
			}
			$("#paperTable tbody").html(temp);
			$("#pageNo").val(pager.pageNo); // 当前页码
			// 添加分页显示
			$("#pager").html(PageBar.getHtml("onQueryBtnClick", pager.totalSize, pager.pageNo, pager.totalNo));
		} else {
			jQuery.messager.alert('提示:', data.message, 'info');
		}
	});
}

// 将任务的状态和执行次数重置
function crossSortingDelete(id) {
	// alert(id);
	var params = {};
	params.id = $.trim(id);
	doCrossSortingDelete(params);
}

function doCrossSortingDelete(params) {
	var url = $("#contextPath").val() + "/crossSorting/delete";
	CommonClient.post(url, params, function(data) {
		if (data == undefined || data == null) {
			jQuery.messager.alert('提示:','HTTP请求无数据返回！','info');
			return;
		}
		if (data.code == 1) {// 1:normal
			//对当前页面做重新查询
			onQueryBtnClick($.trim($("#pageNo").val()));
			//jQuery.messager.alert('提示:','删除成功','info');
		} else {// 0:exception,2:warn
			jQuery.messager.alert('提示:', data.message, 'info');
		}
	});
}

function goAddBtnClick() {
	location.href = $("#contextPath").val() + "/crossSorting/goAddBatch";
}

function getData(value) {
	if (value == null || value == "") {
		return "";
	} else {
		return value;
	}
}


//----------------------

function getNum(str) {
	var num = $.trim($("#" + str).val());
	if (num == undefined || num == null || num == "") {
		return 0;
	} else {
		return parseInt(num);
	}
}

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
