$(function() {
	var grid = $('#grid')._datagrid({
		checkOnSelect:false,
        selectOnCheck:false
	});
	
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
	
	$("#xzslb").click(function(){
		var fileName = encodeURIComponent(encodeURIComponent("火花塞价格表--模版.xlsx"));
		window.open("file/df?fn="+fileName);
	});
	
	var ii;
	$("#scslb").fileupload({
		url:'spark/batchEdit',
		dataType: 'json',
		done:function(e,result){
			layer.close(ii);
			if(result.result.r){
				$("#grid").datagrid('reload');
				var fileName = encodeURIComponent(encodeURIComponent(result.result.f));
				asyncbox.open({
					title:result.result.m,
					html:'<a href="#" onclick="javascript:(window.open(\'file/df2?fn='+fileName+'\'))">点击下载上传结果信息</a>',
					width : 200,
					height : 200
				});
			}else{
				$.messager.alert("上传失败",result.result.m);
			}
		},
		fail:function(){
			layer.close(ii);
			$.messager.alert("错误提示","系统异常请联系管理员。");
		},
		add: function (e, data) {
		   ii = layer.load();
		   data.submit();
        }
	});
	
	
	$('#picDialog').dialog({
		buttons:[{text:'关闭',handler:function(){$('#picDialog').dialog('close');$("#upf").empty();}}]
	});
	$('#lookDialog').dialog({
		buttons:[{text:'关闭',handler:function(){$('#lookDialog').dialog('close');$("#tp").empty();}}]
	});
	
	
	$("#upPic").click(function(){
		var val = $("#pinpai").combobox("getData");
		$("#pinpai").combobox("select",val[0].id);
		$('#picDialog').dialog('open').dialog('setTitle','上传图片');
	});
	$("#lookPic").click(function(){
		var val = $("#lpinpai").combobox("getData");
		$("#lpinpai").combobox("select",val[0].id);
		$('#lookDialog').dialog('open').dialog('setTitle','查看图片');
	});
	
	$("#upFile").fileupload({
		 url:'spark/upload',
		 dataType: 'json',
		 add:function(e,data){
			 var brand = $("#pinpai").combobox("getValue");
			 data.url = 'spark/upload?brand='+brand;
			 data.submit();
		 },
		 done:function(e,result){
			 $("#upf").empty();
			 if(result.result.r){
				var html = '<image src="file/download?fID='+result.result.fID+'" style="width:320px;height:230px"/>';
				$("#upf").append(html);
				$.messager.alert("上传完成",result.result.m);
			 }else{
				$.messager.alert("上传失败",result.result.m);
			 }
	    },
	    fail:function(){
	    	$.messager.alert("错误提示","系统异常请联系管理员。");
	    }
	 });
	
	$("#lpinpai").combobox({
		onSelect:function(record){
			var brand = $("#lpinpai").combobox("getValue");
			var html = '<image src="spark/download?brand='+brand+'" style="width:320px;height:230px"/>';
			$("#tp").empty();
			$("#tp").append(html);
		}
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
