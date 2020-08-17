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
        row.classList.add("table-row");

        row.innerHTML =
        "<td>" + data[i].fileName + "</td>" +
        "<td>" + data[i].myIP + "</td>" +
        "<td>" + data[i].uploadDate + "</td>" +
        "<td>" + "Some Description" + "</td>" +
        "<td>" + form(data[i].pcapEntity) + "</td>";

        table.appendChild(row);
      }

      $("#dataTable").DataTable();
    },
    error: function () {
      alert('Failed to load data from database');
    }
  });
});

// Trying to make table row clickable without showing the form button
// Attempts to hid the last <td> tag causes issues with bootstrap table function like sort/search
// Changing tag type resolves bootstrap issues but causes form to return null
// Current solotion, hide form and have blank column in HTML table
$(document).ready(function ($) {
  $(".table-row").click(function () {

    $(this).find("form").submit()
  });
});


function form(text){
  const BEGIN = "<form action='/list-of-files' method='post' id='file-form'> \
  <input type='hidden' name='file' value='";
  const END = "'/><input type='submit' name='mockdata'\
   value='Load File'/></form>";

  return BEGIN + text + END;
}