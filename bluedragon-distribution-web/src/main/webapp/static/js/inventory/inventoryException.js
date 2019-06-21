$(function () {
    var queryUrl = '/inventoryException/listData';
    var exportUrl = '/inventoryException/toExport';
    var handleUrl = '/inventoryException/handle';

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
            field: 'id',
            title: '主键',
            align: 'center',
            visible: false
        },  {
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
            field: 'inventoryTaskId',
            title: '任务码',
            align: 'center'
        },{
            field: 'waybillCode',
            title: '运单号',
            align: 'center'
        },{
            field: 'packageCode',
            title: '包裹号',
            align: 'center'
        },{
            field: 'expType',
            title: '异常类型',
            align: 'center',
            formatter: function (value, row, index) {
                if (value == "1" )
                    return "多货";
                else if (value == "2")
                    return "少货";
            }
        },{
            field: 'latestPackStatus',
            title: '最新物流状态',
            align: 'center',
        },{
            field: 'expStatus',
            title: '处理状态',
            align: 'center',
            formatter: function (value, row, index) {
                if (value == "0" )
                    return "未处理";
                else if (value == "1")
                    return "已处理";
            }
        },{
            field: 'expDesc',
            title: '异常描述',
            align: 'center'
        },{
            field: 'createUserErp',
            title: '盘点任务创建人',
            align: 'center'
        },{
            field: 'inventoryUserErp',
            title: '盘点扫描人',
            align: 'center'
        },{
            field: 'createTime',
            title: '盘点创建时间',
            align: 'center',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        },{
            field: 'inventoryTime',
            title: '盘点扫描时间',
            align: 'center',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        },{
            field: 'expUserErp',
            title: '盘点任务创建人',
            align: 'center'
        },{
            field: 'expOperateTime',
            title: '盘点扫描时间',
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
            $.datePicker.createNew({
                elem: '#createStartTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });
            $.datePicker.createNew({
                elem: '#createEndTime',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });

            //查询
            $('#btn_query').click(function () {
                $("#query-form #isRepeat").val(0);
                tableInit().refresh();
            });

            //查询重复
            $('#btn_query_repeat').click(function () {
                $("#query-form #isRepeat").val(1);
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

    // 处理
    $('#btn_handle').click(function() {
        var rows = $('#dataTable').bootstrapTable('getSelections');
        if (rows.length < 1) {
            alert("错误，未选中数据");
            return;
        }
        var flag = confirm("是否处理这些数据?");
        if (flag == true) {
            var params = [];
            for(var i in rows){
                params.push(rows[i].id);
            };
            $.ajaxHelper.doPostSync(handleUrl,JSON.stringify(params),function(res){
                if(res&&res.succeed&&res.data){
                    alert('操作成功,处理'+res.data+'条。');
                    tableInit().refresh();
                }else{
                    alert('操作异常！');
                }
            });
        }
    });
});


function initDateQuery(){
    var v = $.dateHelper.formatDate(new Date());
    $("#createStartTime").val(v+" 00:00:00");
    $("#createEndTime").val(v+" 23:59:59");
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

    /*异常类型*/
    $('#expType').select2({
        width: '80',
        allowClear:true,
        placeholder:'请选择',
        data:[
            {id:'1',text:'多货'},
            {id:'2',text:'少货'}
            ]
    });

    /*处理状态*/
    $('#expStatus').select2({
        width: '100',
        allowClear:true,
        placeholder:'请选择',
        data:[
            {id:'0',text:'未处理'},
            {id:'1',text:'已处理'}
        ]
    });

    $.combobox.clearAllSelected('expType');
    $.combobox.clearAllSelected('expStatus');
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
                placeholder:'请选择操作单位',
                allowClear:true,
                data:result
            });

            if(initLogin){
                //第一次登录 初始化登录人分拣中心
                if($("#loginUserCreateSiteCode").val() != -1){
                    //登录人大区
                    $(selectId).val($("#loginUserCreateSiteCode").val()).trigger('change');
                }
            }
            initLogin = false;
        }
    });
}

var initLogin = true;
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
            });

            if ($("#loginUserOrgId").val() != -1) {
                //登录人大区
                $('#org-select').val($("#loginUserOrgId").val()).trigger('change');
            } else {
                $('#org-select').val(null).trigger('change');
            }

        }
    });

}
