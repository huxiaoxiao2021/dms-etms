$(function () {
    var queryUrl = '/inventoryTask/listData';
    var exportUrl = '/inventoryTask/toExport';

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
            field: 'orgName',
            title: '区域',
            align: 'center'
        }, {
            field: 'orgId',
            title: '区域编号',
            align: 'center',
            visible: false
        }, {
            field: 'createSiteName',
            title: '操作场地',
            align: 'center'
        },{
            field: 'createSiteCode',
            title: '操作场地编号',
            align: 'center',
            visible: false
        },{
            field: 'directionName',
            title: '下游场地',
            align: 'center'
        },{
            field: 'inventoryScope',
            title: '盘点范围',
            align: 'center',
            formatter: function (value, row, index) {
                if (value == "1" )
                    return "自定义";
                else if (value == "2")
                    return "全场";
                else if (value == "3")
                return "异常区";
            }
        },{
            field: 'directionCode',
            title: '下游场地编号',
            align: 'center',
            visible: false
        },{
            field: 'inventoryTaskId',
            title: '任务码',
            align: 'center'
        },{
            field: 'waybillSum',
            title: '运单数',
            align: 'center'
        },{
            field: 'packageSum',
            title: '包裹数',
            align: 'center'
        },{
            field: 'exceptionSum',
            title: '差异数',
            align: 'center'
        },{
            field: 'createUserErp',
            title: '盘点人ERP',
            align: 'center'
        },{
            field: 'createTime',
            title: '创建时间',
            align: 'center',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        },{
            field: 'endTime',
            title: '完成时间',
            align: 'center',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
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
/*            $.datePicker.createNew({
                elem: '#createStartTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /!*重置表单验证状态*!/

                }
            });
            $.datePicker.createNew({
                elem: '#createEndTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /!*重置表单验证状态*!/

                }
            });

            /!*起始时间*!/
            /!*截止时间*!/
            $.datePicker.createNew({
                elem: '#completeStartTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /!*重置表单验证状态*!/

                }
            });
            $.datePicker.createNew({
                elem: '#completeEndTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /!*重置表单验证状态*!/

                }
            });*/

            //查询
            $('#btn_query').click(function () {
                tableInit().refresh();
            });

        };

        return oInit;
    };

    //导出
    function initExport(tableInit) {
        $('#btn_export').click(function () {
            var params = tableInit.getSearchCondition();
            var form = $("<form method='post'></form>"),
                input;
            form.attr({"action": exportUrl});

            $.each(params, function (key, value) {
                input = $("<input type='hidden' class='search-param'>");
                input.attr({"name": key});
                input.val(value);
                form.append(input);
            });
            form.appendTo(document.body);
            form.submit();
            document.body.removeChild(form[0]);
        });
    }

    initSelect();
    initOrg();
    initDateQuery();
    tableInit().init();
    pageInit().init();
    initExport(tableInit());

});


function initDateQuery(){
    var v = $.dateHelper.formatDate(new Date());
    $("#createStartTime").val(v+" 00:00:00");
    $("#createEndTime").val(v+" 23:59:59");
    $("#completeStartTime").val(v+" 00:00:00");
    $("#completeEndTime").val(v+" 23:59:59");

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
            if(data[0].code=200){
                for(var i in data){
                    if(data[i].siteCode && data[i].siteCode != ""){
                        result.push({id:data[i].siteCode,text:data[i].siteName});
                    }
                }
            }

            $(selectId).select2({
                placeholder:'请选择分拣中心',
                allowClear:true,
                data:result
            });

            $.combobox.clearAllSelected('site-select');
        }
    });
}

function findDirection(selectId,directionListUrl,initIdSelectId){
    $(selectId).html("");
    $.ajax({
        type : "get",
        url : directionListUrl,
        data : {},
        async : false,
        success : function (data) {
            list = data.data;
            var result = [];
            if(data.code == 200 && list.length > 0){
                for(var i in list){
                    if(list[i].code && list[i].name != ""){
                        result.push({id:list[i].code,text:list[i].name});
                    }
                }
                $(selectId).select2({
                    placeholder:'请选择下游场地',
                    allowClear:true,
                    data:result
                });

            } else {
                result.push({id:-99,text:"无下游目的地列表"})
                $(selectId).select2({
                    placeholder:'无下游目的地列表',
                    allowClear:true,
                    data:result
                });

            }
            $.combobox.clearAllSelected('direction-select');
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

            $('#org-select').select2({
                placeholder: '请选择机构',
                allowClear: true,
                data: result
            });

            $("#org-select")
                .on("change", function (e) {
                    $("#query-form #createSiteCode").val("");
                    var orgId = $("#org-select").val();
                    $("#query-form #orgId").val(orgId);
                    if (orgId) {
                        var siteListUrl = '/services/bases/dms/' + orgId;
                        findSite("#site-select", siteListUrl, "#query-form #createSiteCode");
                    }

                });

            $("#site-select").on("change", function (e) {
                var createSiteCode = $("#site-select").val();
                $("#query-form #createSiteCode").val(createSiteCode);

                if (createSiteCode) {
                    var directionListUrl = '/services/inventory/getDirectionList/' + createSiteCode;
                    findDirection("#direction-select", directionListUrl, "#query-form #directionCode");
                }
            });
            $("#direction-select").on("change", function (e) {
                var directionCode = $("#direction-select").val();
                if (directionCode != -99) {
                    $("#query-form #directionCode").val(directionCode);
                }
            });
            $.combobox.clearAllSelected('org-select');
            $.combobox.clearAllSelected('site-select');
            $.combobox.clearAllSelected('direction-select');
        }
    });

}
