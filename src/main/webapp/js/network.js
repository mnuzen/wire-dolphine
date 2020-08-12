const SOURCE = 0;
const SHAPE = "dot"
const SHAPE_SIZE = 16;
const EDGE_LIMIT = 20; // limit the number of edges on visualization 

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

/** Create visualization network graph based on IPs. */
function createIPNetwork(){
  fetch('/PCAP-freq-loader') // retrieve all Datastore data that has proper label
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
    
    populateSmallGraph();
    createNetwork();

    function populateSmallGraph(){
      for (var n = 1; n < data.length+1; n++) {
        nodes[n] = {id: n, label: data[n-1].destination + "\n" + data[n-1].frequency + " Connections", group: n};
        // take normalized frequency
        var normaled = normalize(data[n-1].frequency);
        for (var m = 0; m < normaled; m++) {
          edges[edge] = {from: n, to: SOURCE};
          edge++;
        }
      }
    } // end small graph

    // normalize frequency on a scale from 1 to EDGE_LIMIT
    function normalize(m){
      var max = data[0].frequency; //largest frequency
      var min = data[data.length-1].frequency; //smallest frequency
      return ((m - min) / (max - min) * EDGE_LIMIT + 1);
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
} // end IP network

/** Create visualization network graph based on data in datastore without descriptive labels.*/
function drawObfusNetwork(){
  fetch('/PCAP-freq-loader') 
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
    
    populateSmallGraph();
    createNetwork();

    function populateSmallGraph(){
      for (var n = 1; n < data.length+1; n++) {
        nodes[n] = {id: n, label: "Node: " + n + "\n" + data[n-1].frequency + " Connections", group: n};
        // take normalized frequency
        var normaled = normalize(data[n-1].frequency);
        for (var m = 0; m < normaled; m++) {
          edges[edge] = {from: n, to: SOURCE};
          edge++;
        }
      }
    } // end small graph

    // normalize frequency on a scale from 1 to EDGE_LIMIT
    function normalize(m){
      var max = data[0].frequency; //largest frequency
      var min = data[data.length-1].frequency; //smallest frequency
      return ((m - min) / (max - min) * EDGE_LIMIT + 1);
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
} // end obfuscated 