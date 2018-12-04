$(function() {

    jQuery.ajax({
        type: 'GET',
        contentType : 'application/json',
        url : "/signReturn/printInfo?newWaybillCode="+$('#newWaybillCode').val()+"&waybillCode="+$('#waybillCode').val(),
        dataType : 'json',
        async : true,
        success : function(result) {

            //TODO 填充主表的数据
            var temp = "";
            temp+="<td>"+result['newWaybillCode']+"</td>";
            temp+="<td>"+result['busiId']+"</td>";
            temp+="<td>"+result['busiName']+"</td>";
            temp+="<td>"+result['returnCycle']+"</td>";
            temp+="<td>"+$.dateHelper.formateDateOfTs(result['operateTime'])+"</td>";
            temp+="<td>"+result['orgId']+"</td>";
            temp+="<td>"+result['operateUser']+"</td>";
            temp+="<td>"+result['mergeCount']+"</td>";
            $('#printMain').html(temp);

            //TODO  填充明细表的数据
            temp = "";
            temp+= "<tr><td>运单号</td><td>妥投时间</td><td>运单号</td><td>妥投时间</td></tr>";

            debugger;
            var list = result['mergedWaybillList'];
            var t = 0;
            temp += "<tr>";
            for(var key in list){
                t++;
                if(t%2 == 1){
                    temp += "<tr>";
                }
                temp += "<td>"+list[key]['waybillCode']+"</td><td>"+$.dateHelper.formateDateTimeOfTs(list[key]['deliveredTime'])+"</td>";
                if(t%2 == 0){
                    temp += "</tr>";
                }
            }

            $('#printInfoD').html(temp);
        }
    });
    window.print();
});
