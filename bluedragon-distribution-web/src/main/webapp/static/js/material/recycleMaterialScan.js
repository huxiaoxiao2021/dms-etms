$(function () {
    let queryUrl = '/recycleMaterialScan/listData';
    let tableInit = function () {
        let oTableInit = {};
        oTableInit.init = function () {
            $('#dataTable').bootstrapTable({
                url: queryUrl, // 请求后台的URL（*）
                method: 'post', // 请求方式（*）
                toolbar: '#toolbar', // 工具按钮用哪个容器
                queryParams: oTableInit.getSearchParams, // 查询参数（*）
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
//				search : true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
                showRefresh: true, // 是否显示刷新按钮
                minimumCountColumns: 2, // 最少允许的列数
                clickToSelect: true, // 是否启用点击选中行
                showToggle: true, // 是否显示详细视图和列表视图的切换按钮
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
        oTableInit.tableColums = [
            {
                title: "序号",
                formatter: function(value, row, index) {
                    // 显示行号
                    return index + 1;
                },
                align: 'center',
                width: '50px'
            },
            {
                field: 'materialType',
                title: '物资类型',
                align: 'center',
                formatter : function(value){
                    return value === 1 ? '保温箱' : '其它';
                }
            },
            {
                field: 'materialCode',
                title: '物资编号',
                align: 'center'
            },
            {
                field: 'boardCode',
                title: '板号',
                align: 'center'
            },
            {
                field: 'scanType',
                title: '扫描类型',
                align: 'center',
                formatter: function (value) {
                    if (value === 1) {
                        return '入库';
                    }
                    else if (value === 2) {
                        return '出库';
                    }
                    else {
                        return '-';
                    }
                }
            },
            {
                field: 'createSiteName',
                title: '操作机构',
                align: 'center'
            },
            {
                field: 'userErp',
                title: '操作人erp',
                align: 'center'
            },
            {
                field: 'operateTime',
                title: '操作时间',
                align: 'center',
                formatter: function (value) {
                    return $.dateHelper.formateDateTimeOfTs(value);
                }
            },
            {
                field: 'materialStatus',
                title: '物资状态',
                align: 'center',
                formatter: function (value) {
                    if (value === 1) {
                        return '已入库未出库';
                    }
                    else if (value === 2) {
                        return '已出库';
                    }
                    else {
                        return '-';
                    }
                }
            }
        ];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
        };
        return oTableInit;
    };
    let pageInit = function () {
        let oInit = {};
        oInit.init = function () {

            /*起始时间*/
            /*截止时间*/
            $.datePicker.createNew({
                elem: '#startTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });
            $.datePicker.createNew({
                elem: '#endTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });

            //查询
            $('#btn_query').click(function () {
                let days = getDaysByDateString($('#startTime').val(), $('#endTime').val());
                if (days > 30) {
                    Jd.alert("查询时间不能超过30天，请缩小时间范围!");
                    return;
                }
                tableInit().refresh();
            });

        };

        return oInit;
    };

    initSelect();
    initDateQuery();
    tableInit().init();
    pageInit().init();

});

function  getDaysByDateString(dateString1, dateString2) {
    let startDate = Date.parse(dateString1.replace('/-/g', '/'));
    let endDate = Date.parse(dateString2.replace('/-/g', '/'));
    return (endDate - startDate) / (24 * 60 * 60 * 1000);
}

function initDateQuery(){
    let today = $.dateHelper.formatDate(new Date());
    let fifteenDayAgo = $.dateHelper.addDays(new Date(), -15);
    $("#startTime").val(fifteenDayAgo + " 00:00:00");
    $("#endTime").val(today + " 23:59:59");
}

function initSelect() {
    let materialStatusSelect = $("#materialStatusSelect"),
        materialStatus = $("#materialStatus");
    let defaultV = materialStatusSelect.val();
    materialStatus.val(defaultV);
    materialStatusSelect.on('change', function () {
        let v = materialStatusSelect.val();
        if (v === 1 || v === 2) {
            materialStatus.val(v);
        }
        else {
            materialStatus.val(null);
        }
    });

    let scanTypeSelect = $("#scanTypeSelect"),
        scanType = $("#scanType");
    defaultV = scanTypeSelect.val();
    scanType.val(defaultV);
    scanTypeSelect.on('change', function () {
        let v = scanTypeSelect.val();
        if (v === 1 || v === 2) {
            scanType.val(v);
        }
        else {
            scanType.val(null);
        }
    });

    let materialTypeSelect = $("#materialTypeSelect"),
        materialType = $("#materialType");
    defaultV = materialTypeSelect.val();
    materialType.val(defaultV);
    materialTypeSelect.on('change', function () {
        let v = materialTypeSelect.val();
        if (v === 1 || v === 99) {
            materialType.val(v);
        }
        else {
            materialType.val(null);
        }
    });
}