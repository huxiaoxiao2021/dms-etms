$(function() {
	var queryUrl = '/half/packageHalfRedelivery/listData';
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
				showToggle : false, // 是否显示详细视图和列表视图的切换按钮
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
					}else{
						params[_k]=_v;
					}
				}
			});
			return params;
		};


		oTableInit.tableColums = [ {
            field: '#',
            title: '操作',
            align:'center',
            formatter:function(value,row,index){
                return row.dealState == 1? "<a href='javascript:;' onclick='goto(\""+row.waybillCode+"\")'>处理</a>" : "";
            }
        },{
			field : 'waybillCode',
			title : '运单号'
		} ,{
			field : 'packageCode',
			title : '包裹号'
		} ,{
			field : 'dealState',
			title : '状态',
			formatter : function(value,row,index){
				return value==1?'已反馈':'已处理';

			}
		},{
			field : 'createUser',
			title : '操作人ERP'
		},{
			field : 'dmsSiteName',
			title : '所属机构'
		}, {
			field : 'packageState',
			title : 'ECLP反馈结果',
			formatter : function(value,row,index){
				return value==570?'待再投':value==580?'待拒收':value==590?'待报废':'其他-'+ value.toString();

			}
		} ,{
			field : 'eclpDealTime',
			title : 'ECLP反馈时间',
			formatter : function(value,row,index){
				return $.dateHelper.formateDateTimeOfTs(value);
			},
			width:120,
			class:'min_120'
		} ];
		oTableInit.refresh = function() {
			$('#dataTable').bootstrapTable('refreshOptions',{pageNumber:1});
			//$('#dataTable').bootstrapTable('refresh');
		};
		return oTableInit;
	};
	var pageInit = function() {
		var oInit = new Object();
		var postdata = {};
		oInit.init = function() {
			/*起始时间*/ /*截止时间*/
			$.datePicker.createNew({
				elem: '#startTime',
				theme: '#3f92ea',
				type: 'datetime',
				min: -60,//最近30天内
				max: 0,//最近30天内
				btns: ['now', 'confirm'],
				done: function(value, date, endDate){
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
	initDateQuery();
	tableInit().init();
	pageInit().init();
	initSelect();
});

function initSelect(){
	$("#query-form #isReceiptSelect").select2({
		width: '100%',
		placeholder:'请选择',
		allowClear:true

	});
	$("#query-form #isReceiptSelect").val(null).trigger('change');
	//ID 冲突。。select2插件有问题
	$("#query-form #isReceiptSelect").on('change',function(e){
		var v = $("#query-form #isReceiptSelect").val();
		if(v == 1 || v == 2){
			$("#query-form #dealState").val(v);
		}
	});
}

function initDateQuery(){
	var startTime = $.dateHelper.formatDateTime(new Date(new Date().toLocaleDateString()));
	var endTime = $.dateHelper.formatDateTime(new Date(new Date(new Date().toLocaleDateString()).getTime()+24*60*60*1000-1));
	$("#startTime").val(startTime);
	$("#endTime").val(endTime);
}

function goto(waybillCode){
    window.location.href = "http://" + window.location.host +"/half/packageHalfDetail/" + waybillCode;
}