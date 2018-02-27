function main() {
//	getSiteData(-1, -1);
	if($("#originateOrg")){
		var orgId = $("#originateOrg").val();
		getSiteData(1, orgId);
	}
	
	if($("#destinationOrg")){
		var orgId = $("#destinationOrg").val();
		getSiteData(2, orgId);
	}
	
	if($("#transferOneOrg")){
		var orgId = $("#transferOneOrg").val();
		getSiteData(3, orgId);
	}
	
	if($("#transferTwoOrg")){
		var orgId = $("#transferTwoOrg").val();
		getSiteData(4, orgId);
	}
	
	if($("#transferThreeOrg")){
		var orgId = $("#transferThreeOrg").val();
		getSiteData(5, orgId);
	}
	
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
	$("#transferOneOrg").change(function() {
		$("#transferOneName").val("");
		$("#transferOneId").val("");
		$("#transferOneName").unautocomplete();
		var orgId = $("#transferOneOrg").val();
		getSiteData(3, orgId);
	});
	$("#transferTwoOrg").change(function() {
		$("#transferTwoName").val("");
		$("#transferTwoId").val("");
		$("#transferTwoName").unautocomplete();
		var orgId = $("#transferTwoOrg").val();
		getSiteData(4, orgId);
	});
	$("#transferThreeOrg").change(function() {
		$("#transferThreeName").val("");
		$("#transferThreeId").val("");
		$("#transferThreeName").unautocomplete();
		var orgId = $("#transferThreeOrg").val();
		getSiteData(5, orgId);
	});
	
	// 初始化机构下拉框
	initOrg();
}

// 初始化机构下拉框
function initOrg() {
	var url = $("#contextPath").val() + "/services/bases/allorgs";
	var param = {};
	$.getJSON(url, function(data) {
		var orgList = data;
		var tableObj_originateOrg = $('#originateOrg');
		var tableObj_destinationOrg = $('#destinationOrg');
		var tableObj_transferOneOrg = $('#transferOneOrg');
		var tableObj_transferTwoOrg = $('#transferTwoOrg');
		var tableObj_transferThreeOrg = $('#transferThreeOrg');

		var optionList;
		for (var i = 0; i < orgList.length; i++) {
			if (orgList[i].orgId != -100) {
				optionList += "<option value='" + orgList[i].orgId + "'>"
						+ orgList[i].orgName + "</option>";
			}
		}
		tableObj_originateOrg.append(optionList);
		tableObj_destinationOrg.append(optionList);
		tableObj_transferOneOrg.append(optionList);
		tableObj_transferTwoOrg.append(optionList);
		tableObj_transferThreeOrg.append(optionList);
	});
}

function getSiteData(index, orgId) {
	var subType = 6420;
	var contextPath = $("#contextPath").val();
	var b2bSites = "";
	var b2bSiteArray=new Array();
	var url = contextPath + "/services/bases/getB2BSiteAll/" + subType;
	jQuery.ajax({
		type : "GET",
		url : url,
		data : {
			subType : subType
		},
		success : function(msg) {
			jQuery.each(msg, function(infoIndex, info) {
				if (info.code == 200) {
					var node = {
						label:info.siteName,
						value:info.siteCode
					};
					b2bSiteArray[infoIndex] = node;
				}
			});

			initSiteList(b2bSiteArray);

			// b2bfSiteArray = b2bSites.split(" ");
            //
			// orginalDms(b2bfSiteArray);
			// destinationDms(b2bfSiteArray);
			// transferDms1(b2bfSiteArray);
			// transferDms2(b2bfSiteArray);
			// transferDms3(b2bfSiteArray);

			initSiteList(b2bSiteArray);
		}
	});
}

function initSiteList(b2bSiteArray){
	$("#originalDmsName").autocomplete({
		// 静态的数据源
		source: b2bSiteArray,
		select: function(event, ui){
			// 这里的this指向当前输入框的DOM元素
			// event参数是事件对象
			// ui对象只有一个item属性，对应数据源中被选中的对象

			$(this).value = ui.item.label;
			$("#originalSiteCode").val( ui.item.value );

			// 必须阻止默认行为，因为autocomplete默认会把ui.item.value设为输入框的value值
			event.preventDefault();
		}
	});
}
function orginalDms(b2bfSiteArray) {
	$('#originalDmsName').autocomplete(b2bfSiteArray, {
		minChars : 0,
		max : 20,
		matchContains : true
	}).result(function(event, data) {
		var result = data[0].split("|");
		$("#originalSiteName").val(result[0]);
		$("#originalSiteCode").val(result[1]);
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
function transferDms1(selfSiteArray) {
	$('#transferOneName').autocomplete(selfSiteArray, {
		minChars : 0,
		max : 20,
		matchContains : true
	}).result(function(event, data, formatted) {
		var result = data[0].split("|");
		$("#transferOneName").val(result[0]);
		$("#transferOneId").val(result[1]);
	});
}
function transferDms2(selfSiteArray) {
	$('#transferTwoName').autocomplete(selfSiteArray, {
		minChars : 0,
		max : 20,
		matchContains : true
	}).result(function(event, data, formatted) {
		var result = data[0].split("|");
		$("#transferTwoName").val(result[0]);
		$("#transferTwoId").val(result[1]);
	});
}
function transferDms3(selfSiteArray) {
	$('#transferThreeName').autocomplete(selfSiteArray, {
		minChars : 0,
		max : 20,
		matchContains : true
	}).result(function(event, data, formatted) {
		var result = data[0].split("|");
		$("#transferThreeName").val(result[0]);
		$("#transferThreeId").val(result[1]);
	});
}
function getSites(info) {
	return info.siteName + "|" + info.siteCode + "|";
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
		var originalDmsName = $("#originalDmsName").val();
		var desDmsName = $("#destinationDmsName").val();
		var transferOneName = $("#transferOneName").val();
		var transferTwoName = $("#transferTwoName").val();
		var transferThreeName = $("#transferThreeName").val();
  
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
