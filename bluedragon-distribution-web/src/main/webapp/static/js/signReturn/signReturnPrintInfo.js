$(function() {

    jQuery.ajax({
        type: 'GET',
        contentType : 'application/json',
        url : "/signReturn/printInfo?newWaybillCode="+$('#newWaybillCode').val()+"&waybillCode="+$('#waybillCode').val(),
        dataType : 'json',
        async : true,
        success : function(result) {

            //填充主表的数据
            var temp = "";
            temp+="<td>"+(result['newWaybillCode'] == null ?"——" :result['newWaybillCode'])+"</td>";
            temp+="<td>"+(result['busiId'] == null ?"——":result['busiId'])+"</td>";
            temp+="<td>"+(result['busiName'] == null ? "——":result['busiName'])+"</td>";
            temp+="<td>"+(result['returnCycle']==null?"——":result['returnCycle'])+"</td>";
            temp+="<td>"+($.dateHelper.formateDateOfTs(result['operateTime'])==null?"——":$.dateHelper.formateDateOfTs(result['operateTime']))+"</td>";
            temp+="<td>"+(result['createSiteName']==null?"——":result['createSiteName'])+"</td>";
            temp+="<td>"+(result['operateUser']==null?"——":result['operateUser'])+"</td>";
            temp+="<td>"+(result['mergeCount']==null?"——":result['mergeCount'])+"</td>";
            $('#printMain').html(temp);

            //填充明细表的数据
            temp = "";
            temp+= "<tr><td>运单号</td><td>妥投时间</td><td>运单号</td><td>妥投时间</td></tr>";

            var list = result['mergedWaybillList'];
            var t = 0;
            temp += "<tr>";
            for(var key in list){
                t++;
                if(t%2 == 1){
                    temp += "<tr>";
                }
                temp += "<td>"+(list[key]['waybillCode']==null?"——":list[key]['waybillCode'])+"</td><td>"+($.dateHelper.formateDateTimeOfTs(list[key]['deliveredTime'])==null?"——":$.dateHelper.formateDateTimeOfTs(list[key]['deliveredTime']))+"</td>";
                if(t%2 == 0){
                    temp += "</tr>";
                }
                if(t==list.length && t%2==1){
                temp += "<td>——</td><td>——</td>";
                }
            }

            $('#printInfoD').html(temp);
            window.print();
        }
    });

});
