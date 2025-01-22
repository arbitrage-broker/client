ajaxUrl = "/api/v1/wallet";
rules = {
    network: "required",
    currency: "required",
    walletAddress: {
        required: true,
        validTronWallet: true
    },
    transactionHash: {
        required: true,
        validTronHash: true
    },
    amount: {
        required: true,
        currency: ["$", false]
    },
    actualAmount: {
        required: true,
        currency: ["$", false]
    }
};
messages = {
    network: resources.pleaseEnter.format(resources.network),
    currency: resources.pleaseEnter.format(resources.currency),
    walletAddress: {
        required: resources.pleaseEnter.format(resources.amount),
        validTronWallet: resources.invalidFormat.format(resources.wallet)
    },
    transactionHash: {
        required: resources.pleaseEnter.format(resources.transactionHash),
        validTronHash: resources.invalidFormat.format(resources.transactionHash)
    },
    amount: {
        required: resources.pleaseEnter.format(resources.amountInUSD),
        currency: resources.invalidFormat.format(resources.amountInUSD)
    },
    actualAmount: {
        required: resources.pleaseEnter.format(resources.actualAmount),
        currency: resources.invalidFormat.format(resources.actualAmount)
    }
};

columns = [{
    data: 'amount',
    render: function (data) { return addPeriod(data) }
},{
    data: 'actualAmount',
    render: function (data) { return addPeriod(data) }
},{
    data: 'currency'
},{
    data: 'transactionType'
},{
    data: 'status',
    render: function (data) { return `<span class="${data == 'Active' ? 'green':'red'}">${data}</span>`}
},{
    data: 'modifiedDate',
    render: function (data) { return toLocalizingDateString(data, true) }
}];
function customTableOptions(){
    let tbl_option = tableOptions();
    tbl_option.order = [[5, 'desc']];
    return tbl_option;
}


function fillWalletAddress(network, currency) {
    $.blockUI(blockUiOptions());
    $.ajax({
        type: "GET",
        url: `/api/v1/role-detail/${currentUser.role}/${network}/${currency}`,
        dataType: "json",
        contentType: "application/json;charset=utf-8",
        success: function (data) {
            $.unblockUI();
            $("#walletAddress").val(data.address);
            initQrCode(data.address);
            $("#walletAddress-title").html(data.description + `&nbsp;<a class="fas fa-copy" href="javascript:copyValue('${data.address}', 'Wallet address')"></a>`);
        },
        error: function (header, status, error) {
            $.unblockUI();
            show_warning("No wallet address is available for the selected network and currency.");
            $("#walletAddress").val('');
            $("#walletAddress-title").empty();
            initQrCode("---");
        }
    });
}

function onLoad() {
    fillWalletAddress('TRC20','USDT');
    $('#amount').on('input', function() {
        clearTimeout(timeout);
        timeout = setTimeout(updateActualAmount, 500);
    });
    $("#currency,#network").on('change', updateActualAmount);
}
function initQrCode(value) {
    QRCode.toCanvas(value, { width: 200, margin: 2 }, function(error, canvas) {
        if (error) {
            console.error(error);
        } else {
            $("#qrcode").html(canvas);
        }
    });
}

function loadSaveEntityByInput() {
    let model = {
        amount: $("#amount").val().replace(/,/g, ""),
        actualAmount: $("#actualAmount").val().replace(/,/g, ""),
        network: $("#network").val(),
        currency: $("#currency").val(),
        transactionType: 'DEPOSIT',
        user: {id: currentUser.id},
        transactionHash: $("#transactionHash").val(),
        address: $("#walletAddress").val()
    };
    return model;
}
function loadSearchEntityByInput() {
    let model = {
        transactionTypes: ['DEPOSIT','BONUS','REWARD'],
        userId: currentUser.id
    };
    return model;
}
function clearAll_(){
    $("#amount,#transactionHash","#actualAmount").val('');
}
function afterSubmitForm(entity) {
    $("#deposit-alert").remove();
    let infoElement = `<div id="deposit-alert" class="alert alert-info">
    <button onclick="$('#deposit-alert').hide()" class="close">&times;</button>
    <div class="alert-content"><i class="fa fa-info-circle" style="font-size:17px"></i>&nbsp;&nbsp;${resources.depositAlert}</div>
</div>`;
    $(".btn-primary").after(infoElement);
}

let timeout;
function fetchTokenPrice(network,currency, callback) {
    const endpoints = {
        'TRC20': 'https://apilist.tronscanapi.com/api/token/price?token=',
        'BEP20': '/api/v1/common/price/BEP20/'
    };
    $.blockUI(blockUiOptions());
    $.ajax({
        type: "GET",
        url: endpoints[network] + currency,
        dataType: "json",
        contentType: "application/json;charset=utf-8",
        success: function (data) {
            $.unblockUI();
            if (network === 'TRC20') {
                callback(get(()=>data.price_in_usd));
            } else if (network === 'BEP20') {
                callback(get(()=>data.price));
            } else {
                callback(get(()=>data.price)); // Default for other networks
            }
        },
        error: function (header, status, error) {
            $.unblockUI();
            callback(null);
            // if (isNullOrEmpty(get(() => header.responseJSON)))
            //     show_error('ajax answer GET returned error: ' + error.responseText);
            // else if (header.responseJSON.status >= 400 && header.responseJSON.status < 500) show_warning(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
            // else show_error(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
        }
    });
}
function updateActualAmount() {
    console.log('change trigerred');
    const network = $('#network').val();
    const currency = $('#currency option:selected').attr('data');
    const usdAmount = $('#amount').val();

    fillWalletAddress(network,$("#currency option:selected").text());
    if (!usdAmount) {
        $('#actualAmount').val('');
        return;
    } else if(currency == 'USDT' || currency == 'BUSD') {
        $('#actualAmount').val(usdAmount);
        return;
    }


    fetchTokenPrice(network,currency,function (price){
        if(isNullOrEmpty(price)) {
            $('#actualAmount').val('');
        } else {
            const converted = parseFloat(usdAmount) / parseFloat(price);
            $('#actualAmount').val(converted.toFixed(6));
        }
    });
}
function handleError(responseJSON){
    if(responseJSON.status == 417){
        if($("#transaction-notice").length == 0)
            $("#saveDeposit").after(`<div id="transaction-notice" class="col-12 alert alert-warning">
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