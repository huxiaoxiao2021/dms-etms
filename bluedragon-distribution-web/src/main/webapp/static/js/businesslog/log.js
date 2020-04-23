$(document).ready(function () {

    document.getElementById("startTime").value = DateUtil.formatDateTime(new Date(nowTimeAddDay(-29)));
    document.getElementById("endTime").value = DateUtil.formatDateTime(new Date());
    //根据biztype获取operatetype
    function getOperateType(bizTypeCode) {
        // console.log(bizTypeCode)
        jQuery.ajax({
            type: "POST",
            url: "/businesslog/getOpeBySysAndBiz",
            data: {systemCode: 112, bizTypeCode: bizTypeCode},
            success: function (msg) {
                if (msg.code == 200) {
                    $("#operatetype").empty()

                    var keys = Object.keys(msg.data);
                    var defaultnull = $("<option value=''>-</option>");

                    $("#operatetype").append(defaultnull)

                    jQuery.each(keys, function (index, item) {
                        console.log(item + ":" + msg.data[item])
                        var name = msg.data[item];
                        var optionitem = $("<option value="+item+">"+name+"</option>");
                        $("#operatetype").append(optionitem)
                    })
                }
            }
        });
    }
    var p = new Paging();
    function getlogs(offset, limit) {
        if (offset + limit > 15000) {
            alert("只能查询前10000条数据");
            return;
        }
        $("#showloading").show();
        var showRequest = $('#showRequest').prop("checked");
        if(showRequest){
            $('#operateRequest').attr("style","display:;");
            $('#operateResponse').attr("style","display:;");
        }else {
            $('#operateRequest').attr("style","display:none;");
            $('#operateResponse').attr("style","display:none;");
        }

        var waybillCode = $("#waybillCode").val();
        var packageCode = $("#packageCode").val();
        var boxCode = $("#boxCode").val();
        var sendCode = $("#sendCode").val();
        var siteCode = $("#siteCode").val();
        var siteName = $("#siteName").val();
        var operatorName = $("#operatorName").val();
        var sourceSys = $("#waybillCode").val();
        var bizType = $("#biztype").val();
        var operateType = $("#operatetype").val();
        var serverIp = $("#serverIp").val();
        var otherKey = $("#otherKey").val();
        var startTime = $("#startTime").val();
        var endTime = $("#endTime").val();
        var orderByField = $("#orderByField").val();
        var orderBy = $("#orderBy").val();

        if (startTime==''){
            startTime='1970-01-01 08:00:00'
        }
        if(endTime==''){
            endTime='9999-12-31 00:00:00'
        }


        $.ajax({
            type: "POST",
            url: "/businesslog/getBusinessLog",
            contentType: "application/json;charset=utf-8",
            data: JSON.stringify({
                waybillCode: waybillCode,
                packageCode: packageCode,
                boxCode: boxCode,
                sendCode: sendCode,
                siteCode: siteCode,
                siteName: siteName,
                operatorName: operatorName,
                sourceSys: sourceSys,
                bizType: bizType,
                operateType: operateType,
                serverIp: serverIp,
                otherKey: otherKey,
                startTime: startTime,
                endTime: endTime,
                orderByField: orderByField,
                orderBy: orderBy,
                offset: offset,
                limit: limit
            }),
            dataType: "json",
            success: function (message) {
                $("#logcontent").empty();
                $("#showloading").hide();

                if (message.statusCode == 200) {
                    var data = message.rows;
                    jQuery.each(data, function (index, item) {
                        var bizTypeName = item.bizTypeName == null ? "-" : item.bizTypeName;
                        var operateTypeName = item.operateTypeName == null ? "-" : item.operateTypeName;
                        var waybillCode = item.waybillCode == null ? "-" : item.waybillCode;
                        var packageCode = item.packageCode == null ? "-" : item.packageCode;
                        var boxCode = item.boxCode == null ? "-" : item.boxCode;
                        var sendCode = item.sendCode == null ? "-" : item.sendCode;
                        var operatorName = item.operatorName == null ? "-" : item.operatorName;
                        var siteCode = item.siteCode == null ? "-" : item.siteCode;
                        var siteName = item.siteName == null ? "-" : item.siteName;
                        var timeStamp = item.timeStamp == null ? "-" : item.timeStamp;
                        var responseCode = item.responseCode == null ? "-" : item.responseCode;
                        var responseMessage = item.responseMessage == null ? "-" : item.responseMessage;
                        var operateRequest = item.operateRequest == null ? "-" : item.operateRequest;
                        var operateResponse = item.operateResponse == null ? "-" : item.operateResponse;

                        var showRequestTd = '<td style="max-width: 450px;word-wrap:break-word;word-break:break-all;">'+operateRequest+'</td>'
                                +'<td style="max-width: 450px;word-wrap:break-word;word-break:break-all;">'+operateResponse+'</td>';
                        if(!showRequest){
                            showRequestTd = '';
                        }
                        var tr = $('<tr>'
                                +'<td>'+(index+1)+'</td>'
                                +'<td>'+bizTypeName+'</td>'
                                +'<td>'+operateTypeName+'</td>'
                                +'<td>'+waybillCode+'</td>'
                                +'<td>'+packageCode+'</td>'
                                +'<td>'+boxCode+'</td>'
                                +'<td>'+sendCode+'</td>'
                                +'<td>'+operatorName+'</td>'
                                +'<td>'+siteCode+'</td>'
                                +'<td>'+siteName+'</td>'
                                +'<td>'+timeStamp+'</td>'
                                +'<td>'+responseCode+'</td>'
                                +'<td>'+responseMessage+'</td>'
                                + showRequestTd
                                +'</tr>');
                        console.log(tr);
                        $("#logcontent").append(tr)
                    });

                    if (offset == 0 && $("#pageTool").children().length != 0) {
                        p.render({
                            count: message.total,
                            pagesize: limit,
                            current: 1
                        });
                        $("#dataTotal").text("  共"+message.total+"条");
                    }
                    if ($("#pageTool").children().length == 0) {
                        //分页第一次加载
                        p.init({
                            target: '#pageTool',
                            pagesize: 10,
                            count: message.total,
                            current: 1,
                            toolbar: true,
                            changePagesize: function (pagesize) {
                                getlogs(0, parseInt(pagesize, 10));
                            },
                            callback: function (pagecount, size, count) {
                                // console.log(pagecount, size, count)
                                getlogs((pagecount - 1) * size, parseInt(size, 10));
                            }
                        });
                        $("#dataTotal").text("  共"+message.total+"条");
                    }

                } else {
                    alert(message.statusMessage)
                }


            },
            error: function (message) {

            }
        });
    }

    //获取biztype
    jQuery.ajax({
        type: "POST",
        url: "/businesslog/getBizTypeConfigBySystemCode",
        data: {systemCode: 112},
        success: function (msg) {
            if (msg.code == 200) {
                var keys = Object.keys(msg.data);

                $("#biztype").empty();

                var defaultnull1 = $("<option value=''>-</option>");
                $("#biztype").append(defaultnull1)

                jQuery.each(keys, function (index, item) {
                    var name = msg.data[item];
                    var optionitem = $("<option value="+item+">"+name+"</option>");
                    $("#biztype").append(optionitem)
                })
            }
        }
    });

    //biztype变更
    $("#biztype").change(function (e) {
        var bizTypeCode = $(this).val();
        getOperateType(bizTypeCode)
    })

    $("#search").click(function () {
        var waybillCode = $("#waybillCode").val();
        var packageCode = $("#packageCode").val();
        var boxCode = $("#boxCode").val();
        var sendCode = $("#sendCode").val();
        var siteCode = $("#siteCode").val();
        var siteName = $("#siteName").val();
        var operatorName = $("#operatorName").val();
        var sourceSys = $("#waybillCode").val();
        var bizType = $("#biztype").val();
        var operateType = $("#operatetype").val();
        if(startTime == ''){
            alert("请输入开始时间！");
            return;
        }
        if(endTime == ''){
            alert("请输入结束时间！");
            return;
        }
        if(endTime < startTime){
            alert("开始时间必须大于结束时间！");
            return;
        }
        var limitDay = 30;
        if(dateDiffDay(startTime,endTime) > limitDay){
            alert("开始时间与结束时间跨度不能超过"+limitDay+"天！");
            return;
        }
        getlogs(0, 10);
    })

})

function dateDiffDay(sDate1, sDate2) {
    var startDate = sDate1.split(" ")[0];
    var endDate = sDate2.split(" ")[0];
    var difValue = (new Date(endDate) - new Date(startDate)) / (1000 * 60 * 60 * 24);
    return difValue;
}

function nowTimeAddDay(day){

    var date1 = new Date();
    var date2 = new Date(date1);
    date2.setDate(date1.getDate()+day);
    var month = (date2.getMonth()+1) + "";
    if(month.length == 1){
        month ='0'+month;
    }

    var time2 = date2.getFullYear()+"-"+month+"-"+date2.getDate();
    return time2;
}