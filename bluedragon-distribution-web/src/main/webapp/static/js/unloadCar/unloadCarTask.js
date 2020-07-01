$(function () {
    var queryUrl = '/unloadCarTask/listData';
    var distributeUrl = '/unloadCarTask/distributeTask';

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
            $(_selector).each(function () {
                var _k = this.id;
                var _v = $(this).val();
                if(_k && _v){
                    if(_k == 'startTime' || _k =='endTime'){
                        params[_k]=new Date(_v).getTime();;
                    }else{
                        params[_k]=_v;
                    }
                }
            });
            return params;
        };
        oTableInit.tableColums = [{
            checkbox: true
        }, {
            field: 'vehicleNumber',
            title: '车牌号',
            align: 'center'
        }, {
            field: 'startSiteName',
            title: '上游机构',
            align: 'center'
        },{
            field: 'sealTime',
            title: '上游封车时间',
            align: 'center',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
    //        visible: false
        },{
            field: 'sealCode',
            title: '封车号',
            align: 'center'
        },{
            field: 'batchCode',
            title: '批次号',
            align: 'center',
            formatter: function (value, row, index) {
                if (value != null )
                    return value.replace(/,/g,"<br>");
            }
        },{
            field: 'waybillNum',
            title: '运单总数',
            align: 'center'
        },{
            field: 'packageNum',
            title: '包裹总数',
            align: 'center'
        },{
            field: 'unloadUserErp',
            title: '卸车负责人',
            align: 'center'
        },{
            field: 'helperErps',
            title: '协助人员',
            align: 'center'
        },{
            field: 'distributeTime',
            title: '分配时间',
            align: 'center',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
        },{
            field: 'operateUserErp',
            title: '操作人',
            align: 'center'
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
                elem: '#startTime',
                theme: '#3f92ea',
                type: 'datetime',
                // min: -60,//最近30天内
                // max: 0,//最近30天内
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/
                }
            });
            $.datePicker.createNew({
                elem: '#endTime',
                theme: '#3f92ea',
                type: 'datetime',
                // min: -60,//最近30天内
                // max: 0,//最近30天内
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/
                }
            });

            //查询
            $('#btn_query').click(function () {
                tableInit().refresh();
            });
            $('#railWayPlatForm').keyup()

        };

        return oInit;
    };


    initSelect();
    initDateQuery();
    tableInit().init();
    pageInit().init();

    // 分配任务
    $('#btn_distribute').click(function() {
        var rows = $('#dataTable').bootstrapTable('getSelections');
        var unloadUser = $('#unloadUser').val();
        var railWayPlatForm = $('railWayPlatForm').val();
        if(unloadUser.length < 1){
            alert("错误，未填卸车负责人ERP!");
            return;
        }
        if (rows.length < 1) {
            alert("错误，未选中数据");
            return;
        }
        var flag = confirm("是否处理这些数据?");
        if (flag == true) {
            var sealCarCodes = [];
            for(var i in rows){
                sealCarCodes.push(rows[i].sealCarCode);
            };
            var request = new Object();
            request.unloadUserErp = unloadUser;
            request.railWayPlatForm = railWayPlatForm;
            request.sealCarCodes = sealCarCodes;

            $.ajaxHelper.doPostSync(distributeUrl,JSON.stringify(request),function(res){
                if(res && res.succeed && res.data){
                    alert('操作成功,处理'+res.data+'条。');
                    tableInit().refresh();
                }else{
                    alert(res.message);
                }
            });
        }
    });

    //查询卸车人姓名
    $('#unloadUser').focusout(function() {
        var unloadUser = $('#unloadUser').val();
        if(unloadUser.length < 1){
            alert("错误，未填卸车负责人ERP!");
            return;
        }

        var userUrl = '/unloadCarTask/getUserName?unloadUser=' + $("#unloadUser").val();
        $.ajaxHelper.doGetSync(userUrl,null,function(res){
            if(res && !res.succeed){
                alert(res.message);
            }
            $("#unloadUserName").val(res.data);
        });
    });
});


function initDateQuery(){
    var startTime = $.dateHelper.formatDateTime(new Date(new Date().toLocaleDateString()));
    var endTime = $.dateHelper.formatDateTime(new Date(new Date(new Date().toLocaleDateString()).getTime()+24*60*60*1000-1));
    $("#startTime").val(startTime);
    $("#endTime").val(endTime);

}

function initSelect() {
    var defualt = $("#query-form #distributeTypeSelect").val();
    $("#query-form #distributeType").val(defualt);
    $("#query-form #distributeTypeSelect").on('change', function (e) {
        var v = $("#query-form #distributeTypeSelect").val();
        if (v == 0 || v == 1 || v == 2) {
            $("#query-form #distributeType").val(v);
        } else {
            $("#query-form #distributeType").val(null);
        }
    });
}