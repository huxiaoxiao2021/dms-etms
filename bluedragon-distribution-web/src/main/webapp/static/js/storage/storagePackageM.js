$(function() {
	var saveUrl = '/storage/storagePackageM/save';
	var deleteUrl = '/storage/storagePackageM/deleteByIds';
  	var forceSendUrl = '/storage/storagePackageM/forceSend';
  	var queryUrl = '/storage/storagePackageM/listData';


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
				field : 'performanceCode',
				title : '履约单号'
			}, {
				field : 'waybillCode',
				title : '运单号'
			}, {
				field : 'packageSum',
				title : '系统包裹数'
			}, {
				field : 'putawayPackageSum',
				title : '上架包裹数',
				formatter : function(value,row,index){
					return '<a href="#" class="show-storage-view" onclick="showView(\''+row.waybillCode+'\',event)">'+value+'</a>';
				}
			}, {
				field : 'storageCode',
				title : '储位号'
			}, {
				field : 'status',
				title : '暂存状态',
				formatter : function(value,row,index){
					return value=="1"?"已上架":value=="2"?"可发货":value=="3"?"强制可发货":value=="4"?"已发货":"未知状态";
				}
			}, {
				field : 'planDeliveryTime',
				title : '预计送达时间',
            	formatter : function(value,row,index){
					return $.dateHelper.formateDateOfTs(value);
					}
			}, {
				field : 'updateTime',
				title : '上架时间',
				formatter : function(value,row,index){
					return $.dateHelper.formateDateTimeOfTs(value);
				}
			}, {
				field : 'updateUser',
				title : '上架人erp'
			}, {
				field : 'createSiteName',
				title : '所属分拣中心'
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
		    $('#btn_query').click(function() {
		    	tableInit().refresh();
			});

            $.datePicker.createNew({
                elem: '#putawayDateLEStr',
                theme: '#3f92ea',
                type: 'datetime',
                //btns: ['clear','now'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });
            $.datePicker.createNew({
                elem: '#putawayDateGEStr',
                theme: '#3f92ea',
                type: 'datetime',
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });

            $.datePicker.createNew({
                elem: '#planStartDateLE',
                theme: '#3f92ea',
                btns: ['clear','now'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/
                }
            });


			// 强制发货
			$('#btn_force_send').click(function() {
				debugger;
				var rows = $('#dataTable').bootstrapTable('getSelections');
				var ids = [];
				if (rows.length == 0) {
					alert("请选择一条数据");
					return;
				}
                $.each( rows, function( index, val ) {
                    ids.push(this.performanceCode);
                } );

                $.unique(ids);
                var confirmStr = "是否对履约单:\n";
                $.each( ids, function( index, val ) {
                    confirmStr += this+"\n"
                } );
				if(confirm(confirmStr+"强制发货？")){
                    $.ajaxHelper.doPostSync(forceSendUrl,JSON.stringify(ids),function(res){
                        if(res&&res.succeed&&res.data){
                            alert("强制发货成功");
                            tableInit().refresh();
                        }else{
                            alert(res.message);
						}
                    });
				}

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


});

function showView(waybillCode,event){
	//获取暂存上架明细
    var queryStorageDUrl = '/storage/storagePackageD/showViews/';


    $("#storageDTbody").html("<tr><td colspan='6'>努力加载中...</td></tr>");

    $('#viewModal').modal("show");


	$.ajaxHelper.doGetSync(queryStorageDUrl+waybillCode,{},function(data){

		var tbodyHtml = "";
		if(data.code == 200){
			if(data.data.length == 0){
                tbodyHtml = "<tr><td colspan='6'>未获取到明细数据</td></tr>";
			}

            for(var i = 0 ; i<data.data.length ; i++ ){
                var storagePackageD = data.data[i];
                tbodyHtml += "<tr><td>"+storagePackageD.performanceCode+"</td>";
                tbodyHtml += "<td>"+storagePackageD.waybillCode+"</td>";
                tbodyHtml += "<td>"+storagePackageD.packageCode+"</td>";
                tbodyHtml += "<td>"+storagePackageD.storageCode+"</td>";
                tbodyHtml += "<td>"+storagePackageD.createUser+"</td>";
                tbodyHtml += "<td>"+$.dateHelper.formateDateTimeOfTs(storagePackageD.putawayTime)+"</td></tr>";

            }
		}else{
            tbodyHtml = "<tr><td colspan='6'>"+data.message+"</td></tr>";
		}

		$("#storageDTbody").html(tbodyHtml);



	});

    event.stopPropagation();

}