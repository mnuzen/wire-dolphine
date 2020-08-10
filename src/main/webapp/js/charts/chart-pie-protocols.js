// Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';

// Pie Chart Example
var ctx = document.getElementById("protocalPieChart");
var myPieChart = new Chart(ctx, {
  type: 'pie',
  data: {
    labels: ["HTTP", "HTTPS", "UDP", "TCP", "DNS", "DHCP"],
    datasets: [{
      data: [protocolCount.HTTP, protocolCount.HTTPS, protocolCount.UDP,
        protocolCount.TCP, protocolCount.DNS, protocolCount.DHCP
      ],
      backgroundColor: ['#f6c23e', '#1cc88a', '#e74a3b', '#36b9cc', '#858796', '#4e73df'],
      hoverBackgroundColor: ['#DDA925', '#03AF71', '#CE3122', '#1DA0B3', '#6C6E7D', '#355AC6'],
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
