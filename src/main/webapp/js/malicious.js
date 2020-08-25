$(document).ready(async function () {
  await $.ajax({
    type: 'GET',
    dataType: 'json',
    async: false,
    url: '/data-malicious',
    success: function (data) {

      let maliciousCount = {
        Bad: 0,
        Good: 0,
        Unknown: 0
      };

      let locationMap = new Map()
      let locationList = [];
      let freqList = [];

      for (i in data) {

        var table = document.getElementById("table");
        var row = document.createElement("tr");

        if (data[i].flagged.toLowerCase() === "true") {
          row.setAttribute("id", "flagged");
          maliciousCount.Bad++;

          freqList.push({key: data[i].domain, value: data[i].frequency})

          //Counts Location of IP's
          if (locationMap.has(data[i].location)) {
            locationMap.get(data[i].location).val++;
          } else {
            locationMap.set(data[i].location, {
              val: 1
            });
          }
        } else if (data[i].flagged.toLowerCase() === "false") {
          maliciousCount.Good++;
        } else {
          maliciousCount.Unknown++;
        }

        row.innerHTML = "<td>" + data[i].destination + "</td> <td>" + data[i].domain +
          "</td> <td>" + data[i].location + "</td> <td>" + data[i].flagged + "</td>";

        table.appendChild(row);
      }
        
      //Sorts hashmap by key and Top 5 locations
      locationMap[Symbol.iterator] = function* () {
        yield*[...this.entries()].sort(function (a, b) {
          return +b[1] - +a[1]
        });
      }

      let index = 0;
      for (let [key, value] of locationMap) {
        locationList[key] = value.val;
        index++;
        if (index == 5) {
          break;
        }
      }
      
      // sorts and splits freq
      freqList.sort(function(a, b){return b.value - a.value}).slice(0,5);

      freqKey = freqList.map(function (obj) {
        return obj.key;
      });

      freqValues = freqList.map(function (obj) {
        return obj.value;
      });


      loadChart("maliciousPieChart", Object.keys(maliciousCount), Object.values(maliciousCount),
        ['#e74a3b', '#1cc88a', '#36b9cc'],
        ['#CE3122', '#03AF71', '#1DA0B3'], );

      loadChart("locationPieChart", Object.keys(locationList), Object.values(locationList),
        ['#858796', '#f6c23e', '#36b9cc', '#1cc88a', '#e74a3b'],
        ['#6C6E7D', '#DDA925', '#1DA0B3', '#03AF71', '#CE3122']);

      loadChart("freqPieChart", freqKey, freqValues,
        ['#FF588C', '#f6c23e', '#36b9cc', '#1cc88a', '#e74a3b'],
        ['#f63e72', '#DDA925', '#1DA0B3', '#03AF71', '#CE3122']);

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