$(document).ready(main);

function main() {
	
	$("#uploadBtn").click(upload);

	$(function(){
		if($("#inputExcel").val()!=""){
	    	alert($("#inputExcel").val());
	    	return false;
	    }
	})
}

function upload(){
	if($("#excelFile").val()==''){
		alert('请选择上传文件！');
		return false;			
	}
	var strRegex="(.xls|.XLS)|(.xlsx|.XLSX)$";
	var re=new RegExp(strRegex);
	if(re.test($("#excelFile").val().toLowerCase())){
		return true;
	}else{
		alert('文件格式不合法，文件的扩展名必须为.xls或.XLS或.xlsx或.XLSX格式！');
		return false;
	}
	return true;
}

function return_back(){
	var contextPath = $("#contextPath").val();
	location.href =contextPath+"/base/crossbox/index";
}



