$(document).ready(function () {
    $.ajax({
        type: 'GET',
        dataType: 'json',
        async: false,
        url: '/data', //table.json for hardcode
        success: function (data) {
            for (i in data) {
                var table = document.getElementById("table");
                var row = document.createElement("tr");

                if (data[i].flagged == true) { //maybe as string "true"
                    row.setAttribute("id", "flagged");
                }
                if (data[i].protocal == "HTTP") {
                    row.setAttribute("id", "warning");
                }

                row.innerHTML = "<td>" + data[i].source + "</td> <td>" + data[i].destination +
                    "</td> <td>" + data[i].domain + "</td> <td>" + data[i].location +
                    "</td> <td>" + data[i].size + "</td> <td>" + data[i].protocal +
                    "</td> <td>" + data[i].flagged + "</td>";

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

