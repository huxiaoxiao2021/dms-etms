//初始化
$(document).ready(main);

function main() {
    // 返回按钮
    $('#backBtn').click(function () {
        goBack();
    });

    init(1);
}

// 返回
function goBack() {
    window.location.href = $("#contextPath").val() + "/areaDestPlan/index";
}

function init(pageNo) {
    var params = getParams();
    params.pageNo = pageNo;
    doQuery(params);
}

function getParams() {
    var params = {};
    params.planId = $.trim($("#planId").val());
    return params;
}

// 查询请求
function doQuery(params) {
    var url = $("#contextPath").val() + "/areaDest/getList";
    CommonClient.post(url, params, function (data) {
        if (data == undefined || data == null) {
            alert("未配置方案");
            return;
        }
        if (data.code == 200) {
            var pager = data.data;
            var dataList = pager.data;
            if (dataList != null && dataList.length > 0) {
                var temp = "";
                for (var i = 0; i < dataList.length; i++) {
                    temp += "<tr class='a2' style=''>";
                    if (dataList[i].routeType == 1) {
                        temp += "<td>直发到站</td>";
                        temp += "<td>" + (dataList[i].createSiteName) + "</td>";
                        temp += "<td>-----</td>";
                        temp += "<td>-----</td>";
                        temp += "<td>" + (dataList[i].receiveSiteName) + "</td>";
                    } else if (dataList[i].routeType == 2) {
                        temp += "<td>直发分拣</td>";
                        temp += "<td>" + (dataList[i].createSiteName) + "</td>";
                        temp += "<td>-----</td>";
                        temp += "<td>" + (dataList[i].transferSiteName) + "</td>";
                        temp += "<td>" + (dataList[i].receiveSiteName) + "</td>";
                    } else {
                        temp += "<td>多级分拣</td>";
                        temp += "<td>" + (dataList[i].createSiteName) + "</td>";
                        temp += "<td>" + (dataList[i].transferSiteName) + "</td>";
                        temp += "<td>" + (dataList[i].receiveSiteName) + "</td>";
                        temp += "<td>-----</td>";
                    }
                    temp += "</tr>";
                }
                $("#paperTable tbody").html(temp);
            }
            // 添加分页显示
            $("#pager").html(PageBar.getHtml("init", pager.totalSize, pager.pageNo, pager.totalNo));
        } else {
            alert(data.message);
        }
    });
}