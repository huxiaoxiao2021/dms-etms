$(function () {
    var inspectionQueryUrl = '/abnormalDispose/abnormalDispose/inspection/listData';
    var tableInit = function () {
        var oTableInit = new Object();
        oTableInit.init = function () {
            $('#dataTableInspection').bootstrapTable({
                url: inspectionQueryUrl, // 请求后台的URL（*）
                method: 'post', // 请求方式（*）
                toolbar: '#toolbar_inspection', // 工具按钮用哪个容器
                queryParams: oTableInit.getSearchParams, // 查询参数（*）
                //height : 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                uniqueId: "ID", // 每一行的唯一标识，一般为主键列
                pagination: true, // 是否显示分页（*）
                pageNumber: 1, // 初始化加载第一页，默认第一页
                pageSize: 10, // 每页的记录行数（*）
                pageList: [10, 25, 50, 100], // 可供选择的每页的行数（*）
                cache: false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                sidePagination: "server", // 分页方式：client客户端分页，server服务端分页（*）
                striped: true, // 是否显示行间隔色
                showColumns: true, // 是否显示所有的列
                sortable: true, // 是否启用排序
                sortOrder: "asc", // 排序方式
//				search : true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
                showRefresh: true, // 是否显示刷新按钮
                minimumCountColumns: 2, // 最少允许的列数
                clickToSelect: true, // 是否启用点击选中行
                showToggle: false, // 是否显示详细视图和列表视图的切换按钮
//				showPaginationSwitch : true, // 是否显示分页关闭按钮
                strictSearch: true,
                // icons: {refresh: "glyphicon-repeat", toggle:
                // "glyphicon-list-alt", columns: "glyphicon-list"},
                // search:false,
                // cardView: true, //是否显示详细视图
                // detailView: true, //是否显示父子表
                // showFooter:true,
                // paginationVAlign:'center',
                // singleSelect:true,
                columns: oTableInit.tableColums
            });
        };
        oTableInit.getSearchParams = function (params) {

            var temp = oTableInit.getSearchCondition();
            if (!temp) {
                temp = {};
            }
            temp.offset = params.offset;
            temp.limit = params.limit;
            // 这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            return temp;
        };
        /**
         * 获取查询参数
         * @param _selector 选择器（默认为'.search-param'）
         */
        oTableInit.getSearchCondition = function (_selector) {
            var params = {};
            if (!_selector) {
                _selector = ".search-param-inspection";
            }
            $(_selector).each(function () {
                var _k = this.name;
                var _v = $(this).val();
                if (_k && _v) {
                    params[_k] = _v;
                }
            });
            return params;
        };

        // $('#dataTableInspection').bootstrapTable('destroy');//这里必须要添加这个销毁，否则新增、修改、查看的切换可编辑列表中的数据可能加载出现问题。

        oTableInit.tableColums = [/*{
         checkbox : false
         }, */{
            field: 'sealVehicleDate',
            title: '解封车时间',
            formatter: function (value, row, index) {
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        }, {
            field: 'waybillCode',
            title: '运单'
        }, {
            field: 'prevAreaName',
            title: '上级区域'
        }, {
            field: 'prevSiteName',
            title: '上级站点'
        }, {
            field: 'endCityName',
            title: '目的城市'
        }, {
            field: 'endSiteName',
            title: '目的站点'
        }, {
            field: 'isDispose',
            title: '是否提报异常',
            formatter: function (value, row, index) {
                if(row.qcCode){
                    return '是';
                }else{
                    return '否';
                }
          }
        }, {
            field: 'qcCode',
            title: '异常编码',
            formatter: function (value, row, index) {
                return "<a href='#' onclick=\"updateQc('"+value+"',"+index+")\">维护</a>";
            }
        }, {
            field: 'createUser',
            title: '异常提交人'
        }, {
            field: 'createTime',
            title: '异常提交时间',
            formatter: function (value, row, index) {
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        }, {
            field: 'temp',
            title: '提交异常',
            formatter:function(value, row, index){
                return "<a href='#' onclick='sumbitQc()'>提交</a>";
            }
        }];
        oTableInit.refresh = function () {
            $('#dataTableInspection').bootstrapTable('refreshOptions', {pageNumber: 1});
        };
        return oTableInit;
    };
    var pageInit = function () {
        var oInit = new Object();
        var postdata = {};
        oInit.init = function () {
            $('#inspectionDetail').hide();
            $('#btn_query_inspection').click(function () {
                tableInit().refresh();
            });
            // 初始化页面上面的按钮事件
            $('#btn_back_inspection').click(function () {
                $('#inspectionDetail').hide();
                $('#dataTableMainDiv').show();
            });
        };
        return oInit;
    };
    var initSelect = function () {
        //ID 冲突。。select2插件有问题
        $("#query-form-inspection #isDisposeInspectionSelect").on('change', function (e) {
            var v = $("#query-form-inspection #isDisposeInspectionSelect").val();
            if (v == 0 || v == 1) {
                $("#query-form-inspection #isDisposeInspection").val(v);
            }
        });
    }
    tableInit().init();
    pageInit().init();
    initSelect();

});

function sumbitQc() {
    alert("提交异常")
}
function queryinspection(transferNo){
    $('#dataTableMainDiv').hide();
    $('#inspectionDetail').show();
    $('#transferNoInspection').val(transferNo);
    $('#dataTableInspection').bootstrapTable('refreshOptions', {pageNumber: 1});
}
function updateQc(value, index){
    alert(value)
}
