//初始化
$(document).ready(main);

var dataArray = [];// 页面的数据
var dataItem = {};// 正在修改的数据
var redisQueue = [];// redis-queue配置信息

function main() {

	// 查询按钮提交处理
	$('#queryBtn').click(function() {
		onQueryBtnClick();
	});

	// 清除按钮提交处理
	$('#clearBtn').click(function() {
		onClearBtnClick();
	});

	initRedisTable();

}

function initRedisTable() {
	// 初始化表格
	$('#redisTable').datagrid({
		toolbar : '#redisTable-tb',
		fit : true,
		fitColumns : true,
		rownumbers : true,
		showFooter : true,
		singleSelect : true,
		collapsible : true,
		// sortName:'key',
		// sortOrder:'asc',
		columns : [ [ {
			field : 'key',
			title : 'Key',
			width : 200,
			align : 'right'
		}, {
			field : 'length',
			title : 'Length',
			width : 200,
			sortable : 'true'
		} ] ]
	});

	$(window).resize(function() {
		$('#redisTable').datagrid('resize');
	});
}

function onQueryBtnClick() {
	var param = {};
	param.key = $.trim($("#key").val());

	if (param.key == "") {
		alert("请输入参数[Key]!");
		$("#key").focus();
		return false;
	}
	// alert("--->param:" + $.param(param));
	doQueryRedisQueueCount(param);
}

// 查询请求
function doQueryRedisQueueCount(param) {
	var url = $("#contextPath").val() + "/admin/redis-monitor/queryValueByKey";
	CommonClient.post(url, param, function(data) {
		if (data == undefined || data == null) {
			alert("HTTP请求无数据返回！");
			return;
		}
		if (data.code == 1) {// 1:normal
			// alert(JSON.stringify(data));
			dataArray = data.data;
			refreshDataTable(dataArray);
		} else {// 0:exception,2:warn
			alert(data.message);
		}
	});
}

// 刷新tabel数据显示
function refreshDataTable(data) {
	$('#redisTable').datagrid('loadData', data);// 刷新页面数据
}

function onClearBtnClick() {
	var param = {};
	param.key = $.trim($("#key").val());

	if (param.key == "") {
		alert("请输入参数[Key]!");
		$("#key").focus();
		return false;
	}
	// alert("--->param:" + $.param(param));
	doClearRedisData(param);
}

// 清除请求
function doClearRedisData(param) {
	var url = $("#contextPath").val() + "/admin/redis-monitor/deleteByKey";
	CommonClient.post(url, param, function(data) {
		if (data == undefined || data == null) {
			alert("HTTP请求无数据返回！");
			return;
		}
		if (data.code == 1) {// 1:normal
			// 重新根据当前key查询一遍
			onQueryBtnClick();
		} else {// 0:exception,2:warn
			alert(data.message);
		}
	});
}
