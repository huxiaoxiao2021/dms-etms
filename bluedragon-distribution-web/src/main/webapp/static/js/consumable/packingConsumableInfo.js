$(function() {
	var saveUrl = '/consumable/packingConsumableInfo/save';
	var queryUrl = '/consumable/packingConsumableInfo/listData';
	var modifyInfoPageUrl = '/consumable/packingConsumableInfo/getModifyPage';
	var addInfoPageUrl = '/consumable/packingConsumableInfo/getAddPage';
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
			field: 'id',
			title: 'ID',
			visible:false
			},
			{
				field: 'code',
				title: '编号'
			},
			{
				field: 'type',
				title: '耗材类型编号',
				visible:false
			},
			{
				field: 'typeName',
				title: '类型'
			},
			{
				field: 'name',
				title: '名称'
			},
			{
				field: 'volume',
				title: '体积（立方厘米）'
			},
			{
				field: 'weight',
				title: '重量（公斤）'
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
				field: 'op',
				title: '操作',
				formatter : function (value, row, index) {
					return '<a class="mdinfo" href="javascript:void(0)" ><i class="glyphicon glyphicon-pencil"></i>&nbsp;修改&nbsp;</a>';
				},
				events: {
					'click .mdinfo': function(e, value, row, index) {
						layer.open({
							id:'modifyInfoFrame',
							type: 2,
							title:'耗材信息修改',
							shadeClose: true,
							shade: 0.7,
							shadeClose: false,
							maxmin: true,
							area: ['800px', '420px'],
							content: modifyInfoPageUrl,
							success: function(layero, index){
								var id = row.id;
								var code = row.code;
								var name = row.name;
								var type = row.type;
								var typeName = row.typeName;
								var volume = row.volume;
								var volumeCoefficient = row.volumeCoefficient;
								var specification = row.specification;
								var unit = row.unit;

								var frameId = document.getElementById("modifyInfoFrame").getElementsByTagName("iframe")[0].id;
								var frameWindow = $('#' + frameId)[0].contentWindow;
								frameWindow.$('#id-value-input').val(id);
								frameWindow.$('#code-value-input').val(code);
								frameWindow.$('#name-value-input').val(name);
								frameWindow.$.combobox.setValues('type-select', type);
								// if (type != "TY001" || type == "TY002" || type == "TY007") {
								// 	frameWindow.$('#with-value-input').prop("readonly", false);
								// 	frameWindow.$('#length-value-input').prop("readonly", false);
								// 	frameWindow.$('#height-value-input').prop("readonly", false);
								// 	frameWindow.$('#volume-coefficient-value-input').prop("readonly", false);
								// }
								frameWindow.$('#type-value-input').val(type);
								frameWindow.$('#type-name-value-input').val(typeName);
								frameWindow.$('#volume-value-input').val(volume);
								frameWindow.$('#volume-coefficient-value-input').val(volumeCoefficient);
								frameWindow.$('#specification-value-input').val(specification);
								frameWindow.$('#unit-value-input').val(unit);
								if (specification != null && specification != "") {
									var dataArr = specification.split("*");
									if (dataArr.length == 3) {
										frameWindow.$('#length-value-input').val(dataArr[0]);
										frameWindow.$('#with-value-input').val(dataArr[1]);
										frameWindow.$('#height-value-input').val(dataArr[2]);
									}
								}
							}
						});
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
				tableInit().refresh();
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
		layer.open({
			id:'addInfoFrame',
			type: 2,
			title:'增加包装耗材信息',
			shadeClose: true,
			shade: 0.7,
			maxmin: true,
			shadeClose: false,
			area: ['800px', '420px'],
			content: addInfoPageUrl
		});

	});
});