$(function () {
    const queryUrl = '/enterpriseDistribution/listData';
    const packageDetailQueryUrl = '/enterpriseDistribution/detailListData';
    const exportUrl = '/enterpriseDistribution/toExport';
    const tableInit = function () {
        const oTableInit = new Object();
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
        oTableInit.getSearchCondition = function (_selector) {
            var params = {};
            if (!_selector) {
                _selector = ".search-param";
            }
            $(_selector).each(function () {
                var _k = this.id;
                var _v = $(this).val();
                if (_k && (_v != null && _v != '')) {
                    params[_k] = _v;
                }
            });
            return params;
        };
        oTableInit.tableColums = [{
            checkbox: true
        }, {
            field: 'waybillNo',
            title: '运单号',
            align: 'center',
            formatter: function (value, row, index) {
                return '<a class="full-collect-detail-btn" href="javascript:void(0)">' + value + '</a>'
            },
            events: {
                'click .full-collect-detail-btn': function (e, value, row, index) {
                    $packageDetailContainer.show()
                    detailTable.detailInfo = row;
                    $("#detailQueryWaybillNo").val(row.waybillNo)
                    $("#detailWaybillNo").html(row.waybillNo);
                    $("#detailOptStatus").html(row.optStatusName);
                    $("#detailExceptionReason").html(row.exceptionReasonName);
                    if (!row.addValueService) {
                       $("#detailAddValueService").html("");
                    }else if (row.addValueService === "sc-a-0124") {
                        $("#detailAddValueService").html("质检");
                    }
                    $("#detailTotalQty").html(row.totalQty);
                    $("#detailCheckedQty").html(row.checkedQty);
                    $("#detailUpdateUser").html(row.updateUser);
                    $("#detailCreateTime").html($.dateHelper.formateDateTimeOfTs(row.createTime));
                    $("#detailUpdateTime").html($.dateHelper.formateDateTimeOfTs(row.updateTime));

                    $packageDetailBsTable.bootstrapTable('refreshOptions', {url: packageDetailQueryUrl})
                    layer.open({
                        id: 'packageDetailLayer',
                        type: 1,
                        title: '增值服务-质检详情',
                        shadeClose: true,
                        shade: 0.7,
                        maxmin: true,
                        area: ['1100px', '700px'],
                        content: $packageDetailContainer,
                        success: function (layero, index) {
                        },
                        cancel: function (index, layero) {
                            layer.close(index)
                            $packageDetailContainer.hide()
                        },
                        end: function () {
                            $packageDetailContainer.hide()
                        }
                    });
                },
            }
        }, {
            field: 'optStatusName',
            title: '状态',
            align: 'center'
        }, {
            field: 'totalQty',
            title: 'sku总件数',
            align: 'center'
        }, {
            field: 'checkedQty',
            title: '已质检SKU数量',
            align: 'center'
        }, {
            field: 'exceptionReasonName',
            title: '异常原因',
            align: 'center'
        }, {
            field: 'addValueService',
            title: '增值服务',
            align: 'center',
            formatter: function (value, row, index) {
                if (!value) {
                    return "";
                }else if (value === "sc-a-0124") {
                    return "质检";
                }
            }
        }, {
            field: 'createTime',
            title: '创建时间',
            align: 'center',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        }, {
            field: 'updateUser',
            title: '操作人',
            align: 'center'
        }, {
            field: 'updateTime',
            title: '更新时间',
            align: 'center',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        }, {
            field: 'exceptionRemark',
            title: '异常备注',
            align: 'center'
        }];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
        };
        return oTableInit;
    };
    const pageInit = function () {
        var oInit = new Object();
        oInit.init = function () {

            $.datePicker.createNew({
                elem: '#createStartTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function (value, date, endDate) {
                    /*重置表单验证状态*/

                }
            });
            $.datePicker.createNew({
                elem: '#createEndTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function (value, date, endDate) {
                    /*重置表单验证状态*/

                }
            });
            $.datePicker.createNew({
                elem: '#updateStartTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function (value, date, endDate) {
                    /*重置表单验证状态*/

                }
            });
            $.datePicker.createNew({
                elem: '#updateEndTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function (value, date, endDate) {
                    /*重置表单验证状态*/

                }
            });

            //查询
            $('#btn_query').click(function () {
                $('#btn_export').attr("disabled", false);
                // var days = getDaysByDateString($('#startTime').val(), $('#endTime').val());
                // if (days > 30) {
                //     Jd.alert("查询时间不能超过30天，请缩小时间范围!");
                //     return;
                // }
                if ($("#waybillNo").val() != undefined && $("#waybillNo").val().length > 50) {
                    Jd.alert("运单号长度超出限制!");
                    return;
                }
                if ($("#updateUser").val() != undefined && $("#updateUser").val().length > 50) {
                    Jd.alert("操作人ERP长度超出限制!", );
                    return;
                }
                if ($("#createStartTime").val() != undefined && $("#createEndTime").val() != undefined ) {
                    if ($.dateHelper.formateDateTimeOfTs($("#createStartTime").val()) > $.dateHelper.formateDateTimeOfTs($("#createEndTime").val())) {
                        Jd.alert(" 结束时间不能小于开始时间!", "info");
                        return;
                    }
                }
                if ($("#updateStartTime").val() != undefined && $("#updateEndTime").val() != undefined ) {
                    if ($.dateHelper.formateDateTimeOfTs($("#updateStartTime").val()) > $.dateHelper.formateDateTimeOfTs($("#updateEndTime").val())) {
                        Jd.alert(" 结束时间不能小于开始时间!", "info");
                        return;
                    }
                }
                if ($("#updateEndTime").val() != undefined && $("#createStartTime").val() != undefined) {
                    if ($.dateHelper.formateDateTimeOfTs($("#updateEndTime").val()) < $.dateHelper.formateDateTimeOfTs($("#createStartTime").val())) {
                        Jd.alert(" 结束时间不能小于开始时间!", "info");
                        return;
                    }
                }
                tableInit().refresh();
            });

            $('#btn_reset').click(function () {
                $('#query-form').resetForm();
                tableInit().refresh();
            });
        };

        return oInit;
    };

    //导出
    function initExport(tableInit) {
        $('#btn_export').click(function () {
            // var startDate = Date.parse($('#startTime').val().replace('/-/g', '/'));
            // var endDate = Date.parse($('#endTime').val().replace('/-/g', '/'));
            // if((endDate - startDate) > (1 * 24 * 60 * 60 * 1000)){
            //     Jd.alert("只能导出一天的数据,请缩短复核时间范围!");
            //     return;
            // }
            // checkConcurrencyLimit({
            //     currentKey: exportReportEnum.ENTERPRISE_DISTRIBUTION_QUALITY_INSPECTION,
            //     checkPassCallback: function (result) {
            const params = tableInit.getSearchCondition();
            if ($("#dataTable").bootstrapTable("getOptions").totalRows > 200000) {
                Jd.confirm("已选择的数据超出20w，单次最多只支持20w数据导出，是否仍要超出？", function(val) {
                    if (!val) {
                        return;
                    } else {
                        exportData(params);
                    }
                })
            } else {
                exportData(params);
            }
            //   },checkFailCallback: function (result) {
            //         // 导出校验失败，弹出提示消息
            //         alert(result.message)
            //     }
            // });
        });
    }

    function exportData (params) {
        let param = "";
        if(params.waybillNo != undefined && params.waybillNo != "undefined"){
            param = "&waybillNo=" + params.waybillNo;
        }
        if(params.optStatus != undefined && params.optStatus != "undefined"){
            param += "&optStatus=" +  params.optStatus;
        }
        if(params.exceptionReason != undefined && params.exceptionReason != "undefined"){
            param += "&exceptionReason=" +  params.exceptionReason;
        }
        if(params.updateUser != undefined && params.updateUser != "undefined"){
            param += "&updateUser=" +  params.updateUser;
        }
        if(params.createStartTime != undefined && params.createStartTime != "undefined"){
            param += "&createStartTime=" +  params.createStartTime;
        }
        if(params.createEndTime != undefined && params.createEndTime != "undefined"){
            param += "&createEndTime=" +  params.createEndTime;
        }
        if(params.updateStartTime != undefined && params.updateStartTime != "undefined"){
            param += "&updateStartTime=" +  params.updateStartTime;
        }
        if(params.updateEndTime != undefined && params.updateEndTime != "undefined"){
            param += "&updateEndTime=" +  params.updateEndTime;
        }
        $('#btn_export').attr("disabled",true);
        location.href = exportUrl + "?" + param;
    }

    $("[data-toggle='tooltip']").tooltip();

    function initDateQuery(){
        var v = $.dateHelper.formatDate(new Date());
        $("#createStartTime").val(v+" 00:00:00");
        $("#createEndTime").val($.dateHelper.formatDate($.dateHelper.addDays(new Date(), 0)) + " 23:59:59");

        $("#updateStartTime").val(v+" 00:00:00");
        $("#updateEndTime").val($.dateHelper.formatDate($.dateHelper.addDays(new Date(), 0)) + " 23:59:59");
    }

    initDateQuery();
    tableInit().init();
    pageInit().init();
    initExport(tableInit());


    // 一单多件包裹明细
    const detailTable = {
        queryParams: {},
        detailInfo:{},
        tableColums: [
            {
                checkbox: true
            }, {
                field: 'goodsNo',
                title: '商品编码',
                align: 'center'
            }, {
                field: 'goodsName',
                title: '商品名称',
                align: 'center'
            }, {
                field: 'barcode',
                title: '69码',
                align: 'center'
            }, {
                field: 'materialCode',
                title: '客户物料编码',
                align: 'center'
            }, {
                field: 'goodsQty',
                title: '商品数量',
                align: 'center'
            }, {
                field: 'checkedQty',
                title: '已质检数量',
                align: 'center'
            }, {
                field: 'goodsCode',
                title: '货号',
                align: 'center'
            }, {
                field: 'imagePath',
                title: '图片',
                align: 'center',
                formatter: function (val, row, index) {
                    return '<a  href="' + val + '" target= "_blank">' + '图片' + '</a>'
                }
            }, {
                field: 'updateUser',
                title: '操作人',
                align: 'center'
            }, {
                field: 'updateTime',
                title: '操作时间',
                align: 'center',
                formatter : function(value){
                    return $.dateHelper.formateDateTimeOfTs(value);
                }
            }],
        refresh: function () {
            $packageDetailBsTable.bootstrapTable('refreshOptions', {pageNumber: 1});
        }
    };
    detailTable.getSearchParams = function (params) {
        detailTable.queryParams.limit = params.limit
        detailTable.queryParams.offset = params.offset
        detailTable.queryParams.waybillNo = $("#detailQueryWaybillNo").val()
        return detailTable.queryParams;
    };
    const $packageDetailContainer = $('#packageDetailContainer')
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
        showRefresh: false, // 是否显示刷新按钮
        minimumCountColumns: 2, // 最少允许的列数
        clickToSelect: false, // 是否启用点击选中行
        showToggle: false, // 是否显示详细视图和列表视图的切换按钮
        strictSearch: true,
        columns: detailTable.tableColums
    });

});
