ajaxUrl = "/api/v1/wallet";
var businessRules;
rules = {
    walletAddress: {
        required: true,
        validTronWallet: true
    },
    currency: "required",
    amount: {
        required: true,
        currency: ["$", false]
    }
};
messages = {
    walletAddress: {
        required: resources.pleaseEnter.format(resources.amount),
        validTronWallet: resources.invalidFormat.format(resources.wallet)
    },
    currency:  resources.pleaseEnter.format(resources.currency),
    amount: {
        required: resources.pleaseEnter.format(resources.amount),
        currency: resources.invalidFormat.format(resources.amount)
    },
};

columns = [{
    data: 'network'
},{
    data: 'amount',
    render: function (data) { return addPeriod(data) }
},{
    data: 'transactionType'
},{
    data: 'status',
    render: function (data) { return `<span class="${data == 'Active' ? 'green':'red'}">${data}</span>`}
}, {
    data: 'modifiedDate',
    render: function (data) { return toLocalizingDateString(data, true) }
}];

function customTableOptions(){
    let tbl_option = tableOptions();
    tbl_option.order = [[4, 'desc']];
    return tbl_option;
}

function onLoad() {
    $.getJSON("/api/v1/user/" + currentUser.id, function (user) {
        $("#walletAddress").val(user.walletAddress);
    });
    $.getJSON("/api/v1/parameter/find-by-group-code/BUSINESS_RULES", function (data) {
        businessRules = data;
        validate($("#withdrawalType").val());
    });
    $("#withdrawalType").on('change', function (){
        validate($("#withdrawalType").val());
    });
}
function validate(withdrawalType){
    $.get(`api/v1/wallet/allowed-withdrawal-balance/${currentUser.id}/${withdrawalType}`, function (allowedAmount){
        let minWithdraw = businessRules.find(x => x.code == 'MIN_WITHDRAW').value ?? '15';
        let transferFee = businessRules.find(x => x.code == 'TRANSFER_FEE').value ?? '2';
        $("#amount").attr('placeholder',`${minWithdraw} ~ ${allowedAmount}`);
        $("#withdraw-notice .alert-content ul").html(`<li>${resources.minWithdrawalNotice.format(minWithdraw)}</li>
                                                      <li>${resources.maxWithdrawalNotice.format(allowedAmount)}</li>
                                                      <li>${resources.transferFee.format(transferFee)}</li>`);
        if(withdrawalType == 'WITHDRAWAL_PROFIT') {
            if (parseFloat(allowedAmount) < parseFloat(businessRules.find(x => x.code == 'MIN_WITHDRAW').value)) {
                $("#saveWithdraw").addClass("disabled").attr("aria-disabled", true).attr('disabled',true);
                show_warning(`Insufficient profit <strong>${allowedAmount} USD</strong> to withdraw!`);
            } else {
                $("#saveWithdraw").removeClass("disabled").removeAttr("aria-disabled").removeAttr('disabled');
            }
        } else if(withdrawalType == 'WITHDRAWAL') {
            if(parseInt(allowedAmount) <= 0) {
                $("#saveWithdraw").addClass("disabled").attr("aria-disabled", true).attr('disabled',true);
                show_warning(`Insufficient fund <strong>${allowedAmount} USD</strong> to withdraw!`);
            } else {
                $("#saveWithdraw").removeClass("disabled").removeAttr("aria-disabled").removeAttr('disabled');
            }
        }
    });
}
function loadSaveEntityByInput() {
    let model = {
        amount: $("#amount").val().replace(/,/g, ""),
        actualAmount: $("#amount").val().replace(/,/g, ""),
        network: $("#network").val(),
        currency: 'USDT',
        transactionType: $("#withdrawalType").val(),
        user: {id: currentUser.id},
        address: $("#walletAddress").val()
    };
    return model;
}
function loadSearchEntityByInput() {
    let model = {
        transactionTypes: ['WITHDRAWAL','WITHDRAWAL_PROFIT'],
        userId: currentUser.id
    };
    return model;
}
function clearAll_(){
    $("#amount","#walletAddress").val('');
}

function submitForm(form){
    let entity = loadSaveEntityByInput();
    $.get(`api/v1/subscription-package/find-matched-plan/${currentUser.id}/${entity.amount}`,function (data){
       if(isNullOrEmpty(data)){
           if(confirm(resources.subscriptionCanceled))
               submitEntity(entity);
       } else if(data == 'noChange') {
               submitEntity(entity);
       } else {
           if(confirm(resources.subscriptionDowngraded.format(data)))
               submitEntity(entity);
       }
    });
}

function afterSubmitForm(entity) {
    currentUser.walletAddress = $("#walletAddress").val();
    $.blockUI(blockUiOptions());
    $(".btn-primary").attr("disabled", 'disabled');
    $.ajax({
        type: "PATCH",
        url: 'api/v1/user/' + currentUser.id,
        dataType: "json",
        contentType: "application/json;charset=utf-8",
        data: JSON.stringify(currentUser),
        success: function (data) {
            $.unblockUI();
            $(".btn-primary").removeAttr("disabled");
            if (data.error == null) {
                clearAll();
                //show_success(resources.saveSuccess);
            } else {
                show_error(data.error);
            }
        },
        error: function (header, status, error) {
            $.unblockUI();
            $(".btn-primary").removeAttr("disabled");
            if(isNullOrEmpty(get(() => header.responseJSON)))
                show_error('ajax answer post returned error: ' + error.responseText);
            else if(header.responseJSON.status >= 400 && header.responseJSON.status < 500) show_warning(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
            else show_error(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
        }
    });
    $("#withdraw-alert").remove();
    let infoElement = `<div id="withdraw-alert" class="alert alert-info">
    <button onclick="$('#withdraw-alert').hide()" class="close">&times;</button>
    <div class="alert-content">${resources.withdrawalAlert}</div>
    </div>`;
    $(".btn-primary").after(infoElement);
}

function handleError(responseJSON){
    if(responseJSON.status == 417){
        if($("#transaction-notice").length == 0)
            $("#withdraw-notice").after(`<div id="transaction-notice" class="col-12 alert alert-warning">
                                        <button onclick="$('#transaction-notice').hide()" class="close">&times;</button>
                                        <div class="alert-content">
                                            <i class="fa fa-warning" style="font-size:17px"></i>
                                            <ul style="margin: 0">
                                                <li>${responseJSON.message}</li>                                           
                                            </ul>
                                        </div>
                                    </div>`);
    }
}