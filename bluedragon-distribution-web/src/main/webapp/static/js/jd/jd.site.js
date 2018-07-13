/**
 * 监控-站点&B商家选择控件
 * Created by IntelliJ IDEA.
 * User: lijiale
 * Date: 12-9-18
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */

(function($) {
    var JConsole = {} ,debug = false,button;
    JConsole.info = function(msg) {
        if ($.browser.mozilla && debug) {
            console.info(msg);
        } else if(debug){
            if (!button) {
                button = D.createElement('input');
                button.type = 'button';
                button.value='clear..';
                $(button).click(function() {
                    $('.console_info').remove();
                });
                D.body.appendChild(button);
            }
            var span = D.createElement('div');
            span.innerHTML = msg;
            $(span).addClass('console_info');
            D.body.appendChild(span);
        }
    }


    var cfg = {value:'siteCode',head:{name:'siteName',code:'siteCode'},url:'/common/getSiteByName',letterName:true}
//    var cfg = {value:'siteCode',siteCode:'',siteName:''}
    var D = document;
    $.fn.siteInput = function(setting) {
        JConsole.info('init...')
        $.extend(cfg,setting);
        _init(this[0],cfg);
    }

    function _init(obj,opt) {
        var size = Jd.getSize(obj), pos = Jd.getPosition(obj);
        var div = D.createElement('div');
        $(div).css({
                    position:'absolute',
                    width:size.w,
                    height:size.h,
                    left:pos.x + 'px',
                    top:pos.y + 'px'
                }).addClass('siteInput');

        var ul = D.createElement('ul'),input = D.createElement('input');
        $(input).attr('type', 'text').width(size.w);
        $(div).append(input).append(ul);
        window.attachEvent ? input.attachEvent('onpropertychange', function(event) {
            event = event || window.event;
            JConsole.info('onpropertychange....');
            JConsole.info('this tagName....' + this.tagName);
            JConsole.info('this srcElement....' + event.srcElement.tagName);
            completeSite.call(this.tagName ? this :  event.srcElement, false)
        }) : input.addEventListener('input', function(event) {
            completeSite.call(this, false)
        }, false);
        D.body.appendChild(div);
        input.s_div = div;
        input.s_render = obj;
        input.s_size = size;
        input.opt = opt;
        $(input).blur(blurHandler).focus(completeSite).keydown(keySelect);
        $(obj).parents('form').submit(function(){blurHandler.call(input)});
        var val = $.trim($(obj).val());
        if (val) {
            $(input).val(val);
            completeSite.call(input, true);
        }
    }

    function keySelect(e) {
        e = e || window.event;
        if (e.keyCode == 38 || e.keyCode == 40) {
            select.call(this, e.keyCode);
        } else if (e.keyCode == 13) {
            setVal.call(this);
        }
    }

    function select(i) {
        var ul = $(this.s_div).find('ul');
        var sel = ul.find('.sel_li'),list = ul.find('li'),nxt,prv,result;
        JConsole.info('select....');
        JConsole.info('li length:' + list.length);
        if (list.length == 0) {
            return false;
        }
        nxt = sel.length > 0 ? sel.next() : list.first(),prv = sel.length > 0 ? sel.prev() : list.last();
        switch (i) {
            case 40:
                result = nxt.length > 0 ? nxt : list.first();
                break;
            case 38:
                result = prv.length > 0 ? prv : list.last();
                break;
        }
        JConsole.info('select result:' + result);
        sel.removeClass('sel_li');
        result.addClass('sel_li');
    }

    function blurHandler() {
        if (!hover) {
            JConsole.info('blur....')
            completeSite.call(this, true)
        }
    }

    var siteData = {};
    var n_reg = /^[0-9]+$/,c_reg = /^[a-zA-Z]*[\u4E00-\u9FA5]+.*$/,e_reg=/^[a-zA-Z]+[a-zA-Z0-9]*$/;
    function completeSite(blur) {
        JConsole.info('completeSite...')
        JConsole.info('this tageName:' + this.tagName);
        var val = jQuery.trim(this.value),obj = this;
        JConsole.info('value:' +val);
        JConsole.info('cfg.letterName:' + cfg.letterName);
        if (val && (val.indexOf('？') != -1 || val.indexOf('|') != -1)) {
            JConsole.info('return...');
            return false;
        }
        var match = c_reg.test(val) || (e_reg.test(val) && cfg.letterName);
        match && $(this.s_render).val(val);  //fix bug: the siteCode will not rendered  when site search haven't finished
        if (blur || match) {
            var key = !match ? val : val.substring(0, 1);
//            var url =(!$.browser.msie ? 'http://' + Jd.host :  Jd.host) + cfg.url;
            var url =(Jd.host ? 'http://' + Jd.host :  '') + cfg.url;
            JConsole.info('ajax url:'  + url);
            if (key && !siteData[key]) {
                $.ajax({
                            type: "POST",
                            url: url,
                            data: {name:key},
                            success: function(msg) {
                                siteData[key] = msg;
                                showSites.call(obj, siteData[key], blur);
                            }
                        });
            } else {
                showSites.call(this, siteData[key], blur);
            }
        }
    }

    var hover = false;

    function showSites(sites, blur) {
        JConsole.info('showSites');
        var val = jQuery.trim(this.value);
        var data = [],site;
        if (sites && sites.push && sites[0]) {
            if (n_reg.test(val)) {
                data.push('<li name="' + sites[0][cfg.head.name] + '|' + sites[0][cfg.head.code]  + '" class="sel_li">' + sites[0][cfg.head.name]  + '|' + sites[0][cfg.head.code]  + '</li>');
            } else {
                for (var i in sites) {
                    if (data.length >= 10) {
                        break;
                    }
                    site = sites[i];
                    site[cfg.head.name] .indexOf(val) != -1 && data.push('<li name="' + site[cfg.head.name] + '|' + site[cfg.head.code] + '">' + site[cfg.head.name].replace(val, '<span>' + val + '</span>') + '|' + site[cfg.head.code] + '</li>');
                }
            }
        }
        if (data.length > 0) {
            $(this.s_div).css({height:'auto'}).find('ul').html(data.join('')).find('li').click(
                    function() {
                        JConsole.info('click....')
                        addVal.call(this);
                    }).hover(function() {
                        hover = true;
                        $(this).addClass('sel_li')
                    }, function() {
                        hover = false;
                        $(this).removeClass('sel_li')
                    });
            $(this.s_div).find('ul').show();
        } else {
            JConsole.info('clear....')
            $(this.s_div).css({height:this.s_size.h + 'px'}).find('ul').hide();
            $(this.s_div).find('ul li').remove();   //clear site when not find a matched result
        }
        blur && setVal.call(this, blur);
    }

    function addVal() {
        var input = $(this).parent().siblings('input')[0],arr = $(this).attr('name').split('|');
        $(input).val($(this).attr('name'));
        $(input.s_render).val(arr[1]);
        $(input.s_div).css({height:input.s_size.h + 'px'}).find('ul').hide();
    }

    function setVal(blur) {
        JConsole.info('setVal....')
        var val = $.trim(this.value),html = $(this.s_div).find('.sel_li').attr('name');
        if (blur) {
            JConsole.info(blur);
            html = html ? html : $(this.s_div).find('li').first().attr('name');
            if (val && !html) {
                val = n_reg.test(val) ? '？|' + val : val + '|？';
                $(this).val(val);
            }
            if (!val) {
                $(this.s_div).find('ul li').remove();
                $(this.s_render).val('');
            }
        }
        if (val && html) {
            var arr = html.split('|');
            $(this).val(html);
            $(this.s_render).val(arr[1]);
        }
        $(this.s_div).css({height:this.s_size.h + 'px'}).find('ul').hide();
    }

})(jQuery)
