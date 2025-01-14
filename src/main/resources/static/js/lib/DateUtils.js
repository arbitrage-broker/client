function toLocalizingDateString(date, showTime) {
    if (resources.local === "fa") {
        return gregorianStringToShamsiString(date, showTime);
    } else {
        return toGregorianString(date, showTime);
    }
}

function toGregorianString(shamsiString, showTime) {
    if (isNullOrEmpty(shamsiString)) {
        // if (!isNullOrEmpty(showTime) && showTime) {
        //     return "0001-01-01 00:00:00";
        // }
        return null;
    }
    if (resources.local === "fa") {
        return shamsiStringTogregorianString(shamsiString, showTime);
    } else {
        var datePart = dateSpliter(shamsiString);
        var dateOnly = "{0}-{1}-{2}".format(datePart.year, datePart.month, datePart.day);
        if (!isNullOrEmpty(showTime) && showTime) {
            return "{0}T{1}:{2}:{3}".format(
                dateOnly,
                datePart.hour,
                datePart.minute,
                datePart.second);
        }
        return dateOnly;
    }
}

function shamsiStringTogregorianString(shamsiString, showTime) {
    if (isNullOrEmpty(shamsiString)) return null;
    shamsiString = persianDigitToEnglish(shamsiString);
    var datePart = dateSpliter(shamsiString);
    var gregorian = toGregorian(parseInt(datePart.year), parseInt(datePart.month), parseInt(datePart.day));
    var gregorianDate = "{0}-{1}-{2}".format(gregorian.gy, addZero(gregorian.gm), addZero(gregorian.gd));
    if (!isNullOrEmpty(showTime) && showTime) {
        return "{0}T{1}:{2}:{3}".format(
            gregorianDate,
            addZero(datePart.hour),
            addZero(datePart.minute),
            addZero(datePart.second));
    }
    return gregorianDate;
}

function addZero(value) {
    if (value < 10)
        return "0" + value;
    return value + "";
}

function gregorianStringToShamsiString(gregorianString, showTime) {
    if (isNullOrEmpty(gregorianString)) return null;
    var datePart = dateSpliter(gregorianString);
    var jalali = toJalaali(parseInt(datePart.year), parseInt(datePart.month), parseInt(datePart.day));
    var jalaliDate = "{0}/{1}/{2}".format(jalali.jy, jalali.jm, jalali.jd);
    if (!isNullOrEmpty(showTime) && showTime) {
        return "{0}T{1}:{2}:{3}".format(
            jalaliDate,
            datePart.hour,
            datePart.minute,
            datePart.second);
    }
    return jalaliDate;
}

function dateSpliter(dateString) {
    dateString = dateString.replace('T',' ').replace('Z','');
    if (dateString.indexOf(' ') > -1) {
        dateString = dateString.split(' ');
    } else if (dateString.indexOf('  ') > -1) {
        dateString = dateString.split('  ');
    }
    var spliter = '/';
    if (isArray(dateString)) {
        var dateOnly = dateString[0];
        var time = dateString[1];
        if (dateOnly.indexOf('-') > -1)
            spliter = '-';
        return {
            year: parseInt(dateOnly.split(spliter)[0]),
            month: parseInt(dateOnly.split(spliter)[1]),
            day: parseInt(dateOnly.split(spliter)[2]),
            hour: parseInt(time.split(':')[0]),
            minute: parseInt(time.split(':')[1]),
            second: parseInt(time.split(':')[2])
        }
    }
    if (dateString.indexOf('-') > -1)
        spliter = '-';
    return {
        year: dateString.split(spliter)[0],
        month: dateString.split(spliter)[1],
        day: dateString.split(spliter)[2],
        hour: 0,
        minute: 0,
        second: 0
    }
}
function toTimeString(inputDate) {
    const date = new Date(inputDate);
    if (isNaN(date)) {
        throw new Error('Invalid date');
    }
    // Define options for time and date formatting
    const optionsTime = { hour: 'numeric', minute: 'numeric', hour12: true };
    // Extract time (e.g., "8:02 PM")
    const timeString = date.toLocaleTimeString('en-US', optionsTime);

    return `${timeString}`;
}
function toDateTimeString(inputDate) {
    const date = new Date(inputDate);
    if (isNaN(date)) {
        throw new Error('Invalid date');
    }
    // Define options for time and date formatting
    const optionsTime = { hour: 'numeric', minute: 'numeric', hour12: true };
    const optionsDate = { day: 'numeric', month: 'short', year: 'numeric' };

    // Extract time (e.g., "8:02 PM")
    const timeString = date.toLocaleTimeString('en-US', optionsTime);

    // Extract date (e.g., "12 Feb 2014")
    const dateString = date.toLocaleDateString('en-GB', optionsDate).toUpperCase();

    // Combine and format (e.g., "8:02 PM 12 FEB 2014")
    return `${timeString} ${dateString}`;
}
function addDays(date, days) {
    var result = new Date(date);
    result.setDate(result.getDate() + days);
    return result;
}