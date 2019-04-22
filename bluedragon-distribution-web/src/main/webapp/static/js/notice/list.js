$(function () {
    var queryUrl = '/notice/show';

    var attachmentPageUrl = '/notice/attachment/view/';

    var tableInit = function () {
        var oTableInit = new Object();
        oTableInit.init = function () {
            $('#dataTable').bootstrapTable({
                url: queryUrl, // 请求后台的URL（*）
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
            }, {
                field: 'theme',
                title: '主题'
            }, {
                field: 'attachment',
                title: '附件信息',
                formatter: function (value, row, index) {
                    return '<a class="attachment" href="javascript:void(0)" ><i class="glyphicon glyphicon-file"></i>&nbsp;附件信息&nbsp;</a>';
                },
                events: {
                    'click .attachment': function (e, value, row, index) {
                        layer.open({
                            id: 'attachmentFrame',
                            type: 2,
                            title: '附件信息',
                            shadeClose: true,
                            shade: 0.7,
                            shadeClose: false,
                            maxmin: true,
                            area: ['1000px', '600px'],
                            content: attachmentPageUrl + row.id
                        });
                    }
                }
            }, {
                field: 'typeText',
                title: '类型'
            }, {
                field: 'level',
                title: '级别',
                formatter: function (value, row, index) {
                    if (value == 3) {
                        return '<span style="color: red;font-weight: bold;">' + row.levelText + '</span>';
                    } else if (value == 2) {
                        return '<span style="color: green;font-weight: bold;">' + row.levelText + '</span>';
                    } else {
                        return row.levelText;
                    }
                }
            }, {
                field: 'isTopDisplay',
                title: '置顶',
                formatter: function (value, row, index) {
                    if (value == 1) {
                        return '置顶';
                    } else {
                        return '-';
                    }
                }
            }, {
                field: 'uploadTime',
                title: '上传时间',
                formatter: function (value, row, index) {
                    if (value == null) {
                        return null;
                    } else {
                        return $.dateHelper.formatDateTime(new Date(value));
                    }
                }
            }];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refresh');
        };
        return oTableInit;
    };

    tableInit().init();
});