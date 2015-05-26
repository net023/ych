$(function() {
	 var grid = $("#grid")._datagrid({
		 checkOnSelect:false,
		 selectOnCheck:false,
		 showFooter:true,
		 rownumbers:true,
		 pagination:true,
		 toolbar:[{iconCls:'icon-add',handler:add,text:"添加"}]
	 });
	 
	 $('#carouselEditDialog').dialog({
        buttons:[{text:'保存',handler:function(){
            if(!$('#carouselEditForm').form('validate')){return;}
            $('#carouselEditForm')._ajaxForm(function(r){
                if(r.r){$('#carouselEditDialog').dialog('close');$('#grid').datagrid('reload');}else{$.messager.alert('操作提示', r.m,'error');}
            });
        }},{text:'关闭',handler:function(){$('#carouselEditDialog').dialog('close');}}]
    });
	 
	 
	 function add(){
        $('#carouselEditForm').attr('action','carousel/add').resetForm();
        $('#id').val('');
        $('#f_id').val('');
        $('#n_id').val('');
        $('#carouselEditDialog').dialog({closed:false,title:'新增'});
	 }
	 
	 $("#carousel").fileupload({
		 url:'file/upload',
		 dataType: 'json',
		 done:function(e,result){  
			 if(result.result.r){
				$("#f_id").val(result.result.fID);
				$.messager.alert("上传完成",result.result.m);
			}else{
				$.messager.alert("上传失败",result.result.m);
			}
	    },
	    fail:function(){
	    	$.messager.alert("错误提示","系统异常请联系管理员。");
	    }
	 });
	 
})

var formatter = {
	 opt : function(value, rowData, rowIndex) {
		var html = '<a class="spacing a-blue" onclick="edit('+rowIndex+');" href="javascript:void(0);">修改</a>';
		html += '<a class="spacing a-red" onclick="del('+rowIndex+');" href="javascript:void(0);">删除</a>';
		return html;
	 },
	 img : function(value, rowData, rowIndex) {
		var html = '<span><image src="file/download?fID='+value+'" style="width:50px;height:50px" onmouseover="show('+rowIndex+');"/></span>';
		return html;
	 },
	 state:function(value,rowData,rowIndex){
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
}

function show(rowIndex){
	//通过fID进行ajax查询图片文件在服务器上的路径进行访问
	var data = $('#grid').datagrid('getRows')[rowIndex];
	$("#imgUrl").attr("src","file/download?fID="+data.f_id);
//	$('#imgShowDialog').dialog('open').dialog('setTitle','图片显示');
	$('#imgShowDialog').dialog({closed:false,title:'图片显示'});
}

function edit(rowIndex){
	$('#carouselEditForm').attr('action','carousel/update').resetForm();
    var data = $('#grid').datagrid('getRows')[rowIndex];
    $('#carouselEditForm')._jsonToForm(data);
    $('#carouselEditDialog').dialog('open').dialog('setTitle','修改');
    $("#n_id").combobox("select",data.n_id);
   // $("#upimg").click();
}

function del(rowIndex){
	$.messager.confirm('操作提示', '确定要删除？', function(r){
        if (r){
            var data = $('#grid').datagrid('getRows')[rowIndex];
            $._ajaxPost('carousel/batchDel',{ids:data.id+""}, function(r){
                if(r.r){$('#grid').datagrid('reload');}else{$.messager.alert('操作提示', r.m,'error');}
            });
        }
    });
}

function modify(i,s){
	$._ajaxPost("carousel/modify",{id:i,state:s},function(r){
		if(r.r){
			$("#grid").datagrid('load');
		}else{
			$.messager.alert('操作失败',r.m,'error');
		}
	});
}