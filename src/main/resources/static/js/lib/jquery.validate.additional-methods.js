/**
* Return true if the field value matches the given format RegExp
*
* @example $.validator.methods.pattern("AR1004",element,/^AR\d{4}$/)
* @result true
*
* @example $.validator.methods.pattern("BR1004",element,/^AR\d{4}$/)
* @result false
*
* @name $.validator.methods.pattern
* @type Boolean
* @cat Plugins/Validate/Methods
*/
$.validator.addMethod("pattern", function (value, element, param) {
    if (this.optional(element)) {
        return true;
    }
    if (typeof param === "string") {
        param = new RegExp("^(?:" + param + ")$");
    }
    return param.test(value);
}, $.validator.format(resources.invalidFormat));

$.validator.addMethod("decimalPlaces", function(value, element, params) {
    if (this.optional(element)) {
        return true;
    }
    if (value.indexOf(".") === -1) {
        return true;
    }
    var parts = value.split(".");
    return (parts[1].length <= params);
}, "Please enter a number with no more than {0} decimal places.");

$.validator.addMethod("validTronHash", function (value, element) {
    // Tron transaction hash regex: 64 hexadecimal characters
    const tronHashRegex = /^[0-9a-fA-F]{64}$/;
    return this.optional(element) || tronHashRegex.test(value);
}, resources.invalidTronHash);

$.validator.addMethod("validTronWallet", function (value, element) {
    // Tron wallet addresses must be 34 characters long, start with 'T', and only contain valid Base58 characters
    return this.optional(element) || /^[T][1-9A-HJ-NP-Za-km-z]{33}$/.test(value);
}, resources.invalidTronAddress);

// Accept a value from a file input based on a required mimetype
$.validator.addMethod("accept", function (value, element, param) {

    // Split mime on commas in case we have multiple types we can accept
    var typeParam = typeof param === "string" ? param.replace(/\s/g, "") : "image/*",
		optionalValue = this.optional(element),
		i, file, regex;

    // Element is optional
    if (optionalValue) {
        return optionalValue;
    }

    if ($(element).attr("type") === "file") {

        // Escape string to be used in the regex
        // see: http://stackoverflow.com/questions/3446170/escape-string-for-use-in-javascript-regex
        // Escape also "/*" as "/.*" as a wildcard
        typeParam = typeParam
				.replace(/[\-\[\]\/\{\}\(\)\+\?\.\\\^\$\|]/g, "\\$&")
				.replace(/,/g, "|")
				.replace(/\/\*/g, "/.*");

        // Check if the element has a FileList before checking each file
        if (element.files && element.files.length) {
            regex = new RegExp(".?(" + typeParam + ")$", "i");
            for (i = 0; i < element.files.length; i++) {
                file = element.files[i];

                // Grab the mimetype from the loaded file, verify it matches
                if (!file.type.match(regex)) {
                    return false;
                }
            }
        }
    }

    // Either return true because we've validated each file, or because the
    // browser does not support element.files and the FileList feature
    return true;
}, $.validator.format("please enter a valid mim type"));
/**
 * Validates currencies with any given symbols by @jameslouiz
 * Symbols can be optional or required. Symbols required by default
 *
 * Usage examples:
 *  currency: ["�", false] - Use false for soft currency validation
 *  currency: ["$", false]
 *  currency: ["RM", false] - also works with text based symbols such as "RM" - Malaysia Ringgit etc
 *
 *  <input class="currencyInput" name="currencyInput">
 *
 * Soft symbol checking
 *  currencyInput: {
 *     currency: ["$", false]
 *  }
 *
 * Strict symbol checking (default)
 *  currencyInput: {
 *     currency: "$"
 *     //OR
 *     currency: ["$", true]
 *  }
 *
 * Multiple Symbols
 *  currencyInput: {
 *     currency: "$,�,�"
 *  }
 */
$.validator.addMethod("currency", function (value, element, param) {
    var isParamString = typeof param === "string",
        symbol = isParamString ? param : param[0],
        soft = isParamString ? true : param[1],
        regex;

    symbol = symbol.replace(/,/g, "");
    symbol = soft ? symbol + "]" : symbol + "]?";
    regex = "^[" + symbol + "([1-9]{1}[0-9]{0,2}(\\,[0-9]{3})*(\\.[0-9]{0,2})?|[1-9]{1}[0-9]{0,}(\\.[0-9]{0,2})?|0(\\.[0-9]{0,2})?|(\\.[0-9]{1,2})?)$";
    regex = new RegExp(regex);
    return this.optional(element) || regex.test(value);

}, "Please specify a valid currency");
/*
 * partnumber:	{require_from_group: [1,".productinfo"]},
 * description: {require_from_group: [1,".productinfo"]}
 *
 * options[0]: number of fields that must be filled in the group
 * options[1]: CSS selector that defines the group of conditionally required fields
 */
$.validator.addMethod("require_from_group", function (value, element, param) {
    return $(param[1]).length >= param[0];
}, $.validator.format("Please select more than {0} {1}"));

$.validator.addMethod("date", function (value, element) {
    value = persianDigitToEnglish(value);
    return this.optional(element) || /[0-9]{4}\/(0[1-9]|1[0-2])\/(0[1-9]|[1-2][0-9]|3[0-1])/.test(value);
}, $.validator.format(resources.invalidFormat));

$.validator.addMethod("dateAndTime", function (value, element) {
    value = persianDigitToEnglish(value);
    return this.optional(element) || /[0-9]{4}\/(0[1-9]|1[0-2])\/(0[1-9]|[1-2][0-9]|3[0-1])  ([01]\d|2[0-3]):([0-5]\d):([0-5]\d)/.test(value);
}, $.validator.format(resources.invalidFormat));

$.validator.addMethod("mobile", function (value, element) {    
    return this.optional(element) || /^(?:0098|\\+98|0)[9][0-9]{9}$/.test(value);
}, $.validator.format(resources.invalidFormat));

$.validator.addMethod("postalCode", function (value, element) {    
    return this.optional(element) || /^(\d{10})$/.test(value);
}, $.validator.format(resources.invalidFormat));

$.validator.addMethod("exactLength", function (value, element, param) {
    return this.optional(element) || value.length == param;
}, $.validator.format("Please enter exactly {0} {1}"));

$.validator.addMethod("greaterThan",
    function (value, element, param) {
        var $otherElement = $(param);
        return parseInt(value, 10) > parseInt($otherElement.val(), 10);
    });
$.validator.addMethod("lessThan",
    function (value, element, param) {
        var $otherElement = $(param);
        return parseInt(value, 10) < parseInt($otherElement.val(), 10);
    });