var rules = {};
var messages = {};
var columns = [];
var ajaxUrl;
var version;
var dataTable;
window.onload = function () {
    $(document).ajaxStop($.unblockUI);

    $("input,select,textarea").each(function () {
        $(this).attr("name", $(this).attr("id"));
    });

    //$('.date input,.date .input-group-addon').MdPersianDateTimePicker(datePickerOptions());
    if(!isNullOrEmpty($("#requestMapping").val()))
        ajaxUrl = $("#requestMapping").val();

    $('form:eq(0):not(#myModal form)').validate(validationOptions(rules, messages, function (form) {
            let fn = window["submitForm"];
            if (typeof fn === 'function') {
                fn(form);
            } else {
                fn = window["loadSaveEntityByInput"];
                if (typeof fn === 'function') {
                    let entity = fn(true);
                    if (entity instanceof FormData) {
                        submitFormData(entity);
                    } else {
                        submitEntity(entity);
                    }
                }
            }
        })
    );

    $(".table:not(#myModal .table)").on("click", ".fa-pencil", function () {
        let fn = window["loadInputByEntity"];
        if (typeof fn === 'function') {
            $.getJSON(ajaxUrl + "/" + $(this).attr("id"), function (entity) {
                if (entity.error == null) {
                    if (entity.status == 404) {
                        show_warning(resources.nothingFound);
                    } else {
                        fn(entity);
                        version = entity.version;
                    }
                } else {
                    show_error(entity.error);
                }
            });
        }
    });

    $(".table:not(#myModal .table)").on("click", ".fa-trash", function (e) {
        if (confirm(resources.areYouSure)) {
            $.ajax({
                type: "DELETE",
                url: ajaxUrl + "/" + $(this).attr("id"),
                dataType: "json",
                contentType: "application/json",
                success: function (data) {
                    if (data == undefined) {
                        $.publish("reloadTable");
                        show_success(resources.deleteSuccess);
                    } else {
                        show_error(data.error);
                    }
                },
                error: function (header, status, error) {
                    if(isNullOrEmpty(get(()=>header.responseJSON)))
                        show_error('ajax answer post returned error: ' + error.responseText);
                    else if(header.responseJSON.status >= 400 && header.responseJSON.status < 500) show_warning(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
                    else show_error(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
                }
            });
        }
    });

    let onLoad = window["onLoad"];
    if (typeof onLoad === 'function') {
        onLoad();
    }

    if ($('.table:not(#myModal .table):eq(0)').length > 0) {
        let loadTable = window["loadTable"];
        if (typeof loadTable === 'function') {
            loadTable();
        } else {
            dataTable = initAjaxTable('.table:not(#myModal .table):eq(0)', columns, ajaxUrl, "loadSearchEntityByInput");
            $.subscribe('reloadTable', dataTable.ajax.reload);
        }
    }
}

function submitFormData(formData) {
    formData.append('version', version);
    let beforeSubmitForm = window["beforeSubmitForm"];
    if (typeof beforeSubmitForm === 'function') {
        beforeSubmitForm(entity);
    }
    $.blockUI(blockUiOptions());
    $.ajax({
        type: isNullOrEmpty(formData.id) ? "POST" : "PATCH",
        url: isNullOrEmpty(formData.id) ? ajaxUrl : `${ajaxUrl}/${formData.id}`,
        processData: false,
        contentType: false,
        data: formData,
        success: function (data) {
            $.unblockUI();
            if (data.error == null) {
                clearAll();
                show_success(resources.saveSuccess);
                $.publish('reloadTable');
                let fn = window["afterSubmitForm"];
                if (typeof fn === 'function') {
                    fn(data);
                }
            } else {
                show_error(data.error);

            }
        },
        error: function (header, status, error) {
            let handleError = window["handleError"];
            if (typeof handleError === 'function') {
                handleError(header.responseJSON);
            }
            if(isNullOrEmpty(get(()=>header.responseJSON)))
                show_error('ajax answer post returned error: ' + error.responseText);
            else if(header.responseJSON.status >= 400 && header.responseJSON.status < 500) show_warning(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
            else show_error(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
        }
    });
}

function submitEntity(entity) {
    entity.version = version;
    let beforeSubmitForm = window["beforeSubmitForm"];
    if (typeof beforeSubmitForm === 'function') {
        beforeSubmitForm(entity);
    }
    $.blockUI(blockUiOptions());
    $.ajax({
        type: isNullOrEmpty(entity.id) ? "POST" : "PATCH",
        url: isNullOrEmpty(entity.id) ? ajaxUrl : `${ajaxUrl}/${entity.id}`,
        dataType: "json",
        contentType: "application/json;charset=utf-8",
        data: JSON.stringify(entity),
        success: function (data) {
            $.unblockUI();
            if (data.error == null) {
                clearAll();
                show_success(resources.saveSuccess);
                $.publish('reloadTable');
                let fn = window["afterSubmitForm"];
                if (typeof fn === 'function') {
                    fn(data);
                }
            } else {
                show_error(data.error);
            }
        },
        error: function (header, status, error) {
            let handleError = window["handleError"];
            if (typeof handleError === 'function') {
                handleError(header.responseJSON);
            }
            if(isNullOrEmpty(get(() => header.responseJSON)))
                show_error('ajax answer post returned error: ' + error.responseText);
            else if(header.responseJSON.status >= 400 && header.responseJSON.status < 500) show_warning(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
            else show_error(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
        }
    });
}

function print(entity) {
    $('#CustommersInfo').printElement({
        printMode: 'popup',
        leaveOpen: true,
        pageTitle: resources.developer,
        midleText: entity,
        topText: resources.developer,
        centerText: $('#printPopUp #titleText').val(),
        imageUrl: "../favicon.png",
        date: "تاریخ : " + gregorianStringToShamsiString(new Date().toJSON()),
        footerText: resources.developer + "<br/>" + resources.siteName,
        scriptSrc: "../assets/js/jquery-1.10.2.js,../js/report.js",
        overrideElementCSS: [{href: '../css/report.css', media: 'print'}]
    });
}

function clearAll() {
    let fn = window["clearAll_"];
    if (typeof fn === 'function') {
        fn();
    } else {
        $('form').each(function (index, frm) {
            frm.reset();
        });
        $('form input:hidden:not(#myModal form input:hidden)').val('');
        $('#myModal .panel-body input,#myModal .panel-body textarea').val('');
        $("select.select2-hidden-accessible").val('').trigger('change');
    }
}