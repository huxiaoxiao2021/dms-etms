var queryUrl = '/inspectionCheck/listData';
var queryPackageUrl = '/inspectionCheck/listPackage';

$(function () {

    $('#fileField').hide();
    //浏览
    $('#btn_browse').click(function () {
        $('#fileField').click();
    });

    var tableInit = function () {
        var oTableInit = new Object();
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
        oTableInit.tableColums = [{
            checkbox: true
        }, {
            field: 'waybillCode',
            title: '运单号',
            align: 'center'
        },{
            field: 'packageNum',
            title: '总包裹数',
            align: 'center'
        }, {
            field: 'inspectionedPackNum',
            title: '已验包裹数',
            align: 'center'
        }, {
            field: 'gather',
            title: '是否集齐',
            align: 'center',
            formatter: function (value, row, index) {
                return $("#query-form #gatherTypeSelect").val() == 1 ? "是" : "否";
            }
        },{
            field: 'operate',
            title: '操作',
            align: 'center',
            formatter : function (value, row, index) {
                var flage;
                flage = '<a class="search" href="javascript:void(0)" ><i class="glyphicon glyphicon-search"></i>&nbsp;查看已验包裹&nbsp;</a>'
                return flage;
            },
            events: {
                'click .search': function(e, value, row, index) {
                    window.open("/inspectionCheck/listPackage/?waybillCode="+row.waybillCode);
                }
            }
        }];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
        };
        return oTableInit;
    };
    var pageInit = function () {
        var oInit = new Object();
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
                var days = getDaysByDateString($('#startTime').val(),$('#endTime').val());
                if(days > 2){
                    Jd.alert("查询时间不能超过2天，请缩小时间范围!");
                    return;
                }
                let clickStrict = ClickFrequencyUtil.controlClick($('#query-form'), $('#btn_query'));
                if (!clickStrict) {

                    tableInit().refresh();
                }
            });

        };

        return oInit;
    };

    initSelect();
    initDateQuery();
    tableInit().init();
    pageInit().init();

});


function initDateQuery(){
    var v = $.dateHelper.formatDate(new Date());
    $("#startTime").val(v+" 00:00:00");
    $("#endTime").val(v+" 23:59:59");
}

function  getDaysByDateString(dateString1,dateString2) {
    var startDate = Date.parse(dateString1.replace('/-/g', '/'));
    var endDate = Date.parse(dateString2.replace('/-/g', '/'));
    var days = (endDate - startDate) / (1 * 24 * 60 * 60 * 1000);
    return days;
}

function initSelect() {
    var defualt = $("#query-form #gatherTypeSelect").val();
    $("#query-form #gatherType").val(defualt);
    $("#query-form #gatherTypeSelect").on('change', function (e) {
        var v = $("#query-form #gatherTypeSelect").val();
        if (v == 1 || v == 2 || v == 3) {
            $("#query-form #gatherType").val(v);
        } else {
            $("#query-form #gatherType").val(null);
        }
    });
}
