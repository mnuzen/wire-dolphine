function loadData() {
  fetch('/data')
    .then(response => response.json())
    .then((data) => {
      maliciousCount = {
        true: 0,
        false: 0,
        unknown: 0
      };

      trafficCount = {
        incoming: 1,
        outgoing: 1
      };

      protocolCount = {
        HTTP: 0,
        HTTPS: 0,
        UDP: 0,
        TCP: 0,
        DNS: 0,
        OTHER: 0 //add others that might be needed/add dynamically?
      };
      for (i in data) {

        //malicious Counter
        if (data[i].flagged == true) {
          window.maliciousCount.true++;
        } else if (data[i].flagged == false) {
          window.maliciousCount.false++;
        } else {
          window.maliciousCount.unknown++;
        }

        //protocol
        switch (data[i].protocol) {
          case "HTTP":
            window.protocolCount.HTTP++;
            break;

          case "HTTPS":
            window.protocolCount.HTTPS++;
            break;

          case "UDP":
            window.protocolCount.UDP++;
            break;

          case "TCP":
            window.protocolCount.TCP++;
            break;

          case "DNS":
            window.protocolCount.DNS++;
            break;

          default:
            window.protocolCount.OTHER++;
        }
      }

      // Set new default font family and font color to mimic Bootstrap's default styling
      Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
      Chart.defaults.global.defaultFontColor = '#858796';

      // Protocol Chart
      var proID = document.getElementById("protocolPieChart");
      var myPieChart = new Chart(proID, {
        type: 'pie',
        data: {
          labels: ["HTTP", "HTTPS", "UDP", "TCP", "DNS", "Other"],
          datasets: [{
            data: [protocolCount.HTTP, protocolCount.HTTPS, protocolCount.UDP,
              protocolCount.TCP, protocolCount.DNS, protocolCount.OTHER
            ],
            backgroundColor: ['#f6c23e', '#1cc88a', '#e74a3b', '#36b9cc', '#4e73df', '#858796'],
            hoverBackgroundColor: ['#DDA925', '#03AF71', '#CE3122', '#1DA0B3', '#355AC6', '#6C6E7D'],
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


      // Mal IP Chart
      var malID = document.getElementById("maliciousPieChart");
      var myPieChart = new Chart(malID, {
        type: 'pie',
        data: {
          labels: ["Good", "Bad", "Unknown"],
          datasets: [{
            data: [maliciousCount.false, maliciousCount.true, maliciousCount.unknown],
            backgroundColor: ['#1cc88a', '#e74a3b', '#36b9cc'],
            hoverBackgroundColor: ['#03AF71', '#CE3122', '#1DA0B3'],
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


      // Traffic Chart
      var traID = document.getElementById("trafficPieChart");
      var myPieChart = new Chart(traID, {
        type: 'pie',
        data: {
          labels: ["Incoming", "Outgoing"],
          datasets: [{
            data: [trafficCount.incoming, trafficCount.outgoing],
            backgroundColor: ['#4e73df', '#1cc88a'],
            hoverBackgroundColor: ['#355AC6', '#03AF71'], //https://www.hexcolortool.com/ darken by 10% on hover
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

    });
}