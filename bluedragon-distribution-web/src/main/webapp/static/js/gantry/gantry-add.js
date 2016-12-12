/**
 * Created by dudong on 2016/3/14.
 */
$(document).ready(init);

/**
 * 页面加载执行
 * */
function init() {
    $("#gantry_add_column_org").change(function(){
        org_change($(this).val());
    });

    $("#gantry_add_back").click(function(){
        back_to_gantry_list();
    });

    $("#gantry_add_submit").click(function(){
       gantry_add_submit();
    });

    $("#gantry_modify_back").click(function(){
       back_to_gantry_list();
    });
}


/**
 * 增加按钮提交
 * */
function gantry_add_submit(){
    var params = {};
    try {
        params = check_Build_Param();
    }catch (e){
        alert(e);
        return;
    }
    do_add_gantry(params);
}


/**
 * 执行增加操作
 * */
function do_add_gantry(params){
    var url = $("#contextPath").val() + "/gantry/doAdd";
    CommonClient.post(url, params, function(data) {
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
function check_Build_Param(){
    var params = {};
    params.serialNumber = $.trim($("#gantry_add_column_serial_num").val());
    params.orgCode = $.trim($("#gantry_add_column_org").val());
    if(params.orgCode == null || params.orgCode == undefined || params.orgCode.length <= 0){
        throw "请选择龙门架所在机构";
    }
    params.orgName = $.trim($("#gantry_add_column_org option:selected").text())
    params.siteCode = $.trim($("#gantry_add_column_site").val());
    if(params.siteCode == null || params.siteCode == undefined || params.siteCode.length <= 0){
        throw "请选择龙门架所在分拣中心";
    }
    params.siteName = $.trim($("#gantry_add_column_site option:selected").text())
    params.supplier = $.trim($("#gantry_add_column_supplier").val());
    params.modelNumber = $.trim($("#gantry_add_column_model_num").val());
    params.type = $.trim($("#gantry_add_column_type").val());
    params.mark = $.trim($("#gantry_add_column_mark").val());
    params.version= $(".gantry_column_list_div_text input[name='gantry_add_column_version']:checked").val();
    return params;
}

/**
 * 返回主页
 * */
function back_to_gantry_list(){
    window.location.href= $("#contextPath").val() + "/gantry/index";
}

/**
 * 机构变更，实现分拣中心联动
 * */
function org_change(orgId) {
    if ($.trim(orgId).length <= 0) {
        init_dms_select();
        return;
    }
    var url = $("#contextPath").val() + "/gantry/dmsList";
    var param = {"orgId": orgId};
    $.getJSON(url, param, function (data) {
        init_dms_select(data);
    });
}


/**
 * 初始化分拣中心下拉列表
 * */
function init_dms_select(data) {
    var dmsList = data;
    var optionList = "";
    if (dmsList == undefined || null == dmsList || $.trim(dmsList).length <= 0) {
        optionList = "<option value=''>请选择站点...</option>";
    } else {
        optionList = "<option value=''>请选择站点...</option>";
        for (var i = 0; i < dmsList.length; i++) {
            optionList += "<option value='" + dmsList[i].siteCode + "'>" + dmsList[i].siteName + "</option>";
        }
    }
    $("#gantry_add_column_site").html(optionList);
}





