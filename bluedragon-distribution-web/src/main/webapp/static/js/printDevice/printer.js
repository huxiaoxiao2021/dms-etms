/**
 * js打印
 * 
 */
	var wsUri = "ws://localhost:37650";
    var websocket = null;
	var mapCallback = new Array();
	var printerArray=null;
    var bInit = false;
    var msgTobeSend = new Array();
	
	function initWebSocket() {
	    try {
	        if (typeof MozWebSocket == 'function')
	            WebSocket = MozWebSocket;
	        if ( websocket && websocket.readyState == 1 )
	            websocket.close();
	        var wsUri = "ws://localhost:37650";
	        websocket = new WebSocket( wsUri );
	        websocket.onopen = function (evt) {
                socketOpen();
	        };
	        websocket.onclose = function (evt) {
	        };
	        websocket.onmessage = function (evt) {
	            console.log( "Message received :", evt.data );
	            //异步返回消息处理
	            revieveMsg(evt.data);
	        };
	        websocket.onerror = function (evt) {
	        };
	    } catch (exception) {
	    }
	};

    function socketOpen() {
	    bInit = true;
	    for(var i in msgTobeSend) {
	        websocket.send(msgTobeSend[i]);
	    }
	    msgTobeSend = [];
	}
	
	//服务器返回消息处理
	function revieveMsg(data) {
        //TODO
	    //TODO 解析返回的XML字符串。
	    //解析出来messageId,调用mapCallback中的callback。
	    var xmlDoc=null;
	    var arrayStr =new Array();
	    domParser = new  DOMParser();
        xmlDoc = domParser.parseFromString(data, 'text/xml');
        var messageId = xmlDoc.getElementsByTagName("MessageId")[0].firstChild.nodeValue;
        //分拣机列表类型回调处理
        if(messageId.indexOf("GET_PRINTERS") >= 0 ){
        	var elements = xmlDoc.getElementsByTagName("Printer");
			for (var i = 0; i < elements.length; i++) {
			    var printerName = elements[i].getElementsByTagName("PrinterName")[0].firstChild.nodeValue;
			    arrayStr[i] = printerName;
			}
			//返回函数调用
            mapCallback[messageId](arrayStr);
        }
	}
	
	
	function printPic(printerName,imageData,width,height) {
		var messageId = "ADD_PRINT_IMAGE" + Date.parse(new Date());
		var msg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \<Message><Version>1.0</Version><Command>ADD_PRINT_IMAGE</Command><MessageId>"+messageId+"</MessageId><PrinterName>"+printerName+"</PrinterName><TaskName>打印图片</TaskName><ImageData>"+imageData+"</ImageData><Width>"+width+"</Width><Height>"+height+"</Height><Zoon>1.0</Zoon></Message>";
        if(!bInit) {
            msgTobeSend.push(msg);
            initWebSocket();
        } else {
            websocket.send(msg);
            console.log( "string sent :", '"'+msg+'"' );
        }
    	
    }
	
	
	var getPrinters = function(callback) {
        //获取打印机列表
	    var messageId = "GET_PRINTERS" + Date.parse(new Date());
	    var msg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Message><Version>1.0</Version><Command>GET_PRINTERS</Command><MessageId>" + messageId + "</MessageId></Message>";
	    //回调函数设置
	    mapCallback[messageId] =   callback;
        if(!bInit) {
            msgTobeSend.push(msg);
            initWebSocket();
        } else {
            websocket.send(msg);
            console.log( "string sent :", '"'+msg+'"' );
        }
	    
    }