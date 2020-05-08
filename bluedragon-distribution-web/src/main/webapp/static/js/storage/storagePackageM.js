$(function() {
	var saveUrl = '/storage/storagePackageM/save';
	var deleteUrl = '/storage/storagePackageM/deleteByIds';
  	var forceSendUrl = '/storage/storagePackageM/forceSend';
  	var queryUrl = '/storage/storagePackageM/listData';
	var cancelUrl = '/storage/storagePackageM/cancelPutaway'; //取消上架
	var exportUrl = '/storage/storagePackageM/toExport';

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
                field : 'source',
                title : '来源',
                align: 'center',
                formatter : function(value,row,index){
                    return value=="1"?"金鹏暂存":value=="2"?"快运暂存":"";
                }
            }, {
				field : 'performanceCode',
				title : '履约单号',
                align: 'center'
			}, {
				field : 'waybillCode',
				title : '运单号',
                align: 'center'
			}, {
				field : 'packageSum',
				title : '系统包裹数',
                align: 'center'
			}, {
				field : 'putawayPackageSum',
				title : '上架包裹数',
                align: 'center',
				formatter : function(value,row,index){
					return '<a href="#" class="show-storage-view" onclick="showView(\''+row.waybillCode+'\',event)">'+value+'</a>';
				}
			}, {
				field : 'storageCode',
				title : '储位号',
                align: 'center'
			}, {
				field : 'status',
				title : '暂存状态',
                align: 'center',
				formatter : function(value,row,index){
					return value=="1"?"已上架":value=="2"?"可发货":value=="3"?"强制可发货":value=="4"?"已发货":"未知状态";
				}
			}, {
				field : 'planDeliveryTime',
				title : '预计送达时间',
                align: 'center',
            	formatter : function(value,row,index){
					return $.dateHelper.formateDateOfTs(value);
					}
			}, {
				field : 'updateTime',
				title : '上架时间',
                align: 'center',
				formatter : function(value,row,index){
					return $.dateHelper.formateDateTimeOfTs(value);
				}
			}, {
				field : 'updateUser',
				title : '上架人erp',
                align: 'center'
			}, {
				field : 'createSiteName',
				title : '所属分拣中心',
                align: 'center'
			}, {
                field : 'putAwayCompleteTime',
                title : '全部上架完成时间',
                align: 'center',
                formatter : function(value,row,index){
                    return $.dateHelper.formateDateTimeOfTs(value);
                }
            }, {
                field : 'downAwayCompleteTime',
                title : '全部下架完成时间',
                align: 'center',
                formatter : function(value,row,index){
                    return $.dateHelper.formateDateTimeOfTs(value);
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
			$('#dataEditDiv').hide();		
		    $('#btn_query').click(function() {
		    	tableInit().refresh();
			});

            $.datePicker.createNew({
                elem: '#putawayDateLEStr',
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
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });
            $.datePicker.createNew({
                elem: '#planDeliveryTimeGEStr',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                }
            });
            $.datePicker.createNew({
                elem: '#planDeliveryTimeLEStr',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                }
            });


			// 强制发货
			$('#btn_force_send').click(function() {

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

			//取消上架功能
            $('#btn_cancel').click(function() {
                var rows = $('#dataTable').bootstrapTable('getSelections');
                if (rows.length < 1) {
                    alert("错误，未选中数据");
                    return;
                }
                var flag = confirm("是否取消上架这些数据?");
                if (flag == true) {
                    var params = [];
                    for(var i in rows){
                        params.push(rows[i].id);
                    };
                    $.ajaxHelper.doPostSync(cancelUrl,JSON.stringify(params),function(res){
                        if(res&&res.succeed && res.data){
                            alert('操作成功');
                            tableInit().refresh();
                        }else{
                            alert('操作异常！');
                        }
                    });
                }
            });
            $('#btn_send_status').click(function() {
				$('#sendStatusModal').modal('show');
			});
            $('#btn_query_waybill').click(function() {
                $('#queryWaybillModal').modal('show');
            });
            $('#sendStatusModalBtn').click(function() {
                //同步发货状态按钮
				var waybillCode = $("#sendStatusModalWaybillCode").val();
				if(waybillCode == null ||waybillCode == ""){
					alert("请输入运单号");
				}
                var url = "/storage/storagePackageM/refreshSendStatus/"+waybillCode;
                $.post(url,function(data){
                	if(data.data){
                        alert("同步成功!");
					}else{
                        alert(data.message);
					}
                    $("#sendStatusModalWaybillCode").val("");
                	$("#btn_query").click();
                },"json");

                return false;
            });

            $('#queryWaybillModalBtn').click(function() {
                var waybillCode = $("#queryWaybillModalWaybillCode").val();

                if(waybillCode == null || waybillCode == "") {
                	alert("运单号必须输入");
				}

                $("#queryWaybillModalTbody").html("");
                //查询履约单下运单信息
                var url = "/storage/storagePackageM/queryWaybills/"+waybillCode;
				$.post(url,function(data){
					if(data.code == 200){
						if(data.data==null || data.data.length == 0){
							alert("未获取到相应数据");
						}else{
                            var tbodyHtml = "";
                            for(var i = 0 ; i<data.data.length ; i++ ){
                                var pojo = data.data[i];
                                tbodyHtml += "<tr><td>"+pojo.fulfillmentOrderId+"</td>";
                                tbodyHtml += "<td>"+pojo.deliveryOrderId+"</td></tr>";
                            }
                            $("#queryWaybillModalTbody").html(tbodyHtml);
						}

					}else{
						alert(data.message);
					}

				},"json");

                return false;
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

			// 导出
            $('#btn_export').click(function () {
                debugger;
                var params = tableInit().getSearchCondition();
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

            // 储位充足变更
            $('#btn_edit').click(function() {
                $('#editStorageCap').modal('show');
            });
            // 储位充足变更保存
            $('#storageIsEnoughSave').click(function() {
                debugger;
                var isEnough;
                if($('#storageIsEnough').attr("checked")){
                    isEnough = 1;
                }else {
                    isEnough = 0;
                }
                var url = "/storage/storagePackageM/updateStorageStatusBySiteCode/"+$('#loginUserCreateSiteCode').val() + "/" + isEnough;
                $.post(url,function(data){
                    if(data.data){
                        Jd.alert("保存成功");
                        $('#editStorageCap').modal('hide');
                    }else{
                        Jd.alert("保存失败");
                    }
                },"json");
            });

			$('#btn_return').click(function() {
				$('#dataEditDiv').hide();
				$('#dataTableDiv').show();
			});		
		};
		return oInit;
	};

    initOrg();
    initDateQuery();
	tableInit().init();
	pageInit().init();
    initStorageIsEnough();


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

//初始化储位是否充足
function initStorageIsEnough(){
    var url = "/storage/storagePackageM/getStorageStatusBySiteCode/"+$('#loginUserCreateSiteCode').val();
    $.post(url,function(data){
        if(data.data){
            $('#storageIsEnough').attr("checked","checked");
            $('#btn_edit').css("color","blue");
            $('#btn_edit').text('储位充足客户可下单');
        }else{
            $('#storageIsEmpty').attr("checked","checked");
            $('#btn_edit').css("color","red");
            $('#btn_edit').text('储位已满停止客户下单');
        }
    },"json");

}

function initDateQuery(){
    var v = $.dateHelper.formatDate(new Date());

    $("#putawayDateGEStr").val(v+" 00:00:00");
    $("#putawayDateLEStr").val(v+" 23:59:59");
    $("#planDeliveryTimeGEStr").val(v+" 00:00:00");
    $("#planDeliveryTimeLEStr").val(v+" 23:59:59");
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
