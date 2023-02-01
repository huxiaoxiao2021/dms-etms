$(function () {
    var queryUrl = '/spotCheckReport/listData';
    var packageDetailQueryUrl = '/spotCheckReport/packageDetailListData';
    var exportUrl = '/spotCheckReport/toExport';
    var upExcessPictureUrl = '/spotCheckReport/toUpload';
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
                showRefresh: true, // 是否显示刷新按钮
                minimumCountColumns: 2, // 最少允许的列数
                clickToSelect: true, // 是否启用点击选中行
                showToggle: true, // 是否显示详细视图和列表视图的切换按钮
                strictSearch: true,
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
            field: 'reviewDate',
            title: '复核日期',
            align: 'center',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        }, {
            field: 'waybillCode',
            title: '运单号',
            align: 'center'
        },{
            field: 'businessType',
            title: '业务类型',
            align: 'center',
            formatter: function (value, row, index) {
                if(value != null && value === 1){
                    return "B网";
                }
                if(value != null && value === 2){
                    return "医药";
                }
                return "C网";
            }
        },{
            field: 'productTypeName',
            title: '产品标识',
            align: 'center'
        }, {
            field: 'merchantCode',
            title: '配送商家编号',
            align: 'center'
        }, {
            field: 'merchantName',
            title: '商家名称',
            align: 'center'
        }, {
            field: 'isTrustMerchant',
            title: '信任商家',
            align: 'center',
            formatter: function (value, row, index) {
                return value === 1 ? "是" : "否";
            }
        },{
            field: 'reviewOrgName',
            title: '复核区域',
            align: 'center'
        }, {
            field: 'reviewSiteCode',
            title: '复核分拣',
            align: 'center',
            visible: false
        }, {
            field: 'reviewSiteName',
            title: '复核分拣'
        },{
            field: 'siteTypeName',
            title: '机构类型',
            align: 'center',
            formatter: function (value, row, index) {
                return value == null ? "" : ((value === 1) ? "分拣中心" : "转运中心");
            }
        },{
            field: 'reviewUserErp',
            title: '复核人ERP',
            align: 'center'
        },{
            field: 'reviewWeight',
            title: '复核重量',
            align: 'center'
        },{
            field: 'reviewVolume',
            title: '复核体积',
            align: 'center'
        },{
            field: 'reviewLWH',
            title: '复核长宽高',
            align: 'center'
        },{
            field: 'contrastOrgName',
            title: '核对操作区域',
            align: 'center'
        },{
            field: 'contrastWarZoneName',
            title: '核对操作战区',
            align: 'center'
        },{
            field: 'contrastAreaName',
            title: '核对操作片区',
            align: 'center'
        },{
            field: 'contrastSiteName',
            title: '核对操作站点',
            align: 'center'
        },{
            field: 'contrastStaffAccount',
            title: '核对操作人ERP',
            align: 'center'
        },{
            field: 'contrastWeight',
            title: '核对重量',
            align: 'center'
        },{
            field: 'contrastVolume',
            title: '核对体积',
            align: 'center'
        },{
            field: 'diffWeight',
            title: '重量差异',
            align: 'center'
        }, {
            field: 'diffStandard',
            title: '误差标准值',
            align: 'center'
        },{
            field: 'reviewSource',
            title: '复核来源',
            align: 'center',
            formatter: function (value, row, index) {
                if(value === 1){
                    return "平台打印抽检";
                }
                if(value === 2){
                    return "DWS抽检";
                }
                if(value === 3){
                    return "网页抽检";
                }
                if(value === 4){
                    return "安卓抽检";
                }
                if(value === 5){
                    return "人工抽检";
                }
                return "未知来源";
            }
        },{
            field: 'contrastSource',
            title: '核对来源',
            align: 'center',
            formatter: function (value, row, index) {
                return value === null ? null : value === 1 ? "计费重量(计费)" : value === 2 ? "运单复重" : value === 3 ? "下单重量" : "计费重量(运单)";
            }
        },{
            field: 'machineCode',
            title: '设备编码',
            align: 'center'
        },{
            field: 'machineStatus',
            title: '设备状态',
            align: 'center',
            formatter: function (value, row, index) {
                return value === 1 ? "合格" : value === 2 ? "不合格" : ""
            }
        },{
            field: 'isGatherTogether',
            title: '是否集齐',
            align: 'center',
            formatter: function (value, row, index) {
                const isGatherTogether = value === 1 ? "是" : "否";
                if(row.reviewSource === 2 && row.isMultiPack === 1){
                    return '<a class="full-collect-detail-btn" href="javascript:void(0)">' + isGatherTogether + '</a>'
                }
                return isGatherTogether;
            },
            events: {
                // 一单多件包裹抽检明细查看
                'click .full-collect-detail-btn': function(e, value, row, index) {
                    $packageDetailContainer.show()
                    detailTable.queryParams.waybillCode = row.waybillCode
                    detailTable.queryParams.reviewSiteCode = row.reviewSiteCode
                    if($packageCodeInput.val() !== ''){
                        detailTable.queryParams.packageCode = $packageCodeInput.val()
                    }
                    $packageDetailBsTable.bootstrapTable('refreshOptions', {url: packageDetailQueryUrl})
                    layer.open({
                        id:'packageDetailLayer',
                        type: 1,
                        title:'一单多件包裹抽检明细',
                        shadeClose: true,
                        shade: 0.7,
                        maxmin: true,
                        area: 'auto',
                        maxWidth: '1200',
                        content: $packageDetailContainer,
                        success: function(layero, index){
                        },
                        cancel: function(index, layero){
                            layer.close(index)
                            $packageDetailContainer.hide()
                        },
                        end: function (){
                            $packageDetailContainer.hide()
                        }
                    });
                },
            }
        },{
            field: 'isExcess',
            title: '是否超标',
            align: 'center',
            formatter: function (value, row, index) {
                return value === 1 ? "超标" : value === 0 ? "未超标" : value === 2 ? "集齐待计算" : "未知";
            }
        },{
            field: 'isIssueDownstream',
            title: '是否下发',
            align: 'center',
            formatter: function (value, row, index) {
                return value === 1 ? "已下发" : "未下发";
            }
        },{
            field: 'spotCheckStatus',
            title: '抽检状态',
            align: 'center',
            formatter: function (value, row, index) {
                if(value === 1){
                    return "待核实";
                }
                if(value === 2){
                    return "认责";
                }
                if(value === 3){
                    return "系统认责待确认责任人";
                }
                if(value === 4){
                    return "系统认责已确认责任人";
                }
                if(value === 5){
                    return "升级判责";
                }
                if(value === 6){
                    return "判责有效";
                }
                if(value === 7){
                    return "判责无效";
                }
                if(value === 8){
                    return "处理完成";
                }
                if(value === 9){
                    return "超时认责";
                }
                if(value === 101){
                    return "抽检中";
                }
                if(value === 102){
                    return "抽检无效-未超标";
                }
                if(value === 103){
                    return "抽检无效-AI失败";
                }
                return null;
            }
        }, {
            field: 'isHasPicture',
            title: '有无图片',
            align: 'center',
            formatter: function (value, row, index) {
                return value === 1 ? '有' : '无';
            }
        }, {
                field: 'pictureAIDistinguishReason',
                title: '图片AI识别结果',
                align: 'center',
                visible: false,
                formatter: function (value, row, index) {
                    return value;
                }
            },{
            field: 'operate',
            title: '操作',
            align: 'center',
            formatter : function (value, row, index) {
                if((row.reviewSource === 1 || (row.reviewSource === 2 && row.isMultiPack === 0))
                    && row.isHasPicture === 0
                    && row.isExcess === 1){
                    // 平台打印抽检 | dws一单一件抽检 可手动上传图片
                    return '<a class="upLoad" href="javascript:void(0)" ><i class="glyphicon glyphicon-search"></i>&nbsp;上传&nbsp;</a>';
                }
                return '<a class="search" href="javascript:void(0)" ><i class="glyphicon glyphicon-search"></i>&nbsp;查看&nbsp;</a>';
            },
            events: {
                'click .search': function(e, value, row, index) {
                    $.ajax({
                        type : "get",
                        url : "/spotCheckReport/securityCheck/" + row.waybillCode,
                        data : {},
                        async : false,
                        success : function (res) {
                            if(res.code === 200){
                                window.open("/spotCheckReport/toSearchPicture/?waybillCode="+row.waybillCode
                                    +"&reviewSiteCode="+row.reviewSiteCode + "&reviewSource="+row.reviewSource);
                            }else {
                                Jd.alert(res.message);
                            }
                        }
                    });

                },
                'click .upLoad': function(e, value, row, index) {
                    layer.open({
                        id:'upExcessPicture',
                        type: 2,
                        title:'超标图片上传',
                        shadeClose: true,
                        shade: 0.7,
                        maxmin: true,
                        area: ['1000px', '500px'],
                        content: upExcessPictureUrl + "?waybillCode=" + row.waybillCode + "&packageCode=" + row.packageCode + "&reviewSiteCode=" + row.reviewSiteCode ,
                        success: function(layero, index){
                        }
                    });
                },
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
                elem: '#reviewStartTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });
            $.datePicker.createNew({
                elem: '#reviewEndTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });

            // 查询
            $('#btn_query').click(function () {
                $('#btn_export').attr("disabled",false);
                var days = getDaysByDateString($('#reviewStartTime').val(),$('#reviewEndTime').val());
                if(days > 30){
                    Jd.alert("查询时间不能超过30天，请缩小时间范围!");
                    return;
                }
                tableInit().refresh();
            });

        };

        return oInit;
    };

    //导出
    function initExport(tableInit) {
        $('#btn_export').click(function () {
            var startDate = Date.parse($('#reviewStartTime').val().replace('/-/g', '/'));
            var endDate = Date.parse($('#reviewEndTime').val().replace('/-/g', '/'));
            if((endDate - startDate) > (24 * 60 * 60 * 1000)){
                Jd.alert("只能导出一天的数据,请缩短复核时间范围!");
                return;
            }
            checkConcurrencyLimit({
                currentKey: exportReportEnum.STORAGE_PACKAGE_M_REPORT,
                checkPassCallback: function (result) {

                    var params = tableInit.getSearchCondition();

                    var param = "";
                    if(params.reviewOrgCode != undefined && params.reviewOrgCode != "undefined"){
                        param = "&reviewOrgCode=" + params.reviewOrgCode;
                    }
                    if(params.reviewSiteCode != undefined && params.reviewSiteCode != "undefined"){
                        param += "&reviewSiteCode=" +  params.reviewSiteCode;
                    }
                    if(params.isExcess != undefined && params.isExcess != "undefined"){
                        param += "&isExcess=" +  params.isExcess;
                    }
                    param += "&reviewStartTime=" +  $("#reviewStartTime").val();
                    param += "&reviewEndTime=" +  $("#reviewEndTime").val();
                    if(params.waybillCode != undefined && params.waybillCode != "undefined"){
                        param += "&waybillCode=" +  params.waybillCode;
                    }
                    if(params.merchantName != undefined && params.merchantName != "undefined"){
                        param += "&merchantName=" +  encodeURI(encodeURI(params.merchantName));
                    }
                    if(params.reviewErp != undefined && params.reviewErp != "undefined"){
                        param += "&reviewErp=" +  params.reviewErp;
                    }
                    if(params.contrastErp != undefined && params.contrastErp != "undefined"){
                        param += "&contrastErp=" +  params.contrastErp;
                    }
                    if(params.businessType != undefined && params.businessType != "undefined"){
                        param += "&businessType=" +  params.businessType;
                    }
                    if(params.isTrustMerchant != undefined && params.isTrustMerchant != "undefined"){
                        param += "&isTrustMerchant=" +  params.isTrustMerchant;
                    }
                    if(params.isGatherTogether != undefined && params.isGatherTogether != "undefined"){
                        param += "&isGatherTogether=" +  params.isGatherTogether;
                    }
                    if(params.isIssueDownstream != undefined && params.isIssueDownstream != "undefined"){
                        param += "&isIssueDownstream=" +  params.isIssueDownstream;
                    }

                    $('#btn_export').attr("disabled",true);
                    location.href = exportUrl + "?" + param;

              }
            });
        });
    }

    $("[data-toggle='tooltip']").tooltip();

    initSelect();
    initOrg();
    initDateQuery();
    tableInit().init();
    pageInit().init();
    initExport(tableInit());

    // 一单多件包裹明细
    const detailTable = {
        queryParams: {},
        tableColums: [
            {
                field: 'reviewDate',
                title: '复核日期',
                align: 'center',
                formatter : function(value,row,index){
                    return $.dateHelper.formateDateTimeOfTs(value);
                }
            },{
                field: 'waybillCode',
                title: '运单号',
                align: 'center'
            },{
                field: 'packageCode',
                title: '包裹号',
                align: 'center'
            },{
                field: 'businessType',
                title: '业务类型',
                align: 'center',
                formatter: function (value, row, index) {
                    if(value != null && value === 1){
                        return "B网";
                    }
                    if(value != null && value === 2){
                        return "医药";
                    }
                    return "C网";
                }
            },{
                field: 'productTypeName',
                title: '产品标识',
                align: 'center'
            }, {
                field: 'merchantCode',
                title: '配送商家编号',
                align: 'center'
            }, {
                field: 'merchantName',
                title: '商家名称',
                align: 'center'
            }, {
                field: 'isTrustMerchant',
                title: '信任商家',
                align: 'center',
                formatter: function (value, row, index) {
                    return value === 1 ? "是" : "否";
                }
            },{
                field: 'reviewOrgName',
                title: '复核区域',
                align: 'center'
            }, {
                field: 'reviewSiteCode',
                title: '复核分拣',
                align: 'center',
                visible: false
            }, {
                field: 'reviewSiteName',
                title: '复核分拣'
            },{
                field: 'siteTypeName',
                title: '机构类型',
                align: 'center',
                formatter: function (value, row, index) {
                    return value == null ? "" : ((value === 1) ? "分拣中心" : "转运中心");
                }
            },{
                field: 'reviewUserErp',
                title: '复核人ERP',
                align: 'center'
            },{
                field: 'reviewWeight',
                title: '复核重量',
                align: 'center'
            },{
                field: 'reviewVolume',
                title: '复核体积',
                align: 'center'
            },{
                field: 'reviewLWH',
                title: '复核长宽高',
                align: 'center'
            },{
                field: 'contrastOrgName',
                title: '核对操作区域',
                align: 'center'
            },{
                field: 'contrastWarZoneName',
                title: '核对操作战区',
                align: 'center'
            },{
                field: 'contrastAreaName',
                title: '核对操作片区',
                align: 'center'
            },{
                field: 'contrastSiteName',
                title: '核对操作站点',
                align: 'center'
            },{
                field: 'contrastDutyErp',
                title: '核对操作人ERP',
                align: 'center'
            },{
                field: 'contrastWeight',
                title: '核对重量',
                align: 'center'
            },{
                field: 'contrastVolume',
                title: '核对体积',
                align: 'center'
            },{
                field: 'diffStandard',
                title: '误差标准值',
                align: 'center'
            },{
                field: 'reviewSource',
                title: '复核来源',
                align: 'center',
                formatter: function (value, row, index) {
                    if(value === 1){
                        return "平台打印抽检";
                    }
                    if(value === 2){
                        return "DWS抽检";
                    }
                    if(value === 3){
                        return "网页抽检";
                    }
                    if(value === 4){
                        return "安卓抽检";
                    }
                    if(value === 5){
                        return "人工抽检";
                    }
                    return "未知来源";
                }
            },{
                field: 'contrastSource',
                title: '核对来源',
                align: 'center',
                formatter: function (value, row, index) {
                    return value === null ? null : value === 1 ? "计费重量(计费)" : value === 2 ? "运单复重" : value === 3 ? "下单重量" : "计费重量(运单)";
                }
            },{
                field: 'machineCode',
                title: '设备编码',
                align: 'center'
            },{
                field: 'isHasPicture',
                title: '有无图片',
                align: 'center',
                formatter: function (value, row, index) {
                    return value === 1 ? '有' : '无';
                }
            }],
        refresh: function () {
            $packageDetailBsTable.bootstrapTable('refreshOptions', {pageNumber: 1});
        }
    };
    detailTable.getSearchParams = function (params) {
        detailTable.queryParams.limit = params.limit;
        detailTable.queryParams.offset = params.offset;
        detailTable.queryParams.packageCode = $('#packageCode').val();
        return detailTable.queryParams;
    };
    const $packageDetailForm = $('#packageDetailForm');
    const $packageCodeInput = $packageDetailForm.find('#packageCode');
    const $packageDetailContainer = $('#packageDetailContainer');
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
        columns: detailTable.tableColums
    });

    // 包裹查询
    $('#pack_btn_query').click(function () {
        $packageDetailBsTable.bootstrapTable('refreshOptions', {pageNumber: 1});
    });

});



function  getDaysByDateString(dateString1,dateString2) {
    var startDate = Date.parse(dateString1.replace('/-/g', '/'));
    var endDate = Date.parse(dateString2.replace('/-/g', '/'));
    var days = (endDate - startDate) / (1 * 24 * 60 * 60 * 1000);
    return days;
}

function initDateQuery(){
    var v = $.dateHelper.formatDate(new Date());
    $("#reviewStartTime").val(v+" 00:00:00");
    $("#reviewEndTime").val(v+" 23:59:59");
}

function initSelect() {
    // 业务类型
    var defualt = $("#query-form #businessTypeSelect").val();
    $("#query-form #businessType").val(defualt);
    $("#query-form #businessTypeSelect").on('change', function (e) {
        var v = $("#query-form #businessTypeSelect").val();
        if (v === '0' || v === '1' || v === '2') {
            $("#query-form #businessType").val(v);
        } else {
            $("#query-form #businessType").val(null);
        }
    });
    // 是否信任商家
    defualt = $("#query-form #isTrustMerchantSelect").val();
    $("#query-form #isTrustMerchant").val(defualt);
    $("#query-form #isTrustMerchantSelect").on('change', function (e) {
        var v = $("#query-form #isTrustMerchantSelect").val();
        if (v === '0' || v === '1') {
            $("#query-form #isTrustMerchant").val(v);
        } else {
            $("#query-form #isTrustMerchant").val(null);
        }
    });
    // 是否集齐
    defualt = $("#query-form #isGatherTogetherSelect").val();
    $("#query-form #isGatherTogether").val(defualt);
    $("#query-form #isGatherTogetherSelect").on('change', function (e) {
        var v = $("#query-form #isGatherTogetherSelect").val();
        if (v === '0' || v === '1') {
            $("#query-form #isGatherTogether").val(v);
        } else {
            $("#query-form #isGatherTogether").val(null);
        }
    });
    // 是否超标
    defualt = $("#query-form #isExcessSelect").val();
    $("#query-form #isExcess").val(defualt);
    $("#query-form #isExcessSelect").on('change', function (e) {
        var v = $("#query-form #isExcessSelect").val();
        if (v === '0' || v === '1' || v === '2') {
            $("#query-form #isExcess").val(v);
        } else {
            $("#query-form #isExcess").val(null);
        }
    });
    // 是否下发
    defualt = $("#query-form #isIssueDownstreamSelect").val();
    $("#query-form #isIssueDownstream").val(defualt);
    $("#query-form #isIssueDownstreamSelect").on('change', function (e) {
        var v = $("#query-form #isIssueDownstreamSelect").val();
        if (v === '0' || v === '1') {
            $("#query-form #isIssueDownstream").val(v);
        } else {
            $("#query-form #isIssueDownstream").val(null);
        }
    });
}

function findSite(selectId,siteListUrl,initIdSelectId){
    $(selectId).html("");
    $.ajax({
        type : "get",
        url : siteListUrl,
        data : {},
        async : false,
        success : function (data) {


            var result = [];
            if(data.length==1 && data[0].code!="200"){


                result.push({id:"-999",text:data[0].message});

            }else{
                for(var i in data){
                    if(data[i].siteCode && data[i].siteCode != ""){
                        result.push({id:data[i].siteCode,text:data[i].siteName});
                    }
                }

            }
            if(initIdSelectId && result[0].id!="-999"){
                $(initIdSelectId).val(result[0].id);
            }

            $(selectId).select2({
                width: '100%',
                placeholder:'请选择分拣中心',
                allowClear:true,
                data:result
            });
            $(selectId).val(null).trigger('change');

        }
    });
}

// 初始化大区下拉框
function initOrg() {


    var url = "/services/bases/allorgs";
    var param = {};
    $.ajax({
        type: "get",
        url: url,
        data: param,
        async: false,
        success: function (data) {

            var result = [];
            for (var i in data) {
                if (data[i].orgId && data[i].orgId != "") {
                    result.push({id: data[i].orgId, text: data[i].orgName});

                }

            }

            $('#site-group-select').select2({
                width: '100%',
                placeholder: '请选择机构',
                allowClear: true,
                data: result
            });

            $("#site-group-select")
                .on("change", function (e) {
                    $("#query-form #createSiteCode").val("");
                    var orgId = $("#site-group-select").val();
                    $("#query-form #reviewOrgCode").val(orgId);
                    if (orgId) {
                        var siteListUrl = '/services/bases/dms/' + orgId;
                        findSite("#site-select", siteListUrl, "#query-form #createSiteCode");
                    }

                });

            $("#site-select").on("change", function (e) {
                var _s = $("#site-select").val();
                $("#query-form #reviewSiteCode").val(_s);
            });


            if ($("#loginUserOrgId").val() != -1) {
                //登录人大区
                $('#site-group-select').val($("#loginUserOrgId").val()).trigger('change');
            } else {
                $('#site-group-select').val(null).trigger('change');
            }


        }
    });

}
