var query_sendCode_detail_url = "/exception/sendCodeHandler/querySendCodeDetails";
var waybill_trance_url = "/waybill/trackInfo/";

$(document).ready(function () {
    $('#dataTable').bootstrapTable({
        url : query_sendCode_detail_url, // 请求后台的URL（*）
        method : 'post', // 请求方式（*）
        toolbar : '#toolbar', // 工具按钮用哪个容器
        // height : 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
        uniqueId : "ID", // 每一行的唯一标识，一般为主键列
        pagination : true, // 是否显示分页（*）
        pageNumber : 1, // 初始化加载第一页，默认第一页
        pageSize : 10, // 每页的记录行数（*）
        pageList : [ 10, 25, 50, 100 ], // 可供选择的每页的行数（*）
        cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        sidePagination : "server", // 分页方式：client客户端分页，server服务端分页（*）
        striped : true, // 是否显示行间隔色
        showColumns : true, // 是否显示所有的列
        sortable : true, // 是否启用排序
        sortOrder : "asc", // 排序方式
        search : true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
        showRefresh : true, // 是否显示刷新按钮
        minimumCountColumns : 2, // 最少允许的列数
        clickToSelect : true, // 是否启用点击选中行
        showToggle : true, // 是否显示详细视图和列表视图的切换按钮
//				showPaginationSwitch : true, // 是否显示分页关闭按钮
        strictSearch : true,
        // icons: {refresh: "glyphicon-repeat", toggle:
        // "glyphicon-list-alt", columns: "glyphicon-list"},
        // search:false,
        // cardView: true, //是否显示详细视图
        // detailView: true, //是否显示父子表
        // showFooter:true,
        // paginationVAlign:'center',
        // singleSelect:true,
        queryParams:function (params) {
            var sendCodeStr = $("#sendCodes").val();
            var type = $("#type").val();
            var request = {
                pageNo : params.offset + 1,
                pageSize : params.limit,
                type: type
            };
            if (sendCodeStr != null && sendCodeStr != "") {
                request.sendCodes = JSON.parse(sendCodeStr);
            }
            return request;
        },
        onRefreshOptions:function () {
            this.tableBlocker = $.pageBlocker.block();
        },
        onLoadSuccess:function (res) {
            if(res.rows == null){
                $('#data-table').bootstrapTable('removeAll');
            }
            $.pageBlocker.close(this.tableBlocker);
        },
        onLoadError:function (res) {
            $('#data-table').bootstrapTable('removeAll');
            $.pageBlocker.close(this.tableBlocker);
        },
        columns : [
            {
                field : "",
                title : "序号",
                align : "center",
                halign : "center",
                visible:false,
                format: function (value, row, index) {
                    return index;
                }
            },
            {
                field : "packageCode",
                title : "包裹号",
                align : "center",
                halign : "center"

            },
            {
                field : "waybillCode",
                title : "运单号",
                align : "center",
                halign : "center"
            },
            {
                field : "boxCode",
                title : "箱号",
                align : "center",
                halign : "center"
            },
            {
                field : "",
                title : "查看全程跟踪",
                align : "center",
                halign : "center",
                formatter:function(value,row,index) {
                    return '<a class="showTrance" style="cursor: pointer" >&nbsp;查看&nbsp;</a>';
                },
                events:{
                    'click .showTrance':function (event,value, row, index) {
                        var packageCode = row.packageCode;
                        window.open($("#waybillAddress").val() + waybill_trance_url + packageCode, "target");
                    }
                }
            }
        ]
    });
});
