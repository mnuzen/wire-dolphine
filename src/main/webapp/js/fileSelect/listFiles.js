$(document).ready(async function () {
    await $.ajax({
        type: 'GET',
        dataType: 'json',
        async: false,
        url: '/list-of-files',
        success: function (data) {
            for (i in data) {

                var table = document.getElementById("table");
                var row = document.createElement("tr");
                row.innerHTML = "<td>" + data[i].pcapEntity + "</td> <td>" + data[i].fileName +
                    "</td> <td>" + data[i].myIP + "</td> <td>" + data[i].uploadDate + "</td>";
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

