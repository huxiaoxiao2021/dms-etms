var toUploadUrl = '/weightAndVolumeCheckOfB2b/toUpload';
$(function () {
    var waybillSubmitUrl = '/weightAndVolumeCheckOfB2b/waybillSubmitUrl';

    var tableInit = function () {
        var oTableInit = new Object();
        oTableInit.init = function () {
            $('#waybillDataTable').bootstrapTable({
                url: '/weightAndVolumeCheckOfB2b/checkIsExcessOfWaybill', // 请求后台的URL（*）
                method: 'post', // 请求方式（*）
                queryParams: oTableInit.getSearchParams,
                height: 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                uniqueId: "ID", // 每一行的唯一标识，一般为主键列
                pagination: true, // 是否显示分页（*）
                pageNumber: 1, // 初始化加载第一页，默认第一页
                pageSize: 10, // 每页的记录行数（*）
                cache: false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                sidePagination: "server", // 分页方式：client客户端分页，server服务端分页（*）
                clickToSelect: true, // 是否启用点击选中行
                striped: true, // 是否显示行间隔色
                sortable: true, // 是否启用排序
                sortOrder: "asc", // 排序方式
                columns: oTableInit.tableColums,
                responseHandler: function(result){
                    if(result.code !== 200){
                        Jd.alert(result.message);
                        return {
                            "total" : 0,
                            "rows" : []
                        }
                    }
                    if(result.total > 0){
                        $("#btn_submit").attr("disabled", false);
                        $("#waybillWeight").attr("disabled", true);
                        $("#waybillVolume").attr("disabled", true);
                        $("#waybillLength").attr("disabled", true);
                        $("#waybillWidth").attr("disabled", true);
                        $("#waybillHeight").attr("disabled", true);
                    }
                    return {
                        "total" : result.total,
                        "rows" : result.rows
                    }
                },
                onLoadError: function(){  //加载失败时执行
                    Jd.alert("服务异常!");
                    return {
                        "total" : 0,
                        "rows" : []
                    }
                }
            });
        };

        oTableInit.getSearchParams = function (params) {
            var temp = oTableInit.getSearchCondition();
            if (!temp) {
                temp = {};
            }
            return temp;
        };
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
            field: 'waybillCode',
            title: '运单号',
            align: 'center'
        }, {
            field: 'packNum',
            title: '包裹数',
            align: 'center',
        }, {
            field: 'waybillWeight',
            title: '总重量/公斤',
            align: 'center',
        }, {
            field: 'waybillVolume',
            title: '总体积/立方厘米',
            align: 'center',
        }, {
            field: 'isExcess',
            title: '是否超标',
            align: 'center',
            formatter: function (value, row, index) {
                return value === 1 ? "是" : "否";
            }
        }, {
            field: 'upLoadNum',
            title: '上传图片数量',
            align: 'center'
        }, {
            field: 'excessType',
            title: '超标类型',
            align: 'center',
            visible: false
        },{
            field: 'isMultiPack',
            title: '多包裹',
            align: 'center',
            visible: false
        }, {
            field: 'operate',
            title: '操作',
            align: 'center',
            formatter: function (value, row, index) {
                var flag;
                if (row.isExcess === 0) {
                    flag = '';
                } else {
                    flag = '<a class="search" href="javascript:void(0)" ><i class="glyphicon glyphicon-search"></i>&nbsp;查看&nbsp;</a>'
                        + '<a class="upLoad" href="javascript:void(0)" ><i class="glyphicon glyphicon-upload"></i>&nbsp;点击上传&nbsp;</a>';
                }
                return flag;
            },
            events: {
                'click .upLoad': function (e, value, row, index) {
                    layer.open({
                        id: 'upExcessPicture',
                        type: 2,
                        title: '超标图片上传',
                        shade: 0.7,
                        maxmin: true,
                        shadeClose: false,
                        area: ['1000px', '600px'],
                        content: toUploadUrl + "?waybillOrPackageCode=" + row.waybillCode
                            + "&createSiteCode=" + $('#createSiteCode').val()
                            + "&weight=" + row.waybillWeight
                            + "&excessType=" + row.excessType
                            + "&isMultiPack=" + row.isMultiPack,
                        success: function (layero, index) {
                        }
                    });
                },
                'click .search': function (e, value, row, index) {
                    var rowIndex = this.parentNode.parentNode.rowIndex;
                    var count = $('#waybillDataTable')[0].rows[rowIndex].cells[5].innerHTML;
                    if(count === '0'){
                        Jd.alert("请先上传超标图片!");
                        return;
                    };
                    window.open($('#excessPicWeightOrPanorama').val());
                    window.open($('#excessPicFace').val());
                    window.open($('#excessPicLength').val());
                    window.open($('#excessPicWidth').val());
                    window.open($('#excessPicHeight').val());
                }
            }
        }];
        oTableInit.refresh = function () {
            $('#waybillDataTable').bootstrapTable('refreshOptions', {pageNumber: 1});
        };
        return oTableInit;
    };

    // 页面初始化查询
    $("#btn_submit").attr("disabled", true);
    $("#btn_check").attr("disabled", true);
    tableInit().init();

    // 单号回车
    $('#waybillOrPackageCode').keydown(function (event) {
        if(event.keyCode === 13){
            let getWaybillInfoUrl = '/weightAndVolumeCheckOfB2b/getWaybillInfo?waybillCode=' + $("#waybillOrPackageCode").val();
            $.ajaxHelper.doGetSync(getWaybillInfoUrl, null, function (res) {
                if (res && res.code === 200) {
                    $("#waybillOrPackageCode").attr("disabled", true);
                    $("#btn_check").attr("disabled", false);
                    $('#weightVolumeIsShow').css("display","block");
                    if(res.data){
                        isMultiPack = true;
                        $('#waybillVolumeIsShow').css("display","block");
                        $('#lengthIsShow').css("display","none");
                        $('#widthIsShow').css("display","none");
                        $('#heightIsShow').css("display","none");
                    }else {
                        isMultiPack = false;
                        $('#waybillVolumeIsShow').css("display","none");
                        $('#lengthIsShow').css("display","block");
                        $('#widthIsShow').css("display","block");
                        $('#heightIsShow').css("display","block");
                    }
                }else{
                    isMultiPack = false;
                    alert(res.message);
                }
            });
        }
    });

    //检查
    $('#btn_check').click(function(){
        //运单维度
        let weight = $('#waybillWeight').val();
        let length = $('#waybillLength').val();
        let width = $('#waybillWidth').val();
        let height = $('#waybillHeight').val();
        let volume = $('#waybillVolume').val();

        let reg = /^(-?\d+)(\.\d{1,2})?$/;
        if(isNaN(weight) || weight <= 0){
            Jd.alert('请输入正确的重量!');
            return;
        }
        if(!reg.test(weight)){
            Jd.alert('重量最多两位小数!');
            return;
        }

        if(isMultiPack){
            if(isNaN(volume) || volume <= 0){
                Jd.alert('请输入正确的体积!');
                return;
            }
            if(!reg.test(volume)){
                Jd.alert('体积最多两位小数!');
                return;
            }
        }else {
            if(isNaN(length) || length <= 0
                || isNaN(width) || width <= 0
                || isNaN(height) || height <= 0){
                Jd.alert('请输入正确的长宽高!');
                return;
            }
            if(!reg.test(length) || !reg.test(width) || !reg.test(height)){
                Jd.alert('长宽高最多两位小数!');
                return;
            }
        }
        tableInit().refresh();
        $("#waybillWeight").attr("disabled", false);
        $("#waybillVolume").attr("disabled", false);
        $("#waybillLength").attr("disabled", false);
        $("#waybillWidth").attr("disabled", false);
        $("#waybillHeight").attr("disabled", false);
    });

    //提交
    $('#btn_submit').click(function () {
        let waybillData = $('#waybillDataTable').bootstrapTable('getData');
        let uploadNum = $('#waybillDataTable')[0].rows[1].cells[5].innerHTML;
        if(waybillData[0].isExcess === 1 && uploadNum !== '5'){
            Jd.alert('请先上传' + waybillData[0].waybillCode + '的超标图片!');
            return;
        }
        let param = {};
        param.waybillOrPackageCode = waybillData[0].waybillCode;
        param.packNum = waybillData[0].packNum;
        param.waybillWeight = waybillData[0].waybillWeight;
        param.waybillLength = $("#waybillLength").val();
        param.waybillWidth = $("#waybillWidth").val();
        param.waybillHeight = $("#waybillHeight").val();
        param.waybillVolume = waybillData[0].waybillVolume;
        param.isExcess = waybillData[0].isExcess;
        param.excessType = waybillData[0].excessType;
        param.upLoadNum = uploadNum;
        param.createSiteCode = $('#createSiteCode').val();
        param.loginErp = $('#loginErp').val();
        if(waybillData[0].isExcess === 1){
            let excessPicUrls = [];
            excessPicUrls.push($('#excessPicWeightOrPanorama').val());
            excessPicUrls.push($('#excessPicFace').val());
            excessPicUrls.push($('#excessPicLength').val());
            excessPicUrls.push($('#excessPicWidth').val());
            excessPicUrls.push($('#excessPicHeight').val());
            param.urls = excessPicUrls;
        }

        jQuery.ajax({
            type: 'post',
            url: waybillSubmitUrl,
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(param),
            async: true,
            success: function (result) {
                if(result && result.code === 200){
                    Jd.alert('提交成功!');
                    reset();
                    $('#waybillDataTable').bootstrapTable("removeAll");
                    $('#excessPicWeightOrPanorama').val('');
                    $('#excessPicFace').val('');
                    $('#excessPicLength').val('');
                    $('#excessPicWidth').val('');
                    $('#excessPicHeight').val('');
                }else {
                    Jd.alert(result.message);
                }
            }
        });
    });

    //重置
    $('#btn_refresh').click(function () {
        reset();
        $('#waybillDataTable').bootstrapTable("removeAll");
    });

});

let isMultiPack = false;

function reset(){
    isMultiPack = false;
    $("#waybillOrPackageCode").attr("disabled", false);
    $("#waybillWeight").attr("disabled", false);
    $("#waybillVolume").attr("disabled", false);
    $("#waybillLength").attr("disabled", false);
    $("#waybillWidth").attr("disabled", false);
    $("#waybillHeight").attr("disabled", false);
    $("#btn_check").attr("disabled", true);
    $("#btn_submit").attr("disabled", true);
    $('#weightVolumeIsShow').css("display","none");
    $('#waybillOrPackageCode').focus();
    $('#waybillOrPackageCode').val('');
    $('#waybillWeight').val('');
    $('#waybillVolume').val('');
    $('#waybillLength').val('');
    $('#waybillWidth').val('');
    $('#waybillHeight').val('');
}

//确认框
function confirm(msg,okFunc,cancelFunc){
    var cancelFunc = arguments[2] ? arguments[2] : function () {};
    layer.confirm( '<span style="margin-left:40px;margin-right:40px;font-size: 18px;font-weight: bold;font-family:"Arial","Microsoft YaHei","黑体","宋体",sans-serif;">'
        + msg+ '</span>', {
        // skin: 'layui-layer-lan',
        title:'请仔细确认',
        async: false,
        closeBtn: 0,
        shade: 0.7,
        icon: 3,
        anim: 1,
        area: ['420px', '200px'],
        btn: ['确定','取消']
    },function (index) {
        okFunc();
        layer.close(index);
    },function () {
        cancelFunc();
    });

}

function onEditableSave(field, row, oldValue, $el) {
    $('#packageDataTable').bootstrapTable('resetView');
    return false;
}