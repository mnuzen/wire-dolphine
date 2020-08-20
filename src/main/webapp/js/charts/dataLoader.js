$(document).ready(async function () {
  await loadCharts();
});

async function loadCharts() {
  let data = await loadData();

  const maliciousCount = data[0];
  const trafficCount = data[1];
  const protocolCount = data[2];

  loadChart("protocolPieChart", Object.keys(protocolCount), Object.values(protocolCount),
    ['#f6c23e', '#1cc88a', '#e74a3b', '#36b9cc', '#4e73df', '#858796'],
    ['#DDA925', '#03AF71', '#CE3122', '#1DA0B3', '#355AC6', '#6C6E7D']);

  loadChart("trafficPieChart", Object.keys(trafficCount), Object.values(trafficCount),
    ['#4e73df', '#1cc88a'], ['#355AC6', '#03AF71']);


  loadChart("maliciousPieChart", Object.keys(maliciousCount), Object.values(maliciousCount),
    ['#e74a3b', '#1cc88a', '#36b9cc'],
    ['#CE3122', '#03AF71', '#1DA0B3'], );

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

  maliciousCount = {
    Bad: 0,
    Good: 0,
    Unknown: 0
  };

  trafficCount = {
    Incoming: 0,
    Outgoing: 0
  };

  protocolCount = {
    HTTP: 0,
    HTTPS: 0,
    UDP: 0,
    TCP: 0,
    DNS: 0,
    OTHER: 0 //add others that might be needed/add dynamically?
  };
 
  await fetch('/data-table')
    .then(response => response.json())
    .then((data) => {
      for (i in data) {
        
        //malicious counter
        if (data[i].flagged.toLowerCase() === "true") {
          maliciousCount.Bad++;
        } else if (data[i].flagged.toLowerCase() === "false") {
          maliciousCount.Good++;
        } else {
          maliciousCount.Unknown++;
        }

        //traffic counter
        if (data[i].source === userIP) {
          trafficCount.Outgoing++;
        }
        else {
          trafficCount.Incoming++;
        }

        //protocol counter
        switch (data[i].protocol.toLowerCase()) {
          case "http":
            protocolCount.HTTP++;
            break;

          case "https":
            protocolCount.HTTPS++;
            break;

          case "udp":
            protocolCount.UDP++;
            break;

          case "tcp":
            protocolCount.TCP++;
            break;

          case "dns":
            protocolCount.DNS++;
            break;

          default:
            protocolCount.OTHER++;
        }
      }
    });

    return [maliciousCount, trafficCount, protocolCount]
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