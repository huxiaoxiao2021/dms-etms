$(function () {
    var queryUrl = '/exportLog/exportLogList';
    var exportUrl = '/waybillCodeCheckForKA/toExport';
    var tableInit = function () {
        var oTableInit = new Object();
        oTableInit.init = function () {
            $('#dataTable').bootstrapTable({
                url: queryUrl, // 请求后台的URL（*）
                method: 'post', // 请求方式（*）
                queryParams: oTableInit.getSearchParams, // 查询参数（*）
                resPonseHandler: oTableInit.getResponseHeader,
                height: 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
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
                minimumCountColumns: 2, // 最少允许的列数
//				showPaginationSwitch : true, // 是否显示分页关闭按钮
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
        oTableInit.getSearchCondition = function(_selector) {
            var params = {};
            if (!_selector) {
                _selector = ".search-param";
            }
            $(_selector).each(function () {
                var _k = this.id;
                var _v = $(this).val();
                if(_k && (_v != null && _v != '')){
                    params[_k] = _v;
                }
            });
            return params;
        };
        // 获取返回值的操作
        oTableInit.getResponseHeader = function(data) {
            return data;
        };
        oTableInit.reqDeleteItem = function(data) {
            var loadingIndex = layer.load('请稍候...');
            $.ajax({
                url: '/exportLog/deleteFile',
                type: 'POST',
                dataType: 'json',
                data: data,
                success: function (result) {
                    layer.close(loadingIndex);
                    if (result && result.code == 200) {
                        layer.msg('删除成功');
                        $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
                    } else {
                        layer.msg(result.message)
                    }
                },
                error: function (e) {
                    layer.close(loadingIndex);
                    layer.msg('网络繁忙');
                }
            })
        };
        oTableInit.tableColums = [{
            field: 'id',
            visible: false
        },{
            field: 'fileName',
            visible: false
        },{
            field: 'exportCode',
            title: '导出任务号',
            align: 'center'
        },{
            field: 'createTime',
            title: '创建时间',
            align: 'center',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        },{
            field: 'status',
            title: '任务状态',
            align: 'center',
            formatter: function (value, row, index) {
                var status = parseInt(value), str = '';
                switch (status) {
                    case 0:
                        str = '初始化';break;
                    case 1:
                        str = '执行中';break;
                    case 2:
                        str = '执行成功';break;
                    case 3:
                        str = '执行失败';break;
                    default:
                        str = '-';break;
                }
                return str;
            }
        },{
            field: '',
            title: '操作',
            align: 'center',
            formatter: function (value, row, index) {
                return [
                    '<a class="btn-link btn-xs download" href="javascript:void(0)" title="下载">',
                    '下载',
                    '</a>  ',
                    '<a class="btn-link btn-xs delete" href="javascript:void(0)" title="删除">',
                    '删除',
                    '</a>'
                ].join('')
            },
            events: {
                'click .download': function (e, value, row, index) {
                    // 下载按钮点击事件 ，直接打开页面，传一个东西过去
                    window.open('/exportLog/downLoadFile?fileName=' + row.exportCode + '&_t=' + new Date().getTime(),'_blank');
                },
                'click .delete': function (e, value, row, index) {
                    // 调用删除接口，然后刷新列表
                    oTableInit.reqDeleteItem(row);
                }
            }
        }];
        return oTableInit;
    };
    var pageInit = function () {
        var oInit = new Object();
        oInit.init = function () {
            //刷新
            $('#btn_query').click(function () {
                $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
            });

            //返回
            $('#btn_reback').click(function () {
                window.location.href="/waybillCodeCheckForKA/toSearchIndex";
            });
        };

        return oInit;
    };

    tableInit().init();
    pageInit().init();

});