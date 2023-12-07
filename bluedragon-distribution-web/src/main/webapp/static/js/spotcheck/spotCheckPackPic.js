$(function() {

    var waybillCode = $('#waybillCode').val()
    var reviewSiteCode = $('#reviewSiteCode').val()
    var reviewSource = $('#reviewSource').val()
    var url = '/spotCheckReport/searchPicture';

    // 一单多件包裹明细
    const detailTable = {
        queryParams: {},
        tableColums: [
            {
                field: 'waybillCode',
                title: '运单号',
                align: 'center',
                visible: true
            },{
                field: 'packageCode',
                title: '包裹号',
                align: 'center',
                visible: true
            }, {
                field: 'url',
                title: '图片/视频链接',
                align: 'center',
                formatter: function (value, row, index) {
                    let allPictureUrl = '';
                    if (value) {
                        const total = value.split(";");
                        let order = 0;
                        for (const single of total) {
                            if (single) {
                                order++;
                                allPictureUrl += '<a href="' + single + '" target="_blank">图片' + order + '</a>&nbsp;'
                            }
                        }
                    }
                    if (row.videoUrl) {
                        allPictureUrl += '<a href="' + row.videoUrl + '" target="_blank">视频1' + '</a>&nbsp;'
                    }
                    return allPictureUrl;
                }
            }
        ],
        refresh: function () {
            $packageDetailBsTable.bootstrapTable('refreshOptions', {pageNumber: 1});
        }
    }

    detailTable.getSearchParams = function (params) {
        var temp = detailTable.getSearchCondition();
        if (!temp) {
            temp = {};
        }
        temp.waybillCode = waybillCode;
        temp.packageCode = $('#packageCode').val();
        temp.reviewSiteCode = reviewSiteCode;
        temp.reviewSource = reviewSource;
        temp.offset = params.offset;
        temp.limit = params.limit;
        // 这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
        return temp;
    };

    /**
     * 获取查询参数
     * @param _selector 选择器（默认为'.search-param'）
     */
    detailTable.getSearchCondition = function(_selector) {
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

    const $packageDetailBsTable = $('#packageDetailTable').bootstrapTable({
        url: '', // 请求后台的URL（*）
        method: 'post', // 请求方式（*）
        toolbar: '#detailToolbar', // 工具按钮用哪个容器
        queryParams: detailTable.getSearchParams, // 查询参数（*）
        height: 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
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
        showRefresh: true, // 是否显示刷新按钮
        minimumCountColumns: 2, // 最少允许的列数
        clickToSelect: true, // 是否启用点击选中行
        showToggle: true, // 是否显示详细视图和列表视图的切换按钮
        strictSearch: true,
        columns: detailTable.tableColums,
        responseHandler: function(res) {
            return {
                "total": res.data.total,//总页数
                "rows": res.data.data   //数据
            };
        },
    });

    var tableInit = function() {

        $packageDetailBsTable.bootstrapTable('refreshOptions', {url: url})
        //查询
        $('#btn_query').click(function () {
            detailTable.refresh();
        });
    };

    tableInit();

});
