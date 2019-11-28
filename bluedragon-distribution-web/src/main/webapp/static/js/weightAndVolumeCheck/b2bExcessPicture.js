$(function() {

    var url = '/weightAndVolumeCheck/searchB2bExcessPicture?waybillCode='+$('#waybillCode').val()
        +'&siteCode='+$('#siteCode').val() +'&isWaybillSpotCheck='+$('#isWaybillSpotCheck').val();
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

                var data = JSON.parse(result.data);
                for(var packageCode in data){
                    temp += "<tr><td rowspan='5' style='font-size: 20px'>"+packageCode+"</a></td>";
                    var excessPictureUrl = data[packageCode][0];
                    temp += "<td><a href="+excessPictureUrl+" target='_blank'>"+excessPictureUrl+"</a></td>";
                    temp += "</tr>";
                    for(var i=1;i<data[packageCode].length;i++){
                        excessPictureUrl = data[packageCode][i];
                        temp += "<tr><td><a href="+excessPictureUrl+" target='_blank'>"+excessPictureUrl+"</a></td></tr>";
                    }
                }

                $("#b2bExcessPicture").html(temp);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Jd.alert("服务异常!");
                return;
            }
        });
    };

    tableInit();

});
