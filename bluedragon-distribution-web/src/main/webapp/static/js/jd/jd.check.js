/**
 * validate.js
 */
(function($) {

    var JConsole = {} ,debug = false;
    JConsole.info = function(msg) {
        if ($.browser.mozilla && debug) {
            console.info(msg);
        }
    }
    JConsole.error = function(msg) {
        if ($.browser.mozilla) {
            console.error(msg);
        }
    }
    var opt = {
        lazy:false,
        event:'change',
        observer:true,
        strict:true,
        showTip:false,
        tipzIndex:1,
        forbidden:false
    };

    var rule = {
        'default':'@val', //如果this.value == @val 那么验证结果必定为真,
        'require':'@name不能为空',
        'length':'@name长度须在@lower至@greater之间',
        'email':'@name不是电子邮箱格式',
        'number':'@name不是数字',
        'decimal':'@name不是数字',
        'idCard':'@name必须是身份证号码',
        'mobile':'@name不是手机号码',
        'telephone':'@name不是固定电话或手机号码',
        'postCode':'@name不是邮编',
        'bankCardNo':'@name不是正确的银行卡号',
        'vehicleLicense':'@name不是车牌号码',
        'account':'@name只能包含字母、数字@ext并以字母开头',
        'no':'只能包含字母和数字'
    }
    var handler = {
        'require':_req,
        'length':_len,
        'email':/\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/,
        'number':_num,
        'idCard':/^(\d{15}$|^\d{18}$|^\d{17}(\d|X|x))$/,
        'mobile':/^(13[0-9]|15[0-9]|18[0-9])\d{8}$/,
        'telephone':/(^(0[0-9]{2,3}-)?([2-9][0-9]{6,7})+(-[0-9]{1,4})?$)|(^(13[0-9]|15[0-9]|18[0-9])\d{8}$)/,
        'postCode':/^[a-zA-Z0-9 ]{3,12}$/,
        'decimal':_decimal,
        'bankCardNo':/^[0-9]{19}$/,
        //'vehicleLicense':/^[\u4E00-\u9FA5]([0-9A-Z]{6})|([0-9A-Z]{5}[\u4E00-\u9FA5]{1})$/,
        'vehicleLicense':/^[\u4E00-\u9FA5][A-Z][0-9A-Z]{5}$/,
        'account':/^\w+[\d\w]*$/,
        'no':/^[a-zA-Z0-9]+$/
    }

    var T = {},C = {},R = {},N = {},E = {},V = {};

    $.fn.validateSingle = function() {
        if (this && this.length) {
            return _check.call(this[0]).r;
        }
    }

    $.closeValidate = function(o) {
        opt.forbidden = o;
    }

    function _check() {
        try {
            if (opt.forbidden) {
                return;
            }
            var s = _sign(this),result = {};
            if ((!N[s] && (!this.value || $(this).val() == ''))
                    || (this.value && V[s] && this.value == V[s])) {
                JConsole.info('<...skip all validate...>');
                $(this).clearMsg();
                R[s] = {r:true};
                return R[s];
            }
            if (!opt.strict && C[s]) {
                return R[s];
            }
            JConsole.info('execute check :' + s)
            for (var r in T[s]) {
                if (r != 'sub') {
                    if (E[s] && E[s][r]) {
                        JConsole.info('execute custom check:' + r);
                        JConsole.info(typeof E[s][r]);
                        result = E[s][r].call(this);
                        JConsole.info('custom check over');
                    } else {
                        if (typeof handler[r] == 'object') {
                            result.r = handler[r].test(this.value);
                            !result.r && (result.msg = _msgHandler(rule[r], this));
                        } else if (handler[r]) {
                            result = handler[r](this, T[s][r]);
                        }
                    }
                } else {
                    JConsole.info('call join func....')
                    for (var i in T[s][r]) {
                        result = T[s][r][i].call(this);
                        if (!result.r) {
                            break;
                        }
                    }
                }
                if (!result.r) {
                    break;
                }
            }
            R[s] = result,C[s] = true;
            _showMsg(this, result);
            return result.r;
        } catch(err) {
            JConsole.error(err)
        }
    }

    $.fn.setDefaultVal = function(val) {
        val && $.trim(val) != '' ? (V[_sign(this)] = $.trim(val)) : (V[_sign(this)] = null);
    }

    $.fn.validateInit = function(setting) {
        JConsole.info('checkInit....')
        JConsole.info('TagName:' + this.attr('tagName'));
        if (this.attr('tagName') != 'FORM') {
            throw new Error('Invalid Element :' + this.attr('tagName'))
        }
        opt = $.extend({}, opt, setting);
        opt.observer && (opt.ob = 2.8);
        this.bind('submit',
                function() {
                    if (!opt.forbidden && !$(this).validate()) {
                        return false;
                    }
                }).find('select,textarea,input[type!="button"][type!="submit"][type!="reset"]').each(function() {
                    !T[_sign(this)] && _init.call(this);
                });
    }

    function _init(forceInit) {
        var v = $(this).attr('v'),s = _sign(this);
        if (v || forceInit) {
            JConsole.info('Init...' + s)
            !T[s] && (T[s] = {});
            if (v) {
                var arr = v.split(';');
                for (var i in arr) {
                    if ($.trim(arr[i]) == '') {
                        continue;
                    }
                    var arg = arr[i].split(':');
                    arg[0] != 'default' ? (T[s][arg[0]] = arg.length > 1 ? arg[1] : true ) :  (V[s] != '' && (V[s] = arg[1]));
                    arg[0] == 'require' && (N[s] = true) ;
                    JConsole.info(arg[0] + ',' + T[s][arg[0]]);
                }
            }
            if (!opt.lazy) {
                JConsole.info('handler bind...' + this.tagName)
                if (this.tagName == 'SELECT') {
                    $(this).change(function() {
                        JConsole.info('type...1')
                        C[_sign(this)] = false;
                        _check.call(this);
                    });
                } else if (this.type != 'text' && this.type != 'password'
                        && this.tagName != 'TEXTAREA') {
                    $(this).click(function() {
                        JConsole.info('type...2')
                        C[_sign(this)] = false;
                        _check.call(this);
                    })
                } else {
                    JConsole.info('type...3')
                    $(this).bind(opt.event, _check).keyup(function() {
                        C[_sign(this)] = false;
                    });
                }

                if (this.tagName == 'TEXTAREA') {
                    // this.oninput = _check
                }
            }
        }
    }

    $.fn.validateJoin = function(handler, ru) {
        if (this[0] && this[0].tagName) {
            var s = _sign(this),rule = ru ? ru : 'sub';
            !T[s] && ( _init.call(this[0], true));
            if (!ru) {
                !T[s][rule] && (T[s][rule] = []);
                T[s][rule].push(handler);
            } else {
                !E[s] && (E[s] = {},E[s][ru] = handler);
            }
        }
    }

    $.fn.validate = function(c) {
        typeof c == 'boolean' && (opt.forbidden = !c);
        if (this.attr('tagName') != 'FORM') {
            throw new Error('Invalid Element :' + this.attr('tagName'))
        }
        if(opt.forbidden) {
            return true;
        }
        this.find('select,textarea,input[type!="button"][type!="submit"][type!="reset"]').each(function() {
            if (T[_sign(this)]) {
                _check.call(this);
            }
        });
        var result = true;
        for (var r in R) {
            if (!R[r].r) {
                result = false;
                break;
            }
        }
        return result;
    }

    $.fn.clearMsg = function() {
        if (this[0] && this[0].tagName) {
            JConsole.info('clear msg .....')
            this.find('select,textarea,input[type!="button"][type!="submit"][type!="reset"]').andSelf().each(function() {
                if (T[_sign(this)]) {
                    C[_sign(this)] = false;
                    Jd.clearTip(this);
                    if (!opt.showTip) {
                        $(this).unbind('focus', showTip).unbind('blur', closeTip);
                        $(this).removeClass('areaError').removeClass('areaPass')
                                .removeClass('inputPass').removeClass('inputError')
                    }
                }
            });
        }
    }

    function _sign(dom) {
        dom = dom.tagName ? dom : dom[0];
        var sign = dom.id + dom.name + dom.tagName;
        if (dom.tagName == 'INPUT') {
            if (dom.type == 'checkbox' || dom.type == 'radio') {
                sign += dom.value;
            }
        }
        return sign;
    }

    function _req(dom) {
        JConsole.info('>>>>>>req::');
        var r = {r:false,msg:_msgHandler(rule['require'], dom)},val = dom.value;
        if (dom.tagName == 'SELECT') {
            val = $(dom).find('option:selected').attr('value');
        }
        JConsole.info(dom.tagName);
        JConsole.info(val);
        if (dom.type == 'radio' || dom.type == 'checkbox') {
            $('input[name="' + dom.name + '"]:checked').length == 0 ? (r.r = false) : (r.r = true);
        } else if (val && $.trim(val) != '') {
            r.r = true;
        }
        return r;
    }

    function _len(dom, param) {
        var r = {r:false,msg:_msgHandler(rule['length'], dom)},val = dom.value;
        var lower,greater,val = dom.value;
        if (param.indexOf(',') == -1) {
            lower = 0;
            greater = parseInt(param, 10);
        } else {
            var temp = param.split(',');
            lower = parseInt(temp[0], 10);
            greater = parseInt(temp[1], 10);
        }
        if(lower == greater) {
            r.msg = '长度必须是' + lower + '位';
        }else{
            r.msg = r.msg.replace('@lower', lower).replace('@greater', greater);
        }
        var l = val.length ? val.length : 0;
        if (l >= lower && l <= greater) {
            r.r = true;
        }
        return r;
    }

    function _decimal(dom,param) {
        var r = {r:true,msg:_msgHandler(rule['decimal'], dom)},val = dom.value,mc;
        var max,min,n,val = dom.value,v,reg ="^[+-]?\\d+(\\.\\d{1,n})?$",d_reg = /^[+-]?\d+(\.\d+)?$/;
        if(typeof param != 'boolean') {
            var args = param.split(',');
            args.length == 3 ? (n = args[0],max=args[1],min=args[2]):args.length == 2 ? (n = args[0],max=args[1]) : n = args[0];
            if(d_reg.test(val)) {
                d_reg = new RegExp(reg.replace('n',n));
                (n = n > 0 ? n : 0) && (mc = 10^n);
                d_reg.test(val) ? (val  && _compare(val,max) > 0 ? (r.r = false,r.msg = '不能大于' + max)
                        : ( _compare(val,min) < 0 && (r.r = false,r.msg = '不能小于' + min)))
                        :(r.r = false,r.msg = (n > 0 ? '小数位数不得多于' + n + '位':'必须是整数'))
                JConsole.info(val);
                JConsole.info(max)
                JConsole.info(min)
            }else{
                r.r = false,r.msg = '非法的数字格式';
            }
        }else{
            !d_reg.test(val) && (r.r = false )
        }
        return r;
    }

    function _compare(v1,v2) {
        JConsole.info('compare:' + v1 + ',' + v2);
        var f1 = v1.indexOf('-'),f2 = v2.indexOf('-'),f,r;
        f1 != -1 && f2 == -1 ? (r = -1) : (f1 == -1 && f2 != -1) ? (r = 1) : (r = 0);
        f1 != -1 && (v1 = v1.replace('-',''),f = true);
        f2 != -1 ?  (v2 = v2.replace('-','')) : (f = false);
        var a1 = jQuery.trim(v1).split('.'),a2 = jQuery.trim(v2).split('.');
        r == 0 && (a1[0].length > a2[0].length ? (r = 1) : (a1[0].length < a2[0].length) ? (r = -1):(r = checkQueue(a1[0],a2[0])));
        r == 0 && (r = checkQueue(a1[1],a2[1]));
        function checkQueue(q1,q2) {
            q1 = q1 && q1 != '' ? q1 : '0',q2 = q2 && q2 != '' ? q2 : '0';
            var len = Math.max(q1.length,q2.length);
            for(var i = len - 1; i >= 0 ; i--) {
                if(q1.charAt(i) > q2.charAt(i) && q1.charAt(i) != '0') {
                    return 1;
                }else if(q1.charAt(i) < q2.charAt(i) && q2.charAt(i) != '0') {
                    return -1;
                }
            }
            return 0;
        }
        return f ? (r > 0 ? r = -1 : r < 0 ? r = 1 : r ) : r;
    }

    function _num(dom,param) {
        var r = {r:true,msg:_msgHandler(rule['number'], dom)},val = dom.value;
        var max,min,reg = /^\d+$/;
        !reg.test(val) && (r.r = false);
        if(typeof param != 'boolean' && r.r) {
             var args = param.split(',');
             args.length == 2 ? (max=args[0],min=args[1]):  (max = args[0]);
             parseInt(val,10) > parseInt(max,10) ? (r.r = false,r.msg = '不能大于' + max)
                     : (min && parseInt(val,10) < parseInt(min,10) ? (r.r = false,r.msg='不能小于' + min) : (r.r = true));
        }
        return r;
    }

    function _msgHandler(msg, dom) {
        var n = dom.title ? dom.title : '';
        if (msg.indexOf('@name') != -1) {
            msg = msg.replace('@name', n);
        }
        return msg;
    }

    function showTip() {
        JConsole.info('showTip:' + this);
        Jd.errTip(this.msgBox, M[_sign(this.msgBox)], opt.ob, null, opt.tipzIndex);
    }

    function closeTip() {
        Jd.clearTip(this.msgBox);
    }

    M = {}
    function _showMsg(obj, result) {
        JConsole.info(obj.tagName + ':' + result.r);
        var $box = $(obj).parents('.validateBox'),dom;
//        if (dom.type == 'radio' || dom.type == 'checkbox') {
            $box.length > 0 ? dom = $box[0] : dom = obj;
            obj.msgBox = dom;
            JConsole.info(dom);
//        }
        if (!result.r) {
            JConsole.info('Msg:' + result.msg);
            if (!opt.showTip) {
                Jd.clearTip(dom);
                M[_sign(dom)] = result.msg;
                $(obj).unbind('focus', showTip).unbind('blur', closeTip);
                $(obj).bind('focus', showTip).bind('blur', closeTip);
                if (dom.tagName == 'TEXTAREA') {
                    $(dom).addClass('areaError').removeClass('areaPass')
                } else {
                    $(dom).addClass('inputError').removeClass('inputPass')
                }
            } else {
                Jd.errTip(dom, result.msg, opt.ob, null, opt.tipzIndex);
            }
        } else {
            Jd.succTip(dom, opt.ob, null, opt.tipzIndex);
            if (!opt.showTip) {
                JConsole.info('unbind .....')
                $(obj).unbind('focus', showTip).unbind('blur', closeTip);
                if (dom.tagName == 'TEXTAREA') {
                    $(dom).addClass('areaPass').removeClass('areaError')
                } else {
                    $(dom).addClass('inputPass').removeClass('inputError')
                }
            }
        }
    }

    $.fn.formSerialize = function() {
        this.find('input[name="sbmt_timestamp"]').length == 0 && this.append('<input name="sbmt_timestamp" type="hidden"/>');
        this.find('input[name="sbmt_timestamp"]').val(new Date().getTime());
        return this.serialize();
    }

})(jQuery)