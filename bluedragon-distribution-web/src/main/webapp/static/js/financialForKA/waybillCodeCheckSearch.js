$(function () {
    var queryUrl = '/waybillCodeCheckForKA/listData';
    var exportUrl = '/waybillCodeCheckForKA/toExport';
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
        },{
            field: 'waybillCode',
            title: '运单号',
            align: 'center'
        },{
            field: 'compareCode',
            title: '比较单号',
            align: 'center'
        },{
            field: 'busiCode',
            title: '商家编码',
            align: 'center'
        }, {
            field: 'busiName',
            title: '商家名称',
            align: 'center'
        },{
            field: 'operateSiteCode',
            title: '操作站点',
            align: 'center'
        },{
            field: 'operateSiteName',
            title: '操作站点名称',
            align: 'center'
        },{
            field: 'checkResult',
            title: '校验结果',
            align: 'center',
            formatter: function (value, row, index) {
                return (value != null && value == "1") ? "成功" : "失败";
            }
        },{
            field: 'operateErp',
            title: '操作人ERP',
            align: 'center'
        },{
            field: 'operateTime',
            title: '操作时间',
            align: 'center',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        }];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
        };
        return oTableInit;
    };
    var formStartDateStr = '';
    var pageInit = function () {
        var oInit = new Object();
        var startTimeControl;
        var endTimeControl;
        oInit.init = function () {

            /*起始时间*/
            /*截止时间*/
            startTimeControl=  $.datePicker.createNew({
                elem: '#startTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/
                    var endDate = $("#endTime").val();
                    if (!endDate || endDate == '') return;
                    var oneDay=1000 * 60 * 60 * 24;
                    var startTimeStr=$("#startTime").val();
                    var startDate = new Date(value.replace('-', '/'));
                    var endDate = new Date($("#endTime").val().replace('-', '/'));
                    console.log(startDate, endDate);
                    if ((startDate.getTime() - endDate.getTime()) > 0) {
                        setTimeout(function () {
                            $("#startTime").val(startTimeStr);
                        }, 0);
                        return layer.msg("开始时间不能超过结束时间");
                    }
                    var days = parseInt((endDate.getTime() - startDate.getTime()) / (oneDay));
                    if (days > 7) {
                        setTimeout(function () {
                            $("#startTime").val(startTimeStr);
                        }, 0);
                        return layer.msg('查询天数不可超过7天');
                    }
                }
            });
            endTimeControl= $.datePicker.createNew({
                elem: '#endTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date){
                    var startDateStr = $("#startTime").val();
                    if (!startDateStr || startDateStr == '') return;
                    var oneDay=1000 * 60 * 60 * 24;
                    var endTimeStr=$("#endTime").val();
                    var endDate = new Date(value.replace('-', '/'));
                    var startDate = new Date($("#startTime").val().replace('-', '/'));
                    console.log(startDate, endDate);
                    if ((startDate.getTime() - endDate.getTime()) > 0) {
                        setTimeout(function () {
                            $("#endTime").val(endTimeStr);
                        }, 0);
                        return layer.msg("结束时间不能早于开始时间");
                    }
                    var days = parseInt((endDate.getTime() - startDate.getTime()) / (oneDay));
                    if (days > 7) {
                        setTimeout(function () {
                            $("#endTime").val(endTimeStr);
                        }, 0);
                        return layer.msg('查询天数不可超过7天');
                    }
                }
            });

            //查询
            $('#btn_query').click(function () {
                if($("#endTime").val()==""){
                    alert("请选择结束时间");
                    return ;
                }
                var oneDay=1000 * 60 * 60 * 24;
                var startTimeStr=$("#startTime").val();
                var startDate = new Date(startTimeStr.replace('-', '/'));
                var endDate = new Date($("#endTime").val().replace('-', '/'));
                var days = parseInt((endDate.getTime() - startDate.getTime()) / (oneDay));
                if(days>7){
                    alert("选择时间不能超过7天");
                    return;
                }
                tableInit().refresh();
            });

            //查看导出任务
            $('#btn_to_export').click(function () {
                window.location.href="/waybillCodeCheckForKA/toSearchExportTaskIndex";
            });

            //返回
            $('#btn_reback').click(function () {
                window.location.href="/waybillCodeCheckForKA/toIndex";
            });

        };

        return oInit;
    };

    //导出
    function initExport(tableInit) {
        $('#btn_export').click(function () {
            var params = tableInit.getSearchCondition();
            // 先请求校验，用现有参数
            return reqCheckExport(params);
            // 再请求导出，用现有参数
            var form = $("<form method='post'></form>"),
                input;
            form.attr({"action": exportUrl});

            $.each(params, function (key, value) {
                input = $("<input type='hidden' class='search-param'>");
                input.attr({"name": key});
                input.val(value);
                form.append(input);
            });
            form.appendTo(document.body);
            form.submit();
            document.body.removeChild(form[0]);
        });
    }

    // 请求校验导出是否超限制
    function reqCheckExport(data) {
        var loadingIndex = layer.load('请稍候...');
        $.ajax({
            url: '/waybillCodeCheckForKA/exortCheck',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(data),
            contentType:"json/application",
            success: function (result) {
                layer.close(loadingIndex);
                if (result && result.code == 200) {
                    // 校验成功了，请求导出接口
                    layer.close(loadingIndex);
                    reqExportData(data);
                } else {
                    layer.close(loadingIndex);
                    // 校验失败了，提示信息
                    layer.msg(result.message)
                }
            },
            error: function (e) {
                layer.close(loadingIndex);
                layer.msg('网络繁忙');
            }
        })
    }

    // 请求导出
    function reqExportData(data) {
        var loadingIndex = layer.load('请稍候...');
        $.ajax({
            url: '/waybillCodeCheckForKA/toExport',
            type: 'POST',
            dataType: 'json',
            data: data,
            success: function (result) {
                layer.close(loadingIndex);
                if (result && result.code == 200) {
                    // 校验成功了，请求导出接口
                    layer.close(loadingIndex);
                    layer.msg('导出任务创建成功，请稍候点击[查看导出任务]查看');
                } else {
                    layer.close(loadingIndex);
                    // 校验失败了，提示信息
                    layer.msg(result.message)
                }
            },
            error: function (e) {
                layer.close(loadingIndex);
                layer.msg('网络繁忙');
            }
        })
    }

    initSelect();
    initDateQuery();
    tableInit().init();
    pageInit().init();
    initExport(tableInit());

});

function initDateQuery(){
    var v = $.dateHelper.formatDate(new Date());
    $("#startTime").val(v+" 00:00:00");
    $("#endTime").val(v+" 23:59:59");
}

function initSelect() {
    var defualt = $("#query-form #checkResultSelect").val();
    $("#query-form #checkResult").val(defualt);
    $("#query-form #checkResultSelect").on('change', function (e) {
        var v = $("#query-form #checkResultSelect").val();
        if (v == 0 || v == 1) {
            $("#query-form #checkResult").val(v);
        } else {
            $("#query-form #checkResult").val(null);
        }
    });
}