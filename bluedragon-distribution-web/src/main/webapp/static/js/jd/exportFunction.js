$(document).ready(function() {
	$("#exportButton").click(function() {
		commonExport(url, counts);
	});
});
function commonExport(requestHead, counts, buttonId) {
	var mybuttion = "#exportButton";
	if (typeof (buttonId) != "undefined") {
		mybuttion = buttonId;
	}

	var count = 0;
	if (typeof (counts) != "undefined") {
		count = counts;
	}
	if (count <= 0) {
		Jd.alert("数据为空，无法导出");
	} else {
		$(mybuttion).attr('disabled', true);
		location.href = requestHead;
	}
}
