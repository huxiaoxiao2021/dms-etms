$(function() {

    jQuery.ajax({
        type: 'GET',
        contentType : 'application/json',
        url : "/signReturn/printInfo?waybillCode="+$('#waybillCode').val()+"&waybillCodeInMerged="+$('#waybillCodeInMerged').val(),
        dataType : 'json',
        async : true,
        success : function(result) {

            //TODO 填充主表的数据
            var temp = "";
            temp+="<td>"+result['mergedWaybillCode']+"</td>";
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
            var map = result['map'];
            var t = 0;
            temp += "<tr>";
            for(var key in map){
                t++;
                if(t%2 == 1){
                    temp += "<tr>";
                }
                // temp += "<td>"+key+"</td><td>"+map[key]+"</td>";
                temp += "<td>"+key+"</td><td>"+$.dateHelper.formateDateTimeOfTs(map[key])+"</td>";
                if(t%2 == 0){
                    temp += "</tr>";
                }
            }

            $('#printInfoD').html(temp);
        }
    });
    window.print();
});
