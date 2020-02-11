$(function () {
    var sendQueryUrl = '/abnormalDispose/abnormalDispose/send/listData';
    var pushAbnormalOrderUrl = '/services/abnormalorder/pushAbnormalOrders';//外呼地址
    var pushExceptioninfoUrl = '/services/qualitycontrol/exceptioninfos';//异常地址
    var pushExceptionInfoCheckUrl = '/services/qualitycontrol/redeliverychecknew';//异常提交前的校验：协商再投状态校验
    var exportUrl = '/abnormalDispose/abnormalDispose/send/toExport';
    var tableInit = function () {
        var oTableInit = new Object();
        oTableInit.init = function () {
            $('#dataTableSend').bootstrapTable({
                url: sendQueryUrl, // 请求后台的URL（*）
                method: 'post', // 请求方式（*）
                toolbar: '#toolbar_send', // 工具按钮用哪个容器
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
                _selector = ".search-param-send";
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
            field: 'inspectionDate',
            title: '验货时间',
            formatter: function (value, row, index) {
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        }, {
            field: 'inspectionSiteName',
            title: '验货分拣中心'
        }, {
            field: 'waybillCode',
            title: '运单号'
        }, {
            field: 'nextAreaName',
            title: '下级区域'
        }, {
            field: 'nextSiteName',
            title: '下级站点'
        }, {
            field: 'endCityName',
            title: '目的城市'
        }, {
            field: 'endSiteName',
            title: '目的站点'
        }, {
            field: 'isDispose',
            title: '是否提报异常',
            formatter: function (value, row, index) {
                if (value == '1') {
                    return '是';
                } else {
                    return '否';
                }
            }
        }, {
            field: 'abnormalType',
            title: '异常类型',
            formatter: function (value, row, index) {
                if (value == '1') {
                    return '外呼';
                } else if (value == '0') {
                    return '异常';
                } else {
                    return ;
                }
            }
        }, {
            field: 'abnormalReason1',
            title: '一级原因'
        }, {
            field: 'createUser',
            title: '异常提交人'
        }, {
            field: 'createTime',
            title: '异常提交时间',
            formatter: function (value, row, index) {
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        }];
        oTableInit.refresh = function () {
            $('#dataTableSend').bootstrapTable('refreshOptions', {pageNumber: $('#dataTableSend').bootstrapTable.pageNumber});
        };
        return oTableInit;
    };
    var abnormal1 = [];//异常一级原因
    var abnormal2 = {};//异常二级原因
    var outcall1 = [];//外呼一级原因
    var outcall2 = {};//外呼二级原因
    //加载
    var loadDictionar = function (type) {
        var requestFlag = false;
        var params = {};
        if (type == 0 && abnormal1.length == 0) {//异常
            requestFlag = true;
            params.typeGroups = '110-7040';
            abnormal1.push({id: 0, text: '0-请选择'});

        } else if (type == 1 && outcall1.length == 0) {//外呼
            requestFlag = true;
            params.typeGroups = '5003';
            outcall1.push({id: 0, text: '0-请选择'});
        }
        if (requestFlag) {//需要请求后台
            var dictionarListUrl = '/services/bases/getBaseDictionaryTreeMulti';
            $.ajax({
                type: "get",
                url: dictionarListUrl,
                data: params,
                async: false,
                success: function (response) {
                    var result = [];
                    var v_data = response.data;
                    for (var i in v_data) {
                        var v_text = v_data[i].typeCode + '-' + v_data[i].typeName;
                        var v_id = v_data[i].typeCode;
                        var v_vid = v_data[i].id;
                        var v_typeGroup = v_data[i].typeGroup;
                        if (v_data[i].nodeLevel == 2) {//作为一级原因
                            if (type == 0) {
                                abnormal1.push({id: v_id, text: v_text, vid: v_vid, typeGroup: v_typeGroup});
                            } else {
                                outcall1.push({id: v_id, text: v_text, vid: v_vid, typeGroup: v_typeGroup});
                            }
                        }
                        if (v_data[i].nodeLevel == 3) {//作为二级原因
                            if (type == 0) {
                                if (!abnormal2[v_data[i].parentId]) {
                                    abnormal2[v_data[i].parentId] = [{id: 0, text: '0-请选择'}];
                                }
                                abnormal2[v_data[i].parentId].push({
                                    id: v_id,
                                    text: v_text,
                                    vid: v_vid,
                                    typeGroup: v_typeGroup
                                });
                            } else {
                                if (!outcall2[v_data[i].parentId]) {
                                    outcall2[v_data[i].parentId] = [{id: 0, text: '0-请选择'}];
                                }
                                outcall2[v_data[i].parentId].push({
                                    id: v_id,
                                    text: v_text,
                                    vid: v_vid,
                                    typeGroup: v_typeGroup
                                });
                            }
                        }
                    }
                }
            });
        }
        var result = type == 0 ? abnormal1 : outcall1;
        $("#edit-form #abnormalReason1Select1").empty();
        $("#edit-form #abnormalReason1Select1").select2({
            width: '100%',
            placeholder: 0,
            allowClear: false,
            dropdownCss: {'z-index': 999999},
            data: result
        });
        $("#edit-form #abnormalReason1Select1").val(0).trigger('change');

    }
    //一级原因和二级的级联
    var abnormalReason1Select1Change = function () {
        var type = $('input:radio:checked').val();
        var abnormalReason1Select1 = $("#abnormalReason1Select1").val();
        var result = [];
        if (abnormalReason1Select1) {
            result = type == 0 ? abnormal2[abnormalReason1Select1] : outcall2[abnormalReason1Select1];
        }
        $("#edit-form #abnormalReason1Select2").empty();
        $("#edit-form #abnormalReason1Select2").select2({
            width: '100%',
            placeholder: 0,
            allowClear: false,
            dropdownCss: {'z-index': 999999},
            data: result
        });
        $("#edit-form #abnormalReason1Select2").val(null).trigger('change');
    }

    //提报异常
    function sumbitQc() {
        var waybillcodes = $('#waybillCodeSend').val();
        var type = $('#abnormalTypeSend').val();
        var abnormalReason1Select1 = $("#abnormalReason1Select1").select2("data")[0];
        var abnormalReason1Select2 = $("#abnormalReason1Select2").select2("data")[0];
        var usercode = $("#currUserCode").text();
        if (!usercode) {
            return;
        }
        var params = {};
        params.waveBusinessId = $('#waveBusinessIdSend').val();
        if (type == 1) {//发外呼
            $("#exceptionOutBoundOffLine").jqmShow();
            return;
        } else {
            params.userERP = usercode;
            params.qcType = 2;//2代表运单
            params.qcValue = waybillcodes;
            if (abnormalReason1Select1.id > 0) {
                params.qcCode = parseInt(abnormalReason1Select1.id);
                params.qcName = abnormalReason1Select1.text;
                params.isSortingReturn = abnormalReason1Select1.typeGroup == 110;
            } else {
                params.qcCode = 0;
                params.qcName = '';
                Jd.alert("请选择原因");
                return;
            }
            if (abnormalReason1Select2 && abnormalReason1Select2.id > 0) {
                params.qcCode = parseInt(abnormalReason1Select2.id);
                params.qcName = abnormalReason1Select2.text;
                params.isSortingReturn = abnormalReason1Select2.typeGroup == 110;
            }
            pushExceptionInfo(params);
        }

    }

    var pageInit = function () {
        var oInit = new Object();
        var postdata = {};
        oInit.init = function () {
            $('#sendDetail').hide();
            $('#dataEditDiv').hide();
            // 初始化页面上面的按钮事件
            $('#btn_query_send').click(function () {
                tableInit().refresh();
            });
            $('#btn_back_send').click(function () {
                $('#sendDetail').hide();
                $('#dataTableMainDiv').show();
                $("#isDisposeSendSelect").val(2).trigger('change');
                $("#query-form-send #isDisposeSend").val(null);
                $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
            });
            $('#abnormal_save').click(function () {
                sumbitQc();
            });

            //注册一级原因和二级原因的级联事件
            $("#edit-form #abnormalReason1Select1")
                .on("change", function (e) {
                    abnormalReason1Select1Change();
                });
            //弹窗行为
            $('#dataEditDiv').jqm({modal: true});
            $('#dataEditDiv').css('opacity', 1);//透明程度
            $('#exceptionOutBoundOffLine').jqm({modal: true});
            $('#exceptionOutBoundOffLine').css('opacity', 1);//透明程度
            $('#btn_abnormal').click(function () {
                var rows = $('#dataTableSend').bootstrapTable('getSelections');
                if (rows && rows.length > 0) {
                    var waybillCodes = '';
                    for (var i = 0; i < rows.length; i++) {
                        waybillCodes = waybillCodes + ',' + rows[i].waybillCode;
                    }
                    $('#waybillCodeSend').val(waybillCodes.substring(1));
                    $('#dataEditDiv').jqmShow();
                } else {
                    Jd.alert("请选择运单");
                }
            });
            //加载下拉
            loadDictionar(0);
            //注册事件
            $('input:radio[name="abnormalTypeRadio"]').change(function () {
                var type = $('input:radio:checked').val();
                $('#abnormalTypeSend').val(type);
                loadDictionar(type);
            });
        };
        return oInit;
    };
    var initSelect = function () {
        //ID 冲突。。select2插件有问题
        $("#query-form-send #isDisposeSendSelect").on('change', function (e) {
            var v = $("#query-form-send #isDisposeSendSelect").val();
            if (v == 0 || v == 1) {
                $("#query-form-send #isDisposeSend").val(v);
            }else{
                $("#query-form-send #isDisposeSend").val(null);
            }
        });
    }
    //初始化导出按钮
    var initExport = function (tableInit) {
        $("#btn_export_send").on("click", function (e) {

            var params = tableInit.getSearchCondition();

            if ($.isEmptyObject(params)) {
                Jd.alert('禁止全量导出，请确定查询范围');
                return;
            }
            var form = $("<form method='post'></form>"),
                input;
            form.attr({"action": exportUrl});

            $.each(params, function (key, value) {

                input = $("<input type='hidden' class='search-param'>");
                input.attr({"name": key});
                if (key == 'startTime' || key == 'endTime') {
                    input.val(new Date(value));
                } else {
                    input.val(value);
                }
                form.append(input);
            });
            form.appendTo(document.body);
            form.submit();
            document.body.removeChild(form[0]);
        });
    }
    tableInit().init();
    pageInit().init();
    initSelect();
    initExport(tableInit());

    var pushExceptionInfo = function (param){
        var checkParam = {};
        checkParam.code = param.qcValue;
        checkParam.codeType = param.qcType;

        jQuery.ajax({
            type: "POST",
            url: pushExceptionInfoCheckUrl,
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(checkParam),
            async: false,
            success: function (response) {
                if (response.code == 200) {
                    if (response.data.isCompleted == false) {
                        Jd.alert("此条码包含【发起协商再投未处理】状态的运单：" + response.data.waybillCode +  "需商家审核完成才能提交异常");
                        return;
                    } else {
                        jQuery.ajax({
                            type: "POST",
                            url: pushExceptioninfoUrl,
                            contentType: 'application/json',
                            dataType: 'json',
                            data: JSON.stringify(param),
                            async: false,
                            success: function (response) {
                                if (response.code == 200) {
                                    $('#dataEditDiv').jqmHide();
                                    Jd.alert("异常提报成功，系统正在处理，请稍后刷新查看");
                                    $('#dataTableSend').bootstrapTable('refreshOptions', {pageNumber: 1});
                                } else {
                                    Jd.alert(response.message);
                                }

                            },
                            error: function () {
                                Jd.alert("服务器异常");
                            }
                        });
                    }
                } else {
                    Jd.alert(response.message);
                }

            },
            error: function () {
                Jd.alert("服务器异常");
            }
        });
    }
});

function querySend(waveBusinessId, siteCode, num) {
    if (num && num > 5000) {
        Jd.alert("班次未结束，暂不开放明细查看");
        return;
    }
    $('#dataTableMainDiv').hide();
    $('#sendDetail').show();
    $('#waveBusinessIdSend').val(waveBusinessId);
    $('#siteCodeSend').val(siteCode);
    $('#dataTableSend').bootstrapTable('refreshOptions', {pageNumber: 1});
}
