$(function() {
	 var grid = $("#grid")._datagrid({
		 checkOnSelect:false,
		 selectOnCheck:false,
		 showFooter:true,
		 rownumbers:true,
		 pagination:true,
		 toolbar:[
			         {text:"添加",iconCls:"icon-add",handler:add},"-",
			         {text:"批量删除",iconCls:"icon-remove",handler:batchDel}
			         ],
		frozenColumns:[[{field:'ck',checkbox:true}]],
		idField:"id"
	 });
	
	 $("#newsClassDialog").dialog({
			buttons:[{text:"保存",handler:function(){
				if(!$("#newsClassForm").form("validate")){
					return;
				}else{
					$("#newsClassForm")._ajaxForm(function(r){
						if(r.r){
							$("#newsClassDialog").dialog("close");
							$("#grid").datagrid("reload");
						}else{
							$.messager.alert("操作提示",r.m,"error");
						}
					});
				}
			}},{text:"关闭",handler:function(){
				$("#newsClassDialog").dialog("close");
			}}]
		});
	 
	 function add(){
		$('#newsClassForm').attr('action','newsClass/add').resetForm();
        $('#id').val('');
        $('#newsClassDialog').dialog('open').dialog("setTitle","添加");
	}
		
	function batchDel(){
		var check = $('#grid').datagrid('getChecked');
        if(check.length > 0){
            $.messager.confirm('操作提示', '确定要删除所选配置？', function(r){
                if (r){
                    var configs = new Array();
                    for(var i in check){
                    	configs[i] = check[i].id;
                    }
                    $._ajaxPost('newsClass/batchDel',{ids:configs.join('|')},function(r){
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
	 qn : function(value, rowData, rowIndex){
		 if(value){
			 return value;
		 }
		 return 0;
	 }
 }

function edit(rowIndex){
	$('#newsClassForm').attr('action','newsClass/update').resetForm();
    var data = $('#grid').datagrid('getRows')[rowIndex];
    $('#newsClassForm')._jsonToForm(data);
    $('#newsClassDialog').dialog('open').dialog('setTitle','修改');
}

function del(rowIndex){
	$.messager.confirm('操作提示', '删除该分类,该分类下的所有文章也会被删除!确定要删除？', function(r){
        if (r){
            var data = $('#grid').datagrid('getRows')[rowIndex];
            $._ajaxPost('newsClass/batchDel',{ids:data.id+""}, function(r){
                if(r.r){$('#grid').datagrid('reload');}else{$.messager.alert('操作提示', r.m,'error');}
            });
        }
    });
}