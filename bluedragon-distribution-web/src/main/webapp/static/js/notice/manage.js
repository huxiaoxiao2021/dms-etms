$(function () {
    var queryUrl = '/notice/show';

    var addPageUrl = '/notice/addView';

    var deleteUrl = '/notice/deleteByIds';

    var attachmentPageUrl = '/notice/attachment/view/';

    var tableInit = function () {
        var oTableInit = new Object();
        oTableInit.init = function () {
            $('#dataTable').bootstrapTable({
                url: queryUrl, // 请求后台的URL（*）
                method: 'post', // 请求方式（*）
                toolbar: '#toolbar', // 工具按钮用哪个容器
                height: 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                uniqueId: "ID", // 每一行的唯一标识，一般为主键列
                pagination: false, // 是否显示分页（*）
                cache: false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                sidePagination: "server", // 分页方式：client客户端分页，server服务端分页（*）
                striped: true, // 是否显示行间隔色
                showColumns: true, // 是否显示所有的列
                sortable: true, // 是否启用排序
                sortOrder: "asc", // 排序方式
                showRefresh: true, // 是否显示刷新按钮
                minimumCountColumns: 2, // 最少允许的列数
                clickToSelect: true, // 是否启用点击选中行
                showToggle: true, // 是否显示详细视图和列表视图的切换按钮
                strictSearch: true,
                columns: oTableInit.tableColums
            });
        };

        oTableInit.tableColums = [
            {
                checkbox: true
            }, {
                field: 'id',
                title: 'ID',
                visible: false
            }, {
                field: 'theme',
                title: '主题',
                align: 'center',
                width: 300
            }, {
                field: 'typeText',
                title: '类型',
                align: 'center',
                width: 50
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
                },
                align: 'center',
                width: 50
            }, {
                field: 'isTopDisplay',
                title: '置顶',
                formatter: function (value, row, index) {
                    if (value == 1) {
                        return '置顶';
                    } else {
                        return '-';
                    }
                },
                align: 'center',
                width: 50
            }, {
                field: 'uploadTime',
                title: '上传时间',
                formatter: function (value, row, index) {
                    if (value == null) {
                        return null;
                    } else {
                        return $.dateHelper.formatDateTime(new Date(value));
                    }
                },
                align: 'center',
                width: 100
            }, {
                field: 'attachment',
                title: '附件信息',
                formatter: function (value, row, index) {
                    return '<a class="attachment" href="javascript:void(0)" ><i class="glyphicon glyphicon-file"></i>&nbsp;附件&nbsp;</a>';
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
                            area: ['800px', '450px'],
                            content: attachmentPageUrl + row.id
                        });
                    }
                },
                align: 'center',
                width: 50
            }];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refresh');
        };
        return oTableInit;
    };

    var pageInit = function () {
        var oInit = new Object();
        oInit.init = function () {
            // 增加
            $('#btn_add').click(function () {
                layer.open({
                    id: 'addFrame',
                    type: 2,
                    title: '新增通知',
                    shadeClose: true,
                    shade: 0.7,
                    maxmin: true,
                    shadeClose: false,
                    area: ['800px', '500px'],
                    content: addPageUrl
                });
            });

            // 删除
            $('#btn_delete').click(function () {
                var rows = $('#dataTable').bootstrapTable('getSelections');
                if (rows.length < 1) {
                    alert("请选择需要删除的记录");
                    return;
                }
                $.msg.confirm("是否删除这些数据?", function () {
                    var params = [];
                    for (var i in rows) {
                        params.push(rows[i].id);
                    }
                    $.ajaxHelper.doPostSync(deleteUrl, JSON.stringify(params), function (res) {
                        if (res != null && res.code == 200) {
                            $.msg.ok('操作成功，删除' + res.data + '条。');
                            tableInit().refresh();
                        } else {
                            $.msg.error("操作失败！", res.message);
                        }
                    });
                });
            });

            $('#btn_query').click(function () {
                tableInit().refresh();
            });
        };
        return oInit;
    };

    tableInit().init();
    pageInit().init();
});