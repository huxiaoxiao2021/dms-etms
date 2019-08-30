$(function() {
	var saveUrl = '/collect/collectGoodsPlace/save';
    var updateUrl = '/collect/collectGoodsPlace/update';
    var saveTypeUrl = '/collect/collectGoodsPlace/saveType';
	var deleteUrl = '/collect/collectGoodsPlace/deleteByIds';
    var deleteAreaUrl = '/collect/collectGoodsArea/deleteByCodes';
  	var detailUrl = '/collect/collectGoodsPlace/detail/';
  	var queryUrl = '/collect/collectGoodsPlace/listData';
    var checkAreaCodeUrl = '/collect/collectGoodsArea/check';
    var findPlaceTypeUrl = '/collect/collectGoodsPlaceType/find/';
  	var collectPlaceArray = new Array();

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
				field : 'createSiteName',
				title : '场地'
			}, {
				field : 'collectGoodsAreaCode',
				title : '集货区'
			}, {
				field : 'collectGoodsPlaceCode',
				title : '集货位'
			}, {
				field : 'collectGoodsPlaceType',
				title : '货位类型',
				formatter : function(value,row,index){
					return value=="1"?"小单":value=="2"?"中单":value=="3"?"大单":value=="4"?"异常":"未知类型";
				}
			}, {
				field : 'collectGoodsPlaceStatus',
				title : '状态',
				formatter : function(value,row,index){
					return value=="0"?"空闲":value=="1"?"非空闲":value=="2"?"已满":"未知状态";
				}
			} , {
				field : 'createTime',
				title : '创建时间',
			    formatter : function(value,row,index){
                return $.dateHelper.formateDateTimeOfTs(value);
            }
			}, {
				field : 'createUser',
				title : '创建人'
			}, {
				field : 'updateTime',
				title : '修改时间',
				formatter : function(value,row,index){
					return $.dateHelper.formateDateTimeOfTs(value);
				}
			}, {
				field : 'updateUser',
				title : '修改人'
			}];
		oTableInit.refresh = function() {
			$('#dataTable').bootstrapTable('refresh');
		};
		return oTableInit;
	};
	var pageInit = function() {
		var oInit = new Object();
		oInit.init = function() {

            $.datePicker.createNew({
                elem: '#createTimeGEStr',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/


                }
            });
            $.datePicker.createNew({
                elem: '#createTimeLEStr',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });
            $.datePicker.createNew({
                elem: '#updateTimeGEStr',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });
            $.datePicker.createNew({
                elem: '#updateTimeLEStr',
                theme: '#3f92ea',
                type: 'datetime',
                btns: ['now', 'confirm'],
                done: function(value, date, endDate){
                    /*重置表单验证状态*/

                }
            });


			$('#dataEditDiv').hide();		
		    $('#btn_query').click(function() {
		    	tableInit().refresh();
			});
			$('#btn_add').click(function() {

                $.ajaxHelper.doPostSync(findPlaceTypeUrl+$("#loginUserCreateSiteCode").val(),"",function(res){
                    if(res&&res.succeed){
                        var smallMax = 10;
                        var middleMin = 11;
                        var middleMax = 100
                        var bigMin = 101;
                        for(var pojo in res.data){
                            if(res.data[pojo]['collectGoodsPlaceType'] == 1){
                                smallMax = res.data[pojo]['maxPackNum'];

                            }else if(res.data[pojo]['collectGoodsPlaceType'] == 2){
                                middleMin = res.data[pojo]['minPackNum'];
                                middleMax = res.data[pojo]['maxPackNum'];

                            }else if(res.data[pojo]['collectGoodsPlaceType'] == 3){
                                bigMin = res.data[pojo]['minPackNum'];

                            }
                        }

                        $("#add-form-title").html("当前场站："+$("#loginUserCreateSiteName").val()+" 包裹数量范围（件）：小单≤"+smallMax+" 中单"+
                            middleMin+"～"+middleMax+" 大单≥"+bigMin);
                    }
                });

			    $('.edit-param').each(function () {
			    	var _k = this.id;
			        if(_k){
			        	$(this).val('');
			        }
			    });
                collectPlaceArray = new Array();
                $("#place-add-table").html("");
                $("#collect-goods-area").attr("readonly",false);
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
                if (rows[0].collectGoodsPlaceType == 4) {
                    alert("不允许修改异常类型的集货位");
                    return;
                }
                if (rows[0].collectGoodsPlaceStatus != 0) {
                    alert("不允许修改非空闲的集货位");
                    return;
                }

			    $.ajaxHelper.doPostSync(detailUrl+rows[0].id,null,function(res){
			    	if(res&&res.succeed&&res.data){
					    $('#place-change .edit-param').each(function () {
					    	var _k = this.name;
					        var _v = res.data[_k];
					        if(_k){
					        	if(_v != null && _v != undefined){
						        	$(this).val(_v);
						        }else{
						        	$(this).val('');
						        }
					        } 
					    });
			    	}
			    });
                $("#place-change").modal('show');
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
                        if (rows[i].collectGoodsPlaceType == 4) {
                            alert("不允许删除异常类型的集货位");
                            return;
                        }
                        if (rows[i].collectGoodsPlaceStatus != 0) {
                            alert("不允许删除非空闲的集货位");
                            return;
                        }
				    };
					$.ajaxHelper.doPostSync(deleteUrl,JSON.stringify(params),function(res){
				    	if(res&&res.succeed&&res.data){
				    		alert('操作成功,删除'+res.data+'条。');
				    		tableInit().refresh();
				    	}else{
				    		alert(res.message);
				    	}
				    });
				}
			});

            // 删
            $('#btn_area_delete').click(function() {
                var rows = $('#dataTable').bootstrapTable('getSelections');
                if (rows.length < 1) {
                    alert("错误，未选中数据");
                    return;
                }
                var params = [];
                var confirmMsg = "是否删除这些数据? 集货区:";
                for(var i in rows){
                    if(params.indexOf(rows[i].collectGoodsAreaCode)==-1){
                        params.push(rows[i].collectGoodsAreaCode);
                        confirmMsg += rows[i].collectGoodsAreaCode+" ";
                    }

                    if (rows[i].collectGoodsPlaceStatus != 0) {
                        alert("不允许删除非空闲的集货位");
                        return;
                    }
                };

                var flag = confirm(confirmMsg);
                if (flag == true) {
                    $.ajaxHelper.doPostSync(deleteAreaUrl,JSON.stringify(params),function(res){
                        if(res&&res.succeed&&res.data){
                            alert('操作成功!');
                            tableInit().refresh();
                        }else{
                            alert(res.message);
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
                        alert(res.message);
			    	}
			    });
				$('#dataEditDiv').hide();
				$('#dataTableDiv').show();
			});
            $('#btn_place_type_change').click(function() {

                $("#place-type-change-site-name").html("当前场站："+$("#loginUserCreateSiteName").val());

            	//获取集货位类型基础数据
                $.ajaxHelper.doPostSync(findPlaceTypeUrl+$("#loginUserCreateSiteCode").val(),"",function(res){
                    if(res&&res.succeed){
                       for(var pojo in res.data){
                           if(res.data[pojo]['collectGoodsPlaceType'] == 1){
                           	   $("#small-pack-max-num").val(res.data[pojo]['maxPackNum']);
                               $("#small-waybill-max-num").val(res.data[pojo]['maxWaybillNum']);
						   }else if(res.data[pojo]['collectGoodsPlaceType'] == 2){
                               $("#middle-pack-min-num").html(res.data[pojo]['minPackNum']+"个");
                               $("#middle-pack-max-num").val(res.data[pojo]['maxPackNum']);
                               $("#middle-waybill-max-num").val(res.data[pojo]['maxWaybillNum']);
                           }else if(res.data[pojo]['collectGoodsPlaceType'] == 3){
                               $("#big-pack-min-num").html(res.data[pojo]['minPackNum']+"个");
                               $("#big-waybill-max-num").val(res.data[pojo]['maxWaybillNum']);
                           }
					   }
                    }
                });
                $('#place-type-change').modal('show');
            });

            /**
			 * 绘制集货位表格
             */
            $('#btn_add_place').click(function() {

				if(checkPlaceAdd()){
                    $("#collect-goods-area").attr("readonly","readonly");
                    var collectGoodsArea = $("#collect-goods-area").val();
                    var collectGoodsPlaceNum = $("#collect-goods-place-num").val();
					var collectGoodsPlaceType = $("#collect-goods-place-type").val();
					var collectGoodsPlaceTypeName = collectGoodsPlaceType==1?"小单":collectGoodsPlaceType==2?"中单":"大单";
                    var tableHtml = "";
                    var isAppend =  collectPlaceArray.length > 0;
                    var initEndCodeIndex = collectPlaceArray.length;

					for(var i = 1 ; i<= collectGoodsPlaceNum;i++){


						var endCodeIndex = i+initEndCodeIndex;
						var collectGoodsPlaceCode = collectGoodsArea+
							(endCodeIndex<10?"00"+endCodeIndex:endCodeIndex<100?"0"+endCodeIndex:endCodeIndex);
						tableHtml += "<tr><td>"+collectGoodsArea+"</td><td>"+collectGoodsPlaceTypeName+"</td><td>"+collectGoodsPlaceCode+"</td></tr>";
                        var o = {};
                        o['collectGoodsPlaceCode'] = collectGoodsPlaceCode;
                        o['collectGoodsAreaCode'] = collectGoodsArea;
                        o['collectGoodsPlaceType'] = collectGoodsPlaceType;
						collectPlaceArray.push(o);
					}
					if(isAppend){
                        $("#place-add-table").append(tableHtml);
					}else{
                        $("#place-add-table").html(tableHtml);
					}


				}

            });


            $("#collect-goods-area").change(function(){
                if(checkAreaCodeExist($("#collect-goods-area").val())){
                    alert("集货区编码已存在");
                    return;
                }
			});



            /**
			 * 储位添加按钮
             */
            $('#btn_add_place_submit').click(function() {

                if(checkAreaCodeExist($("#collect-goods-area").val())){
                    alert("集货区编码已存在");
                    return;
                }
                if(collectPlaceArray.length == 0){
                    alert("未添加任何集货位，禁止提交！");
                    return;
                }
                $.ajaxHelper.doPostSync(saveUrl,JSON.stringify(collectPlaceArray),function(res){
                    if(res&&res.succeed){
                        alert('操作成功');
                        tableInit().refresh();
                    }else{
                        alert('操作异常');
                    }
                });
                $('#dataEditDiv').hide();
                $('#dataTableDiv').show();

                collectPlaceArray = new Array();
                $("#collect-goods-area").attr("readonly",false);
			});

            $("#middle-pack-max-num").change(function(){
               if(checkMiddlePackMaxNum()){
                   var middlePackMaxNum = $("#middle-pack-max-num").val();
                   $("#big-pack-min-num").html(1+Number(middlePackMaxNum)+"个");
			   }



			});

            $("#small-pack-max-num").change(function(){
                if(checkSmallPackMaxNum()){
                    var smallPackMaxNum = $("#small-pack-max-num").val();
                    $("#middle-pack-min-num").html(1+Number(smallPackMaxNum)+"个");
				}


            });


			function checkSmallPackMaxNum(){
                var smallPackMaxNum = $("#small-pack-max-num").val();
                var reg1 = /^[1-9][0-9]{0,4}$/;
                if(!reg1.test(smallPackMaxNum) || smallPackMaxNum > 19998){
                    alert("中单包裹数最大不得超过19998！");
                    return false;
                }
                return true;
			}

            function checkMiddlePackMaxNum(){
                var middlePackMaxNum = $("#middle-pack-max-num").val();
                var reg1 = /^[1-9][0-9]{0,4}$/;
                if(!reg1.test(middlePackMaxNum) || middlePackMaxNum > 19999 || middlePackMaxNum < Number($("#small-pack-max-num").val())){
                    alert("中单包裹数最大不得超过19999！并且必须大于中单最小包裹数");
                    return false;
                }
                return true;
            }

            function checkWaybillMaxNum(){

                var smallWaybillMaxNum = $("#small-waybill-max-num").val();
                var middleWaybillMaxNum = $("#middle-waybill-max-num").val();
                var bigWaybillMaxNum = $("#big-waybill-max-num").val();
                var reg1 = /^[1-9][0-9]?$/;
                if(!reg1.test(smallWaybillMaxNum) || !reg1.test(middleWaybillMaxNum) || !reg1.test(bigWaybillMaxNum)){
                    alert("可存放单据范围0到99！请重新输入");
                    return false;
                }
                return true;
            }


			function checkPlaceAdd(){
				var collectGoodsArea = $("#collect-goods-area").val();
                var collectGoodsPlaceNum = $("#collect-goods-place-num").val();
                var reg1 = /^[1-9][0-9]{0,2}$/;
                var reg2 = /^[A-Z]$/;
                if(!reg2.test(collectGoodsArea)){
                	alert("集货位编码只能是一位大写字母！");
                    return false;
                }

                if(!reg1.test(collectGoodsPlaceNum) || collectGoodsPlaceNum > 998){
                    alert("集货位数量应在1至998内！");
                    return false;
                }
                if(collectPlaceArray.length > 998){
                    alert("集货位数量应在1至998内！");
                    return false;
                }

                if(checkAreaCodeExist($("#collect-goods-area").val())){
                    alert("集货区编码已存在");
                    return false;
                }

				return true;
			}

            /**
			 * 保存集货类型 配置数据
             */
			$("#btn-collect-goods-palce-type").click(function(){

				if(checkSmallPackMaxNum()&&checkMiddlePackMaxNum()&&checkWaybillMaxNum()){
					//校验通过提交数据
					var params = {"createSiteCode":$("#loginUserCreateSiteCode").val()};
                    $('#place-type-change-form .edit-param').each(function () {
                        var _k = this.name;
                        var _v = $(this).val();
                        if(_k && _v){

                          params[_k]=_v;

                        }
                    });
                    $.ajaxHelper.doPostSync(saveTypeUrl,JSON.stringify(params),function(res){
                        if(res&&res.succeed){
                            alert('操作成功');
                            tableInit().refresh();
                        }else{
                            alert(res.message);
                        }
                    });
				}

			});

			$("#btn-collect-goods-palce-update").click(function(){

                var params = {};
                $('#place-change .edit-param').each(function () {
                    var _k = this.name;
                    var _v = $(this).val();
                    if(_k && _v){
                        params[_k]=_v;
                    }
                });
                $.ajaxHelper.doPostSync(updateUrl,JSON.stringify(params),function(res){
                    if(res&&res.succeed){
                        alert('操作成功');
                        tableInit().refresh();
                    }else{
                        alert('操作异常');
                    }
                });
                $("#place-change").modal('hide');
			});

			$('#btn_return').click(function() {
				$('#dataEditDiv').hide();
				$('#dataTableDiv').show();
			});


			function checkAreaCodeExist(areaCode){
				var result = true;
                $.ajaxHelper.doPostSync(checkAreaCodeUrl,JSON.stringify({"collectGoodsAreaCode":areaCode,
                    "createSiteCode":$("#loginUserCreateSiteCode").val()}),function(res){
                    if(res&&res.succeed){
                        result = res.data;
                    }
                });
                return result;
			}

		};
		return oInit;
	};
    initOrg();
	tableInit().init();
	pageInit().init();
});

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

            $('#site-group-select,#site-select').attr("disabled","disabled");
        }
    });

}