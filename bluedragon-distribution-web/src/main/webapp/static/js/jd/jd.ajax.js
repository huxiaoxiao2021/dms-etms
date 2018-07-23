(function ($) {
    var topDoc;
    if (parent&&parent.document&&parent.document.getElementById("topFrame")){
        topDoc=parent.document.getElementById("topFrame").contentDocument;
    }
    var currUserCode;
    //获取当前网址
    var curWwwPath = window.document.location.href;
    //获取主机地址之后的目录
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    //获取主机地址，如： http://localhost:8083
    var localhostPath = curWwwPath.substring(0, pos);
    var quitUrl = localhostPath + "/quit";
    if (topDoc) {
        var currUserCode = topDoc.getElementById("currUserCode").textContent;
        if (!currUserCode) {
            alert("用户信息加载异常，请重新登录");
            parent.location.href = quitUrl;
        }
    } else {
        alert("用户信息加载失败，请重新登录");
        parent.location.href = quitUrl;
    }
    //首先备份下jquery的ajax方法
    var _ajax = $.ajax;

    //重写jquery的ajax方法
    $.ajax = function (opt) {
        //备份opt中error和success方法
        var fn = {
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            success: function (data, textStatus) {
            }
        }
        if (opt.error) {
            fn.error = opt.error;
        }
        if (opt.success) {
            fn.success = opt.success;
        }

        //扩展增强处理
        var _opt = $.extend(opt, {
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                fn.error(XMLHttpRequest, textStatus, errorThrown);
            },
            success: function (data, textStatus) {
                fn.success(data, textStatus);
            },
            beforeSend: function (request) {
                request.setRequestHeader("currusercode", currUserCode);
            },
            complete: function (XHR, TS) {
                //请求完成后回调函数 (请求成功或失败之后均调用)。
                if (XHR.status = 888) {
                    parent.location.href = quitUrl;
                }
            }
        });
        return _ajax(_opt);
    };
})(jQuery);