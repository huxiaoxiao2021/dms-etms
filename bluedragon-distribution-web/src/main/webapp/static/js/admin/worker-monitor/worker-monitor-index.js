//初始化
$(document).ready(main);

//全局变量
var taskTableList = null;

function main() {

	// 查询按钮提交处理
	$('#queryBtn').click(function() {
		onQueryBtnClick();
	});

	// 批量重置按钮提交处理
	$('#batchResetBtn').click(function() {
		taskBatchReset();
	});

	// 初始化任务表下拉框
	initTaskTableList();

}

function initTaskTableList() {
	var url = $("#contextPath").val() + "/static/js/admin/worker-monitor/worker-queue.json";
	$.getJSON(url, function(data) {
		taskTableList = data;
		var tableObj = $('#table_list');
		var optionList = "";
		for (var i = 0; i < taskTableList.length; i++) {
			optionList += "<option value='" + taskTableList[i].table_name + "'>" + taskTableList[i].table_name + "</option>";
		}
		tableObj.append(optionList);
	});
	$("#currentPage").val("");
}

function onQueryBtnClick() {
	var params = getParams();
	if (params.tableName == "空值") {
		alert("请输入参数[tableName]!");
		return false;
	}
	if (!comptime(params.startTime, params.endTime)) {
		alert("开始时间不能大于结束时间!");
		return false;
	}
	doQueryWorker(params);
}

// 比较时间大小
function comptime(startDate, endDate) {
	if (startDate != null && endDate != null && startDate.length > 0 && endDate.length > 0) {
		var startDateTemp = startDate.split(" ");
		var endDateTemp = endDate.split(" ");
		var arrStartDate = startDateTemp[0].split("-");
		var arrEndDate = endDateTemp[0].split("-");
		var arrStartTime = startDateTemp[1].split(":");
		var arrEndTime = endDateTemp[1].split(":");
		var allStartDate = new Date(arrStartDate[0], arrStartDate[1], arrStartDate[2], arrStartTime[0], arrStartTime[1], arrStartTime[2]);
		var allEndDate = new Date(arrEndDate[0], arrEndDate[1], arrEndDate[2], arrEndTime[0], arrEndTime[1], arrEndTime[2]);
		if (allStartDate.getTime() > allEndDate.getTime()) {
			return false;
		}
	}
	return true;
}

function getParams() {
	var params = {};
	params.tableName = $.trim($("#table_list").val());
	params.taskType = $.trim($("#type_list").val());
	params.taskStatus = $.trim($("#status_list").val());
	params.executeCount = $.trim($("#count_list").val());
	params.keyword1 = $.trim($("#keyword1").val());
	params.keyword2 = $.trim($("#keyword2").val());
	params.startTime = $.trim($("input[name='start_time']").val());
	params.endTime = $.trim($("input[name='end_time']").val());
	return params;
}

// 查询请求
function doQueryWorker(params) {
	var url = $("#contextPath").val() + "/admin/worker-monitor/list";
	CommonClient.post(url, params, function(data) {
		if (data == undefined || data == null) {
			alert("HTTP请求无数据返回！");
			return;
		}
		if (data.code == 1) {
			// alert(JSON.stringify(data));
			var pager = data.data;
			var taskList = pager.data;
			var temp = "";
			for (var i = 0; i < taskList.length; i++) {
				temp += "<tr class='a2' style=''>";
				temp += "<td>" + (i + 1) + "</td>";
				temp += "<td>" + (taskList[i].type) + "</td>";
				temp += "<td>" + (taskList[i].status) + "</td>";
				temp += "<td>" + (taskList[i].keyword1) + "</td>";
				temp += "<td>" + (taskList[i].keyword2) + "</td>";
				temp += "<td>" + (taskList[i].body) + "</td>";
				temp += "<td>" + (getDateString(getData(taskList[i].executeTime))) + "</td>";
				temp += "<td>" + (taskList[i].ownSign) + "</td>";
				temp += "<td>" + "<input type='button' value='重置' onclick='taskReset(" + taskList[i].id + ")'>" + "</td>";
			}
			$("#paperTable tbody").html("");
			$("#paperTable").append(temp);
			$("#pageNo").val(pager.pageNo); // 当前页码
			$("#totalNo").val(pager.totalNo); // 总页码 tableName
			$("#tableName").val(pager.tableName);
			$("#currentPage").val(pager.pageNo + "/" + pager.totalNo);
		} else {
			alert(data.message);
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

	if (params.tableName == "") {
		alert("请输入参数[tableName]!");
		return false;
	}
	doQueryWorker(params);
}

function goPrevPage() {
	var params = getParams();
	var pageNo = getNum("pageNo");
	params.pageNo = pageNo <= 1 ? 1 : pageNo - 1;
	// params.pageSize = $.trim($("#pageSize").val());

	if (params.tableName == "") {
		alert("请输入参数[tableName]!");
		return false;
	}
	doQueryWorker(params);
}

function goNextPage() {
	var params = getParams();
	var pageNo = getNum("pageNo");
	var totalNo = getNum("totalNo");
	params.pageNo = pageNo >= totalNo ? pageNo : pageNo + 1;
	// params.pageSize = $.trim($("#pageSize").val());

	if (params.tableName == "") {
		alert("请输入参数[tableName]!");
		return false;
	}
	doQueryWorker(params);
}

function goTailPage() {
	var params = getParams();
	params.pageNo = getNum("totalNo");
	// params.pageSize = $.trim($("#pageSize").val());

	if (params.tableName == "") {
		alert("请输入参数[tableName]!");
		return false;
	}
	doQueryWorker(params);
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

// 将任务的状态和执行次数重置
function taskReset(id) {
	// alert(id);
	var params = {};
	params.id = $.trim(id);
	params.tableName = $.trim($("#tableName").val());
	doTaskReset(params);
}

function doTaskReset(params) {
	var url = $("#contextPath").val() + "/admin/worker-monitor/doTaskReset";
	CommonClient.post(url, params, function(data) {
		if (data == undefined || data == null) {
			alert("HTTP请求无数据返回！");
			return;
		}
		if (data.code == 1) {// 1:normal
			var taskTypeList = data.data;
			alert("重置成功");
		} else {// 0:exception,2:warn
			alert(data.message);
		}
	});
}

function taskBatchReset() {
	var params = getParams();
	if (params.tableName == "") {
		alert("请输入参数[tableName]!");
		return false;
	}
	doTaskBatchReset(params);
}

function doTaskBatchReset(params) {
	var url = $("#contextPath").val() + "/admin/worker-monitor/doTaskBatchReset";
	CommonClient.post(url, params, function(data) {
		if (data == undefined || data == null) {
			alert("HTTP请求无数据返回！");
			return;
		}
		if (data.code == 1) {
			alert("批量重置成功");
		} else {
			alert(data.message);
		}
	});
}

// 实现与任务表名联动
function initTaskType() {
//	// select DISTINCT task_type from task_weight;
//	var url = $("#contextPath").val() + "/admin/worker-monitor/queryTaskTypeByTableName";
//	var param = {};
//	param.tableName = $.trim($("#table_list").val());
//	if ($.trim($("#table_list").val()) == "空值") {
//		$('#type_list').html("<option value='' selected='selected'>任务类型</option>");
//		return;
//	}
//	CommonClient.post(url, param, function(data) {
//		if (data == undefined || data == null) {
//			alert("HTTP请求无数据返回！");
//			return;
//		}
//		if (data.code == 1) {// 1:normal
//			var taskTypeList = data.data;
//			loadTaskTypeList(taskTypeList);
//		} else {// 0:exception,2:warn
//			loadTaskTypeList(null);
//			alert(data.message);
//		}
//	});
	var tableName = $.trim($("#table_list").val());
	for (var i = 0; i < taskTableList.length; i++) {
		if(null != tableName && taskTableList[i].table_name == tableName){
			var taskTypeList = taskTableList[i].task_type_list;
			loadTaskTypeList(taskTypeList);
			break;
		}
	}
}

function loadTaskTypeList(taskTypeList) {
	var typeObj = $('#type_list');
	$('#type_list').html("");
	var optionList = "";
	optionList += "<option value='' selected='selected'>任务类型</option>";
	if (taskTypeList != null) {
		for (var i = 0; i < taskTypeList.length; i++) {
			optionList += "<option value='" + taskTypeList[i].task_type + "'>" + taskTypeList[i].task_type + "</option>";
		}
	}
	typeObj.append(optionList);
	$("#currentPage").val("");
	$("#paperTable tbody").html("");
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