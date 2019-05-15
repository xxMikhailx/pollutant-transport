var isCorrect = true;

function validateData() {
    isCorrect = true;
    $(".concentration-input").map((idx, elem) => validateInput($(elem)));
    $(".time-input").map((idx, elem) => validateInput($(elem)));
    validateInput($(".river-speed"));
    validateTimeRepeatable();
    if (!isCorrect) {
        $(".incorrect-input-values-error-message").removeClass("d-none");
    }
    return isCorrect;
}

function validateConcentrationData(e) {
    validateInput($(this));
}

function validateTimeData(e) {
    validateInput($(this));
}

function validateRiverSpeedData(e) {
    validateInput($(this));
}

function validateInput(input) {
    var regular = /^[0-9]{1,10}(\.[0-9]{1,10})?$/;
    var result = regular.test(input.val());
    if (result) {
        input.addClass("is-valid");
        input.removeClass("is-invalid");
    } else {
        isCorrect = false;
        input.addClass("is-invalid");
        input.removeClass("is-valid");
    }
}

function validateTimeRepeatable() {
    var errorMessage = $(".unique-time-value-error-message");
    var timeValues = $(".time-input").map((idx, elem) => $(elem).val()).get();

    if (!isUniqueArray(timeValues)) {
        errorMessage.removeClass("d-none");
        isCorrect = false;
    } else if(!errorMessage.hasClass("d-none")){
        errorMessage.addClass("d-none");
    }
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