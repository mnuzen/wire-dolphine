$(document).ready(async function () {
  await loadCharts();
});

async function loadCharts() {
  let data = await loadData();

  const trafficCount = data[0];
  const protocolCount = data[1];

  loadChart("protocolPieChart", Object.keys(protocolCount), Object.values(protocolCount),
    ['#36b9cc','#f6c23e','#858796'],
    ['#1DA0B3','#DDA925','#6C6E7D']);

  loadChart("trafficPieChart", Object.keys(trafficCount), Object.values(trafficCount),
    ['#4e73df', '#1cc88a'], ['#355AC6', '#03AF71']);
}

async function loadMyIP() {
  let ip;
  await fetch('/file-attributes')
    .then(response => response.json())
    .then((data) => {
      ip = data.myIP;
      
    });

    return ip;
}

async function loadData() {

  let userIP = await loadMyIP()

  trafficCount = {
    Incoming: 0,
    Outgoing: 0
  };

  protocolCount = {
    TCP: 0,
    UDP: 0,
    OTHER: 0 //add others that might be needed/add dynamically?
  };
 
  await fetch('/data')
    .then(response => response.json())
    .then((data) => {
      for (i in data) {
        
        //traffic counter
        if (data[i].source === userIP) {
          trafficCount.Outgoing++;
        }
        else {
          trafficCount.Incoming++;
        }

        //protocol counter
        switch (data[i].protocol.toLowerCase()) {
          case "tcp":
            protocolCount.UDP++;
            break;

          case "udp":
            protocolCount.TCP++;
            break;

          default:
            protocolCount.OTHER++;
        }
      }
    });

    return [trafficCount, protocolCount]
}

function loadChart(chartID, chartLables = [], chartData = [], chartBColor = [], chartHColor = []) {
  Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
  Chart.defaults.global.defaultFontColor = '#858796';

  var pieID = document.getElementById(chartID);
  var myPieChart = new Chart(pieID, {
    type: 'pie',
    data: {
      labels: chartLables,
      datasets: [{
        data: chartData,
        backgroundColor: chartBColor,
        hoverBackgroundColor: chartHColor,
        hoverBorderColor: "rgba(234, 236, 244, 1)",
      }],
    },
    options: {
      maintainAspectRatio: false,
      tooltips: {
        backgroundColor: "rgb(255,255,255)",
        bodyFontColor: "#858796",
        borderColor: '#dddfeb',
        borderWidth: 1,
        xPadding: 15,
        yPadding: 15,
        displayColors: false,
        caretPadding: 10,
      },
      legend: {
        display: true,
        position: 'bottom',
        align: "start"
      },
    },
  });
}