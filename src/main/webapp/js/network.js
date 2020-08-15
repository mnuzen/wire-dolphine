const SOURCE = 0;
const SHAPE = "dot"
const SHAPE_SIZE = 16;
const EDGE_LIMIT = 20; // limit the number of edges on visualization 
const NODE_LIMIT = 20;

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

    var num_nodes = Object.keys(data).length;
    var title = "My Computer";

    // add source node
    var n = SOURCE;
    var edge = 0;
    nodes[n] = {id: n, label: title, group: n};  
    n++;

    var limit = 0;
    // put all addresses onto graph
    Object.keys(data).forEach(key => {
      if (limit < NODE_LIMIT) {
        var normaled = normalize(data[key]);
        nodes[n] = {id: n, label: key + "\n" + data[key] + " Connections", group: n}; // maybe coloring by class?
        for (var m = 0; m < normaled; m++) {
            edges[edge] = {from: n, to: SOURCE};
            edge++;
        }
        n++;
      }
      limit++;
    });
    
    createNetwork();

    // normalize frequency on a scale from 1 to EDGE_LIMIT
    function normalize(m){
      if (num_nodes > 1) {
        var max = Object.keys(data)[0]; //largest frequency
        var min = Object.keys(data)[num_nodes-1]; //smallest frequency
        return ((m - data[min]) / (data[max] - data[min]) * EDGE_LIMIT + 1);
      }
      else {
        return m;
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
} // end IP network

/** Create visualization network graph based on data in datastore without descriptive labels.*/
function drawObfusNetwork(){
  fetch('/PCAP-freq-loader') // retrieve all Datastore data that has proper label
  .then(response => response.json())
  .then((data) => {
  
    // initialize nodes and edges arrays
    var nodes = new Array();
    var edges = new Array();

    var num_nodes = Object.keys(data).length;
    var title = "My Computer";

    // add source node
    var n = SOURCE;
    var edge = 0;
    nodes[n] = {id: n, label: title, group: n}; 
    n++;

    var limit = 0;
    // put all addresses onto graph
    Object.keys(data).forEach(key => {
      if (limit < NODE_LIMIT) {
        var normaled = normalize(data[key]);
        nodes[n] = {id: n, label: key + "\n" + data[key] + " Connections", group: n}; // maybe coloring by class?
        for (var m = 0; m < normaled; m++) {
            edges[edge] = {from: n, to: SOURCE};
            edge++;
        }
        n++;
      }
      limit++;
    });
    
    createNetwork();

    // normalize frequency on a scale from 1 to EDGE_LIMIT
    function normalize(m){
      if (num_nodes > 1) {
        var max = Object.keys(data)[0]; //largest frequency
        var min = Object.keys(data)[num_nodes-1]; //smallest frequency
        return ((m - data[min]) / (data[max] - data[min]) * EDGE_LIMIT + 1);
      }
      else {
        return m;
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
} // end obfuscated