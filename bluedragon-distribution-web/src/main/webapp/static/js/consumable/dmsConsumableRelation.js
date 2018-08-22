$(function() {
	var saveUrl = '/consumable/dmsConsumableRelation/save';
	var deleteUrl = '/consumable/dmsConsumableRelation/deleteByIds';
  	var detailUrl = '/consumable/dmsConsumableRelation/detail/';
  	var queryUrl = '/consumable/dmsConsumableRelation/listData';
	var enableUrl = '/consumable/dmsConsumableRelation/enableByCodes';
	var disableUrl = '/consumable/dmsConsumableRelation/disableByCodes';
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
				field: 'dmsId',
				title: '分拣中心编号',
				visible:false
			},
			{
				field: 'dmsName',
				title: '分拣中心名称'
			},
			{
				field: 'code',
				title: '耗材编号'
			},
			{
				field: 'type',
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
				field: 'operateUserCode',
				title: '操作人编号',
				visible:false
			},
			{
				field: 'operateUserErp',
				title: '操作人ERP'
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
				},
				visible:false
			},
			{
				field: 'status',
				title: '启用状态',
				formatter : function (value, row, index) {
					if(value == 0)
					{
						return '<span style="color: red;font-weight: bold;">未启用</span>';
					} else if(value == 1){
						return '<span style="color: green;font-weight: bold;">启用</span>';
					} else {
						return value;
					}
				}
			}
		];
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
			//启用
			$('#btn_enable').click(function() {
				var rows = $('#dataTable').bootstrapTable('getSelections');
				if (rows.length < 1) {
					$.msg.error("错误，未选中数据！");
					return;
				}
				$.msg.confirm('是否启用这些耗材信息？',function () {
					var params = [];
					for(var i in rows){
						params.push(rows[i].code);
					};
					$.ajaxHelper.doPostSync(enableUrl,JSON.stringify(params),function(res){
						if(res&&res.succeed&&res.data){
							$.msg.ok('操作成功！','',function () {
								tableInit().refresh();
							});

						}else{
							$.msg.error("操作异常！",res.data);
						}
					});
				})
			});

			//启用
			$('#btn_disable').click(function() {
				var rows = $('#dataTable').bootstrapTable('getSelections');
				if (rows.length < 1) {
					$.msg.error("错误，未选中数据！");
					return;
				}
				$.msg.confirm('是否停用这些耗材信息？',function () {
					var params = [];
					for(var i in rows){
						params.push(rows[i].code);
					};
					$.ajaxHelper.doPostSync(disableUrl,JSON.stringify(params),function(res){
						if(res&&res.succeed&&res.data){
							$.msg.ok('操作成功！','',function () {
								tableInit().refresh();
							});

						}else{
							$.msg.error("操作异常！",res.data);
						}
					});
				})
			});

		};
		return oInit;
	};
	
	tableInit().init();
	pageInit().init();
});