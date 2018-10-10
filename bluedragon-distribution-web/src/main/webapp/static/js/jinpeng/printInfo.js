$(function() {

    var tableInit = function() {

        /*获得履约单下的所有信息，并循环添加到table中*/
        jQuery.ajax({
            type: 'GET',
            contentType : 'application/json',
            url : '/jinpeng/performance/printInfo?performanceCode='+$('#performanceCode').val()+'&waybillorPackCode='+$('#waybillorPackCode').val(),
            dataType : 'json',
            async : true,
            success : function(result) {
                if(result['code']!=200){
                    alert(result['message']);
                    return;
                }
                var data = JSON.parse(result['data']);

                var temp = "";
                var waybillRowIndex = 0;
                var waybillRowspan = 0;
                var boxRowIndex = 0;
                var boxRowspan = 0;
                var waybillCount = 0;
                var waybillSum = 0;
                var boxCount = 0;
                var boxSum = 0;

                for(var performance in data){
                    temp += "<tr><td colspan='8' style='font-size: 25px'>履约单号："+performance+"</td></tr>";
                    for(var waybill in data[performance]){
                        waybillCount++;
                        for(var boxCode in data[performance][waybill]){
                            boxCount++;
                            for(var sku in data[performance][waybill][boxCode]){

                                //item代表商品对象
                                var item = data[performance][waybill][boxCode][sku];
                                var itemSum = 0;
                                for(var i in item.map){
                                    itemSum++;
                                }

                                temp += "<tr>";

                                for(var skuMap in data[performance][waybill][boxCode][sku].map){

                                waybillRowIndex++;
                                boxRowIndex++;


                                if(waybillRowIndex == 1){
                                    waybillRowspan = 0;
                                    //获得一个运单下有多少商品，代表的是td标签中的rowSpan的值
                                    for(var boxCode1 in data[performance][waybill]){
                                        for(var sku1 in data[performance][waybill][boxCode1]){
                                            for(var i in data[performance][waybill][boxCode1][sku1].map){

                                                waybillRowspan++;
                                            }
                                        }
                                    }
                                    //获得一个履约单下有多少运单号
                                    for(var waybill1 in data[performance]){
                                        waybillSum++;
                                    }
                                    temp += "<td width='120px' rowspan='"+waybillRowspan+"'>"+waybillCount+"/"+waybillSum+"</br>运单号:"+waybill+"</td>";
                                    waybillSum=0;
                                }
                                if(waybillRowIndex == waybillRowspan){
                                    waybillRowIndex = 0;
                                }

                                if(boxRowIndex == 1){
                                    boxRowspan = 0;
                                    //获得一个箱号下有多少商品，代表的是td标签中的rowSpan的值
                                    for (var sku1 in data[performance][waybill][boxCode]) {
                                        for(var i in data[performance][waybill][boxCode][sku1].map){

                                            boxRowspan++;
                                        }
                                    }
                                    //获得一个运单下有多少箱号
                                    for(var box in data[performance][waybill]){
                                        boxSum++;
                                    }
                                    temp += "<td width='120px' rowspan='"+boxRowspan+"'>"+boxCount+"/"+boxSum+"</br>箱号:"+boxCode+"</td>";
                                    boxSum=0;
                                }
                                if(boxRowIndex == boxRowspan){
                                    boxRowIndex = 0;
                                }
                                }
                                for(var k in item.map){
                                    temp += "<td rowspan='"+itemSum+"' width='120px' style='text-align: left;border-right-style: none'>SKU："+item.skuId+"</td>"+
                                        "<td width='200px' rowspan='"+itemSum+"' style='text-align: left;border-right-style: none;border-left-style: none'>"+item.skuName+"</td>"+
                                        "<td rowspan='"+itemSum+"' width='120px' style='text-align: left;border-right-style: none'>PO："+(item.poNo==null?"——":item.poNo)+"</td>"+
                                        //start
                                    "<td width='150px' style='text-align: left;border-right-style: none'>子单号："+k+"</td>"+
                                    "<td width='60px' style='text-align: left;border-left-style: none'>"+item.map[k]+"件</td>"+
                                        //end
                                    "<td rowspan='"+itemSum+"' width='60px' style='text-align: left'>"+item.skuNum+"件</td>";
                                    delete item.map[k];
                                    break;
                                }
                                temp += "</tr>";

                                if(itemSum>1){
                                    for(var k in item.map){
                                        temp += "<tr>" +
                                            "<td width='120px' style='text-align: left;border-right-style: none'>子单号："+k+"</td>" +
                                            "<td width='60px' style='text-align: left;border-left-style: none'>"+item.map[k]+"件</td>";
                                        "</tr>";
                                    }
                                }
                        }
                        }
                        boxCount=0;
                    }
                    waybillCount=0;
                }

                $("#printInfo").html(temp);
            }

        });
    };

    tableInit().init();

});
