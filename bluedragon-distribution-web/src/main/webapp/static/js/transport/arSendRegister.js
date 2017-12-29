$(function () {
    var saveUrl = '/transport/arSendRegister/save';
    var deleteUrl = '/transport/arSendRegister/deleteByIds';
    var detailUrl = '/transport/arSendRegister/detail/';
    var queryUrl = '/transport/arSendRegister/listData';
    var getFlightInfoUrl = '/transport/arSendRegister/getFlightInfo';

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
        oTableInit.getSearchCondition = function (_selector) {
            var params = {};
            if (!_selector) {
                _selector = ".search-param";
            }
            $(_selector).each(function () {
                var _k = this.id;
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
            title: '状态'
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
            title: '发货日期'
        }, {
            field: 'siteOrder',
            title: '发货批次'
        }, {
            field: 'airlineCompany',
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
            title: '摆渡车型'
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
            title: '操作时间'
        }];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refresh');
        };
        return oTableInit;
    };

    var pageInit = function () {
        var oInit = new Object();
        oInit.init = function () {
            $('#dataEditDiv').hide();
            /*起始时间*/
            $.datePicker.createNew({
                elem: '#startOperDate',
                type: 'datetime',
                theme: '#3f92ea',
                btns: ['now', 'confirm'],
                done: function (value, date, endDate) {
                    /*重置表单验证状态*/
                    $('#query-form').bootstrapValidator('resetForm', true);
                }
            });

            /*截止时间*/
            $.datePicker.createNew({
                elem: '#endOperDate',
                type: 'datetime',
                theme: '#3f92ea',
                done: function (value, date, endDate) {
                    $('#startOperDate').val(value);
                    console.log(value); //得到日期生成的值，如：2017-08-18
                    console.log(date); //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
                    console.log(endDate); //得结束的日期时间对象，开启范围选择（range: true）才会返回。对象成员同上。
                }
            });

            /*截止时间*/
            $.datePicker.createNew({
                elem: '#sendDate',
                type: 'datetime',
                theme: '#3f92ea',
                done: function (value, date, endDate) {
                    /*重置表单验证状态*/
                    $('#edit-form').bootstrapValidator('resetForm', true);
                }
            });

            $('#btn_query').click(function () {
                tableInit().refresh();
            });

            $('#btn_add').click(function () {
                $('.edit-param').each(function () {
                    var _k = this.id;
                    if (_k) {
                        $(this).val('');
                    }
                });
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
                    if (res && res.succeed && res.data) {
                        $('.edit-param').each(function () {
                            var _k = this.id;
                            var _v = res.data[_k];
                            if (_k && _v) {
                                $(this).val(_v);
                            }
                        });
                    }
                });
                $("#sendCodeNum").text(0);
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
                    ;
                    $.ajaxHelper.doPostSync(deleteUrl, JSON.stringify(params), function (res) {
                        if (res && res.succeed && res.data) {
                            alert('操作成功,删除' + res.data + '条。');
                            tableInit().refresh();
                        } else {
                            alert('操作异常！');
                        }
                    });
                }
            });

            $('#btn_submit').click(function () {
                var params = {};
                $('.edit-param').each(function () {
                    var _k = this.id;
                    var _v = $(this).val();
                    if (_k && _v) {
                        params[_k] = _v;
                    }
                });
                $.ajaxHelper.doPostSync(saveUrl, JSON.stringify(params), function (res) {
                    if (res && res.succeed) {
                        alert('操作成功');
                        tableInit().refresh();
                    } else {
                        alert('操作异常');
                    }
                });
                $('#dataEditDiv').hide();
                $('#dataTableDiv').show();
            });

            $('#btn_return').click(function () {
                $('#dataEditDiv').hide();
                $('#dataTableDiv').show();
            });

            $("#sendCodeStr").keydown(function (event) {
                if (event.keyCode == 13) {
                    $("#sendCodeNum").text(Number($("#sendCodeNum").text()) + 1);
                }
            });

            $("#orderCode").blur(function(){
                alert("调用接口加载航班信息");

                var orderCode = $(this).val();
                if (orderCode != null && orderCode != undefined && orderCode != '') {
                    $.ajaxHelper.doPostSync(getFlightInfoUrl, JSON.stringify(orderCode), function (res) {
                        if (res && res.succeed) {
                            alert('操作成功');
                            tableInit().refresh();
                        } else {
                            alert('操作异常');
                        }
                    });
                }
            })
        };
        return oInit;
    };

    /*下拉框*/
    $('#shuttleBus').select2({
        width: '300',
        placeholder: '车型',
        allowClear: true,
        data: [
            {id: 1, text: '选择项11111'},
            {id: 2, text: '选择项2'},
            {id: 3, text: '选择项3'},
            {id: 4, text: '选择项4'}
        ]
    });

    $("#shuttleBus").select2('val', 0);

    tableInit().init();
    pageInit().init();
});