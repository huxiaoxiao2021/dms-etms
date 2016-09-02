function queryBtn(pageNo) {
	var params = getParams();
	params.pageNo = pageNo;
	doQuery(params);
}

function getParams() {
	var params = {};
	params.sendCode = $.trim($("#sendCode").val());
	params.packageBarCode = $.trim($("#packageBarCode").val());
	params.businessType = $.trim($("#businessType").val());
	params.operator = $.trim($("#operator").val());

	params.canReceive = $.trim($("#canReceive").val());
	return params;
}

function doQuery(params) {
	var url = $("#contextPath").val() + "/query_reversereceive/query";
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
					temp += "<td>" + (10*(pager.pageNo-1)+i+1) + "</td>";

					temp += "<td>"
					if (resultList[i].sendCode != null)
						temp += resultList[i].sendCode;
					temp += "</td>";

					temp += "<td>";
					if (resultList[i].packageBarCode != null)
						temp += resultList[i].packageBarCode;
					temp += "</td>";

					temp += "<td>";
					if (resultList[i].businessType != null)
						temp += resultList[i].businessType;
					temp += "</td>";

					temp += "<td> ";
					if (resultList[i].canReceive != null)
						temp += resultList[i].canReceive;
					temp += "</td>";

					temp += "<td>";
					if(resultList[i].operatorCode != null)
						temp +=resultList[i].operatorCode;
					temp +=	"</td>";

					temp += "<td>";
					if(resultList[i].operator != null)
						temp += resultList[i].operator;
					temp +=	"</td>";

					temp += "<td>";
					if(resultList[i].operateTime != null)
						temp += dateFormat(resultList[i].operateTime);
					temp +=	"</td>";

					temp += "<td>";
					if(resultList[i].rejectCode != null)
						temp += resultList[i].rejectCode;
					temp +=	"</td>";

					temp += "<td>";
					if(resultList[i].rejectMessage != null)
						temp += resultList[i].rejectMessage;
					temp +=	"</td>";

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

function funReset() {
	$("#sendCode").val("");
	$("#packageBarCode").val("");
	$("#businessType").val("");
	$("#operator").val("");
	$("#canReceive").val(-1);
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
