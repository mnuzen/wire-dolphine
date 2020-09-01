// Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';

var addrs = [];
var freqs = [];

$(document).ready(async function () {
  await drawIPVisualization();
});


async function drawIPVisualization() {
    await setup();

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
    } //end options
    });
}

async function setup() {
  await fetch('/PCAP-IP')
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