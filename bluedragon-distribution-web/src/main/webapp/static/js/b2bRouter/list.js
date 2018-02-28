function main() {
	// 初始化任务表下拉框
	initB2BSite();
}


// 初始化始发网点和目的网点
function initB2BSite() {
	var url = $("#contextPath").val() + "/services/bases/getB2BSiteAll/";
	var param = {};
	$.getJSON(url, function(data) {
		var B2BSiteList = data;
		var tableObj_originateOrg = $('#originateB2BSite');
		var tableObj_destinationOrg = $('#destinationB2BSite');
		var optionList;
		for (var i = 0; i < B2BSiteList.length; i++) {
			if (B2BSiteList[i].siteCode != -100) {
				optionList += "<option value='" + B2BSiteList[i].siteCode + "')>"
						+ B2BSiteList[i].siteName + "</option>";
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