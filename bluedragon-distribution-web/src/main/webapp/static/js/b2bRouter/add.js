function main() {
	initOriAndTrans();
	initDes();

	$("#destinationSiteType").change(function() {
		alert("change")
		$("#destinationSiteName").val("");
		$("#destinationSiteCode").val("");
		$("#destinationSiteName").unautocomplete();
		initDes();
	});
}

function initDes() {
	var siteType = $("#destinationSiteType").val();
	var subTypes = 6420;
	var contextPath = $("#contextPath").val();
	var b2bSiteArray=new Array();
	var url = "/services/bases/getB2BSiteAll/"+subTypes;

	alert(siteType);
	if (siteType == 1){
		jQuery.ajax({
			type : "GET",
			url : url,
			data : {
				subTypes : subTypes
			},
			success : function(msg) {
				jQuery.each(msg, function(infoIndex, info) {
					if(info.code == 200) {
						var node = {
							label: info.siteName,
							value: info.siteCode
						};
						b2bSiteArray[infoIndex] = node;
					}
				});
				initSiteList(b2bSiteArray,$("#destinationSiteName"),$("#destinationSiteCode"));
			}
		});
	}else{
		var data = [{label:'仓库1',value:101},{label:'仓库2',value:102},{label:'仓库3',value:103}];
		initSiteList(data,$("#destinationSiteName"),$("#destinationSiteCode"));
	}
}

function initOriAndTrans() {
	var subTypes = 6420;
	var contextPath = $("#contextPath").val();
	var b2bSiteArray=new Array();
	var url = "/services/bases/getB2BSiteAll/"+subTypes;
	jQuery.ajax({
		type : "GET",
		url : url,
		data : {
			subTypes : subTypes
		},
		success : function(msg) {
			jQuery.each(msg, function(infoIndex, info) {
				if(info.code == 200) {
					var node = {
						label: info.siteName,
						value: info.siteCode
					};
					b2bSiteArray[infoIndex] = node;
				}
			});
			initSiteList(b2bSiteArray,$("#originalSiteName"),$("#originalSiteCode"));
			initSiteList(b2bSiteArray,$("#transOneSiteName"),$("#transOneSiteCode"));
			initSiteList(b2bSiteArray,$("#transTwoSiteName"),$("#transTwoSiteCode"));
			initSiteList(b2bSiteArray,$("#transThreeSiteName"),$("#transThreeSiteCode"));
			initSiteList(b2bSiteArray,$("#transFourSiteName"),$("#transFourSiteCode"));
			initSiteList(b2bSiteArray,$("#transFiveSiteName"),$("#transFiveSiteCode"));
		}
	});
}

function initSiteList(b2bSiteArray,siteNameObj,siteCodeObj){
	if(b2bSiteArray.length<1){
		siteNameObj.clear();
		siteCodeObj.clear();
	}else {
		siteNameObj.autocomplete(b2bSiteArray, {
			formatItem: function (item) {
				return item.label;
			},
			minChars: 0,
			max: 20,
			matchContains: true,
		}).result(function (event, item) {
			siteNameObj.val(item.label);
			siteCodeObj.val(item.value);
		});
	}
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
			
	if ($("#dataForm").validate()) {
		var originalSiteName = $("#originalSiteName").val();
		var transOneSiteName = $("#transOneSiteName").val();
		var transTwoSiteName = $("#transTwoSiteName").val();
		var transThreeSiteName = $("#transThreeSiteName").val();
		var transFourSiteName = $("#transFourSiteName").val();
		var transFiveSiteName = $("#transFiveSiteName").val();
		var destinationSiteName = $("#destinationSiteName").val();
		var destinationSiteType = $("#destinationSiteType").val();

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

			var originalSiteCode = $("#originalSiteCode").val();
			var transOneSiteCode = $("#transOneSiteCode").val();
			var transTwoSiteCode = $("#transTwoSiteCode").val();
			var transThreeSiteCode = $("#transThreeSiteCode").val();
			var transFourSiteCode = $("#transFourSiteCode").val();
			var transFiveSiteCode = $("#transFiveSiteCode").val();
			var destinationSiteCode = $("#destinationSiteCode").val();

			var siteNameFullLine=destinationSiteName;
			var siteCodeFullLine=destinationSiteCode;

			if(transFiveSiteName!= ""){
				siteNameFullLine = transFiveSiteName +"-"+siteNameFullLine;
				siteCodeFullLine = transFiveSiteCode +"-"+siteCodeFullLine;
			}
			if(transFourSiteName!=""){
				siteNameFullLine = transFourSiteName +"-"+siteNameFullLine;
				siteCodeFullLine = transFourSiteCode +"-"+siteCodeFullLine;
			}
			if(transThreeSiteName!=""){
				siteNameFullLine = transThreeSiteName +"-"+siteNameFullLine;
				siteCodeFullLine = transThreeSiteCode +"-"+siteCodeFullLine;
			}
			if(transTwoSiteName!=""){
				siteNameFullLine = transTwoSiteName +"-"+siteNameFullLine;
				siteCodeFullLine = transTwoSiteCode +"-"+siteCodeFullLine;
			}
			if(transOneSiteName!=""){
				siteNameFullLine = transOneSiteName +"-"+siteNameFullLine;
				siteCodeFullLine = transOneSiteCode +"-"+siteCodeFullLine;
			}

			siteNameFullLine = originalSiteName +"-"+siteNameFullLine;
			siteCodeFullLine = originalSiteCode +"-"+siteCodeFullLine;

			$("#siteNameFullLine").val(siteNameFullLine);
			$("#siteIdFullLine").val(siteCodeFullLine);

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
	var url =  "/base/crossbox/index?originateOrg="
		+ params.originateOrg + "&originateOrgName=" + encodeURIComponent(encodeURIComponent(params.originateOrgName))+"&originalDmsName=" + encodeURIComponent(encodeURIComponent(params.originalDmsName)) + "&updateOperatorName="
		+ encodeURIComponent(encodeURIComponent(params.updateOperatorName)) + "&destinationOrg=" + params.destinationOrg + "&destinationOrgName=" + encodeURIComponent(encodeURIComponent(params.destinationOrgName)) + "&destinationDmsName="
		+ encodeURIComponent(encodeURIComponent(params.destinationDmsName)) +  "&startDate=" + params.startDate + "&endDate="
		+ params.endDate + "&transferOrg=" + params.transferOrg + "&transferOrgName=" + encodeURIComponent(encodeURIComponent(params.transferOrgName)) +"&transferName="
		+ encodeURIComponent(encodeURIComponent(params.transferName)) + "&yn=" + params.yn;
	window.location.href = url;
}
