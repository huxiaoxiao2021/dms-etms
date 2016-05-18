/**
 * Created by dudong on 2016/5/4.
 */
$(document).ready(init);

function init(){
    $("#fresh_add_back").click(function(){
        backToIndex();
    });

    $("#fresh_add_submit").click(function(){
        freshWaybillAddCommit();
    });
}

function backToIndex(){
    window.location.href= $("#contextPath").val() + "/fresh/index";
}

function freshWaybillAddCommit(){
    try {
        doAddFreshWaybillCommit(checkParam());
    } catch (e) {
        alert(e);
        return;
    }
}

function doAddFreshWaybillCommit(params){
    var url = $("#contextPath").val() + "/fresh/add";
    CommonClient.post(url, params, function(data) {
        if (data == undefined || data == null) {
            alert("提示请求无数据返回");
            return;
        }
        if (data.code == 200) {
            alert("录入温度成功");
        } else {
            alert(data.message);
        }
    });
}

function freshWaybillModifyCommit(){
    try {
        doModifyFreshWaybillCommit(checkParam());
    } catch (e) {
        alert(e);
        return;
    }
}


function doModifyFreshWaybillCommit(params) {
    var url = $("#contextPath").val() + "/fresh/update";
    CommonClient.post(url, params, function(data) {
        if (data == undefined || data == null) {
            alert("提示请求无数据返回");
            return;
        }
        if (data.code == 200) {
            alert("修改温度成功");
        } else {
            alert(data.message);
        }
    });
}

function checkParam(){

    var numReg = new RegExp("^[0-9]*$");
    var zFloatReg = new RegExp("^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$");
    var fFloatReg = new RegExp("^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$");
    var params = {};
    params.id = $.trim($("#fresh_add_column_list_id").val());
    params.packageCode = $.trim($("#fresh_add_column_package_code").val());
    if(params.packageCode.length <= 0) {
        throw "包裹号不能为空";
    }

    params.boxType = $.trim($("#fresh_add_column_box_type").val());
    if(params.boxType.length <= 0) {
        throw "请选择保温箱型号";
    }

    params.slabType = $.trim($("#fresh_add_column_slab_type").val());
    if(params.slabType.length <= 0) {
        throw "请选择冰板型号";
    }

    params.slabNum =$.trim($("#fresh_add_column_slab_num").val());
    if(params.slabNum.length <= 0 || !numReg.test(params.slabNum)) {
        throw "冰板数量格式不正确";
    }

    params.packageTemp = $.trim($("#fresh_add_column_package_temp").val());
    if(params.packageTemp.length <= 0 || !(zFloatReg.test(params.packageTemp) || fFloatReg.test(params.packageTemp))) {
        throw "商品温度格式不正确";
    }

    params.slabTemp = $.trim($("#fresh_add_column_slab_temp").val());
    if(params.slabTemp.length <= 0 || !(zFloatReg.test(params.slabTemp) || fFloatReg.test(params.slabTemp))) {
        throw "冰板温度格式不正确";
    }

    return params;
}