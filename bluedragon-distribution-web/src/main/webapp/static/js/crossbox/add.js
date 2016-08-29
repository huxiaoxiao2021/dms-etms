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
				transferDms1(selfSiteArray);
			} else if (index == 4) {
				transferDms2(selfSiteArray);
			} else if (index == 5) {
				transferDms3(selfSiteArray);
			} else {
				orginalDms(selfSiteArray);
				destinationDms(selfSiteArray);
				transferDms1(selfSiteArray);
				transferDms2(selfSiteArray);
				transferDms3(selfSiteArray);
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

function ret_back() {
	var contextPath = $("#contextPath").val();
	location.href = contextPath + "/base/crossbox/index";
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
				document.location.href = contextPath
						+ "/base/crossbox/index";
			}
		}
	});
}