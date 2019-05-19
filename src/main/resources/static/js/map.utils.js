function onMapClick(e) {
    removeMarketOnMap();

    currentMarker = L.marker([e.latlng.lat, e.latlng.lng]);
    currentMarker.addTo(mymap);

    $("#lat").val(e.latlng.lat);
    $("#lng").val(e.latlng.lng);
    isPickedMapPoint = true;
    showOrHideErrorMessage($(".empty-map-point-error-message"), isPickedMapPoint);
}

function removeMarketOnMap() {
    currentMarker.remove();
}