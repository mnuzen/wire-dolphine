$(document).ready(async function () {
  await $.ajax({
    type: 'GET',
    dataType: 'json',
    async: false,
    url: '/data-malicious',
    success: function (data) {

      maliciousCount = {
        Bad: 0,
        Good: 0,
        Unknown: 0
      };

      for (i in data) {

        var table = document.getElementById("table");
        var row = document.createElement("tr");

        if (data[i].flagged.toLowerCase() === "true") {
          row.setAttribute("id", "flagged");
          maliciousCount.Bad++;
        }else if (data[i].flagged.toLowerCase() === "false") {
          maliciousCount.Good++;
        } else {
          maliciousCount.Unknown++;
        }

        row.innerHTML = "<td>" + data[i].destination + "</td> <td>" + data[i].domain + 
          "</td> <td>" + data[i].location + "</td> <td>" + data[i].flagged + "</td>";

        table.appendChild(row);
      }
      
      loadChart("maliciousPieChart", Object.keys(maliciousCount), Object.values(maliciousCount),
      ['#e74a3b', '#1cc88a', '#36b9cc'],
      ['#CE3122', '#03AF71', '#1DA0B3'], );

      $("#dataTable").DataTable();
    },
    error: function () {
      alert('Failed to load data from database');
    }
  });
});

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