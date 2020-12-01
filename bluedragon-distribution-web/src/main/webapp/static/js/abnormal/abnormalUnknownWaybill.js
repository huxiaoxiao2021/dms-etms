var tableInit;
$(function () {
    var saveUrl = '/abnormal/abnormalUnknownWaybill/save';
    var deleteUrl = '/abnormal/abnormalUnknownWaybill/deleteByIds';
    var detailUrl = '/abnormal/abnormalUnknownWaybill/detail/';
    var queryUrl = '/abnormal/abnormalUnknownWaybill/listData';
    tableInit = function () {
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
                columns: oTableInit.tableColums,
                responseHandler:function (data) {
                    if(data.code != 200){
                        alert(data.message);
                        return {
                            "total":0,
                            "rows":[]
                        }
                    }else{
                        return {
                            "total":data.data.total,
                            "rows":data.data.rows
                        }
                    }

                }
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
                    if (_k == 'startTime' || _k == 'endTime') {
                        params[_k] = new Date(_v).getTime();
                    } else {
                        params[_k] = _v;
                    }
                }
            });
            if (waybillCodes) {
                params.waybillCode = waybillCodes;
            }
            return params;
        };
        oTableInit.tableColums = [{
            checkbox: true
        }, {
            field: 'waybillCode',
            title: '运单号'
        }, {
            field: 'orderNumber',
            title: '第几次上报'
        }, {
            field: 'traderName',
            title: '商家名称'
        }, {
            field: 'dmsSiteName',
            title: '机构名称'
        }, {
            field: 'areaName',
            title: '区域名称'
        }, {
            field: 'isReceipt',
            title: '是否回复',
            formatter: function (value, row, index) {
                return value == 1 ? '是' : '否';
            }
        }, {
            field: 'receiptTime',
            title: '回复时间',
            formatter: function (value, row, index) {
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        }, {
            field: 'receiptFrom',
            title: '回复来源',
            formatter: function (value, row, index) {
                return value == 'E' ? 'ECLP系统' : value == 'W' ? '运单系统' : value == 'B' ? '商家回复' : value;
            }
        }, {
            field: 'receiptContent',
            title: '托寄物'
        }, {
            field: 'createUser',
            title: '提报人'
        }, {
            field: 'operation',
            title: '操作',
            formatter: function (value, row, index) {
                if (row.orderNumber > 0 && row.isReceipt == 1) {
                    return "<a href='#' onclick='do_submitAgain(\""
                        + row.waybillCode + "\")'>再次上报</a>";
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
            $('#dataEditDiv').hide();
            /*起始时间*/
            /*截止时间*/
            $.datePicker.createNew({
                elem: '#startTime',
                theme: '#3f92ea',
                type: 'datetime',
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
                type: 'datetime',
                min: -60,//最近30天内
                max: 0,//最近30天内
                btns: ['now', 'confirm'],
                done: function (value, date, endDate) {
                    /*重置表单验证状态*/
                }
            });
            $('#btn_query').click(function () {
                waybillCodes = null;//清空批量查询
                //校验输入的运单号
                var url = '/abnormal/abnormalUnknownWaybill/checkWaybillCode?waybillCodes=' + $("#waybillCode").val();
                $.ajaxHelper.doGetSync(url, null, function (res) {
                    if (res && !res.succeed) {
                        alert(res.message);
                    }else{
                        let clickStrict = ClickFrequencyUtil.controlClick($('#query-form'), $('#btn_query'));
                        if (!clickStrict) {

                            refreshTable();
                        }
                    }
                });
            });
            $('#btn_add').click(function () {
                $('.edit-param').each(function () {
                    var _k = this.id;
                    if (_k) {
                        $(this).val('');
                    }
                });
                $('#edit-form #typeGroup').val(null).trigger('change');
                $('#edit-form #parentId').val(null).trigger('change');
                $('#dataTableDiv').hide();
                $('#dataEditDiv').show();
            });
            // 修改操作
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
                            if (_k) {
                                if (_v != null && _v != undefined) {
                                    $(this).val(_v);
                                } else {
                                    $(this).val('');
                                }
                            }
                        });
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
                    ;
                    $.ajaxHelper.doPostSync(deleteUrl, JSON.stringify(params), function (res) {
                        if (res && res.succeed && res.data) {
                            alert('操作成功,删除' + res.data + '条。');
                            refreshTable();
                        } else {
                            alert('操作异常！');
                        }
                    });
                }
            });
            $('#btn_queryOnly').click(function () {
                var params = {};
                $('.edit-param').each(function () {
                    var _k = this.id;
                    var _v = $(this).val();
                    if (_k && _v) {
                        params[_k] = _v;
                    }
                });
                params['isReport'] = 0;//不上报
                $.ajaxHelper.doPostSync(saveUrl, JSON.stringify(params), function (res) {
                    if (res && res.succeed) {
                        if (res.data) {
                            //批量查询
                            waybillCodes = res.data;
                            $("#startTime").val(null);
                            $("#endTime").val(null);
                        }
                        refreshTable();
                        $('#dataEditDiv').hide();
                        $('#dataTableDiv').show();
                    } else if (res) {
                        alert(res.message);
                    } else {
                        alert('操作异常');
                    }
                });
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
                params['isReport'] = 1;//查询并上报
                $.ajaxHelper.doPostSync(saveUrl, JSON.stringify(params), function (res) {
                    if (res && res.succeed) {
                        alert(res.message);
                        if (res.data) {
                            //批量查询
                            waybillCodes = res.data;
                            $("#startTime").val(null);
                            $("#endTime").val(null);
                        }
                        refreshTable();
                        $('#dataEditDiv').hide();
                        $('#dataTableDiv').show();
                    } else if (res) {
                        alert(res.message);
                    } else {
                        alert('操作异常');
                    }
                });
            });
            $('#btn_return').click(function () {
                $('#dataEditDiv').hide();
                $('#dataTableDiv').show();
            });
        };
        return oInit;
    };

    function initDateQuery() {
        var startTime = $.dateHelper.formatDateTime(new Date(new Date().toLocaleDateString()));
        var endTime = $.dateHelper.formatDateTime(new Date(new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1));
        $("#startTime").val(startTime);
        $("#endTime").val(endTime);
    }

    //初始化导出按钮
    function initExport(tableInit){
        $("#btn_export").on("click",function(e){

            var url = "/abnormal/abnormalUnknownWaybill/toExport";
            var params = tableInit.getSearchCondition();

            var areaId = params["areaId"];
            var dmsSiteCode = params["dmsSiteCode"];
            if(areaId ==null|| dmsSiteCode==null){
                alert('导出功能 "机构"和"分拣中心"必选');
                return ;
            }

            if (isEmptyObject(params)){
                alert('禁止全量导出，请确定查询范围');
                return;
            }
            var form = $("<form method='post'></form>"),
                input;
            form.attr({"action":url});

            $.each(params,function(key,value){

                input = $("<input type='hidden' class='search-param'>");
                input.attr({"name":key});
                if (key == 'startTime' || key == 'endTime'){
                    input.val(new Date(value));
                }else{
                    input.val(value);
                }
                form.append(input);
            });
            form.appendTo(document.body);
            form.submit();
            document.body.removeChild(form[0]);
        });
    }

    // initDateQuery();
    initOrg("#areaId","#dmsSiteCode");
    pageInit().init();
    initExport(tableInit());
    initSelect();
});

var waybillCodes = null;//多运单查询用
//再次提报
function do_submitAgain(waybillCode) {
    waybillCodes = null;
    var url = '/abnormal/abnormalUnknownWaybill/submitAgain/' + waybillCode;
    $.ajaxHelper.doGetSync(url, null, function (res) {
        if (res && res.succeed) {
            if (res.data) {
                //指定查询
                waybillCodes = res.data;
                $("#startTime").val(null);
                $("#endTime").val(null);
            }
            alert(res.message);
            $('#dataTable').bootstrapTable('refresh');
        } else if (res) {
            alert(res.message);
        } else {
            alert('操作异常');
        }
    });
}

//初始化区域、分拣中心
function initOrg(orgSelect,siteSelect) {
    var url = "/services/bases/allorgs";
    var param = {};
    $.ajax({
        type : "get",
        url : url,
        data : param,
        async : false,
        success : function (data) {
            var result = [];
            for(var i in data){
                if(data[i].orgId && data[i].orgId != ""){
                    result.push({id:data[i].orgId,text:data[i].orgName});
                }
            }
            $(orgSelect).select2({
                width: '100%',
                placeholder:'请选择机构',
                allowClear:true,
                data:result
            });
            $(orgSelect).val(null).trigger('change');
            $(orgSelect)
                .on("change", function(e) {
                    var areaId = $(orgSelect).val();
                    if(areaId){
                        var siteListUrl = '/services/bases/dms/'+areaId;
                        findSite(siteSelect,siteListUrl);
                    }
                });
        }
    });
}

//机构-分拣中心 级联选择
function findSite(selectId,siteListUrl){
    $(selectId).empty();
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
            $(selectId).select2({
                width: '100%',
                placeholder:'请选择分拣中心',
                allowClear:true,
                data:result
            });
        }
    });
}

function initSelect() {
    $("#query-form #isReceiptSelect").select2({
        width: '100%',
        placeholder: '请选择',
        allowClear: true

    });
    $("#areaId").select2({
        width: '100%',
        placeholder:'请选择机构',
        allowClear:true
    });
    $("#dmsSiteCode").select2({
        width: '100%',
        placeholder:'请选择分拣中心',
        allowClear:true
    });


    $("#query-form #isReceiptSelect").val(null).trigger('change');
    //ID 冲突。。select2插件有问题
    $("#query-form #isReceiptSelect").on('change', function (e) {
        var v = $("#query-form #isReceiptSelect").val();
        if (v == 0 || v == 1) {
            $("#query-form #isReceipt").val(v);
        } else {
            $("#query-form #isReceipt").val(null);
        }
    });
}
function isEmptyObject(e) {
    var t;
    for (t in e)
        return !1;
    return !0
}

var isLoad = false;//是否初始化过 表格框
function refreshTable() {
    if(isLoad){
        tableInit().refresh();
    }else{
        tableInit().init();
        isLoad = true;
    }
}