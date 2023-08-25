function main() {

	//加载站点组件
	$('#switchSiteDom_start').sitePluginSelect({
		'createSiteCode': $("#originalDmsId").val() ? $("#originalDmsId").val() : null,
		'createSiteCodeName' : 'originalDmsId',
		'createSiteNameName': 'originalDmsName',
		'siteTypes': null,
		'changeBtnShow': false,
		'provinceOrOrgMode' : 'province',
		'onlySiteAndProvinceSelect' : true
	});
	$('#switchSiteDom_dest').sitePluginSelect({
		'createSiteCode': $("#destinationDmsId").val() ? $("#destinationDmsId").val() : null,
		'createSiteCodeName' : 'destinationDmsId',
		'createSiteNameName': 'destinationDmsName',
		'changeBtnShow': false,
		'siteTypes': null,
		'provinceOrOrgMode' : 'province',
		'onlySiteAndProvinceSelect' : true
	});
	$('#switchSiteDom_transfer1').sitePluginSelect({
		'createSiteCode': $("#transferOneId").val() ? $("#transferOneId").val() : null,
		'createSiteCodeName' : 'transferOneId',
		'createSiteNameName': 'transferOneName',
		'changeBtnShow': false,
		'siteTypes': null,
		'provinceOrOrgMode' : 'province',
		'onlySiteAndProvinceSelect' : true
	});
	$('#switchSiteDom_transfer2').sitePluginSelect({
		'createSiteCode': $("#transferTwoId").val() ? $("#transferTwoId").val() : null,
		'createSiteCodeName' : 'transferTwoId',
		'createSiteNameName': 'transferTwoName',
		'changeBtnShow': false,
		'siteTypes': null,
		'provinceOrOrgMode' : 'province',
		'onlySiteAndProvinceSelect' : true
	});
	$('#switchSiteDom_transfer3').sitePluginSelect({
		'createSiteCode': $("#transferThreeId").val() ? $("#transferThreeId").val() : null,
		'createSiteCodeName' : 'transferThreeId',
		'createSiteNameName': 'transferThreeName',
		'changeBtnShow': false,
		'siteTypes': null,
		'provinceOrOrgMode' : 'province',
		'onlySiteAndProvinceSelect' : true
	});
}

function clearHiddenInput() {
	if ($("#transferOneName").val() == "") {
		$("#transferOneId").val("");
	}
	if ($("#transferTwoName").val() == "") {
		$("#transferTwoId").val("");
	}
	if ($("#transferThreeName").val() == "") {
		$("#transferThreeId").val("");
	}
}
function sbmt() {
	clearHiddenInput();// 清空可编辑下拉框后同时清理隐藏id框
	
	var contextPath = $("#contextPath").val();		
			
		var originalDmsName = $('#switchSiteDom_start').sitePluginSelect('getSelected').siteName;
		var desDmsName = $('#switchSiteDom_dest').sitePluginSelect('getSelected').siteName;
		var transferOneName = $('#switchSiteDom_transfer1').sitePluginSelect('getSelected').siteName;
		var transferTwoName = $('#switchSiteDom_transfer2').sitePluginSelect('getSelected').siteName;
		var transferThreeName = $('#switchSiteDom_transfer3').sitePluginSelect('getSelected').siteName;
  
		if (originalDmsName == "" || desDmsName == "" || transferOneName == ""){
			alert("始发分拣中心&中转1&目的分拣中心必填");
		}else if (originalDmsName == desDmsName){
			alert("始发分拣中心和目的分拣中心不能重复");
		}else if (transferOneName == ""
				&& (transferTwoName != "" || transferThreeName != "")) {
			alert("请按照顺序填写中转地");
		} else if (transferTwoName == "" && transferThreeName != "") {
			alert("请按照顺序填写中转地");
		} else {
			var flag = false;
			var message = "";
			jQuery.ajax({
				url : contextPath + "/base/crossbox/check?" + Math.random(),
				type : 'post',
				data : $('#dataForm').formSerialize(),
				dataType : 'json',
				async : false,
				success : function(data) {
					if (data && data.code == 0) {
						if (data.message) {
							alert(data.message);
						} else {
							alert("校验路由是否存在异常");
						}
					} else if (data && data.code == 2) {
						flag = true;
						message = data.message;
					} else {
						doAdd();
					}
				}
			});
			if (flag) {
				var re=confirm(message);
				if(re==true){
					doUpdate();
				}
			}
		}
}
function doAdd() {
	var contextPath = $("#contextPath").val();
	jQuery.ajax({
		url : contextPath + "/base/crossbox/doAdd?" + Math.random(),
		type : 'post',
		data : $('#dataForm').formSerialize(),
		dataType : 'json',
		async : false,
		success : function(data) {
			if (data && data.code == 0) {
				if (data.message) {
					alert(data.message);
				} else {
					alert("添加异常");
				}
			} else {
				alert("添加成功");
				document.location.href = contextPath
						+ "/base/crossbox/index";
			}
		}
	});
}
function doUpdate() {
	var contextPath = $("#contextPath").val();
	jQuery.ajax({
		url : contextPath + "/base/crossbox/doUpdate?" + Math.random(),
		type : 'post',
		data : $('#dataForm').formSerialize(),
		dataType : 'json',
		async : false,
		success : function(data) {
			if (data && data.code == 0) {
				if (data.message) {
					alert(data.message);
				} else {
					alert("更新异常");
				}
			} else {
				alert("更新成功");
				back_index();//携带之前的查询条件参数跳转到主页
			}
		}
	});
}

/**
 * 获取查询条件的参数
 */
function getQueryParams() {
	var params = {};
	params.originalProvinceAgencyCode = $.trim($("#query_originalProvinceAgencyCode").val());
	params.originalDmsId = $.trim($("#query_originalDmsId").val());

	params.destinationProvinceAgencyCode = $.trim($("#query_destinationProvinceAgencyCode").val());
	params.destinationDmsId = $.trim($("#query_destinationDmsId").val());

	params.startDate = $.trim($("#query_startDate").val());
	params.endDate = $.trim($("#query_endDate").val());

	params.transferProvinceAgencyCode = $.trim($("#query_transferProvinceAgencyCode").val());
	params.transferId = $.trim($("#query_transferId").val());

	params.yn = $.trim($("#query_yn").val());
	return params;
}

/**
 * 主页跳转
 */
function back_index(){
	var params = getQueryParams();
	var url =  "/base/crossbox/index?originalProvinceAgencyCode="+ params.originalProvinceAgencyCode
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
