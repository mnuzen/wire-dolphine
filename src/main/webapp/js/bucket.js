google.charts.load('current', {'packages':['corechart']});
var NUM_CLASSES = 4;

function drawVisualization() {
  drawClassVisualization();
  drawIPVisualization();    
}

function drawClassVisualization() {
  fetch('/PCAP-bucket')
  .then(response => response.json())
  .then((bucketData) => {

    google.charts.load('current', {'packages':['corechart']});
    var data = new google.visualization.DataTable();
    
    // Declare protocols set
    let protocols = new Set();
    parseProtocols();

     // Add columns
    data.addColumn('string', 'IP Class');
    protocols.forEach(protocol => {
      data.addColumn('number', protocol);
    });
    data.addColumn('number', 'Total');

    // Add rows
    addRows();
    
    // Set up chart
    var graphSize = parseInt(protocols.size-1);
    console.log(graphSize);

    var tableOptions = {
      title : 'Number of Connections per Protocol by IP Class',
      vAxis: {title: 'Number of Connections'},
      hAxis: {title: 'IP Class'},
      seriesType: 'bars',
      series: {graphSize: {type: 'line'}}        
    };

    // Draw chart
    var chart = new google.visualization.ComboChart(document.getElementById('chart_class_div'));
    chart.draw(data, tableOptions);

    /** Functions */
    function parseProtocols() {
      // Loop through all four classes
      Object.keys(bucketData).forEach(className =>  {
        Object.keys(bucketData[className]).forEach(key => {
          protocols.add(key); // add all possible protocols
        });
      });
    }

    // Iterate through all Classes and parser protocol frequencies
    function addRows() {
      Object.keys(bucketData).forEach(className => {
        var row = [];
        var total = 0;
        row.push(className); // add Class

        // add protocols in correct order
        protocols.forEach(protocol => {
          if (protocol in bucketData[className]) {
            row.push(bucketData[className][protocol]);
            total += bucketData[className][protocol]; 
          }
          else {
            row.push(0);
          }
        });

        row.push(total);
        data.addRow(row);
      });
    }
  });
} // end class visualization


function drawIPVisualization() {
  fetch('/PCAP-IP')
  .then(response => response.json())
  .then((bucketData) => {

    google.charts.load('current', {'packages':['corechart']});
    var data = new google.visualization.DataTable();
    
     // Add columns
    data.addColumn('string', 'IP Class');
    data.addColumn('number', 'Total');

    // Add rows
    addRows();

    var tableOptions = {
      title : 'Number of Connections Grouped by Prefix',
      vAxis: {title: 'Number of Connections'},
      hAxis: {title: 'Prefix'},
      seriesType: 'bars',
      series: {1: {type: 'line'}}        
    };

    // Draw chart
    var chart = new google.visualization.ComboChart(document.getElementById('chart_dist_div'));
    chart.draw(data, tableOptions);

    // Iterate through all Classes and parser protocol frequencies
    function addRows() {
      Object.keys(bucketData).forEach(addr => {
        data.addRow([addr, bucketData[addr]]);
        console.log("Address and Frequency: " + addr + bucketData[addr]);
      });
    }
  });
} // end class visualization
