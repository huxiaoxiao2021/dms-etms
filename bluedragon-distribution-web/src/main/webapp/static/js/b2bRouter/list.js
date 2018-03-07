function main() {
	// 初始化任务表下拉框
	var originalSiteType = $("#originalSiteType").val();
	var destinationSiteType = $("#destinationSiteType").val();

	getSiteData(originalSiteType,$("#originalSiteName"),$("#originalSiteCode"));
	getSiteData(destinationSiteType,$("#destinationSiteName"),$("#destinationSiteCode"));

	$("#destinationSiteType").change(function() {
		$("#destinationSiteName").val("");
		$("#destinationSiteCode").val("");
		$("#destinationSiteName").unautocomplete();
		var siteType = $("#destinationSiteType").val();
		getSiteData(siteType,$("#destinationSiteName"),$("#destinationSiteCode"));
	});

	$("#checkAll").click(function () {
		var checked = $("#checkAll").prop("checked");
		$("#paperTable tbody input[type=checkbox][name=record]").each(function(){
			if(checked) {
				$(this).prop("checked", true);
				$(this).parents("tr").css("color","#FF6500");
			} else {
				$(this).removeAttr("checked");
				$(this).parents("tr").css("color","#000000");
			}
		});
	});

	queryBtn(1);
}


function checkboxclick() {
	$("input[name=record]").each(function () {
		if($(this).prop("checked")){
		    $(this).parents("tr").css("color","#FF6500");
		}else{
		    $(this).parents("tr").css("color","#000000");
		}
	})
}

/**
 * 获取网点信息
 * @param siteType
 * @param siteNameObj
 * @param siteCodeObj
 */
function getSiteData(siteType,siteNameObj,siteCodeObj) {
	var subTypes = 6420;
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

/**
 * 初始化选择框
 * @param b2bSiteArray
 * @param siteNameObj
 * @param siteCodeObj
 */
function initSiteList(b2bSiteArray,siteNameObj,siteCodeObj){
	if(b2bSiteArray.length<1){
		siteNameObj.val("");
		siteCodeObj.val("");
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


/**
 * 查询
 * @param pageNo
 */
function queryBtn(pageNo) {
	var params = getParams();
	params.pageNo = pageNo;
	doQuery(params);
}

/**
 * 新增
 */
function addBtn() {
	var contextPath = $("#contextPath").val();
	location.href = contextPath + "/b2bRouter/toAdd";
}

/**
 * 修改
 */
function updateBtn(){
	var checkedKeys = $( "#paperTable  tbody input[type=checkbox][name='record']:checked");

	if(checkedKeys) {
		if(checkedKeys.length == 0){
			alert("请选择一条记录！");
		}
		if (checkedKeys.length >1) {
			alert("一次只能修改一条记录");
		} else {
			var params = getParams();
			var id = $(checkedKeys[0]).val();
			var url ="/b2bRouter/toEdit?id="+id + "&originalSiteCode=" + params.originalSiteCode +
				"&originalSiteName=" + params.originalSiteName + "&destinationSiteType=" + params.destinationSiteType +
				"&destinationSiteCode=" + params.destinationSiteCode + "&destinationSiteName=" + params.destinationSiteName;
			window.location.href = url;

		}
	}else{
		alert("请选择一条记录！");
	}
}


/**
 * 删除
 */
function deleteBtn(){
	var checkedKeys = $( "#paperTable  tbody input[type=checkbox][name='record']:checked");

	if(checkedKeys) {
		if (checkedKeys.length > 0) {
			var re = confirm("确认删除这 "+ checkedKeys.length +" 条记录？");
			if (re == true) {
				var contextPath = $("#contextPath").val();
				var idList = new Array();
				for(var i=0;i<checkedKeys.length;i++){
					idList[i] = $(checkedKeys[i]).val();
				}

				var url = contextPath + "/b2bRouter/delete";
				jQuery.ajax({
					type: 'post',
					url: url,
					dataType : "json",//必须json
					contentType : "application/json", // 指定这个协议很重要  
					data : JSON.stringify(idList),
					async : false,
					success: function (msg) {
						alert("删除成功！");
						window.location.href='/b2bRouter/index';
					}
				});
			}
		}else{
			alert("请选择要删除的记录！");
		}
	}else{
		alert("请选择要删除的记录！");
	}
}

/**
 * 导入
 */
function importBtn(){
	var contextPath = $("#contextPath").val();
	location.href = contextPath + "/b2bRouter/toImport";
}

function getParams() {
	var params = {};
	params.originalSiteType = $.trim($("#originalSiteType").val());
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
				var temp = "";
				for (var i = 0; i < resultList.length; i++) {
					temp += "<tr class='a2' style=''>";
					// temp += "<td> <input type='checkbox' id="+ resultList[i].id +" name= 'record' value="+resultList[i].id +" /> </td>"
					temp += "<td> <input type='checkbox' id='"+ resultList[i].id +"' name= 'record' value='"+resultList[i].id +"' onclick='checkboxclick()' /> </td>"
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
					if (resultList[i].operatorUserErp != null)
						temp += resultList[i].operatorUserErp;
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