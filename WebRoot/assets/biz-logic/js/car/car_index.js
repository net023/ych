$(function() {
	$('#queryButton').click(function(){
        var params = $('#queryForm')._formToJson();
        $(grid).datagrid('load',params);
    });
	
	$("#batchup").fileupload({
		url:'car/batchup',
		dataType: 'json',
		done:function(e,result){
			if(result.result.r){
				$.messager.alert("上传完成",result.result.m);
			}else{
				$.messager.alert("上传失败",result.result.m);
			}
		},
		fail:function(){
			$.messager.alert("错误提示","系统异常请联系管理员。");
		}
	});
	
	var grid = $('#grid')._datagrid({
		checkOnSelect:false,
        selectOnCheck:false
	});
	
    
});
var formatter = {
    status:function(value,rowData,rowIndex){
		var states = {
				0:'已启用',
				1:'已禁用'
		}[rowData.status];
		var toStates = {
				0:1,
				1:0
		}[rowData.status];
		var color = {
				0:'green',
				1:'red'
		}[rowData.status];
		
		return states && ('<a class="spacing a-'+color+'" onclick="modify('+rowData.id+','+toStates+');" href="javascript:void(0);">'+states+'</a>');
	}
};

function modify(i,s){
	$._ajaxPost("car/modify",{id:i,state:s},function(r){
		if(r.r){
			$("#grid").datagrid('reload');
		}else{
			$.messager.alert('操作失败',r.m,'error');
		}
	});
}
