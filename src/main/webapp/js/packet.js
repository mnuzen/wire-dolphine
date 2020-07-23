/**
 * Adds PCAP data.
 */ 
function getPCAPDestination() {
    fetch('/PCAP-data') // sends a request to /data
    .then(response => response.json()) // parases response as JSON 
    .then((packets) => {
      // packets is an object, not a string, so we have to
      // reference its fields to create HTML content
  
      const packetElement = document.getElementById('message-container');
      packetElement.innerHTML = '';
      packets.forEach(element => packetElement.appendChild(createListElement(element)));
    });
  }