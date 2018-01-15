$(function () {
    var insertUrl = '/transport/arSendRegister/insert';
    var updateUrl = '/transport/arSendRegister/update';
    var deleteUrl = '/transport/arSendRegister/deleteByIds';
    var detailUrl = '/transport/arSendRegister/detail/';
    var queryUrl = '/transport/arSendRegister/listData';
    var getTransportInfoUrl = '/transport/arSendRegister/getTransportInfo';
    var getAllBusTypeUrl = '/transport/arSendRegister/getAllBusType';

    /**
     * 获取所有车辆类型信息
     */
    var getAllBusType = function () {
        var allBusType = null;
        $.ajaxHelper.doPostSync(getAllBusTypeUrl, null, function (result) {
            if (result.code == 200) {
                allBusType = result.data;
            } else {
                alert(result.message);
            }
        });
        return allBusType;
    }

    var allBusType = getAllBusType();

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
                if (_k && _v) {
                    params[_k] = _v;
                }
            });
            return params;
        };
        oTableInit.tableColums = [{
            checkbox: true
        }, {
            field: 'status',
            title: '状态',
            formatter: function (value, row, index) {
                if (value == 1) {
                    return "已发货";
                } else if (value == 2) {
                    return "已提货";
                } else {
                    return "-";
                }
            }
        }, {
            field: 'orderCode',
            title: '航空单号'
        }, {
            field: 'transportName',
            title: '运力名称'
        }, {
            field: 'siteOrder',
            title: '铁路站序'
        }, {
            field: 'sendDate',
            title: '发货日期',
            formatter: function (value, row, index) {
                if (value != null && value != '') {
                    return jQuery.dateHelper.formateDateOfTs(value);
                } else {
                    return "-";
                }
            }
        }, {
            field: 'sendCode',
            title: '发货批次'
        }, {
            field: 'transCompany',
            title: '航空公司'
        }, {
            field: 'startCityName',
            title: '起飞城市'
        }, {
            field: 'endCityName',
            title: '落地城市'
        }, {
            field: 'planStartTime',
            title: '预计起飞时间'
        }, {
            field: 'planEndTime',
            title: '预计落地时间'
        }, {
            field: 'sendNum',
            title: '发货件数'
        }, {
            field: 'chargedWeight',
            title: '计费重量'
        }, {
            field: 'remark',
            title: '发货备注'
        }, {
            field: 'shuttleBusType',
            title: '摆渡车型',
            formatter: function (value, row, index) {
                if (allBusType != null && allBusType.length > 0) {
                    for (var i = 0, len = allBusType.length; i < len; i++) {
                        if (allBusType[i].busTypeId == value) {
                            return allBusType[i].busTypeName;
                        }
                    }
                }
                return "-";
            }
        }, {
            field: 'shuttleBusNum',
            title: '摆渡车牌号'
        }, {
            field: 'operatorErp',
            title: '操作人'
        }, {
            field: 'operationDept',
            title: '操作部门'
        }, {
            field: 'operationTime',
            title: '操作时间',
            formatter: function (value, row, index) {
                if (value != null && value != '') {
                    return jQuery.dateHelper.formateDateTimeOfTs(value);
                } else {
                    return "-";
                }
            }
        }];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refresh');
        };
        return oTableInit;
    };

    $('#edit-form').bootstrapValidator({
        live: 'enabled',
        message: 'This value is not valid',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            transportName: {
                validators: {
                    notEmpty: {
                        message: '必填项，请输入航班号/车次号'
                    },
                    stringLength: {
                        max: 16,
                        message: '航班号/车次号长度不能超过16个字符'
                    }
                }
            },
            orderCode: {
                validators: {
                    stringLength: {
                        max: 30,
                        message: '航空单号长度不能超过30个字符'
                    },
                    callback: {
                        message: '铁路站序和航空单号不允许同时为空',
                        callback: function (value, validator, $field) {
                            if (value == null || value == '') {
                                var siteOrder = $('#siteOrderEdit').val();
                                if (siteOrder == null || siteOrder == '') {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                }
            },
            siteOrder: {
                validators: {
                    stringLength: {
                        max: 16,
                        message: '铁路站序长度不能超过16个字符'
                    },
                    callback: {
                        message: '铁路站序和航空单号不允许同时为空',
                        callback: function (value, validator, $field) {
                            if (value == null || value == '') {
                                var orderCode = $('#orderCodeEdit').val();
                                if (orderCode == null || orderCode == '') {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                }
            },
            sendCode: {
                validators: {
                    notEmpty: {
                        message: '必填项，请输入发货批次号'
                    }
                }
            },
            sendNum: {
                validators: {
                    notEmpty: {
                        message: '必填项，请输入发货件数'
                    },
                    integer: {
                        message: '发货件数只能输入整数'
                    },
                    between: {
                        min: 1,
                        max: 9999,
                        message: '发货数量必须在1到9999之间'
                    }
                }
            },
            chargedWeight: {
                validators: {
                    notEmpty: {
                        message: '必填项，请输入计费重量'
                    },
                    numeric: {
                        message: '计费重量只能输入数字'
                    },
                    between: {
                        min: 0.01,
                        max: 999999.99,
                        message: '计费重量必须在0.01到999999.99之间'
                    },
                    regexp: {
                        regexp: /^\d+(\.\d{2})?$/,
                        message: '小数点后仅可保留两位小数'
                    }
                }
            },
            sendDate: {
                validators: {
                    notEmpty: {
                        message: '必填项，请输入发货日期'
                    },
                    date: {
                        format: 'YYYY-MM-DD',
                        message: '请输入正确的日期格式'
                    },
                    callback: {
                        message: '发货日期必须在3日内',
                        callback: function (value, validator, $field) {
                            var currentDate = new Date();
                            var inputDate = new Date(value.replace(/-/, "/"));
                            if (inputDate > currentDate) {
                                return false;
                            }
                            return Math.abs(currentDate - inputDate) < 4 * 24 * 3600 * 1000;
                        }
                    }
                }
            },
            remark: {
                validators: {
                    stringLength: {
                        max: 500,
                        message: '备注信息长度不能超过500个字符'
                    }
                }
            }
        }
    });

    var clearAllInfo = function () {
        $('#edit-form').bootstrapValidator('resetForm', true);
        $('.edit-param').each(function () {
            $(this).val('');
        });
        $("#sendCodeNum").text(0);
        $("#shuttleBusType").val(0).trigger("change");
        clearTransportInfo();
    }

    var clearTransportInfo = function () {
        $("#transCompany").text('');
        $("#transCompanyCode").text('');
        $("#startCityId").val('');
        $("#endCityId").val('');
        $("#startCityName").text('');
        $("#endCityName").text('');
        $("#startStationId").val('');
        $("#endStationId").val('');
        $("#startStationName").text('');
        $("#endStationName").text('');
        $("#planStartTime").text('');
        $("#planEndTime").text('');
    }

    var setTransportInfo = function (data) {
        $("#transCompany").text(data.transCompany == null ? "" : data.transCompany);
        $("#transCompanyCode").text(data.transCompanyCode == null ? "" : data.transCompanyCode);
        $("#startCityId").val(data.startCityId);
        $("#endCityId").val(data.endCityId);
        $("#startCityName").text(data.startCityName == null ? "" : data.startCityName);
        $("#endCityName").text(data.endCityName == null ? "" : data.endCityName);
        $("#startStationId").val(data.startStationId );
        $("#endStationId").val(data.endStationId);
        $("#startStationName").text(data.startStationName == null ? "" : data.startStationName);
        $("#endStationName").text(data.endStationName == null ? "" : data.endStationName);
        $("#planStartTime").text(data.planStartTime == null ? "" : data.planStartTime);
        $("#planEndTime").text(data.planEndTime == null ? "" : data.planEndTime);
    }

    var getTransportInfo = function (params) {
        params["transCompany"] = $("#transCompany").text();
        params["transCompanyCode"] = $("#transCompanyCode").val();
        params["startCityId"] = $("#startCityId").val();
        params["startCityName"] = $("#startCityName").text();
        params["endCityId"] = $("#endCityId").val();
        params["endCityName"] = $("#endCityName").text();
        params["startStationId"] = $("#startStationId").val();
        params["startStationName"] = $("#startStationName").text();
        params["endStationId"] = $("#endStationId").val();
        params["endStationName"] = $("#endStationName").text();
        params["planStartTime"] = $("#planStartTime").text();
        params["planEndTime"] = $("#planEndTime").text();
        return params;
    }

    var getCurrentDate = function () {
        var date = new Date();
        var separator = "-";
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        return date.getFullYear() + separator + month + separator + strDate;
    }

    var pageInit = function () {
        var oInit = new Object();
        oInit.init = function () {
            $('#dataEditDiv').hide();
            /*起始时间*/
            $.datePicker.createNew({
                elem: '#startOperTime',
                type: 'datetime',
                theme: '#3f92ea',
            });
            $.datePicker.setValue("startOperTime", getCurrentDate() + " 00:00:00");

            /*截止时间*/
            $.datePicker.createNew({
                elem: '#endOperTime',
                type: 'datetime',
                theme: '#3f92ea',
                done: function (value, date, endDate) {
                    $('#startOperTime').val(value);
                    console.log(value); //得到日期生成的值，如：2017-08-18
                    console.log(date); //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
                    console.log(endDate); //得结束的日期时间对象，开启范围选择（range: true）才会返回。对象成员同上。
                }
            });
            $.datePicker.setValue("endOperTime", getCurrentDate() + " 23:59:59");

            /*发货时间*/
            $.datePicker.createNew({
                elem: '#sendDateEdit',
                type: 'date',
                theme: '#3f92ea',
                done: function (value, date, endDate) {
                    $('#edit-form').bootstrapValidator('updateStatus', 'sendDate', 'NOT_VALIDATED').bootstrapValidator('validateField', 'sendDate');
                }
            });

            $('#btn_query').click(function () {
                tableInit().refresh();
            });

            $('#btn_add').click(function () {
                $('.edit-param').each(function () {
                    var _k = this.name;
                    if (_k) {
                        $(this).val('');
                    }
                });
                $.datePicker.setValue("sendDateEdit", getCurrentDate());
                $("#sendCodeNum").text(0);
                $('#dataTableDiv').hide();
                $('#dataEditDiv').show();
            });

            // 初始化页面上面的按钮事件
            // 改
            $('#btn_edit').click(function () {
                var rows = $('#dataTable').bootstrapTable('getSelections');
                if (rows.length > 1) {
                    alert("修改操作，只能选择一条数据");
                    return;
                }
                if (rows.length == 0) {
                    alert("请选择一条数据");
                    return;
                }
                $.ajaxHelper.doPostSync(detailUrl + rows[0].id, null, function (res) {
                    if (res && res.data) {
                        $('#id').val(res.data.id);
                        $('#orderCodeEdit').val(res.data.orderCode);
                        $('#transportNameEdit').val(res.data.transportName);
                        $('#siteOrderEdit').val(res.data.siteOrder);
                        $('#sendNumEdit').val(res.data.sendNum);
                        $('#chargedWeightEdit').val(res.data.chargedWeight);
                        $('#remarkEdit').val(res.data.remark);
                        $('#sendCode').val(res.data.sendCode);
                        $('#shuttleBusType').val(Number(res.data.shuttleBusType)).trigger("change");
                        $('#shuttleBusNumEdit').val(res.data.shuttleBusNum);
                        $('#sendDateEdit').val(jQuery.dateHelper.formateDateOfTs(res.data.sendDate));
                        setTransportInfo(res.data);
                    }
                });
                $('#dataTableDiv').hide();
                $('#dataEditDiv').show();
            });

            // 删
            $('#btn_delete').click(function () {
                var rows = $('#dataTable').bootstrapTable('getSelections');
                if (rows.length < 1) {
                    alert("错误，未选中数据");
                    return;
                }
                var flag = confirm("是否删除这些数据?");
                if (flag == true) {
                    var params = [];
                    for (var i in rows) {
                        params.push(rows[i].id);
                    }
                    $.ajaxHelper.doPostSync(deleteUrl, JSON.stringify(params), function (res) {
                        if (res && res.code == 200 && res.data) {
                            alert('操作成功,删除' + res.data + '条。');
                            tableInit().refresh();
                        } else {
                            alert('操作异常！');
                        }
                    });
                }
            });

            $('#btn_submit').click(function () {
                $('#edit-form').bootstrapValidator('validate');
                var isValid = $("#edit-form").data("bootstrapValidator").isValid();
                if (isValid) {
                    var url;
                    var id = $('#id').val();
                    if (id != null && id != '') {
                        url = updateUrl;
                    } else {
                        url = insertUrl;
                    }
                    var params = {};
                    $('.edit-param').each(function () {
                        var _k = this.name;
                        var _v = $(this).val();
                        if (_k && _v) {
                            params[_k] = _v;
                        }
                    });
                    params["shuttleBusType"] = $('#shuttleBusType').val();
                    params = getTransportInfo(params);
                    $.ajaxHelper.doPostSync(url, JSON.stringify(params), function (res) {
                        if (res && res.data) {
                            alert('操作成功');
                            tableInit().refresh();
                        } else {
                            alert('操作异常');
                        }
                    });
                    clearAllInfo();
                    $('#dataEditDiv').hide();
                    $('#dataTableDiv').show();
                }
            });

            $('#btn_return').click(function () {
                clearAllInfo();
                $('#dataEditDiv').hide();
                $('#dataTableDiv').show();
            });

            $("#sendCode").keydown(function (event) {
                // 如果是回车
                if (event.keyCode == 13) {
                    $("#sendCodeNum").text(Number($("#sendCodeNum").text()) + 1);
                }
            });

            $("#orderCodeEdit").blur(function () {
                $('#edit-form').bootstrapValidator('updateStatus', 'siteOrder', 'NOT_VALIDATED').bootstrapValidator('validateField', 'siteOrder');
                var transportName = $("#transportNameEdit").val();
                if (transportName != null && transportName != '') {
                    var orderCode = $(this).val();
                    if (orderCode != null && orderCode != '') {
                        var param = {};
                        param["transportName"] = transportName;
                        param["orderCode"] = orderCode;
                        $.ajaxHelper.doPostSync(getTransportInfoUrl, JSON.stringify(param), function (response) {
                            if (response != null && response.code == 200) {
                                $("#siteOrderEdit").val("");
                                clearTransportInfo();
                                setTransportInfo(response.data);
                            } else {
                                alert('加载航班信息异常');
                            }
                        });
                    }
                }
            });

            $("#siteOrderEdit").blur(function () {
                $('#edit-form').bootstrapValidator('updateStatus', 'orderCode', 'NOT_VALIDATED').bootstrapValidator('validateField', 'orderCode');
                var transportName = $("#transportNameEdit").val();
                if (transportName != null && transportName != '') {
                    var siteOrder = $(this).val();
                    if (siteOrder != null && siteOrder != '') {
                        var param = {};
                        param["transportName"] = transportName;
                        param["siteOrder"] = siteOrder;
                        $.ajaxHelper.doPostSync(getTransportInfoUrl, JSON.stringify(param), function (response) {
                            if (response != null && response.code == 200) {
                                $("#orderCodeEdit").val("");
                                clearTransportInfo();
                                setTransportInfo(response.data);
                            } else {
                                alert('加载铁路信息异常');
                            }
                        });
                    }
                }
            });
        };
        return oInit;
    };

    var data = new Array();
    if (allBusType != null && allBusType.length > 0) {
        for (var i = 0, len = allBusType.length; i < len; i++) {
            var option = {};
            option["id"] = allBusType[i].busTypeId;
            option["text"] = allBusType[i].busTypeName;
            data.push(option);
        }
    }

    /*下拉框*/
    $('#shuttleBusType').select2({
        placeholder: '车型',
        allowClear: true,
        data: data
    }).val(0).trigger("change");

    tableInit().init();
    pageInit().init();
});