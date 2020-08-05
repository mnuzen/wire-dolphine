const SOURCE = 0;
const SHAPE = "dot"
const SHAPE_SIZE = 16;
const EDGE_LIMIT = 50; // limit the number of edges on visualization 

const GROUP1 = 1;
const GROUP2 = 2;
const GROUP3 = 3;

const GRAV_CONSTANT = -26;
const CENTRAL_GRAV = 0.005;
const SPRING_LENGTH = 230;
const SPRING_CONST = 0.18;
const MAX_VEL = 146;
const TIMESTEP = 0.35;
const ITER = 150; 

const NUM_GROUPS = 3;

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
      nodes[GROUP1] = {id: GROUP1, label: "Group 17", group: GROUP1};
      edges[edge] = {from: SOURCE, to: GROUP1};
      edge++;

      nodes[GROUP2] = {id: GROUP2, label: "Group 18", group: GROUP2};
      edges[edge] = {from: SOURCE, to: GROUP2};
      edge++;

      nodes[GROUP3] = {id: GROUP3, label: "Group 19", group: GROUP3};
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
        else { // all other IP addresses that do not start with 17, 18, or 19
          // add edges based on freqs
          var j = 0;
          while (j < EDGE_LIMIT && j < data[i].frequency){
            edges[edge] = {from: node, to: SOURCE};
            edge++;
            j++;
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
            gravitationalConstant: GRAV_CONSTANT,
            centralGravity: CENTRAL_GRAV,
            springLength: SPRING_LENGTH,
            springConstant: SPRING_CONST,
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