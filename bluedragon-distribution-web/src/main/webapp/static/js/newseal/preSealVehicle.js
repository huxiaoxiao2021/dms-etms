$(function() {
	var batchSealUrl = '/newseal/preSealVehicle/batchSeal';
    var queryUrl = '/newseal/preSealVehicle/queryPreSeals';
    var modifyVolumePageUrl = '/newseal/preSealVehicle/getModifyVolumePage';

    var preData = new Map();
    var measureMap = new Map();
    var noVolumeMap = new Map();

    var isDoing = false;
    /*blockUI*/
 /*   var lockPage = function () {
        $.blockUI({
            message: "<span class='pl20 icon-loading'>加载中-请您稍候</span>",
            css: {
                border: 'none',
                padding: '15px',
                backgroundColor: '#fff',
                '-webkit-border-radius': '10px',
                '-moz-border-radius': '10px',
                opacity: .5,
                color: '#000'
            }
        });
    };
    var unLockPage = function () {
        $.unblockUI();
    };*/
    /*$.combobox.createNew('hourRange',{
        width: '150',
        placeholder:'查询时间范围',
        allowClear:true,
        data:[
            {id:'12',text:'12小时'},
            {id:'24',text:'24小时'},
            {id:'48',text:'48小时'},
            {id:'72',text:'72小时'}
        ]
    });*/

	var tableInit = function() {
		var oTableInit = new Object();
		oTableInit.init = function() {
			$('#dataTable').bootstrapTable('destroy').bootstrapTable({
				url : queryUrl, // 请求后台的URL（*）
				method : 'post', // 请求方式（*）
				toolbar : '#toolbar', // 工具按钮用哪个容器
                toolbarAlign : 'right',
				queryParams : oTableInit.getSearchParams, // 查询参数（*）
				// height : 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
				uniqueId : "ID", // 每一行的唯一标识，一般为主键列
				pagination : false, // 是否显示分页（*）
				pageNumber : 1, // 初始化加载第一页，默认第一页
				pageSize : 10000, // 每页的记录行数（*）
				pageList : [ 10, 25, 50, 100 ], // 可供选择的每页的行数（*）
				cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
				sidePagination : "client", // 分页方式：client客户端分页，server服务端分页（*）
				striped : true, // 是否显示行间隔色
				// showColumns : true, // 是否显示所有的列
				// sortable : true, // 是否启用排序
				// sortOrder : "asc", // 排序方式
//				search : true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
				showRefresh : false, // 是否显示刷新按钮
				// minimumCountColumns : 2, // 最少允许的列数
				clickToSelect : false, // 是否启用点击选中行
				// showToggle : true, // 是否显示详细视图和列表视图的切换按钮
//				showPaginationSwitch : true, // 是否显示分页关闭按钮
// 				strictSearch : true,
				// icons: {refresh: "glyphicon-repeat", toggle:
				// "glyphicon-list-alt", columns: "glyphicon-list"},
				// search:false,
				// cardView: true, //是否显示详细视图
				detailView: true, //是否显示父子表
                rowStyle : function(row,index){
                    return {css:{"background-color":"#61add3"}}
                },
				// showFooter:true,
				// paginationVAlign:'center',
				// singleSelect:true,
                responseHandler: function(data){
                    if(data.code == 200){
                        saveData(data.data);
                        $('#pre_num').val(preData.size);
                        return data.data;
                    }else{
                        preData = new Map();
                        noVolumeMap = new Map();
                        measureMap = new Map();
                        $('#pre_num').val(0);
                        $('#sel_num').val(0);
                        alert(data.message);
                        return [];
                    }
                },
                //注册加载子表的事件。注意下这里的三个参数！
                onExpandRow: function (index, row, $detail) {
                    oTableInit.InitSubTable(index, row, $detail);
                },
                onPostBody :function (index, row, $detail) {
                    // oTableInit.InitSubTable(index, row, $detail);
                    $("#dataTable").bootstrapTable('expandAllRows');
                    setSelNum();
                },
                columns : oTableInit.tableColums
			}).on('check.bs.table', function (e, row){
			    if(row.id != null){
                    $("#" + row.receiveSiteCode).bootstrapTable("checkAll");
                }
                setSelNum();
            }).on('uncheck.bs.table', function (e, row){
                if(row.id != null){
                    $("#" + row.receiveSiteCode).bootstrapTable("uncheckAll");
                }
                setSelNum();
            }).on('check-all.bs.table', function (e, rows){
                for(var i = 0; i < rows.length; i++){
                    if(rows[i].id != null){
                        $("#" + rows[i].receiveSiteCode).bootstrapTable("checkAll");
                    }
                }
                setSelNum();

            }).on('uncheck-all.bs.table', function (e, rows){
                for(var i = 0; i < rows.length; i++){
                    if(rows[i].id != null){
                        $("#" + rows[i].receiveSiteCode).bootstrapTable("uncheckAll");
                    }
                }
                setSelNum();
            });
		};
        //初始化子表格
        oTableInit.InitSubTable = function (index, row, $detail) {
            var cur_table = $detail.html('<table style="table-layout: fixed;"></table>').find('table');
            var vehicleNumbers = row.vehicleNumbers;
            var vehicleSealCodeMap = row.vehicleSealCodeMap;
            $(cur_table).attr("id", row.receiveSiteCode);
            $(cur_table).bootstrapTable('destroy').bootstrapTable({
                data : row.sendCodes, // 请求后台的URL（*）
                uniqueId : "ID", // 每一行的唯一标识，一般为主键列
                pagination : false, // 是否显示分页（*）
                cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                striped : true, // 是否显示行间隔色
                showRefresh : false, // 是否显示刷新按钮
                clickToSelect : false, // 是否启用点击选中行
                checkboxHeader:false,
                theadClasses : "thead-light",
                classes:"table table-borderless table-striped",
                detailView: false, //是否显示父子表
                columns: [{
                    checkbox : true,
                    width: '50px',
                    align: 'center',
                    formatter : subStateFormatter
                }, {
                    field: 'receiveSiteCode',
                    title: '目的地',
                    align : "center",
                    visible:false
                }, {
                    field: 'sealDataCode',
                    align : "center",
                    title: '批次号'
                }, {
                    field: 'vehicleNumber',
                    align : "center",
                    title: '车牌号',
                    width: '50%',
                    formatter: function(value, row, index) {
                        if(vehicleNumbers.length > 1 ){
                            var headOption = "<option value =''>请选择车辆</option>";
                            $.each(vehicleNumbers,function(i,obj){
                                if(row["vehicleNumber"] != null && row["vehicleNumber"] == obj){
                                    headOption = headOption + "<option selected value='"+obj+"'>"+obj+"</option>";
                                }else{
                                    headOption = headOption + "<option value='"+obj+"'>"+obj+"</option>";
                                }
                            });
                            var option = '<select class="form-control vehicleNumberSelect" id='+row.sealDataCode+' name="vehicleNumberSelect" style="width:200px;text-align: center;margin:auto">'+
                                headOption + '</select>';
                            return option;
                        }else{
                            return value;
                        }
                    },
                    events: {'change .vehicleNumberSelect': function (e, value, row, index) {
                        var valueSelected = $(this).children('option:selected').val();
                            $('#table').bootstrapTable('updateCell', {
                                index: index,
                                field: 'vehicleNumber',
                                value: valueSelected
                            });
                        row.vehicleNumber = valueSelected;
                        row.sealCodes = vehicleSealCodeMap[valueSelected];
                        reloadNote(index, row.receiveSiteCode);
                        }
                    }
                }, {
                    field: 'volume',
                    title: '体积',
                    visible:false
                }, {
                    field: 'weight',
                    title: '重量',
                    visible:false
                }, {
                    field: 'note',
                    title: '提示',
                    formatter: function(value, row, index) {
                        var noteValue;
                        if (row.vehicleNumber != null && noVolumeMap.get(row.vehicleNumber) != null) {
                            return '<span style="color: red;font-weight: bold;">请补录体积</span>';
                        } else {
                            return '';
                        }
                    },
                }]
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
            var hourRange = $('input:radio:checked').val();
            return {"hourRange" : hourRange};
		};

        oTableInit.tableColums = [{
            checkbox : true,
            width: '50px',
            formatter : stateFormatter/*,
            events: {'check .bs-checkbox': function (row, $element) {
                $("#" + row.receiveSiteCode).bootstrapTable("checkAll");
            },'uncheck .bs-checkbox': function (row, $element) {
                $("#" + row.receiveSiteCode).bootstrapTable("uncheckAll");
            }
            }*/
        },{
            title: "序号",
            formatter: function(value, row, index) {
                // 显示行号
                return index + 1;
            },
            align: 'center',
            width: '50px'
        }, {
            field : 'receiveSiteName',
            title : '目的地'
        }, {
            field : 'sendCodes',
            title : '批次数量',
            width: '80px',
            formatter: function (value, row, index) {
                return value.length;
            }
        }, {
            field : 'transportCode',
            title : '运力编码'
        }, {
            field : 'sendCarTime',
            title : '标准发车时间',
            formatter: function (value, row, index) {
                return value.substring(value.length-5);
            }
        }, {
            field : 'preSealSource',
            title : '来源',
            width: '50px',
            formatter: function (value, row, index) {
                if (value == null || value == 1) {
                    return '普通';
                } else if (value == 2) {
                    return '传摆';
                } else {
                    return value;
                }
            }
        }, {
            field : 'transWayName',
            title : '运力类型',
            width: '80px',
        }, {
            field : 'sealCodeStr',
            width: '50%',
            title : '封签号',
            cellStyle:{
                css:{"background-color":"#61add3","overflow":"hidden","white-space":"nowrap","text-overflow":"ellipsis"}
            }
        }, {
            field: 'op',
            title: '操作',
            formatter : function (value, row, index) {
                if (row.transWay != null && row.transWay == 1) {
                return '<a class="mdinfo" href="javascript:void(0)" ><i class="glyphicon glyphicon-pencil"></i><span style="color: black;font-weight: bold;">补录体积</span></a>';
                } else {
                    return '';
                }
            },
            events: {
                'click .mdinfo': function(e, value, row, index) {
                    layer.open({
                        id:'modifyVolumeFrame',
                        type: 2,
                        title:'补录体积',
                        shadeClose: true,
                        shade: 0.7,
                        maxmin: true,
                        area: ['850px', '380px'],
                        content: modifyVolumePageUrl,
                        success: function(layero, index){
                            var transportCode = row.transportCode;
                            var transWayName = row.transWayName;
                            var frameId = document.getElementById("modifyVolumeFrame").getElementsByTagName("iframe")[0].id;
                            var frameWindow = $('#' + frameId)[0].contentWindow;
                            frameWindow.$('#transportCode-input').val(transportCode);
                            frameWindow.$('#transWayName-input').val(transWayName);
                            //非公路零担不需要录体积
                            frameWindow.$("#btn_reload").click();

                        }
                    });

                }
            }
        },  {
            field : 'id',
            title : 'ID',
            visible:false
        }, {
            field : 'preSealUuid',
            title : 'preSealUuid',
            visible:false
        }, {
            field : 'createSiteCode',
            title : 'createSiteCode',
            visible:false
        }, {
            field : 'createSiteName',
            title : 'createSiteName',
            visible:false
        }, {
            field : 'receiveSiteCode',
            title : 'receiveSiteCode',
            visible:false
        }, {
            field : 'receiveSiteName',
            title : 'receiveSiteName',
            visible:false
        }, {
            field : 'sendCodes',
            title : 'sendCodes',
            visible:false
        }, {
            field : 'vehicleNumbers',
            title : 'vehicleNumbers',
            visible:false
        }, {
            field : 'transWay',
            title : 'transWay',
            visible:false
        }
        ];
        function stateFormatter(value, row, index) {
            if (row.sendCodes.length > 0) {
                // oTableInit.bootstrapTable('expandRow', index);
                return {
                    checked: true  //设置选中
                };
            } else {
                return {
                    checked: false,//设置选中
                    disabled :true
                };
            }
        };
        function subStateFormatter(value, row, index) {
            var selectedFalg0 = row.selectedFalg;
            if(selectedFalg0 != null && selectedFalg0 == false){
	            return {
	                checked: false  //设置不选中
	            };
            }else{
	             return {
	                checked: true  //设置选中
	            };
            }
        };
		oTableInit.refresh = function() {
			$('#dataTable').bootstrapTable('refresh');
		};

        function reloadNote(index, receiveSiteCode) {

            $("#" + receiveSiteCode).bootstrapTable('updateCell',
                {
                    index: index,
                    field: 'note',
                    value: ''
                });
        }

		return oTableInit;
	};

	var pageInit = function() {
		var oInit = new Object();
		oInit.init = function() {
		    $('#btn_query_transport').click(function() {
                window.open("/newseal/preSealVehicle/unusedtransport");
			});

		    $('#btn_query').click(function() {
                // lockPage();
		    	tableInit().refresh();
		    	// unLockPage();
			});
			$('#btn_submit').click(function() {
                var params = $("#dataTable").bootstrapTable('getAllSelections');
                if (params.length == 0) {
                    Jd.alert('请选择要需要封车的目的地批次数据！');
                    return;
                }
                var hasNoSelected = false;
                for ( var i = 0; i <params.length && !hasNoSelected; i++){
                	var id  = params[i].receiveSiteCode;
                	var subAllData = $("#" + id).bootstrapTable('getData');
                	var subSelected = $("#" + id).bootstrapTable('getAllSelections');
                	if(subAllData.length > subSelected.length){
                		hasNoSelected = true;
                	}
                }
                var confirmMsg = "确认执行一键封车？";
                if(hasNoSelected){
                	confirmMsg = "存在未选中的批次，确认执行一键封车？";
                }
                !$.msg.confirm(confirmMsg,function () {
                    // if(isDoing){
                    //     return;
                    // }
                    // isDoing = true;
                    // lockPage();
                    // $('#btn_submit').disabled = true;
                    var failedList = [];
                    var nowAllowCommit = [];
                    var postData = [];

                    for ( var i = 0; i <params.length; i++){
                        console.log(params[i]);
                        var id  = params[i].receiveSiteCode;
                        var pre = preData.get(id);
                        var isOk = true;
                        var isCommitOk = true;
                        var subPre = [];
                        var subParams = $("#" + id).bootstrapTable('getAllSelections');
                        for ( var j = 0; j <subParams.length; j++){
                            var vehicleNumber = subParams[j]["vehicleNumber"];
                            if(vehicleNumber == null || vehicleNumber == undefined || vehicleNumber == ''){
                                isOk = false;
                                break;
                            } else if (noVolumeMap.get(vehicleNumber) != null) {
                                nowAllowCommit.push(params[i].receiveSiteName);
                                isCommitOk = false;
                                break;
                            }
                            else{
                                subPre.push({"vehicleNumber":vehicleNumber, "sealDataCode":subParams[j].sealDataCode,
                                    "receiveSiteCode":subParams[j].receiveSiteCode, "sealCodes":subParams[j].sealCodes,
                                    "volume":measureMap.get(vehicleNumber).volume,"weight":measureMap.get(vehicleNumber).weight});
                            }
                        }
                        if(isOk){
                            if(subPre.length > 0 && isCommitOk){
                                pre.sendCodes = subPre;
                                postData.push(pre);
                            }
                        }else{
                            failedList.push(params[i].receiveSiteName);
                        }

                        console.log(subParams);
                    }
                    if(failedList.length > 0){
                        Jd.alert('以下目的地存在未选择车牌号的批次：' + failedList.toString() );
                    }else{
                        console.log(JSON.stringify(postData));
                        if (postData.length > 0) {
                            $.ajaxHelper.doPostSync(batchSealUrl,JSON.stringify(postData),function(data){
                                if(data.code == 200){
                                    if (nowAllowCommit.length > 0) {
                                        Jd.alert('数据上传成功，以下目的地需要补录体积后重新上传：' + nowAllowCommit.toString() );
                                    } else {
                                        Jd.alert('数据上传成功！');
                                    }
                                    tableInit().refresh();
                                }else if(data.code == 600){
                                    var faileddata = data.data;
                                    if(faileddata != null && faileddata.length > 0){
                                        saveData(faileddata);
                                        $('#pre_num').val(preData.size);
                                        $("#dataTable").bootstrapTable('load', faileddata);
                                    }
                                    Jd.alert(data.message);
                                }else{
                                    $('#pre_num').val(0);
                                    $('#sel_num').val(0);
                                    Jd.alert(data.message);
                                    return [];
                                }
                            });
                        } else {
                            if (nowAllowCommit.length > 0) {
                                Jd.alert('以下目的地需要补录体积后重新上传：' + nowAllowCommit.toString() );
                            }
                        }


                    }
                    // $('#btn_submit').disabled = false;
                    // isDoing = false;
                    // unLockPage();
                },function () {});

            });

		};
		return oInit;
	};

	var saveData = function(sdata) {
        preData = new Map();
        noVolumeMap = new Map();
        measureMap = new Map();
        var data = JSON.parse(JSON.stringify(sdata));
	    if(data != null && data.length > 0){
            for (var i = 0; i < data.length; i++){
                preData.set(data[i].receiveSiteCode, data[i]);
                var preSealSource = data[i].preSealSource;
                var transWay = data[i].transWay;
                for (var key in data[i].vehicleMeasureMap) {
                    measureMap.set(key, data[i].vehicleMeasureMap[key]);
                    var volume = data[i].vehicleMeasureMap[key].volume;
                    if (preSealSource == 2 && transWay == 1 && (volume == null || volume <= 0)) {
                        noVolumeMap.set(key, data[i].vehicleMeasureMap[key]);
                    }
                }
            }
        }
        $('#pre_num').val(preData.size);
    }

    var setSelNum = function () {
        var params = $("#dataTable").bootstrapTable('getAllSelections');
        $('#sel_num').val(params.length);
    }

	tableInit().init();
	pageInit().init();
});