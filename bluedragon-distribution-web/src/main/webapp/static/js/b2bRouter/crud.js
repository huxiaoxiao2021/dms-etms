function crud(options) {
    var defaults = {
        type:undefined,//c,u,u,d其中一种
        dataGridId:undefined,
        title:undefined,
        bodyId:undefined,
        cUrl:undefined,
        uUrl:undefined,
        dUrl:undefined,
        rUrl:undefined,
        primaryName:undefined,
        tipFieldNames:undefined,
        editDialogSize:undefined,
        disableFields:undefined,
        dialogShowBefore:undefined
    };
    var setting = $.extend(defaults, options);

    $("#dialogTitleContent").empty();
    $("#dialogTitleContent").text(setting.title);
    $("#dialogBodyContent").empty();
    $("#dialogBodyContent").html($(setting.bodyId).html());

    if (setting.editDialogSize) {
        $("#dialog .modal-dialog").removeClass("modal-lg");
        $("#dialog .modal-dialog").removeClass("modal-sm");
        $("#dialog .modal-dialog").addClass(setting.editDialogSize);
    }

    var formUrl = undefined;
    if(setting.type == "c") {
        formUrl = setting.cUrl;
        $("#dialogBodyContent form [crud]:not([crud=c])").remove();
        if (setting.dialogShowBefore) {
            setting.dialogShowBefore($("#dialogBodyContent"));
        }
        $("#dialog").modal("show");
    } else if(setting.type == "u") {
        formUrl = setting.uUrl;
        var checkedKeys = $(setting.dataGridId + " tbody input[type=checkbox][name=" + setting.primaryName + "]:checked");
        if(checkedKeys) {
            if (checkedKeys.length != 1) {
                showTipDialog("danger", "<strong>请只选择一条记录来编辑</strong>");
            } else {
                $.blockUI({
                    message: '<h1>Processing</h1>',
                    css: { border: '3px solid #a00' }
                });
                var parameter = {};
                parameter[setting.primaryName] = $(checkedKeys[0]).val();
                $.ajax({
                    type:"post",
                    dataType:"json",
                    url:setting.rUrl,
                    data:parameter,
                    timeout: 3000,
                    success: function(result, textStatus, jqXHR){
                        $.unblockUI();
                        if(result && result.success && result.data) {
                            var formFields = $("#dialogBodyContent form input,#dialogBodyContent form select,#dialogBodyContent form textarea");
                            if (formFields && formFields.length > 0) {
                                for (property in result.data) {
                                    for (i = 0 ; i < formFields.length; i++) {
                                        var formField = formFields[i];
                                        if (property == $(formField).attr("name")) {
                                            if ($(formField).is("input")) {
                                                if ($(formField).attr("type") == "checkbox") {
                                                    if(result.data[property] == true) {
                                                        $(formField).attr("checked", "checked");
                                                    } else {
                                                        $(formField).removeAttr("checked");
                                                    }
                                                } else if ($(formField) instanceof Array && $(formField[0]).attr("type") == "radio") {
                                                    for (radioFormField in $(formField)) {
                                                        if ($(radioFormField).val() == result.data[property]) {
                                                            $(radioFormField).attr("checked", "checked");
                                                        } else {
                                                            $(radioFormField).removeAttr("checked");
                                                        }
                                                    }
                                                } else {
                                                    $(formField).val(result.data[property]);
                                                }
                                            } else if ($(formField).is("select")) {
                                                $(formField).val(result.data[property]+"");
                                            } else if ($(formField).is("textarea")) {
                                                $(formField).text(result.data[property]);
                                            }
                                        }
                                    }
                                }
                            }
                            if(setting.disableFields && setting.disableFields.length > 0) {
                                for (var i = 0 ; i < setting.disableFields.length; i++) {
                                    var readonlyFields = $(
                                        "#dialogBodyContent form input[name="+setting.disableFields[i]+"]," +
                                        "#dialogBodyContent form select[name="+setting.disableFields[i]+"]," +
                                        "#dialogBodyContent form textarea[name="+setting.disableFields[i]+"]");
                                    $(readonlyFields).attr("readonly", "readonly");
                                }
                            }
                            $("#dialogBodyContent form [crud]:not([crud=u])").remove();
                            if (setting.dialogShowBefore) {
                                setting.dialogShowBefore($("#dialogBodyContent"));
                            }
                            $("#dialog").modal("show");
                        } else {
                            showTipDialog("info", "<strong>没有找到对应记录</strong>");
                        }
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        $.unblockUI();
                        showHttpError(XMLHttpRequest);
                    }
                });
            }
        }
    } else if(setting.type == "d") {
        formUrl = setting.dUrl;
        var checkedKeys = $(setting.dataGridId + " tbody input[type=checkbox]:checked");
        if (!checkedKeys || checkedKeys.length == 0) {
            showTipDialog("danger", "<strong>请选择要删除的记录</strong>");
        } else {
            var parameter = {};
            parameter[setting.primaryName] = [];
            var checkedRows = [];
            for (var i = 0; i < checkedKeys.length; i++) {
                parameter[setting.primaryName].push($(checkedKeys[i]).val());
                checkedRows.push($(checkedKeys[i]).parent().parent());
            }
            var confirmMessage = "确定要删除以下记录？<br/>";
            if (setting.tipFieldNames && setting.tipFieldNames.length) {
                for (var i = 0 ; i < checkedRows.length; i++) {
                    for (var j = 0 ; j < setting.tipFieldNames.length; j++) {
                        var fieldTd = $(checkedRows[i]).find("td[field=" + setting.tipFieldNames[j] + "]");
                        if (fieldTd) {
                            confirmMessage += $(setting.dataGridId + " thead th[field=" + setting.tipFieldNames[j] + "]").text() + "=";
                            if ($(fieldTd).attr("checkbox")) {
                                confirmMessage += $(fieldTd).find("input[type=checkbox]").val();
                            } else {
                                confirmMessage += $(fieldTd).text();
                            }
                        }
                        if (j < setting.tipFieldNames.length - 1) {
                            confirmMessage += ",";
                        }
                    }
                    confirmMessage += "<br/>";
                }
            }
            showConfirmDialog(confirmMessage, function (confirmResult) {
                if(!confirmResult){
                    return;
                }
                $.blockUI();
                $.ajax({
                    type:"post",
                    dataType:"json",
                    url:formUrl,
                    traditional: true,
                    data:parameter,
                    timeout: 3000,
                    success: function(result, textStatus, jqXHR){
                        $.unblockUI();
                        if(result && result.success) {
                            showBusinessSuccess();
                        } else {
                            showBusinessError(result.errorMessage);
                        }
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        $.unblockUI();
                        showHttpError(XMLHttpRequest);
                    }
                });
            });
        }
    }

    $("#dialogFootContent  button[data-action=save]").unbind();
    $("#dialogFootContent  button[data-action=save]").on("click", function () {
        var isValid = false;
        $("#dialogBodyContent form").isValid(
            function(value){
                isValid = value;
            }
        );
        if (!isValid) {
            return;
        }
        $("#dialog").block({
            message: '<h1>正在保存</h1>',
            css: { border: '3px solid #a00' }
        });
        $("#dialogBodyContent form").ajaxSubmit({
            type:"post",
            dataType:"json",
            url:formUrl,
            beforeSubmit: function(arr, $form, options) {
                var notEmptyData = [];
                for(var i = 0 ; i < arr.length ; i++) {
                    if (arr[i].value && arr[i].value != null && arr[i].value != "") {
                        notEmptyData.push(arr[i]);
                    }
                }
                if (notEmptyData.length > 0) {
                    while (arr.length > 0) {
                        arr.pop();
                    }
                    for(var j = 0 ; j < notEmptyData.length ; j++) {
                        arr.push(notEmptyData[j]);
                    }
                }
                return true;
            },
            success: function(result, statusText, xhr) {
                $("#dialog").unblock();
                if(result && result.success) {
                    $("#dialog").modal("hide");
                    showBusinessSuccess();
                } else {
                    showBusinessError(result.errorMessage);
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                $("#dialog").unblock();
                showHttpError(XMLHttpRequest);
            }
        });
    });

}

function showBusinessTip(tip) {
    showTipDialog("info", tip);
}

function showBusinessSuccess() {
    showTipDialog("success", "<strong>执行成功</strong>");
}

function showBusinessError(message) {
    showTipDialog("danger", "<strong>执行错误</strong>&nbsp;请查看详情", message);
}

function showHttpError(XMLHttpRequest) {
    showTipDialog("danger", "<strong>请求服务器错误</strong>&nbsp;状态:" + XMLHttpRequest.status);
}

function showTipDialog(alertType, summary, detail) {
    $("#dialogForTipBodyContent #summary").removeClass();
    $("#dialogForTipBodyContent #summary").addClass("alert alert-" + alertType);
    $("#dialogForTipBodyContent #summary").html(summary);
    if (detail) {
        $("#dialogForTipBodyContent #detail").css("display", "block");
        $("#dialogForTipBodyContent #detail").text(detail);
        /*$("#dialogForTip").removeClass("modal-sm");
         $("#dialogForTip").addClass("modal-lg");*/
    } else {
        /*$("#dialogForTip").removeClass("modal-lg");
         $("#dialogForTip").addClass("modal-sm");*/
        $("#dialogForTipBodyContent #detail").css("display", "none");
    }
    $("#dialogForTip").modal('show');
}

function showConfirmDialog(confirm, callback) {
    $("#dialogForConfirmBodyContent #confirm").empty();
    $("#dialogForConfirmBodyContent #confirm").html(confirm);
    $("#dialogForConfirmFootContent #yes").unbind();
    $("#dialogForConfirmFootContent #no").unbind();
    $("#dialogForConfirmFootContent #yes").on("click", function(){
        $("#dialogForConfirm").modal("hide");
        callback(true);
    });
    $("#dialogForConfirmFootContent #no").on("click", function(){
        $("#dialogForConfirm").modal("hide");
        callback(false);
    });
    $("#dialogForConfirm").modal("show");
}

function query(tableId, url, parameter, pagingId) {
    $(tableId).block({
        message: '<h1>正在保存</h1>',
        css: { border: '3px solid #a00' }
    });
    if (!parameter) {
        parameter = {};
    }
    if(!parameter.pageSize) {
        parameter.pageSize = 50;
    }
    if(!parameter.pageNum) {
        parameter.pageNum = 1;
    }
    $.ajax({
        type:"post",
        dataType:"json",
        url:url,
        traditional: true,
        data: parameter,
        timeout: 3000,
        success: function(result, textStatus, jqXHR){
            $(tableId).unblock();
            if(result && result.success) {
                $(tableId + " tbody").empty();
                $(tableId + " thead th[field] input[type=checkbox]").removeAttr("checked");
                var fields = $(tableId + " thead th[field]");
                if (fields && fields.length > 0 && result.data && result.data.records && result.data.records.length > 0) {
                    var rowsHtml = "";
                    for (var i = 0 ; i < result.data.records.length; i++) {
                        rowsHtml += "<tr>";
                        for (var j = 0 ; j < fields.length ; j ++) {
                            var fieldName = $(fields[j]).attr("field");
                            var fieldValue = result.data.records[i][fieldName];
                            if (!fieldValue) {
                                fieldValue = "";
                            }
                            var formatter = $(fields[j]).attr("formatter");
                            if (formatter && formatter != "") {
                                formatter = eval(formatter);
                                fieldValue = formatter(fieldValue);
                            }
                            var hiddenFieldHtml =  $(fields[j]).attr("hidden") ? "style=\"display:none\"" : "";
                            if ($(fields[j]).attr("checkbox")) {
                                rowsHtml += "<td " + hiddenFieldHtml + " field=\"" + fieldName + "\" checkbox=\"true\"><input type=\"checkbox\" name=\""+ fieldName +"\" value=\"" + fieldValue + "\"></td>"
                            } else {
                                rowsHtml += "<td " + hiddenFieldHtml + " field=\"" + fieldName + "\">" + fieldValue + "</td>"
                            }
                        }
                        rowsHtml += "</tr>"
                    }
                }
                $(tableId + " tbody").html(rowsHtml);
                if (pagingId) {
                    var pagingBarContent = "";
                    var firstAndPreviousEnable = (result.data.pageNum > 1);
                    var nextAndLastEnable = (result.data.pageNum < result.data.pageTotal);
                    if (firstAndPreviousEnable) {
                        pagingBarContent += "<li id=\"first\"><a href=\"#\">First</a></li>";
                        pagingBarContent += "<li id=\"previous\"><a href=\"#\">Previous</a></li>";
                    } else {
                        pagingBarContent += "<li id=\"first\" class=\"disabled\"><span>First</span></li>";
                        pagingBarContent += "<li id=\"previous\" class=\"disabled\"><span>Previous</span></li>";
                    }
                    var pageNumCount = 5, halfPageNumCount = parseInt(pageNumCount/2);
                    if (result.data.pageTotal <= pageNumCount) {
                        for (var i = 1 ; i <= result.data.pageTotal; i++) {
                            pagingBarContent += '<li id="pageNum" ' + (i == result.data.pageNum ? 'class="active"' : '') + '><a href="#">'+ i +'</a></li>';
                        }
                    } else {
                        if (result.data.pageNum - halfPageNumCount < 1) {
                            for (var i = 1; i <= pageNumCount; i++) {
                                pagingBarContent += '<li id="pageNum" ' + (i == result.data.pageNum ? 'class="active"' : '') + '><a href="#">'+ i +'</a></li>';
                            }
                        } else if(result.data.pageNum + halfPageNumCount > result.data.pageTotal){
                            var tempPagNumContent = "";
                            for (var i = result.data.pageTotal, j = 1; i >= 1 && j <= pageNumCount; i--, j++) {
                                tempPagNumContent = '<li id="pageNum" ' + (i == result.data.pageNum ? 'class="active"' : '') + '><a href="#">'+ i +'</a></li>' + tempPagNumContent;
                            }
                            pagingBarContent += tempPagNumContent;
                        } else {
                            for (var i = result.data.pageNum - halfPageNumCount; i <= result.data.pageNum + halfPageNumCount; i++) {
                                pagingBarContent += '<li id="pageNum" ' + (i == result.data.pageNum ? 'class="active"' : '') + '><a href="#">'+ i +'</a></li>';
                            }
                        }
                    }
                    if (nextAndLastEnable) {
                        pagingBarContent += "<li id=\"next\"><a href=\"#\">Next</a></li>";
                        pagingBarContent += "<li id=\"last\"><a href=\"#\">Last</a></li>";
                    } else {
                        pagingBarContent += "<li id=\"next\" class=\"disabled\"><span>Next</span></li>";
                        pagingBarContent += "<li id=\"last\" class=\"disabled\"><span>Last</span></li>";
                    }
                    pagingBarContent += '<li><span>共' + result.data.recordTotal + '条</span></li>';
                    pagingBarContent += '<li><span>共' + result.data.pageTotal + '页</span></li>';
                    pagingBarContent += '<li><span>每页</span></li>';
                    var pageSizeModel = new Array(50,100,300,1000,10000);
                    for (var i = 0 ; i< pageSizeModel.length; i++) {
                        pagingBarContent += '<li id="pageSize" ' + (result.data.pageSize == pageSizeModel[i] ? 'class="active"' : '') + '><a href=\"#\">' + pageSizeModel[i] + '</a></li>';
                    }
                    $(pagingId).html(pagingBarContent);
                    //先解除事件
                    $(pagingId + " #first a").unbind();
                    $(pagingId + " #previous a").unbind();
                    $(pagingId + " #next a").unbind();
                    $(pagingId + " #last a").unbind();
                    $(pagingId + " #pageSize a").unbind();
                    var pageSize = parseInt($(pagingId + " #pageSize.active a").text());
                    //再逐个绑定
                    $(pagingId + " #first a").on("click", function(){
                        var parameter = {pageSize : pageSize, pageNum : 1};
                        query(tableId, url, parameter, pagingId);
                    });
                    $(pagingId + " #previous a").on("click", function(){
                        var parameter = {pageSize : pageSize, pageNum : parseInt($(pagingId + " #pageNum.active").text()) - 1};
                        query(tableId, url, parameter, pagingId);
                    });
                    $(pagingId + " #pageNum a").on("click", function(){
                        var parameter = {pageSize : pageSize, pageNum : parseInt($(this).text())};
                        query(tableId, url, parameter, pagingId);
                    });
                    $(pagingId + " #next a").on("click", function(){
                        var parameter = {pageSize : pageSize, pageNum : parseInt($(pagingId + " #pageNum.active").text()) + 1};
                        query(tableId, url, parameter, pagingId);
                    });
                    $(pagingId + " #last a").on("click", function(){
                        var parameter = {pageSize : pageSize, pageNum : result.data.pageTotal};
                        query(tableId, url, parameter, pagingId);
                    });
                    $(pagingId + " #pageSize a").on("click", function(){
                        $(pagingId + " #pageSize").removeClass("active");
                        $(this).parent().addClass("active");
                        pageSize = parseInt($(pagingId + " #pageSize.active a").text());
                        var parameter = {pageSize : pageSize, pageNum : 1};
                        query(tableId, url, parameter, pagingId);
                    });
                }
            } else {
                showBusinessError(result.errorMessage);
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            $(tableId).unblock();
            showHttpError(XMLHttpRequest);
        }
    });
}

function initSelect(selectRef, selectId, url, parameter, valueField, labelField) {
    $.ajax({
        type:"post",
        dataType:"json",
        url:url,
        traditional: true,
        data: parameter,
        timeout: 10000,
        success: function(result, textStatus, jqXHR){
            if(result && result.success) {
                var selectObj = selectRef ? $(selectRef) : $(selectId);
                if (!selectObj) {
                    showTipDialog("danger", "没有找到select对象");
                    return;
                }
                selectObj.empty();
                if (valueField && labelField) {
                    var optionsHtml = "";
                    for (var i = 0 ; i < result.data.records.length; i++) {
                        optionsHtml += "<option value=\"" + result.data.records[i][valueField] + "\">" + result.data.records[i][labelField] + "</option>"
                    }
                }
                selectObj.html(optionsHtml);
            } else {
                showBusinessError(result.errorMessage);
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            showHttpError(XMLHttpRequest);
        }
    });
}