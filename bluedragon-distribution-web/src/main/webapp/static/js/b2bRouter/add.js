function main() {
	//加载站点组件
	$('#switchSiteDom_start').sitePluginSelect({
		'createSiteCode': $("#originalSiteCode").val() ? $("#originalSiteCode").val() : null,
		'onlySiteSelect': true,
		'createSiteCodeName': 'originalSiteCode',
		'createSiteNameName': 'originalSiteName',
		'provinceOrOrgMode': 'province',
		'siteTypes' : null,
		'subTypes' : [6420,6450],
		'changeBtnShow': false
	});
	$('#switchSiteDom_transfer1').sitePluginSelect({
		'createSiteCode': $("#transferOneSiteCode").val() ? $("#transferOneSiteCode").val() : null,
		'onlySiteSelect': true,
		'createSiteCodeName': 'transferOneSiteCode',
		'createSiteNameName': 'transferOneSiteName',
		'provinceOrOrgMode': 'province',
		'siteTypes' : null,
		'subTypes' : [6420,6450],
		'changeBtnShow': false
	});
	$('#switchSiteDom_transfer2').sitePluginSelect({
		'createSiteCode': $("#transferTwoSiteCode").val() ? $("#transferTwoSiteCode").val() : null,
		'onlySiteSelect': true,
		'createSiteCodeName': 'transferTwoSiteCode',
		'createSiteNameName': 'transferTwoSiteName',
		'provinceOrOrgMode': 'province',
		'siteTypes' : null,
		'subTypes' : [6420,6450],
		'changeBtnShow': false
	});
	$('#switchSiteDom_transfer3').sitePluginSelect({
		'createSiteCode': $("#transferThreeSiteCode").val() ? $("#transferThreeSiteCode").val() : null,
		'onlySiteSelect': true,
		'createSiteCodeName': 'transferThreeSiteCode',
		'createSiteNameName': 'transferThreeSiteName',
		'provinceOrOrgMode': 'province',
		'siteTypes' : null,
		'subTypes' : [6420,6450],
		'changeBtnShow': false
	});
	$('#switchSiteDom_transfer4').sitePluginSelect({
		'createSiteCode': $("#transferFourSiteCode").val() ? $("#transferFourSiteCode").val() : null,
		'onlySiteSelect': true,
		'createSiteCodeName': 'transferFourSiteCode',
		'createSiteNameName': 'transferFourSiteName',
		'provinceOrOrgMode': 'province',
		'siteTypes' : null,
		'subTypes' : [6420,6450],
		'changeBtnShow': false
	});
	$('#switchSiteDom_transfer5').sitePluginSelect({
		'createSiteCode': $("#transferFiveSiteCode").val() ? $("#transferFiveSiteCode").val() : null,
		'onlySiteSelect': true,
		'createSiteCodeName': 'transferFiveSiteCode',
		'createSiteNameName': 'transferFiveSiteName',
		'provinceOrOrgMode': 'province',
		'siteTypes' : null,
		'subTypes' : [6420,6450],
		'changeBtnShow': false
	});
	$('#switchSiteDom_dest').sitePluginSelect({
		'createSiteCode': $("#destinationSiteCode").val() ? $("#destinationSiteCode").val() : null,
		'onlySiteSelect': true,
		'createSiteCodeName': 'destinationSiteCode',
		'createSiteNameName': 'destinationSiteName',
		'provinceOrOrgMode': 'province',
		'siteTypes' : null,
		'subTypes' : [6420,6450],
		'changeBtnShow': false
	});
	
	//初始化网点信息
	var originalSiteType = $("#originalSiteType").val();

	getSiteData(originalSiteType,$("#originalSiteName"),$("#originalSiteCode"));
	getSiteData(originalSiteType,$("#transferOneSiteName"),$("#transferOneSiteCode"));
	getSiteData(originalSiteType,$("#transferTwoSiteName"),$("#transferTwoSiteCode"));
	getSiteData(originalSiteType,$("#transferThreeSiteName"),$("#transferThreeSiteCode"));
	getSiteData(originalSiteType,$("#transferFourSiteName"),$("#transferFourSiteCode"));
	getSiteData(originalSiteType,$("#transferFiveSiteName"),$("#transferFiveSiteCode"));

}

/**
 * 获取网点信息
 * @param siteType
 * @param siteNameObj
 * @param siteCodeObj
 */
function getSiteData(siteType,siteNameObj,siteCodeObj) {
	var subTypes =  "6420,6450";
	var contextPath = $("#contextPath").val();
	var b2bSiteArray=new Array();
	if(siteType == 1) {
		var url = "/services/bases/getB2BSiteAll/" + subTypes;
		jQuery.ajax({
			type: "GET",
			url: url,
			data: {
				subTypes: subTypes
			},
			success: function (msg) {
				jQuery.each(msg, function (infoIndex, info) {
					if (info.code == 200) {
						var node = {
							label: info.siteName,
							value: info.siteCode
						};
						b2bSiteArray[infoIndex] = node;
					}
				});
				initSiteList(b2bSiteArray,siteNameObj,siteCodeObj);
			}
		});
	} else if(siteType == 2){
		var url = "/services/bases/getBaseAllStore/";
		jQuery.ajax({
			type: "GET",
			url: url,
			success: function (msg) {
				jQuery.each(msg, function (infoIndex, info) {
					if (info.code == 200) {
						var node = {
							label: info.siteName,
							value: info.siteCode
						};
						b2bSiteArray[infoIndex] = node;
					}
				});
				initSiteList(b2bSiteArray,siteNameObj,siteCodeObj);
			}
		});
	}
}

function initSiteList(b2bSiteArray,siteNameObj,siteCodeObj){
	if(b2bSiteArray.length<1){
		siteNameObj.val();
		siteCodeObj.val();
		siteNameObj.unautocomplete();
	}else {
		siteNameObj.autocomplete(b2bSiteArray, {
			formatItem: function (item) {
				return item.label;
			},
			minChars: 0,
			max: 20,
			matchContains: true
		}).result(function (event, item) {
			siteNameObj.val(item.label);
			siteCodeObj.val(item.value);
		});
	}
}

function clearHiddenInput() {
	if ($("#transOneSiteName").val() == "") {
		$("#transOneSiteCode").val("");
	}
	if ($("#transTwoSiteName").val() == "") {
		$("#transTwoSiteCode").val("");
	}
	if ($("#transThreeSiteName").val() == "") {
		$("#transThreeSiteCode").val("");
	}
	if ($("#transFourSiteName").val() == "") {
		$("#transFourSiteCode").val("");
	}
	if ($("#transFiveSiteName").val() == "") {
		$("#transFiveSiteCode").val("");
	}
}
function sbmt() {
	clearHiddenInput();// 清空可编辑下拉框后同时清理隐藏id框
	
	var contextPath = $("#contextPath").val();		
			
		var originalSiteName = $('#switchSiteDom_start').sitePluginSelect('getSelected').siteName;
		var transOneSiteName = $('#switchSiteDom_transfer1').sitePluginSelect('getSelected').siteName;
		var transTwoSiteName = $('#switchSiteDom_transfer2').sitePluginSelect('getSelected').siteName;
		var transThreeSiteName = $('#switchSiteDom_transfer3').sitePluginSelect('getSelected').siteName;
		var transFourSiteName = $('#switchSiteDom_transfer4').sitePluginSelect('getSelected').siteName;
		var transFiveSiteName = $('#switchSiteDom_transfer5').sitePluginSelect('getSelected').siteName;
		var destinationSiteName = $('#switchSiteDom_dest').sitePluginSelect('getSelected').siteName;


		if (originalSiteName == "" || destinationSiteName == ""){
			alert("始发网点&目的网点必填");
		}else if (originalSiteName == destinationSiteName){
			alert("始发网点和目的网点不能重复");
		}else if (transOneSiteName == "" &&
			(transTwoSiteName != "" || transThreeSiteName != "" || transFourSiteName !="" || transFiveSiteName!= "")) {
			alert("请按照顺序填写中转网点");
		} else if (transTwoSiteName == "" &&
			(transThreeSiteName != "" || transFourSiteName !="" || transFiveSiteName!= "")) {
			alert("请按照顺序填写中转网点");
		} else if(transThreeSiteName == "" && (transFourSiteName !="" || transFiveSiteName!= "")) {
			alert("请按照顺序填写中转网点");
		} else if(transFourSiteName =="" && transFiveSiteName!= ""){
			alert("请按照顺序填写中转网点");
		} else {
			var flag = false;
			var message = "";

			jQuery.ajax({
				url : contextPath + "/b2bRouter/check?" + Math.random(),
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
		url : contextPath + "/b2bRouter/doAdd?" + Math.random(),
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
						+ "/b2bRouter/index";
			}
		}
	});
}


function doUpdate() {
	var contextPath = $("#contextPath").val();
	jQuery.ajax({
		url : contextPath + "/b2bRouter/doUpdate?" + Math.random(),
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
				document.location.href = contextPath
					+ "/b2bRouter/index";
			}
		}
	});
}

/**
 * 获取查询条件的参数
 */
function getQueryParams() {
	var params = {};
	params.originateOrg = $.trim($("#query_originateOrg").val());
	params.originateOrgName = $.trim($("#query_originateOrgName").val());
	params.originalDmsName = $.trim($("#query_originalDmsName").val());
	params.updateOperatorName = $.trim($("#query_updateOperatorName").val());

	params.destinationOrg = $.trim($("#query_destinationOrg").val());
	params.destinationOrgName = $.trim($("#query_destinationOrgName").val());
	params.destinationDmsName = $.trim($("#query_destinationDmsName").val());

	params.startDate = $.trim($("#query_startDate").val());
	params.endDate = $.trim($("#query_endDate").val());

	params.transferOrg = $.trim($("#query_transferOrg").val());
	params.transferOrgName = $.trim($("#query_transferOrgName").val());
	params.transferName = $.trim($("#query_transferName").val());

	params.yn = $.trim($("#query_yn").val());
	return params;
}

/**
 * 主页跳转
 */
function back_index(){
	var params = getQueryParams();
	var url =  "/b2bRouter/index?originateOrg="
		+ params.originateOrg + "&originateOrgName=" + encodeURIComponent(encodeURIComponent(params.originateOrgName))+"&originalDmsName=" + encodeURIComponent(encodeURIComponent(params.originalDmsName)) + "&updateOperatorName="
		+ encodeURIComponent(encodeURIComponent(params.updateOperatorName)) + "&destinationOrg=" + params.destinationOrg + "&destinationOrgName=" + encodeURIComponent(encodeURIComponent(params.destinationOrgName)) + "&destinationDmsName="
		+ encodeURIComponent(encodeURIComponent(params.destinationDmsName)) +  "&startDate=" + params.startDate + "&endDate="
		+ params.endDate + "&transferOrg=" + params.transferOrg + "&transferOrgName=" + encodeURIComponent(encodeURIComponent(params.transferOrgName)) +"&transferName="
		+ encodeURIComponent(encodeURIComponent(params.transferName)) + "&yn=" + params.yn;
	window.location.href = url;
}
