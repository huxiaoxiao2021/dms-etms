function main() {
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
    $("#waybillCode").keypress(function(event){
        var keycode = (event.keyCode ? event.keyCode : event.which);
        if(keycode == '13'){
            var waybillCode=$("#waybillCode").val();

            receiverAddressQuery(waybillCode);
            $("#waybillCode").val("");
        }
    });

    var pageInit = function() {
        var oInit = new Object();
        oInit.init = function() {
            /*起始时间*/ /*截止时间*/
            $.datePicker.createNew({
                elem: '#sendDateStart',
                theme: '#3f92ea',
                type: 'datetime',
                // min: -60,//最近30天内
                // max: 0,//最近30天内
                btns: ['now', 'confirm'],
                done: function(value, date, sendDateStart){
                    /*重置表单验证状态*/
                }
            });
            $.datePicker.createNew({
                elem: '#sendDateEnd',
                theme: '#3f92ea',
                type: 'datetime',
                // min: -60,//最近30天内
                // max: 0,//最近30天内
                btns: ['now', 'confirm'],
                done: function(value, date, sendDateEnd){
                    /*重置表单验证状态*/
                }
            });

            $('#btn_query').click(function() {
                tableInit().refresh();
            });
        };
        return oInit;
    };
    pageInit().init();
    initDateQuery();
	queryBtn(1);
}
function initDateQuery(){
    var sendDateStart = $.dateHelper.formatDateTime(new Date(new Date().toLocaleDateString()));
    var sendDateEnd = $.dateHelper.formatDateTime(new Date(new Date(new Date().toLocaleDateString()).getTime()+24*60*60*1000-1));
    $("#sendDateStart").val(sendDateStart);
    $("#sendDateEnd").val(sendDateEnd);
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
 * 选择后打印
 */
function printBtn() {

    var checkedKeys = $( "#paperTable  tbody input[type=checkbox][name='record']:checked");

    if(checkedKeys) {
        if (checkedKeys.length > 0) {
                var contextPath = $("#contextPath").val();
                var idList = new Array();
                for(var i=0;i<checkedKeys.length;i++){
                    idList[i] = $(checkedKeys[i]).val();
                }
                var url = contextPath + "/waybill/rma/printWaybillRma";
                jQuery.ajax({
                    type: 'post',
                    url: url,
                    dataType : "json",//必须json
                    contentType : "application/json", // 指定这个协议很重要
                    data : JSON.stringify(idList),
                    async : false,
                    success: function (msg) {

                    }
                });
        }else{
            alert("请选择要打印的记录！");
        }
    }else{
        alert("请选择要打印的记录！");
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

function getParams() {
	var params = {};
	params.sendDateStart = $.trim($("#sendDateStart").val());
	params.sendDateEnd = $.trim($("#sendDateEnd").val());
	params.hasPrint = $.trim($("#hasPrint").val());
	params.receiverAddress = $.trim($("#receiverAddress").val());
	return params;
}

function doQuery(params) {
	var url =  "/waybill/rma/query";
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
					temp += "<td> <input type='checkbox' id='"+ resultList[i].id +"' name= 'record' value='"+resultList[i].id +"' onclick='checkboxclick()' /> </td>"

                    temp += "<td>"
                    if (resultList[i].waybillCode != null)
                        temp += resultList[i].waybillCode;
                    temp += "</td>";

					temp += "<td>"
					if (resultList[i].receiverAddress != null)
						temp += resultList[i].receiverAddress;
					temp += "</td>";

					temp += "<td>";
					if (resultList[i].isPrint != null)
						temp += resultList[i].isPrint;
					temp += "</td>";

					temp += "</tr>";
				}
			}
			$("#paperTable tbody").html(temp);
			$("#pager").html(
				PageBar.getHtml("queryBtn", pager.totalSize, pager.pageNo,pager.totalNo));
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
function receiverAddressQuery(waybillCode) {
    var contextPath = $("#contextPath").val();
    var url = contextPath + "/waybill/rma/receiverAddressQuery";

    jQuery.ajax({
        type: 'post',
        url: url,
        dataType : "json",//必须json
        contentType : "application/json", // 指定这个协议很重要
        data : JSON.stringify(waybillCode),
        async : false,
        success: function (msg) {

        }
    });
	
}