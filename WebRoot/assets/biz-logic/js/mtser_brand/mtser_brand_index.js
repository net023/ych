$(function() {
	$("#firstCategory").combobox({
		onLoadSuccess:function(data){
			if(data){
				$(this).combobox("select",data[0].id);
			}
		}
	});
	$("#secondCategory").combobox({
		onLoadSuccess:function(data){
			if(data){
				$(this).combobox("select",data[0].id);
			}
		}
	});
	$("#firstCategory").combobox({
		onSelect:function(record){
			if(record.id){
				$("#secondCategory").combobox("reload","mtser/getAllSecondsByFirst?pid="+record.id+"&r="+Math.random());
			}else{
				$("#secondCategory").combobox('loadData','');
				$("#secondCategory").combobox("setValues",['选择二级类目']);
				var val = $("#secondCategory").combobox("getData");
				if(val){
					$("#secondCategory").combobox("select",val[0].id);
				}
			}
			
		}
	});
	$('#editDialog').dialog({
        buttons:[{text:'保存',handler:function(){
            if(!$('#editForm').form('validate')){return;}
            $('#editForm')._ajaxForm(function(r){
                if(r.r){
                	$('#editDialog').dialog('close');
                	$('#grid').datagrid('reload');
                }else{$.messager.alert('操作提示', r.m,'error');}
            });
           
        }},{text:'关闭',handler:function(){$('#editDialog').dialog('close');}}]
    });
	
	var grid = $('#grid')._datagrid({
		checkOnSelect:false,
		selectOnCheck:false,
		frozenColumns:[[
		                {field:'ck',checkbox:true}
		                ]],
		                toolbar : [/*{
		                	text : '添加零配件过滤品牌',
		                	iconCls : 'icon-add',
		                	handler : handler_add
		                }, '-',*/ {
		                	text : '删除所选',
		                	iconCls : 'icon-remove',
		                	handler : batch_del
		                }, '-' ]
	
	});
	$('#addButton').click(function(){
		console.log($('#secondCategory').combo('getValue'));
		console.log(!$('#secondCategory').combo('getValue'));
		if ($('#secondCategory').combobox('getData').length > 1) {
			if (!$('#secondCategory').combo('getValue') || $('#secondCategory').combo('getValue') == '选择二级类目') {
				$.messager.alert('操作提示', '请选择正确的二级类目', 'error');
				return;
			}
		} else {
			if ($('#firstCategory').combo('getValue') == '') {
				$.messager.alert('操作提示', '请选择正确的一级类目', 'error');
				return;
			}
		}
		$('#addForm').attr('action', 'mtser_brand/add')._ajaxForm(function(r){
			if(r.r){
				$('#grid').datagrid('reload');
			}else{$.messager.alert('操作提示', r.m,'error');}
		});
	});
	function handler_add() {
        $('#editForm').attr('action','mtser_brand/add').resetForm();
        $('#id').val('');
        $('input:checkbox').each(function () {
            $(this).attr('checked',false);
        });
        $('#editDialog').dialog('open').dialog("setTitle","添加零配件过滤品牌");
        
        //选择第一个
        var val = $("#firstCategory").combobox("getData");
        $("#firstCategory").combobox("select",val[0].id);
    }
    
    /*批量删除*/
    function batch_del() {
        var check = $('#grid').datagrid('getChecked');
        if(check.length > 0){
            $.messager.confirm('操作提示', '确定要删除过滤品牌？', function(r){
                if (r){
                    var ids = new Array();
                    for(var i in check){
                        ids[i] = check[i].ms_id;
                    }
                    $._ajaxPost('mtser_brand/batchDel',{ids:ids.join('|')},function(r){
                        if(r.r){$('#grid').datagrid('reload');}else{$.messager.alert('操作提示', r.m,'error');}
                    });
                }
            });
        }
    }
});
var formatter = {
    opt : function(value, rowData, rowIndex) {
        var html = '<a class="spacing a-blue" onclick="updVersion('+rowIndex+');" href="javascript:void(0);">修改</a>';
            html+= '<a class="spacing a-red" onclick="delVersion('+rowIndex+');" href="javascript:void(0);">删除</a>';
        return html;
    }
};
function updataHandler(data){
	$("#filter").val(data.brand_filter);
	$("#id").val(data.ms_id);
}
function updVersion(rowIndex) {
    $('#editForm').attr('action','mtser_brand/update').resetForm();
    var data = $('#grid').datagrid('getRows')[rowIndex];
    $('#editForm')._jsonToForm(data);
    $('#editDialog').dialog('open').dialog('setTitle','修改品牌过滤信息');
    $("#picNode").empty();
    $('input:checkbox').each(function () {
        $(this).attr('checked',false);
    });
    updataHandler(data);
}
function delVersion(rowIndex) {
    var data = $('#grid').datagrid('getRows')[rowIndex];
    $.messager.confirm('操作提示', '确定要删除过滤品牌？', function(r){
        if (r){
        	$._ajaxPost('mtser_brand/batchDel',{ids:data.ms_id},function(r){
        		if(r.r){$('#grid').datagrid('reload');}else{$.messager.alert('操作提示', r.m,'error');}
        	});
        }
    });
}
