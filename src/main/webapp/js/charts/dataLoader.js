
window.maliciousCount = {
    true: 1,
    false: 1,
    unknown: 1
};

window.trafficCount = {
    incoming: 1,
    outgoing: 1
  };

  window.protocolCount = {
    HTTP: 1,
    HTTPS: 1,
    UDP: 1,
    TCP: 1,
    DNS: 1,
    DHCP: 1 //add others that might be needed/add var by dynamically?
  };
  
  //can't get it to update global scope
  function loadData() {
  fetch('/data') // retrieve all Datastore data that has "data" label
    .then(response => response.json())
    .then((data) => {
      for (i in data) {

        //malicious Counter
        if (data[i].flagged === "TRUE") {
          window.maliciousCount.true++;
        } else if (data[i].flagged === "FAlSE") {
          window.maliciousCount.false++;
        } else {
          window.maliciousCount.unknown++;
        }
        window.protocolCount.HTTP++;
        //protocal
        switch (data[i].protocol) {
          case "HTTP":
            window.protocolCount.HTTP++;
            break;

          case "HTTPS":
            window.protocolCount.HTTPS++;
            break;

          case "UDP":
            window.protocolCount.UDP++;
            break;

          case "TCP":
            window.protocolCount.TCP++;
            break;

          case "DNS":
            window.protocolCount.DNS++;
            break;

          case "DHCP":
            window.protocolCount.DHCP++;
            break;

          default:
            throw new Error('Unknown Protocol');
        }
      }
    });
}