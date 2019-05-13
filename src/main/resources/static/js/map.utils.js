function onMapClick(e) {
    removeMarketOnMap();

    nearestPointId = findNearestPointId(turf.point([e.latlng.lng, e.latlng.lat]));
    currentMarker = L.marker([svislochRiverCoordinates[nearestPointId][1], svislochRiverCoordinates[nearestPointId][0]]);
    currentMarker.addTo(mymap);

    $("#lat").val(svislochRiverCoordinates[nearestPointId][1]);
    $("#lng").val(svislochRiverCoordinates[nearestPointId][0]);
}

function findNearestPointId(point) {
    var minDistance = turf.distance(turf.point([svislochRiverCoordinates[0][0], svislochRiverCoordinates[0][1]]), point);
    var pointId = 0;

    for (var i = 1; i < svislochRiverCoordinates.length; i++) {
        var distance = turf.distance(turf.point([svislochRiverCoordinates[i][0], svislochRiverCoordinates[i][1]]), point);
        if (distance < minDistance) {
            minDistance = distance;
            pointId = i;
        }
    }

    return pointId;
}

function removeMarketOnMap() {
    currentMarker.remove();
}

function createNewPoint() {
    var $newPointTemplate = $('<div class="row time-concentration-pair"><div class="form-group col-5 col-sm-5 col-md-5 col-lg-5 col-xl-5"><label class="time-label" for="time">' + timeTitle + '</label><input type="text" class="form-control time-input" id="time" value="0.0"></div><div class="form-group col-5 col-sm-5 col-md-5 col-lg-5 col-xl-5"><label class="concentration-label" for="concentration">' + concentrationTitle + '</label><input type="text" class="form-control concentration-input" id="concentration" value="0.0"></div><div class=" col-2 col-sm-2 col-md-2 col-lg-2 col-xl-2 d-flex align-items-center"><a href="#" class="text-danger mt-3 remove-point"><i class="fas fa-minus"></i></a></div></div>');
    $(".time-concentration-pair").last().after($newPointTemplate);
}

function removePoint(e) {
    $(this).parent().parent().remove();
}

function generateJson() {
    var timeList = $(".time-input").map((idx, elem) => $(elem).val()).get();
    var concentrationList = $(".concentration-input").map((idx, elem) => $(elem).val()).get();

    var result = "[";
    for (var i = 0; i < timeList.length; i++) {
        result = result + "{\"concentration\": " + concentrationList[i] + ",\"time\": " + timeList[i] + "}";
        if (i !== timeList.length - 1) {
            result = result + ",";
        }
    }
    result = result + "]"

    $("#timeConcentrationPairsJson").val(result);
}