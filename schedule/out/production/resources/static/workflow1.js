
var states;


// Automatically label each of the nodes

function searchAll(){
    var enteId=$("#enteId").val();
    var taskKey=$("#taskKey").val();
    var type=$("#type").val();
    if(null==enteId||enteId==""){
        alert("enteId不能为空");
        return;
    }
    $.ajax({
        url: "/schedule/taskView/getRelyPicData?enteId="+enteId+"&taskKey="+taskKey+"&type="+type,
        dataType: "json",
        type: "get",
        async:false,
        success: function(result) {  //这里就是我出错的地方
            states=result.states;
            var edge=result.edge;
            if(states.length==0){
                alert("无数据");
                return;
            }

            createPic(edge);
        },
        error: function(data) {
            alert("请求出错");
            alert(data);
        }
    });
}


function createPic(edge){
    var g = new dagreD3.graphlib.Graph().setGraph({rankdir: "LR"});
    for(var i=0;i<edge.length;i++){
        g.setEdge(edge[i].sourceTaskKey,edge[i].targetTaskKey,{label:""});
    }

    states.forEach(function(state) { g.setNode(state, { label: state }); });
    g.nodes().forEach(function(v) {
        var node = g.node(v);
        node.rx = node.ry = 5;
    });

    var svg = d3.select("svg");
    var inner = svg.select("g");

    var zoom = d3.behavior.zoom().on("zoom", function() {
        inner.attr("transform", "translate(" + d3.event.translate + ")" +
            "scale(" + d3.event.scale + ")");
    });
    svg.call(zoom);
    var render = new dagreD3.render();
    render(inner, g);
    var initialScale = 0.75;
    zoom
        .translate([(svg.attr("width") - g.graph().width * initialScale) / 2, 20])
        .scale(initialScale)
        .event(svg);
    svg.attr('height', g.graph().height * initialScale + 1000);
}