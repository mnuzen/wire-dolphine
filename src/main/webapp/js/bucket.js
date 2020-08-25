google.charts.load('current', {'packages':['corechart']});
var NUM_CLASSES = 4;

// Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';

var addrs = [];
var freqs = [];

function drawVisualization(){
    drawIPVisualization();
    drawClassVisualization();
}

function drawIPVisualization() {
    setup();

    var classes = ["65.0.0.0/8"];
    var name = ["Class A"];
    // populate 'annotations' array dynamically based on 'classes'
    var annotations = classes.map(function(cl, index) {
    return {
        type: 'line',
        id: 'vline' + index,
        mode: 'vertical',
        scaleID: 'x-axis-0',
        value: cl,
        borderColor: 'green',
        borderWidth: 1,
        label: {
            enabled: true,
            position: "center",
            content: name[index]
        }
    }
    });
    console.log(annotations);

    var ctx = document.getElementById("IPVis");
    var IPVis = new Chart(ctx, {
    type: 'bar',
    data: {
        labels: addrs,
        datasets: [{
        label: "Number of Packets",
        backgroundColor: "#4e73df",
        hoverBackgroundColor: "#2e59d9",
        borderColor: "#4e73df",
        data: freqs,
        }],
    },
    options: {
        maintainAspectRatio: false,
        layout: {
        padding: {
            left: 10,
            right: 25,
            top: 25,
            bottom: 0
        }
        },
        scales: {
        xAxes: [{
            labelString: "Number of Packets",
            gridLines: {
            display: true,
            drawBorder: false
            },
            ticks: {
            maxTicksLimit: 6
            },
            maxBarThickness: 25,
        }],
        yAxes: [{
            labelString: "IP Prefix",
            gridLines: {
            color: "rgb(234, 236, 244)",
            zeroLineColor: "rgb(234, 236, 244)",
            drawBorder: false,
            borderDash: [2],
            zeroLineBorderDash: [2]
            }
        }],
        },
        legend: {
        display: true
        },
        tooltips: {
        titleMarginBottom: 10,
        titleFontColor: '#6e707e',
        titleFontSize: 14,
        backgroundColor: "rgb(255,255,255)",
        bodyFontColor: "#858796",
        borderColor: '#dddfeb',
        borderWidth: 1,
        xPadding: 15,
        yPadding: 15,
        displayColors: false,
        caretPadding: 10,
        },
        /*annotation: {
         drawTime: 'afterDatasetsDraw',
         annotations: annotations
        },*/
    } //end options
    });
}

function setup() {
  fetch('/PCAP-IP')
  .then(response => response.json())
  .then((bucketData) => {
    // Iterate through all Classes and parser protocol frequencies
    Object.keys(bucketData).forEach(addr => {
      addrs.push(addr);
      freqs.push(bucketData[addr]);
      console.log("Address and Frequency: " + addr + bucketData[addr]);
    });
  });
} // end setup

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
    console.log(graphSize);

    var tableOptions = {
      title : 'Number of Packets per Protocol by IP Class',
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
