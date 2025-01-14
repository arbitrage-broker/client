function onLoad(){
    const id = getURLParameter("id");
    if(!isNullOrEmpty(id)){
        loadContent(id,"#inbox-body");
    }
    $.getJSON("api/v1/notification/findAll-by-recipientId/" + currentUser.id + "?sort=read,desc", function (data) {
        data.content.forEach(function (notif){
            const notifElement = `<a href="javascript:loadContent(${notif.id},'#inbox-body')">
                                <div class="mail_list">
                                    <div class="left">
                                        ${!notif.read ? '<i class="fa fa-circle"></i>': ''} <i class="fa fa-edit"></i>
                                    </div>
                                    <div class="right">
                                        <h3>${notif.sender.selectTitle} <small>${toTimeString(notif.createdDate)}</small></h3>
                                        <p>${notif.subject.length > 98 ? notif.subject.substring(0,98) + "..." : notif.subject}</p>
                                    </div>
                                </div>
                            </a>`;
            $('#inbox-list').append(notifElement);
        });
    });
    $.getJSON("api/v1/notification/findAll-by-senderId/" + currentUser.id + "?sort=read,desc", function (data) {
        data.content.forEach(function (notif) {
            const notifElement = `<a href="javascript:loadContent(${notif.id},'#sent-body')">
                                <div class="mail_list">
                                    <div class="left">
                                        ${!notif.read ? '<i class="fa fa-circle"></i>': ''} <i class="fa fa-edit"></i>
                                    </div>
                                    <div class="right">
                                        <h3>${notif.sender.selectTitle} <small>${toTimeString(notif.createdDate)}</small></h3>
                                        <p>${notif.subject.length > 98 ? notif.subject.substring(0,98) + "..." : notif.subject}</p>
                                    </div>
                                </div>
                            </a>`;
            $('#sent-list').append(notifElement);
        });
    });
}

function loadContent(id, target){
    $.getJSON(`${ajaxUrl}/${id}`, function (data) {
        $(target).html(`<div class="mail_heading row">                                  
                                    <div class="col-md-12 text-left">
                                        <p class="date"> ${toDateTimeString(data.createdDate)}</p>
                                    </div>
                                    <div class="col-md-12">
                                        <h4> ${data.subject}</h4>
                                    </div>
                                </div>
                                <div class="sender-info">
                                    <div class="row">
                                        <div class="col-md-12">
                                            ${currentUser.id === data.sender.id
                                                ? `<strong>me</strong>`
                                                : `<strong>${data.sender.selectTitle}</strong> <span>(${data.sender.email})</span>`
                                            }
                                            to
                                            ${currentUser.id === data.recipient.id
                                                ? `<strong>me</strong>`
                                                : `<strong>${data.recipient.selectTitle}</strong> <span>(${data.recipient.email})</span>`
                                            }
                                            <a class="sender-dropdown"><i class="fa fa-chevron-down"></i></a>
                                        </div>
                                    </div>
                                </div>
                                <div class="view-mail">
                                    <p>${data.body} </p>                                   
                                </div>`);
    })
}
function loadSaveEntityByInput() {
    let model = {
        sender: {id: currentUser.id},
        subject: $('#subject').val(),
        body: $('#body').val()
    };
    return model;
}
function sendNotification() {
    let entity = loadSaveEntityByInput();
    $.blockUI(blockUiOptions());
    $("#sendNotification").attr("disabled", 'disabled');
    $.ajax({
        type: "POST",
        url: ajaxUrl+"/support",
        dataType: "json",
        contentType: "application/json;charset=utf-8",
        data: JSON.stringify(entity),
        success: function (data) {
            $.unblockUI();
            $("#sendNotification").removeAttr("disabled");
            if (data.error == null) {
                clearModal();
                $('#send-notification-modal').modal('hide');
                show_success(resources.sentSuccess);
            } else {
                show_error(data.error);
            }
        },
        error: function (header, status, error) {
            $.unblockUI();
            $("#sendNotification").removeAttr("disabled");
            if(isNullOrEmpty(get(() => header.responseJSON)))
                show_error('ajax answer post returned error: ' + error.responseText);
            else show_error(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
        }
    });
}

function clearModal(){
    $("#body,#subject").val('');
}