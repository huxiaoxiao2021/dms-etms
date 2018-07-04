$(function () {
    var mainQueryUrl = '/abnormalDispose/abnormalDispose/listData';
    var tableInit = function () {
        var oTableInit = new Object();
        oTableInit.init = function () {
            $('#dataTable').bootstrapTable({
                url: mainQueryUrl, // 请求后台的URL（*）
                method: 'post', // 请求方式（*）
                toolbar: '#toolbar_main', // 工具按钮用哪个容器
                queryParams: oTableInit.getSearchParams, // 查询参数（*）
                //height : 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
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
                showToggle: false, // 是否显示详细视图和列表视图的切换按钮
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
                var _k = this.name;
                var _v = $(this).val();
                if (_k) {
                    if (_k == 'startTime' || _k == 'endTime') {
                        if (!_v) {
                            alert("时间范围不允许为空")
                            return;
                        }
                        params[_k] = new Date(_v).getTime();
                    } else if (_v != '-1') {
                        params[_k] = _v;
                    }
                }
            });
            return params;
        };


        oTableInit.tableColums = [
            {
                field: 'waveBusinessId',
                title: 'id',
                visible: false
            },
            {
                field: 'siteCode',
                title: 'siteCode',
                visible: false
            },
            {
                field: 'dateTime',
                title: '日期',
                formatter: function (value, row, index) {
                    return $.dateHelper.formateDateOfTs(value);
                }
            }, {
                field: 'areaName',
                title: '区域'
            }, {
                field: 'transferNo',
                title: '中转班次'
            }, {
                field: 'transferStartTime',
                title: '班次开始时间',
                formatter: function (value, row, index) {
                    return $.dateHelper.formateDateTimeOfTs(value);
                }

            }, {
                field: 'transferEndTime',
                title: '班次结束时间',
                formatter: function (value, row, index) {
                    return $.dateHelper.formateDateTimeOfTs(value);
                }
            }, {
                field: 'notReceiveNum',
                title: '未收货数量',
                formatter: function (value, row, index) {
                    return "<a href='#' onclick='queryinspection(\"" + row.waveBusinessId + "\",\""+row.siteCode+"\"," + value + ")'>" + value + "</a>";
                }
            }, {
                field: 'notReceiveDisposeNum',
                width: 100,
                title: '已处理未收货异常数'
            }, {
                field: 'notReceiveProgress',
                title: '未收货异常处理进度',
                formatter: function (value, row, index) {
                    if (value) {
                        return value + '%';
                    } else {
                        return '0%';
                    }
                }
            }, {
                field: 'notSendNum',
                title: '未发货数量',
                formatter: function (value, row, index) {
                    return "<a href='#' onclick='querySend(\"" + row.waveBusinessId + "\",\""+row.siteCode+"\"," + value + ")'>" + value + "</a>";
                }
            }, {
                field: 'notSendDisposeNum',
                width: 100,
                title: '已处理未发货异常数'
            }, {
                field: 'notSendProgress',
                title: '未发货异常处理进度',
                formatter: function (value, row, index) {
                    if (value) {
                        return value + '%';
                    } else {
                        return '0%';
                    }
                }
            }, {
                field: 'totalProgress',
                title: '总进度',
                formatter: function (value, row, index) {
                    if (value) {
                        return value + '%';
                    } else {
                        return '0%';
                    }
                }
            }];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
            //$('#dataTable').bootstrapTable('refresh');
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
                //type: 'datetime',
                format: 'yyyy-MM-dd',
                min: -60,//最近30天内
                max: 0,//最近30天内
                btns: ['now', 'confirm'],
                done: function (value, date, endDate) {
                    /*重置表单验证状态*/
                }
            });
            $.datePicker.createNew({
                elem: '#endTime',
                theme: '#3f92ea',
                //type: 'datetime',
                format: 'yyyy-MM-dd',
                min: -60,//最近30天内
                max: 0,//最近30天内
                btns: ['now', 'confirm'],
                done: function (value, date, endDate) {
                    /*重置表单验证状态*/
                }
            });
            //加载数据
            $('#btn_query').click(function () {
                tableInit().refresh();
            });
        };
        return oInit;
    };

//初始化日期时间
    var initDateQuery = function () {
        var startTime = $.dateHelper.formateDateOfTs(new Date(new Date().toLocaleDateString()));
        var endTime = $.dateHelper.formateDateOfTs(new Date(new Date().toLocaleDateString()));
        $("#startTime").val(startTime);
        $("#endTime").val(endTime);
    }

//加载分拣中心
    var loadSite = function (params) {
        var siteListUrl = '/base/dmsStorageArea/getSiteListByKey';
        $.ajax({
            type: "get",
            url: siteListUrl,
            data: params,
            async: true,
            success: function (data) {
                var result = [];
                if (data) {
                    for (var i in data) {
                        if (data[i].dmsSiteCode && data[i].dmsSiteCode != "") {
                            result.push({id: data[i].dmsSiteCode, text: data[i].siteName});
                        }
                    }
                }
                $("#dmsSiteCode").empty();
                $("#query-form #dmsSiteCode").select2({
                    width: '100%',
                    placeholder: '请选择',
                    allowClear: true,
                    data: result
                });
                if (result.length > 0) {
                    $("#query-form #dmsSiteCode").val(result[0].id).trigger('change');
                } else {
                    $("#query-form #dmsSiteCode").val(null).trigger('change');
                }
            }
        });
    }

//初始化区域
    var initArea = function () {
        var url = "/base/dmsStorageArea/getAllArea";
        var param = {};
        $.ajax({
            type: "get",
            url: url,
            data: param,
            async: true,
            success: function (data) {
                var result = [];
                for (var i in data) {
                    if (data[i].id && data[i].id != "") {
                        result.push({id: data[i].id, text: data[i].name});
                    }
                }
                $('#query-form #areaId').select2({
                    width: '100%',
                    placeholder: '请选择',
                    allowClear: true,
                    data: result
                });

                if (result.length > 0) {
                    $("#query-form #areaId").val(result[0].id).trigger('change');
                } else {
                    $("#query-form #areaId").val(null).trigger('change');
                }
                $("#query-form #areaId")
                    .on("change", function (e) {
                        var areaId = $("#areaId").val();
                        if (areaId) {
                            initProvince({areaId: areaId});
                            $('#query-form #cityId').select2({data: []});
                            $("#cityId").empty();
                            $("#query-form #cityId").val(null).trigger('change');
                            loadSite({areaId: areaId});
                        }
                    });
            }
        });
    }

//加载省
    var initProvince = function (param) {
        var url = "/base/dmsStorageArea/getProvinceListByKey";
        $.ajax({
            type: "get",
            url: url,
            data: param,
            async: true,
            success: function (data) {
                var result = [];
                for (var i in data) {
                    if (data[i].id && data[i].id != "") {
                        result.push({id: data[i].id, text: data[i].name});
                    }
                }
                $("#provinceId").empty();
                $('#query-form #provinceId').select2({
                    width: '100%',
                    placeholder: '请选择',
                    allowClear: true,
                    data: result
                });
                if (result.length > 0) {
                    $("#query-form #provinceId").val(result[0].id).trigger('change');
                } else {
                    $("#query-form #provinceId").val(null).trigger('change');
                }
                $("#query-form #provinceId")
                    .on("change", function (e) {
                        var areaId = $("#areaId").val();
                        var provinceId = $("#provinceId").val();
                        if (provinceId) {
                            if (provinceId == -1) {
                                $('#query-form #cityId').select2({data: []});
                                $("#cityId").empty();
                                $("#query-form #cityId").val(null).trigger('change');
                            } else {
                                initCity({areaId: areaId, provinceId: provinceId});
                            }
                            loadSite({areaId: areaId, provinceId: provinceId});
                        }
                    });

            }
        });
    }

//加载市
    var initCity = function (param) {
        var url = "/base/dmsStorageArea/getCityListByKey";
        $.ajax({
            type: "get",
            url: url,
            data: param,
            async: true,
            success: function (data) {
                var result = [];
                for (var i in data) {
                    if (data[i].assortCode && data[i].assortCode != "") {
                        result.push({id: data[i].assortCode, text: data[i].assortName});
                    }
                }
                $("#cityId").empty();
                $('#query-form #cityId').select2({
                    width: '100%',
                    placeholder: '请选择',
                    allowClear: true,
                    data: result
                });
                if (result.length > 0) {
                    $("#query-form #cityId").val(result[0].id).trigger('change');
                } else {
                    $("#query-form #cityId").val(null).trigger('change');
                }
                $("#query-form #cityId")
                    .on("change", function (e) {
                        var areaId = $("#areaId").val();
                        var provinceId = $("#provinceId").val();
                        var cityId = $("#cityId").val();
                        if (cityId) {
                            loadSite({areaId: areaId, provinceId: provinceId, cityId: cityId});
                        }
                    });
            }
        });
    }

    initArea();
    loadSite({});
    initDateQuery();
    tableInit().init();
    pageInit().init();
});

