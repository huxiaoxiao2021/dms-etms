$(function() {
    var saveUrl = '/consumable/waybillConsumableRecord/save';
    var confirmUrl = '/consumable/waybillConsumableRecord/confirmByIds';
    var detailUrl = '/consumable/waybillConsumableRecord/detail/';
    var queryUrl = '/consumable/waybillConsumableRecord/listData';
    var detailPageUrl = '/consumable/waybillConsumableRelation/toIndex';
    var exportDataUrl = '/consumable/waybillConsumableRecord/export';
    var packUserErpUrl = '/consumable/waybillConsumableRelation/updatePackUserErp';

    /*最多查询近x天数据*/
    var PAST_LIMIT = 94;
    var PERIOD_LIMIT = 32;
    $.combobox.createNew('confirmStatus',{
        width: '150',
        placeholder:'确认状态',
        allowClear:true,
        data:[
            {id:'0',text:'未确认'},
            {id:'1',text:'已确认'},
            {id:'2',text:'全部'}
        ]
    });
    var tableInit = function() {
        var oTableInit = new Object();
        oTableInit.init = function() {
            $('#dataTable').bootstrapTable({
                url : queryUrl, // 请求后台的URL（*）
                method : 'post', // 请求方式（*）
                toolbar : '#toolbar', // 工具按钮用哪个容器
                queryParams : oTableInit.getSearchParams, // 查询参数（*）
                // height : 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                uniqueId : "ID", // 每一行的唯一标识，一般为主键列
                pagination : true, // 是否显示分页（*）
                pageNumber : 1, // 初始化加载第一页，默认第一页
                pageSize : 10, // 每页的记录行数（*）
                pageList : [ 10, 25, 50, 100 ], // 可供选择的每页的行数（*）
                cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                sidePagination : "server", // 分页方式：client客户端分页，server服务端分页（*）
                striped : true, // 是否显示行间隔色
                showColumns : true, // 是否显示所有的列
                sortable : true, // 是否启用排序
                sortOrder : "asc", // 排序方式
//				search : true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
                showRefresh : true, // 是否显示刷新按钮
                minimumCountColumns : 2, // 最少允许的列数
                clickToSelect : true, // 是否启用点击选中行
                showToggle : true, // 是否显示详细视图和列表视图的切换按钮
//				showPaginationSwitch : true, // 是否显示分页关闭按钮
                strictSearch : true,
                // icons: {refresh: "glyphicon-repeat", toggle:
                // "glyphicon-list-alt", columns: "glyphicon-list"},
                // search:false,
                // cardView: true, //是否显示详细视图
                // detailView: true, //是否显示父子表
                // showFooter:true,
                // paginationVAlign:'center',
                // singleSelect:true,
                columns : oTableInit.tableColums
            });
        };
        oTableInit.getSearchParams = function(params) {
            var temp = oTableInit.getSearchCondition();
            if(!temp){
                temp={};
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
                if(_k && _v){
                    if(_k == 'startTime' || _k =='endTime'){
                        params[_k]=new Date(_v).getTime();;
                    }else {
                        if(_k == 'confirmStatus' && _v == "2"){

                        }else{
                            params[_k]=_v;
                        }
                    }
                }
            });
            return params;
        };
        oTableInit.tableColums = [ {
            checkbox : true
        },{
            field: 'id',
            title: 'ID',
            visible:false
        },{
            field : 'waybillCode',
            title : '运单号'
        },{
            field : 'receiveTime',
            title : '揽收时间',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        },{
            field : 'dmsName',
            title : '始发转运中心'
        },{
            field : 'receiveUserErp',
            title : '揽收人员ERP'
        },{
            field : 'confirmStatus',
            title : '状态',
            formatter : function(value,row,index){
                return value==1?'已确认':'未确认';
            }
        },{
            field : 'confirmUserErp',
            title : '确认人ERP'
        },{
            field : 'confirmTime',
            title : '确认时间',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        },{
            field : 'op',
            title : '操作',
            formatter : function (value, row, index) {
                return '<a class="gpdetail" href="javascript:void(0)" ><i class="glyphicon glyphicon-list-alt"></i>&nbsp;耗材明细&nbsp;</a>';
            },
            events: {
                'click .gpdetail': function(e, value, row, index) {
                    layer.open({
                        id:'detailFrame',
                        type: 2,
                        title:'耗材明细修改确认',
                        shadeClose: true,
                        shade: 0.7,
                        shadeClose: false,
                        maxmin: true,
                        area: ['1300px', '670px'],
                        content: detailPageUrl,
                        success: function(layero, index){
                            var waybillCode = row.waybillCode;
                            var confirmStatus = row.confirmStatus;
                            var frameId = document.getElementById("detailFrame").getElementsByTagName("iframe")[0].id;
                            var frameWindow = $('#' + frameId)[0].contentWindow;
                            frameWindow.$('#waybillCode-value-input').val(waybillCode);
                            frameWindow.$('#confirmStatus-value-input').val(confirmStatus);
                            frameWindow.$('#btn_query').click();
                        }
                    });
                }
            }
        }];
        oTableInit.refresh = function() {
            $('#dataTable').bootstrapTable('refreshOptions',{pageNumber:1});
            //$('#dataTable').bootstrapTable('refresh');
        };
        return oTableInit;
    };
    var pageInit = function() {
        var oInit = new Object();
        oInit.init = function() {
            $('#dataEditDiv').hide();
            /*起始时间*/ /*截止时间*/
            $.datePicker.createNew({
                elem: '#startTime',
                theme: '#3f92ea',
                type: 'datetime',
                // min: -PAST_LIMIT,//最近3个月内
                max: 0,//最近3个月内
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    $('#query-form').data("bootstrapValidator").resetForm();
                }
            });
            $.datePicker.createNew({
                elem: '#endTime',
                theme: '#3f92ea',
                type: 'datetime',
                // min: -PAST_LIMIT,//最近3个月内
                max: 0,//最近3个月内
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    $('#query-form').data("bootstrapValidator").resetForm();
                }
            });

            $('#btn_query').click(function() {
                var flag = $.formValidator.isValid('query-form');
                if(flag == true){
                    tableInit().refresh();
                }else{
                    $.msg.warn('查询条件有误','请您检查查询条件是否有误');
                };
            });
            $('#btn_add').click(function() {
                $('.edit-param').each(function () {
                    var _k = this.id;
                    if(_k){
                        $(this).val('');
                    }
                });
                $('#edit-form #typeGroup').val(null).trigger('change');
                $('#edit-form #parentId').val(null).trigger('change');
                $('#dataTableDiv').hide();
                $('#dataEditDiv').show();
            });
            // 修改操作
            $('#btn_edit').click(function() {
                var rows = $('#dataTable').bootstrapTable('getSelections');
                if (rows.length > 1) {
                    alert("修改操作，只能选择一条数据");
                    return;
                }
                if (rows.length == 0) {
                    alert("请选择一条数据");
                    return;
                }
                $.ajaxHelper.doPostSync(detailUrl+rows[0].id,null,function(res){
                    if(res&&res.succeed&&res.data){
                        $('.edit-param').each(function () {
                            var _k = this.id;
                            var _v = res.data[_k];
                            if(_k){
                                if(_v != null && _v != undefined){
                                    $(this).val(_v);
                                }else{
                                    $(this).val('');
                                }
                            }
                        });
                    }
                });
                $('#dataTableDiv').hide();
                $('#dataEditDiv').show();
            });

            // 批量确认
            $('#btn_confirm').click(function() {
                var rows = $('#dataTable').bootstrapTable('getSelections');
                if (rows.length < 1) {
                    $.msg.warn("错误，未选中数据");
                    return;
                }
                $.msg.confirm('是否确认这些运单？',function () {
                    var params = [];
                    for(var i in rows){
                        if(rows[i].confirmStatus != 1){
                            var data = {};
                            data["id"] = rows[i].id;
                            data["waybillCode"] = rows[i].waybillCode;
                            data["dmsId"] = rows[i].dmsId;
                            params.push(data);
                        }
                    }
                    if(params.length == 0){
                        $.msg.warn("至少选中一条未确认的数据，已确认的运单不会重复确认！");
                    }else{
                        $.ajaxHelper.doPostSync(confirmUrl,JSON.stringify(params),function(res){
                            if(res != null && res.code == 200){
                                $.msg.ok(res.message)
                                tableInit().refresh();
                            }else if (res != null && res.code == 400){
                                $.msg.warn(res.message);
                                tableInit().refresh();
                            }else {
                                $.msg.error('操作异常！');
                            }
                        });
                    }
                })
            });
            $('#btn_submit').click(function() {
                var params = {};
                $('.edit-param').each(function () {
                    var _k = this.id;
                    var _v = $(this).val();
                    if(_k && _v){
                        params[_k]=_v;
                    }
                });
                $.ajaxHelper.doPostSync(saveUrl,JSON.stringify(params),function(res){
                    if(res&&res.succeed){
                        alert('操作成功');
                        tableInit().refresh();
                    }else{
                        alert('操作异常');
                    }
                });
                $('#dataEditDiv').hide();
                $('#dataTableDiv').show();
            });
            $('#btn_return').click(function() {
                $('#dataEditDiv').hide();
                $('#dataTableDiv').show();
            });

            /*表单验证*/
            $.formValidator.createNew('query-form',{
                excluded: [':disabled', ':hidden', ':not(:visible)'],
                live: 'enabled',
                submitButtons: 'button[type="submit"]',
                message: '验证不通过',
                fields: {
                    startTime:{
                        validators: {
                            notEmpty: {
                                message: '请您选择起始时间'
                            }
                        }
                    },
                    endTime:{
                        validators: {
                            notEmpty: {
                                message: '请您选择截止时间'
                            }
                        }
                    }
                }
            });
            /*导出*/
            $('#btn_export').click(function () {
                var flag = $.formValidator.isValid('query-form');
                if(flag == true) {
                    /*时间校验*/
                    var now = new Date();
                    var startTimeStr = $.datePicker.getValue('startTime');
                    var endTimeStr = $.datePicker.getValue('endTime');
                    var receiveUserErp = $("#receiveUserErp").val();
                    var waybillCode = $("#waybillCode").val();
                    var confirmStatus = $("#confirmStatus").val();
                    /* var startTime = $.dateHelper.parseDateTime(startTimeStr);
                    var endTime = $.dateHelper.parseDateTime(endTimeStr);

                    if(window.date.subtract(endTime,startTime).toDays() > PERIOD_LIMIT){
                        $.msg.alert("时间范围有误：起始时间、截止时间间隔最大为1个月");
                        return;
                    }

                    if(startTime >= endTime){
                        $.msg.warn("起始时间必须小于截止时间！");
                        return;
                    }

                    if(startTime > now || endTime > now){
                        $.msg.warn("不能导出未来的数据！");
                        return;
                    }*/

                    var confirmMessage = '确认按照当前条件进行数据导出吗？（单次最大支持导出50000条数据，如超过50000条，请缩短时间范围多次导出）';

                    $.msg.confirm(confirmMessage,function () {
                        /*获取参数*/
                        var queryParams = $.formHelper.serialize('query-form');

                        var exportUrl = exportDataUrl + '?startTimeStr=' + startTimeStr + '&endTimeStr=' + endTimeStr;
                        if(receiveUserErp != ""){
                            exportUrl = exportUrl + '&receiveUserErp=' + receiveUserErp;
                        }
                        if(waybillCode != ""){
                            exportUrl = exportUrl + '&waybillCode=' + waybillCode;
                        }
                        if(confirmStatus != null && confirmStatus != "2"){
                            exportUrl = exportUrl + '&confirmStatus=' + confirmStatus;
                        }
                        console.log(exportUrl);
                        window.open(exportUrl);
                    });
                }else{
                    $.msg.warn('导出查询条件有误','请您检查导出查询条件是否有误');
                }
            });
            //
            $('#btn_update_erp').click(function() {
                var rows = $('#dataTable').bootstrapTable('getSelections');

                var confirmStatus = $('#confirmStatus-value-input').val()
                if (rows.length < 1) {
                    $.msg.warn("错误，未选中数据");
                    return;
                }

                var waybillCodeList = [];
                for(var i in rows){
                    if (rows[i].confirmStatus == 0) {
                        waybillCodeList.push(rows[i].waybillCode);
                    }
                };
                if (waybillCodeList.length == 0) {
                    $.msg.warn("至少选择一条未确认的运单，已确认的运单无法修改打包人ERP！");
                    return;
                }

                var packUserErp = $('#erp-value-input').val();
                if (packUserErp == null || packUserErp == '') {
                    $.msg.warn("请录入包装人ERP");
                    return;
                }

                var params = {packUserErp: packUserErp};
                params.waybillCodeList = waybillCodeList;
                $.msg.confirm('是否将这些数据的包装人更新为【' + packUserErp + '】？',function () {
                    var blocker = $.pageBlocker.block();
                    $.ajaxHelper.doPostSync(packUserErpUrl,JSON.stringify(params),function(res){
                        if(res.code == 200){
                            $.msg.ok('操作成功！');
                            tableInit().refresh();
                        }else{
                            $.msg.error(res.message);
                        }
                    });
                    $.pageBlocker.close(blocker);
                });

            })
        };
        return oInit;
    };

    initDateQuery();
    tableInit().init();
    pageInit().init();
});

function initDateQuery(){
    var startTime = $.dateHelper.formatDateTime(new Date(new Date().toLocaleDateString()));
    var endTime = $.dateHelper.formatDateTime(new Date(new Date(new Date().toLocaleDateString()).getTime()+24*60*60*1000-1));
    $("#startTime").val(startTime);
    $("#endTime").val(endTime);
}