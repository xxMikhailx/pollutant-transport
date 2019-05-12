var mymap = L.map('mainMap').setView([53.902334, 27.5618791], 7);

L.tileLayer('https://a.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(mymap);
L.geoJson(svislochRiver).addTo(mymap);