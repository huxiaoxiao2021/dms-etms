var toUploadUrl = '/weightAndVolumeCheckOfB2b/toUpload';
var searchExcessPictureUrl = '/weightAndVolumeCheckOfB2b/searchExcessPicture';
$(function () {
    $('#btn_weightVolumeRatio_check').css("display","none");
    var waybillSubmitUrl = '/weightAndVolumeCheckOfB2b/waybillSubmitUrl';
    var packageSubmitUrl = '/weightAndVolumeCheckOfB2b/packageSubmitUrl';

    $('#waybillDataTable').bootstrapTable({
        url: '/weightAndVolumeCheckOfB2b/init', // 请求后台的URL（*）
        method: 'get', // 请求方式（*）
        height: 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
        uniqueId: "ID", // 每一行的唯一标识，一般为主键列
        pagination: true, // 是否显示分页（*）
        cache: false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        sidePagination: "server", // 分页方式：client客户端分页，server服务端分页（*）
        clickToSelect: true, // 是否启用点击选中行
        striped: true, // 是否显示行间隔色
        sortable: true, // 是否启用排序
        sortOrder: "asc", // 排序方式
        columns: [{
            checkbox: true
        }, {
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
            title: '总体积/立方米',
            align: 'center',
        },{
            field: 'isExcess',
            title: '是否超标',
            align: 'center',
            formatter: function (value, row, index) {
                return value == "1" ? "是" : "否";
            }
        }, {
            field: 'upLoadNum',
            title: '上传图片数量',
            align: 'center'
        }, {
            field: 'operate',
            title: '操作',
            align: 'center',
            formatter : function (value, row, index) {
                var flag;
                if(row.isExcess == 0){
                    flag = '';
                }else{
                    flag = '<a class="search" href="javascript:void(0)" ><i class="glyphicon glyphicon-search"></i>&nbsp;查看&nbsp;</a>'
                        + '<a class="upLoad" href="javascript:void(0)" ><i class="glyphicon glyphicon-upload"></i>&nbsp;点击上传&nbsp;</a>';
                }
                return flag;
            },
            events: {
                'click .upLoad': function(e, value, row, index) {
                    var rowIndex = this.parentNode.parentNode.rowIndex;
                    layer.open({
                        id:'upExcessPicture',
                        type: 2,
                        title:'超标图片上传',
                        shadeClose: true,
                        shade: 0.7,
                        maxmin: true,
                        shadeClose: false,
                        area: ['1000px', '600px'],
                        content: toUploadUrl + "?waybillOrPackageCode=" + row.waybillCode
                            + "&createSiteCode=" + $('#createSiteCode').val() + "&rowIndex=" + rowIndex + "&isWaybill=" + 1,
                        success: function(layero, index){
                        }
                    });
                },
                'click .search': function(e, value, row, index) {
                    var rowIndex = this.parentNode.parentNode.rowIndex;
                    var count = $('#waybillDataTable')[0].rows[rowIndex].cells[6].innerHTML;
                    // if(count == 0){
                    //     Jd.alert("请先上传超标图片!");
                    //     return;
                    // };
                    $.ajax({
                        type : "get",
                        url : searchExcessPictureUrl + "?waybillOrPackageCode=" + row.waybillCode + "&siteCode=" + $('#createSiteCode').val(),
                        data : {},
                        async : false,
                        success : function (data) {
                            if(data && data.code == 200){
                                //在新窗口展示所有图片
                                var list = data.data;
                                for(var i=0;i<list.length;i++){
                                    if(list[i] == null || list[i] == ""){
                                        break;
                                    }
                                    window.open(list[i]);
                                }
                            }else{
                                Jd.alert(data.message);
                            }
                        }
                    });
                }
            }
        }],
        responseHandler: function(result){
            if(result.code != 200){
                Jd.alert(result.message);
                return {
                    "total" : 0,
                    "rows" : []
                }
            }
            return {
                "total" : result.data.length,
                "rows" : result.data
            }
        },
        onLoadError: function(){  //加载失败时执行
            Jd.alert("服务异常!");
        }
    });



    //焦点聚焦：1、运单2、重量3、体积
    $('#waybillOrPackageCode').focus();
    $('#waybillOrPackageCode').keydown(function (event) {
        if(event.keyCode == 13){
            if($('#checkOfWaybill').attr("checked")){
                $('#waybillWeight').focus();
            }else{
                //包裹维度提交按钮置为不可用
                $("#btn_submit").attr("disabled",true);
                $('#packageDataTable').bootstrapTable('destroy');
                showAllPackage();
                resetOfWaybbill();
            }
        };
    });
    $('#waybillWeight').keydown(function (event) {
        if(event.keyCode == 13){
            $('#waybillVolume').focus();
        };
    });

    //按运单维度控制页面显示
    $('#checkOfWaybill').click(function(){
        $('#waybillWeight').val('0.00');
        $('#waybillVolume').val('0.000000');
        $('#waybillOrPackageCode').focus();
        $('#waybillOrPackageCode').val("");//初始化输入框
        $('#waybillVolumeIsShow').css("display","block");
        $('#waybillWeightIsShow').css("display","block");
        $('#packageDataTableDiv').css("display","none");
        $('#waybillDataTableDiv').css("display","block");
        $('#btn_refresh').css("display","block");
        $("#btn_submit").attr("disabled",false);
        $('#waybillDataTable').bootstrapTable('refreshOptions', {pageNumber: 1});//初始化
        $('#btn_weightVolumeRatio_check').css("display","none");
    });
    //按包裹维度控制页面显示
    $('#checkOfPackage').click(function(){
        $('#waybillOrPackageCode').focus();
        $('#waybillOrPackageCode').val("");//初始化输入框
        $('#waybillVolumeIsShow').css("display","none");
        $('#waybillWeightIsShow').css("display","none");
        $('#waybillDataTableDiv').css("display","none");
        $('#waybillWeight').val("");
        $('#waybillVolume').val("");
        $('#packageDataTableDiv').css("display","none");
        $('#btn_refresh').css("display","none");
        //包裹维度提交按钮置为不可用
        $("#btn_submit").attr("disabled",true);
        $('#packageDataTable').bootstrapTable('destroy');//初始化表格
        $('#btn_weightVolumeRatio_check').css("display","block");
        showAllPackage();
    });


    //包裹维度重泡比校验
    $('#btn_weightVolumeRatio_check').click(function(){
        var packageData = $('#packageDataTable').bootstrapTable('getSelections');
        if(packageData.length == 0){
            Jd.alert('请选中后再校验!');
            return;
        }
        if(packageData.length > 1){
            Jd.alert('请选中一条记录校验!');
            return;
        }
        var num = $('#packageDataTable tbody .selected').attr("data-index");
        var index = parseInt(num) + 1;
        $('#packageDataTable')[0].rows[index].cells[8].innerHTML = '×';
        var weight = packageData[0].weight;
        var length = packageData[0].length;
        var width = packageData[0].width;
        var height = packageData[0].height;
        var volume = (length*width*height)/1000000;
        if(weight == null || length == null || width == null || height == null){
            Jd.alert('录入后再校验!');
            return;
        }
        if(weight/volume > 7800){
            Jd.alert('当前包裹号'+packageData[0].packageCode+'重泡比超过7800，请核实后重新录入!');
            return;
        }
        if(weight > 5000){
            Jd.alert('当前包裹号'+packageData[0].packageCode+'重量超过5000KG，请核实后重新录入!');
            return;
        }
        if(volume > 5){
            Jd.alert('当前包裹号'+packageData[0].packageCode+'体积超过5m³，请核实后重新录入!');
            return;
        }
        //重泡比弱拦截
        if(weight/volume < 168 || weight/volume > 330){
            var messageBodyStr = '重泡比超过正常范围168:1到330:1，请确认是否强制录入？';
            confirm(messageBodyStr,
                function () {
                    $('#packageDataTable')[0].rows[index].cells[8].innerHTML = '√';
                },
                function () {
                    return;
                });
        }else {
            $('#packageDataTable')[0].rows[index].cells[8].innerHTML = '√';
        }
    });

    //检查
    $('#btn_check').click(function(){
        if($('#checkOfWaybill').attr("checked")){
            //运单维度
            var weight = $('#waybillWeight').val();
            var volume = $('#waybillVolume').val();
            if(isNaN(weight) || isNaN(volume)){
                Jd.alert('请输入正确的重量/体积!');
                return;
            }
            if(weight <= 0 || volume <= 0){
                Jd.alert('请输入正确的重量/体积!');
                return;
            }
            var reg = /^(-?\d+)(\.\d{1,2})?$/;
            if(!reg.test(weight)){
                Jd.alert('重量最多两位小数!');
                return;
            }
            reg = /^(-?\d+)(\.\d{1,6})?$/;
            if(!reg.test(volume)){
                Jd.alert('体积最多六位小数!');
                return;
            }

            //重泡比强校验
            if(weight/volume > 7800){
                Jd.alert('当前运单重泡比超过7800,请核实后重新录入!');
                return;
            }

            var param = {};
            param.isWaybill = 1;
            param.waybillOrPackageCode = $('#waybillOrPackageCode').val();
            param.waybillWeight = $('#waybillWeight').val();
            param.waybillVolume = $('#waybillVolume').val();
            param.createSiteCode = $('#createSiteCode').val();

            jQuery.ajax({
                type: 'post',
                url: '/weightAndVolumeCheckOfB2b/checkIsExcessOfWaybill',
                dataType: "json",//必须json
                contentType: "application/json", // 指定这个协议很重要
                data: JSON.stringify(param),
                async: true,
                success: function (data) {
                    if (data.code == 200) {
                        //重泡比弱拦截
                        if(weight/volume < 168 || weight/volume > 330){
                            var messageBodyStr = '重泡比超过正常范围168:1到330:1，请确认是否强制录入？';
                            confirm(messageBodyStr,
                                function () {
                                    var sign = false;
                                    var allTableData = $('#waybillDataTable').bootstrapTable('getData');
                                    var result = data.data;
                                    $.each(allTableData,function(i,e){
                                        if(result[0].waybillCode == e.waybillCode){
                                            Jd.alert("运单号"+e.waybillCode+"已扫描请勿重复扫描!");
                                            sign = true;
                                            return;
                                        }
                                    });
                                    if(!sign){
                                        $('#waybillDataTable').bootstrapTable('append', data.data);
                                    }
                                },
                                function () {
                                    return;
                                });
                        }
                    }else {
                        Jd.alert(data.message);
                    }
                }
            });
            //初始化输入框
            resetOfWaybbill();

        }else {
            //包裹维度组装表格数据
            var allTableData = $('#packageDataTable').bootstrapTable('getData');
            var params = [];
            var flage = 0;
            var weightVolumeRatio = 0;
            var errorPackageCode = null;
            $.each(allTableData,function(i,e){
                var isTrue = $('#packageDataTable')[0].rows[i+1].cells[8].innerHTML;
                if(isTrue != '√'){
                    errorPackageCode = e.packageCode;
                    weightVolumeRatio = 1;
                    return false;
                }
                if(e.weight == null || e.length == null || e.width == null || e.height == null){
                    flage = 1;
                    return false;
                };
                var param = {};
                param.packageCode = e.packageCode;
                param.weight = e.weight;
                param.length = e.length;
                param.width = e.width;
                param.height = e.height;
                params.push(param);
            });
            if(weightVolumeRatio == 1){
                Jd.alert(errorPackageCode + '的重泡比校验不合格!');
                return;
            }
            if(flage == 1){
                Jd.alert('表格数据不能为空!');
                return;
            }
            if(params.length == 0){
                Jd.alert('请输入运单号/包裹号!');
                return;
            }
            //判断是否超标
            jQuery.ajax({
                type: 'post',
                url: '/weightAndVolumeCheckOfB2b/checkIsExcessOfPackage',
                dataType: "json",//必须json
                contentType: "application/json", // 指定这个协议很重要
                data: JSON.stringify(params),
                async: false,
                success: function (data) {
                    if (data.code == 200) {
                        for (var i=1;i<params.length+1;i++){
                            $('#packageDataTable')[0].rows[i].cells[6].innerHTML = data.data == 1 ? '是' : '否';
                            $('#packageDataTable')[0].rows[i].cells[7].innerHTML = 0;
                        }
                    }else {
                        Jd.alert(data.message);
                    }
                }
            });
            $("#btn_submit").attr("disabled",false);
        }

    });



    //提交
    $('#btn_submit').click(function () {

        //运单维度
        if($('#checkOfWaybill').attr("checked")){
            var waybillData = $('#waybillDataTable').bootstrapTable('getSelections');
            if(waybillData.length == 0){
                Jd.alert('请选中在提交!');
                return;
            }
            if(waybillData.length > 1){
                Jd.alert('请选中一条记录提交!');
                return;
            }
            var param = {};
            for(var i=0;i<waybillData.length;i++){
                var uploadNum = $('#waybillDataTable')[0].rows[i+1].cells[6].innerHTML;
                if(waybillData[i].isExcess == 1 && uploadNum != 5){
                    Jd.alert('请先上传'+waybillData[i].waybillCode+'的超标图片!');
                    return;
                }
                param.isWaybill = 1;
                param.waybillOrPackageCode = waybillData[i].waybillCode;
                param.packNum = waybillData[i].packNum;
                param.waybillWeight = waybillData[i].waybillWeight;
                param.waybillVolume = waybillData[i].waybillVolume;
                param.isExcess = waybillData[i].isExcess;
                param.upLoadNum = uploadNum;
                param.createSiteCode = $('#createSiteCode').val();
                param.loginErp = $('#loginErp').val();
            }

            jQuery.ajax({
                type: 'post',
                url: waybillSubmitUrl,
                dataType: "json",//必须json
                contentType: "application/json", // 指定这个协议很重要
                data: JSON.stringify(param),
                async: true,
                success: function (result) {
                    if(result && result.code == 200){
                        Jd.alert('提交成功!');
                    }else {
                        Jd.alert(result.message);
                    }
                }
            });
            resetOfWaybbill();
        }else {
            //包裹维度
            var allTableData = $('#packageDataTable').bootstrapTable('getData');
            //判断是否全部提交
            var isExcess = $('#packageDataTable')[0].rows[1].cells[5].innerHTML;
            if(isExcess == '是'){
                for(var i = 0;i<allTableData.length;i++){
                    var upLoadNum = $('#packageDataTable')[0].rows[i+1].cells[6].innerHTML;
                    if(upLoadNum !='5'){
                        Jd.alert('请全部上传后再提交!');
                        return;
                    }
                }
            }
            //组装数据并提交
            var params = [];
            $.each(allTableData,function(i,e){
                var param = {};
                param.loginErp = $('#loginErp').val();
                param.createSiteCode = $('#createSiteCode').val();
                param.isWaybill = 0;
                param.packageCode = e.packageCode;
                param.weight = e.weight;
                param.length = e.length;
                param.width = e.width;
                param.height = e.height;
                param.isExcess = isExcess == '是'?1:0;
                param.upLoadNum = isExcess == '是'?5:0;
                params.push(param);
            });

            jQuery.ajax({
                type: 'post',
                url: packageSubmitUrl,
                dataType: "json",//必须json
                contentType: "application/json", // 指定这个协议很重要
                data: JSON.stringify(params),
                async: true,
                success: function (data) {
                    if (data.code == 200) {
                        Jd.alert('提交成功!');
                    }else {
                        Jd.alert(data.message);
                    }
                }
            });

        }

        resetOfWaybbill();
    });

    //重置
    $('#btn_refresh').click(function () {
        resetOfWaybbill();
        $('#waybillDataTable').bootstrapTable('refreshOptions', {pageNumber: 1});//初始化
    });

    //数字加减框校验
    initAndCheckUpDown();


});

//运单维度初始化
function resetOfWaybbill(){
    $('#waybillOrPackageCode').focus();
    $('#waybillOrPackageCode').val('');
    $('#waybillWeight').val('0.00');
    $('#waybillVolume').val('0.000000');
}

function initAndCheckUpDown() {
    var regPos = /^\d+(\.\d+)?$/; //非负浮点数
    $('#waybillWeightUp').on('click', function() {
        if(!regPos.test($('#waybillWeight').val())){
            Jd.alert(" 运单总重量输入格式不正确!");
            $('#waybillWeight').val("0.00");
            return;
        }
        $('#waybillWeight').val( convert2($('#waybillWeight').val(),1));
    });
    $('#waybillVolumeUp').on('click', function() {
        if(!regPos.test($('#waybillVolume').val())){
            Jd.alert(" 运单总体积输入格式不正确!");
            $('#waybillVolume').val("0.000000");
            return;
        }
        $('#waybillVolume').val( convert6($('#waybillVolume').val(),1));
    });
    $('#waybillWeightDown').on('click', function() {
        if(!regPos.test($('#waybillWeight').val())){
            Jd.alert(" 运单总重量输入格式不正确!");
            $('#waybillWeight').val("0.00");
            return;
        }
        if($('#waybillWeight').val() == '0.00'){
            $('#waybillWeightDown').disable();
        }
        $('#waybillWeight').val( convert2($('#waybillWeight').val(),-1));
    });
    $('#waybillVolumeDown').on('click', function() {
        if(!regPos.test($('#waybillVolume').val())){
            Jd.alert(" 运单总体积输入格式不正确!");
            $('#waybillVolume').val("0.000000");
            return;
        }
        if($('#waybillVolume').val() == '0.000000'){
            $('#waybillVolumeDown').disable();
        }
        $('#waybillVolume').val( convert6($('#waybillVolume').val(),-1));
    });
}

//保留两位小数
function convert2(number1Str,number2){
    var number1 = parseFloat(number1Str);
    var total = number1 + number2;
    return total.toFixed(2);
}
//保留六位小数
function convert6(number1Str,number2){
    var number1 = parseFloat(number1Str);
    var total = number1 + number2;
    return total.toFixed(6);
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

//展示所有包裹列表
function showAllPackage(){
    $('#packageDataTableDiv').css("display","block");
    $('#packageDataTable').bootstrapTable({
        url: '/weightAndVolumeCheckOfB2b/getPackage?waybillOrPackageCode=' + $('#waybillOrPackageCode').val(), // 请求后台的URL（*）
        method: 'get', // 请求方式（*）
        height: 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
        uniqueId: "ID", // 每一行的唯一标识，一般为主键列
        pagination: true, // 是否显示分页（*）
        cache: false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        sidePagination: "server", // 分页方式：client客户端分页，server服务端分页（*）
        striped: true, // 是否显示行间隔色
        sortable: true, // 是否启用排序
        sortOrder: "asc", // 排序方式
        clickToSelect: true, // 是否启用点击选中行
        singleSelect: true,
        columns: [{
            checkbox: true
        },{
            field: 'packageCode',
            title: '包裹号',
            align: 'center'
        }, {
            field: 'weight',
            title: '重量/公斤',
            align: 'center',
            editable: {
                type: 'text',
                emptytext:'待录入',
                validate: function (value) {
                    if ($.trim(value) == '') {
                        return '重量不能为空!';
                    }
                    if (isNaN(value)) {
                        return '重量必须是数字';
                    }
                    var reg = /(^[0-9]\d*(\.\d{1,2})?$)|(^[1-9]\d*$)/;
                    if(!reg.test(value)) {
                        return '重量小数点后最多2位';
                    }
                }
            }
        }, {
            field: 'length',
            title: '长/厘米',
            align: 'center',
            editable: {
                type: 'text',
                emptytext:'待录入',
                validate: function (value) {
                    if ($.trim(value) == '') {
                        return '长不能为空!';
                    }
                    if (isNaN(value)) {
                        return '重量必须是数字';
                    }
                    var reg = /(^[0-9]\d*(\.\d{1})?$)|(^[1-9]\d*$)/;
                    if(!reg.test(value)) {
                        return '长小数点后最多1位';
                    }
                }
            }
        }, {
            field: 'width',
            title: '宽/厘米',
            align: 'center',
            editable: {
                type: 'text',
                emptytext:'待录入',
                validate: function (value) {
                    if ($.trim(value) == '') {
                        return '宽不能为空!';
                    }
                    if (isNaN(value)) {
                        return '重量必须是数字';
                    }
                    var reg = /(^[0-9]\d*(\.\d{1})?$)|(^[1-9]\d*$)/;
                    if(!reg.test(value)) {
                        return '宽小数点后最多1位';
                    }
                }
            }
        }, {
            field: 'height',
            title: '高/厘米',
            align: 'center',
            editable: {
                type: 'text',
                emptytext:'待录入',
                validate: function (value) {
                    if ($.trim(value) == '') {
                        return '高不能为空!';
                    }
                    if (isNaN(value)) {
                        return '重量必须是数字';
                    }
                    var reg = /(^[0-9]\d*(\.\d{1})?$)|(^[1-9]\d*$)/;
                    if(!reg.test(value)) {
                        return '高小数点后最多1位';
                    }
                }
            }
        }, {
            field: 'isExcess',
            title: '是否超标',
            align: 'center',
            formatter: function (value, row, index) {
                return value == "1" ? "超标" : value == "0" ? "未超标" : "-";
            }
        }, {
            field: 'upLoadNum',
            title: '上传图片数量',
            align: 'center'
        }, {
            field: 'weightVolumeRatioCheck',
            title: '重泡比校验',
            align: 'center',
            formatter: function (value, row, index) {
                return "×";
            }
        }, {
            field: 'operate',
            title: '操作',
            align: 'center',
            formatter: function (value, row, index) {
                var flag = '<a class="search" href="javascript:void(0)" ><i class="glyphicon glyphicon-search"></i>&nbsp;查看&nbsp;</a>'
                        + '<a class="upLoad" href="javascript:void(0)" ><i class="glyphicon glyphicon-upload"></i>&nbsp;点击上传&nbsp;</a>';
                return flag;
            },
            events: {
                'click .upLoad': function(e, value, row, index) {
                    var rowIndex = this.parentNode.parentNode.rowIndex;
                    var isExcess = $('#packageDataTable')[0].rows[index+1].cells[6].innerHTML;
                    if(isExcess != '是'){
                        Jd.alert("超标才能上传!");
                        return;
                    }
                    layer.open({
                        id:'upExcessPicture',
                        type: 2,
                        title:'超标图片上传',
                        shadeClose: true,
                        shade: 0.7,
                        maxmin: true,
                        shadeClose: false,
                        area: ['1000px', '600px'],
                        content: toUploadUrl + "?waybillOrPackageCode=" + row.packageCode
                            + "&createSiteCode=" + $('#createSiteCode').val() + "&rowIndex=" + rowIndex + "&isWaybill=" + 0,
                        success: function(layero, index){
                        }
                    });
                },
                'click .search': function(e, value, row, index) {
                    var isExcess = $('#packageDataTable')[0].rows[index+1].cells[6].innerHTML;
                    if(isExcess != '是'){
                        Jd.alert("超标才能查看!");
                        return;
                    };
                    var count = $('#packageDataTable')[0].rows[index+1].cells[7].innerHTML;
                    if(count == 0){
                        Jd.alert("请先上传超标图片!");
                        return;
                    };
                    $.ajax({
                        type : "get",
                        url : searchExcessPictureUrl + "?waybillOrPackageCode=" + row.packageCode + "&siteCode=" + $('#createSiteCode').val(),
                        data : {},
                        async : false,
                        success : function (data) {
                            if(data && data.code == 200){
                                //在新窗口展示所有图片
                                var list = data.data;
                                for(var i=0;i<list.length;i++){
                                    if(list[i] == null || list[i] == ""){
                                        break;
                                    }
                                    window.open(list[i]);
                                }
                            }else{
                                Jd.alert(data.message);
                            }
                        }
                    });
                }
            }
        }],
        onEditableSave: function (field, row, oldValue, $el) {
            $('#packageDataTable').bootstrapTable('resetView');
        },
        responseHandler: function(result){
            if(result.code != 200){
                Jd.alert(result.message);
                return {
                    "total" : 0,
                    "rows" : []
                }
            }else {
                return {
                    "total" : result.data.length,
                    "rows" : result.data
                }
            }
        },
        onLoadError: function(){  //加载失败时执行
            Jd.alert("服务异常!");
        }
    });


}