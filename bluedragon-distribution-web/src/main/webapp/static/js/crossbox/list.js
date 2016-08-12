function main() {
	getSiteData(-1, -1);
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
	initOrg();

	// 初始化列表，加载所有信息
	initList();
}

// 初始化现在的列表
function initList() {
	var params = {};
	params.originateOrg = -1;
	params.originalDmsName = null;
	params.originalDmsId = null;

	params.updateOperatorName = null;

	params.destinationOrg = -1;
	params.destinationDmsName = null;
	params.destinationDmsId = null;

	params.startDate = null;
	params.endDate = null;

	params.transferOrg = -1;
	params.transferName = null;
	params.transferId = null;

	params.yn = -1;

	var url = $("#contextPath").val() + "/base/crossbox/query";
	var url_edit = $("#contextPath").val() + "/base/crossbox/toEdit?id="
	CommonClient.post(url, params,
			function(data) {
				if (data == undefined || data == null) {
					alert('提示:', 'HTTP请求无数据返回！', 'info');
					return;
				}

				if (data.code == 1) {
					var pager = data.data;
					var resultList = pager.data;
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
							temp += "<a href=" + url_edit + resultList[i].id
									+ ">修改</a>";
							temp += "<a href= '#' onclick='toDelete( "
									+ resultList[i].id + ")' >删除</a>";
						} else if (resultList[i].yn == 2) {
							temp += "<a href= '#' onclick='toDelete( "
									+ resultList[i].id + ")' >删除</a>";
						}
						temp += "</td>";
						temp += "</tr>";
					}

					$("#paperTable tbody").html(temp);
					$("#pager").html(
							PageBar.getHtml("queryBtn", pager.totalSize,
									pager.pageNo, pager.totalNo));
				} else {
					alert('提示:', data.message, 'info');
				}
			});
}

// 初始化始发区域、目的区域、中转区域下拉框
function initOrg() {
	var url = $("#contextPath").val() + "/services/bases/allorgs";
	var param = {};
	$.getJSON(url, function(data) {
		var orgList = data;
		var tableObj_originateOrg = $('#originateOrg');
		var tableObj_destinationOrg = $('#destinationOrg');
		var tableObj_transferOrg = $('#transferOrg');
		var optionList;
		for (var i = 0; i < orgList.length; i++) {
			if (orgList[i].orgId != -100) {
				optionList += "<option value='" + orgList[i].orgId + "'>"
						+ orgList[i].orgName + "</option>";
			}
		}
		tableObj_originateOrg.append(optionList);
		tableObj_destinationOrg.append(optionList);
		tableObj_transferOrg.append(optionList);
	});
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
function addBtn() {
	var contextPath = $("#contextPath").val();
	location.href = contextPath + "/base/crossbox/toAdd";
}

function queryBtn(pageNo) {
	var params = getParams();
	params.pageNo = pageNo;
	doQuery(params);
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
						temp += "<a href=" + url_edit + resultList[i].id
								+ ">修改</a>";
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
