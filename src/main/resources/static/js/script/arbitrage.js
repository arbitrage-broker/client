let selectedSubscription = {};
let purchaseLimit = $("#purchaseLimit").val();
$(function () {
    if (!isNullOrEmpty($("#selectedSubscription").val())) {
        selectedSubscription = JSON.parse($("#selectedSubscription").val());
        $("#subscription-package-content .collapse-link").click();
        // $("#trading-content").show();
    }
    if(!isNullOrEmpty(purchaseLimit)){
        $("#trading-order").html(`<div id="purchase-limit-notice" class="col-12 alert alert-warning">
                                    <button onclick="$('#purchase-limit-notice').hide()" class="close">&times;</button>
                                    <div class="alert-content">
                                        <i class="fa fa-warning" style="font-size:17px"></i>
                                        <ul style="margin: 0">
                                            <li>You have reached the purchase limitation, please try ${purchaseLimit}</li>                                           
                                        </ul>
                                    </div>
                                </div>`);
    }
});

function purchase(subscriptionPackageId, amount) {
    let jsonData = {user:{id: currentUser.id}, subscriptionPackage:{id:subscriptionPackageId}};
    $.blockUI(blockUiOptions());
    $.postJSON("/api/v1/subscription/purchase", jsonData, function (data) {
        $.unblockUI();
        if (data.error == null) {
            show_success(resources.saveSuccess);
            refreshSubscriptionPackages();
        } else {
            show_error(data.error);
        }
    },function (header, status, error) {
        $.unblockUI();
        if(isNullOrEmpty(get(() => header.responseJSON)))
            show_error('ajax answer post returned error: ' + error.responseText);
        else if(header.responseJSON.status >= 400 && header.responseJSON.status < 500) show_warning(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
        else show_error(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
        if(header.responseJSON.status == 406)
            setTimeout(function() {
                loadPages('deposit?amount=' + amount);
            }, 5000);
    });
}
function refreshSubscriptionPackages(){
    //create subscription packages
    $("#subscription-package").empty();
    $.getJSON("/api/v1/subscription-package?size=100&sort=price,asc&status=Active", function (subscriptionPackages) {
        $.get("/api/v1/subscription/find-active-by-user/" + currentUser.id, function (subscription) {
            selectedSubscription = subscription;
            subscriptionPackages.content.forEach(function (value) {
                const selectedSubscriptionPackagePrice = get(()=>subscription.subscriptionPackage.price,0);
                // Create the entire price element
                const priceElement = `
                <div class="col-md-3 col-sm-6" id="subscription-package-item-${value.id}">
                    <div class="pricing">
                        <div class="title">
                            <h2>${value.name}</h2>
                            <h1>${addPeriod(value.price)} ${value.currency}</h1>
                        </div>
                        <div class="x_content">
                            <div class="">
                                <div class="pricing_features">
                                    <ul class="list-unstyled text-left">
                                        <li><i class="fa ${value.name == 'Free' ? 'fa-times text-danger' : 'fa-check text-success'}"></i> Allow withdrawal.</li>
                                        <li><i class="fa fa-check text-success"></i> Order count <strong> ${value.orderCount}</strong> times</li>
                                        <li><i class="fa fa-check text-success"></i> Trading reward between range (<strong>${value.minTradingReward} - ${value.maxTradingReward}</strong>) ${value.currency}</li>
                                        <li><i class="fa fa-check text-success"></i> User profit percentage (<strong>${value.userProfitPercentage}%</strong>)</li>
                                        <li><i class="fa fa-check text-success"></i> Site profit percentage (<strong>${value.siteProfitPercentage}%</strong>)</li>
                                        <li><i class="fa fa-check text-success"></i> Will Expire in (<strong>${value.duration < 0 ? '<i class="fa fa-infinity"></i>' : value.duration}</strong>) days</li>
                                        <li><i class="fa fa-check text-success"></i> Parent referral bonus <strong>${value.parentReferralBonus}</strong> ${value.currency}</li>
                                        <li><i class="fa fa-check text-success"></i> Maximum acceptable amount <strong>${addPeriod(value.maxPrice)}</strong> ${value.currency}</li>
                                    </ul>
                                </div>
                            </div>
                            <div class="pricing_footer">
                                <a id="trade-btn-${value.id}" href="javascript:trade(${value.id});" class="btn btn-success btn-block ${selectedSubscriptionPackagePrice!=value.price ? 'disabled' : ''}" aria-disabled="${selectedSubscriptionPackagePrice != value.price}" role="button">Trade ${isNullOrEmpty(purchaseLimit) ? 'Now' : purchaseLimit}</a> 
                                <a href="javascript:purchase(${value.id},${value.price});" class="btn btn-primary btn-block ${selectedSubscriptionPackagePrice >= value.price ? 'disabled' : ''}" aria-disabled="${selectedSubscriptionPackagePrice >= value.price}" role="button">Purchase<span></span></a>                          
                            </div>
                        </div>
                    </div>
                </div>`;
                // Append the price element to the subscription package container
                $("#subscription-package").append(priceElement);
            });
            $("#trading-content").show();
            $("#subscription-package-content .collapse-link").click();
            trade(selectedSubscription.subscriptionPackage.id);
        });
    });
}
async function trade(id, callback) {
    $("#trade-btn-"+ id).attr("disabled", "disabled").attr("aria-disabled",true).addClass("disabled");
    purchaseLimit = await (await fetch("/api/v1/arbitrage/purchase-limit/" + currentUser.id)).text();
    if(!isNullOrEmpty(purchaseLimit)) {
        show_warning(`You have reached the purchase limitation, please try ${purchaseLimit}`);
        $(`#subscription-package-item-${id} .btn-success`).text('Trade ' + purchaseLimit);
        $("#trading-order").html(`<div id="purchase-limit-notice" class="col-12 alert alert-warning">
                                    <button onclick="$('#purchase-limit-notice').hide()" class="close">&times;</button>
                                    <div class="alert-content">
                                        <i class="fa fa-warning" style="font-size:17px"></i>
                                        <ul style="margin: 0">
                                            <li>You have reached the purchase limitation, please try ${purchaseLimit}</li>                                           
                                        </ul>
                                    </div>
                                </div>`);
        return;
    } else {
        $(`#subscription-package-item-${id} .btn-success`).text(resources.tradeNow);
    }
    if(!isNullOrEmpty($("#selectedSubscription").val()) && !$("#trading-content").is(":visible")) {
        $("#subscription-package-content .collapse-link").click();
        $("#trading-content").show();
        // goToByScroll("#trading-content");
    }
    //create trading orders
    $("#trading-content .x_title h2 small").text(selectedSubscription.subscriptionPackage.name);
    const orderCount = get(() => selectedSubscription.subscriptionPackage.orderCount, 0);
    if (orderCount > 0) {
        $("#trading-order").empty();
        for (let i = 0; i < orderCount; i++) {
            let coin = await (await fetch('api/v1/coin/buy/' + currentUser.id)).json();
            let exchanges = await (await fetch('api/v1/exchange/buy/' + currentUser.id)).json();
            const orderElement = `
            <div class="col-md-3 widget widget_tally_box">
                <div class="x_panel">
                    <div class="x_title">
                        <h2>Order ${i + 1}</h2>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">

                        <div style="text-align: center; margin-bottom: 17px">
                         <span id="order-${i + 1}" class="chart" data-percent="100">
                              <span class="percent"></span>
                         </span>
                        </div>

                        <h3 class="name_title"><img width="40px" src="${coin.logo}"/> ${coin.name}</h3>
                        <p>From exchange <img width="20px" src="${exchanges[0].logo}"/> ${exchanges[0].name} to <img width="20px" src="${exchanges[1].logo}"/> ${exchanges[1].name}</p>

                        <div class="divider"></div>
                        <a id="buy-btn-${i + 1}" href="javascript:buy(${i + 1}, ${exchanges[0].id}, ${coin.id}, ${selectedSubscription.id});" class="btn btn-primary btn-block" role="button">Buy/Sell</a>
                    </div>
                </div>
            </div>`;
            // Append the order element to the trading container
            $("#trading-order").append(orderElement);
        }
        if (typeof callback === 'function') {
            callback();
        }
    }
    $("#trade-btn-"+ id).removeAttr("disabled").attr("aria-disabled",false).removeClass("disabled");
}
function buy(index, exchangeId, coinId, subscriptionId) {
    $("#buy-btn-" + index).addClass("disabled").attr("aria-disabled", "true");
    $("#order-" + index).easyPieChart({
        easing: 'easeOutElastic',
        delay: 3000,
        barColor: '#26B99A',
        trackColor: '#fff',
        scaleColor: false,
        lineWidth: 20,
        trackWidth: 16,
        lineCap: 'butt',
        onStep: function (from, to, percent) {
            $(this.el).find('.percent').text(Math.round(percent));
        },
        onStop:function (from, to) {
            console.log('Animation complete. Final percentage: ' + to);
            $.postJSON("/api/v1/arbitrage",{user:{id:currentUser.id}, exchange:{id:exchangeId}, coin:{id: coinId}, subscription:{id:subscriptionId}}, function (data) {
                $("#buy-btn-" + index).after(`<p>You received ${data.reward} profit from this purchase at ${new Date(data.createdDate).toLocaleString()}</p>`);
                $("#buy-btn-" + index).remove();
            },function (header, status, error) {
                if(isNullOrEmpty(get(() => header.responseJSON)))
                    show_error('ajax answer post returned error: ' + error.responseText);
                else if(header.responseJSON.status >= 400 && header.responseJSON.status < 500) show_warning(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
                else show_error(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
                $("#buy-btn-" + index).after(`<p class="red">${header.responseJSON.message}</p>`);
                $("#buy-btn-" + index).remove();
            });
        },
        animate: {
            duration: (Math.floor(Math.random() * (60 - 20 + 1)) + 20) * 1000, // random number 20000-60000 seconds animation duration
            enabled: true
        }
    });
}