const SOURCE = 0;
const SHAPE = "dot"
const SHAPE_SIZE = 16;
const EDGE_LIMIT = 15; // limit the number of edges on visualization 

const GROUP1 = 1;
const GROUP2 = 2;
const GROUP3 = 3;

const GRAV_CONSTANT = -26;
const CENTRAL_GRAV = 0.005;
const SPRING_LENGTH = 500;
const SPRING_CONST = 0.08;
const MAX_VEL = 30;
const TIMESTEP = 0.35;
const ITER = 200; 
const OVERLAP_CONST = 1;

const NUM_GROUPS = 5;

/** Create network graph based on data in datastore. 
  ** Graph consists of three harded groups for visuals based on first two bits of IP addresses: 
  ** Group 17, Group 18, and Group 19 for IP addresses with those two bits as their first two bits. 
  ** All other IP addresses that don't have 17, 18, or 19 as their first two bits are connected to the source node.
 */
function createNetworkOne(){
  fetch('/PCAP-loader') // retrieve all Datastore data that has "data" label
  .then(response => response.json())
  .then((data) => {
  
    // initialize nodes and edges arrays
    var nodes = new Array();
    var edges = new Array();

    var num_nodes = data.length;
    var title = "My Computer " + num_nodes;

    // add source node
    var curr = SOURCE;
    var edge = 0;
    nodes[SOURCE] = {id: SOURCE, label: title, group: SOURCE}; 
    
    if (num_nodes > NUM_GROUPS) {
      populateLargeGraph();
      createNetwork();
    }
    else {
      for (var n = 1; n < data.length; n++) {
        nodes[n] = {id: n, label: "Node " + n, group: n};
        for (var m = 0; m < data[n].frequency; m++) {
            edges[edge] = {from: SOURCE, to: n};
            edge++;
        }
      }
      createNetwork();
    }

    /* Populate nodes and edges with the following formatting:
     ** NODE: {id: ID, label: LABEL, group: GROUP} 
     ** EDGE: {from: SOURCE, to: DESTINATION}
    */
    function populateLargeGraph(){
      helper(SOURCE);
      for (var i = 1; i <= NUM_GROUPS; i++) {
        var inc = SOURCE + i;
        helper(inc);
      }
      for (var j = NUM_GROUPS+1; j <= 25; j++) {
        helper(j);
      }

      function helper(node) {
        for (var i = 0; i < NUM_GROUPS; i++) { 
          curr++;
          nodes[curr] = {id: curr, label: "Node " + curr, group: curr%NUM_GROUPS};
          for (var j = 0; j < EDGE_LIMIT && j < data[curr].frequency; j++) { 
            edges[edge] = {from: node, to: curr};
            edge++;
          }
        }
      }
    }

    function populateSmallGraph(){
      for (var n = 1; n < num_nodes; n++) {
        nodes[n] = {id: n, label: "Node " + n, group: n%NUM_GROUPS};
        for (var m = 0; m < EDGE_LIMIT && m < data[n].frequency; m++) { 
          edges[edge] = {from: SOURCE, to: n};
          edge++;
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
            gravitationalConstant: GRAV_CONSTANT,
            centralGravity: CENTRAL_GRAV,
            springLength: SPRING_LENGTH,
            springConstant: SPRING_CONST,
            avoidOverlap: OVERLAP_CONST,
        },
        maxVelocity: MAX_VEL,
        solver: "forceAtlas2Based",
        timestep: TIMESTEP,
        stabilization: { iterations: ITER },
        },
      };
      network = new vis.Network(container, finalData, options);
    }
    
    window.addEventListener("load", () => {draw();});
  });
}