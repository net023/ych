$(function() {
	 var grid = $("#grid")._datagrid({
		 checkOnSelect:false,
		 selectOnCheck:false,
		 showFooter:true,
		 rownumbers:true,
		 pagination:true,
		 queryParams:$('#queryForm')._formToJson(),
		 toolbar:[
			         {text:"添加",iconCls:"icon-add",handler:add},"-",
			         {text:"批量删除",iconCls:"icon-remove",handler:batchDel}
			         ],
		frozenColumns:[[{field:'ck',checkbox:true}]],
		fitColumns:true,
		nowrap:true,
		idField:"id"
	 });
	  
	 $("#newsDialog").dialog({
			buttons:[{text:"保存",handler:function(){
				if(!$("#newsForm").form("validate")){
					return;
				}else{
					$("#newsForm")._ajaxForm(function(r){
						if(r.r){
						//	window.editor.html('');
							$("#newsDialog").dialog("close");
							$("#grid").datagrid("reload");
						}else{
							$.messager.alert("操作提示",r.m,"error");
						}
					});
				}
			}},{text:"关闭",handler:function(){
				//window.editor.html('');
				$("#newsDialog").dialog("close");
			}}]
		});
	  
	 $('#queryButton').click(function(){
		 if(!$('#queryForm').form('validate')){return;}
		 var params = $('#queryForm')._formToJson();
		 $(grid).datagrid('load',params);
	 });
	 
	 function add(){
			$('#newsForm').attr('action','news/add').resetForm();
//			$("#type").combobox("select",'');
	        $('#id').val('');
	        $("#answer").html('');
//	        $('#newsDialog').dialog('open').dialog("setTitle","添加");
	        $("#newsDialog").dialog({closed:false,title:'新增'});
		}
			
		function batchDel(){
			var check = $('#grid').datagrid('getChecked');
	        if(check.length > 0){
	            $.messager.confirm('操作提示', '确定要删除所选？', function(r){
	                if (r){
	                    var configs = new Array();
	                    for(var i in check){
	                    	configs[i] = check[i].id;
	                    }
	                    $._ajaxPost('news/batchDel',{ids:configs.join('|')},function(r){
	                        if(r.r){$('#grid').datagrid('reload');}else{$.messager.alert('操作提示', r.m,'error');}
	                    });
	                }
	            });
	        }
		}
		
		
})

 var formatter = {
	 opt : function(value, rowData, rowIndex) {
		 var html = '<a class="spacing a-blue" onclick="edit('+rowIndex+');" href="javascript:void(0);">修改</a>';
			html += '<a class="spacing a-red" onclick="del('+rowIndex+');" href="javascript:void(0);">删除</a>';
		 return html;
	 },
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
 }

function edit(rowIndex){
	$('#newsForm').attr('action','news/update').resetForm();
    var data = $('#grid').datagrid('getRows')[rowIndex];
    $('#newsForm')._jsonToForm(data);
    $("#answer").html(data.b_content);
    $("#type").combobox("select",data.n_type);
    $('#newsDialog').dialog('open').dialog('setTitle','修改');
}

function del(rowIndex){
	$.messager.confirm('操作提示', '确定要删除？', function(r){
        if (r){
            var data = $('#grid').datagrid('getRows')[rowIndex];
            $._ajaxPost('news/batchDel',{ids:data.id+""}, function(r){
                if(r.r){$('#grid').datagrid('reload');}else{$.messager.alert('操作提示', r.m,'error');}
            });
        }
    });
}

function modify(i,s){
	$._ajaxPost("news/modify",{id:i,state:s},function(r){
		if(r.r){
			$("#grid").datagrid('load');
		}else{
			$.messager.alert('操作失败',r.m,'error');
		}
	});
}