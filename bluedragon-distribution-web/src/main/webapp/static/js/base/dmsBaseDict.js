$(function() {
	var saveUrl = '/base/dmsBaseDict/save';
	var deleteUrl = '/base/dmsBaseDict/deleteByIds';
    var detailUrl = '/base/dmsBaseDict/detail/';
    var queryUrl = '/base/dmsBaseDict/listData';
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
		oTableInit.tableColums = [ {
				checkbox : true
			}, {
				field : 'typeCode',
				title : '编码'
			}, {
				field : 'typeName',
				title : '名称'
			}, {
				field : 'nodeLevel',
				title : '层级'
			}, {
				field : 'parentId',
				title : '上级Id'
			}, {
				field : 'typeGroup',
				title : '分组'
			}, {
				field : 'memo',
				title : '描述'
			} ];
		oTableInit.refresh = function() {
			$('#dataTable').bootstrapTable('refresh');
		};
		return oTableInit;
	};
	var pageInit = function() {
		var oInit = new Object();
		oInit.init = function() {
			$('#dataEditDiv').hide();
			$('#query-form #nodeLevel').change(
					function() {
						var nodeLevel = $(this).val();
						if (nodeLevel == null || nodeLevel == '') {
							nodeLevel = 0;
						}
						var url = "/base/dmsBaseDict/getDicListByNodeLevel/" + (nodeLevel - 1);
						var param = {};
						$.get(url, param, function(myData) {
							var data = myData.data;
							var result = [];
							for ( var i in data) {
								if (data[i].id && data[i].typeName) {
									result.push({
										id : data[i].id,
										text : data[i].typeCode + '-'
												+ data[i].typeName
									});
								}
							}
							$("#query-form #parentId").html("");
							$('#query-form #parentId').select2({
								width : '240px',
								placeholder : '请选择上级节点',
								allowClear : true,
								data : result
							});
							$('#query-form #parentId').val(null).trigger(
									'change');
						});
					});
			$('#edit-form #nodeLevel').change(function() {
				var nodeLevel = $(this).val();
				if(nodeLevel != null && nodeLevel != ''){
					var url = "/base/dmsBaseDict/getDicListByNodeLevel/"+(nodeLevel-1);
				    var param = {};
				    $.get(url, param, function (myData) {
				        var data = myData.data;
				        var result = [];
				        for(var i in data){
				            if(data[i].id && data[i].typeName){
				                result.push({id:data[i].id,text:data[i].typeCode+'-'+data[i].typeName});
				            }
				        }
				        $("#edit-form #parentId").html("");
				        $('#edit-form #parentId').select2({
				            width: '300px',
				            placeholder:'请选择上级节点',
				            allowClear:true,
				            data:result
				        });
				        $('#edit-form #parentId').val(null).trigger('change');
				    });
				}
	        });			
		    $('#btn_query').click(function() {
		    	tableInit().refresh();
			});
			$('#btn_add').click(function() {
			    $('.edit-param').each(function () {
			    	var _k = this.id;
			        if(_k){
			        	$(this).val('');
			        }
			    });
			    $('#edit-form #typeGroup').val(null).trigger('change');
			    $('#edit-form #parentId').val(null).trigger('change');
				$('#dataTableDiv').hide();
				$('#dataEditDiv').show();
			});
			// 修改操作
			$('#btn_edit').click(function() {
				var rows = $('#dataTable').bootstrapTable('getSelections');
				if (rows.length > 1) {
					alert("修改操作，只能选择一条数据");
					return;
				}
				if (rows.length == 0) {
					alert("请选择一条数据");
					return;
				}
			    $.ajaxHelper.doPostSync(detailUrl+rows[0].id,null,function(res){
			    	if(res&&res.succeed&&res.data){
					    $('.edit-param').each(function () {
					    	var _k = this.id;
					        var _v = res.data[_k];
					        if(_k){
					        	if(_v != null && _v != undefined){
						        	$(this).val(_v);
						        	if(_k=='typeGroup'||_k=='parentId'){
						        		$(this).trigger('change');
			                        }
						        	if(_k=='nodeLevel'){
						        		var parentId = res.data['parentId'];
							    		initEditParentIdSelect(_v,parentId);
						        	}
						        }else{
						        	$(this).val('');
						        }
					        } 
					    });
			    	}
			    });
				$('#dataTableDiv').hide();
				$('#dataEditDiv').show();
			});

			// 删
			$('#btn_delete').click(function() {
				var rows = $('#dataTable').bootstrapTable('getSelections');
				if (rows.length < 1) {
					alert("错误，未选中数据");
					return;
				}
				var flag = confirm("是否删除这些数据?");
				if (flag == true) {
					var params = [];
					for(var i in rows){
						params.push(rows[i].id);
				    };
					$.ajaxHelper.doPostSync(deleteUrl,JSON.stringify(params),function(res){
				    	if(res&&res.succeed&&res.data){
				    		alert('操作成功,删除'+res.data+'条。');
				    		tableInit().refresh();
				    	}else{
				    		alert('操作异常！');
				    	}
				    });
				}
			});
			$('#btn_submit').click(function() {
				var params = {};
				if($('#edit-form #nodeLevel').val()==null){
					alert('请选择层级');
					return;
				}
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
			initTypeGroupsSelect();
			initParentIdsSelect();
			//初始化分组下拉框
			function initTypeGroupsSelect() {
			    var url = "/base/dmsBaseDict/getAllDicGroups";
			    var param = {};
			    $.get(url, param, function (myData) {
			        var data = myData.data;
			        var result = [];
			        for(var i in data){
			            if(data[i].id && data[i].typeName){
			                result.push({id:data[i].id,text:data[i].typeCode+'-'+data[i].typeName});
			            }
			        }
			        $('#query-form #typeGroup').select2({
			            width: '240px',
			            placeholder:'请选择分组',
			            allowClear:true,
			            data:result
			        });

			        $('#edit-form #typeGroup').select2({
			            width: '300px',
			            placeholder:'请选择分组',
			            allowClear:true,
			            data:result
			        });
			        $('#query-form #typeGroup').val(null).trigger('change');
			        $('#edit-form #typeGroup').val(null).trigger('change');
			    }, "json");
			}
			//初始化分组下拉框
			function initParentIdsSelect() {
				var url = "/base/dmsBaseDict/getDicListByNodeLevel/-1";
			    var param = {};
			    $.get(url, param, function (myData) {
			        var data = myData.data;
			        var result = [];
			        for(var i in data){
			            if(data[i].id && data[i].typeName){
			                result.push({id:data[i].id,text:data[i].typeCode+'-'+data[i].typeName});
			            }
			        }
			        $('#query-form #parentId').select2({
			            width: '240px',
			            placeholder:'请选择上级节点',
			            allowClear:true,
			            data:result
			        });

			        $('#edit-form #parentId').select2({
			            width: '300px',
			            placeholder:'请选择上级节点',
			            allowClear:true,
			            data:result
			        });
			        $('#query-form #parentId').val(null).trigger('change');
			        $('#edit-form #parentId').val(null).trigger('change');
			    }, "json");
			}
			//初始化分组下拉框
			function initEditParentIdSelect(nodeLevel,parentId) {
				if(nodeLevel != null && nodeLevel != ''){
					var url = "/base/dmsBaseDict/getDicListByNodeLevel/"+(nodeLevel-1);
				    var param = {};
				    $.get(url, param, function (myData) {
				        var data = myData.data;
				        var result = [];
				        for(var i in data){
				            if(data[i].id && data[i].typeName){
				                result.push({id:data[i].id,text:data[i].typeCode+'-'+data[i].typeName});
				            }
				        }
				        $("#edit-form #parentId").html("");
				        $('#edit-form #parentId').select2({
				            width: '300px',
				            placeholder:'请选择上级节点',
				            allowClear:true,
				            data:result
				        });
				        $('#edit-form #parentId').val(parentId).trigger('change');
				    });
				}
			}			
		};
		return oInit;
	};
	
	tableInit().init();
	pageInit().init();
});