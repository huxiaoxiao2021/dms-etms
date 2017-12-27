/******************************/
/*各插件通用工具类 luyue5 */
/******************************/
jQuery.extend({

    /******************************/
    /*通用*/
    /******************************/
    go:function(url){
        window.location.href=url;
    },
    ajaxHelper:{
        doPostSync:function (apiUrl,paramObject,successFunc) {
          $.ajax({
              type:'post',
              data:paramObject,
              url:apiUrl,
              async:false,
              dataType:'json',
              contentType: 'application/json',
              error:function(XMLHttpRequest, textStatus, errorThrown){
                  console.log('involk failded');
              },
              success:function(res){
                  successFunc(res);
                  return res;
              }
          });
        },
        doPostAsync:function (apiUrl,paramObject,successFunc) {
            $.ajax({
                type:'post',
                data:paramObject,
                url:apiUrl,
                async:true,
                dataType:'json',
                contentType: 'application/json',
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    console.log('involk failded');
                },
                success:function(res){
                    successFunc(res);
                    return res;
                }
            });
        },
        doGetSync:function (apiUrl,paramObject,successFunc) {
            $.ajax({
                type:'get',
                data:paramObject,
                url:apiUrl,
                async:false,
                dataType:'json',
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    console.log('involk failded');
                },
                success:function(res){
                    successFunc(res);
                    return res;
                }
            });
        },
        doGetAsync:function (apiUrl,paramObject,successFunc) {
            $.ajax({
                type:'get',
                data:paramObject,
                url:apiUrl,
                async:true,
                dataType:'json',
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    console.log('involk failded');
                },
                success:function(res){
                    successFunc(res);
                    return res;
                }
            });
        }
    },
    /******************************/
    /*消息框*/
    /******************************/
    msg:{
        alert:function (msg) {
            swal({title:msg});
        },
        warn:function (title,msg) {
            swal({title:title,text:msg,type : "warning"});
        },
        ok:function (title,msg) {
            swal({title:title,text:msg,type : "success"});
        },
        error:function (title,msg) {
            swal({title:title,text:msg,type : "error"});
        },
        confirm:function(msg,callback){
            swal({
                title : msg,
                type : "warning",
                showCancelButton : true,
                confirmButtonColor : "#DD6B55",
                confirmButtonText : "确定",
                cancelButtonText : "取消",
                closeOnConfirm : false
            }, function(){
                callback.call(this);
            });
        }
    },
    /******************************/
    /*时间选择器*/
    /******************************/
    datePicker:{
        createNew:function (options) {
            laydate.render(options);
        },
        createDefault: function (domId,onChangeFunc) {
            laydate.render({
                elem: '#' + domId,
                type: 'datetime',
                theme: '#3f92ea',
                change: function(value, date, endDate){
                    onChangeFunc(value, date, endDate);
                }
            });
        },
        setValue:function (domId,dateStr) {
            $('#' + domId).val(dateStr)
        },
        getValue:function(domId){
            return $('#' + domId).val();
        }
    },
    /******************************/
    /*表格*/
    /******************************/
    bootGrid:{
        createNew:function(domId,options) {
            $('#' + domId).bootstrapTable(options);
        },
        /*只能单次刷新不能作用于分页*/
        refresh:function (domId,params) {
            $('#' + domId).bootstrapTable('refreshOptions',{pageNumber:1});
            $('#' + domId).bootstrapTable('refresh',params);
        },
        /*查询重置表格查询条件请用refreshOptions方法*/
        refreshOptions:function(domId,queryUrl,queryParams){
            $('#' + domId).bootstrapTable('refreshOptions',{pageNumber:1});
            $('#' + domId).bootstrapTable('refreshOptions',{
                url:queryUrl,
                queryParams:function (originParams) {
                    queryParams.offset = originParams.offset;
                    queryParams.limit = originParams.limit;
                    return queryParams;
                }
            });
        }
    },
    /******************************/
    /*下拉框*/
    /******************************/
    combobox:{
        createNew:function (domId,options) {
            $('#' + domId).select2(options);
        },
        appendData:function (domId,id,text) {
            var newOption = new Option(text, id, false, false);
            $('#' + domId).append(newOption).trigger('change');
        },
        createDefault:function (domId,placeHolderText,isMulti,width,data) {
            $('#' + domId).select2({
                width: width,
                placeholder:placeHolderText,
                allowClear:true,
                data:data,
                multiple:isMulti,
            });
        },
        getSelected:function (domId) {
            var data = $('#' + domId).select2("data");
            return data;
        }
    },
    /******************************/
    /*表单支持*/
    /******************************/
    formHelper:{
        /*序列化表单 转为对象*/
        serialize:function (formId) {
            var form = $('#' + formId);
            var o = {};
            $.each(form.serializeArray(), function (index) {
                if (o[this['name']]) {
                    o[this['name']] = o[this['name']] + "," + this['value'];
                } else {
                    o[this['name']] = this['value'];
                }
            });
            return o;
        }
    },
    /******************************/
    /*表单验证*/
    /******************************/
    formValidator:{
        createNew:function (domId,options) {
            $("#" + domId).bootstrapValidator(options);
        },
        /*进行表单校验并获取校验结果*/
        isValid:function (domId) {
            $("#" + domId).data('bootstrapValidator').validate();
            var flag = $("#" + domId).data('bootstrapValidator').isValid();
            return flag;
        }
    },
    /******************************/
    /*时间操作大全*/
    /******************************/
    dateHelper:{
        /*date对象转字符串 格式:YYYY-MM-DD HH:mm:ss*/
        formatDateTime:function (date) {
            return window.date.format(date,'YYYY-MM-DD HH:mm:ss');
        },
        /*date对象转字符串 格式:YYYY-MM-DD*/
        formatDate:function (date) {
            return window.date.format(date,'YYYY-MM-DD');
        },
        /*格式:YYYY-MM-DD HH:mm:ss 字符串转为Date对象*/
        parseDateTime:function (dateStr) {
            return window.date.parse(dateStr,'YYYY-MM-DD HH:mm:ss');
        },
        /*格式:YYYY-MM-DD 字符串转为Date对象*/
        parseDateTime:function (dateStr) {
            return window.date.parse(dateStr,'YYYY-MM-DD');
        },
        /*获取某一时间n天前或n天后时间date对象*/
        addDays:function(date,days){
            return window.date.addDays(date, days);
        }

    },
    /******************************/
    /*excel*/
    /******************************/
    exportExcel:function(url){
        this.exportFile("确定要导出为EXCEL吗？",url,1);

    },
    exportCsv:function(url){
        alert(url);
        this.exportFile("确定要导出为CSV吗？",url,2);

    },
    exportFile:function(title,url,exportType){
        swal({
            title : title,
            type : "warning",
            showCancelButton : true,
            confirmButtonColor : "#DD6B55",
            confirmButtonText : "确定",
            cancelButtonText : "取消",
            closeOnConfirm : true
        }, function(){
            if(url.indexOf("?")!=-1){
                location.href = url+"&exportType="+exportType;
            }else{
                location.href = url+"?exportType="+exportType;
            }
        });

    }



});