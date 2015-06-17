$(function() {
	
	var grid = $('#grid')._datagrid({
		checkOnSelect:false,
        selectOnCheck:false
	});
	
	$('#queryButton').click(function(){
        var params = $('#queryForm')._formToJson();
        $(grid).datagrid('load',params);
    });
	
	$("#batchup").fileupload({
		url:'oil/batchup',
		dataType: 'json',
		done:function(e,result){
			if(result.result.r){
				$("#grid").datagrid('reload');
				$.messager.alert("上传完成",result.result.m);
			}else{
				$.messager.alert("上传失败",result.result.m);
			}
		},
		fail:function(){
			$.messager.alert("错误提示","系统异常请联系管理员。");
		}
	});
	
	
	
	
	
	
	//---------------上传图片
	$('#picDialog').dialog({
		buttons:[{text:'关闭',handler:function(){$('#picDialog').dialog('close');$("#upf").empty();}}]
	});
	$("#upPic").click(function(){
		var val = $("#pinpai").combobox("getData");
		$("#pinpai").combobox("select",val[0].id);
		var val = $("#pict").combobox("getData");
		$("#pict").combobox("select",val[0].id);
		$('#picDialog').dialog('open').dialog('setTitle','上传图片');
	});
	$("#upFile").fileupload({
		url:'oil/upload',
		dataType: 'json',
		add:function(e,data){
			var brand = $("#pinpai").combobox("getValue");
			var type = $("#pict").combobox("getValue");
			var model = $("#m1").combobox("getValue");
			var eid = $("#guige1").combobox("getValue");
			data.url = 'oil/upload?brand='+brand+'&type='+type+'&model='+model+'&eid='+eid;
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
	$("#m1").combobox({
		onLoadSuccess:function(data){
			if(data){
				$(this).combobox("select",data[0].id);
			}
		}
	});
	$("#guige1").combobox({
		onLoadSuccess:function(data){
			if(data){
				$(this).combobox("select",data[0].id);
			}
		}
	});
	$("#pinpai").combobox({
		onSelect:function(record){
			if(record.id){
				$("#m1").combobox("reload","oil/getAllModel?bid="+record.id+"&r="+Math.random());
			}
		}
	});
	$("#m1").combobox({
		onSelect:function(record){
			var brand = $("#pinpai").combobox("getValue");
			if(record.id){
				$("#guige1").combobox("reload","oil/getAllEtalon?bid="+brand+'&mid='+record.id+"&r="+Math.random());
			}
		}
	});
	
	//---------------查看图片
	$('#lookDialog').dialog({
		buttons:[{text:'关闭',handler:function(){$('#lookDialog').dialog('close');$("#tp").empty();}}]
	});
	
	$("#lookPic").click(function(){
		var val = $("#lpinpai").combobox("getData");
		$("#lpinpai").combobox("select",val[0].id);
		var val = $("#lpict").combobox("getData");
		$("#lpict").combobox("select",val[0].id);
		$('#lookDialog').dialog('open').dialog('setTitle','查看图片');
	});
	
	$("#m2").combobox({
		onLoadSuccess:function(data){
			if(data){
				$(this).combobox("select",data[0].id);
			}
		}
	});
	$("#guige2").combobox({
		onLoadSuccess:function(data){
			if(data){
				$(this).combobox("select",data[0].id);
			}
		}
	});
	$("#lpinpai").combobox({
		onSelect:function(record){
			var brand = $("#lpinpai").combobox("getValue");
			var type = $("#lpict").combobox("getValue");
			var model = $("#m2").combobox("getValue");
			var eid = $("#guige2").combobox("getValue");
			var html = '<image src="oil/download?brand='+brand+'&type='+type+'&model='+model+'&eid='+eid+'" style="width:320px;height:230px"/>';
			$("#tp").empty();
			$("#tp").append(html);
			if(record.id){
				$("#m2").combobox("reload","oil/getAllModel?bid="+record.id+"&r="+Math.random());
			}
		}
	});
	$("#m2").combobox({
		onSelect:function(record){
			var brand = $("#lpinpai").combobox("getValue");
			if(record.id){
				$("#guige2").combobox("reload","oil/getAllEtalon?bid="+brand+'&mid='+record.id+"&r="+Math.random());
			}
			var type = $("#lpict").combobox("getValue");
			var model = $("#m2").combobox("getValue");
			var eid = $("#guige2").combobox("getValue");
			var html = '<image src="oil/download?brand='+brand+'&type='+type+'&model='+model+'&eid='+eid+'" style="width:320px;height:230px"/>';
			$("#tp").empty();
			$("#tp").append(html);
		}
	});
	$("#guige2").combobox({
		onSelect:function(record){
			var brand = $("#lpinpai").combobox("getValue");
			var type = $("#lpict").combobox("getValue");
			var model = $("#m2").combobox("getValue");
			var eid = $("#guige2").combobox("getValue");
			var html = '<image src="oil/download?brand='+brand+'&type='+type+'&model='+model+'&eid='+eid+'" style="width:320px;height:230px"/>';
			$("#tp").empty();
			$("#tp").append(html);
		}
	});
	$("#lpict").combobox({
		onSelect:function(record){
			var brand = $("#lpinpai").combobox("getValue");
			var type = $("#lpict").combobox("getValue");
			var model = $("#m2").combobox("getValue");
			var eid = $("#guige2").combobox("getValue");
			var html = '<image src="oil/download?brand='+brand+'&type='+type+'&model='+model+'&eid='+eid+'" style="width:320px;height:230px"/>';
			$("#tp").empty();
			$("#tp").append(html);
		}
	});
	
	
	//---------------内datagrid
	$('#priceDialog').dialog({
		buttons:[{text:'保存',handler:function(){
			if(!$('#priceForm').form('validate')){return;}
			$('#priceForm')._ajaxForm(function(r){
				if(r.r){
					$('#priceDialog').dialog('close');
					$("#priceMgGrid").datagrid('reload');
					$.messager.alert('操作提示', r.m,'info');
				}else{$.messager.alert('操作提示', r.m,'error');}
			});
			
		}},{text:'关闭',handler:function(){$('#priceDialog').dialog('close');}}]
	});
	
	$('#priceMgDialog').dialog({
		buttons:[{text:'关闭',handler:function(){$('#priceMgDialog').dialog('close');$("#upf").empty();}}]
	});
	
	var priceMgGrid = $('#priceMgGrid')._datagrid({
		checkOnSelect:false,
        selectOnCheck:false
	});
	
	$("#lookPrice").click(function(){
		$('#priceMgDialog').dialog('open').dialog('setTitle','价格表');
	});
	
	$('#priceQueryButton').click(function(){
        var params = $('#priceQueryForm')._formToJson();
        $(priceMgGrid).datagrid('load',params);
    });
	
	$("#xzslb").click(function(){
		//$._ajaxPost("file/df",{fn:"四滤价格表--模版.xlsx"});
		var fileName = encodeURIComponent(encodeURIComponent("机油价格表--模版.xlsx"));
		window.open("file/df?fn="+fileName);
	});
	
	var ii;
	$("#scslb").fileupload({
		url:'oil/batchEdit',
		dataType: 'json',
		done:function(e,result){
			layer.close(ii);
			if(result.result.r){
				$("#priceMgGrid").datagrid('reload');
				/*var fileName = encodeURIComponent(encodeURIComponent(result.result.f));
				asyncbox.open({
					title:result.result.m,
					//html:"<a href='file/df?fn="+result.result.f+"' target='_blank'>点击下载上传结果信息</a>",
					html:'<a href="#" onclick="javascript:(window.open(\'file/df2?fn='+fileName+'\'))">点击下载上传结果信息</a>',
					width : 200,
					height : 200
				});*/
				$.messager.alert("上传完成",result.result.m);
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
	},
	litre:function(value,rowData,rowIndex){
		if(value==1){
			return "1L";
		}else{
			return "4L";
		}
	},
	type:function(value,rowData,rowIndex){
		if(value==1){
			return "全合成";
		}else if(value==2){
			return "半合成";
		}else{
			return "矿物油";
		}
	}
};

function modify(i,s){
	$._ajaxPost("oil/modify",{id:i,state:s},function(r){
		if(r.r){
			$("#priceMgGrid").datagrid('reload');
		}else{
			$.messager.alert('操作失败',r.m,'error');
		}
	});
}

function recmd(i,s){
	$._ajaxPost("oil/recmd",{id:i,state:s},function(r){
		if(r.r){
			$("#priceMgGrid").datagrid('reload');
		}else{
			$.messager.alert('操作失败',r.m,'error');
		}
	});
}

function editPrice(rowIndex){
	$('#priceForm').attr('action','oil/editPrice').resetForm();
    var data = $('#priceMgGrid').datagrid('getRows')[rowIndex];
    $('#priceForm')._jsonToForm(data);
	$('#priceDialog').dialog('open').dialog('setTitle','修改价格');
}
