/** Create network graph one. */
function createNetworkOne(){
  fetch('/data') // retrieve all Datastore data that has "data" label
  .then(response => response.json())
  .then((data) => {
    const SOURCE = 0;
    const SHAPE = "dot"
    const SHAPE_SIZE = 16;
    
    const GROUP1 = 1;
    const GROUP2 = 2;
    const GROUP3 = 3;

    const NUM_GROUPS = 3;
  
    // initialize nodes and edges arrays
    var nodes = new Array();
    var edges = new Array();

    populateGraph();
    createNetwork();

    /* Populate nodes and edges with the following formatting:
     ** NODE: {id: ID, label: LABEL, group: GROUP} 
     ** EDGE: {from: SOURCE, to: DESTINATION}
    */
    function populateGraph(){
      // add source node
      nodes[SOURCE] = {id: SOURCE, label: "My Computer", group: SOURCE}; 
       // initialize edge counter
       var edge = 0;

      // add three base groups
      nodes[GROUP1] = {id: GROUP1, label: "17", group: GROUP1};
      edges[edge] = {from: SOURCE, to: GROUP1};
      edge++;

      nodes[GROUP2] = {id: GROUP2, label: "18", group: GROUP2};
      edges[edge] = {from: SOURCE, to: GROUP2};
      edge++;

      nodes[GROUP3] = {id: GROUP3, label: "19", group: GROUP3};
      edges[edge] = {from: SOURCE, to: GROUP3};
      edge++;

      // add all destinations
      for (var i = 0; i < data.length; i++) {
        var node = i+NUM_GROUPS+1;
        var ip = parseInt(data[i].destination.substring(0,2));
        nodes[node] = {id: node, label: data[i].destination, group: node};

        
        if (ip == 17){ 
          nodes[node] = {id: node, label: data[i].destination, group: GROUP1};
          // add edges based on freqs
          for (var j = 0; j < data[i].frequency; j++) {
            edges[edge] = {from: node, to: GROUP1};
            edge++;
          } 
        }
        else if (ip == 18) { 
          nodes[node] = {id: node, label: data[i].destination, group: GROUP2};
          // add edges based on freqs
          for (var j = 0; j < data[i].frequency; j++) {
            edges[edge] = {from: node, to: GROUP2};
            edge++;
          }
        } 
        else if (ip == 19) { 
          nodes[node] = {id: node, label: data[i].destination, group: GROUP3};
          // add edges based on freqs
          for (var j = 0; j < data[i].frequency; j++) {
            edges[edge] = {from: node, to: GROUP3};
            edge++;
          }
        } 
        else {
            // add edges based on freqs
          for (var j = 0; j < data[i].frequency; j++) {
            edges[edge] = {from: node, to: SOURCE};
            edge++;
          }
        }
      }
    }

    /* Initialize network based on nodes and edges. */
    function createNetwork(){
      var container = document.getElementById("mynetwork");

      var finalData = {
        nodes: nodes,
        edges: edges,
      };

      var options = {
        nodes: {
          shape: SHAPE,
          size: SHAPE_SIZE,
        },
        physics: {
          forceAtlas2Based: {
            gravitationalConstant: -26,
            centralGravity: 0.005,
            springLength: 230,
            springConstant: 0.18,
        },
        maxVelocity: 146,
        solver: "forceAtlas2Based",
        timestep: 0.35,
        stabilization: { iterations: 150 },
        },
      };
      network = new vis.Network(container, finalData, options);
    }
    
    window.addEventListener("load", () => {draw();});
  });
}