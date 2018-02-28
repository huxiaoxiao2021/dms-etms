function main() {
	// 初始化任务表下拉框
	// initB2BSite();
	queryBtn(1);
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

function getParams() {
	var params = {};
	params.originalSiteCode = $.trim($("#originalSiteCode").val());
	params.originalSiteName = $.trim($("#originalSiteName").val());
	params.destinationSiteType =$.trim($("#destinationSiteType").val());
	params.destinationSiteCode = $.trim($("#destinationSiteCode").val());
	params.destinationSiteName = $.trim($("#destinationSiteName").val());

	return params;
}

function doQuery(params) {
	var url =  "/b2bRouter/query";
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
				alert("获取数据成功");
				alert(resultList.length);
				var temp = "";
				for (var i = 0; i < resultList.length; i++) {
					temp += "<tr class='a2' style=''>";
					temp += "<td> <input type='checkbox' id="+ resultList[i].id +" name= "+resultList[i].id+"/> </td>"

					temp += "<td>"
					if (resultList[i].originalSiteName != null)
						temp += resultList[i].originalSiteName;
					temp += "</td>";


					var siteNameFullLine = resultList[i].siteNameFullLine;
					if(siteNameFullLine != null) {
						var transSiteList = siteNameFullLine.split('-');
						var siteLength = 7;
						for (var j = 1; j<siteLength -1; j++){
							if(j<transSiteList.length-1 && transSiteList[j]!=null){
								temp += "<td>";
								temp += transSiteList[j];
								temp += "</td>";
							}else{
								temp += "<td>-</td>";
							}
						}
					}

					temp += "<td>";
					if (resultList[i].destinationSiteName != null)
						temp += resultList[i].destinationSiteName;
					temp += "</td>";

					temp += "<td> ";
					if (resultList[i].operatorUserName != null)
						temp += resultList[i].operatorUserName;
					temp += "</td>";

					temp += "<td>" + dateFormat(resultList[i].updateTime)
						+ "</td>";

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

//新增
function addBtn() {
	var contextPath = $("#contextPath").val();
	location.href = contextPath + "/b2bRouter/toAdd";
}

function updateBtn(){
	var params = getParams();
	var id = 11;
	var url ="/b2bRouter/toEdit?id="+id + "&destinationSiteType=" + params.destinationSiteType;
	alert(url);

	// +"&originalSiteCode="
	// + params.originalSiteCode + "&originalSiteName=" + encodeURIComponent(encodeURIComponent(params.originalSiteName))
	// + "&destinationSiteType=" + params.destinationSiteType + "&destinationSiteCode="+ params.destinationSiteCode
	// + "&destinationSiteName=" + encodeURIComponent(encodeURIComponent(params.destinationSiteName))

	window.location.href = url;

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