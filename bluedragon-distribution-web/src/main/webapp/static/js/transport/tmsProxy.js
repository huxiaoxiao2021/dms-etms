$(function() {
    var queryUrl = '/transport/tmsProxy/listData';
    var tableInit = function() {
        var oTableInit = new Object();
        oTableInit.init = function() {
            $('#dataTable').bootstrapTable({
                url : queryUrl, // 请求后台的URL（*）
                method : 'post', // 请求方式（*）
                toolbar : '#toolbar', // 工具按钮用哪个容器
                queryParams : oTableInit.getSearchParams, // 查询参数（*）
                //height : 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
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
                    if(_k == 'requirePickupTimeBegin' || _k =='requirePickupTimeEnd'){
                        params[_k]=new Date(_v).getTime();;
                    }else{
                        params[_k]=_v;
                    }
                }
            });
            return params;
        };

        oTableInit.tableColums = [ {
            field: 'number',
            title: '序号',
            width:5 ,
            align:'center',
            switchable:false,
            formatter:function(value,row,index){
                return index+1;//这样的话每翻一页都会重新从1开始
                // var pageSize=$('#tableId').bootstrapTable('getOptions').pageSize;//通过表的#id 可以得到每页多少条
                // var pageNumber=$('#tableId').bootstrapTable('getOptions').pageNumber;//通过表的#id 可以得到当前第几页
                // return pageSize * (pageNumber - 1) + index + 1;    //返回每条的序号： 每页条数 * （当前页 - 1 ）+ 序号
            }
        }, {
            field : 'transBookCode',
            title : '委托书号'
        } ,{
            field : 'statusName',
            title : '状态'
        },{
            field : 'carrierName',
            title : '承运商'
        },{
            field : 'vehicleNumber',
            title : '车牌号'
        },{
            field : 'beginCityName',
            title : '始发城市'
        },{
            field : 'beginNodeName',
            title : '始发网点'
        },{
            field : 'endCityName',
            title : '目的城市'
        },{
            field : 'endNodeName',
            title : '目的网点'
        },{
            field : 'requirePickupTime',
            title : '预约提货时间',
            formatter : function(value){
                return $.dateHelper.formateDateTimeOfTs(value);
            },
            width:120,
            class:'min_120'
        },{
            field: 'printUrl',
            title: '操作',
            width:5 ,
            align:'center',
            switchable:false,
            formatter:function(value){
                return "<a href='javascript:;' onclick='print(\""+value+"\")'>打印</a>";
            }
        }];
        oTableInit.refresh = function() {
            var temp = oTableInit.getSearchCondition();
            if(!temp['requirePickupTimeBegin']){
                alert('开始时间不允许为空');
                return;
            }
            if(!temp['requirePickupTimeEnd']){
                alert('结束时间不允许为空');
                return;
            }
            /*var dates = Math.abs((temp['requirePickupTimeEnd'] - temp['requirePickupTimeBegin']))/(1000*60*60*24);
            if(dates>30){
                alert("时间间隔不允许超过30天");
                return;
            }*/
            // dates=Math.abs((new  Date()- temp['requirePickupTimeBegin']))/(1000*60*60*24);
            // if(dates>30){
            //     Jd.alert("只允许查询30天内的数据");
            //     return;
            // }
            $('#dataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
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
                elem: '#requirePickupTimeBegin',
                theme: '#3f92ea',
                type: 'datetime',
                // min: -30,//最近60天内
                max: 0,//最近60天内
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/
                }
            });
            $.datePicker.createNew({
                elem: '#requirePickupTimeEnd',
                theme: '#3f92ea',
                type: 'datetime',
                // min: -30,//最近60天内
                max: new Date().setDate(new Date().getDate() +1),//最近60天内
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/
                }
            });


            $('#btn_query').click(function() {
                tableInit().refresh();
            });
        };
        return oInit;
    };
    loadSite({})
    initDateQuery();
    tableInit().init();
    pageInit().init();
});

function initDateQuery(){
    var requirePickupTimeBegin = $.dateHelper.formatDateTime(new Date(new Date().toLocaleDateString()));
    var requirePickupTimeEnd = $.dateHelper.formatDateTime(new Date(new Date(new Date().toLocaleDateString()).getTime()+24*60*60*1000-1));
    $("#requirePickupTimeBegin").val(requirePickupTimeBegin);
    $("#requirePickupTimeEnd").val(requirePickupTimeEnd);
}
function print(url){
    window.open(url, '_blank','menubar=no,toolbar=no, status=no,scrollbars=yes');
}

//加载分拣中心
var loadSite = function (params) {
    var siteListUrl = '/transport/tmsProxy/getAllSiteList';
    $.ajax({
        type: "post",
        url: siteListUrl,
        data: params,
        async: true,
        success: function (data) {
            var result = [];
            if (data) {
                for (var i in data) {
                    if (data[i].dmsSiteCode && data[i].dmsSiteCode != "") {
                        result.push({id: data[i].dmsSiteCode, text: data[i].siteName});
                    }
                }
            }
            $("#endNodeCode").empty();
            $("#query-form #endNodeCode").select2({
                placeholder: '请选择',
                allowClear: true,
                data: result
            });
        }
    });
}
