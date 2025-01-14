(function ($) {

    let o = $({});

    $.subscribe = function () {
        o.on.apply(o, arguments);
    };

    $.unsubscribe = function () {
        o.off.apply(o, arguments);
    };

    $.publish = function () {
        o.trigger.apply(o, arguments);
    };

}(jQuery));

$.postJSON = function(url, data, success, error, dataType) {
    return $.ajax({
        url: url,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: success,
        error: error,
        dataType: dataType
    });
};

var currentUser = JSON.parse($("#currentUser").val());
let userOccupied = 0;
userProfileOccupied();

const referralLink = window.location.origin + `/login?referralCode=${currentUser.uid}#signup`;
$(".referral-code").attr("href", referralLink).html(`${currentUser.uid}`);
$(".referral-code-menu").attr("link", referralLink);

if(!isNullOrEmpty(currentUser.profileImageUrl))
    $(".avatar-image-url").attr('src', currentUser.profileImageUrl);

$(function () {
    $('.date input').keydown(function (event) {
        event.preventDefault();
    });
});

if (!String.prototype.format) {
    String.prototype.format = function () {
        let args = arguments;
        return this.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] != 'undefined'
                ? args[number]
                : match
                ;
        });
    };
}

function loadPages(page) {
    window.location.href =  page;
}

function loadScriptRunTime(url, callback) {
    // Adding the script tag to the head as suggested before
    let head = document.getElementsByTagName('head')[0];
    let script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;

    // Then bind the event to the callback function.
    // There are several events for cross browser compatibility.
    script.onreadystatechange = callback;
    script.onload = callback;

    // Fire the loading
    head.appendChild(script);
}


function changeUrlParameterAndReload(param, value) {
    changeUrlParameter(param, value);
    location.reload();
}

function changeUrlParameter(param, value) {
    let currentURL = window.location.href + '&';
    let change = new RegExp('(' + param + ')=(.*)&', 'g');
    let newURL = currentURL.replace(change, '$1=' + value + '&');

    if (getURLParameter(param) !== "") {
        try {
            window.history.replaceState('', '', newURL.slice(0, -1));
        } catch (e) {
            console.log(e);
        }
    } else {
        let currURL = window.location.href;
        if (currURL.indexOf("?") !== -1) {
            window.history.replaceState('', '', currentURL.slice(0, -1) + '&' + param + '=' + value);
        } else {
            window.history.replaceState('', '', currentURL.slice(0, -1) + '?' + param + '=' + value);
        }
    }
}

function getURLParameter(name) {
    let regexS = "[\\?&]" + name + "=([^&#]*)",
        regex = new RegExp(regexS),
        results = regex.exec(window.location.search);
    if (results == null) {
        return "";
    } else {
        return decodeURIComponent(results[1].replace(/\+/g, " "));
    }
}

function removeUrlparameter(parameter) {
    let url = document.location.href;
    let urlparts = url.split('?');

    if (urlparts.length >= 2) {
        let urlBase = urlparts.shift();
        let queryString = urlparts.join("?");

        let prefix = encodeURIComponent(parameter) + '=';
        let pars = queryString.split(/[&;]/g);
        for (let i = pars.length; i-- > 0;)
            if (pars[i].lastIndexOf(prefix, 0) !== -1)
                pars.splice(i, 1);
        url = urlBase + pars.join('&');
        window.history.pushState('', document.title, url); // added this line to push the new url directly to url bar .

    }
    return url;
}

function show_info(mes) {
    // $("#myAlert .alert-content").html(mes);
    // $("#myAlert").removeClass("alert-danger alert-warning alert-success").addClass("alert-info").show().delay(3000).fadeOut();
    new PNotify({
        title: 'New Thing',
        text: mes,
        type: 'info',
        styling: 'bootstrap3',
        nonblock: {nonblock: !0},
    });
}

function show_success(mes) {
    // $("#myAlert .alert-content").html(mes);
    // $("#myAlert").removeClass("alert-danger alert-warning alert-info").addClass("alert-success").show().delay(5000).fadeOut();
    new PNotify({
        title: 'Success',
        text: mes,
        type: 'success',
        styling: 'bootstrap3',
        nonblock: {nonblock: !0},
    });
}

function show_warning(mes) {
    // $("#myAlert .alert-content").html(mes);
    // $("#myAlert").removeClass("alert-success alert-danger alert-info").addClass("alert-warning").show().delay(5000).fadeOut();
    new PNotify({
        title: 'Notice',
        text: mes,
        styling: 'bootstrap3'
    });
}

function show_error(mes) {
    // $("#myAlert .alert-content").html(mes);
    // $("#myAlert").removeClass("alert-success alert-warning alert-info").addClass("alert-danger").show();
    new PNotify({
        title: 'Oh No!',
        text: mes,
        type: 'error',
        hide: false,
        styling: 'bootstrap3'
    });
}

function get(lambdaExpr, defaultValue) {// () => supplier expr
    try {
        return lambdaExpr();
    } catch (e) {
        return isNullOrEmpty(defaultValue) ? '' : defaultValue;
    }
}

function arrayToJsonArray(arr) {
    try {
        return arr.reduce((json, value, key) => {
            json[key] = {id: value};
            return json;
        }, []);
    } catch (e) {
        return [];
    }
}

function isNullOrEmpty(param) {
    if (param == null)
        return true;
    if (param == "null")
        return true;
    if (param == undefined)
        return true;
    if (param == "undefined")
        return true;
    if ((param + "").trim() == "")
        return true;
    return false;
}

function addPeriod(nStr) {
    nStr += '';
    x = nStr.split('.');
    x1 = x[0];
    x2 = x.length > 1 ? '.' + x[1] : '';
    let rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
        x1 = x1.replace(rgx, '$1' + ',' + '$2');
    }
    return x1 + x2;
}

function separetorPrice(id) {
    $(id).each(function () {
        val = $.trim($(this).html());
        if ($.isNumeric(val)) {
            res = addPeriod(val);
            $(this).html(res);
        }
    });
}


function getTagsLabel(element) {
    let labels = [];
    $.each(element.tagsinput('items'), function (i, item) {
        labels.push(item.label);
    });
    return labels;
}

function getTagsFile(element) {
    let files = [];
    $.each(element.tagsinput('items'), function (i, item) {
        if (item.id !== item.label)
            files.push(item.id);
    });
    return files;
}

function getEmptyTagsLabel(element) {
    let labels = [];
    $.each(element.tagsinput('items'), function (i, item) {
        if (item.id === item.label)
            labels.push(item.label);
    });
    return labels;
}

function persianDigitToEnglish(value) {
    return value.replace(/۰/g, "0").replace(/۱/g, "1").replace(/۲/g, "2").replace(/۳/g, "3").replace(/۴/g, "4").replace(/۵/g, "5").replace(/۶/g, "6").replace(/۷/g, "7").replace(/۸/g, "8").replace(/۹/g, "9");
}

function blockUiOptions() {
    return {
        message: '<i class="fa fa-refresh fa-spin fa-2x"></i><br/>' + resources.processing,
        fadeIn: 1000,
        //timeout: 1000,
        overlayCSS: {
            backgroundColor: '#FFF',
            opacity: 0.8,
            cursor: 'wait'
        },
        css: {
            border: 0,
            padding: 0,
            color: '#333',
            backgroundColor: 'transparent'
        },
        onBlock: function () {
            //alert('Page is now blocked, FadeIn complete.');
        }
    }
}

function datePickerOptions() {
    return {
        Placement: 'bottom',
        EnglishNumber: true,
        Trigger: 'click',
        EnableTimePicker: false,
        DisableBeforeToday: false,
        TargetSelector: '.date input',
        GroupId: '',
        ToDate: false,
        FromDate: false,
        Disabled: false,
        Format: 'yyyy/MM/dd',
        GregorianStartDayIndex: 0,
        IsGregorian: false
    }
}

function validationOptions(rules, messages, callback) {
    return {
        ignore: '.skip',
        rules: rules,
        messages: messages,
        highlight: function (element, errorClass, validClass) {
            $(element).addClass('is-invalid').closest('.col-xs-12').addClass('bad');
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).removeClass('is-invalid').closest('.col-xs-12').removeClass('bad');
        },
        errorElement: 'span',
        errorClass: 'invalid-feedback',
        errorPlacement: function (error, element) {
            if (element.is(":radio") || element.is(":checkbox")) {
                error.appendTo(element.parent().parent());
            } else if (element.parent('.input-group').length) {
                error.insertAfter(element.closest('.input-group'));
            } else {
                error.insertAfter(element);
            }
        },
        submitHandler: function (form) {
            if (typeof callback === 'function') {
                callback();
            }
        }
    }
}

function tableOptions() {
    return {
        "responsive": true,
        "processing": true,
        "searching": false,
        "sort": true,
        "lengthMenu": [[10, 25, 50, 100, 1000, -1], [10, 25, 50, 100, 1000, resources.all]],
        "createdRow": function (row, data, index) {
            let fn = window["createdRow"];
            if (typeof fn === 'function') {
                fn(row, data, index);
            }
        },
        language: {
            processing: resources.processing + "&nbsp;<i class='fa fa-refresh fa-spin'/>",
            search: resources.search,
            lengthMenu: resources.showing + " _MENU_ " + resources.record,
            info: resources.showing + " _START_ " + resources.to + " _END_ " + resources.of + " _TOTAL_ " + resources.entries,
            infoEmpty: resources.showing + " 0 " + resources.to + " 0 " + resources.of + " 0 " + resources.entries,
            infoFiltered: "(" + resources.filtered + " " + resources.from + " _MAX_ " + resources.total + " " + resources.entries + ")",
            infoPostFix: "",
            loadingRecords: resources.loading + "&nbsp;<i class='fa fa-refresh fa-spin'/>",
            zeroRecords: resources.nothingFound,
            emptyTable: resources.nothingFound,
            paginate: {
                first: resources.first,
                previous: resources.previous,
                next: resources.next,
                last: resources.last
            },
            aria: {
                sortAscending: ": " + resources.ascending,
                sortDescending: ": " + resources.descending
            }
        }
    };
}
function initAjaxTable(selector, columns, url, filterFunction) {
    let opts = tableOptions();
    let customTableOptions_function = window["customTableOptions"];
    if (typeof customTableOptions_function === 'function') {
        opts = customTableOptions_function();
    }
    opts.serverSide = true;
    opts.columns = columns;
    opts.ajax = {
        'type': 'GET',
        'url': url,
        'data': function (data) {
            let requestParam = {
                "page": data.start / data.length,
                "size": data.length,
                "sort": data.columns[data.order[0].column].data + ',' + data.order[0].dir
            };
            let fn = window[filterFunction];
            if (typeof fn === 'function') {
                return jsonToUrlSearchParams(fn()) + '&' + jsonToUrlSearchParams(requestParam);
            }
            return requestParam;
        },
        "dataSrc": function(json) {
            json.recordsTotal = json.totalElements;
            json.recordsFiltered = json.totalElements;
            return json.content;
        },
        'datatype': 'json',
        'error': function (xhr) {
            if(isNullOrEmpty(get(()=>xhr.responseJSON)))
                show_error('ajax answer GET returned error: ' + xhr.responseText);
            else show_error(xhr.responseJSON.error + ' (' + xhr.responseJSON.status + ') <br>' + xhr.responseJSON.message);
        }
    };
    return $(selector).DataTable(opts);
}

function initTable(selector, columns, data) {
    let opts = tableOptions();
    opts.columns = columns;
    opts.data = data;
    opts.searching = true;
    return $(selector).DataTable(opts);
}

function loadHtmlFileToElement(fileName, params, selector, callBack) {
    $.blockUI(blockUiOptions());
    $.get("/get-html-file/" + fileName, {params: JSON.stringify(params)},
        function (data) {
            $.unblockUI();
            if (typeof callBack === 'function') {
                $(selector).html(data).css({'overflow': 'auto'}).promise().done(callBack);
            } else {
                $(selector).html(data).css({'overflow': 'auto'});
            }
        });
}

function showModal(title, callback) {
    $('#myModal .modal-title').html(title);
    if (typeof callback === 'function') {
        $('#myModal').on('shown.bs.modal', function () {
            callback();
            $(this).off('shown.bs.modal');
        }).modal('show');
    } else {
        $('#myModal').modal('show');
    }
}

function goToByScroll(selector) {
    $('html, body').animate({
        scrollTop: $(selector).offset().top
    }, 2000);
}

function isArray(value) {
    return value && typeof value === 'object' && value.constructor === Array;
}

function showUserAccount() {
    $('#userAccount span').text(resources.hello.format(currentUser.firstName + ' ' + currentUser.lastName));
    loadHtmlFileToElement("UserAccount", null, "#myModal .modal-body", function () {
        showModal(resources.userDetail, initModal);
    });
}

function setSwitchery(selector, checked) {
    let element = $(selector);
    if ($(element).is(':checked') != checked) {
        $(element).trigger("click")
    }
}

function flattenObject(obj, prefix = '') {
    const result = {};
    for (const key in obj) {
        if (obj.hasOwnProperty(key)) {
            const value = obj[key];
            const newKey = prefix ? `${prefix}.${key}` : key;
            if (value && typeof value === 'object' && !Array.isArray(value)) {
                Object.assign(result, flattenObject(value, newKey));
            } else if (!isNullOrEmpty(value)) {
                // Only include non-null values
                result[newKey] = value;
            }
        }
    }
    return result;
}
function jsonToUrlSearchParams(json) {
    const flattened = flattenObject(json);
    const params = new URLSearchParams();
    for (const key in flattened) {
        if (flattened.hasOwnProperty(key)) {
            params.append(key, flattened[key]);
        }
    }
    return params.toString();
}

function copyLink(id, text) {
    let link = $(id).attr('link');
    if(isNullOrEmpty(link))
        link = $(id).attr('href');
    copyValue(link, text);
}
function copyValue(value, name) {
    async () => {
        if (navigator.share) {
            try {
                await navigator.share({
                    title: 'Check this out!',
                    text: `Here’s ${name} to share.`,
                    url: window.location.origin
                });
                console.log('Successfully copied');
                new PNotify({
                    title: 'Successfully copied!',
                    text: `${name} has successfully copied to your clip board!`,
                    type: 'info',
                    styling: 'bootstrap3',
                    nonblock: {nonblock: !0},
                });
            } catch (error) {
                console.error('Error sharing:', error);
            }
        } else {
            alert('Web Share API not supported on this browser.');
        }
    }
    if (navigator.clipboard && navigator.clipboard.writeText) {
        // Use clipboard API if available
        navigator.clipboard.writeText(value).then(() => {
            // Show the "Copied!" message
            $('.copiedMessage').addClass('visible');

            // Hide the message after 2 seconds
            setTimeout(() => {
                $('.copiedMessage').removeClass('visible');
            }, 2000);

            new PNotify({
                title: 'Successfully copied!',
                text: `${name} has successfully copied to your clip board!`,
                type: 'info',
                styling: 'bootstrap3',
                nonblock: {nonblock: !0},
            });
        }).catch((err) => {
            console.error("Failed to copy: ", err);
        });
    } else {
        // Fallback for older browsers
        const tempInput = document.createElement("input");
        tempInput.value = value;
        document.body.appendChild(tempInput);
        tempInput.select();
        try {
            document.execCommand("copy");
            $('.copiedMessage').addClass('visible');
            setTimeout(() => {
                $('.copiedMessage').removeClass('visible');
            }, 2000);
        } catch (err) {
            console.error("Fallback copy failed: ", err);
        }
        document.body.removeChild(tempInput);
    }
}

function userProfileOccupied(){
    if(!isNullOrEmpty(currentUser.firstName)) {
        userOccupied += 5;
        $("#fullName-occupied").text("Yes").removeClass('text-red').addClass('text-green');
    }
    if(!isNullOrEmpty(currentUser.lastName))
        userOccupied+=5;
    if(!isNullOrEmpty(currentUser.email)) {
        userOccupied += 10;
        $("#email-occupied").removeClass('text-red').addClass('text-green').text("Yes");
    }
    if(currentUser.emailVerified) {
        userOccupied += 10;
        $("#email-verified").removeClass('text-red').addClass('text-green').text("Yes");
    }
    if(!isNullOrEmpty(currentUser.profileImageUrl)) {
        userOccupied += 10;
        $("#profileImageUrl-occupied").removeClass('text-red').addClass('text-green').text("Yes");
    }
    if(!isNullOrEmpty(currentUser.country)) {
        userOccupied += 10;
        $("#country-occupied").removeClass('text-red').addClass('text-green').text("Yes");
    }
    if(!isNullOrEmpty(currentUser.walletAddress)) {
        userOccupied += 10;
        $("#walletAddress-occupied").removeClass('text-red').addClass('text-green').text("Yes");
    }
    $.get(`api/v1/wallet/exists?userId=${currentUser.id}&transactionType=DEPOSIT`,function(anyDeposit){
        if(anyDeposit) {
            userOccupied += 10;
            $("#anyDeposit-occupied").removeClass('text-red').addClass('text-green').text("Yes");
        }
        $.get(`api/v1/arbitrage/exists?userId=${currentUser.id}`,function(anyArbitrage){
            if(anyArbitrage) {
                userOccupied += 10;
                $("#anyArbitrage-occupied").removeClass('text-red').addClass('text-green').text("Yes");
            }
            $.get(`api/v1/user/${currentUser.id}`,function(userData){
                if(userData.childCount > 0) {
                    userOccupied += 20;
                    $("#anyReferral-occupied").removeClass('text-red').addClass('text-green').text("Yes");
                }
                $(".userOccupied").text(`${userOccupied}%`);
                if(userOccupied > 50 && userOccupied < 100)
                    $(".userOccupied").removeClass("bg-red").addClass("bg-orange");
                if(userOccupied == 100)
                    $(".userOccupied").removeClass("bg-red").addClass("bg-green");
            });
        });
    });
}
function validateInput(input) {
    input.value = input.value.replace(/[^a-zA-Z0-9]/g, '');
}