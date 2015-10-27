//初始化
$(document).ready(main);

function main() {

	// 查询按钮提交处理
	$('#queryBtn').click(function() {
		onQueryBtnClick(1);
	});

	// 全选框绑定点击事件
	$("#allBtn").click(function() {
		selectAll();
	});

	$("#initialBtn").click(function() {
		initialLoadBill();
	});

	$("#preLoadBtn").click(function() {
		preLoad();
	});

	$("#preLoadCancelBtn").click(function() {
		preLoadCancel();
	});

	// 加载所有的分拣中心
	initDms();
}

// 根据批次号,初始化装载单
function initialLoadBill() {
	var params = {};
	params.sendCode = $.trim($("#sendCode").val());
	if(params.sendCode == null || params.sendCode == ""){
		jQuery.messager.alert('提示:', "批次号不能为空！", 'info');
		return;
	}
	var url = $("#contextPath").val() + "/globalTrade/loadBill/initial";
	CommonClient.post(url, params, function(data) {
		if (data == undefined || data == null) {
			jQuery.messager.alert('提示:', "HTTP请求无数据返回！", 'info');
			return;
		}
		if (data.code == 1) {
			$("#paperTable tbody").html("");
			$("#pager").html("");
			jQuery.messager.alert('提示:', "该批次的装载单初始化成功！", 'info');
		} else {
			jQuery.messager.alert('提示:', data.message, 'error');
		}
	});
}

// 预装载后,重新查询一遍
function preLoad() {
    var reg = /^[A-Za-z0-9\u4e00-\u9fa5]+$/;
    var truckNo = $("#truckNo").val();
    if(reg.test(truckNo) == false){
        jQuery.messager.alert('提示:', "请输入正确的车牌号！", 'error');
        return;
    }
    var loadBillId = new Array();
    var loadBillLen = 0;
    try{
    $(".a2 :input[type='checkbox']").each(function(){
        if($(this).val() != 10){
            throw new Error("error");
        }
        if($(this).attr("checked") != null){
            loadBillId[loadBillLen] = $(this).attr("id");
            loadBillLen = loadBillLen + 1;
        }
    });
    }catch(e){
        jQuery.messager.alert('提示:', "所选装载单的审批状态必须是初始化！", 'error');
        return;
    }
    if(loadBillLen > 0) {
        var loadBillIdStr = loadBillId.join(",");
        $.ajax({
            type: 'POST',
            url:  $("#contextPath").val() + "/globalTrade/preload",
            data: {"carCode":truckNo,"loadBillId":loadBillIdStr},
            success: function(data){
                var json = eval(data);
                if(json.status == 200){
                    jQuery.messager.alert('提示:', json.notes, 'info');
                }else{
                    jQuery.messager.alert('提示:', json.notes, 'error');
                }
            },
            dataType: "json"
        });
    }else{
        jQuery.messager.alert('提示:', "请至少选择一个装载单！", 'error');
        return;
    }
}

// 取消预装载后,重新查询一遍
function preLoadCancel() {
	var singleBtns = $("input[name='singleBtn']:checked");
	if(singleBtns == null || singleBtns.length < 1){
		jQuery.messager.alert('提示:', "至少选择 1 条数据!", 'info');
		return;
	}
	if(singleBtns.length > 2000){
		jQuery.messager.alert('提示:', "最多选择 2000 条数据!", 'info');
		return;
	}
	//校验:保证选择的装载单的审批状态均为40
	var ids = "";
	var first = true;
	for(var i = 0; i < singleBtns.length; i++){
		if(singleBtns[i].value != 40){
			jQuery.messager.alert('提示:', "所选装载单的审批状态必须是未放行!", 'info');
			return;
		}
		if(first){
			ids += singleBtns[i].id;
			first = false;
		}else{
			ids += "," +  singleBtns[i].id;
		}
	}
	var url = $("#contextPath").val() + "/globalTrade/cancel";
	var params = {};
	params.ids = ids;
	CommonClient.post(url, params, function(data) {
		if (data == undefined || data == null) {
			jQuery.messager.alert('提示:', 'HTTP请求无数据返回！', 'info');
			return;
		}
		if (data.code == 1) {
			jQuery.messager.alert('提示:', "取消预分拣成功！", 'info');
		} else {
			jQuery.messager.alert('提示:', data.notes, 'error');
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
			loadDmsList(dmsList, "dmsList");
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
		if (a.siteCode != null && a.siteCode != "" && b.siteCode != null && b.siteCode != "") {
			return a.siteCode.toString().substring(0, 1) > b.siteCode.toString().substring(0, 1) ? 1 : -1;
		}
	});
	var dmsObj = $('#' + selectId);
	$('#dmsList').html("");
	var optionList = "";
	optionList += "<option value='' selected='selected'></option>";
	for (var i = 0; i < dmsList.length; i++) {
		optionList += "<option value='" + dmsList[i].siteCode + "'>" + dmsList[i].siteCode + " " + dmsList[i].siteName + "</option>";
	}
	dmsObj.append(optionList);
}

function onQueryBtnClick(pageNo) {
	var params = getParams();
	params.pageNo = pageNo;
	if (!checkParams(params)) {
		jQuery.messager.alert('提示:', '发货时间，批次号，分拣中心及审批状态，至少选择一个!', 'info');
		return false;
	}
	doQuery(params);
}

function checkParams(params) {
	if (null == params) {
		return false;
	}
	if (params.sendTimeFrom == "" && params.sendTimeTo == "" && params.sendCode == "" && params.dmsCode == "" && params.approvalCode == "") {
		return false;
	}
	return true;
}

function getParams() {
	var params = {};
	params.sendTimeFrom = $.trim($("#sendTimeFrom").val());
	params.sendTimeTo = $.trim($("#sendTimeTo").val());
	params.sendCode = $.trim($("#sendCode").val());
	params.dmsCode = $.trim($("#dmsList").val());
	params.approvalCode = $.trim($("#approvalCode").val());
	params.pageSize = $.trim($("#pageSize").val());
	return params;
}

// 查询请求
function doQuery(params) {
	var url = $("#contextPath").val() + "/globalTrade/loadBill/list";
	CommonClient.post(url, params, function(data) {
		if (data == undefined || data == null) {
			jQuery.messager.alert('提示:', 'HTTP请求无数据返回！', 'info');
			return;
		}
		if (data.code == 1) {
            resetSelectAll();
			var pager = data.data;
			var dataList = pager.data;
			var temp = "";
			for (var i = 0; i < dataList.length; i++) {
				temp += "<tr class='a2' style=''>";
				temp += "<td><input id='" + dataList[i].id + "' value='"+ dataList[i].approvalCode +"' name='singleBtn' alt=" + dataList[i].approvalCode + " onclick='singleClick()' type='checkbox'/></td>";
				temp += "<td>" + (dataList[i].loadId == null ? '' : dataList[i].loadId) + "</td>";
				temp += "<td>" + (dataList[i].waybillCode) + "</td>";
				temp += "<td>" + (dataList[i].packageBarcode) + "</td>";
				temp += "<td>" + (dataList[i].orderId) + "</td>";
				temp += "<td>" + (dataList[i].dmsName) + "</td>";
				temp += "<td>" + (getDateString(getData(dataList[i].sendTime))) + "</td>";
				temp += "<td>" + (dataList[i].sendCode) + "</td>";
				temp += "<td>" + (dataList[i].truckNo == null ? '' : dataList[i].truckNo) + "</td>";
				var type = dataList[i].approvalCode;
				if (type == 10) {
					temp += "<td>初始</td>";
				} else if (type == 20) {
					temp += "<td>已申请</td>";
				} else if (type == 30) {
					temp += "<td>放行</td>";
				} else {
					temp += "<td>未放行</td>";
				}
				temp += "<td>" + (getDateString(getData(dataList[i].approvalTime))) + "</td>";
				temp += "<td>" + (dataList[i].remark == null ? '' : dataList[i].remark) + "</td>";
				temp += "</tr>";
			}
			$("#paperTable tbody").html(temp);
			$("#pageNo").val(pager.pageNo); // 当前页码
			$("#pageSize").val(pager.pageSize);
			// 添加分页显示
			$("#pager").html(PageBar.getHtml("onQueryBtnClick", pager.totalSize, pager.pageNo, pager.totalNo));
		} else {
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

// --------- 全选框和单选框的互动 -------------

function selectAll() {
    var allchecked = null == $("#allBtn").attr("checked") ? false : true;
    $("#allBtn").attr("checked",allchecked);
    $(".a2 :input[type='checkbox']").each(function(){
       $(this).attr("checked",allchecked);
    });
}


function singleClick() {
  $("#allBtn").attr("checked",false);
}

function resetSelectAll(){
    $("#allBtn").attr("checked",false);
}

// ----------------------

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
