/** Create network graph one. */
function createNetworkOne(){
  fetch('/data') // retrieve all Datastore data that has "data" label
  .then(response => response.json())
  .then((data) => {
    const SOURCE = 0;
  
    // initialize nodes and edges arrays
    var nodes = new Array();
    var edges = new Array();

    populateGraph();
    createNetwork();

    /* Populate nodes and edges with the following formatting:
     * NODE: {id: ID, label: LABEL, group: GROUP} 
     * EDGE: {from: SOURCE, to: DESTINATION}
    */
    function populateGraph(){
      // add source node
      nodes[SOURCE] = {id: SOURCE, label: "My Computer", group: SOURCE}; //data[SOURCE].source

      // initialize edge counter
      var ed = 0;

      // add all destinations
      for (var i = 1; i < data.length; i++) {
        nodes[i] = { id: i, label: data[i].destination, group: i};

        // add edges based on freqs
        for (var j = 0; j < data[i].frequency; j++) {
          edges[ed] = { from: SOURCE, to: ed};
          ed++;
        }
      }
    }

    // create a network
    function createNetwork(){
      var container = document.getElementById("mynetwork");

      var finalData = {
        nodes: nodes,
        edges: edges,
      };

      var options = {
        nodes: {
          shape: "dot",
          size: 30,
          font: {
            size: 32,
            color: "#ffffff",
          },
          borderWidth: 2,
        },
        edges: {
          width: 2,
        },
      };

      network = new vis.Network(container, finalData, options);
    }
  });
}


/** Create network graph two with hard-coded data. */
function createNetworkTwo(){
  var color = "gray";
  var len = undefined;

  var nodes = [
    { id: 0, label: "My Computer", group: 0 },
    { id: 4, label: "Class C", group: 6 },
    { id: 7, label: "Class A", group: 8 },
    { id: 13, label: "Class E", group: 7 },
    { id: 15, label: "Class D", group: 5 },
    { id: 16, label: "224.0.0.0", group: 5 },
    { id: 17, label: "224.0.0.1", group: 5 },
    { id: 18, label: "193.0.1.1", group: 6 },
    { id: 19, label: "192.0.1.1", group: 6 },
    { id: 21, label: "240.0.0.0", group: 7 },
    { id: 22, label: "241.0.0.0", group: 7 },
    { id: 23, label: "242.0.0.0", group: 7 },
    { id: 24, label: "1.0.0.1", group: 8 },
    { id: 25, label: "1.1.1.1", group: 8 },
    { id: 26, label: "126.1.1.1", group: 8 },
    { id: 27, label: "128.2.0.1", group: 9 },
    { id: 28, label: "Class B", group: 9 },
    { id: 29, label: "128.1.0.1", group: 9 },
  ];

  var edges = [
    { from: 1, to: 0 },
    { from: 2, to: 0 },
    { from: 4, to: 3 },
    { from: 5, to: 4 },
    { from: 4, to: 0 },
    { from: 7, to: 6 },
    { from: 8, to: 7 },
    { from: 7, to: 0 },
    { from: 10, to: 9 },
    { from: 11, to: 10 },
    { from: 10, to: 4 },
    { from: 13, to: 12 },
    { from: 14, to: 13 },
    { from: 13, to: 0 },
    { from: 16, to: 15 },
    { from: 17, to: 15 },
    { from: 15, to: 0 },
    { from: 19, to: 18 },
    { from: 19, to: 18 },
    { from: 19, to: 18 },
    { from: 20, to: 19 },
    { from: 19, to: 4 },
    { from: 22, to: 21 },
    { from: 23, to: 22 },
    { from: 22, to: 13 },
    { from: 25, to: 24 },
    { from: 26, to: 25 },
    { from: 25, to: 7 },
    { from: 28, to: 27 },
    { from: 29, to: 28 },
    { from: 29, to: 28 },
    { from: 29, to: 28 },
    { from: 28, to: 0 },
  ];

  // create a network
  var container = document.getElementById("mynetwork");
  
  var data = {
    nodes: nodes,
    edges: edges,
  };

  var options = {
    nodes: {
      shape: "dot",
      size: 30,
      font: {
        size: 32,
        color: "#ffffff",
      },
      borderWidth: 2,
    },
    edges: {
      width: 2,
    },
  };

  network = new vis.Network(container, data, options);
}
