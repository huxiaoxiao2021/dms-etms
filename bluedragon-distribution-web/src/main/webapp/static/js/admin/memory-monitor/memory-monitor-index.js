//初始化
$(document).ready(main);

function main() {

    // 查询按钮提交处理
    $('#countQueryBtn').click(function () {
        onQueryBtnClick("count");
    });

    // 批量重置按钮提交处理
    $('#valueQueryBtn').click(function () {
        onQueryBtnClick("value");
    });

}


function onQueryBtnClick(type) {
    var params = getParams();
    if (type == "value" && (params.key == null || params.key == "")) {
        alert("值查询,key不能为空!");
        return false;
    }
    doQueryWorker(params, type);
}

function getParams() {
    var params = {};
    params.ips = $.trim($("#ips").val());
    params.key = $.trim($("#key").val());
    return params;
}

// 查询请求
function doQueryWorker(params, type) {
    var url;
    if (type == "count") {
        url = $("#contextPath").val() + "/admin/memory/count";
    } else {
        url = $("#contextPath").val() + "/admin/memory/value";
    }
    //alert(JSON.stringify(params));
    CommonClient.postJson(url, params, function (result) {
        if (result == undefined || result == null) {
            alert("HTTP请求无数据返回！");
            return;
        }
        if (result.code == 200) {
            var data = result.data;
            var temp = "";
            for (var i = 0; i < data.length; i++) {
                temp += "<tr class='a2' style=''>";
                temp += "<td>" + (i + 1) + "</td>";
                temp += "<td>" + (data[i].ip) + "</td>";
                if (type == "count") {
                    temp += "<td>" + (data[i].value) + "</td>";
                } else {
                    temp += "<td style='width:50px;'>" + JSON.stringify(data[i].value) + "</td>";
                }
            }
            $("#paperTable tbody").html("");
            $("#paperTable").append(temp);
        } else {
            alert(result.message);
        }
    });
}
