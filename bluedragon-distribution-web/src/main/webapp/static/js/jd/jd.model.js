/**
 * Created by IntelliJ IDEA.
 * User: ole
 * Date: 11-6-16
 * Time: 下午2:25
 */
Jd.importCss('model/jd.model.css');   //replace it with the path of "qt.model.css" in your project
(function(Jd) {
    var isIE=!-[1,];
    var isIE6=isIE&&!window.XMLHttpRequest;
    /**
     * show dom without callback function
     * @param dom
     */
    Jd.show = function(dom) {
        var dialog = new Jd.Dialog({
                    innerDom:dom,
                    closeBtn:true,
                    close:'hide',
                    cur:true,
                    drag:true
                });
        dialog.show();
    }

    /**
     * show dom with a simple wrapper
     * @param conf
     */
    Jd.WrappedDialog = function(conf) {
        var div = document.createElement('div');
        div.setAttribute("class","qt_wrapper");
        if (conf.innerDom) {
            if(!isIE6){
                $(div).append('<span class="c1"></span><span class="c2"></span><span class="c3"></span><span class="c4"></span>'
                        + '<span class="c6"></span><span class="c7"></span><span class="c8"></span><span class="c9"></span>');
            }else{
                $(div).css({border:'5px solid #CCCCCC'})
            }
            $(div).append(conf.innerDom);
            conf.innerDom = div;
        }
        return new Jd.Dialog(conf);
    }
    /**
     * message alert..
     * @param _c  //message
     * @param _s  //type
     * @param handler //callback function
     */
    var type_map = {'warn':'error','info':'info','error':'error','success':'success','confirm':'confirm'};
    Jd.alert = function(_c, _s, handler) {
        var c = {type:'info',btntxt:'确定',closeBtn:false};
        if(_c.content){
            c = $.extend(c,_c);
        }else{
            c.content = _c;
            c.type = _s?_s:c.type;
            c.handle = handler;
        }
        c.type = type_map[_s]?type_map[_s]:'info';
        var href = isIE6?'###':'javascript:void(0)';
        var dom = document.createElement('div');
        $(dom).addClass('qt_alert')
            .append('<table cellpadding="0px" cellspacing="0px" width="320px"><tr><td align="right"><span class="ico ico_' + c.type + '">&nbsp;</span></td>' +
                '<td width="270px">' + c.content + '</td></tr></table>'
                +'<div style="text-align:center;margin-top:10px;"><a class="qt_btn" href="' + href + '">' + c.btntxt + '</a></div>');
        var dialog = new Jd.WrappedDialog({
            id:c.content,
            innerDom : dom,
            cur : true,
            closeBtn:c.closeBtn
        });
        dialog.show();
        dialog.find('.qt_btn').unbind().bind('click', function() {
            handler && handler();
            dialog.close();
        });
    }
    Jd.confirm = function(_c, handler, param) {
        var c = {ybtntxt:'是',nbtntxt:'否',closeBtn:false,type:'confirm'};
        if(_c.content){
            c = $.extend(c,_c);
        }else{
            c.content = _c;
            c.handle = handler;
            c.param = param;
        }
        var href = isIE6?'###':'javascript:void(0)';
        var dom = document.createElement('div');
        $(dom).addClass('qt_alert')
              .append('<table cellpadding="0px" cellspacing="0px" width="320px"><tr><td align="right"><span class="ico ico_' + c.type + '">&nbsp;</span></td>' +
                '<td width="270px">' + c.content + '</td></tr></table>'
                +'<div style="text-align:center;margin-top:10px;"><a class="qt_btn" href="' + href + '">' + c.ybtntxt + '</a>' +
                '<a style="margin-left: 25px" class="qt_btn" href="' + href + '">' + c.nbtntxt + '</a></div>');
        var dialog = new Jd.WrappedDialog({
            id:c.content,
            innerDom : dom,
            cur : true,
            closeBtn:c.closeBtn
        });
        dialog.show();
        dialog.find('.qt_btn').first().unbind().bind('click', function() {
            dialog.close();
            c.handle(true, c.param);
        });
        dialog.find('.qt_btn').last().unbind().bind('click', function() {
            dialog.close();
            c.handle(false, c.param);
        });
    }
    /**
     * A tip for error msg
     * @param dom
     * @param content
     * @param ob
     */
    var _TIP_LOG = {};
    Jd.errTip = function(dom,content,ob,bindTo,zIndex){
        dom = bindTo?bindTo:dom;
        dom = typeof dom == 'object'?dom:$('#' + dom)[0];
        var token = dom.id?dom.id:assgin(dom);
        if(_TIP_LOG[token] && _TIP_LOG[token] == content){
            return;
        }
        _TIP_LOG[token] = content;
        var div = document.createElement('div');
        $(div).append('<i></i><span>' + content + '</span>').addClass('qt_err');
        Jd.tip({
            model:'multi',
            token:token,
            obj:dom,
            closeHandler:[],
            innerDom:div,
            zIndex:zIndex,
            ob:ob
        });
    }
    /**
     * A tip for success msg
     * @param dom
     * @param content                                                           
     * @param ob
     */
    Jd.succTip = function(dom,ob,bindTo,zIndex){
        dom = bindTo?bindTo:dom;
        dom = typeof dom == 'object'?dom:$('#' + dom)[0];
        var token = dom.id?dom.id:assgin(dom);
        if(_TIP_LOG[token] && _TIP_LOG[token] == 1){
            return;
        }
        _TIP_LOG[token] = 1;
        var div = document.createElement('div');
        $(div).append('<i></i><strong><span>&nbsp;</span></strong>').addClass('qt_r');
        Jd.tip({
            model:'multi',
            token:token,
            obj:dom,
            closeHandler:[],
            innerDom:div,
            zIndex:zIndex,
            ob:ob
        });
    }
    Jd.clearTip = function(dom,bindTo){
        dom = typeof dom == 'object'?dom:$('#' + dom)[0];
        dom = bindTo?bindTo:dom;
        var token = dom.id?dom.id:assgin(dom);
          if(_TIP_LOG[token] && _TIP_LOG[token] == -1){
            return;
        }
        _TIP_LOG[token] = -1;
        var div = document.createElement('div');
        $(div).append(' ').addClass('qt_r');
        Jd.tip({
            model:'multi',
            token:token,
            obj:dom,
            closeHandler:[],
            zIndex:1,
            innerDom:div
        });
    }

    function assgin(dom){
        var sign = dom.name + dom.id + dom.tagName;
        !dom.sign && (dom.sign = sign + new Date().getTime() + ':' + Math.random());
        return dom.sign;
    }
})(Jd)
