$(function() {
	var saveUrl = '/collect/collectGoodsDetail/save';
	var deleteUrl = '/collect/collectGoodsDetail/deleteByIds';
  var detailUrl = '/collect/collectGoodsDetail/detail/';
  var queryUrl = '/business/businessReturnAdress/queryBusinessReturnAdressList';
  var exportUrl = '/business/businessReturnAdress/exportBusinessReturnAdressList';
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
				pageList : [ 10, 25, 50, 100 ,500 ], // 可供选择的每页的行数（*）
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
        oTableInit.tableColums = [{
            checkbox: true
        },{
            field : 'dmsSiteCode',
            title : '场地ID',
            align: 'center'
        },{
            field : 'dmsSiteName',
            title : '场地名称',
            align: 'center'
        }, {
            field : 'lastOperateTime',
            title : '最新换单时间',
            align: 'center',
            formatter: function (value, row, index) {
                if (value == null) {
                    return null;
                } else {
                    return $.dateHelper.formatDateTime(new Date(value));
                }
            }
        },{
            field : 'businessId',
            title : '商家ID',
            align: 'center'
        },{
            field : 'businessName',
            title : '商家名称',
            align: 'center'
        },{
            field : 'deptNo',
            title : '事业部编码',
            align: 'center'
        },{
            field : 'returnAdressStatusDesc',
            title : '此时是否已维护退货信息',
            align: 'center'
        },{
            field : 'returnQuantity',
            title : '退货量',
            align: 'center'
        }];
		oTableInit.refresh = function() {
			$('#dataTable').bootstrapTable('refresh');
		};
		return oTableInit;
	};
	var pageInit = function() {
		var oInit = new Object();
		oInit.init = function() {
			$('#dataEditDiv').hide();


            $.datePicker.createNew({
                elem: '#lastOperateTimeGteStr',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/


                }
            });
            $.datePicker.createNew({
                elem: '#lastOperateTimeLtStr',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });
            var v0 = $.dateHelper.formatDate(new Date());
            var v1 = $.dateHelper.formatDate(new Date());
            
            $("#lastOperateTimeGteStr").val(v0+" 00:00:00");
            $("#lastOperateTimeLtStr").val(v1+" 23:59:59");
            
		    $('#btn_query').click(function() {
		    	if(!checkQueryParams()){
		    		return;
		    	}
		    	tableInit().refresh();
			});
            $("#btn_export").on("click",function(e){
		    	if(!checkQueryParams()){
		    		return;
		    	}
                var params = tableInit().getSearchCondition();

                var form = $("<form method='post'></form>"),
                    input;
                form.attr({"action":exportUrl});

                $.each(params,function(key,value){

                    input = $("<input type='hidden' class='search-param'>");
                    input.attr({"name":key});
                    input.val(value);
                    form.append(input);
                });
                form.appendTo(document.body);
                form.submit();
                document.body.removeChild(form[0]);

            });
		};
		return oInit;
	};
	pageInit().init();
	tableInit().init();
});
function checkQueryParams(){
	var date0 = new Date($("#lastOperateTimeGteStr").val());
	var date1 = new Date($("#lastOperateTimeLtStr").val());
	if(date1.getTime()<date0.getTime()){
		alert('结束时间不能小于开始时间！');
		return false;
	}
	var date00 = $.dateHelper.addDays(date0,30);
	if(date1.getTime()>date00.getTime()){
		alert('时间区间不能大于30天！');
		return false;
	}	
	return true;
}