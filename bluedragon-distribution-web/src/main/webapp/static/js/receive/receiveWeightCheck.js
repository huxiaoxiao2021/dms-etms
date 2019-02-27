$(function () {
    var queryUrl = '/receive/listData';
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
            title: '复核日期'
        }, {
            field: 'orderNumber',
            title: '包裹号'
        }, {
            field: 'traderName',
            title: '商家名称'
        }, {
            field: 'dmsSiteName',
            title: '复核区域'
        }, {
            field: 'areaName',
            title: '复核分拣'
        },{
            field: 'areaName',
            title: '复核人erp'
        },{
            field: 'areaName',
            title: '分拣重量kg'
        },{
            field: 'areaName',
            title: '复核长宽高cm'
        },{
            field: 'areaName',
            title: '复核体积cm³'
        },{
            field: 'areaName',
            title: '揽收区域'
        },{
            field: 'areaName',
            title: '揽收营业部'
        },{
            field: 'areaName',
            title: '揽收人erp'
        },{
            field: 'areaName',
            title: '揽收重量kg'
        },{
            field: 'areaName',
            title: '揽收长宽高cm'
        },{
            field: 'areaName',
            title: '揽收体积cm³'
        },{
            field: 'areaName',
            title: '重量差异'
        },{
            field: 'areaName',
            title: '体积重量差异'
        },{
            field: 'areaName',
            title: '误差标准值'
        },{
            field: 'areaName',
            title: '是否超标'
        }];
        oTableInit.refresh = function () {
            $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
        };
        return oTableInit;
    };
    var pageInit = function () {
        var oInit = new Object();
        oInit.init = function () {

            $('#btn_query').click(function() {
                tableInit().refresh();
            });

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
                elem: '#putawayDateGEStr',
                theme: '#3f92ea',
                type: 'endTime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });

            //查询
            $('#btn_query').click(function () {
                waybillCodes = null;//清空批量查询
                //校验输入的运单号
                var url = '/abnormal/abnormalUnknownWaybill/checkWaybillCode?waybillCodes=' + $("#waybillCode").val();
                $.ajaxHelper.doGetSync(url, null, function (res) {
                    if (res && !res.succeed) {
                        alert(res.message);
                    }else{
                        tableInit().refresh();
                    }
                });
            });

        };

        return oInit;
    };

    initOrg();
    initDateQuery();
    tableInit().init();
    pageInit().init();

});


function initDateQuery(){
    var v = $.dateHelper.formatDate(new Date());
    $("#startTime").val(v+" 00:00:00");
    $("#endTime").val(v+" 23:59:59");
}

var initLogin = true;
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
