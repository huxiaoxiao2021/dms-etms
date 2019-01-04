function main() {
    $("#checkAll").click(function () {
        var checked = $("#checkAll").prop("checked");
        $("#paperTable tbody input[type=checkbox][name=record]").each(function () {
            if (checked) {
                $(this).prop("checked", true);
                $(this).parents("tr").css("color", "#FF6500");
            } else {
                $(this).removeAttr("checked");
                $(this).parents("tr").css("color", "#000000");
            }
        });
    });

    $("#waybillCode").keypress(function (event) {
        var keycode = (event.keyCode ? event.keyCode : event.which);
        if (keycode == '13') {
            var waybillCode = $("#waybillCode").val();
            receiverAddressQuery(waybillCode);
            $("#waybillCode").val("");
        }
    });

    initDateQuery();
}

function initDateQuery() {
    var curDate = new Date();
    var preDate = new Date(curDate.getTime() - 24 * 60 * 60 * 1000 * 3);
    var sendDateStart = $.dateHelper.formatDateTime(new Date(preDate.toLocaleDateString()));
    var sendDateEnd = $.dateHelper.formatDateTime(new Date(new Date(curDate.toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1));
    $("#sendDateStart").val(sendDateStart);
    $("#sendDateEnd").val(sendDateEnd);
    $("#pageSize").val("10");
}

function checkBoxClick() {
    $("input[name=record]").each(function () {
        if ($(this).prop("checked")) {
            $(this).parents("tr").css("color", "#FF6500");
        } else {
            $(this).parents("tr").css("color", "#000000");
        }
    })
}

/**
 * 选择后打印
 */
function printBtn() {
    var checkedKeys = $("#paperTable tbody input[type=checkbox][name='record']:checked");
    if (checkedKeys) {
        if (checkedKeys.length > 0) {
            var idList = new String($(checkedKeys[0]).val());
            if (checkedKeys.length > 1) {
                for (var i = 1; i < checkedKeys.length; i++) {
                    idList = idList + "," + $(checkedKeys[i]).val();
                }
            }
            openPostWindow("/waybill/rma/printWaybillRma", "idList", idList);
        } else {
            jQuery.messager.alert('提示:', '请选择要打印的记录！', 'info');
        }
    }
    else {
        jQuery.messager.alert('提示:', '请选择要打印的记录！', 'info');
    }
}

/**
 * 导出
 */
function exportBtn() {
    var checkedKeys = $("#paperTable tbody input[type=checkbox][name='record']:checked");
    if (checkedKeys) {
        if (checkedKeys.length > 0) {
            var url = "/waybill/rma/toExport";
            var idList = new String($(checkedKeys[0]).val());
            if (checkedKeys.length > 1) {
                for (var i = 1; i < checkedKeys.length; i++) {
                    idList = idList + "," + $(checkedKeys[i]).val()
                }
            }
            openPostWindow(url, "idList", idList);
        } else {
            jQuery.messager.alert('提示:', '请选择要导出的记录！', 'info');
        }
    } else {
        jQuery.messager.alert('提示:', '请选择要导出的记录！', 'info');
    }
}

function openPostWindow(url, name, param) {
    var tempForm = document.createElement("form");
    tempForm.id = "tempForm";
    tempForm.method = "post";
    tempForm.action = url;
    tempForm.target = "blank";

    var hideInput = document.createElement("input");
    hideInput.type = "hidden";
    hideInput.name = name;
    hideInput.value = param;

    tempForm.appendChild(hideInput);
    if (document.all) {
        tempForm.attachEvent("onsubmit", function () {
        });        //IE
        document.body.appendChild(tempForm);
        tempForm.fireEvent("onsubmit");
    } else {
        var subObj = tempForm.addEventListener("submit", function () {
        }, false);    //firefox
        document.body.appendChild(tempForm);
        tempForm.dispatchEvent(new Event("submit"));
    }

    tempForm.submit();
    document.body.removeChild(tempForm);
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
    params.printStatus = $.trim($("#printStatus").val());
    params.receiverAddress = $.trim($("#receiverAddress").val());
    params.pageSize = $.trim($("#pageSize").val());
    return params;
}

function doQuery(params) {
    var url = "/waybill/rma/query";
    CommonClient.post(url, params, function (data) {
        if (data == undefined || data == null) {
            jQuery.messager.alert('提示:', '查询失败', 'error');
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
                    temp += "<td> <input type='checkbox' id='" + resultList[i].id + "' name= 'record' value='" + resultList[i].id + "' onclick='checkBoxClick()' /> </td>"

                    temp += "<td>"
                    if (resultList[i].waybillCode != null)
                        temp += resultList[i].waybillCode;
                    temp += "</td>";

                    temp += "<td>"
                    if (resultList[i].receiverAddress != null)
                        temp += resultList[i].receiverAddress;
                    temp += "</td>";

                    temp += "<td>";
                    if (resultList[i].printStatus != null)
                        if (resultList[i].printStatus == 1) {
                            temp += "已打印";
                        } else {
                            temp += "未打印";
                        }
                    temp += "</td>";

                    temp += "</tr>";
                }
            }
            $("#paperTable tbody").html(temp);
            $("#pageSize").val(pager.pageSize);
            $("#pager").html(PageBar.getHtml("queryBtn", pager.totalSize, pager.pageNo, pager.totalNo));
        } else {
            jQuery.messager.alert('提示:', data.message, 'error');
        }
    });
}

function receiverAddressQuery(waybillCode) {
    var contextPath = $("#contextPath").val();
    var url = contextPath + "/waybill/rma/receiverAddressQuery";
    jQuery.ajax({
        type: 'post',
        url: url,
        dataType: "json",//必须json
        contentType: "application/json", // 指定这个协议很重要
        data: waybillCode,
        async: false,
        success: function (msg) {
            if (msg == null) {
                jQuery.messager.alert('提示:', '查询地址信息失败', 'info');
                return;
            }
            if (msg.code == 1) {
                $("#receiverAddress").val(msg.data)
            } else {
                jQuery.messager.alert('提示:', msg.message, 'info');
                return;
            }
        }
    });

}