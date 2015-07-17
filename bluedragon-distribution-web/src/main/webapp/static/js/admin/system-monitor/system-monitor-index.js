//初始化
$(document).ready(main);

var dataArray = [];//页面的数据
var dataItem = {};//正在修改的数据
var redisQueue = [];//redis-queue配置信息

function main(){
	
	$('#searchBtn').click(function() {
		onSearchBtnClick();
	});
	
	$('#addBtn').click(function() {
		onAddBtnClick();
	});
	
	$('#removeBtn').click(function() {
		onRemoveBtnClick();
	});
	
	$('#sumBtn').click(function() {
		onSumBtnClick();
	});
	
	$('#reloadBtn').click(function() {
		onReloadBtnClick();
	});
	
	//查询按钮提交处理
	$('#queryBtn').click(function() {
		onQueryBtnClick();
	});
	
	$('#selectQueryBtn').click(function() {
		onSelectQueryBtnClick();
	});
	
	$('#move2DBBtn').click(function() {
		onMove2DBBtnClick();
	});
	
	$('#queryDetailBtn').click(function() {
		onQueryDetailBtnClick();
	});
	
	initPage();
}

function initPage(){
	
	initRedisTable();
	initRedisQueueTable();
	initRedisDetailTable();
	
	var url = $("#contextPath").val() + "/static/js/admin/system-monitor/redis-queue.json";
	$.getJSON(url, function(data) {
		redisQueue = data;
		$('#redisQueueTable').datagrid('loadData',redisQueue);//刷新页面数据
	});
}

function initRedisTable(){
	//初始化表格
	$('#redisTable').datagrid({
		toolbar:'#redisTable-tb',
		fit:true,
		fitColumns: true,
		rownumbers:true,
		showFooter: true,
		singleSelect:true,
		collapsible:true,
		sortName:'count', 
		sortOrder:'asc',
	    columns:[[
	        {field:'queue',title:'Queue',width:300,align:'right',formatter:function(value,rec){
				return '<A href="javascript:viewDetail(\'' +value+ '\')">'+value+'</A>';
			}},
	        {field:'count',title:'Count',width:100,sortable:'true'}
	    ]]  
	}); 
	
	$(window).resize(function(){
		$('#redisTable').datagrid('resize');
	});
}

function initRedisQueueTable(){
	//初始化表格
	$('#redisQueueTable').datagrid({
		fitColumns: true,
		rownumbers:true,
		singleSelect:true,
	    columns:[[
	        {field:'qName',title:'qName',width:300,align:'right'},
	        {field:'qSign',title:'qSign',width:80},
	        {field:'qSize',title:'qSize',width:80}
	    ]]  
	}); 
	
	$(window).resize(function(){
		$('#redisQueueTable').datagrid('resize');
	});
}

function initRedisDetailTable(){
	//初始化表格
	$('#redisDetailTable').datagrid({
		toolbar:'#redisDetailTable-tb',
		fit:true,
		fitColumns: true,
		rownumbers:true,
		singleSelect:true,
		collapsible:true,
	    columns:[[
	        {field:'detail',title:'Redis Queue Detail',width:100}
	    ]],
	    onSelect:function(rowIndex, rowData){
	    	$("#detailData").val(rowData.detail);
        }
	});
	
	$(window).resize(function(){
		$('#redisDetailTable').datagrid('resize');
	});
}

function onSearchBtnClick(){
	var queue = $.trim($("#queue").val());
	if(queue == ""){
		alert("请输入队列名称[Queue]!");
		$("#queue").focus();
		return false;
	}
	var row = {};
	var rows = $('#redisTable').datagrid('getRows');
	if(rows != null && rows.length > 0){
		for ( var i = 0; i < rows.length; i++) {
			row = rows[i];
			if(row.queue == queue){
				var index =  $('#redisTable').datagrid('getRowIndex', row);
				$('#redisTable').datagrid('selectRow', index);
				$('#redisTable').datagrid('scrollTo', index);
				break;
			}
		}
	}
}

function onAddBtnClick(){
	var obj = {};
	obj.queue = $.trim($("#queue").val());
	obj.count = 0;
	
	if(obj.queue == ""){
		alert("请输入队列名称[Queue]!");
		$("#queue").focus();
		return false;
	}
	dataArray.push(obj);
	
	$('#redisTable').datagrid('appendRow',obj);
	var lastIndex = $('#redisTable').datagrid('getRows').length-1;
	$('#redisTable').datagrid('selectRow', lastIndex);
	$('#redisTable').datagrid('scrollTo', lastIndex);
}

function onRemoveBtnClick(){
	var row = $('#redisTable').datagrid('getSelected');
	if (row){
		var index = $('#redisTable').datagrid('getRowIndex', row);
		$('#redisTable').datagrid('deleteRow', index);
		
		var tmp_arr = [];
		$.each(dataArray,function(key,val){ //回调函数有两个参数,第一个是元素索引,第二个为当前值 
			if(row.queue != val.queue){
				tmp_arr.push(val);
			}
		});
		dataArray = tmp_arr;
	}
}

function onSumBtnClick(){
	var rows = $('#redisTable').datagrid('getRows');
	if(rows == null || rows.length < 1){
		return;
	}
	var sum = 0;
	for ( var i = 0; i < rows.length; i++) {
		sum += rows[i].count;
	}
	alert("求和共计["+sum+"]条数据!");
}

function onReloadBtnClick(){
	var rows = $('#redisTable').datagrid('getRows');
	if(rows == null || rows.length < 1){
		return;
	}
	
	var tmp = [];
	for ( var i = 0; i < rows.length; i++) {
		row = rows[i];
		tmp.push(row.queue);
	}
	var param = {};
	param.qName = tmp.join(",");
//	alert("--->param:" + $.param(param));
	doQueryRedisQueueCount(param);
}

function onQueryBtnClick(){
	var param = {};
	param.qName = $.trim($("#qName").val());
	param.qSign = $.trim($("#qSign").val());
	param.qSize = $.trim($("#qSize").val());
	
	if(param.qName == ""){
		alert("请输入队列名称[qName]!");
		$("#qName").focus();
		return false;
	}
//	alert("--->param:" + $.param(param));
	doQueryRedisQueueCount(param);
}

function onSelectQueryBtnClick(){
	var row = $('#redisQueueTable').datagrid('getSelected');
	if (row){
		doQueryRedisQueueCount(row);
	}
}

function onMove2DBBtnClick(){
	
	var row = $('#redisQueueTable').datagrid('getSelected');
	if (row){
		if(!confirm("请确认危险操作!")) return;
		doMove2DB(row);
	}
}

//查询请求
function doQueryRedisQueueCount(param){
	var url = $("#contextPath").val() + "/admin/system-monitor/doQueryRedisQueueCount";
	CommonClient.post(url,param,function(data){
		if(data == undefined || data == null){
			alert("HTTP请求无数据返回！");
			return;
		}
		if(data.code == 1){//1:normal
//			alert(JSON.stringify(data));
			dataArray = data.data;
			refreshDataTable(dataArray);
		}
		else{//0:exception,2:warn
			alert(data.message);
		}
	});
}

//查询请求
function doMove2DB(param){
	var url = $("#contextPath").val() + "/admin/system-monitor/doMove2DB";
	CommonClient.post(url,param,function(data){
		if(data == undefined || data == null){
			alert("HTTP请求无数据返回！");
			return;
		}
			alert(data.message);
			doQueryRedisQueueCount(param);
	});
}

//刷新tabel数据显示
function refreshDataTable(data) {
	$('#redisTable').datagrid('loadData',data);//刷新页面数据
	
//	var sum = 0;
//	for ( var i = 0; i < data.length; i++) {
//		sum += data[i].count;
//	}
//	var footer = {};
//	footer.queue = "总计：";
//	footer.count = sum;
	
//	var table_data = {};
//	table_data.total = data.length;
//	table_data.rows = data;
//	table_data.footer = footer;
	
//	$('#redisTable').datagrid('loadData',data);//刷新页面数据
}

function viewDetail(queue){
	$("#queueKey").val(queue);
	$('#redisDetailDlg').dialog('open');
	onQueryDetailBtnClick();
}

function onQueryDetailBtnClick(){
	var param = {};
	param.queueKey = $.trim($("#queueKey").val());
	param.currentPage = $.trim($("#currentPage").val());
	param.pageSize = 100;
	if(param.queueKey == ""){
		alert("请输入队列名称[Queue]!");
		$("#queueKey").focus();
		return false;
	}
	if(param.currentPage == "" || param.currentPage < 1){
		param.currentPage = 1;
	}
//	alert("--->param:" + $.param(param));
	doQueryRedisQueueDetail(param);
}

function doQueryRedisQueueDetail(param){
	$("#detailData").val("");
	refreshDetailTable([]);//清空页面数据
	
	var url = $("#contextPath").val() + "/admin/system-monitor/doQueryRedisQueueDetail";
	CommonClient.post(url,param,function(data){
		if(data == undefined || data == null){
			alert("HTTP请求无数据返回！");
			return;
		}
		if(data.code == 1){//1:normal
//			alert(JSON.stringify(data));
			refreshDetailTable(data.data);
		}
		else{//0:exception,2:warn
			alert(data.message);
		}
	});
}

function refreshDetailTable(data) {
	var tmp = [];
	for ( var i = 0; i < data.length; i++) {
		var item = {};
		item.detail = data[i];
		tmp.push(item);
	}
	$('#redisDetailTable').datagrid('loadData',tmp);//刷新页面数据
}
