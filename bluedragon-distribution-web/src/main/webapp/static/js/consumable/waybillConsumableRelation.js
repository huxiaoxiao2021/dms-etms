/*****************************************/
/*耗材明细 shipeilin*/
/*****************************************/
$(function () {
    /*****************************************/
    /*变量及方法*/
    /*****************************************/
    /*增删改查URL*/
    var saveUrl = '/consumable/waybillConsumableRelation/save';
    var deleteUrl = '/consumable/waybillConsumableRelation/deleteByIds';
    var detailUrl = '/consumable/waybillConsumableRelation/detail/';
    var queryUrl = '/consumable/waybillConsumableRelation/listData';
    /*****************************************/
    /*组件*/
    /*****************************************/

    /*表格*/
    $.bootGrid.createNew('data-table',{
        method: 'post',
        height: 600,
        uniqueId: "itemId",                 //每一行的唯一标识，一般为主键列
        pagination: true,                   //是否显示分页（*）
        pageNumber:1,                       //初始化加载第一页，默认第一页
        pageSize: 50,                       //每页的记录行数（*）
        pageList: [50, 100],                //可供选择的每页的行数（*）
        toolbar: '#toolbar',                //工具按钮用哪个容器
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
        striped: true,                      //是否显示行间隔色
        showColumns: true,                  //是否显示所有的列
        sortable: false,                    //是否启用排序
        sortOrder: "asc",                   //排序方式
        showRefresh: true,                  //是否显示刷新按钮
        minimumCountColumns: 2,             //最少允许的列数
        clickToSelect: true,                //是否启用点击选中行
        showToggle:true,                    //是否显示详细视图和列表视图的切换按钮
        // showPaginationSwitch:true,          //是否显示分页关闭按钮
        singleSelect:true,
        onRefreshOptions:function () {
            this.tableBlocker = $.pageBlocker.block();
        },
        onLoadSuccess:function (res) {
            if(res.rows == null){
                $('#data-table').bootstrapTable('removeAll');
            }
            if(res.statusCode != 200){
                $.msg.error('查询失败',res.statusMessage);
            }
            $.pageBlocker.close(this.tableBlocker);
        },
        onLoadError:function (res) {
            $('#data-table').bootstrapTable('removeAll');
            $.pageBlocker.close(this.tableBlocker);
        },
        columns: [
            {
                field: 'waybillCode',
                title: '运单号',
            },{
                field: 'consumableCode',
                title: '耗材ID'
            },{
                field: 'waybillCode',
                title: '耗材类型',
            },{
                field: 'consumableCode',
                title: '耗材名称'
            },{
                field: 'receiveQuantity',
                title: '规格（厘米）',
            },{
                field: 'waybillCode',
                title: '体积（立方厘米）',
            },{
                field: 'consumableCode',
                title: '体积系数'
            },{
                field: 'receiveQuantity',
                title: '单位',
            },{
                field: 'receiveQuantity',
                title: '揽收数量',
            },{
                field: 'confirmQuantity',
                title: '确认数量'
            },{
                field: 'operateUserErp',
                title: '操作人ERP',
            }
        ]
    });

    /*表单验证*/
    $.formValidator.createNew('query-form',{
        excluded:[":disabled"],
        live: 'enabled',
        submitButtons: 'button[type="submit"]',
        message: '验证不通过',
        fields: {
            infoId:{
                validators: {
                    notEmpty: {
                        message: '巡检记录ID为空！'
                    }
                }
            }
        }
    });

    /*****************************************/
    /*按钮动作*/
    /*****************************************/

    /*查询巡检记录明细*/
    $('#btn_query').click(function () {
        /*进行查询参数校验*/
        var flag = $.formValidator.isValid('query-form');
        if(flag == true)
        {
            /*获取参数*/
            var queryParams = $.formHelper.serialize('query-form');

            /*表格查询*/
            $.bootGrid.refreshOptions('data-table',queryUrl,queryParams);
        }else{
            $.msg.warn('查询条件有误','请您检查查询条件是否有误');
        }
    });

});
