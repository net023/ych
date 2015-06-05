$(function() {
	$("#brand").combobox({
		onSelect:function(record){
			if(record.id){
				//清空系列
				$("#series").combobox("loadData","");
				//清空型号
				$("#model").combobox("loadData","");
				$("#series").combobox("setValues",['全部']);
				$("#model").combobox("setValues",['全部']);
				//设置系列的值
				$("#series").combobox("reload","spark/getAllSeries?bid="+record.id+"&r="+Math.random());
			}else{
				//清空系列
				$("#series").combobox("loadData","");
				//清空型号
				$("#model").combobox("loadData","");
				$("#series").combobox("setValues",['全部']);
				$("#model").combobox("setValues",['全部']);
			}
		}
	});
	$("#series").combobox({
		onSelect:function(record){
			if(record.id){
				//清空系列
				$("#model").combobox("loadData","");
				//清空型号
				$("#model").combobox("setValues",['全部']);
				//设置型号的值
				$("#model").combobox("reload","spark/getAllModels?sid="+record.id+"&r="+Math.random());
			}else{
				//清空系列
				$("#model").combobox("loadData","");
				//清空型号
				$("#model").combobox("setValues",['全部']);
			}
		}
	});
	
	$('#queryButton').click(function(){
        var params = $('#queryForm')._formToJson();
        if(params.series=='全部'){params.series='';}
        if(params.model=='全部'){params.model='';}
        $(grid).datagrid('load',params);
    });
	
	$("#batchup").fileupload({
		url:'spark/batchup',
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
	
	
	$('#priceDialog').dialog({
		buttons:[{text:'保存',handler:function(){
			if(!$('#priceForm').form('validate')){return;}
			$('#priceForm')._ajaxForm(function(r){
				if(r.r){
					$('#priceDialog').dialog('close');
					$("#grid").datagrid('reload');
					$.messager.alert('操作提示', r.m,'info');
				}else{$.messager.alert('操作提示', r.m,'error');}
			});
			
		}},{text:'关闭',handler:function(){$('#priceDialog').dialog('close');}}]
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
	},
	recmd:function(value,rowData,rowIndex){
		var states = {
				0:'已推荐',
				1:'未推荐'
		}[rowData.recmd];
		var toStates = {
				0:1,
				1:0
		}[rowData.recmd];
		var color = {
				0:'green',
				1:'red'
		}[rowData.recmd];
		
		return states && ('<a class="spacing a-'+color+'" onclick="recmd('+rowData.id+','+toStates+');" href="javascript:void(0);">'+states+'</a>');
	},
	editPrice:function(value,rowData,rowIndex){
		return '<a class="spacing a-blue" onclick="editPrice('+rowIndex+');" href="javascript:void(0);">修改价格</a>';
	}
};

function modify(i,s){
	$._ajaxPost("spark/modify",{id:i,state:s},function(r){
		if(r.r){
			$("#grid").datagrid('reload');
		}else{
			$.messager.alert('操作失败',r.m,'error');
		}
	});
}

function recmd(i,s){
	$._ajaxPost("spark/recmd",{id:i,state:s},function(r){
		if(r.r){
			$("#grid").datagrid('reload');
		}else{
			$.messager.alert('操作失败',r.m,'error');
		}
	});
}

function editPrice(rowIndex){
	$('#priceForm').attr('action','spark/editPrice').resetForm();
    var data = $('#grid').datagrid('getRows')[rowIndex];
    $('#priceForm')._jsonToForm(data);
	$('#priceDialog').dialog('open').dialog('setTitle','修改价格');
}
