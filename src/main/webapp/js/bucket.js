google.charts.load('current', {'packages':['corechart']});
var NUM_CLASSES = 4;

function drawVisualization() {
  fetch('/PCAP-bucket')
  .then(response => response.json())
  .then((bucketData) => {

    google.charts.load('current', {'packages':['corechart']});
    var data = new google.visualization.DataTable();
    
    // Declare protocols set
    let protocols = new Set();

    // Loop through all four classes
    for (var i = 0; i < NUM_CLASSES; i++) {
      Object.keys(bucketData[i]).forEach(key => {
        //protocols.add(key); // add all possible protocols
      });
      protocols.add(i);
      console.log(bucketData[i]);
    }

     // Add columns
    data.addColumn('string', 'IP Class');
    protocols.forEach(protocol => {
      data.addColumn('number', protocol);
    });

    // Loop through all four classes
    Object.keys(bucketData).forEach(className => {
      var row = [];
      row.push(className); // add Class

      // add protocols in correct order
      protocols.forEach(protocol => {
        if (protocol in bucketData[className]) {
          row.push(bucketData[className][protocol]); 
        }
        else {
          row.push(0);
        }
      });
      data.addRow(row);
    });

    var options = {
      title : 'Number of Connections per Protocol by IP Class',
      vAxis: {title: 'Number of Connections'},
      hAxis: {title: 'IP Class'},
      seriesType: 'bars',
      series: {2: {type: 'line'}}        
    };

    var chart = new google.visualization.ComboChart(document.getElementById('chart_div'));
    chart.draw(data, options);
  });
}