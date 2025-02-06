ajaxUrl = "/api/v1/user";
var imageProfileUrl = '';
var myDropzone;
var referralDataTable1;
var referralDataTable2;
let isSubmitting = false;
rules = {
    walletAddress: {
        validTronWallet: true
    }
};
messages = {
    walletAddress: {
        validTronWallet: resources.invalidFormat.format(resources.wallet)
    }
};
$(function () {
    $("#full-name").text(`${currentUser.selectTitle}`);
    $("#country-label").html(`<strong><i class="fa fa-map-marker"></i> Country:</strong> ${currentUser.country.name}`);
    $("#email-label").html(`<strong><i class="fa fa-envelope"></i> Email:</strong> ${currentUser.email}`);
    $("#user-name").html(`<strong><i class="fa fa-id-card"></i> UserName:</strong> ${currentUser.userName}`);
    $("#wallet-label").html(`${isNullOrEmpty(currentUser.walletAddress)? '<strong><i class="fa fa-wallet"></i> Wallet:</strong> ---': '<strong><i class="fa fa-wallet"></i> Wallet:</strong> ' + currentUser.walletAddress}`);
    const referralLink = window.location.origin + `/login?referralCode=${currentUser.uid}#signup`;
    $(".referral-code").attr("href", referralLink);
    $(".referral-code").html(`${currentUser.uid}`);

    $(".toggle-password").click(function() {
        $(this).toggleClass("fa-eye fa-eye-slash");
        var input = $(this).prev('input');
        if (input.attr("type") == "password") {
            input.attr("type", "text");
        } else {
            input.attr("type", "password");
        }
    });
    $('#modal-body').validate({
        messages: {
            password: {
                minlength: resources.mustBeMoreThan.format(resources.password, 5, resources.character)
            },
            email: {
                email: resources.invalidFormat.format(resources.email)
            },
            repeatPassword: {
                minlength: resources.mustBeMoreThan.format(resources.password, 5, resources.character),
                equalTo: resources.confirmPasswordDoesNotMach
            }
        },
        rules: {
            password: {
                minlength: 5
            },
            email: {
                email: true
            },
            repeatPassword: {
                minlength: 5,
                equalTo: "#password"
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
        }
    });
    loadLabelByEntity(currentUser.id);
});
function loadTable() {
    referralDataTable1 = initAjaxTable('#referral-table-1', [{
        data: 'profileImageUrl',
        searchable: false,
        sortable: false,
        render: function (data) { return `<img src="${isNullOrEmpty(data) ? '/images/find_user.png' : data}" style="width: 35px;"></img>` }
    },{
        data: 'firstName',
        render: function (data, type, row) { return data + " " + row.lastName }
    },{
        data: 'userName'
    },{
        data: 'email'
    },{
        data: 'uid'
    },{
        data: 'country.name'
    },{
        data: 'deposit'
    },{
        data: 'withdrawal'
    },{
        data: 'reward'
    },{
        data: 'modifiedDate',
        render: function (data) { return toLocalizingDateString(data, true) }
    }], "/api/v1/user", "loadSearchReferralEntity1ByInput");
    $.subscribe('reloadReferral1Table', referralDataTable1.ajax.reload);

    referralDataTable2 = initAjaxTable('#referral-table-2', [{
        data: 'profileImageUrl',
        searchable: false,
        sortable: false,
        render: function (data) { return `<img src="${isNullOrEmpty(data) ? '/images/find_user.png' : data}" style="width: 35px;"></img>` }
    },{
        data: 'firstName',
        render: function (data, type, row) { return data + " " + row.lastName }
    },{
        data: 'userName'
    },{
        data: 'email'
    },{
        data: 'uid'
    },{
        data: 'country.name'
    },{
        data: 'deposit'
    },{
        data: 'withdrawal'
    },{
        data: 'reward'
    },{
        data: 'modifiedDate',
        render: function (data) { return toLocalizingDateString(data, true) }
    }], "/api/v1/user", "loadSearchReferralEntity2ByInput");
    $.subscribe('reloadReferral2Table', referralDataTable2.ajax.reload);
}
function customTableOptions(){
    let tbl_option = tableOptions();
    tbl_option.order = [[9, 'desc']];
    return tbl_option;
}
function loadInputByEntity(id) {
    $.getJSON(`${ajaxUrl}/${id}`,function(entity) {
        $("#countrySelect2").html("<option value='" + get(() => entity.country.id) + "' selected>" + get(() => entity.country.name) + "</option>").trigger('change');
        $("#countrySelect2").val(get(() => entity.country.id)).trigger('change');
        $("#email").val(entity.email);
        $("#name").val(entity.firstName);
        $("#lastName").val(entity.lastName);
        $("#walletAddress").val(entity.walletAddress);
    });
}
function loadLabelByEntity(id) {
    $.getJSON(`${ajaxUrl}/${id}`, function(entity) {
        if(!isNullOrEmpty(entity.profileImageUrl))
            $("#profile-image-url").attr('src',entity.profileImageUrl);

        $("#full-name").text(`${entity.selectTitle}`);
        $("#country-label").html(`<strong><i class="fa fa-map-marker"></i> Country:</strong> ${entity.country.name}`);
        $("#email-label").html(`<strong><i class="fa fa-envelope"></i> Email:</strong> ${entity.email}`);
        $("#user-name").html(`<strong><i class="fa fa-id-card"></i> UserName:</strong> ${entity.userName}`);
        $("#wallet-label").html(`${isNullOrEmpty(entity.walletAddress)? '<strong><i class="fa fa-wallet"></i> Wallet:</strong> ---': '<strong><i class="fa fa-wallet"></i> Wallet:</strong> ' + entity.walletAddress}`);
        const referralLink = window.location.origin + `/login?referralCode=${entity.uid}#signup`;
        $(".referral-code").attr("href", referralLink);
        $(".referral-code").html(`${entity.uid}`);
    });
}
function loadSaveEntityByInput() {
    let model = {
        id: currentUser.id,
        userName: currentUser.userName,
        firstName: $("#name").val(),
        lastName: $("#lastName").val(),
        version: currentUser.version,
        email: $("#email").val(),
        country: {id: $("#countrySelect2").val()},
        walletAddress: $("#walletAddress").val(),
        profileImageUrl: imageProfileUrl,
        password: $("#password").val()
    };
    return model;
}
function loadSearchReferralEntity1ByInput() {
    return  {
        parentId:  currentUser.id
    };
}
function loadSearchReferralEntity2ByInput() {
    return  {
        treePath: currentUser.treePath + ",%,%"
    };
}

function updateUser() {
    $("#myModal form").submit();
}
function clearAll_() {
    $("#walletAddress,#email,#name,#lastName, input:password").val('');
    $("#countrySelect2").val('').trigger('change');
    myDropzone.removeAllFiles(true);
}

Dropzone.autoDiscover = false;
function onLoad() {
// Handle tab shown event
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        // Adjust the DataTable in the newly active tab
        let targetTab = $(e.target).attr('href'); // Get the ID of the active tab
        $(targetTab).find('.table').DataTable().columns.adjust().responsive.recalc();
    });
    myDropzone = new Dropzone(".dropzone", {
        url: "/api/v1/files",
        maxFiles: 1,
        init: function () {
            this.on("success", function (file,response) {
                // This event triggers after all files in the queue are processed
                imageProfileUrl = response.url;
            });
            this.on("maxfilesexceeded", function (file) {
                // Remove the previous file
                this.removeAllFiles();
                this.addFile(file);
                $.ajax({
                    type: "DELETE",
                    url: "/api/v1/files/" + imageProfileUrl.substring(imageProfileUrl.lastIndexOf('/')+1),
                    dataType: "json",
                    contentType: "application/json",
                    success: function (data) {
                        if (data == undefined) {
                            show_success(resources.deleteSuccess);
                        } else {
                            show_error(data.error);
                        }
                    },
                    error: function (header, status, error) {
                        if(isNullOrEmpty(get(()=>header.responseJSON)))
                            show_error('ajax answer post returned error: ' + header.responseText);
                        else show_error(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
                    }
                });
            });
        }
    });

    $('#myModal form').validate({
        rules: {
            name: "required",
            lastName: "required",
            walletAddress: {
                validTronWallet: true
            },
            countrySelect2: "required",
            password: {
                minlength: 5
            },
            repeatPassword: {
                minlength: 5,
                equalTo: "#password"
            },
            email: {
                required: true,
                email: true
            }
        },
        messages: {
            name: resources.pleaseEnter.format(resources.name),
            lastName: resources.pleaseEnter.format(resources.lastName),
            walletAddress: {
                validTronWallet: resources.invalidFormat.format(resources.wallet)
            },
            countrySelect2: resources.pleaseSelect.format(resources.country),
            password: {
                minlength: resources.mustBeMoreThan.format(resources.password, 5, resources.character)
            },
            repeatPassword: {
                minlength: resources.mustBeMoreThan.format(resources.password, 5, resources.character),
                equalTo: resources.confirmPasswordDoesNotMach
            },
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
            if (isSubmitting) {
                return; // Prevent resubmission
            }
            isSubmitting = true;
            $("#myModal .modal-content").block(blockUiOptions());
            let entity = loadSaveEntityByInput();
            $.ajax({
                type: "PATCH",
                url: `${ajaxUrl}/${entity.id}`,
                dataType: "json",
                contentType: "application/json;charset=utf-8",
                data: JSON.stringify(entity),
                success: function (data) {
                    $("#myModal .modal-content").unblock();
                    if (data.error == null) {
                        clearAll();
                        loadLabelByEntity(currentUser.id);
                        currentUser = data;
                        userProfileOccupied();
                        show_success(resources.saveSuccess);
                    } else {
                        show_error(data.error);
                    }
                    isSubmitting = false;
                },
                error: function (header, status, error) {
                    $("#myModal .modal-content").unblock();
                    isSubmitting = false;
                    if(isNullOrEmpty(get(()=>header.responseJSON)))
                        show_error('ajax answer post returned error: ' + error.responseText);
                    else if(header.responseJSON.status >= 400 && header.responseJSON.status < 500) show_warning(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
                    else show_error(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
                }
            });
        }
    });
}