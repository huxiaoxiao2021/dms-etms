/**
 * 省区站点级联插件
 *
 *
 */
(function($){
    $.fn.sitePluginSelect = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(
                arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.sitePluginSelect');
        }
    };
    $.fn.sitePluginSelect._default = {
        orgId : null,
        createSiteCode : null, //场地编号 反选是传入，省区和枢纽会自动查出来
        createSiteName : null, //场地名称
        provinceAgencyCode : null, //省区编码
        areaHubCode : null, //枢纽编码
        provinceAgencyCodeClass : null, //省区下拉框样式
        areaHubCodeClass : null,  //枢纽下拉框样式
        createSiteCodeClass : null,  //场地下拉框样式
        provinceAgencyCodeWidth:'120px',
        areaHubCodeWidth:'120px',
        createSiteCodeWidth:'200px',
        siteTypes : [64], //默认只查询分拣中心
        callback : null
    };
    var methods = {
        _optionsDetail : null,
        init : function(_options){
            new TypeSelectClass(_options,$(this));
        }
    };
    var TypeSelectClass = function(){
        this.init.apply(this, arguments);
    };
    TypeSelectClass.prototype = {
        _options : null,
        _targetDom : null,
        _targetDomId : null,
        init : function(_options,container){
            this._options = $.extend({},$.fn.sitePluginSelect._default,_options);
            this._targetDom = container;
            this._targetDomId = container.attr('id');
            //this._targetDom.empty();
            this._initTypeSelect();

        },
        _initTypeSelect : function(){
            //初始化HTML
            this._initHtml();
            this._
            //初始化参数
            this._initParams();


            //绑定事件
            this._bindEvent();
        },
        _initHtml : function(){
            //拉取原来内容
            var oldHtmlStr = this._targetDom.html();
            var tmp = [];
            // html 构建
            //切换按钮
            //tmp.push('<table>');
            //tmp.push('<tr>')
            //tmp.push('<td>')

            tmp.push('<switchMode>');
            tmp.push('<a class="changeSiteSwitchModeBtn" nowSiteSwitchMode="org" style="cursor:pointer">切换省区</a>');
            tmp.push('</switchMode>');

            //tmp.push('</td>')

            //省区模式
            //tmp.push('<td>')

            tmp.push('<province>');
            tmp.push('<select id="provinceSelect" class="'+this._options.provinceAgencyCodeClass+'"></select>');
            tmp.push('<areaHub><select id="areaSelect" class="'+this._options.areaHubCodeClass+'"></select></areaHub>');
            tmp.push('<select id="siteSelect" class="'+this._options.createSiteCodeClass+'"></select>');
            tmp.push('<input type="hidden" id="provinceAgencyCode"  name="provinceAgencyCode" class="form-control search-param">');
            tmp.push('<input type="hidden" id="areaHubCode" name="areaHubCode" class="form-control search-param">');
            tmp.push('<input type="hidden" id="createSiteCode"  name="createSiteCode" class="form-control search-param">');
            tmp.push('</province>');
            //tmp.push('</td>')

            //原大区模式
            //tmp.push('<td>')
            tmp.push('<org>');
            tmp.push(oldHtmlStr);
            tmp.push('</org>');
            //tmp.push('</td>')

            //tmp.push('</tr>')

            //tmp.push('</table>');

            this._targetDom.html(tmp.join(''));
            this._hideProvince();
        },
        _initParams : function () {
            //补充省区和枢纽数据，以传入站点查询到的数据为准
            if(this._options.createSiteCode){
                //存在站点则查询站点
                var site = this._querySiteBySiteCode(this._options.createSiteCode);
                if(site){
                    this._options.provinceAgencyCode = site.provinceAgencyCode;
                    this._options.areaHubCode = site.areaCode;
                    this._options.createSiteName = site.siteName;
                }
            }
        },
        _bindEvent : function (){
            //绑定事件
            //切换按钮
            let that = this;
            this._targetDom.find('.changeSiteSwitchModeBtn').on('click',function (e) {
                that._changeSiteSwitchModeClick();
            });

            //省区变化
            this._targetDom.find('province #provinceSelect').on("change", function (e) {
                that._provinceSelectChange();
                that._targetDom.find('province #areaSelect').val(that._targetDom.find('province #areaSelect').val()).trigger('change');
                that._targetDom.find('province #siteSelect').val(that._targetDom.find('province #siteSelect').val()).trigger('change');
            });
            //枢纽变化
            this._targetDom.find('province #areaSelect').on("change", function (e) {
                that._areaHubChange();
                that._targetDom.find('province #siteSelect').val(that._targetDom.find('province #siteSelect').val()).trigger('change');
            });
            //站点变化
            this._targetDom.find('province #siteSelect').on("change", function (e) {
                that._siteChange();
            });

        },
        _provinceSelectChange : function (){
            //省区变化事件
            var _s = this._targetDom.find('province #provinceSelect').val();
            this._targetDom.find('province [name=provinceAgencyCode]').val(_s);
            //清理待上传的数据
            this._targetDom.find('province [name=createSiteCode]').val("");
            this._targetDom.find('province [name=areaHubCode]').val("");
            //清理下拉框
            this._targetDom.find('province #areaSelect').html('');
            this._targetDom.find('province #siteSelect').html('');
            var province = this._targetDom.find('province #provinceSelect').select2("data");
            //单选只取第一个
            if (province[0]) {
                //是否存在枢纽
                if(province[0].hasAreaFlag){
                    //存在 开始渲染枢纽
                    this._initAreaSelect(province[0].id,this._options.areaHubCode);
                    this._targetDom.find('province areaHub').show();
                    //this._targetDom.find('province #areaSelect').val(this._targetDom.find('province #areaSelect').val()).trigger('change');
                }else{
                    this._targetDom.find('province areaHub').hide();
                    //加载站点列表
                    this._initSiteSelect(this._targetDom.find('province [name=provinceAgencyCode]').val(),this._targetDom.find('province [name=areaHubCode]').val(),this._options.createSiteCode);
                }
            }

        },
        _areaHubChange : function (){
            //枢纽变化事件
            var _s = this._targetDom.find('province #areaSelect').val();
            this._targetDom.find('province [name=areaHubCode]').val(_s);

            this._targetDom.find('province #siteSelect').html('');
            //初始化站点插件
            this._initSiteSelect(this._targetDom.find('province [name=provinceAgencyCode]').val(),this._targetDom.find('province [name=areaHubCode]').val(),this._options.createSiteCode);

            //触发站点选择事件
            //this._targetDom.find('province #siteSelect').val(this._targetDom.find('province #siteSelect').val()).trigger('change');


        },
        _siteChange : function (){
            //站点变化事件
            var _s = this._targetDom.find('province #siteSelect').val();
            this._targetDom.find('province [name=createSiteCode]').val(_s);
        },
        _changeSiteSwitchModeClick : function (){
            //切换模式绑定事件
            var changeSiteSwitchModeBtn = this._targetDom.find('.changeSiteSwitchModeBtn');
            if(changeSiteSwitchModeBtn.attr('nowSiteSwitchMode') == 'org'){
                //由机构切换到省区
                //调整原查询条件属性值
                this._reNameSubmitData('org');
                changeSiteSwitchModeBtn.html('切换七大区');
                changeSiteSwitchModeBtn.attr('nowSiteSwitchMode','province');


                this._targetDom.find('province #provinceSelect').html('');
                this._targetDom.find('province #areaSelect').html('');
                this._targetDom.find('province #siteSelect').html('');
                this._targetDom.find('province [name=provinceAgencyCode]').val('');

                //获取所有省区

                this._initProvinceSelect(this._options.provinceAgencyCode);


                changeSiteSwitchModeBtn.attr('loadedProvinceSelect','loadedProvinceSelect');


                this._hideOrg();
                this._showProvince();
            }else{
                //由省区切换机构
                this._reNameSubmitData('province');
                changeSiteSwitchModeBtn.html('切换省区');
                changeSiteSwitchModeBtn.attr('nowSiteSwitchMode','org');

                this._showOrg();
                this._hideProvince();
            }

        },
        _reNameSubmitData : function (switchType){
            //重命名调整元素防止误提交
            if(switchType == 'org'){
                //由机构切换到省区

                $('#'+this._targetDomId+' org input,#'+this._targetDomId+' org select').each(function (i) {
                    if($(this).attr('id')){
                        $(this).attr('id','reName'+$(this).attr('id'));
                    }
                    if($(this).attr('name')){
                        $(this).attr('name','reName'+$(this).attr('name'));
                    }
                });

                $('#'+this._targetDomId+' province input,#'+this._targetDomId+' province select').each(function (i) {
                    if($(this).attr('id') && $(this).attr('id').startsWith('reName')){
                        $(this).attr('id',$(this).attr('id').replace('reName',''));
                    }
                    if($(this).attr('name') && $(this).attr('name').startsWith('reName')){
                        $(this).attr('name',$(this).attr('name').replace('reName',''));
                    }
                });

            }else{

                $('#'+this._targetDomId+' province input,#'+this._targetDomId+' province select').each(function (i) {
                    if($(this).attr('id')){
                        $(this).attr('id','reName'+$(this).attr('id'));
                    }
                    if($(this).attr('name')){
                        $(this).attr('name','reName'+$(this).attr('name'));
                    }
                });

                $('#'+this._targetDomId+' org input,#'+this._targetDomId+' org select').each(function (i) {
                    if($(this).attr('id') && $(this).attr('id').startsWith('reName')){
                        $(this).attr('id',$(this).attr('id').replace('reName',''));
                    }
                    if($(this).attr('name') && $(this).attr('name').startsWith('reName')){
                        $(this).attr('name',$(this).attr('name').replace('reName',''));
                    }
                });

            }
        },
        _queryAllProvinceAgencyInfo:function () {
            //获取所有省区
            var result = [];
            var url = "/common/queryAllProvinceAgencyInfo";
            var param = {};
            $.ajax({
                type : "post",
                url : url,
                data : param,
                async : false,
                success : function (data) {
                    if(data.code == 200 && data.data != null){
                        var provinces = data.data;
                        for(var i in provinces){
                            result.push({
                                id:provinces[i].provinceAgencyCode,
                                text:provinces[i].provinceAgencyName,
                                hasAreaFlag:provinces[i].hasAreaFlag});
                        }
                    }
                }
            });
            return result;

        },
        _queryAllAreaInfo : function(provinceAgencyCode){
            //获取省区下枢纽
            var result = [];
            var url = "/common/queryAllAreaInfo";
            var param = {'provinceAgencyCode':provinceAgencyCode};
            $.ajax({
                type : "post",
                url : url,
                data : param,
                async : false,
                success : function (data) {
                    if(data.code == 200 && data.data != null){
                        var areaInfos = data.data;
                        for(var i in areaInfos){
                            result.push({
                                id:areaInfos[i].areaCode,
                                text:areaInfos[i].areaName});
                        }
                    }
                }
            });
            return result;
        },
        _querySitePageByCondition : function(provinceAgencyCode,areaHubCode,siteName){
            //获取站点列表
            var result = [];
            var url = "/common/querySitePageByCondition";
            var params = {
                provinceAgencyCode:provinceAgencyCode,
                areaCode:areaHubCode?areaHubCode:null,
                siteName:siteName,
                siteTypes:this._options.siteTypes
            };
            $.ajax({
                type : "post",
                url : url,
                data : JSON.stringify(params),
                dataType:'json',
                contentType: 'application/json',
                async : false,
                success : function (data) {
                    if(data.code == 200 && data.data != null && data.data.data != null){
                        var siteInfos = data.data.data;
                        for(var i in siteInfos){
                            result.push({
                                id:siteInfos[i].siteCode,
                                text:siteInfos[i].siteCode + '-' + siteInfos[i].siteName});
                        }
                    }
                }
            });
            return result;
        },
        _querySiteBySiteCode : function (siteCode){
            //根据站点编码获取站点
            var result = null;
            var url = "/common/querySitePageByCondition";
            var params = {
                siteCodes:[siteCode],
                siteTypes:this._options.siteTypes
            };
            $.ajax({
                type : "post",
                url : url,
                data : JSON.stringify(params),
                dataType:'json',
                contentType: 'application/json',
                async : false,
                success : function (data) {
                    if(data.code == 200 && data.data != null && data.data.data != null){
                        result = data.data.data[0];
                    }
                }
            });
            return result;

        },
        _initProvinceSelect : function (provinceAgencyCode) {
            //初始化省区
            var provinces = this._queryAllProvinceAgencyInfo();
            provinces.unshift({id:'-1',text:'请选择省区'});
            let selectedData = [] ;
            let that = this;
            this._targetDom.find('province #provinceSelect').select2({
                width: that._options.provinceAgencyCodeWidth,
                placeholder: '请选择省份',
                allowClear: true,
                data: provinces
            });
            if(provinceAgencyCode){
                //存在直接反选
                this._targetDom.find('province #provinceSelect').val(provinceAgencyCode).trigger('change');
            }else{
                //触发一次选择事件
                this._targetDom.find('province #provinceSelect').val(this._targetDom.find('province #provinceSelect').val()).trigger('change');
            }
        },
        _initAreaSelect : function (provinceAgencyCode,areaHubCode) {
            //初始化枢纽
            var areaInfos = this._queryAllAreaInfo(provinceAgencyCode);
            areaInfos.unshift({id:'-1',text:'请选择枢纽'});
            let that = this;
            this._targetDom.find('province #areaSelect').select2({
                width: that._options.areaHubCodeWidth,
                placeholder: '请选择枢纽',
                allowClear: true,
                data: areaInfos,

            });
            if(areaHubCode && this._options.provinceAgencyCode == provinceAgencyCode){
                //存在直接反选
                this._targetDom.find('province #areaSelect').val(areaHubCode).trigger('change');
            }
        },
        _initSiteSelect : function (provinceAgencyCode,areaHubCode,siteCode) {
            //初始化站点选择组件
            let that = this;
            var selectedData = [];
            var needChangeEvent = false;
            if(siteCode && this._options.provinceAgencyCode == provinceAgencyCode && this._options.areaHubCode == areaHubCode){
                //都匹配时在勾选
                selectedData.push({id: siteCode, text: this._formatShowSiteName(this._options.createSiteCode ,this._options.createSiteName), selected: true})
                needChangeEvent = true;
            }
            this._targetDom.find('province #siteSelect').select2({
                ajax: {
                    url: '/common/querySitePageByCondition',
                    dataType: 'json',
                    type: 'POST',
                    contentType: 'application/json',
                    async : false,
                    delay: 250,
                    data: function(params) {
                        return JSON.stringify({
                            provinceAgencyCode:provinceAgencyCode,
                            areaCode:areaHubCode?areaHubCode:null,
                            siteName:params.term,
                            siteTypes:that._options.siteTypes
                        });
                    },
                    processResults: function(data, params) {
                        params.page = params.page || 1;
                        var sites = [];
                        if(data.code == 200 && data.data != null && data.data.data != null){
                            var siteInfos = data.data.data;
                            for(var i in siteInfos){
                                sites.push({
                                    id:siteInfos[i].siteCode,
                                    text:that._formatShowSiteName(siteInfos[i].siteCode,siteInfos[i].siteName)});
                            }
                        }

                        return {
                            results: sites
                            // pagination: {
                            //     more: (params.page * 30) < data.total_count
                            // }
                        };
                    },
                    cache: true
                },
                minimumInputLength: 0,
                language: {
                    inputTooShort: function(args) {
                        //var remainingChars = args.minimum - args.input.length;
                        return "请至少输入 " + args.minimum + " 个文字";
                    },
                    noResults: function() {
                        return "没有查找到对应场地请检查输入的内容是否正确";
                    }
                },
                placeholder: '请输入场地名称检索',
                width: that._options.createSiteCodeWidth,
                data: selectedData// 默认选中
            });
            //默认加载站点列表
            // var siteInfos = this._querySitePageByCondition(provinceAgencyCode,areaHubCode,null)
            // siteInfos.unshift({id:'-1',text:'请选择场地'});
            // this._targetDom.find('province #siteSelect').select2({data:siteInfos});
            //触发站点选择事件
            //this._targetDom.find('province #siteSelect').val(this._targetDom.find('province #siteSelect').val()).trigger('change');

            if(needChangeEvent){
                this._targetDom.find('province #siteSelect').val(siteCode).trigger('change');
            }
        },
        _hideProvince : function (){
            //隐藏省区模式
            this._targetDom.find('province').hide();
        },
        _showProvince : function (){
            //显示省区模式
            this._targetDom.find('province').show();
        },
        _hideOrg : function (){
            //隐藏省区模式
            this._targetDom.find('org').hide();
        },
        _showOrg : function (){
            //隐藏省区模式
            this._targetDom.find('org').show();
        },
        _formatShowSiteName : function (code,name){
            //格式化显示的场地名称内容
            return code + '-' + name;
        }
    };
})(jQuery)
