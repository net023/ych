var files = new Array();
/** 
*删除数组指定下标或指定对象 
*/ 
/*Array.prototype.remove=function(obj){ 
	for(var i =0;i <this.length;i++){ 
		var temp = this[i]; 
		if(temp==obj){
			this.splice(i,1);
		}
	} 
}; */
$(function() {
	$("#city").combobox({
		onLoadSuccess:function(data){
			if(data){
				$(this).combobox("select",data[0].id);
			}
		}
	});
	$("#county").combobox({
		onLoadSuccess:function(data){
			if(data){
				$(this).combobox("select",data[0].id);
			}
		}
	});
	
	//省市联动
	$("#province").combobox({
		onSelect:function(record){
			if(record.id){
				//设置地级市的值
				$("#city").combobox("reload","city/getAll?pid="+record.id+"&r="+Math.random());
			}else{
				//清空地级市
				$("#city").combobox("loadData","");
				//清空区县
				$("#county").combobox("loadData","");
				$("#city").combobox("setValues",['选择地级市']);
				$("#county").combobox("setValues",['选择区县']);
				var val = $("#city").combobox("getData");
				if(val){
					$("#city").combobox("select",val[0].id);
				}
		        var val = $("#county").combobox("getData");
		        if(val){
		        	$("#county").combobox("select",val[0].id);
		        }
			}
			
		}
	});
	$("#city").combobox({
		onSelect:function(record){
			if(record.id){
				//设置地级市的值
				$("#county").combobox("reload","county/getAll?cid="+record.id+"&r="+Math.random());
			}else{
				//清空区县
				$("#county").combobox("loadData","");
				$("#county").combobox("setValues",['选择区县']);
				var val = $("#county").combobox("getData");
				if(val){
					$("#county").combobox("select",val[0].id);
				}
			}
			
		}
	});
	
	var sjcj = {
			point:{lat:0,lng:0},
			address:null,
			rs:null
	}
	window.sjcj = sjcj;
	
	function mapHandler(){
	     $("#province").combobox("select",window.sjcj.rs.province);
	        //--
         $("#city").combobox("select",window.sjcj.rs.city);
	        //--
	     $("#county").combobox("select",window.sjcj.rs.district);
	     
	     $("#lon").val(window.sjcj.point.lng);
	     $("#lat").val(window.sjcj.point.lat);
	     
	     $("#address").val(window.sjcj.rs.street+window.sjcj.rs.streetNumber);
	}	
		
		
	$("#mapDialog").dialog({
		buttons:[{text:"关闭",handler:function(){$("#mapDialog").dialog("close");}}]
	});
	
	$("#selectOnMap").click(function(){
		$('#mapDialog').dialog('open').dialog("setTitle","在地图上选择地点");
		
		var mk;
		var map = new BMap.Map("mapDialog");
		map.centerAndZoom("重庆",14);
		var geolocation = new BMap.Geolocation();
		var geoc = new BMap.Geocoder()
		geolocation.getCurrentPosition(function(r){
			if(this.getStatus() == BMAP_STATUS_SUCCESS){
				mk = new BMap.Marker(r.point);
				map.addOverlay(mk);
				map.panTo(r.point);
				window.sjcj.point = r.point;
				geoc.getLocation(r.point, function(rs){
					window.sjcj.address = rs.address;
					window.sjcj.rs = rs.addressComponents;
					mapHandler();
				}); 
			}
			else {
				alert('failed'+this.getStatus());
			}        
		},{enableHighAccuracy: true})
		//回到默认地方
			map.addControl(new BMap.GeolocationControl());
			//显示三种地图类型切换
			map.addControl(new BMap.MapTypeControl()); 
			//小地图
			map.addControl(new BMap.OverviewMapControl());
			//导航标尺
			var opts = {anchor:BMAP_ANCHOR_TOP_LEFT};
			map.addControl(new BMap.NavigationControl(opts));
			
			map.addEventListener("click",function(e){
				window.sjcj.point = e.point;
				map.removeOverlay(mk);
				//mk.dispose();
				mk = new BMap.Marker(e.point);
				map.addOverlay(mk);
				map.panTo(e.point);
				geoc.getLocation(e.point, function(rs){
					window.sjcj.address = rs.address;
					window.sjcj.rs = rs.addressComponents;
					alert(rs.address);
					mapHandler();
				});   
			});
		
		window.setTimeout(function(){
			//隐藏百度地图logo
			$("#mapDialog > div").eq(-2).hide();
		},1000);
	});
	
	$("#storePic").fileupload({
		 url:'file/upload',
		 dataType: 'json',
		 done:function(e,result){
			 if(result.result.r){
				var html = '<td id="f_'+result.result.fID+'"><image src="file/download?fID='+result.result.fID+'" style="width:80px;height:80px"/><a href="javascript:;" onclick="delFile('+result.result.fID+')">删除</a></td>';
				$("#picNode").append(html);
				files.push(result.result.fID);
				$.messager.alert("上传完成",result.result.m);
			 }else{
				$.messager.alert("上传失败",result.result.m);
			 }
	    },
	    fail:function(){
	    	$.messager.alert("错误提示","系统异常请联系管理员。");
	    }
	 });
	
	
	$('#editDialog').dialog({
        buttons:[{text:'保存',handler:function(){
        	$("#fs").val(files.join("|"));
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
        toolbar : [{
                    text : '添加门店',
                    iconCls : 'icon-add',
                    handler : handler_add
                }, '-', {
                    text : '删除所选',
                    iconCls : 'icon-remove',
                    handler : batch_del
                }, '-' ]
        
	});
	
    $('#queryButton').click(function(){
        var params = $('#queryForm')._formToJson();
        $(grid).datagrid('load',params);
    });
    
    function handler_add() {
        $('#editForm').attr('action','store/add').resetForm();
        $('#id').val('');
        $("#picNode").empty();
        files = new Array();
        $('input:checkbox').each(function () {
            $(this).attr('checked',false);
        });
        $('#editDialog').dialog('open').dialog("setTitle","添加门店");
        
        //选择第一个
        var val = $("#province").combobox("getData");
        $("#province").combobox("select",val[0].id);
        //--
//        var val = $("#city").combobox("getData");
//        $("#city").combobox("select",val[0].id);
        //--
//        var val = $("#county").combobox("getData");
//        $("#county").combobox("select",val[0].id);
        //--
    }
    
    /*批量删除*/
    function batch_del() {
        var check = $('#grid').datagrid('getChecked');
        if(check.length > 0){
            $.messager.confirm('操作提示', '确定要删除所选门店？', function(r){
                if (r){
                    var ids = new Array();
                    for(var i in check){
                        ids[i] = check[i].id;
                    }
                    $._ajaxPost('store/batchDel',{ids:ids.join('|')},function(r){
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
};


function modify(i,s){
	$._ajaxPost("store/modify",{id:i,state:s},function(r){
		if(r.r){
			$("#grid").datagrid('load');
		}else{
			$.messager.alert('操作失败',r.m,'error');
		}
	});
}


/*修改版本*/
function updVersion(rowIndex) {
    $('#editForm').attr('action','store/update').resetForm();
    var data = $('#grid').datagrid('getRows')[rowIndex];
    $('#editForm')._jsonToForm(data);
    $('#editDialog').dialog('open').dialog('setTitle','修改门店');
    $("#picNode").empty();
    $('input:checkbox').each(function () {
        $(this).attr('checked',false);
    });
    //填补省市联动
    updataHandler(data);
}


function updataHandler(data){
	$("#province").combobox("select",data.province);
	$("#city").combobox("select",data.city);
	$("#county").combobox("select",data.county);
	
	//checkbox checked
	if(data.ms){
		var ms = data.ms.split(",");
		for(var item in ms){
			$("#ms_"+ms[item]).attr("checked",'true');
		}
	}
	
	//show img
	if(data.fs){
		var fs = data.fs.split("|");
		files = fs;
		for(var item in fs){
			var html = '<td id="f_'+fs[item]+'"><image src="file/download?fID='+fs[item]+'" style="width:80px;height:80px"/><a href="javascript:;" onclick="delFile('+fs[item]+')">删除</a></td>';
			$("#picNode").append(html);
		}
	}
	
}


/*删除门店*/
function delVersion(rowIndex) {
    var data = $('#grid').datagrid('getRows')[rowIndex];
    $.messager.confirm('操作提示', '确定要删除所选门店？', function(r){
        if (r){
        	$._ajaxPost('store/batchDel',{ids:data.id},function(r){
        		if(r.r){$('#grid').datagrid('reload');}else{$.messager.alert('操作提示', r.m,'error');}
        	});
        }
    });
}

/*删除文件*/
function delFile(fid){
	$._ajaxPost('file/delete',{fID:fid},function(r){
        if(r.r){
        	$.messager.alert("删除完成",r.m);
        	$('#f_'+fid).remove();
        	console.info(files);
//        	files.remove(fid);
        	removeArrElement(files,fid);
        	console.info(files);
        }else{
        	$.messager.alert('删除失败', r.m,'error');
        }
    });
	return false;
}

function removeArrElement(arr,obj){
	for(var i=0;i<arr.length;i++){
		var temp = arr[i]; 
		if(temp==obj){
			arr.splice(i,1);
		}
	}
}