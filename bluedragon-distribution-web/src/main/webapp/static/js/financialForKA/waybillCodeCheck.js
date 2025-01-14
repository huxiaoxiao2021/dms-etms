$(function() {

    //成功提示音地址
    var successBeep = "/static/music/success.wav";
    //失败提示音地址
    var failBeep= "/static/music/fail.wav";

    //条码相同提示语隐藏
    $("#successMessage").hide();

    //焦点聚集在输入框1
    $('#barCodeOfOne').focus();
    //输入框1回车事件后焦点聚集在输入框2
    $('#barCodeOfOne').keydown(function (event) {
        if(event.keyCode == 13){
            $("#successMessage").hide();
            $('#barCodeOfTwo').focus();
        };
    });

    //输入框2回车事件后校验输入的条码
    $('#barCodeOfTwo').keydown(function (event) {
        if(event.keyCode == 13){
            var params={};
            var barCodeOfOne=$('#barCodeOfOne').val();
            var barCodeOfTwo=$('#barCodeOfTwo').val();
            if(barCodeOfOne && barCodeOfTwo){
                params.barCodeOfOne=barCodeOfOne;
                params.barCodeOfTwo=barCodeOfTwo;
                params.operateSiteCode = $('#operateSiteCode').val();
                params.operateSiteName = $('#operateSiteName').val();
                params.operateErp = $('#operateErp').val();
            }else{
                Jd.alert("请输入条码!");
                return;
            }
            var url = '/waybillCodeCheckForKA/check';
            $.ajaxHelper.doPostAsync(url, JSON.stringify(params), function (res) {
                if (res && res.code !=200) {
                    //条码不同，提示音错误警告
                    playSound(successBeep);
                    //输入框2失去焦点
                    $('#barCodeOfTwo').blur();
                    //弹框提示
                    var aaa = "<font style='color: red;font-size: 25px'>"+res.message+"</font>";
                    var sss = "<font style='font-size: 25px;'>"+res.message+"</font>";
                    if(res.code == 600){
                        layer.open({
                            title: '提示',
                            shadeClose: true,
                            shade: 0.7,
                            area: ['350px', '200px'],
                            content: aaa,
                            btn: ['确定'],
                            yes: function(index){
                                $('#barCodeOfOne').val("");
                                $('#barCodeOfTwo').val("");
                                $('#barCodeOfOne').focus();
                                layer.close(index);
                            },
                        });
                    }else {
                        layer.open({
                            title: '提示',
                            shadeClose: true,
                            shade: 0.7,
                            area: ['350px', '200px'],
                            content: sss,
                            btn: ['确定'],
                            yes: function(index){
                                $('#barCodeOfOne').val("");
                                $('#barCodeOfTwo').val("");
                                $('#barCodeOfOne').focus();
                                layer.close(index);
                            },
                        });
                    }
                }else{
                    //条码相同提示语隐藏
                    $("#successMessage").show();
                    //条码相同，提示音成功
                    playSound(failBeep);
                    //光标聚集在输入框1并清空
                    $('#barCodeOfOne').val("");
                    $('#barCodeOfTwo').val("");
                    $('#barCodeOfOne').focus();
                }
            });
        };
    });

    //报警提示音
    function playSound(src) {
        var auto = $("#auto");
        auto.attr("src",src);
    }

});
