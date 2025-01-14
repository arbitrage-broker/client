ajaxUrl = "/api/v1/user/";
var referralDataTable1;
var referralDataTable2;
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
        data: 'bonus'
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
        data: 'bonus'
    },{
        data: 'reward'
    },{
        data: 'modifiedDate',
        render: function (data) { return toLocalizingDateString(data, true) }
    }], "/api/v1/user", "loadSearchReferralEntity2ByInput");
    $.subscribe('reloadReferral2Table', referralDataTable2.ajax.reload);
}
function loadSearchReferralEntity1ByInput() {
    return  {
        parentId:  currentUser.id,
        firstName: $("#name").val(),
        lastName: $("#lastName").val(),
        userName: $("#userName").val(),
        email: $("#email").val()
    };
}
function loadSearchReferralEntity2ByInput() {
    return  {
        treePath: currentUser.id + ",%,%",
        firstName: $("#name").val(),
        lastName: $("#lastName").val(),
        userName: $("#userName").val(),
        email: $("#email").val()
    };
}
function clearAll_() {
    $("#name,#lastName,#userName,#email").val('');
    $("#countrySelect2").val('').trigger('change');
}
function onLoad() {

}