/*****************************************/
/*耗材明细 shipeilin*/
/*****************************************/
$(function () {
    /*****************************************/
    /*变量及方法*/
    /*****************************************/
    /*增删改查URL*/
    var saveUrl = '/consumable/waybillConsumableRelation/save';
    var deleteUrl = '/consumable/waybillConsumableRelation/deleteByIds';
    var queryUrl = '/consumable/waybillConsumableRelation/listData';
    var modifyInfoPageUrl = '/consumable/waybillConsumableRelation/getModifyPage';
    var addInfoPageUrl = '/consumable/waybillConsumableRelation/getAddPage';
    var checkModify = '/consumable/waybillConsumableRecord/check/canModify';
    var packUserErpUrl = '/consumable/waybillConsumableRelation/updatePackUserErp';
    /*****************************************/
    /*组件*/
    /*****************************************/

    /*表格*/
    var tableInit = function() {
        var oTableInit = new Object();
        oTableInit.init = function() {
            $('#dataTable').bootstrapTable({
                url : queryUrl, // 请求后台的URL（*）
                method : 'post', // 请求方式（*）
                toolbar : '#toolbar', // 工具按钮用哪个容器
                queryParams : oTableInit.getSearchParams, // 查询参数（*）
                height : 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
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
                if(_k && (_v != null && _v != '')){
                    params[_k] = _v;
                }
            });
            return params;
        };
        oTableInit.tableColums = [
            {
                checkbox: true
            },
            {
                field: 'id',
                title: 'ID',
                visible:false
            },
            {
                field: 'waybillCode',
                title: '运单号'
            },
            {
                field: 'consumableCode',
                title: '耗材编号'
            },
            {
                field: 'type',
                title: '耗材类型编号',
                visible:false
            },
            {
                field: 'typeName',
                title: '耗材类型'
            },
            {
                field: 'name',
                title: '耗材名称'
            },
            {
                field: 'volume',
                title: '体积（立方厘米）'
            },
            {
                field: 'volumeCoefficient',
                title: '体积系数'
            },
            {
                field: 'specification',
                title: '规格（厘米）'
            },
            {
                field: 'unit',
                title: '单位'
            },
            {
                field: 'receiveQuantity',
                title: '揽收数量'
            },
            {
                field: 'confirmQuantity',
                title: '确认数量'
            },
            {
                field: 'operateUserErp',
                title: '操作人ERP'
            },
            {
                field: 'packUserErp',
                title: '打包人ERP'
            },
            {
                field: 'operateTime',
                title: '操作时间',
                formatter : function (value, row, index) {
                    if(value == null){
                        return null;
                    }else {
                        return $.dateHelper.formatDateTime(new Date(value));
                    }
                }
            },
            {
                field: 'op',
                title: '操作',
                formatter : function (value, row, index) {
                    return '<a class="mdinfo" href="javascript:void(0)" ><i class="glyphicon glyphicon-pencil"></i>&nbsp;修改数量&nbsp;</a>';
                },
                events: {
                    'click .mdinfo': function(e, value, row, index) {
                        var params = {waybillCode: $('#waybillCode-value-input').val()};

                        $.ajaxHelper.doPostSync(checkModify,JSON.stringify(params),function(res){
                            if(res.code != 200)
                            {
                                $.msg.error($('#waybillCode-value-input').val() + "校验异常！");
                            } else {
                                if (res.data == false) {
                                    $.msg.warn($('#waybillCode-value-input').val() + "【已确认】或为【寄付运费运单】，不允许修改耗材使用数量！");
                                }
                                else {
                                    layer.open({
                                        id:'modifyInfoFrame',
                                        type: 2,
                                        title:'耗材信息修改',
                                        shadeClose: true,
                                        shade: 0.7,
                                        shadeClose: false,
                                        maxmin: true,
                                        area: ['850px', '380px'],
                                        content: modifyInfoPageUrl,
                                        success: function(layero, index){
                                            var id = row.id;
                                            var code = row.consumableCode;
                                            var name = row.name;
                                            var type = row.type;
                                            var typeName = row.typeName;
                                            var volume = row.volume;
                                            var volumeCoefficient = row.volumeCoefficient;
                                            var specification = row.specification;
                                            var unit = row.unit;
                                            var receiveQuantity = row.receiveQuantity;
                                            var confirmQuantity = row.confirmQuantity;
                                            var packUserErp = row.packUserErp;
                                            var waybillCode = row.waybillCode;

                                            var frameId = document.getElementById("modifyInfoFrame").getElementsByTagName("iframe")[0].id;
                                            var frameWindow = $('#' + frameId)[0].contentWindow;
                                            frameWindow.$('#id-value-input').val(id);
                                            frameWindow.$('#code-value-input').val(code);
                                            frameWindow.$('#name-value-input').val(name);
                                            frameWindow.$('#type-value-input').val(typeName);
                                            frameWindow.$('#volume-value-input').val(volume);
                                            frameWindow.$('#volume-coefficient-value-input').val(volumeCoefficient);
                                            frameWindow.$('#specification-value-input').val(specification);
                                            frameWindow.$('#unit-value-input').val(unit);
                                            frameWindow.$('#receive-value-input').val(receiveQuantity);
                                            frameWindow.$('#confirm-value-input').val(confirmQuantity);
                                            frameWindow.$('#erp-value-input').val(packUserErp);
                                            frameWindow.$('#waybillCode-value-input').val(waybillCode);
                                        }
                                    });
                                }
                            }
                        },'json');

                    }
                }
            }];
        oTableInit.refresh = function() {
            $('#dataTable').bootstrapTable('refresh');
        };
        return oTableInit;
    };

    var pageInit = function() {
        var oInit = new Object();
        oInit.init = function() {

            $('#btn_query').click(function() {
                var queryParams = $.formHelper.serialize('query-form');
                /*表格查询*/
                $.bootGrid.refreshOptions('dataTable',queryUrl,queryParams);
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
        };
        return oInit;
    };

    tableInit().init();
    pageInit().init();

    /*增加*/
    $('#btn_add').click(function () {

        var params = {waybillCode: $('#waybillCode-value-input').val()};

        $.ajaxHelper.doPostSync(checkModify,JSON.stringify(params),function(res){
            if(res.code != 200)
            {
                $.msg.error($('#waybillCode-value-input').val() + "校验异常！");
            } else {
                if (res.data == false) {
                    $.msg.warn($('#waybillCode-value-input').val() + "【已确认】或为【寄付运费运单】，不允许增加耗材信息！");
                }
                else {
                    layer.open({
                        id:'addInfoFrame',
                        type: 2,
                        title:'增加包装耗材信息',
                        shadeClose: true,
                        shade: 0.7,
                        maxmin: true,
                        shadeClose: false,
                        area: ['800px', '380px'],
                        content: addInfoPageUrl,
                        success: function(layero, index){
                            var frameId = document.getElementById("addInfoFrame").getElementsByTagName("iframe")[0].id;
                            var frameWindow = $('#' + frameId)[0].contentWindow;
                            frameWindow.$('#waybillCode-value-input').val($('#waybillCode-value-input').val());
                        }
                    });
                }
            }
        },'json');

    });

    // 删
    $('#btn_delete').click(function() {
        var rows = $('#dataTable').bootstrapTable('getSelections');
        if (rows.length < 1) {
            $.msg.warn("错误，未选中数据");
            return;
        }

        $.msg.confirm('是否删除这些数据？',function () {
            var blocker = $.pageBlocker.block();
            var params = {waybillCode: $('#waybillCode-value-input').val()};

            $.ajaxHelper.doPostSync(checkModify, JSON.stringify(params), function (res) {
                if (res.code != 200) {
                    $.msg.error($('#waybillCode-value-input').val() + "校验异常！");
                } else {
                    if (res.data == false) {
                        $.msg.warn($('#waybillCode-value-input').val() + "【已确认】或为【寄付运费运单】，不允许删除耗材信息！");
                    }
                    else {
                        var ids = [];
                        for(var i in rows){
                            ids.push(rows[i].id);
                        };
                        params.ids = ids;
                        $.ajaxHelper.doPostSync(deleteUrl,JSON.stringify(params),function(res){
                            if(res&&res.succeed&&res.data){
                                $.msg.ok('操作成功,删除'+res.data+'条。');
                                tableInit().refresh();
                            }else{
                                $.msg.error('操作异常！');
                            }
                        });
                    }
                }
            }, 'json');
            $.pageBlocker.close(blocker);
        });

    });

    //
    $('#btn_update_erp').click(function() {
        var rows = $('#dataTable').bootstrapTable('getSelections');

        var confirmStatus = $('#confirmStatus-value-input').val()
        if (confirmStatus == 1) {
            $.msg.warn($('#waybillCode-value-input').val() + "【已确认】，不允许更新打包人ERP！");
            return;
        }
        if (rows.length < 1) {
            $.msg.warn("错误，未选中数据");
            return;
        }
        var packUserErp = $('#erp-value-input').val();
        if (packUserErp == null || packUserErp == '') {
            $.msg.warn("请录入包装人ERP");
            return;
        }

        var params = {packUserErp: packUserErp};
        $.msg.confirm('是否将这些数据的包装人更新为【' + packUserErp + '】？',function () {
            var blocker = $.pageBlocker.block();

            var ids = [];
            for(var i in rows){
                ids.push(rows[i].id);
            };
            params.ids = ids;

            $.ajaxHelper.doPostSync(packUserErpUrl,JSON.stringify(params),function(res){
                if(res&&res.succeed&&res.data){
                    $.msg.ok('操作成功！');
                    tableInit().refresh();
                }else{
                    $.msg.error(res.message);
                }
            });
            $.pageBlocker.close(blocker);
        });

    });
});
