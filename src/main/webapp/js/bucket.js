google.charts.load('current', {'packages':['corechart']});
//google.charts.setOnLoadCallback(drawVisualization);

function drawVisualization() {
  fetch('/PCAP-bucket')
  .then(response => response.json())
  .then((bucketData) => {

    google.charts.load('current', {'packages':['corechart']});
    var data = new google.visualization.DataTable();

    // Declare columns
    data.addColumn('string', 'IP Class');
    data.addColumn('number', 'UDP');
    data.addColumn('number', 'TCP');
    data.addColumn('number', 'IPv4');
    data.addColumn('number', 'Total');

    Object.keys(bucketData).forEach(key => {
      // int[UDP, TCP, IPv4, TOT]
      var UDP = bucketData[key][0];
      var TCP = bucketData[key][1];
      var IPv4 = bucketData[key][2];
      var TOT = bucketData[key][3];
      data.addRow([key, UDP, TCP, IPv4, TOT]);
    });

    var options = {
        title : 'Number of Connections per Protocol by IP Class',
        vAxis: {title: 'Number of Connections'},
        hAxis: {title: 'IP Class'},
        seriesType: 'bars',
        series: {3: {type: 'line'}}        };

    var chart = new google.visualization.ComboChart(document.getElementById('chart_div'));
    chart.draw(data, options);
  });
}