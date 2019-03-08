/**
 * Created by dudong on 2016/3/14.
 */
$(document).ready(init);

/**
 * 页面加载执行
 * */
function init() {
    $("#gantry_device_org").change(function () {
        org_change($(this).val());
    });

    $("#gantry_query_sub").click(function () {
        gantry_query_sub(1);
    })

    $("#gantry_add_sub").click(function () {
        jQuery.msg.confirm("该页面已经停止注册新的龙门架设备，新增龙门架设备请前往自动化系统。点击确定获取新的操作手册",function () {
            window.open("https://cf.jd.com/pages/viewpage.action?pageId=146384841","_blank");
        });
        // to_add_page();
    });

    gantry_query_sub(1);
}


/**
 * 删除龙门架按钮提交
 * */
function gantry_delete_sub(id) {
    if (confirm("删除后无法恢复，确认操作？")) {
        do_gantry_delete(id);
    }
}


/**
 * 修改龙门架按钮提交
 * */
function gantry_modify_sub(id) {
    if(null == id || undefined == id || $.trim(id).length <= 0){
        alert("请求参数错误");
        return;
    }
    to_modify_page(id);
}


/**
 * 跳转到修改页面
 * */
function to_modify_page(id){
    window.location.href = $("#contextPath").val() + "/gantry/modifyShow?id=" + id+"&t="+new Date().getTime();
}


/**
 *  确定删除
 * */
function do_gantry_delete(id) {
    var url = $("#contextPath").val() + "/gantry/doDel";
    var params = {};
    params.id = id;
    CommonClient.post(url, params, function (data) {
        if (data == undefined || data == null) {
            alert("提示请求无数据返回");
            return;
        }
        if (data.code == 200) {
            alert(data.message);
            window.location.reload()
        } else {
            alert(data.message);
        }
    });
}

/**
 * 单击增加按钮，跳转到增加页面
 * */
function to_add_page() {
    window.location.href = $("#contextPath").val() + "/gantry/addShow";
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
        optionList = "<option value=''>所有分拣中心</option>";
    } else {
        optionList = "<option value=''>所有分拣中心</option>";
        for (var i = 0; i < dmsList.length; i++) {
            optionList += "<option value='" + dmsList[i].siteCode + "'>" + dmsList[i].siteName + "</option>";
        }
    }
    $("#gantry_device_site").html(optionList);
}


/**
 * 提交查询
 * */
function gantry_query_sub(pageNo) {
    var params = getParams();
    params.pageNo = pageNo;
    doQuery(params);
}


/**
 * 组装参数
 * */
function getParams() {
    var params = {};
    params.machineId = $.trim($("#gantry_device_id").val());
    params.orgCode = $.trim($("#gantry_device_org").val());
    params.siteCode = $.trim($("#gantry_device_site").val());
    params.supplier = $.trim($("#gantry_device_supplier").val());
    return params;
}

/**
 *查询请求
 * */
function doQuery(params) {
    var url = $("#contextPath").val() + "/gantry/doQuery";
    CommonClient.post(url, params, function (data) {
        if (data == undefined || data == null) {
            alert("没有符合条件的龙门架设备");
            return;
        }
        if (data.code == 200) {
            var pager = data.data;
            var dataList = pager.data;
            var temp = "";
            for (var i = 0; i < dataList.length; i++) {
                temp += "<tr class='a2' style=''>";
                temp += "<td>" + (dataList[i].machineId) + "</td>";
                temp += "<td>" + (dataList[i].token) + "</td>";
                temp += "<td>" + (null == dataList[i].serialNumber ? "" : dataList[i].serialNumber) + "</td>";
                temp += "<td>" + (dataList[i].orgName) + "</td>";
                temp += "<td>" + (dataList[i].siteName) + "</td>";
                temp += "<td>" + (null == dataList[i].supplier ? "" : dataList[i].supplier) + "</td>";
                temp += "<td>" + (null == dataList[i].modelNumber ? "" : dataList[i].modelNumber) + "</td>";
                temp += "<td>" + (null == dataList[i].type ? "" : dataList[i].type) + "</td>";
                temp += "<td>" + (null == dataList[i].mark ? "" : dataList[i].mark) + "</td>";
                temp += "<td>" + (getDateString(dataList[i].createTime)) + "</td>";
                temp += "<td>" + (getDateString(dataList[i].updateTime)) + "</td>";
                temp += "<td>" + (null == dataList[i].operateName ? "" : dataList[i].operateName) + "</td>";
                temp+="<td>"+dataList[i].version+"</td>";
                temp += "<td>" + ("<a href='javascript:void(0)' onclick='gantry_modify_sub(" + dataList[i].machineId + ")''>修改</a>"
                    + "<a href='javascript:void(0)' onclick='gantry_delete_sub(" + dataList[i].machineId
                    + ")' id='gantry_list_delete'>删除</a>") + "</td>";
                temp += "</tr>";
            }
            $("#paperTable tbody").html(temp);
            // 添加分页显示
            $("#pager").html(PageBar.getHtml("gantry_query_sub", pager.totalSize, pager.pageNo, pager.totalNo));
        } else {
            alert(data.message);
        }
    });
}

function getDateString(millis) {
    if (null == millis) {
        return "";
    }
    var date = new Date();
    date.setTime(millis);
    return date.format('yyyy-MM-dd HH:mm:ss');
}

Date.prototype.format = function (f) {
    var o = {
        "M+": this.getMonth() + 1, // month
        "d+": this.getDate(), // day
        "H+": this.getHours(), // hour
        "m+": this.getMinutes(), // minute
        "s+": this.getSeconds(), // second
        "q+": Math.floor((this.getMonth() + 3) / 3), // quarter
        "S": this.getMilliseconds()
        // millisecond
    }
    if (/(y+)/.test(f))
        f = f.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(f))
            f = f.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
    return f
}

