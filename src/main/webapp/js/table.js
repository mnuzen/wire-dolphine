$(document).ready(function () {
    $.ajax({
        type: 'GET',
        dataType: 'json',
        async: false,
        url: '/data',
        success: function (data) {
            for (i in data) {

                var table = document.getElementById("table");
                var row = document.createElement("tr");

                if (data[i].flagged === "TRUE") { //change for string value in malicous final
                    row.setAttribute("id", "flagged");
                }
                else if (data[i].protocol === "HTTP") {
                    row.setAttribute("id", "warning");
                }

                row.innerHTML = "<td>" + data[i].source + "</td> <td>" + data[i].destination +
                    "</td> <td>" + data[i].domain + "</td> <td>" + data[i].location +
                    "</td> <td>" + data[i].protocol + "</td> <td>" + data[i].size +
                    "</td> <td>" + data[i].flagged +"</td> <td>" + data[i].frequency + "</td>";

                table.appendChild(row);
            }
            //DataTables instantiation.
            $("#dataTable").DataTable();
        },
        error: function () {
            alert('Failed to load data from database');
        }
    });
}); 

