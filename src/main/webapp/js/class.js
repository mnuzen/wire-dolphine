var NUM_CLASSES = 4;
drawClassVisualization();

function drawClassVisualization() {
  fetch('/PCAP-bucket')
  .then(response => response.json())
  .then((classData) => {

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

    var tableOptions = {
      vAxis: {title: 'Number of Packets'},
      hAxis: {title: 'IP Class'},
      seriesType: 'bars',
      series: {3: {type: 'line'}}        
    };

    // Draw chart
    var chart = new google.visualization.ComboChart(document.getElementById('chart_class_div'));
    chart.draw(data, tableOptions);

    /** Functions */
    function parseProtocols() {
      // Loop through all four classes
      Object.keys(classData).forEach(className =>  {
        Object.keys(classData[className]).forEach(key => {
          protocols.add(key); // add all possible protocols
        });
      });
    }

    // Iterate through all Classes and parser protocol frequencies
    function addRows() {
      Object.keys(classData).forEach(className => {
        var row = [];
        var total = 0;
        row.push(className); // add Class

        // add protocols in correct order
        protocols.forEach(protocol => {
          if (protocol in classData[className]) {
            row.push(classData[className][protocol]);
            total += classData[className][protocol]; 
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
