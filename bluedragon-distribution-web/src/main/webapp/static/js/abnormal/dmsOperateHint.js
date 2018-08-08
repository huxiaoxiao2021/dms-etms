$(function() {
	var saveUrl = '/abnormal/dmsOperateHint/save';
	var batchsaveUrl = '/abnormal/dmsOperateHint/batchsave';
	var detailUrl = '/abnormal/dmsOperateHint/detail/';
	var queryUrl = '/abnormal/dmsOperateHint/listData';
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
			checkbox : true
		}, {
			field : 'waybillCode',
			title : '运单号'
		} ,{
            field : 'dmsSiteName',
            title : '操作机构名称'
        },{
            field : 'hintName',
            title : '提示类型'
        },{
            field : 'hintMessage',
            title : '提示语'
        },{
            field : 'isEnable',
            title : '启用状态',
            formatter : function(value,row,index){
                return value==1?'启用提示':'关闭提示';
            }
        },{
			field : 'updateUser',
			title : '操作人ERP'
		},{
            field : 'updateTime',
            title : '操作时间',
            formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            },
            width:120,
            class:'min_120'
        },{
			field : 'hintDmsName',
			title : '显示提示分拣中心'
		},{
			field : 'hintOperateNode',
			title : '显示发货环节'
		},{
			field : 'hintTime',
			title : '显示提示时间',
			formatter : function(value,row,index){
				return $.dateHelper.formateDateTimeOfTs(value);
			},
			width:120,
			class:'min_120'}];
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
            $('#dataEditDiv').hide();
			/*起始时间*/ /*截止时间*/
			$.datePicker.createNew({
                elem: '#startTime',
                theme: '#3f92ea',
                type: 'datetime',
                min: -60,//最近60天内
                max: 0,//最近60天内
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/
                }
			});
			$.datePicker.createNew({
                elem: '#endTime',
                theme: '#3f92ea',
                type: 'datetime',
                min: -60,//最近60天内
                max: 0,//最近60天内
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/
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
                $("#edit-form #waybillCode").prop('disabled', '');
                $('#enable').prop('checked','checked');
				$('#dataTableDiv').hide();
				$('#dataEditDiv').show();
				$("#edit-form").data("bootstrapValidator").resetForm();
                $("#edit-form #waybillCode").val(null);
            });
			// 初始化页面上面的按钮事件
			// 改
			$('#btn_edit').click(function() {
				var rows = $('#dataTable').bootstrapTable('getSelections');
				if (rows.length > 1) {
					Jd.alert("修改操作，只能选择一条数据");
					return;
				}
				if (rows.length == 0) {
					Jd.alert("请选择一条数据");
					return;
				}
				$('.edit-param').each(function () {
					var _k = this.id;
					if(_k){
						$(this).val('');
					}
				});
				$("#edit-form").data("bootstrapValidator").resetForm();
				$.ajaxHelper.doPostSync(detailUrl+rows[0].id,null,function(res){
					if(res&&res.succeed&&res.data){
						$('.edit-param').each(function () {
							var _k = this.id;
							var _v = res.data[_k];
							if(_k && _v){
                                $(this).val(_v);
							}
                            if(_k && _k=='isEnable'){
                                if(_v == 1){
                                    $('#enable').prop('checked','checked');
                                }else{
                                    $('#unable').prop('checked','checked');
                                }
                            }
						});
                        if(res.data['hintCode'] && res.data['hintCode'] == 4){
                            $("#edit-form #hintMark").show();
                            $("#edit-form #hintMark").val($("#edit-form #hintMessage").val());
                            $("#edit-form #hintMessage").hide();
                            $("#edit-form #hintMessage").val('');
                        }
                        $("#edit-form #waybillCode").prop('disabled', 'disabled');
						var v1 = $('#edit-form #hintCode').val();

						if(v1){
							$('#excpTypeEdit').val(v1).trigger('change');
                            changeExcpTypeEdit();
						}
					}
				});
				$('#dataTableDiv').hide();
				$('#dataEditDiv').show();
			});

			$('#btn_submit').click(function() {
				$('#btn_submit').attr("disabled",true);
				//先去校验
				if(!editValidator()){
					$('#btn_submit').attr("disabled",false);
					return;
				}

				var params = {};
                $('#isEnable').val($('input:radio:checked').val());
                $('.edit-param').each(function () {
                    var _k = this.id;
                    var _v = $(this).val();
                    if(_k && _v){
                        if(_k == 'hintMark'){
                            params['hintMessage']=_v;
                        }else{
                            params[_k]=_v;
                        }
                    }
                });
                var v_waybillCode = $('#edit-form #waybillCode').val();
                var url = saveUrl;
                if(v_waybillCode.indexOf("\n") >= 0){
                    url = batchsaveUrl;
                }
                $.ajaxHelper.doPostSync(url,JSON.stringify(params),function(res){
                    if(res&&res.succeed){
                        Jd.alert('操作成功');
                        tableInit().refresh();
                    }else if(res){
                        Jd.alert(res.message);
                    }else{
                        Jd.alert('服务异常');
                    }
                    $('#btn_submit').attr("disabled",false);
                    //还原异常信息下拉框
                    $('#excpTypeEdit').val(null).trigger('change');
                    $('#dataEditDiv').hide();
                    $('#dataTableDiv').show();

                });
			});
			$('#btn_return').click(function() {
				//还原异常信息下拉框
				$('#excpTypeEdit').val(null).trigger('change');
                $("#edit-form #hintMark").val('');
				$('#dataEditDiv').hide();
				$('#dataTableDiv').show();
			});
		};
		return oInit;
	};
	initDateQuery();
	tableInit().init();
	pageInit().init();
	initExcpType();
	editFormValidator();
	initEditSelect();
});

var excpTypeData={};

// 初始化异常类型下拉框
function initExcpType() {
    var url = "/base/dmsBaseDict/selectAndEnum/10002";
    var param = {};
    $.get(url, param, function (myData) {
        var data = myData.data;

        var result = [];
        for(var i in data){
            if(data[i].typeCode && data[i].typeName){
                result.push({id:data[i].typeCode,text:data[i].typeName});

                excpTypeData[data[i].typeCode] = data[i]; //缓存一份
            }

        }

        $('#excpTypeEdit').select2({
            width: '100%',
            placeholder:'请选择提示语类型',
            allowClear:true,
            data:result
        });

        $('#excpTypeEdit').val(null).trigger('change');
    }, "json");
}

function initEditSelect(){

	//监听异常类型发生变化
	$("#excpTypeEdit").on('change',function(e){
		changeExcpTypeEdit();
	});
}

/**
 * 异常类型 发生变化 处理逻辑
 * @returns {boolean}
 */
function changeExcpTypeEdit(){

    var p = $("#excpTypeEdit").val();
    var _p = $("#edit-form #excpType").val();
    if(!p || p ==_p){ //防止 空值 或 修改的时候反选触发change事件 在多走一遍
        return false;
    }
    var k = excpTypeData[p]['typeCode'];
    var v = excpTypeData[p]['typeName'];
    if(k){
        $("#edit-form #hintCode").val(k);
        $("#edit-form #hintName").val(v);
        if(k == 4){//其他选项，备注框可用
            $("#edit-form #hintMessage").hide();
            $("#edit-form #hintMark").show();
            $("#edit-form #hintMessage").val('');
        }else{//普通选项，设置提示语
            $("#edit-form #hintMessage").show();
            $("#edit-form #hintMark").hide();
            $("#edit-form #hintMark").val('');
            $("#edit-form #hintMessage").val(v);
        }
    }
}

function initDateQuery(){
    var startTime = $.dateHelper.formatDateTime(new Date(new Date().toLocaleDateString()));
    var endTime = $.dateHelper.formatDateTime(new Date(new Date(new Date().toLocaleDateString()).getTime()+24*60*60*1000-1));
    $("#startTime").val(startTime);
    $("#endTime").val(endTime);
}

