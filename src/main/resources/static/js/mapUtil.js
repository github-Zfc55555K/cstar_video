

//对节点进行操作的通用方法
var MapUtil = function () { }

/**
 * 根据路径 和 参数 查询
 */
MapUtil.prototype.queryNode = function (url, parm) {
    var nodeList = null;
    $.ajax({
        url: url,
        data: parm,
        async: false,
        error: function () {
            console.log('查询信息失败');
        }
    }).done(function (res) {
        nodeList = eval('(' + res + ')');
    });
    return nodeList;
}
/**
 * 根据层级 获得 标记的图片
 */
MapUtil.prototype.getMarkImg = function (lev) {
    var l = parseInt(lev);
    switch (l) {
        case 1: return 'images/mark.svg'
        default: console.log('404 for 标记资源');
    }
}

MapUtil.prototype.splitPath = function (str) {
    if(str == null || str == ""){
        return [];
    }
    var result = [];
    var dataArr = str.split(',');
    var data = [];
    for(var i = 0;i < dataArr.length;i++){
        data.push(parseFloat(dataArr[i]));
    }
    // js 把一个数组分割成 n 个一组
    for (var i = 0; i < data.length; i++) {
        var res = [];
        var flag = i;
        res.push(data[flag]);
        res.push(data[flag+1]);
        i++;
        result.push(res);
    }
    return result;
}