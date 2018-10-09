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
                    temp += "<tr><td colspan='8' style='font-size: 30px'>履约单号："+performance+"</td></tr>";
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
                                    temp += "<td rowspan='"+waybillRowspan+"'>"+waybillCount+"/"+waybillSum+"</br>运单号:"+waybill+"</td>";
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
                                    temp += "<td rowspan='"+boxRowspan+"'>"+boxCount+"/"+boxSum+"</br>箱号:"+boxCode+"</td>";
                                    boxSum=0;
                                }
                                if(boxRowIndex == boxRowspan){
                                    boxRowIndex = 0;
                                }
                                }
                                for(var k in item.map){
                                    temp += "<td rowspan='"+itemSum+"' width='200px' style='text-align: left;border-right-style: none'>SKU："+item.skuId+"</td>"+
                                        "<td width='300px' rowspan='"+itemSum+"' style='text-align: left;border-right-style: none;border-left-style: none'>"+item.skuName+"</td>"+
                                        "<td rowspan='"+itemSum+"' width='150px' style='text-align: left;border-right-style: none'>PO："+(item.poNo==null?"——":item.poNo)+"</td>"+
                                        //start
                                    "<td width='250px' style='text-align: left;border-right-style: none'>子单号："+k+"</td>"+
                                    "<td width='70px' style='text-align: left;border-left-style: none'>"+item.map[k]+"件</td>"+
                                        //end
                                    "<td rowspan='"+itemSum+"' width='70px' style='text-align: left'>"+item.skuNum+"件</td>";
                                    delete item.map[k];
                                    break;
                                }
                                temp += "</tr>";

                                if(itemSum>1){
                                    for(var k in item.map){
                                        temp += "<tr>" +
                                            "<td width='250px' style='text-align: left;border-right-style: none'>子单号："+k+"</td>" +
                                            "<td width='70px' style='text-align: left;border-left-style: none'>"+item.map[k]+"件</td>";
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
                printInfo();
            }

        });

        function printInfo () {
            /*将页面上的table打印成pdf*/
            html2canvas(document.body, {
                onrendered:function(canvas) {

                    var contentWidth = canvas.width;
                    var contentHeight = canvas.height;

                    //一页pdf显示html页面生成的canvas高度;
                    var pageHeight = contentWidth / 595.28 * 841.89;
                    //未生成pdf的html页面高度
                    var leftHeight = contentHeight;
                    //页面偏移
                    var position = 0;
                    //a4纸的尺寸[595.28,841.89]，html页面生成的canvas在pdf中图片的宽高
                    var imgWidth = 595.28;
                    var imgHeight = 595.28/contentWidth * contentHeight;

                    var pageData = canvas.toDataURL('image/jpeg', 1.0);

                    var pdf = new jsPDF('', 'pt', 'a4');

                    //有两个高度需要区分，一个是html页面的实际高度，和生成pdf的页面高度(841.89)
                    //当内容未超过pdf一页显示的范围，无需分页
                    if (leftHeight < pageHeight) {
                        pdf.addImage(pageData, 'JPEG', 20, 0, imgWidth, imgHeight );
                    } else {
                        while(leftHeight > 0) {
                            pdf.addImage(pageData, 'JPEG', 20, position, imgWidth, imgHeight)
                            leftHeight -= pageHeight;
                            position -= 841.89;
                            //避免添加空白页
                            if(leftHeight > 0) {
                                pdf.addPage();
                            }
                        }
                    }
                    pdf.save('content.pdf');
                }
            });
        }

    };

    tableInit().init();

});
