//初始化
$(document).ready(main);

function main() {

	// 给保存按钮注册点击事件
	$('#saveBtn').click(function() {
		doAddBtnClick();
	});

	$('#cancelBtn').click(function() {
		goList(); // 跳转到查询页面
	});

	// 初始化任务表下拉框
	initOrg();

	// 加载所有的分拣中心
	initDms();

}

function initOrg() {
	var url = $("#contextPath").val() + "/services/bases/allorgs";
	var param = {};
	$.getJSON(url, function(data) {
		// data --> List<BaseResponse>
		var orgList = data;
		var tableObj = $('#orgList');
		var optionList = "";
		optionList += "<option value='' selected='selected'></option>";
		for (var i = 0; i < orgList.length; i++) {
			if (orgList[i].orgId != -100) {
				optionList += "<option value='" + orgList[i].orgId + "'>" + orgList[i].orgName + "</option>";
			}
		}
		tableObj.append(optionList);
	});
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
			loadDmsList(dmsList, "createDmsList");
			$('#mixDmsTable tbody').html("");
		} else if (dmsList.length > 0 && dmsList[0].code == 404) {// 404:
			$('#createDmsList').html(""); // 清空建包/发货分拣中心
			$('#mixDmsTable tbody').html(""); // 清空可混装分拣中心
			jQuery.messager.alert('提示:', "获取分拣中心列表为空！", 'info');
		} else if (dmsList.length > 0 && dmsList[0].code == 20000) {// 20000:error
			$('#createDmsList').html("");
			$('#mixDmsTable tbody').html("");
			jQuery.messager.alert('提示:', "获取分拣中心列表失败！", 'info');
		} else {
			$('#createDmsList').html("");
			$('#mixDmsTable tbody').html("");
			jQuery.messager.alert('提示:', "数据异常！", 'info');
		}
	});
}

function loadDmsList(dmsList, selectId) {
	dmsList.sort(function(a, b) {
		if (a.siteCode != null && a.siteCode != "" && b.siteCode != null && b.siteCode != "") {
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
			loadDmsList(dmsList, "mixDmsList");
		} else if (dmsList.length > 0 && dmsList[0].code == 404) {// 404:
			jQuery.messager.alert('提示:', "获取分拣中心列表为空！", 'info');
		} else if (dmsList.length > 0 && dmsList[0].code == 20000) {// 20000:error
			jQuery.messager.alert('提示:', "获取分拣中心列表失败！", 'info');
		} else {
			jQuery.messager.alert('提示:', "数据异常！", 'info');
		}
	});
}

// 发货分拣中心,目的分拣中心,规则类型变动,重载加载列表(如果有一个为空,就清空列表)
function initMixDmsList() {
	// 获取参数
	var params = getParams();
	$('#mixDmsTable tbody').html(""); // 清空可混装分拣中心
	if (params.createDmsCode == null || params.createDmsCode == "" || params.destinationDmsCode == null || params.destinationDmsCode == "" || params.type == null || params.type == "") {
		return;
	}
	loadMixDmsList(params);
}

function loadMixDmsList(params) {
	initMixDmsTable();
	var url = $("#contextPath").val() + "/crossSorting/mixDms";
	CommonClient.post(url, params, function(data) {
		if (data == undefined || data == null) {
			jQuery.messager.alert('提示:', "HTTP请求无数据返回！", 'info');
			return;
		}
		if (data.code == 1) {
			// alert(JSON.stringify(data));
			var mixDmsList = data.data;
			var temp = "";
			temp += "<tbody align='center'>";
			for (var i = 0; i < mixDmsList.length; i++) {
				temp += "<tr>";
				temp += "<td align='center'>" + (mixDmsList[i].mixDmsCode) + "</td>";
				temp += "<td align='center'>" + (mixDmsList[i].mixDmsName) + "</td>";
				temp += "</tr>";
			}
			temp += "</tbody>";
			$('#mixDmsTable').append(temp);
		} else {
			// alert(data.message);
		}
	});
}

function initMixDmsTable() {
	$('#mixDmsTable thead').html("");
	$('#mixDmsTable tbody').html("");
	var originalTable = "<thead align='center'><tr align='right' ><td align='center'>可混装分拣中心ID</td><td align='center'>可混装分拣中心</td></tr>";
	$('#mixDmsTable').append(originalTable);
}

function getParams() {
	var params = {};
	params.createDmsCode = $.trim($("#createDmsList").val());
	params.destinationDmsCode = $.trim($("#destinationDmsList").val());
	params.type = $.trim($("#typeList").val());
	return params;
}

// 混装分拣中心变动,对table做添加操作(同时做重复校验,重复则不添加)
function addMixDms() {
	// 获取table所有的mixDmsCode
	var mixArray = new Array();
	$("#mixDmsTable tr td:nth-child(1)").each(function() {
		var child = $(this).text();
		if (null != child && child != "") {
			mixArray.push(child);
		}
	});

	// 可混装分拣中心不能超过10个
	if (mixArray.length >= 11) {
		jQuery.messager.alert('提示:', "可混装分拣中心不能超过10个！", 'info');
		return;
	}

	// 过滤重复code,如果重复则不做操作
	var newMixCode = $.trim($("#mixDmsList").val());
	if (null == newMixCode || newMixCode == "") {
		return;
	}
	for (var i = 1; i < mixArray.length; i++) {
		if (mixArray[i] == newMixCode) {
			return;
		}
	}

	// 判断mixDmsCode是否与createDmsCode和destinationDmsCode重复,重复则不操作
	// 目的分拣中心 和 混装分拣中心 可以重复 (变更)
	var createDmsCode = $.trim($("#createDmsList").val());
	var destinationDmsCode = $.trim($("#destinationDmsList").val());
	if (null == createDmsCode || createDmsCode == "") {
		return;
	}
	if (newMixCode == createDmsCode) {
		return;
	}
	
	// createDmsCode 和  destinationDmsCode不能重复
	if(createDmsCode != null && destinationDmsCode != null && createDmsCode == destinationDmsCode){
		jQuery.messager.alert('提示:', "建包/发货分拣中心与目的分拣中心不能重复！", 'info');
		return;
	}

	// 追加table
	var newMixName = $("#mixDmsList").find("option:selected").text();
	if (null == newMixName || newMixName == "") {
		return;
	}
	var newRow = "<tr><td align='center'>" + newMixCode + "</td><td align='center'>" + getName(newMixName) + "</td></tr>";
	$("#mixDmsTable tr:last").after(newRow); 
}

// 批量添加
function doAddBtnClick() {
	// 获取orgId,createDmsCode,createDmsName,destinationDmsCode,destinationDmsName,type
	var params = getAddParams();
	if (checkParams(params)) {
		return false;
	}
	var url = $("#contextPath").val() + "/crossSorting/addBatch";
	CommonClient.post(url, params, function(data) {
		if (data == undefined || data == null) {
			jQuery.messager.alert('提示:', "HTTP请求无数据返回！", 'info');
			return;
		}
		if (data.code == 1) {
			location.href = $("#contextPath").val() + "/crossSorting";
		} else {
			jQuery.messager.alert('提示:', data.message, 'info');
		}
	});
}

function getAddParams() {
	var params = {};
	params.orgId = $.trim($("#orgList").val());
	params.createDmsCode = $.trim($("#createDmsList").val());
	params.createDmsName = getName($("#createDmsList").find("option:selected").text());
	params.destinationDmsCode = $.trim($("#destinationDmsList").val());
	params.destinationDmsName = getName($("#destinationDmsList").find("option:selected").text());
	params.type = $.trim($("#typeList").val());

	// 获取可混装分拣中心
	var data = new Array();
	var mixDmsCodeArray = new Array();
	var mixDmsNameArray = new Array();
	$("#mixDmsTable tr td:nth-child(1)").each(function() {
		var child = $(this).text();
		if (null != child && child != "") {
			mixDmsCodeArray.push(child);
		}
	});
	$("#mixDmsTable tr td:nth-child(2)").each(function() {
		var child = $(this).text();
		if (null != child && child != "") {
			mixDmsNameArray.push(child);
		}
	});
	// alert(mixDmsCodeArray);
	// alert(mixDmsNameArray);
	for (var i = 1; i < mixDmsCodeArray.length; i++) {
		var mixDms = {};
		mixDms.mixDmsCode = mixDmsCodeArray[i];
		mixDms.mixDmsName = mixDmsNameArray[i];
		data.push(mixDms);
	}
	// params.data = data;
	// params.data =
	// "[{\"mixDmsCode\":123,\"mixDmsName\":\"zhangsan\"},{\"mixDmsCode\":456,\"mixDmsName\":\"zhangsan\"}]";
	// alert(JSON.stringify(data));
	params.data = JSON.stringify(data);
	return params;
}

function getName(name) {
	if (null == name || name == "") {
		return "";
	}
	var nameArray = name.split(" ");
	if (nameArray.length >= 2) {
		name = nameArray[1];
	} else {
		name = nameArray[0];
	}
	return name;
}

function checkParams(params) {
	if (params.orgId == null || params.orgId == "" || params.createDmsCode == null || params.createDmsCode == "" || params.createDmsName == null || params.createDmsName == ""
			|| params.destinationDmsCode == null || params.destinationDmsCode == "" || params.destinationDmsName == null || params.destinationDmsName == "" || params.type == null || params.type == "") {
		jQuery.messager.alert('提示:', "参数不能为空", 'info');
		return true;
	}
	return false;
}

// 跳转到查询页面
function goList() {
	location.href = $("#contextPath").val() + "/crossSorting";
}
