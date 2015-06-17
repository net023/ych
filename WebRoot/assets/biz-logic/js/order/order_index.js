$(function() {
	
	 var grid = $("#grid")._datagrid({
		 checkOnSelect:false,
		 selectOnCheck:false,
		 showFooter:true,
		 rownumbers:true,
		 pagination:true
	 });
	 
	 $('#queryButton').click(function(){
        var params = $('#queryForm')._formToJson();
        $(grid).datagrid('load',params);
     });
	 
	 $('#dateDialog').dialog({
			buttons:[{text:'保存',handler:function(){
				if(!$('#dateForm').form('validate')){return;}
				$('#dateForm')._ajaxForm(function(r){
					if(r.r){
						$('#dateDialog').dialog('close');
						$(grid).datagrid('reload');
						$.messager.alert('操作提示', r.m,'info');
					}else{$.messager.alert('操作提示', r.m,'error');}
				});
				
			}},{text:'关闭',handler:function(){$('#dateDialog').dialog('close');}}]
		});
	 
})

var formatter = {
	 opt : function(value, rowData, rowIndex) {
		var html = '<a class="spacing a-blue" onclick="modify('+rowIndex+',1);" href="javascript:void(0);">通过</a>';
		html += '<a class="spacing a-red" onclick="modify('+rowIndex+',2);" href="javascript:void(0);">驳回</a>';
		html += '<a class="spacing a-green" onclick="modify('+rowIndex+',3);" href="javascript:void(0);">完成</a>';
		return html;
	 },
	 img : function(value, rowData, rowIndex) {
		 if(value){
			 var html = $('<div>');
			 var fsArr = value.split(",");
			 for(var i in fsArr){
				 var img = '<image src="file/download?fID='+fsArr[i]+'" style="width:40px;height:40px" onclick="showPicInfo('+fsArr[i]+');"/>';
				 html.append(img);
			 }
			 return html.html();
		 }
	 },
	 edit: function(value,rowData,rowIndex){
		 return '<a class="spacing a-blue" onclick="edit('+rowIndex+');" href="javascript:void(0);">修改预约时间</a>';
	 },
	 type:function(value,rowData,rowIndex){
		 var arr = value.split(",");
		 var types = {
					1:'机油滤清器',
					2:'空气滤清器',
					3:'空调滤清器',
					4:'燃油滤清器',
					5:'机油'
			};
		 var result = "";
		 for(var i in arr){
			 result+=types[arr[i]]+" | ";
		 }
		 return result;
	 },
	 state:function(value,rowData,rowIndex){
		var states = {
				0:'新增',
				1:'已通过',
				2:'已驳回',
				3:'已完成',
		}[rowData.status];
		var color = {
				0:'green',
				1:'blue',
				2:'red',
				3:'gay'
		}[rowData.status];
		return '<span style="color: '+color+';">'+states+'</span>';
	}
}

function showPicInfo(pid){
	$("#imgUrl").attr("src","file/download?fID="+pid);
	$('#imgShowDialog').dialog({closed:false,title:'图片显示'});
}

function edit(rowIndex){
	$('#dateForm').attr('action','order/editDate').resetForm();
    var data = $('#grid').datagrid('getRows')[rowIndex];
    console.info(data);
    $('#dateForm')._jsonToForm(data);
    $("#res_time").val(data.res_time);
    $('#dateDialog').dialog('open').dialog('setTitle','修改预约时间');
}

function modify(rowIndex,s){
	var data = $('#grid').datagrid('getRows')[rowIndex];
	$._ajaxPost("order/modify",{id:data.id,state:s},function(r){
		if(r.r){
			$("#grid").datagrid('reload');
		}else{
			$.messager.alert('操作失败',r.m,'error');
		}
	});
}