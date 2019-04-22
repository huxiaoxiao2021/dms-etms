$(function () {
    var queryUrl = '/notice/attachment/detail';

    var downloadUrl = '/notice/attachment/download';

    var tableInit = function () {
        var oTableInit = new Object();
        oTableInit.init = function () {
            $('#dataTable').bootstrapTable({
                url: queryUrl, // 请求后台的URL（*）
                queryParams: oTableInit.getSearchParams, // 查询参数（*）
                method: 'post', // 请求方式（*）
                height: 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                uniqueId: "ID", // 每一行的唯一标识，一般为主键列
                pagination: false, // 是否显示分页（*）
                cache: false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                sidePagination: "server", // 分页方式：client客户端分页，server服务端分页（*）
                striped: true, // 是否显示行间隔色
                sortable: true, // 是否启用排序
                sortOrder: "asc", // 排序方式
                minimumCountColumns: 2, // 最少允许的列数
                clickToSelect: false, // 是否启用点击选中行
                strictSearch: true,
                columns: oTableInit.tableColums
            });
        };

        oTableInit.tableColums = [
            {
                field: 'id',
                title: 'ID',
                visible: false
            },
            {
                field: 'fileName',
                title: '文件名'
            },
            {
                field: 'type',
                title: '文件类型'
            },
            {
                field: 'size',
                title: '文件大小(KB)'
            },
            {
                field: 'url',
                title: '文件链接',
                formatter: function (value, row, index) {
                    return '<a class="link" href="javascript:void(0)" >下载</a>';
                },
                events: {
                    'click .link': function (e, value, row, index) {
                        var download = downloadUrl + '?attachmentId=' + row.id;
                        window.open(download);
                    }
                }
            }, {
                field: 'createTime',
                title: '创建时间',
                formatter: function (value, row, index) {
                    if (value == null) {
                        return null;
                    } else {
                        return $.dateHelper.formatDateTime(new Date(value));
                    }
                }
            }];

        oTableInit.getSearchParams = function (params) {
            var queryParam = {};
            queryParam.noticeId = $("#notice-id").val();
            return queryParam;
        };

        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refresh');
        };
        return oTableInit;
    };

    tableInit().init();
});


