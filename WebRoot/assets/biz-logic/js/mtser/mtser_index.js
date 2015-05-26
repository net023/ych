$(function(){
    function addHoverDom(treeId, treeNode) {
        var sObj = $("#" + treeNode.tId + "_span");
        if (!treeNode.isParent || $("#addBtn_"+treeNode.tId).length > 0) return;
        var addStr = "<span class='button add' id='addBtn_" + treeNode.tId + "' title='新增' onfocus='this.blur();'></span>";
        sObj.after(addStr);
        var btn = $("#addBtn_"+treeNode.tId);
        if (btn) btn.bind("click", function(){
            $('#mtserEditForm').resetForm();
            var node = {isParent:treeNode.id == 0 ? true : false, parentID:treeNode.id, parentName:treeNode.name,id:'', p_id:treeNode.id, type:treeNode.id == 0 ? 1 : 2};
            $('#mtserEditForm')._jsonToForm(node);
            $('#name').focus();
            return false;
        });
    }
    function removeHoverDom(treeId, treeNode) {
        $("#addBtn_"+treeNode.tId).unbind().remove();
    }
    var setting = {
        edit: {
            enable: true,
            renameTitle: '编辑',
            removeTitle: '删除',
            showRenameBtn: function(treeId, treeNode){if(treeNode.id == 0){return false;} else {return true;}},
            showRemoveBtn: function(treeId, treeNode){if(treeNode.id == 0){return false;} else {return true;}},
            drag:{isCopy : false,isMove : false}
        },
        data: {
            simpleData: { enable: true, idKey: "id", pIdKey: "parentID" },
            key : { name : "showName"},
            keep : {parent: true, leaf: true}
        },
        callback : {
            onClick : function(event, treeId, treeNode) {
                if(treeNode.isParent) {
                    zTree.expandNode(treeNode, !treeNode.open, false, true);
                }
            },
            beforeRemove:function(treeId, treeNode) {
                zTree.selectNode(treeNode);
                $.messager.confirm('提醒','确定要删除该'+(treeNode.isParent ? '及其子' : '')+'菜单吗？',function(r) {
                    if(r){
                        $._ajaxPost('mtser/del',{id:treeNode.id},function(r){
                            if(r.r) {
                                zTree.removeNode(treeNode);
                                $('#mtserEditForm').resetForm();
                            }
                            asyncbox.tips(r.m,r.r ? 'success' : 'error');
                        });
                    }
                });
                return false;
            },
            beforeEditName : function(treeId, treeNode) {
                treeNode.p_id = treeNode.parentID;
                $('#mtserEditForm')._jsonToForm(treeNode);
                var parent = treeNode.getParentNode();
                $('#parentName').val(parent == null ? '无' : parent.showName);
                zTree.selectNode(treeNode);
                return false;
            }
        },
        view : {
            showTitle : false,
            selectedMulti : false,
            autoCancelSelected : false,
            addHoverDom : addHoverDom,
            removeHoverDom : removeHoverDom,
            fontCss : function(treeId, treeNode){
                return {color:"black"};
            }
        }
    };
    $.fn.zTree.init($("#mtserPanel"), setting, window.mtsers);
    var zTree = $.fn.zTree.getZTreeObj("mtserPanel");
    zTree.expandAll(true);
    $('#mtserEditForm').submit(function(){
        if(!$(this).form('validate')){return;}
        var node = $(this)._formToJson();
        if($.trim(node.id) == '') {
            $._ajaxPost('mtser/add', node, function(r){
                if(r.r) {
                    var parentNode = zTree.getNodeByParam('id', node.p_id, null);
                    node.id = r.id;
                    node.showName = node.name;
                    zTree.addNodes(parentNode, node);
                    zTree.selectNode(zTree.getNodeByParam('id', node.id));
                    $('#mtserEditForm').resetForm();
                }
                asyncbox.tips(r.m,r.r ? 'success' : 'error');
            });
        } else {
            $._ajaxPost('mtser/update', node, function(r){
                if(r.r) {
                    var treeNode = zTree.getNodeByParam('id', node.id, null);
                    for(var key in node){
                        treeNode[key] = node[key];
                    }
                    treeNode.isParent = node.isParent == 'true' ? true : false;
                    treeNode.showName = treeNode.name;
                    zTree.updateNode(treeNode);
                    $('#mtserEditForm').resetForm();
                }
                asyncbox.tips(r.m,r.r ? 'success' : 'error');
            });
        }
    });
});
function custom(width, height){
    $('#mtserMgrPanel,#editMtser').height(height - 29);
}