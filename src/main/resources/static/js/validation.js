var isCorrect = true;

function validateDataEvent() {
    validateData();
    showOrHideErrorMessage($(".empty-map-point-error-message"), isPickedMapPoint);
    return isCorrect && isPickedMapPoint;
}

function validateData() {
    isCorrect = true;
    validateRiverSpeedData($(".river-speed"));
    showOrHideErrorMessage($(".incorrect-input-values-error-message"), isCorrect);
}

function validateRiverSpeedData(element) {
    var isCorrectRiverSpeed = isPositiveDouble(element.val()) && element.val() >= 0.1 && element.val() <= 2;
    markInputByValidationResult(element, isCorrectRiverSpeed);
}

function markInputByValidationResult(input, isValid) {
    if (isValid) {
        input.addClass("is-valid");
        input.removeClass("is-invalid");
    } else {
        isCorrect = false;
        input.addClass("is-invalid");
        input.removeClass("is-valid");
    }
}

function showOrHideErrorMessage(errorMessage, correctParameter) {
    if (!correctParameter) {
        errorMessage.removeClass("d-none");
        isCorrect = false;
    } else if(!errorMessage.hasClass("d-none")){
        errorMessage.addClass("d-none");
    }
}

function isPositiveInteger(value) {
    var regular = /^(0|[1-9]{1}[0-9]{0,9})$/;
    return regular.test(value);
}

function isPositiveDouble(value) {
    var regular = /^(0|0{1}\.[0-9]{1,10}|[1-9]{1}[0-9]{0,9}(\.[0-9]{1,10})?)$/;
    return regular.test(value);
}