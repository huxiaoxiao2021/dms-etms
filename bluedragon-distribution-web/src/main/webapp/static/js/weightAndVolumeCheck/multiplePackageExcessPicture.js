$(function() {

    var waybillCode = $('#waybillCode').val()
    var url = '/weightAndVolumeCheck/searchPicture4MultiplePackage?waybillCode='+waybillCode
        +'&siteCode='+$('#siteCode').val() +'&pageNo='+$('#pageNo').val()+'&pageSize='+$('#pageSize').val();
    var tableInit = function() {

        /*获得B网抽检图片链接，并循环添加到table中*/
        $.ajax({
            type : "get",
            url : url,
            data : {},
            async : false,
            success : function (result) {
                if(result.code!=200){
                    Jd.alert(result.message);
                    return;
                }

                var temp = "<tr><td  style='font-size: 25px'>包裹号</td><td  style='font-size: 25px'>超标图片链接</td></tr>";

                var data = result.data;
                var dataList = data.data
                for(var url of dataList){
                    temp += "<tr><td rowspan='5' style='font-size: 20px'>"+waybillCode+"</a></td>";
                    temp += "<td><a href="+url+" target='_blank'>"+url+"</a></td>";
                }

                $("#excessPicture").html(temp);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Jd.alert("服务异常!");
                return;
            }
        });
    };

    tableInit();

});
