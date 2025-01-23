let hcaptchaInstances = {};
let consent = false;
if (!String.prototype.format) {
    String.prototype.format = function () {
        var args = arguments;
        return this.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] != 'undefined'
                ? args[number]
                : match
                ;
        });
    };
}

function onLoadCaptcha() {
    // Render hCaptcha widgets and store their widget IDs
    hcaptchaInstances['login'] = hcaptcha.render('login-captcha', {
        sitekey: 'aa988f36-059e-4026-9d34-f68c0f4c7300'
    });

    hcaptchaInstances['register'] = hcaptcha.render('register-captcha', {
        sitekey: 'aa988f36-059e-4026-9d34-f68c0f4c7300'
    });

    hcaptchaInstances['reset'] = hcaptcha.render('reset-captcha', {
        sitekey: 'aa988f36-059e-4026-9d34-f68c0f4c7300'
    });

    hcaptchaInstances['otp'] = hcaptcha.render('otp-captcha', {
        sitekey: 'aa988f36-059e-4026-9d34-f68c0f4c7300'
    });
}

function consentUser(){
    consent=true;
    $('#myModal').modal('hide');
}
$(function () {
    $(".toggle-password").click(function() {
        $(this).toggleClass("fa-eye fa-eye-slash");
        var input = $(this).prev('input');
        if (input.attr("type") == "password") {
            input.attr("type", "text");
        } else {
            input.attr("type", "password");
        }
    });
    $('#login-form').validate({
        rules: {
            login: "required",
            password: "required"
        },
        messages: {
            login: resources.pleaseEnter.format(resources.usernameOrEmail),
            password: resources.pleaseEnter.format(resources.password)
        },
        highlight: function (element) {
            $(element).addClass('is-invalid').closest('div').addClass('bad');
        },
        unhighlight: function (element) {
            $(element).removeClass('is-invalid').closest('div').removeClass('bad');
        },
        errorElement: 'span',
        errorClass: 'invalid-feedback',
        errorPlacement: function (error, element) {
            error.insertAfter(element);
        },
        submitHandler: function (form) {
            if (!hcaptcha.getResponse(hcaptchaInstances['login'])) {
                $('.error-content').empty().html(`<div class='alert alert-danger'>
                                <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;
                                </button>
                                <p style="color: white;text-shadow: none;font-size: 1rem;margin-bottom: 0px;">Please solve the captcha challenge.</p>
                            </div>`);
                return; // Stop the form submission
            }
            $(form).find("button:submit").attr('disabled','disabled');
            form.submit();
        }
    });
    $('#send_otp-form').validate({
        rules: {
            email: {
                required: true,
                email: true
            }
        },
        messages: {
            email: {
                required: resources.pleaseEnter.format(resources.email),
                email: resources.invalidFormat.format(resources.email)
            }
        },
        highlight: function (element) {
            $(element).addClass('is-invalid').closest('div').addClass('bad');
        },
        unhighlight: function (element) {
            $(element).removeClass('is-invalid').closest('div').removeClass('bad');
        },
        errorElement: 'span',
        errorClass: 'invalid-feedback',
        errorPlacement: function (error, element) {
            error.insertAfter(element);
        },
        submitHandler: function (form) {
            if (!hcaptcha.getResponse(hcaptchaInstances['otp'])) {
                $('.error-content').empty().html(`<div class='alert alert-danger'>
                                <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;
                                </button>
                                <p style="color: white;text-shadow: none;font-size: 1rem;margin-bottom: 0px;">Please solve the captcha challenge.</p>
                            </div>`);
                return; // Stop the form submission
            }
            $(form).find("button:submit").attr('disabled','disabled');
            form.submit();
        }
    });

    $('#register-form').validate({
        ignore: '.skip',
        rules: {
            userName: "required",
            email: {
                required: true,
                email: true
            },
            firstName: "required",
            lastName: "required",
            countrySelect2: "required",
            password: {
                required: true,
                minlength: 5
            },
            repeatPassword: {
                required: true,
                minlength: 5,
                equalTo: "#password"
            },
        },
        messages: {
            userName: resources.pleaseEnter.format(resources.userName),
            email: {
                required: resources.pleaseEnter.format(resources.email),
                email: resources.invalidFormat.format(resources.email)
            },
            firstName: resources.pleaseEnter.format(resources.name),
            lastName: resources.pleaseEnter.format(resources.lastName),
            countrySelect2: resources.pleaseSelect.format(resources.country),
            password: {
                required: resources.pleaseEnter.format(resources.password),
                minlength: resources.mustBeMoreThan.format(resources.password, 5, resources.character)
            },
            repeatPassword: {
                required: resources.pleaseEnter.format(resources.repeatPassword),
                minlength: resources.mustBeMoreThan.format(resources.password, 5, resources.character),
                equalTo: resources.confirmPasswordDoesNotMach
            }
        },
        highlight: function (element) {
            $(element).addClass('is-invalid').closest('div').addClass('bad');
        },
        unhighlight: function (element) {
            $(element).removeClass('is-invalid').closest('div').removeClass('bad');
        },
        errorElement: 'span',
        errorClass: 'invalid-feedback',
        errorPlacement: function (error, element) {
            if (element.hasClass('select2-hidden-accessible')) {
                element.parent().append(error);
            } else {
                error.insertAfter(element);
            }
        },
        submitHandler: function (form) {
            if (!hcaptcha.getResponse(hcaptchaInstances['register'])) {
                $('.error-content').empty().html(`<div class='alert alert-danger'>
                                <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;
                                </button>
                                <p style="color: white;text-shadow: none;font-size: 1rem;margin-bottom: 0px;">Please solve the captcha challenge.</p>
                            </div>`);
                return; // Stop the form submission
            }
            if(!consent) {
                $("#myModal").modal('show');
                return;
            }
            $(form).find("button:submit").attr('disabled','disabled');
            $.ajax({
                type: "POST",
                url: "/api/v1/user/register",
                dataType: "json",
                contentType: "application/json;charset=utf-8",
                data: JSON.stringify({country:{id: $("#countrySelect2").val()}, userName:$("#userName").val(),email:$("#email").val(), password:$("#password").val(),firstName: $("#firstName").val(),lastName: $("#lastName").val(),referralCode: $("#referralCode").val() }),
                success: function (data) {
                    $(form).find("button:submit").removeAttr('disabled');
                    if (data.error == null) {
                        clearAll();
                        new PNotify({
                            title: 'Success',
                            text: 'your information successfully registered.',
                            type: 'success',
                            styling: 'bootstrap3'
                        });
                        window.location.href = "#signin";
                    } else {
                        new PNotify({
                            title: 'Oh No!',
                            text: data.error,
                            type: 'error',
                            hide: false,
                            styling: 'bootstrap3'
                        });
                    }
                },
                error: function (header, status, error) {
                    $(form).find("button:submit").removeAttr('disabled');
                    if(isNullOrEmpty(get(() => header.responseJSON)))
                        new PNotify({
                            title: 'Oh No!',
                            text: 'ajax answer post returned error: ' + error.responseText,
                            type: 'error',
                            hide: false,
                            styling: 'bootstrap3'
                        });
                    else new PNotify({
                        title: 'Oh No!',
                        text: header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message,
                        type: 'error',
                        hide: false,
                        styling: 'bootstrap3'
                    });
                }
            });
        }
    });
});

$('#reset_pass-form').validate({
    ignore: '.skip',
    rules: {
        login: "required",
        otp: "required",
        newPassword: {
            required: true,
            minlength: 5
        },
        repeatNewPassword: {
            required: true,
            minlength: 5,
            equalTo: "#newPassword"
        },
    },
    messages: {
        login: resources.pleaseEnter.format(resources.usernameOrEmail),
        otp: resources.pleaseEnter.format(resources.oneTimePassword),
        newPassword: {
            required: resources.pleaseEnter.format(resources.password),
            minlength: resources.mustBeMoreThan.format(resources.password, 5, resources.character)
        },
        repeatNewPassword: {
            required: resources.pleaseEnter.format(resources.repeatPassword),
            minlength: resources.mustBeMoreThan.format(resources.password, 5, resources.character),
            equalTo: resources.confirmPasswordDoesNotMach
        }
    },
    highlight: function (element) {
        $(element).addClass('is-invalid').closest('div').addClass('bad');
    },
    unhighlight: function (element) {
        $(element).removeClass('is-invalid').closest('div').removeClass('bad');
    },
    errorElement: 'span',
    errorClass: 'invalid-feedback',
    errorPlacement: function (error, element) {
        if (element.hasClass('select2-hidden-accessible')) {
            element.parent().append(error);
        } else {
            error.insertAfter(element);
        }
    },
    submitHandler: function (form) {
        if (!hcaptcha.getResponse(hcaptchaInstances['reset'])) {
            $('.error-content').empty().html(`<div class='alert alert-danger'>
                                <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;
                                </button>
                                <p style="color: white;text-shadow: none;font-size: 1rem;margin-bottom: 0px;">Please solve the captcha challenge.</p>
                            </div>`);
            return; // Stop the form submission
        }
        $(form).find("button:submit").attr('disabled','disabled');
        form.submit();
        // $.ajax({
        //     type: "POST",
        //     url: "/api/v1/user/reset-pass",
        //     dataType: "json",
        //     contentType: "application/json;charset=utf-8",
        //     data: JSON.stringify({country:{id: $("#countrySelect2").val()}, userName:$("#userName").val(),email:$("#email").val(), password:$("#password").val(),firstName: $("#firstName").val(),lastName: $("#lastName").val(),referralCode: $("#referralCode").val() }),
        //     success: function (data) {
        //         if (data.error == null) {
        //             clearAll();
        //             new PNotify({
        //                 title: 'Success',
        //                 text: 'your password successfully changed.',
        //                 type: 'success',
        //                 styling: 'bootstrap3'
        //             });
        //             window.location.href = "#signin";
        //         } else {
        //             new PNotify({
        //                 title: 'Oh No!',
        //                 text: data.error,
        //                 type: 'error',
        //                 hide: false,
        //                 styling: 'bootstrap3'
        //             });
        //         }
        //     },
        //     error: function (header, status, error) {
        //         if(isNullOrEmpty(get(() => header.responseJSON)))
        //             new PNotify({
        //                 title: 'Oh No!',
        //                 text: 'ajax answer post returned error: ' + error.responseText,
        //                 type: 'error',
        //                 hide: false,
        //                 styling: 'bootstrap3'
        //             });
        //         else new PNotify({
        //             title: 'Oh No!',
        //             text: header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message,
        //             type: 'error',
        //             hide: false,
        //             styling: 'bootstrap3'
        //         });
        //     }
        // });
    }
});

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
function clearAll(){
    $('form').each(function (index, frm) {
        frm.reset();
    });
    $('form input:hidden:not(#myModal form input:hidden)').val('');
    $("select.select2-hidden-accessible").val('').trigger('change');
}
function get(lambdaExpr, defaultValue) {// () => supplier expr
    try {
        return lambdaExpr();
    } catch (e) {
        return isNullOrEmpty(defaultValue) ? '' : defaultValue;
    }
}
function validateInput(input) {
    input.value = input.value.replace(/[^a-zA-Z0-9]/g, '');
}

window.onload = onLoadCaptcha;