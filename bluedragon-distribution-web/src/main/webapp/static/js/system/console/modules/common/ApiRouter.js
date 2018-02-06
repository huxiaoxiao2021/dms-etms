/**********************************************************/
/* 数据路由模块  luyue */
/**********************************************************/
define(function(require){
	var app_domain = 'http://localhost';
	var port = "80";
	var api_path = '/system';
	// var base_path = app_domain + ":" + port + api_path;
    var base_path = api_path;


	var apiRouter = {
        /* 接口配置 */
        firstLevelMenusUrl     : base_path + '/getFirstLevelMenus',
        secondLevelMenusUrl    : base_path + '/getSecondLevelMenusById',
		involker:{}
	};

	apiRouter.involker.involkPostSync = function(apiUrl,paramObject,successFunc){
		$.ajax({
			type:'post',
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
	};

	apiRouter.involker.involkPostAsync = function(apiUrl,paramObject,successFunc){
		$.ajax({
			type:'post',
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
	};
	
	apiRouter.involker.involkGetSync = function(apiUrl,paramObject,successFunc){
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
	};
	
	apiRouter.involker.involkGetAsync = function(apiUrl,paramObject,successFunc){
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
	};		
	return apiRouter;
});