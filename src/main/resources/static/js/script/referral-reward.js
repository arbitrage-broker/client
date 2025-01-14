function claimReferralReward(count) {
    $.blockUI(blockUiOptions());
    $.ajax({
        type: "POST",
        url: `api/v1/wallet/claim-referral-reward/${currentUser.id}/${count}`,
        dataType: "json",
        contentType: "application/json;charset=utf-8",
        success: function (data) {
            $.unblockUI();
            if (data.error == null) {
                show_success(resources.saveSuccess);
                generateReferralRewardItems();
            } else {
                show_error(data.error);
            }
        },
        error: function (header, status, error) {
            $.unblockUI();
            if(isNullOrEmpty(get(() => header.responseJSON)))
                show_error('ajax answer post returned error: ' + error.responseText);
            else if(header.responseJSON.status >= 400 && header.responseJSON.status < 500) show_warning(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
            else show_error(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
        }
    });
}

function generateReferralRewardItems(){
    $.blockUI(blockUiOptions());
    $.getJSON(`api/v1/wallet/get-claimed-referrals/${currentUser.id}`,function(claimedReferralsCount){
        $("#claimedReferralsCount").text(claimedReferralsCount);
        let totalReferralCount = parseInt($("#totalReferralCount").text());
        $("#remainedReferralsCount").text(totalReferralCount - parseInt(claimedReferralsCount));
        $.getJSON(`api/v1/parameter/find-by-group-code/REFERRAL_REWARD`,function(parameters){
            $("#referral-rewards-content").empty();
            parameters.forEach(p => {
                let remainedReferralsCount = parseInt($("#remainedReferralsCount").text());
                let percentage = remainedReferralsCount * 100 / parseInt(p.title);
                let element = `<div class="row">
                <div class="col-md-12 col-sm-12 ">
                    <div class="x_panel">
                        <div class="x_title">
                            <h2>${resources.inviteXFriends.format(p.title)}</h2>
                            <div class="clearfix"></div>
                        </div>
                        <div class="x_content">
                            <div class="row">
                                <table class="col-md-12 col-sm-12" style="width: 100%">
                                    <tr>
                                        <td style="padding-left: 10px"><i class="fa fa-coins"></i>&nbsp;${p.value} USD</td>
                                        <td style="text-align: right"><a href="javascript:claimReferralReward(${p.title})" aria-disabled=${percentage < 100} type="button" class="btn btn-sm ${percentage >= 100 ? 'btn-success' : 'btn-secondary disabled'}">${resources.claim}</a></td>
                                    </tr>
                                </table>
                                <div class="col-md-12 col-sm-12">
                                    <div class="progress progress_sm">
                                        <div class="progress-bar bg-green" role="progressbar" data-transitiongoal=${percentage}></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>`;
                $("#referral-rewards-content").append(element);
            });
            $('.progress .progress-bar').progressbar();
            $.unblockUI();
        });
    });
}