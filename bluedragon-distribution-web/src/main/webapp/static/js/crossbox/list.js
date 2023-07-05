function main() {
	//加载站点组件
	$('#switchSiteDom_start').sitePluginSelect({
		'createSiteCode': $("#originalDmsId").val() ? $("#originalDmsId").val() : null,
		'provinceAgencyCodeName' : 'originalProvinceAgencyCode',
		'createSiteCodeName' : 'originalDmsId',
		'changeBtnShow': false,
		'provinceOrOrgMode' : 'province',
		'onlySiteAndProvinceSelect' : true
	});
	$('#switchSiteDom_dest').sitePluginSelect({
		'createSiteCode': $("#destinationDmsId").val() ? $("#destinationDmsId").val() : null,
		'provinceAgencyCodeName' : 'destinationProvinceAgencyCode',
		'createSiteCodeName' : 'destinationDmsId',
		'changeBtnShow': false,
		'provinceOrOrgMode' : 'province',
		'onlySiteAndProvinceSelect' : true
	});
	$('#switchSiteDom_transfer').sitePluginSelect({
		'createSiteCode': $("#transferId").val() ? $("#transferId").val() : null,
		'provinceAgencyCodeName' : 'transferProvinceAgencyCode',
		'createSiteCodeName' : 'transferId',
		'changeBtnShow': false,
		'provinceOrOrgMode' : 'province',
		'onlySiteAndProvinceSelect' : true
	});
	
	// 初始化列表，加载所有信息
	queryBtn(1);
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

	$('#switchSiteDom_start').sitePluginSelect('cleanData');
	$('#switchSiteDom_dest').sitePluginSelect('cleanData');
	$('#switchSiteDom_transfer').sitePluginSelect('cleanData');
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
	params.originalProvinceAgencyCode = $('#switchSiteDom_start').sitePluginSelect('getSelected').provinceAgencyCode === undefined 
		? '' : $('#switchSiteDom_start').sitePluginSelect('getSelected').provinceAgencyCode;
	params.originalDmsId = $('#switchSiteDom_start').sitePluginSelect('getSelected').siteCode === undefined
		? '' : $('#switchSiteDom_start').sitePluginSelect('getSelected').siteCode;

	params.destinationProvinceAgencyCode = $('#switchSiteDom_dest').sitePluginSelect('getSelected').provinceAgencyCode === undefined 
		? '' : $('#switchSiteDom_dest').sitePluginSelect('getSelected').provinceAgencyCode;
	params.destinationDmsId = $('#switchSiteDom_dest').sitePluginSelect('getSelected').siteCode === undefined 
		? '' : $('#switchSiteDom_dest').sitePluginSelect('getSelected').siteCode;

	params.startDate = $.trim($("#startDate").val());
	params.endDate = $.trim($("#endDate").val());

	params.transferProvinceAgencyCode = $('#switchSiteDom_transfer').sitePluginSelect('getSelected').provinceAgencyCode === undefined 
		? '' : $('#switchSiteDom_transfer').sitePluginSelect('getSelected').provinceAgencyCode;
	params.transferId = $('#switchSiteDom_transfer').sitePluginSelect('getSelected').siteCode === undefined 
		? '' : $('#switchSiteDom_transfer').sitePluginSelect('getSelected').siteCode;

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
	checkConcurrencyLimit({
		currentKey: exportReportEnum.CROSS_BOX_REPORT,
		checkPassCallback: function (result) {
			// 提交表单
			var contextPath = $("#contextPath").val();
			var url = contextPath + "/base/crossbox/toExport";
			$("#dataForm").attr("action", url);
			$("#dataForm").submit();
		},
		checkFailCallback: function (result) {
			// 导出校验失败，弹出提示消息
			alert(result.message)
		}
	});
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
	var url = $("#contextPath").val() + "/base/crossbox/toEdit?id=" + id 
		+ "&originalProvinceAgencyCode=" + params.originalProvinceAgencyCode
		+ "&originalDmsId=" + params.originalDmsId
		+ "&destinationProvinceAgencyCode=" + params.destinationProvinceAgencyCode
		+ "&destinationDmsId=" + params.destinationDmsId
		+ "&transferProvinceAgencyCode=" + params.transferProvinceAgencyCode
		+ "&transferId=" + params.transferId
		+ "&startDate=" + params.startDate 
		+ "&endDate=" + params.endDate 
		+ "&yn=" + params.yn;

	window.location.href = url;

}
