var isCorrect = true;

function validateDataEvent(event) {
    validateData();
    return isCorrect;
}

function validateData() {
    isCorrect = true;
    $(".concentration-input").map((idx, element) => validateConcentrationData($(element)));
    $(".time-input").map((idx, element) => validateTimeData($(element)));
    validateRiverSpeedData($(".river-speed"));
    showOrHideErrorMessage($(".incorrect-input-values-error-message"), isCorrect);
    validateTimeRepeatable();
}

function validateConcentrationData(element) {
    var isCorrectConcentration = isPositiveDouble(element.val()) && element.val() <= 100;
    markInputByValidationResult(element, isCorrectConcentration);
}

function validateTimeData(element) {
    markInputByValidationResult(element, isPositiveInteger(element.val()));
}

function validateRiverSpeedData(element) {
    var isCorrectRiverSpeed = isPositiveDouble(element.val()) && element.val() >= 0.1 && element.val() <= 2;
    markInputByValidationResult(element, isCorrectRiverSpeed);
}

function validateTimeRepeatable() {
    var errorMessage = $(".unique-time-value-error-message");
    var timeValues = $(".time-input").map((idx, elem) => $(elem).val()).get();
    showOrHideErrorMessage(errorMessage, isUniqueArray(timeValues));
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

function isUniqueArray(array) {
    var testObject = {};
    var result = false;

    for (var i = 0; i < array.length; i++) {
        result = result || testObject.hasOwnProperty(array[i]);
        testObject[array[i]] = array[i];
    }

    return !result;
}