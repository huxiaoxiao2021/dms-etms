$(function () {
    var queryUrl = '/weightAndVolumeCheck/listData';
    var checkOverExportLimitUrl = '/weightAndVolumeCheck/checkOverExportLimit';
    var exportUrl = '/weightAndVolumeCheck/toExport';
    var upExcessPictureUrl = '/weightAndVolumeCheck/toUpload';
    var searchExcessPictureUrl = '/weightAndVolumeCheck/searchExcessPicture';
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
            field: 'packageCode',
            title: '扫描条码',
            align: 'center'
        },{
            field: 'spotCheckType',
            title: '业务类型',
            align: 'center',
            formatter: function (value, row, index) {
                return (value != null && value == "1") ? "B网" : "C网";
            }
        },{
            field: 'productTypeName',
            title: '产品标识',
            align: 'center'
        }, {
            field: 'busiCode',
            title: '商家ID',
            align: 'center',
            visible: false
        }, {
            field: 'merchantCode',
            title: '配送商家编号',
            align: 'center'
        }, {
            field: 'busiName',
            title: '商家名称',
            align: 'center'
        }, {
            field: 'isTrustBusi',
            title: '信任商家',
            align: 'center',
            formatter: function (value, row, index) {
                return value == "1" ? "是" : "否";
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
            field: 'reviewSubType',
            title: '机构类型',
            align: 'center',
            formatter: function (value, row, index) {
                return (value == null || value == -1) ? "" : ((value == 1) ? "分拣中心" : "转运中心");
            }
        },{
            field: 'reviewErp',
            title: '复核人erp',
            align: 'center'
        },{
            field: 'reviewWeight',
            title: '分拣复重kg',
            align: 'center'
        },{
            field: 'reviewLWH',
            title: '复核长宽高cm',
            align: 'center'
        }, {
            field: 'volumeRate',
            title: '计泡系数',
            align: 'center'
        },{
            field: 'reviewVolumeWeight',
            title: '复核体积重量',
            align: 'center'
        },{
            field: 'moreBigWeight',
            title: '较大重量值kg',
            align: 'center'
        },{
            field: 'billingOrgName',
            title: '计费操作区域',
            align: 'center'
        },{
            field: 'billingDeptName',
            title: '计费操作片区',
            align: 'center'
        },{
            field: 'billingCompany',
            title: '计费操作单位',
            align: 'center'
        },{
            field: 'billingErp',
            title: '计费操作人ERP',
            align: 'center'
        },{
            field: 'billingCalcWeight',
            title: '计费结算重量kg',
            align: 'center'
        },{
            field: 'billingWeight',
            title: '计费重量',
            align: 'center'
        },{
            field: 'billingVolume',
            title: '计费体积',
            align: 'center'
        },{
            field: 'billingVolumeWeight',
            title: '计费体积重量',
            align: 'center'
        },{
            field: 'billingWeightDifference',
            title: '计费结算重量差异',
            align: 'center'
        },{
            field: 'diffStandard',
            title: '误差标准值',
            align: 'center'
        },{
            field: 'isExcess',
            title: '是否超标',
            align: 'center',
            formatter: function (value, row, index) {
                return value == "1" ? "超标" : value == "0" ? "未超标" : "";
            }
        },{
            field: 'weightDiff',
            title: '重量差异',
            align: 'center'
        },{
            field: 'volumeWeightDiff',
            title: '体积重量差异',
            align: 'center'
        },{
            field: 'volumeWeightIsExcess',
            title: '体积重量是否超标',
            align: 'center',
            formatter: function (value, row, index) {
                return value == "1" ? "超标" : value == "0" ? "未超标" : "";
            }
        },{
            field: 'fromSource',
            title: '数据来源',
            align: 'center',
            formatter: function (value, row, index) {
                // 为了兼容之前定义的枚举。。。。
                if(value == "DMS_CLIENT_PACKAGE_WEIGH_PRINT" || value == "SPOT_CHECK_CLIENT_PLATE"){
                    return "平台打印抽检";
                }
                if(value == "DMS_AUTOMATIC_MEASURE" || value == "SPOT_CHECK_DWS"){
                    return "DWS抽检";
                }
                return value == "SPOT_CHECK_DMS_WEB" ? "B网网页抽检" : value == "SPOT_CHECK_ANDROID" ? "B网安卓抽检" : "其它";
            }
        },{
            field: 'isHasPicture',
            title: '有无图片',
            align: 'center',
            formatter: function (value, row, index) {
                return value == 1 ? "有" : "无";
            }
        },{
            field: 'upPicture',
            title: '照片上传',
            align: 'center',
            formatter : function (value, row, index) {
                var flage;
                if(row.isExcess == 0){
                    flage = null;
                }else{
                    if(row.isHasPicture == null || row.isHasPicture == 0){
                        flage = '<a class="upLoad" href="javascript:void(0)" ><i class="glyphicon glyphicon-upload"></i>&nbsp;点击上传&nbsp;</a>' +
                            '<br/>'
                    }else{
                        flage = '<a class="search" href="javascript:void(0)" ><i class="glyphicon glyphicon-search"></i>&nbsp;查看&nbsp;</a>'
                    }
                }
                return flage;
            },
            events: {
                'click .upLoad': function(e, value, row, index) {
                    layer.open({
                        id:'upExcessPicture',
                        type: 2,
                        title:'超标图片上传',
                        shadeClose: true,
                        shade: 0.7,
                        maxmin: true,
                        shadeClose: false,
                        area: ['1000px', '500px'],
                        content: upExcessPictureUrl + "?waybillCode=" + row.waybillCode + "&packageCode=" + row.packageCode + "&reviewDate=" + row.reviewDate,
                        success: function(layero, index){
                        }
                    });
                },
                'click .search': function(e, value, row, index) {
                    var spotCheckType = row.spotCheckType==null?0:row.spotCheckType;
                    var fromSource = row.fromSource;
                    var isWaybillSpotCheck = row.isWaybillSpotCheck==null?-1:row.isWaybillSpotCheck;
                    if(spotCheckType == 1 && fromSource == "SPOT_CHECK_DMS_WEB"){
                        //B网
                        window.open("/weightAndVolumeCheck/toSearchB2bExcessPicture/?waybillCode="+row.packageCode
                            +"&siteCode="+row.reviewSiteCode +"&isWaybillSpotCheck="+isWaybillSpotCheck+"&fromSource="+row.fromSource);
                    }else {
                        //C网
                        $.ajax({
                            type : "get",
                            url : searchExcessPictureUrl + "?packageCode=" + row.packageCode + "&siteCode=" +row.reviewSiteCode,
                            data : {},
                            async : false,
                            success : function (data) {
                                if(data && data.code == 200){
                                    layer.open({
                                        type: 2,
                                        title: "",
                                        shadeClose: true,
                                        shade: 0.5,
                                        area: ['500px','400px'],
                                        content: data.data,
                                        success: function(layero, index) {
                                            layer.iframeAuto(index);
                                        }
                                    });
                                }else{
                                    Jd.alert(data.message);
                                }
                            }
                        });
                    }
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
                $('#btn_export').attr("disabled",false);
                var days = getDaysByDateString($('#startTime').val(),$('#endTime').val());
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
            var startDate = Date.parse($('#startTime').val().replace('/-/g', '/'));
            var endDate = Date.parse($('#endTime').val().replace('/-/g', '/'));
            if((endDate - startDate) > (1 * 24 * 60 * 60 * 1000)){
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
                    if(params.createSiteCode != undefined && params.createSiteCode != "undefined"){
                        param += "&createSiteCode=" +  params.createSiteCode;
                    }
                    if(params.isExcess != undefined && params.isExcess != "undefined"){
                        param += "&isExcess=" +  params.isExcess;
                    }
                    param += "&startTime=" +  $("#startTime").val();
                    param += "&endTime=" +  $("#endTime").val();
                    if(params.waybillCode != undefined && params.waybillCode != "undefined"){
                        param += "&waybillCode=" +  params.waybillCode;
                    }
                    if(params.waybillOrPackCode != undefined && params.waybillOrPackCode != "undefined"){
                        param += "&waybillOrPackCode=" +  params.waybillOrPackCode;
                    }
                    if(params.busiName != undefined && params.busiName != "undefined"){
                        param += "&busiName=" +  encodeURI(encodeURI(params.busiName));
                    }
                    if(params.reviewErp != undefined && params.reviewErp != "undefined"){
                        param += "&reviewErp=" +  params.reviewErp;
                    }
                    if(params.billingErp != undefined && params.billingErp != "undefined"){
                        param += "&billingErp=" +  params.billingErp;
                    }
                    if(params.spotCheckType != undefined && params.spotCheckType != "undefined"){
                        param += "&spotCheckType=" +  params.spotCheckType;
                    }

                    $('#btn_export').attr("disabled",true);
                    location.href = exportUrl + "?" + param;

              },
              checkFailCallback: function (result) {
                    // 导出校验失败，弹出提示消息
                    alert(result.message)
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

});

function  getDaysByDateString(dateString1,dateString2) {
    var startDate = Date.parse(dateString1.replace('/-/g', '/'));
    var endDate = Date.parse(dateString2.replace('/-/g', '/'));
    var days = (endDate - startDate) / (1 * 24 * 60 * 60 * 1000);
    return days;
}

function initDateQuery(){
    var v = $.dateHelper.formatDate(new Date());
    $("#startTime").val(v+" 00:00:00");
    $("#endTime").val(v+" 23:59:59");
}

function initSelect() {
    var defualt = $("#query-form #isExcessSelect").val();
    $("#query-form #isExcess").val(defualt);
    $("#query-form #isExcessSelect").on('change', function (e) {
        var v = $("#query-form #isExcessSelect").val();
        if (v == 0 || v == 1) {
            $("#query-form #isExcess").val(v);
        } else {
            $("#query-form #isExcess").val(null);
        }
    });

    defualt = $("#query-form #spotCheckTypeSelect").val();
    $("#query-form #spotCheckType").val(defualt);
    $("#query-form #spotCheckTypeSelect").on('change', function (e) {
        var v = $("#query-form #spotCheckTypeSelect").val();
        if (v == 0 || v == 1) {
            $("#query-form #spotCheckType").val(v);
        } else {
            $("#query-form #spotCheckType").val(null);
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
                $("#query-form #createSiteCode").val(_s);
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
