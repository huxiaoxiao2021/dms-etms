/**
 * Js������
 */
(function() {
    var Jd = {},FILE_NAME='jd.js',pool = {};
    Jd.Dialog = function(c) {
        if (c.id && (c = $.extend({close:'hide'}, c)) && Jd.get(c.id))return Jd.get(c.id);
        var pop = new DialogPro(c);
        c.id && set(c.id, pop);
        return pop;
    }
    function DialogPro(c) {
        var t = document;
        var d = t.createElement('div');
        var cc = t.createElement('div');
        var p = t.createElement('div');
        var _p = t.createElement('div');
        var cont,tp,timer,mousePos,mouseOffset,sa,browser,sels,done;
        var conf = {drag:true,cur:true,autoSize:true,close:'remove',reload:true};
        $.extend(conf, c);
        $.browser.msie && ((parseInt($.browser.version) > 6 && (browser = 'ie6+')) || (browser = 'ie6'));
        function resize(x, y) {
            conf.width = x ? x : conf.width;
            conf.height = y ? y : conf.height;
            conf.width = conf.width > $(window).width()?($(window).width()-20):conf.width;
            conf.height = conf.height > $(window).height()?($(window).height()-20):conf.height;
            conf.x = (parseInt($(window).width()) - parseInt(conf.width))
                    / 2 + $(window).scrollLeft();
            conf.y = (parseInt($(window).height()) - parseInt(conf.height))
                    / 2 + $(window).scrollTop();
            d && $(d).css({
                    height : conf.height + 'px',
                    width : conf.width + 'px',
                    top : conf.y + 'px',
                    left : conf.x + 'px',
                    'z-index':9999999
            }),$(cont).width('100%').height('100%');
            if (conf.cur) {
                var _hp = document.documentElement.scrollHeight > document.documentElement.clientHeight ?$(t).height(): $(window).height();
                $(p).css({height:_hp + 'px'});
            }
            fab &&  $(fab).css({width:(conf.width - 50) + 'px'});
            closeBtn && $(closeBtn).css({left:(conf.width - 25) + 'px'});
        }

        // ���촰��
        function build() {
            (conf.cur && cur(),_p.appendChild(d)) || t.body.appendChild(d);
            document.activeElement.tagName != 'BODY' && $(document.activeElement).blur();
            conf.width = conf.width ? parseInt(conf.width) : 0;
            conf.height = conf.height ? parseInt(conf.height) : 0;
            $(d).css({
                position : 'absolute',
                background : 'white'
            });
            $(window).bind('resize', ap);
            cont = t.createElement('div');
            $(cont).css({
                position : 'relative',
                top : 0,
                left : 0
            }).height('100%').width('100%');
            var content = t.createElement('div');
            $(content).css({
                position : 'absolute',
                top : '0px',
                left : '0px'
            });
            conf.className && $(content).addClass(conf.className);
            conf.css && $(content).css(conf.css);
            if (conf.url) {
                var frame = t.createElement('iframe');
                frame.frameBorder = 0;
                frame.scrolling = 'no';
                frame.src = conf.url;
                frame.id = conf.id || 'QT_CONTENT_FRAME';
                frame.name = conf.id || 'QT_CONTENT_FRAME';
                $(frame).css({
                    height : conf.height + 'px',
                    width : conf.width + 'px'
                });
                if (!conf.skipResize) {
                    $(frame).load(function() {
                        var height = Math.max(this.contentWindow.document.body.scrollHeight, this.contentWindow.document.documentElement.scrollHeight);
                        var width = Math.max(this.contentWindow.document.body.scrollWidth, this.contentWindow.document.documentElement.scrollWidth);
                        $(this).css({
                            height : height + 'px',
                            width : width + 'px'
                        });
                        resize(width, height);
                    });
                }
                content.appendChild(frame);
                if (conf.skipResize) {
                    resize(conf.height, conf.width);
                }
            } else if (conf.innerDom) {
                content.appendChild(conf.innerDom);
            } else {
                $(content).append('There is nothing to be showed...');
            }
            conf.css && $(content).css(conf.css);
            cont.appendChild(content);
            d.appendChild(cont);
            conf.autoSize && (conf.width = $(content).width(),conf.height = $(content).outerHeight(true));
        }

        function reload() {
            conf.url && $(cont).find('iframe').attr('src', conf.url + '&rnd=' + Math.random());
        }

        var fab;
        var closeBtn;

        function show() {
            if (done) {
                resize();
                conf.reload && reload();
                conf.cur?$(p).show():$(d).show();
                return;
            }
            build();
            if (conf.closeBtn) {
                closeBtn = t.createElement('div');
                $(closeBtn).append('<span style="font-size:18px;color:gray;">��</span>').css({
                    margin:'2px',
                    position:'absolute',
                    top:'0px',
                    cursor:'pointer',
                    left:(conf.width - 26) + 'px',
                    zIndex : 999
                }).bind('click', close).appendTo(cont);

            }

            if (conf.drag) {
                fab = t.createElement('div');
                $(fab).css(
                {
                    position : 'absolute',
                    top : '0px',
                    left : '0px',
                    cursor : 'move',
                    width : conf.width - 50 + 'px',
                    height : '25px'
                });
                cont.appendChild(fab);
                $(fab).mousedown(function(e) {
                    timer = window.setInterval(drag, 10);
                    tp = t.createElement("div");
                    d.style.MozUserSelect = 'none';
                    d.style.MozUserFocus = 'ignore';
                    if (d.setCapture) {
                        d.setCapture();
                    }
                    $(tp).css({
                        position : 'absolute',
                        top : 0,
                        left : 0,
                        filter : 'Alpha(opacity=10)',
                        opacity : 0,
                        zIndex : 200
                    }).height('100%').width('100%');
                    cont.appendChild(tp);
                    getOffset(e);
                    $(t).bind('mousemove', getMousePosition);
                    $(t).bind('mouseup', unDrag);
                });
            }
            if(conf.innerDom) {
            	var _size = Jd.getSize(conf.innerDom);
            	resize(_size.w,_size.y);
            }else{
            	resize();
            }
            done = true;
        }

        function ap() {
            $(p).width('100%');
        }

        // �رմ���
        function close() {
			if(conf.close == 'remove') {
				($.browser.msie) && $(cont).find('iframe').remove();
				if (conf.cur) {
					$(window).unbind('resize', ap);
					t.body.removeChild(p);
				} else {
					t.body.removeChild(d);
				}
			}else{
				if (conf.cur) {
					$(window).unbind('resize', ap);
					$(p).hide();
				} else {
					$(d).hide();
				}
			}
			(sels) && sels.show();
        }

        // �������ֲ�
        function cur() {
            if (browser == 'ie6') {
                sels = $('select:visible');
                sels.hide();
            }
            var _hp = document.documentElement.scrollHeight > document.documentElement.clientHeight ? $(t).height() : $(window).height();
            t.body.appendChild(p);
            $(p).css({
                position : 'absolute',
                height : _hp + 'px',
                zIndex : 100,
                top : 0,
                left : 0
            }).width('100%');
            p.appendChild(_p);
            $(_p).css({
                position : 'relative',
                top : 0,
                left : 0
            }).height('100%').width('100%');
            _p.appendChild(cc);
            $(cc).css({
                position : 'absolute',
                background : 'gray',
                top : 0,
                left : 0,
                filter : 'Alpha(opacity=10)',
                opacity : 0.1
            }).height('100%').width('100%');
        }

        // ��ק
        function drag() {
            d.style.position = 'absolute';
            var _x = mousePos.x - mouseOffset.x + $(window).scrollLeft();
            var _y = mousePos.y - mouseOffset.y + $(window).scrollTop();
            (_y < 2 + $(window).scrollTop()) && (_y = 2 + $(window).scrollTop());
            (_y > ($(window).scrollTop() + $(window).height() - conf.height) - 2)
               && (_y = $(window).scrollTop() + ($(window).height() - conf.height - 2));
            (_x < 2 + $(window).scrollLeft()) && (_x = 2 + $(window).scrollLeft());
            (_x > $(window).scrollLeft() + ($(window).width() - conf.width) - 2)
               && (_x = $(window).scrollLeft() + ($(window).width() - conf.width - 2));
            d.style.left = _x + 'px';
            d.style.top = _y + 'px';
        }

        // ��ק����
        function unDrag() {
            if (tp == null || tp == 'undefined')
                return false;
            d.releaseCapture && d.releaseCapture();
            window.clearInterval(timer);
            d.style.MozUserSelect = '';
            d.style.MozUserFocus = '';
            cont.removeChild(tp);
            tp = null;
            $(t).unbind('mousemove', getMousePosition);
            $(t).unbind('mouseup', unDrag);
        }

        // ���λ�ü�¼
        function getMousePosition(e) {
            e = e || window.event;
            (e.pageX || e.pageY)  && (mousePos = {x : e.pageX, y : e.pageY});
            mousePos = {
                x : e.clientX + document.body.scrollLeft
                        - document.body.clientLeft,
                y : e.clientY + document.body.scrollTop
                        - document.body.clientTop
            };
        }

        // ����ƫ��������ק��Χ
        function getOffset(e) {
            var docPos = Jd.getPosition(d);
            getMousePosition(e);
            mouseOffset = {
                x : mousePos.x - docPos.x
                        + parseInt($(window).scrollLeft()),
                y : mousePos.y - docPos.y + parseInt($(window).scrollTop())
            };
        }

        function find(opt) {
            return $(cont).find(opt);
        }
        return {
            show : show,
            close : close,
            find:find
        };

    }

    // ��ҳ����г�ʼ��
    // ���ܣ�[��ʱ�����ز��ɼ��ͼƬ]
    // ʹ����������<img>��srcֵд��alt�����У�src���Կ��û��߲�д�������дwidth��height
    // cfg = {
    // scope:document.body ��Ҫ���ô˹��ܵ�����
    // top:'20px', ��img����ɼ������ж�������ʱ��ʼ��ʼ��ͼƬ
    // num:1, ��ʾͼƬ����ʱ������[num]��ͼƬ���ڿɼ�����ʹ��ͼƬ����
    // }
    Jd.initImg = function(cfg) {
        cfg = cfg ? cfg : {};
        cfg.scope = cfg.scope ? cfg.scope : document.body;
        cfg.top = cfg.top ? parseInt(cfg.top) : 20;
        cfg.num = cfg.num ? cfg.num : 1;
        imgCount = 0;
        var imgReg = /\.jpg|\.png|\.gif|\.jpeg/i;
        var h = $(window).height() + cfg.top;
        $(cfg.scope).find('img').each(
                function() {
                    var top = Jd.getPosition(this).y;
                    if (imgReg.test(this.alt)) {
                        var tempDiv = document.createElement('div');
                        $(this).after($(tempDiv).css({
                            width : $(this).width() + 'px',
                            height : $(this).height() + 'px'
                        }).attr('name', top + '__lazy__' + this.alt)).addClass(
                                '__lazy__').hide().removeAttr('src');
                        imgCount++;
                    }
                });
        if (imgCount > 0) {
            var rh = h;

            function setRh() {
                rh = h + $(window).scrollTop();
            }

            $(window).bind('scroll', setRh);
            var eh = 0;
            var interval;

            function swapImgSrc() {
                if (rh > eh) {
                    var imgs = $('img[class="__lazy__"]');
                    var rnum = 0;
                    if (imgs.length == 0) {
                        $(window).unbind('scroll', swapImgSrc);
                        window.clearInterval(interval);
                        return;
                    }
                    imgs.each(function() {
                        if (rnum > cfg.num) {
                            return false;
                        }
                        var imgctx = $(this).next().attr('name').split(
                                '__lazy__');
                        var top = parseInt(imgctx[0]);
                        if (top < rh) {
                            $(this).attr('src', imgctx[1]).show().removeClass(
                                    '__lazy__').next().remove();
                        } else {
                            rnum++;
                        }
                    });
                    eh = rh;
                }
            }

            interval = window.setInterval(swapImgSrc, 300);
        }
    }
    // ��ҳ���ض�λ����ʾ��ʾ��Ϣ
    // cfg = {
    // id:...
    // element:obj ������ʾ��Ԫ��ID
    // obj:object   can't use with {element}
    // model ��single|..Ĭ��һ��Ԫ��ֻ����һ��tips
    // width :
    // height :
    // rpos: {x:..,y:..} ���λ�� xyΪ����ֵ �����������ʱpadding,pos,align���Խ���Ч
    // pos: n|s|e|w tips��ʾ��λ�ã�����ڴ���Ԫ�ص�λ�ã�
    // content: text tips����
    // innerDom : Ҫ��ʾ��domԪ�� ���ܺ�contentͬʱʹ��
    // cls : tips����ʽ
    // hideSel: ��Ҫ��tips��ʾʱ��ʱ���ص�Ԫ��ID ����IE6��ʹ��
    // seconds: ��tips���ڵ�ʱ�䣨�룩
    // flash:0~n ms -1ʱ������
    // align:left|right|center tip�봥��Ԫ��λ�õ�ˮƽ���뷽ʽ,��pos��e|wֵ��ͻ
    // vlign:top|bottom|middle tip�봥��Ԫ��λ�õĴ�ֱ���뷽ʽ,��pos��n|sֵ��ͻ
    // padding:����ֵ,tip�봥��Ԫ�ؼ��
    // token: string,ͬһʱ��ֻ����һ��tokenֵ��ͬ��tip��ʾ���󴥷���tip���ȼ������ȴ�����tip
    // closeEvent:string������elementָ��Ԫ�ص�ĳһ�¼��������¼�����ʱ�ر�tip
    // closeHandler:[{id:'',event:''},{},..],�������������tip��click�ر��¼���ʧЧ
    // ob:0~n ms,���>0���ᴴ��һ������������elementԪ�ص���������element���ɼ��Ӧtip���Զ�ɾ��
    // callback:{'close':function(){}} ��ʱֻ֧��closeʱ��ʱ�ص�
    // doClose:�����������֮��closeEvent,closeHandler����������
    // }
    Jd.tip = function(conf) {
        var tipModel;
        if(conf.id ){
            tipModel = Jd.get(conf.id);
        }
        if(!tipModel){
            tipModel = new Jd.TipModel(conf);
            if(conf.id){
                set(conf.id,tipModel);
            }
        }
        tipModel.show();
        return tipModel;
    }
    Jd.TipModel = function (conf){
        var cfg = {model:'single',pos:'e',seconds:-1,flash:-1,align:'center',vlign:'middle',padding:5,ob:false,zIndex:103};
        $.extend(cfg, conf);
        if (cfg.model == 'single') {
            !Jd.tipRecord && (Jd.tipRecord = "");
            if (Jd.tipRecord.indexOf(cfg.element) != -1) return;
            Jd.tipRecord = Jd.tipRecord + "<" + cfg.element + ">";
        }
        cfg.padding = parseInt(cfg.padding);
        var obj = cfg.obj ? cfg.obj : document.getElementById(cfg.element);
        var pos = Jd.getPosition(obj);
        cfg.objPos = Jd.getPosition(obj);
        var _size = Jd.getSize(obj);
      
        var ow = $(obj).width() > $(obj).outerWidth() ? $(obj).width() : $(obj).outerWidth();
        var oh = $(obj).height() > $(obj).outerHeight() ? $(obj).height() : $(obj).outerHeight();
        var cont = document.createElement('div');
        $(document.body).append(cont);
        if (cfg.content) {
            var dom = document.createElement('div');
            $(dom).append('<div style="border:1px solid red;background:#FFFBED;padding:3px">' + cfg.content + '<div>')
            cfg.innerDom = dom
        }
        $(cont).css({
            position : 'absolute',
            zIndex : cfg.zIndex,
            overflow : 'hidden',
            display:'none'
        }).append(cfg.innerDom).append('<input name="__HideSelID__" type="hidden" value="' + cfg.hideSel + '"/>');
        var size = Jd.getSize(cfg.innerDom);
        cfg.width = size.w;
        cfg.height = size.h;
        if (cfg.rpos) {
            pos.x = pos.x + parseInt(cfg.rpos.x, 10);
            pos.y = pos.y + parseInt(cfg.rpos.y, 10);
        } else {
            if (cfg.align == 'center') {
                pos.x = cfg.pos.indexOf('e') != -1 ? pos.x + ow + cfg.padding : cfg.pos
                        .indexOf('w') != -1 ? pos.x - cfg.width - cfg.padding
                        : pos.x + (ow - cfg.width) / 2;
            } else if (cfg.align == 'right'
                    && cfg.pos.indexOf('e') == -1
                    && cfg.pos.indexOf('w') == -1) {
                pos.x = pos.x + ow - cfg.width;
            }
            if (cfg.vlign == 'top' && cfg.pos.indexOf('n') == -1
                    && cfg.pos.indexOf('s') == -1) {
                pos.y = pos.y
            } else if (cfg.vlign == 'bottom' && cfg.pos.indexOf('n') == -1
                    && cfg.pos.indexOf('s') == -1) {
                pos.y = pox.y + oh - cfg.height;
            } else {
                pos.y = cfg.pos.indexOf('s') != -1 ? pos.y + oh + cfg.padding
                        : cfg.pos.indexOf('n') != -1 ? pos.y - cfg.height - cfg.padding
                        : pos.y + (oh - cfg.height) / 2;
            }

        }
        $(cont).css({
            left : pos.x + 'px',
            top : pos.y + 'px'
        });
        cfg.cls && $(cfg.innerDom).addClass(cfg.cls);
        cfg.hideSel && $('#' + cfg.hideSel).hide();
        cfg.width && $(cont).css({width : cfg.width + 'px',height : cfg.height + 'px'});
        if (cfg.flash > 0) {
            var inter = window.setInterval(function() {
                flash(cont);
            }, cfg.flash);
            cfg.inter = inter;
        }
        function flash(obj) {
            ($(obj).attr('name') == 'off' && $(obj).show().attr('name', 'on')) || $(obj).hide().attr('name', 'off');
        }
        if (cfg.ob) {
            var inter = window.setInterval(function() {
                obex(cont, cfg);
            }, cfg.ob * 100);
            cfg.ob = inter;
        }
        function obex(cont, cfg) {
            var obj = document.getElementById(cfg.element);
            obj = obj?obj:cfg.obj;
            if (!obj) {
                return clear(cont, cfg);
            }
            var s = (obj.style && obj.style.display == 'none');
            var s1 = ($(obj).attr('disabled') == true || $(obj).attr('disabled') == 'disabled')
            if (s || s1 || !obj.offsetWidth || parseInt(obj.offsetWidth) == 0) {
                clear(cont, cfg);
            } else {
                var p0 = cfg.objPos;
                var p1 = Jd.getPosition(obj);
                var p2 = Jd.getPosition(cont);
                cont.style.left = (p2.x - (p0.x - p1.x)) + 'px';
                cont.style.top = (p2.y - (p0.y - p1.y)) + 'px';
                cfg.objPos = p1;
            }
        }

        if (!cfg.closeHandler && !cfg.doClose) {
            $(cont).bind('click', {cfg:cfg}, function(e) {
                clear(cont, cfg);
            });
        } else if (!cfg.doClose) {
            for (var k = cfg.closeHandler.length; k > 0; k--) {
                $('#' + cfg.closeHandler[k - 1].id).bind(cfg.closeHandler[k - 1].event, function() {
                    clear(cont, cfg);
                });
            }
        }
        if (cfg.seconds > 0) {
            var timer = window.setTimeout(function() {
                clear(cont, cfg);
            }, cfg.seconds * 1000);
        }
        if (cfg.closeEvent && !cfg.doClose) {
            $(obj).bind(cfg.closeEvent, function() {
                clear(cont, cfg);
            });
        }
        function clear(obj, ops) {
            obj = obj ? obj : cont;
            ops = ops ? ops : cfg;
            $('#' + $(obj).fadeOut().remove().find(
                    ':hidden[name="__HideSelID__"]').val()).show();
            if (ops.model == 'single') {
                Jd.tipRecord = Jd.tipRecord.replace(ops.element, '');
            }
            if (ops.inter) {
                window.clearInterval(ops.inter);
            }
            if (ops.ob != 0) {
                window.clearInterval(cfg.ob);
            }
            if (ops.callback && ops.callback['close']) {
                ops.callback['close']();
            }
            if(ops.id){
                remove(ops.id);
            }
        }

        if (cfg.token) {
            var oldTip = Jd.get(cfg.token);
            if (oldTip) {
                oldTip.clear();
            }
            set(cfg.token, {clear:clear});
        }
        function show(){
            $(cont).fadeIn('fast').show();
        }
        function close(){
            $(cont).fadeOut('fast').hide();
        }
        return {show:show,close:close,clear:clear,dom:cont};
    }
    //����ѡ��ؼ�
    Jd.region = function(cfg) {
        var list = {
            "���㻦":["�Ϻ�","����","�㽭"],
            "����":["����","����"],
            "����":["����","���","�ӱ�","ɽ��","���ɹ�","ɽ��"],
            "����":["����","����","����"],
            "����":["����","�㶫","����","����"],
            "����":["����","����","����"],
            "����":["����","����","�ຣ","����","�½�"],
            "����":["����","�Ĵ�","����","����","����"],
            "�۰�̨":["̨��","���","����"],
            "����":["����"]
        };
        var d0 = document.createElement('div');
        var u0 = document.createElement('ul');
        var u1 = document.createElement('ul');
        var d1 = document.createElement('div');
        for (var p in list) {
            var li = document.createElement('li');
            $(document.createElement('input')).attr('type', 'checkbox')
                    .attr('value', list[p].join(',')).click(
                    function() {
                        selChildren(this.value, this.checked);
                    }).appendTo(li).addClass('checks');
            $(li).append(p).appendTo(u0);
            for (var i = 0; i < list[p].length; i++) {
                var c = list[p][i];
                $(u1).append('<li><input name="_region" type="checkbox" class="checks" value="' + c + '"/>' + c + '</li>')
            }
        }
        function selChildren(c, b) {
            c = c.split(',');
            for (var i = c.length; i > 0; i--) {
                $('.checks:checkbox[value="' + c[i - 1] + '"]').attr('checked', b);
            }
        }

        $(d0).append($(u0).addClass('town_box').addClass('border_b'))
                .append($(u1).addClass('town_box'))
                .append($(d1).addClass('btnbox'))
                .addClass('town');
        $(document.createElement('input'))
                .attr('id', cfg.setVal[0] + 'lbtn').attr('value', 'ȷ��')
                .attr('type', 'button').click(
                function() {
                    var v = [];
                    $(this).parent().parent().find('input[name="_region"]:checked').each(function() {
                        v.push($(this).val());
                    })
                    for (var i = cfg.setVal.length; i > 0; i--) {
                        setVal(cfg.setVal[i - 1], v.join(','));
                    }
                }).addClass('editbtn').addClass('f_l').appendTo(d1);
        $(d1).append('<input type="button" id="' + cfg.setVal[0] + 'rbtn" value="ȡ��" class="editbtn f_r">');
        function setVal(id, v) {
            if ($('#' + id)[0].tagName == 'INPUT') {
                $('#' + id).val(v);
            } else {
                $('#' + id).empty().append(v);
            }
        }

        Jd.tip({
            element:cfg.renderTo,
            innerDom:d0,
            closeHandler:[
                {id:cfg.setVal[0] + 'lbtn',event:'click'},
                {id:cfg.setVal[0] + 'rbtn',event:'click'}
            ],
            token:'region',
            pos:'s',
            align:'left'
        });
    }
    // �ؼ��ľ��λ��
    Jd.getPosition = function(e) {
        var left = 0;
        var top = 0;
        while (e.offsetParent) {
            left += e.offsetLeft;
            top += e.offsetTop;
            e = e.offsetParent;
        }
        left += e.offsetLeft;
        top += e.offsetTop;
        return {
            x : left,
            y : top
        };
    }
    //��ȡ�ؼ���С
    Jd.getSize = function (obj){
    	var w = obj.width?parseInt(obj.width,10):$(obj).width();
    	var h = obj.height?parseInt(obj.height,10):$(obj).height();
    	w = w<$(obj).outerWidth()?$(obj).outerWidth():w;
    	h = w<$(obj).outerHeight()?$(obj).outerHeight():h;
    	if(!w){
    		var __dom = $(obj).clone().hide().appendTo(document.body);
    		w = __dom.width()<__dom.outerWidth()?__dom.outerWidth():__dom.width();
    		h = __dom.height()<__dom.outerHeight()?__dom.outerHeight():__dom.height();
    		__dom.remove();
    	}
    	return {w:w,h:h};
    }
    //��ȡ�����Ԫ��
    Jd.get = function(id) {
        return pool[id];
    }
    //�ṩ�������ڸ�ʽ������
    Jd.DateFormat = function(s) {
        if (!s) {
            s = 'yyyy-MM-dd HH:mm:ss';
        }
        function format(date) {
            return s.replace('yyyy', date.getFullYear()).replace('MM', numFormat(date.getMonth() + 1))
                    .replace('dd', numFormat(date.getDate())).replace('HH', numFormat(date.getHours()))
                    .replace('mm', numFormat(date.getMinutes())).replace('ss', numFormat(date.getSeconds()));
        }

        function numFormat(n) {
            if (n < 10) {
                return "0" + n;
            }
            return n;
        }

        function parse(dateStr, type) {
            var type = type ? type : 0;
            if (s.length != dateStr.length) {
                throw new Error('Format string �� Date string ��ƥ��');
            }
            var _date = new Date();
            _date.setDate(1);
            var yi = s.indexOf('yyyy');
            var Mi = s.indexOf('MM');
            var di = s.indexOf('dd');
            if (yi == -1 || Mi == -1 || di == -1) {
                return;
            }
            _date.setFullYear(parseInt(dateStr.slice(yi, yi + 4), 10));
            _date.setMonth(parseInt(dateStr.slice(Mi, Mi + 2), 10) - 1);
            _date.setDate(parseInt(dateStr.slice(di, di + 2), 10));
            var Hi = s.indexOf('HH');
            var mi = s.indexOf('mm');
            var si = s.indexOf('ss');
            if (Hi == -1) {
                if (type == 0) {
                    _date.setHours(0);
                } else {
                    _date.setHours(23);
                }
            } else {
                _date.setHours(parseInt(dateStr.slice(Hi, Hi + 2), 10));
            }
            if (mi == -1) {
                if (type == 0) {
                    _date.setMinutes(0);
                } else {
                    _date.setMinutes(59);
                }
            } else {
                _date.setMinutes(parseInt(dateStr.slice(mi, mi + 2), 10));
            }
            if (si == -1) {
                if (type == 0) {
                    _date.setSeconds(0);
                } else {
                    _date.setSeconds(59);
                }
            } else {
                _date.setSeconds(parseInt(dateStr.slice(si, si + 2), 10));
            }
            return _date;
        }

        return {
            format:format,
            parse:parse
        }
    }
    function set(id, obj) {
        pool[id] = obj;
    }

    function remove(id) {
        pool[id] = null;
    }

    Jd.cover = function () {
        var p = document.createElement('div');
        var _p = document.createElement('div');
        var cc = document.createElement('div');
        if ($.browser.msie && $.browser.version < 6) {
            var sels = $('select:visible');
            sels.hide();
        }
        var _hp = document.documentElement.scrollHeight > document.documentElement.clientHeight ? $(document).height()
                : $(window).height();
        document.body.appendChild(p);
        $(p).css({
            position : 'absolute',
            height : _hp + 'px',
            zIndex : 100,
            top : 0,
            left : 0
        }).width('100%');
        p.appendChild(_p);
        $(_p).css({
            position : 'relative',
            top : 0,
            left : 0
        }).height('100%').width('100%');
        _p.appendChild(cc);
        $(cc).css({
            position : 'absolute',
            background : 'gray',
            top : 0,
            left : 0,
            filter : 'Alpha(opacity=20)',
            opacity : 0.2
        }).height('100%').width('100%');
        return {
            cover:$(p),
            hide:sels
        };
    }
    /**
     * ��ȡͼƬ��ʵ�ʳߴ�
     * @param src
     */
    var imgSizePool = {};
    Jd.getImgSize = function(src) {
        if (imgSizePool[src]) {
            return imgSizePool[src];
        }
        var img = new Image();
        img.src = src;
        if (img.complete) {
            return {
                x:img.width,
                y:img.height
            }
        } else {
            img.onload = function() {
                imgSizePool[src] = {
                    x:this.width,
                    y:this.height
                }
            }
            img.onerror = function() {
                imgSizePool[src] = { x:-1,y:-1};
            }
            img.onabort = function() {
                imgSizePool[src] = { x:-1,y:-1};
            }
        }
        return imgSizePool[src];
    }
    /**
     *  ͼƬ�Ŵ�
     */
    Jd.zoom = {};
    var zoom = {resized:0,stop:true};
    /**
     *
     * @param cfg {scope:'',data:{..},width:'',hieght:''}
     */
    Jd.zoom.init = function(cfg) {
        var conf = {width:300,height:300,imgBox:true};
        cfg = $.extend(conf, cfg);
        if (cfg.data) {
            for (var src in cfg.data) {
                Jd.getImgSize(src);
                Jd.getImgSize(cfg.data[src]);
            }
        }
        if (cfg.scope) {
            var zobj = $('#' + cfg.scope);
            zoom.zobj = zobj[0];
            zoom.pos = Jd.getPosition(zobj[0]);
            zoom.zise = {width:zobj.outerWidth(),height:zobj.outerHeight()};
            zobj.mouseover(function(e) {
                zoom.fire(e);
            });
            zobj.mouseout(zoom.stopRun);
            var ul = document.createElement('ul');
            $(ul).append('<li class="qt_zoom_zoomIn" style="margin:3px;cursor:pointer;height:32px;width:32px"><img src="/images/zoom-in-icon.png" style="max-width:32px"/></li>' +
                    '<li class="qt_zoom_zoomOut" style="margin:3px;cursor:pointer;height:32px;width:32px"><img src="/images/zoom-out-icon.png" style="max-width:32px"/></li>').addClass('qt_zoom_zul');
            zoom.zul = ul;
            cfg.width = parseInt(cfg.width, 10);
            cfg.height = parseInt(cfg.height, 10);
        }
        if (cfg.imgBox) {
            zoom.box = new Jd.imgBox({data:cfg.data,listPos:'n'}).dom;
            zobj.click(function() {
                zoom.showImg();
            });
        }
        zoom.ops = cfg;
        zoom.rate = 1;
    }
    $(window).resize(function(){
       zoom.resized = 1;
    });
    zoom.stopRun = function(e){
        e = e || window.event;
        var t = e.relatedTarget || e.toElement;
        if(zoom.building || !zoom.zoomTip || !zoom.dom){
            return;
        }
        while (t && t != zoom.zoomTip.dom && t != zoom.dom && t.tagName != 'BODY') {
            t = t.parentNode;
        }
        if (t && t.tagName != 'BODY'){
            return;
        }
        zoom.stop = true;
    }
    /**
     * ����Ŵ���
     * @param r
     */
    Jd.zoom.setRate = function(r) {
        if(zoom.animate)return;
        zoom.animate = true;
        zoom.rate = r;
        if (zoom.running) {
            zoom.size = {width:Math.floor(zoom.pize.width / zoom.rate.toFixed(2)),height:Math.floor(zoom.pize.height / zoom.rate.toFixed(2))};
            $(zoom.dom).animate({
                width:zoom.size.width + 'px',
                height:zoom.size.height + 'px',
                left:zoom.dom.style.left,
                top:zoom.dom.style.top
            }, 200);
            var img = $(zoom.tip.dom).find('img');
            var w = zoom.imgSize.width * zoom.rate + 'px';
            var h = zoom.imgSize.height * zoom.rate + 'px';
//            var top = zoom.tip.dom.style.top;
//        	var left = zoom.tip.dom.style.left;
            img.animate({
            	width:w,
            	height:h,
                top:0,
                left:0
            },200);
        }
        zoom.animate = false;
    }
    zoom.showImg = function() {
        if (zoom.box) {
            var dialog = new Jd.Dialog({
                id:'qt_zoom_img_box',
                drag:false,
                innerDom:zoom.box,
                close:'hide',
                closeBtn:true
            })
            dialog.show();
        }
    }
    zoom.fire = function(e) {
        if(zoom.resized == 1){
            Jd.zoom.init(zoom.ops);
        }
        if (zoom.running) {
            return;
        }
        zoom.building = true;
        zoom.stop = false;
        $(document).unbind('mousemove',zoom.run).bind('mousemove', zoom.run);
        var ops = zoom.ops;
        var obj = $(e.target);
        if (obj.attr('tagName') != 'IMG') {
            obj = obj.find('img');
        }
        if (obj.length == 0) {
            return false;
        }
        var src = obj.attr('src');
        if (zoom.ignore && zoom.ignore[src]) {
            return;
        }
        if (!zoom.size || src != zoom.src || zoom.resized == 1) {
            zoom.src = src;
            var size = Jd.getImgSize(zoom.src);
            var _size = Jd.getImgSize(ops.data[zoom.src]);
            if (!size || !_size) {
                return zoom.running = false;
            }
            if (size.x == -1 || _size.x == -1 || size.x >= _size.x) {
                if (!zoom.ignore) {
                    zoom.ignore = {};
                }
                zoom.ignore[src] = true;
                return zoom.running = false;
            }
            size.x > zoom.zise.width && (size.x = zoom.zise.width);
            size.y > zoom.zise.height && (size.y = zoom.zise.height);
            var s = size.x / _size.x.toFixed(5);
            zoom.s = s;
            zoom.pize = {width:Math.floor(ops.width * s),height:Math.floor(ops.height * s)};
            zoom.size = {width:Math.floor(zoom.pize.width / zoom.rate.toFixed(2)),height:Math.floor(zoom.pize.height / zoom.rate.toFixed(2))};
            zoom.imgSize = {width:_size.x,height:_size.y};
            zoom.dom = document.createElement('div');
            $(zoom.dom).css({
                position:'absolute',
                background:'#87CEFF',
                width:zoom.size.width + 'px',
                border:'1px solid #8470FF',
                height:zoom.size.height + 'px',
                filter : 'Alpha(opacity=30)',
                cursor:'crosshair',
                opacity : 0.3
            });
            if (zoom.ops.delegate) {
                for (var k = zoom.ops.delegate.length; k > 0; k--) {
                    var ev = zoom.ops.delegate[k - 1];
                    $(zoom.dom).bind(ev.event, function() {
                        ev.handler();
                    });
                }
            }
            if(zoom.box){
                $(zoom.dom).click(function(){zoom.showImg();});
            }
            $(document.body).append(zoom.dom);
        }
        if (!zoom.tip || src != zoom.src || zoom.resized == 1) {
            var pop = document.createElement('div');
            var img = document.createElement('img');
            img.src = ops.data[zoom.src];
            $(img).css({
                position:'relative',
                top:'0px',
                left:'0px',
                width:zoom.imgSize.width * zoom.rate + 'px',
                height:zoom.imgSize.height * zoom.rate + 'px'
            });
            var wrapper = document.createElement('div'); 
            $(wrapper).css({
            	position:'absolute',
            	top:'0px',
            	left:'0px',
            	width:ops.width,
            	height:ops.height,
            	overflow:'hidden'
            }).append(img);
            $(pop).css({
            	position:'relative',
                top:'0px',
                left:'0px',
                width:ops.width,
                height:ops.height,
                background:'#FCFCFC',
                border:'1px solid #DDDDDD',
                overflow:'hidden'
            }).append(wrapper);
            zoom.tip = Jd.TipModel({
                model:'multi',
                token:'zoom',
                vlign:'top',
                element:ops.scope,
                pos:'e',
                innerDom:pop,
                doClose:true
            });
            zoom.tip.show();
        }
        $(zoom.dom).unbind('mouseout',zoom.boom).mouseout(zoom.boom).show();
        zoom.zoomTip = new Jd.TipModel({
            model:'multi',
            element:zoom.ops.scope,
            doClose:true,
            rpos:{x:0,y:0},
            vlign:'top',
            token:'zoomTip',
            innerDom:zoom.zul
        });
        zoom.zoomTip.show();
        $(zoom.zoomTip.dom).unbind('mouseout',zoom.boom).mouseout(zoom.boom);
        var zi = $(zoom.zul).find('.qt_zoom_zoomIn img');
        var zo = $(zoom.zul).find('.qt_zoom_zoomOut img');
        zi.unbind('click').click(function() {
            if (zoom.rate < 2) {
                var r = Math.round((zoom.rate + 0.2)*10)/10;
                Jd.zoom.setRate(r);
                zoom.rate > 1 && $(this).parent().next().find('img').show();
                zoom.rate == 2 && $(this).hide();
            }
        });

        zo.unbind('click').click(function() {
            if (zoom.rate > 1) {
                var r = Math.round((zoom.rate - 0.2)*10)/10;
                Jd.zoom.setRate(r);
                zoom.rate < 2 && $(this).parent().prev().find('img').show();
                zoom.rate == 1 && $(this).hide();
            }
        });
        (zoom.rate < 2 && zi.show()) || zi.hide();
        (zoom.rate > 1 && zo.show()) || zo.hide();
        zoom.running = true;
        zoom.building = false;
        zoom.resized = 0;
    }
    zoom.run = function(e) {
        if(zoom.stop){
            zoom.clear();
        }
        if (!zoom.running) {
            return;
        }
        zoom.r = true;
        e = e || window.event;
        var x = e.clientX + $(window).scrollLeft() - zoom.size.width / 2;
        var y = e.clientY + $(window).scrollTop() - zoom.size.height / 2;
        x = Math.floor(x < zoom.pos.x ? zoom.pos.x : x + zoom.size.width >= zoom.pos.x + zoom.zise.width ? zoom.pos.x + zoom.zise.width - zoom.size.width : x);
        y = Math.floor(y < zoom.pos.y ? zoom.pos.y : y + zoom.size.height >= zoom.pos.y + zoom.zise.height ? zoom.pos.y + zoom.zise.height - zoom.size.height : y);
        $(zoom.dom).css({left:x + 'px',top:y + 'px'});
        $(zoom.tip.dom).find('img').css({
            left:-Math.floor((x - zoom.pos.x) / zoom.s * zoom.rate.toFixed(2)) + 'px',
            top:-Math.floor((y - zoom.pos.y) / zoom.s * zoom.rate.toFixed(2)) + 'px'
        });
    }
    zoom.boom = function(e) {
        e = e || window.event;
        var t = e.relatedTarget || e.toElement;
        while (t && t != zoom.zoomTip.dom && t != zoom.dom && t.tagName != 'BODY') {
            t = t.parentNode;
        }
        if (t && t.tagName != 'BODY'){
            return;
        }
        zoom.clear();
    }
    zoom.clear = function() {
        if (zoom.dom && zoom.tip) {
            $(zoom.dom).hide();
            zoom.tip.clear();
            zoom.tip = null;
            $(document).unbind('mousemove', zoom.run);
        }
        zoom.zoomTip && zoom.zoomTip.clear();
        zoom.running = false;
    }
    /**
     * ͼƬչʾ�ؼ�
     * cfg {width:'',height:'',border:true|false,data:{..}}
     */
    Jd.imgBox = function(cfg) {
        var conf = {width:700,height:550,border:5,listPos:'s'};
        cfg = $.extend(conf, cfg);
        cfg.border = parseInt(cfg.border, 10);
        cfg.width = $(window).width() < parseInt(cfg.width, 10)? ($(window).width()-cfg.border):parseInt(cfg.width,10);
        cfg.height = $(window).height() < parseInt(cfg.height, 10)?($(window).height()-cfg.border):parseInt(cfg.height,10);
        
        var ul = document.createElement('ul');
        var div = document.createElement('div');
        var initImg = null;
        $(ul).css({listStyle:'none outside none',
            overflow:'hidden',
            background:'#FAFBFC',
            filter : 'Alpha(opacity=80)',
            cursor:'pointer',
            opacity : 0.8});
        var border = "1px solid #FFD700";
        var cls = 'class="qt_box_show"';
        !cfg.desrc && !cfg.de && (cfg.de = 0);
        if (cfg.data) {
            var i = 0;
            for (var src in cfg.data) {
                i == cfg.de && (cfg.desrc = src);
                if (src != cfg.desrc) {
                    border = "1px solid #DDDDDD";
                    cls = "";
                }else{
                    border = "1px solid #FFD700";
                    cls = 'class="qt_box_show"';
                    initImg = cfg.data[src];
                }
                Jd.getImgSize(src);
                Jd.getImgSize(cfg.data[src]);
                $(ul).append('<li style="background:white;display:inline;float:left;width:80px;height:80px;margin:10px" ' + cls + '>' +
                        '<img  style="max-height:80px;max-width:80px;width:80px;height:80px;border:' + border + '" src="' + src + '"/></li>')
                i++;
            }
        }
        var _style = '';
        if($.browser.msie && parseInt($.browser.version,10) < 7){
            var imgSize = Jd.getImgSize(initImg);
            imgSize.x = imgSize.x > (cfg.width - 10)?(cfg.width-10):imgSize.x;
            imgSize.y = imgSize.y > (cfg.height - 10)?(cfg.height-10):imgSize.x;
            _style = "width:" + imgSize.x + 'px;height:' + imgSize.y + 'px;';
        }
        $(div).css({width:cfg.width + 'px',height:cfg.height + 'px',border:cfg.border + 'px solid #FFEFDF',
            verticalAlign: 'middle',textAlign:'center',position:'relative'})
                .append('<div style="width:' + cfg.width + 'px;height:' + cfg.height + 'px;vertical-align:middle;">' +
                '<i style="display:inline-block;height:100%;vertical-align:middle"></i>' +
                '<img class="qt_box_img" style="' + _style + 'vertical-align:middle;max-width:' + (cfg.width - 10) + 'px;max-height:' + (cfg.height - 10) + 'px"src="' + initImg + '" >' +
                '</div><div class="qt_box_pre"  style="background:url(http://www.specl.com/images/kong.png);position:absolute;width:50%;height:100%;top:0;left:0;"></div>' +
                '<div class="qt_box_next"  style="background:url(http://www.specl.com/images/kong.png);position:absolute;width:50%;height:100%;top:0;right:0"></div>');
        div.width = cfg.width + cfg.border * 2;
        div.height = cfg.height + cfg.border * 2;
        ul.width = 102 * i;
        ul.height = 102;
        $(ul).css({width:ul.width + 'px',height:ul.height + 'px'});
        Jd.layer.addSlider({obj:div,innerDom:ul,pos:cfg.listPos});
        function changeImg(obj) {
            $(obj).addClass('qt_box_show').find('img').css({
                border:'1px solid #FFD700'
            });
            $(obj).siblings('li').each(function(){
            	$(this).removeClass('qt_box_show').find('img').css({border:"1px solid #DDDDDD"});
            });
            var _src =  cfg.data[$(obj).find('img').attr('src')];
            var _img =  $(div).find('.qt_box_img');
            if($.browser.msie && parseInt($.browser.version,10) < 7){
                var _imgSize = Jd.getImgSize(_src);
                _img.css({width:_imgSize.x + 'px',height:_imgSize.y + 'px'});
            }
            _img.attr('src',_src);
        }

        function nextImg() {
            var lis = $(ul).find('li');
            if (lis.length <= 1)return;
            var c = $(ul).find('.qt_box_show').next('li');
            if (c.length == 0) {
                c = lis.eq(0);
            }
            changeImg(c[0]);
        }

        function preImg() {
            var lis = $(ul).find('li');
            if (lis.length <= 1)return;
            var c = $(ul).find('.qt_box_show').prev('li');
            if (c.length == 0) {
                c = lis.eq(lis.length - 1);
            }
            changeImg(c[0]);
        }
        $(div).find('.qt_box_next').click(
                function() {
                    nextImg();
                }).css({cursor:'url("http://www.specl.com/images/next.cur"),auto'});
        $(div).find('.qt_box_pre').click(
                function() {
                    preImg();
                }).css({cursor:'url("http://www.specl.com/images/pre.cur"),auto'});
        var _lis = $(ul).find('li');
        _lis.click(function() {changeImg(this);});
        if(!$.browser.msie || parseInt($.browser.version,10) > 6){
        	_lis.hover(function() {
                $(this).find('img').animate({marginTop:-4}, 150);
            }, function() {
                $(this).find('img').animate({marginTop:0}, 150);
            });
        }
        function set(_src){
        	var jdom = $(div).find('img[src="' + _src + '"]').parent();
        	if(jdom.length > 0){
        		changeImg(jdom[0]);
        	}
        }
        return {
        	dom:div,
        	set:set
        }

    }
    /**
     * �����˵�
     * @param cfg{element:'',obj:{},innerDom:{},align:left|right|center,vlign:top|bottom|middle,hide:true|false,rpos:{x:..|y:..};pos:n|s|e|w}
     */
    Jd.layer = {};
    var layer = {};
    Jd.layer.outfire = function(){
    	layer.hide();
    	layer.burning = false;
    }
    Jd.layer.addSlider = function(cfg) {
        var conf = {align:'center',hide:true,pos:'s',fireLine:50,padding:15,out:{display:'none'},over:'show'};
        cfg = $.extend(conf, cfg);
        var obj = cfg.obj ? cfg.obj : document.getElementById(cfg.element);
        if (!obj || !cfg.innerDom) return;
        $(obj).bind('mouseover', layer.fire);
        $(obj).append(cfg.innerDom);
        var l = {};
        l.hide = true;
        var s = Jd.getSize(obj);
        var bw = parseInt(obj.style.borderWidth,10);
        var _s = Jd.getSize(cfg.innerDom);
        if (cfg.align == 'center' && cfg.vlign == 'middle') {
            cfg.vlign = null;
        }
        if (!cfg.rpos) {
            cfg.rpos = {x:0,y:0};
            if (cfg.align == 'center') {
                cfg.rpos.x = Math.round((s.w - _s.w) / 2);
            } else if (cfg.align == 'right') {
                cfg.rpos.x = s.w - _s.w;
            }
            if (cfg.vlign == 'middle') {
                cfg.rpos.y = Math.round((s.h - _s.h) / 2);
            } else if (cfg.vlign == 'bottom') {
                cfg.rpos.y = s.h - _s.h;
            }
        }
        bw = bw > 0?bw:0;
        l.x = cfg.rpos.x - bw;
        l.xx = l.x + _s.w;
        l.y = cfg.rpos.y;
        l.yy = l.y + _s.y;
        if (cfg.pos == 'n') {
            $(cfg.innerDom).css({top:cfg.padding + 'px',left:l.x + 'px'});
        } else if (cfg.pos == 's') {
            $(cfg.innerDom).css({bottom:cfg.padding + 'px',left:l.x + 'px'});
        } else if (cfg.pos == 'w') {
            $(cfg.innerDom).css({left:cfg.padding + 'px',top:l.y + 'px'});
        } else if (cfg.pos == 'e') {
            $(cfg.innerDom).css({right:cfg.padding + 'px',top:l.y + 'px'});
        }
        var p = Jd.getPosition(obj);
        l.dom = $(cfg.innerDom).css({ position:'absolute'}).css(cfg.out);
        layer.obj = layer.obj ? layer.obj : {};
        layer.obj[obj] = layer.obj[obj] ? layer.obj[obj] : {};
        layer.obj[obj][cfg.pos] = l;
        l.pos = cfg.pos;
        l.over = cfg.over;
        l.out = cfg.out;
        layer.ctx = layer.ctx ? layer.ctx : {};
        layer.ctx.fl = cfg.fireLine + cfg.padding;
    }
    layer.fire = function() {
        if (layer.burning){
        	return;
        }
        layer.burning = true;
        layer.ctx.obj = this;
        layer.ctx.pos = Jd.getPosition(this);
        layer.ctx.size = {w:$(this).width(),h:$(this).height()};
        $(document).bind('mousemove', layer.run);
    }
    layer.run = function(e) {
        e = e || window.event;
        var n = layer.ctx.fl;
        var x = e.clientX + $(window).scrollLeft() - layer.ctx.pos.x;
        var y = e.clientY + $(window).scrollTop() - layer.ctx.pos.y;
        var _x = layer.ctx.size.w - n;
        var _y = layer.ctx.size.h - n;
        if (x > n && y > n && x < _x && y < _y) {
            return;
        }
        if (x < 0 || x > _x + n || y < 0 || y > _y + n) {
            layer.burning = false;
            return $(document).unbind('mousemove', layer.run);
        }
        var obj = layer.obj[layer.ctx.obj];
        var pos = x <= n ? 'w' : x >= _x ? 'e' : y <= n ? 'n' : y >= _y ? 's' : 'null';
        var l = obj[pos];
        if (l && l.hide) {
            if (((l.pos == 's' || l.pos == 'n') && x >= l.x && x <= l.xx)
                    || ((l.pos == 'e' || l.pos == 'w') && y >= l.y && x <= l.yy)) {
                l.over == 'show'?l.dom.show():l.dom.css(l.over);
                l.dom.mouseout(function(e) {
                    e = e || window.event;
                    var toObj = e.relatedTarget || e.toElement;
                    while (toObj && toObj != this && toObj.tagName != 'BODY') {
                        toObj = toObj.parentNode;
                    }
                    if (toObj == this)return;
                    layer.hide();
                });
                layer.ctx.l = l;
                return l.hide = false;
            }
        }
    }
    layer.hide = function() {
        if (layer.ctx && layer.ctx.l) {
            layer.ctx.l.dom.css(layer.ctx.l.out);
            layer.ctx.l.hide = true;
        }
    }
    /**
     * @param obj
     * @param event
     * @param handler
     */
    Jd.bind = function(event, obj, handler) {
        if (window.attachEvent) {
            obj.setCapture(true);
            obj.attachEvent('on' + event, handler);
        } else {
            obj.addEventListener(event, handler, true);
        }
    };

    Jd.bindInput = function(obj,handler){
        if (window.attachEvent) {
            obj.attachEvent('onpropertychange', handler);
        } else {
            obj.addEventListener('input', handler,true);
        }
    }
    /**
     *
     * @param c
     * @param k
     * @param max
     */
    Jd.lockNum = function(cfg){
        var conf = {lazy:true,k:2}
        cfg = $.extend(conf,cfg);
        var el = $(cfg.element);
        el.css({imeMode:'disabled'}).each(function(){
            window.attachEvent ?this.attachEvent('onpaste',breakEvent) : this.addEventListener('paste',breakEvent,false);
        });
        var k = cfg.k + 1,max=cfg.max,min=cfg.min,lazy = cfg.lazy;
        el.keydown(function(ev){
            ev = ev || window.event;
            var apply_dote = false;
            var apply_num = true;
            var val = ev.target.value;
            apply_dote = (val && val.indexOf('.') == -1)?true:false;
            apply_num = (val && val.indexOf('.') != -1
                    && val.indexOf('.') == (val.length-k)
                    && getCursorPosition(this) == val.length)? false:true;
            if(ev.keyCode == 110 || ev.keyCode == 190){
                (!apply_dote && breakEvent(ev)) || (apply_dote && (this.value = this.value + ".")),breakEvent(ev);
            }else if((ev.keyCode>95 && ev.keyCode<106)
                 || (ev.keyCode>47 && ev.keyCode<58)
                ) {
                !apply_num && breakEvent(ev);
                if((min || max) && !lazy){
                    var n = ev.keyCode>95?ev.keyCode-96:ev.keyCode-48;
                    val = parseFloat(val + '' + n,10);
                    max < val && breakEvent(ev);
                    min > val && breakEvent(ev);
                }
            }else if( ev.keyCode == 8
                     || ev.keyCode == 46
                     || ev.keyCode == 37
                     || ev.keyCode == 39
                     || ev.keyCode == 9){
                //do nothing
            }else{
                breakEvent(ev);
            }

        });
        el.blur(function (){
            var i = $.trim($(this).val());
            if(i && i.length > 0) {
                var p = parseFloat(i,10).toFixed(k-1);
                max && p > max && (p = max);
                min && p < min && (p = min);
                k > 0 ? $(this).val(parseFloat(p,10).toFixed(k-1)) : $(this).val(parseInt(p,10))
            }
        });
        function breakEvent(e){
            e = e || window.event;
            e.returnValue = false;
            e.preventDefault();
        }
        function getCursorPosition (ctrl) {
            var CaretPos = 0;
            if (document.selection) {
             ctrl.focus ();
             var Sel = document.selection.createRange ();
             Sel.moveStart ('character', -ctrl.value.length);
             CaretPos = Sel.text.length;
            } else if (ctrl.selectionStart || ctrl.selectionStart == '0')
             CaretPos = ctrl.selectionStart;
            return (CaretPos);
        }
    };
    Jd.importCss = function (model) {
    	if(model.indexOf('http') == -1) {
    		model = Jd.location.replace(FILE_NAME,model);
    	}
        var dom = document.createElement("link");
        dom.setAttribute("type", "text/css");
        dom.setAttribute("rel","stylesheet");
        dom.setAttribute("href",model);
        var heads = document.getElementsByTagName("head");
        if (heads.length) {
            heads[0].appendChild(dom);
        } else {
            document.documentElement.appendChild(dom);
        }
    };
    Jd.getJS = function(url){
        var opt = {
            method:'get',
            url:url,
            async:false,
            dataType:'script'
        }
        $.ajax(opt);
    }
    Jd.include = function(model){
        Jd.getJS(Jd.location.replace(FILE_NAME,model));
    }
    Jd.importJs = function(js){
        var dom = document.createElement("script");
        dom.setAttribute("type", "text/javascript");
        dom.innerHTML = js;
        var heads = document.getElementsByTagName("head");
        if (heads.length) {
            heads[0].appendChild(dom);
        } else {
            document.documentElement.appendChild(dom);
        }
    }
    function getLocation(){
        var s = document.getElementsByTagName("script");
        var l;
        for(var i = s.length;i>0;i--){
            if(s[i-1].src.indexOf(FILE_NAME) != -1){
                l = s[i-1].src;
                break;
            }
        }
        Jd.host = l.replace('http://','').split('/')[0];
        return l;
    }
    Jd.sign = function(obj){
        var s = obj.tagName;
        s+=obj.name;
        s+=obj.id;
        s+=obj.className;
        var p = Jd.getPosition(obj);
        s+=p.x;
        s+=p.y;
        return s;
    }
    Jd.location = getLocation();

    window.Jd = Jd;
})();
Jd.include('jd.model.js');
Jd.include('jd.check.js');
Jd.include('jd.site.js');