// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

var DATA = null;
$(document).ready(async function() {
  await loadMapVisualization();
});

async function loadMapVisualization(url = '/ipgeolocation?PCAPId=sampleId') {
  const res = await fetch(url);
  const countryData = await res.json();
  const datatableHeader = [['Country', 'Requests Sent']];
  const datatableFormat = Object.keys(countryData).map((country) => [
      country,
      countryData[country]
  ]);
  const datatable = datatableHeader.concat(datatableFormat);
  google.charts.load('current', {
    'packages':['geochart'],
    'mapsApiKey': 'AIzaSyDp7gKLNrLGlNIZJtj81lKoFQqIOHJG_PQ'
  });
  google.charts.setOnLoadCallback(drawRegionsMap);
  loadMapStatistics(countryData);
  function drawRegionsMap() {
    const data = google.visualization.arrayToDataTable(datatable);
    const options = {
      backgroundColor: '#f8f9fc',
      datalessRegionColor: '#ffffff',
      colorAxis: {
        minValue: 0
      },
      explorer: {
        axis: 'horizontal',
        keepInBounds: true,
        maxZoomIn: 4.0
      }
    };
    const chart = new google.visualization.GeoChart(document.getElementById('regions_div'));

    chart.draw(data, options);
  }
}

async function loadMapStatistics(data) {
  // todo: handle no data case
  const totalRequests = Object.values(data).reduce((a, b) => a + b);
  const totalCountries = Object.values(data).length;
  const byRequestNum = (a, b) => data[a] - data[b];
  const mostFreqCountry = Object.keys(data).sort(byRequestNum).pop();
  const mostFreqPercentage = (100.0 * data[mostFreqCountry] / totalRequests).toFixed(1);
  $('#request-num').text(totalRequests);
  $('#request-countries').text(totalCountries);
  $('#mostFreqCountry').text(mostFreqCountry);
  $('#mostFreqCountryNum').text(data[mostFreqCountry]);
  $('#mostFreqCountryPercentage').text(mostFreqPercentage);
}