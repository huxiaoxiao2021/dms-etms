/**
 * Created by dudong on 2016/3/14.
 */
$(document).ready(init);

/**
 * 页面加载执行
 * */
function init() {
    $("#gantry_modify_back").click(function () {
        back_to_gantry_list();
    });
    $("#gantry_modify_submit").click(function () {
        gantry_modify_submit();
    });
    $("#gantry_add_column_org").trigger("change");
}


/**
 * 修改按钮提交
 * */
function gantry_modify_submit() {
    var params = {};
    try {
        params = check_Build_Param();
    } catch (e) {
        alert(e);
        return;
    }
    do_modify_gantry(params);
}


/**
 * 执行更新操作
 * */
function do_modify_gantry(params) {
    var url = $("#contextPath").val() + "/gantry/doModify";
    CommonClient.post(url, params, function (data) {
        if (data == undefined || data == null) {
            alert("提示请求无数据返回");
            return;
        }
        if (data.code == 200) {
            alert(data.message);
            back_to_gantry_list();
        } else {
            alert(data.message);
        }
    });
}


/**
 * 检查参数是否合法，然后组装起来
 * */
function check_Build_Param() {
    var params = {};
    params.machineId = $.trim($("#gantry_add_column_machine_id").val());
    if (params.machineId == null || params.machineId == undefined || params.machineId.length <= 0) {
        throw "更新龙门架参数错误";
    }
    params.serialNumber = $.trim($("#gantry_add_column_serial_num").val());
    params.orgCode = $.trim($("#gantry_add_column_org").val());
    if (params.orgCode == null || params.orgCode == undefined || params.orgCode.length <= 0) {
        throw "请选择龙门架所在机构";
    }
    params.orgName = $.trim($("#gantry_add_column_org option:selected").text())
    params.siteCode = $.trim($("#gantry_add_column_site").val());
    if (params.siteCode == null || params.siteCode == undefined || params.siteCode.length <= 0) {
        throw "请选择龙门架所在分拣中心";
    }
    params.siteName = $.trim($("#gantry_add_column_site option:selected").text())
    params.supplier = $.trim($("#gantry_add_column_supplier").val());
    params.modelNumber = $.trim($("#gantry_add_column_model_num").val());
    params.type = $.trim($("#gantry_add_column_type").val());
    params.mark = $.trim($("#gantry_add_column_mark").val());
    params.version= $("gantry_add_column_version").val();
    return params;
}

/**
 * 返回主页
 * */
function back_to_gantry_list() {
    window.location.href = $("#contextPath").val() + "/gantry/index";
}

/**
 * 机构变更，实现分拣中心联动
 * */
function org_change(orgId, siteId) {
    if ($.trim(orgId).length <= 0) {
        init_dms_select(null, siteId);
        return;
    }
    var url = $("#contextPath").val() + "/gantry/dmsList";
    var param = {"orgId": orgId};
    $.getJSON(url, param, function (data) {
        init_dms_select(data, siteId);
    });
}


/**
 * 初始化分拣中心下拉列表
 * */
function init_dms_select(data, siteId) {
    var dmsList = data;
    var optionList = "";
    if (dmsList == undefined || null == dmsList || $.trim(dmsList).length <= 0) {
        optionList = "<option value=''>请选择站点...</option>";
    } else {
        optionList = "<option value=''>请选择站点...</option>";
        for (var i = 0; i < dmsList.length; i++) {
            if (dmsList[i].siteCode == siteId) {
                optionList += "<option selected='selected' value='" + dmsList[i].siteCode + "'>" + dmsList[i].siteName + "</option>";
            } else {
                optionList += "<option value='" + dmsList[i].siteCode + "'>" + dmsList[i].siteName + "</option>";
            }
        }
    }
    $("#gantry_add_column_site").html(optionList);
}





