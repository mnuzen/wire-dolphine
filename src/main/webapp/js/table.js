$(document).ready(function () {
    $.ajax({
        type: 'GET',
        dataType: 'json',
        async: false, // local run
        url: 'table.json',
        success: function (data) {
            for (i in data) {
                var table = document.getElementById("table");
                var row = document.createElement("tr");

                if (data[i].Flagged == 1) {
                    row.setAttribute("id", "flagged");
                }
                if (data[i].Protocal == "HTTP") {
                    row.setAttribute("id", "warning");
                }

                row.innerHTML = "<td>" + data[i].Source + "</td> <td>" + data[i].Destination +
                    "</td> <td>" + data[i].Domain + "</td> <td>" + data[i].Location +
                    "</td> <td>" + data[i].Size + "</td> <td>" + data[i].Protocal +
                    "</td> <td>" + data[i].Flagged + "</td>";

                table.appendChild(row);
            }
            /*DataTables instantiation.*/
            $("#dataTable").DataTable();
        },
        error: function () {
            alert('Fail!');
        }
    });
});