Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';


$(document).ready(async function() {
  await loadDomainVisualization();
});

async function loadData() {
  const res = await fetch('/reversedns');
  return await res.json();
}

async function loadDomainVisualization() {
  const data = await loadData();
  loadDescriptorsFromData(data);
  loadChartsFromData(data);
}

function loadChartsFromData(data) {
  const cdnData = data.cdn;
  const domainData = data.domain;
  loadChart(
      '#domainChart',
      'What domains your packets went to',
      'Packets',
      Object.keys(domainData),
      Object.values(domainData)
  );
  loadChart(
      '#cdnChart',
      'What CDNs your packets went to',
      'Packets',
      Object.keys(cdnData),
      Object.values(cdnData)
  )
}

function loadDescriptorsFromData(data) {
  const cdnPackets = Object.values(data.cdn).reduce((a, b) => a + b, 0);
  const domainPackets = Object.values(data.domain).reduce((a, b) => a + b, 0);
  const percentCDN = ((cdnPackets/cdnPackets + domainPackets) * 100).toFixed(1);
  $("#cdnPackets").text(cdnPackets);
  $("#domainPackets").text(domainPackets);
  $("#percentCDN").text(percentCDN);
}

function loadChart(
    selector,
    title,
    label,
    labels = [],
    data = [],
    color = '#4e73df',
) {
  const ctx = $(selector);
  const chart = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [
        {
          label: label,
          backgroundColor: color,
          hoverBackgroundColor: '#2e59d9',
          borderColor: color,
          data: data,
        }],
    },
    options: {
      maintainAspectRatio: false,
      layout: {
        padding: {
          left: 10,
          right: 25,
          top: 25,
          bottom: 0,
        },
      },
      scales: {
        xAxes: [
          {
            time: {
              unit: 'Packets',
            },
            gridLines: {
              display: false,
              drawBorder: false,
            },
            ticks: {
              maxTicksLimit: 6,
            },
            maxBarThickness: 25,
          }],
        yAxes: [
          {
            ticks: {
              min: 0,
              padding: 10,
            },
            gridLines: {
              color: 'rgb(234, 236, 244)',
              zeroLineColor: 'rgb(234, 236, 244)',
              drawBorder: false,
              borderDash: [2],
              zeroLineBorderDash: [2],
            },
          }],
      },
      legend: {
        display: false,
      },
      title: {
        text: title,
        display: true,
      },
      tooltips: {
        titleMarginBottom: 10,
        titleFontColor: '#6e707e',
        titleFontSize: 14,
        backgroundColor: 'rgb(255,255,255)',
        bodyFontColor: '#858796',
        borderColor: '#dddfeb',
        borderWidth: 1,
        xPadding: 15,
        yPadding: 15,
        displayColors: false,
        caretPadding: 10,
      },
    },
  });
}

