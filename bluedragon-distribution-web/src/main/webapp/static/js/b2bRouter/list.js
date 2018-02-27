function main() {
	$("#originateOrg").change(function() {
		$("#originalDmsName").val("");
		$("#originalDmsId").val("");
		$("#originalDmsName").unautocomplete();
		var orgId = $("#originateOrg").val();
		getSiteData(1, orgId);
	});
	$("#destinationOrg").change(function() {
		$("#destinationDmsName").val("");
		$("#destinationDmsId").val("");
		$("#destinationDmsName").unautocomplete();
		var orgId = $("#destinationOrg").val();
		getSiteData(2, orgId);
	});
	$("#transferOrg").change(function() {
		$("#transferName").val("");
		$("#transferId").val("");
		$("#transferName").unautocomplete();
		var orgId = $("#transferOrg").val();
		getSiteData(3, orgId);
	});

	// 初始化任务表下拉框
	initB2BSite();

	// 初始化列表，加载所有信息
	queryBtn(1);
}


// 初始化始发网点和目的网点
function initB2BSite() {
	var url = $("#contextPath").val() + "/services/bases/allB2BSites";
	var param = {};
	$.getJSON(url, function(data) {
		var B2BSiteList = data;
		var tableObj_originateOrg = $('#originateB2BSite');
		var tableObj_destinationOrg = $('#destinationB2BSite');
		var optionList;
		for (var i = 0; i < B2BSiteList.length; i++) {
			if (B2BSiteList[i].orgId != -100) {
				optionList += "<option value='" + B2BSiteList[i].orgId + "')>"
						+ B2BSiteList[i].orgName + "</option>";
			}
		}
		tableObj_originateOrg.append(optionList);
		tableObj_destinationOrg.append(optionList);
	});
}




//查询
function queryBtn(pageNo) {
	var params = getParams();
	params.pageNo = pageNo;
	doQuery(params);
}

//新增
function addBtn() {
	var contextPath = $("#contextPath").val();
	location.href = contextPath + "/b2bRouter/toAdd";
}

//修改
function changeBtn() {
	// var checkedKeys = $("paperTable tbody input[type=checkbox][name= 'record']:checked");
	// if(checkedKeys) {
	// 	if (checkedKeys.length != 1) {
	// 		showTipDialog("danger", "<strong>请只选择一条记录来编辑</strong>");
	// 	} else {
	// 		$.blockUI({
	// 			message: '<h1>Processing</h1>',
	// 			css: { border: '3px solid #a00' }
	// 		});
	// 		var parameter = {};
	// 		parameter[setting.primaryName] = $(checkedKeys[0]).val();
	// 		$.ajax({
	// 			type:"post",
	// 			dataType:"json",
	// 			url:setting.rUrl,
	// 			data:parameter,
	// 			timeout: 3000,
	// 			success: function(result, textStatus, jqXHR){
	// 				$.unblockUI();
	// 				if(result && result.success && result.data) {
	// 					var formFields = $("#dialogBodyContent form input,#dialogBodyContent form select,#dialogBodyContent form textarea");
	// 					if (formFields && formFields.length > 0) {
	// 						for (property in result.data) {
	// 							for (i = 0 ; i < formFields.length; i++) {
	// 								var formField = formFields[i];
	// 								if (property == $(formField).attr("name")) {
	// 									if ($(formField).is("input")) {
	// 										if ($(formField).attr("type") == "checkbox") {
	// 											if(result.data[property] == true) {
	// 												$(formField).attr("checked", "checked");
	// 											} else {
	// 												$(formField).removeAttr("checked");
	// 											}
	// 										} else if ($(formField) instanceof Array && $(formField[0]).attr("type") == "radio") {
	// 											for (radioFormField in $(formField)) {
	// 												if ($(radioFormField).val() == result.data[property]) {
	// 													$(radioFormField).attr("checked", "checked");
	// 												} else {
	// 													$(radioFormField).removeAttr("checked");
	// 												}
	// 											}
	// 										} else {
	// 											$(formField).val(result.data[property]);
	// 										}
	// 									} else if ($(formField).is("select")) {
	// 										$(formField).val(result.data[property]+"");
	// 									} else if ($(formField).is("textarea")) {
	// 										$(formField).text(result.data[property]);
	// 									}
	// 								}
	// 							}
	// 						}
	// 					}
	// 					if(setting.disableFields && setting.disableFields.length > 0) {
	// 						for (var i = 0 ; i < setting.disableFields.length; i++) {
	// 							var readonlyFields = $(
	// 								"#dialogBodyContent form input[name="+setting.disableFields[i]+"]," +
	// 								"#dialogBodyContent form select[name="+setting.disableFields[i]+"]," +
	// 								"#dialogBodyContent form textarea[name="+setting.disableFields[i]+"]");
	// 							$(readonlyFields).attr("readonly", "readonly");
	// 						}
	// 					}
	// 					$("#dialogBodyContent form [crud]:not([crud=u])").remove();
	// 					if (setting.dialogShowBefore) {
	// 						setting.dialogShowBefore($("#dialogBodyContent"));
	// 					}
	// 					$("#dialog").modal("show");
	// 				} else {
	// 					showTipDialog("info", "<strong>没有找到对应记录</strong>");
	// 				}
	// 			},
	// 			error: function(XMLHttpRequest, textStatus, errorThrown) {
	// 				$.unblockUI();
	// 				showHttpError(XMLHttpRequest);
	// 			}
	// 		});
	// 	}
	// }
	// var contextPath = $("#contextPath").val();
	// location.href = contextPath + "/b2bRouter/toAdd";
}

//删除
function deleteBtn() {
	var contextPath = $("#contextPath").val();
	location.href = contextPath + "/b2bRouter/delete?id="+id;
}

//导入
function importBtn() {
	var contextPath = $("#contextPath").val();
	location.href = contextPath + "/base/crossbox/toAdd";
}


function getSiteData(index, orgId) {
	var contextPath = $("#contextPath").val();
	var slefSites = "";
	var selfSiteArray;
	var url = contextPath + "/services/bases/dms/";
	if (orgId == -1) {
	} else {
		url = url + orgId;
	}
	jQuery.ajax({
		type : "GET",
		url : url,
		data : {
			orgId : orgId
		},
		success : function(msg) {
			jQuery.each(msg, function(infoIndex, info) {
				if (info.code == 200) {
					if (infoIndex == 0) {
						slefSites = getSites(info);
					} else {
						slefSites = getSites(info) + " " + slefSites;
					}
				}
			});
			selfSiteArray = slefSites.split(" ");
			if (index == 1) {
				orginalDms(selfSiteArray);
			} else if (index == 2) {
				destinationDms(selfSiteArray);
			} else if (index == 3) {
				transferDms(selfSiteArray);
			} else {
				orginalDms(selfSiteArray);
				destinationDms(selfSiteArray);
				transferDms(selfSiteArray);
			}
		}
	});
}
function orginalDms(selfSiteArray) {
	$('#originalDmsName').autocomplete(selfSiteArray, {
		minChars : 0,
		max : 20,
		matchContains : true
	}).result(function(event, data, formatted) {
		var result = data[0].split("|");
		$("#originalDmsName").val(result[0]);
		$("#originalDmsId").val(result[1]);
	});
}
function destinationDms(selfSiteArray) {
	$('#destinationDmsName').autocomplete(selfSiteArray, {
		minChars : 0,
		max : 20,
		matchContains : true
	}).result(function(event, data, formatted) {
		var result = data[0].split("|");
		$("#destinationDmsName").val(result[0]);
		$("#destinationDmsId").val(result[1]);
	});
}
function transferDms(selfSiteArray) {
	$('#transferName').autocomplete(selfSiteArray, {
		minChars : 0,
		max : 20,
		matchContains : true
	}).result(function(event, data, formatted) {
		var result = data[0].split("|");
		$("#transferName").val(result[0]);
		$("#transferId").val(result[1]);
	});
}
function getSites(info) {
	return info.siteName + "|" + info.siteCode + "|";
}

function funReset() {
	$("#originateOrg").val(-1);
	$("#originalDmsName").val("");
	$("#destinationOrg").val(-1);
	$("#destinationDmsName").val("");
	$("#transferOrg").val(-1);
	$("#transferName").val("");
	$("#startDate").val("");
	$("#endDate").val("");
	$("#updateOperatorName").val("");
	$("#yn").val(-1);
	$("#queryYn").val(-1);
}


function getParams() {
	var params = {};
	params.originateOrg = $.trim($("#originateOrg").val());
	params.originalDmsName = $.trim($("#originalDmsName").val());
	params.originalDmsId = $.trim($("#originalDmsId").val());
	params.updateOperatorName = $.trim($("#updateOperatorName").val());

	params.destinationOrg = $.trim($("#destinationOrg").val());
	params.destinationDmsName = $.trim($("#destinationDmsName").val());
	params.destinationDmsId = $.trim($("#destinationDmsId").val());

	params.startDate = $.trim($("#startDate").val());
	params.endDate = $.trim($("#endDate").val());

	params.transferOrg = $.trim($("#transferOrg").val());
	params.transferName = $.trim($("#transferName").val());
	params.transferId = $.trim($("#transferId").val());

	params.yn = $.trim($("#yn").val());
	return params;
}

function doQuery(params) {
	var url = $("#contextPath").val() + "/base/crossbox/query";
	var url_edit = $("#contextPath").val() + "/base/crossbox/toEdit?id="
	CommonClient.post(url, params, function(data) {
		if (data == undefined || data == null) {
			jQuery.messager.alert('提示:', 'HTTP请求无数据返回！', 'info');
			return;
		}
		if (data.code == 1) {
			var pager = data.data;
			var resultList = pager.data;
			if (resultList == null) {
				var temp = "";
			} else {
				var temp = "";
				for (var i = 0; i < resultList.length; i++) {
					temp += "<tr class='a2' style=''>";
					temp += "<td>" + (resultList[i].id) + "</td>";

					temp += "<td>"
					if (resultList[i].originalDmsName != null)
						temp += resultList[i].originalDmsName;
					temp += "</td>";

					temp += "<td>";
					if (resultList[i].transferOneName != null)
						temp += resultList[i].transferOneName;
					temp += "</td>";

					temp += "<td>";
					if (resultList[i].transferTwoName != null)
						temp += resultList[i].transferTwoName;
					temp += "</td>";

					temp += "<td> ";
					if (resultList[i].transferThreeName != null)
						temp += resultList[i].transferThreeName;
					temp += "</td>";

					temp += "<td>" + (resultList[i].destinationDmsName)
							+ "</td>";

					temp += "<td>" + (resultList[i].fullLine) + "</td>";
					temp += "<td>" + dateFormat(resultList[i].updateTime)
							+ "</td>";
					temp += "<td>" + (resultList[i].updateOperatorName)
							+ "</td>";

					if (resultList[i].yn == 1)
						temp += "<td>启用</td>";
					else if (resultList[i].yn == 2)
						temp += "<td>未启用</td>";
					else
						temp += "<td>删除</td>";
					temp += "<td>";
					if (resultList[i].effectiveDate != null)
						temp += dateFormat(resultList[i].effectiveDate);
					temp += "</td>";

					temp += "<td>";
					if (resultList[i].yn == 1) {
						temp += "<a href='#' onclick='do_modify( "
								+ resultList[i].id + ")'>修改</a>";
						temp += "<a href= '#' onclick='toDelete( "
								+ resultList[i].id + ")' >删除</a>";
					} else if (resultList[i].yn == 2) {
						temp += "<a href= '#' onclick='toDelete( "
								+ resultList[i].id + ")' >删除</a>";
					}
					temp += "</td>";
					temp += "</tr>";
				}
			}

			$("#paperTable tbody").html(temp);
			$("#pager").html(
					PageBar.getHtml("queryBtn", pager.totalSize, pager.pageNo,
							pager.totalNo));

		} else {
			alert('提示:', data.message, 'info');
		}
	});
}

function toDelete(id) {
	var contextPath = $("#contextPath").val();
	var re = confirm("确认删除？");
	if (re == true) {
		document.location.href = contextPath + "/base/crossbox/delete?id=" + id;
	}
}

function importDataBtn() {
	var contextPath = $("#contextPath").val();
	location.href = contextPath + "/base/crossbox/toImport";
}

function exportDataBtn() {
	var contextPath = $("#contextPath").val();
	var url = contextPath + "/base/crossbox/toExport";
	$("#dataForm").attr("action", url);
	$("#dataForm").submit();
}

function add0(m) {
	return m < 10 ? '0' + m : m;
}
function dateFormat(date) {

	var time = new Date(date);
	var y = time.getFullYear();
	var m = time.getMonth() + 1;
	var d = time.getDate();
	var h = time.getHours();
	var mm = time.getMinutes();
	var s = time.getSeconds();

	return y + '-' + add0(m) + '-' + add0(d) + ' ' + add0(h) + ':' + add0(mm)
			+ ':' + add0(s);
}

/**
 * 修改按钮两个功能：1. 携带查询参数，2.实现页面跳转
 *
 */
function do_modify(id){
	var params = getParams();
    var originateOrgName = $("#originateOrg").find("option:selected").text();
    var destinationOrgName = $("#destinationOrg").find("option:selected").text();
    var transferOrgName = $("#transferOrg").find("option:selected").text();
	var url = $("#contextPath").val() + "/base/crossbox/toEdit?id=" + id + "&originateOrg="
		+ params.originateOrg + "&originateOrgName=" + encodeURIComponent(encodeURIComponent(originateOrgName))+"&originalDmsName=" + encodeURIComponent(encodeURIComponent(params.originalDmsName)) + "&updateOperatorName="
		+ encodeURIComponent(encodeURIComponent(params.updateOperatorName)) + "&destinationOrg=" + params.destinationOrg + "&destinationOrgName=" + encodeURIComponent(encodeURIComponent(destinationOrgName)) + "&destinationDmsName="
		+ encodeURIComponent(encodeURIComponent(params.destinationDmsName)) +  "&startDate=" + params.startDate + "&endDate="
		+ params.endDate + "&transferOrg=" + params.transferOrg + "&transferOrgName=" + encodeURIComponent(encodeURIComponent(transferOrgName)) +"&transferName="
		+ encodeURIComponent(encodeURIComponent(params.transferName)) + "&yn=" + params.yn;

	window.location.href = url;

}
