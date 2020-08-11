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
const CLUSTER_SIZE = 5;


/** Create visualization network graph based on IPs. */
function createIPNetwork(){
  fetch('/PCAP-net-loader') // retrieve all Datastore data that has "data" label
  .then(response => response.json())
  .then((data) => {
  
    // initialize nodes and edges arrays
    var nodes = new Array();
    var edges = new Array();

    var num_nodes = data.length;
    var title = "My Computer";

    // add source node
    var curr = SOURCE;
    var edge = 0;
    nodes[SOURCE] = {id: SOURCE, label: title, group: SOURCE}; 
    
    if (num_nodes > CLUSTER_SIZE) {
      populateLargeGraph();
      createNetwork();
    }
    else {
      populateSmallGraph();
      createNetwork();
    }

    /* Populate nodes and edges with the following formatting:
     ** NODE: {id: ID, label: LABEL, group: GROUP} 
     ** EDGE: {from: SOURCE, to: DESTINATION}
    */
    function populateLargeGraph(){
      // layer 1
      addNodes(SOURCE);

      // layer 2
      for (var i = 1; i <= CLUSTER_SIZE; i++) {
        addNodes(i);
      }

      // layer 3 -- maximum of 125 nodes on the graph (with CLUSTER_SIZE of 5) since graph slows with more nodes
      /*for (var j = CLUSTER_SIZE+1; j <= Math.pow(CLUSTER_SIZE, 2); j++) {
        helper(j);
      }*/

      function addDummies(node) {
        for (var i = 0; i < CLUSTER_SIZE; i++) { 
          curr++;
          if (curr < num_nodes) {
            nodes[curr] = {id: curr, label: "Dummy", group: curr%CLUSTER_SIZE};
            edges[edge] = {from: node, to: curr};
            edge++;
          }
        }
      }

      function addNodes(node) {
        for (var i = 0; i < CLUSTER_SIZE; i++) { 
          curr++;
          if (curr < num_nodes) {
            nodes[curr] = {id: curr, label: data[curr].destination, group: curr%CLUSTER_SIZE};
            for (var j = 0; j < EDGE_LIMIT && j < data[curr].frequency; j++) { 
              edges[edge] = {from: node, to: curr};
              edge++;
            }
          }
        }
      }
    } // end large graph

    function populateSmallGraph(){
      for (var n = 1; n < data.length+1; n++) {
        nodes[n] = {id: n, label: data[n-1].destination, group: n};
        for (var m = 0; m < data[n-1].frequency; m++) {
            edges[edge] = {from: SOURCE, to: n};
            edge++;
        }
      }
    } // end small graph

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
} // end IP network

















/** Create visualization network graph based on data in datastore without descriptive labels.*/
function drawObfusNetwork(){
  fetch('/PCAP-net-loader') // retrieve all Datastore data that has "data" label
  .then(response => response.json())
  .then((data) => {
  
    // initialize nodes and edges arrays
    var nodes = new Array();
    var edges = new Array();

    var num_nodes = data.length;
    var title = "My Computer";

    // add source node
    var curr = SOURCE;
    var edge = 0;
    nodes[SOURCE] = {id: SOURCE, label: title, group: SOURCE}; 
    
    if (num_nodes > CLUSTER_SIZE) {
      populateLargeGraph();
      createNetwork();
    }
    else {
      populateSmallGraph();
      createNetwork();
    }

    /* Populate nodes and edges with the following formatting:
     ** NODE: {id: ID, label: LABEL, group: GROUP} 
     ** EDGE: {from: SOURCE, to: DESTINATION}
    */
    function populateLargeGraph(){
      // layer 1
      helper(SOURCE);

      // layer 2
      for (var i = 1; i <= CLUSTER_SIZE; i++) {
        helper(i);
      }

      // layer 3 -- maximum of 125 nodes on the graph (with CLUSTER_SIZE of 5) since graph slows with more nodes
      for (var j = CLUSTER_SIZE+1; j <= Math.pow(CLUSTER_SIZE, 2); j++) {
        helper(j);
      }

      function helper(node) {
        for (var i = 0; i < CLUSTER_SIZE; i++) { 
          curr++;
          if (curr < num_nodes) {
            nodes[curr] = {id: curr, label: "Node " + curr, group: curr%CLUSTER_SIZE};
            for (var j = 0; j < EDGE_LIMIT && j < data[curr].frequency; j++) { 
              edges[edge] = {from: node, to: curr};
              edge++;
           }
          }
        }
      }
    } // end large graph

    function populateSmallGraph(){
      for (var n = 1; n < data.length+1; n++) {
        nodes[n] = {id: n, label: "Node " + n, group: n};
        for (var m = 0; m < data[n-1].frequency; m++) {
            edges[edge] = {from: SOURCE, to: n};
            edge++;
        }
      }
    } // end small graph

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
} // end obfuscated 

