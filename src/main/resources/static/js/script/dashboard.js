$(function () {
    $.get("/api/v1/user/total-online", function (data) {
        $("#totalOnline").text(get(() => data, 0));
    });

    $.get("api/v1/subscription/find-active-by-user/" + currentUser.id, function (data) {
        if(isNullOrEmpty(data) || data.subscriptionPackage.name != 'Free') {
            $(".reverse_timer").remove();
        } else $("#totalProfitReverseTimer").text(data.remainingWithdrawalPerDay);
    });
    $.getJSON("/api/v1/wallet/total-bonus/" + currentUser.id, function (data) {
        $("#referralBonus").text(get(() => data, 0));
    });

    $.getJSON("/api/v1/wallet/total-profit/" + currentUser.id, function (totalProfit) {
        $("#totalProfit").text(get(() => totalProfit, 0));
        $.getJSON("/api/v1/wallet/total-deposit/" + currentUser.id, function (totalDeposit) {
            $("#totalDeposit").text(get(() => totalDeposit, 0));
            $("#totalBalance").text(get(() => parseFloat(totalDeposit) + parseFloat(totalProfit), 0));
        });
    });

    $.getJSON("/api/v1/wallet/total-withdrawal/" + currentUser.id, function (data) {
        $("#totalWithdrawal").text(get(() => data, 0));
    });

    $.get("/api/v1/arbitrage/count-all-by-user/" + currentUser.id, function (data) {
        $("#totalArbitrage").text(data);
    });

    $.get(`/api/v1/arbitrage/count-today-by-user/${currentUser.id}`, function (data) {
        $("#dailyArbitrage").text(data);
    });

    $.get(`/api/v1/wallet/daily-profit/${currentUser.id}`, function (data) {
        $("#dailyProfit").text(get(() => data, 0));
        if(parseInt(data) < 0)
            $("#dailyProfit").removeClass("green").addClass("red");
    });

    $.get("/api/v1/arbitrage/find-top-coins/5", function (data) {
        data.content.forEach(function (coin, i) {
            $("#coinName-" + (i+1)).text(coin.name);
            $("#coinPercentage-" + (i+1)).text(`${coin.usagePercentage}%`);
        });
        if ($('#top-coin-doughnut').length) {

            var chart_doughnut_settings = {
                type: 'doughnut',
                tooltipFillColor: "rgba(51, 51, 51, 0.55)",
                data: {
                    labels: data.content.map(x=>x.name),
                    datasets: [{
                        data: data.content.map(x=>x.usagePercentage),
                        backgroundColor: [
                            "#3498DB",
                            "#26B99A",
                            "#9B59B6",
                            "#5ECAE3FF",
                            "#E74C3C",
                        ],
                        hoverBackgroundColor: [
                            "#3498DB",
                            "#26B99A",
                            "#9B59B6",
                            "#5ECAE3FF",
                            "#E74C3C",
                        ]
                    }]
                },
                options: {
                    legend: false,
                    responsive: false
                }
            }

            $('#top-coin-doughnut').each(function () {
                var chart_element = $(this);
                var chart_doughnut = new Chart(chart_element, chart_doughnut_settings);
            });

        }
    });
    // $.getJSON("/api/v1/user?page=0&size=2&sort=childCount,desc", function (data) {
    //     data.content.forEach(function (value) {
    //         const topReferralElement =
    //             `<div class="widget_summary">
    //                     <div class="w_left w_25">
    //                         <span>${value.firstName + ' ' + value.lastName}</span>
    //                     </div>
    //                     <div class="w_center w_55">
    //                         <div class="progress">
    //                             <div class="progress-bar bg-green" role="progressbar" aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: ${value.childCount}%;" data-transitiongoal="${value.childCount}">
    //                                 <span class="sr-only">60% Complete</span>
    //                             </div>
    //                         </div>
    //                     </div>
    //                     <div class="w_right w_20">
    //                         <span>${value.childCount == 0 ? 3 : value.childCount}k</span>
    //                     </div>
    //                     <div class="clearfix"></div>
    //                 </div>`;
    //         $("#top-referrals").append(topReferralElement);
    //     });
    // });
    // $.getJSON("/api/v1/user/count-by-country", function (data) {
    //     data.forEach(function (value) {
    //         const countryElement = `<tr>
    //                                     <td>${value.country}</td>
    //                                     <td class="fs15 fw700 text-right">${value.percent}%</td>
    //                                 </tr>`;
    //         $(".countries_list").append(countryElement);
    //     });
    // });

    //chart
    var chart_plot_01_settings = {
        series: {
            lines: {
                show: false,
                fill: true
            },
            splines: {
                show: true,
                tension: 0.4,
                lineWidth: 1,
                fill: 0.4
            },
            points: {
                radius: 0,
                show: true
            },
            shadowSize: 2
        },
        grid: {
            verticalLines: true,
            hoverable: true,
            clickable: true,
            tickColor: "#d5d5d5",
            borderWidth: 1,
            color: '#fff'
        },
        colors: ["rgba(38, 185, 154, 0.38)", "rgba(3, 88, 106, 0.38)"],
        xaxis: {
            tickColor: "rgba(51, 51, 51, 0.06)",
            mode: "time",
            tickSize: [1, "day"],
            //tickLength: 10,
            axisLabel: "Date",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 10
        },
        yaxis: {
            ticks: 8,
            tickColor: "rgba(51, 51, 51, 0.06)",
            axisLabel: "Value",  // Added Y-axis label
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 5
        },
        tooltip: false,
        legend: {  // Added legend settings
            show: true,
            position: "nw",
            labelFormatter: function(label, series) {
                return '<span style="color: #000;">' + label + '</span>';
            }
        }
    };
    if ($("#chart_01").length) {
        $.getJSON("/api/v1/wallet/get-date-range/" + addDays(new Date(),-7).getTime() +"/"+ new Date().getTime() + "/DEPOSIT", function (depositData) {
            const depositArray = Object.entries(depositData).map(([key, value]) => [key, value]).sort((a, b) => Number(a[0]) - Number(b[0]));
            $.getJSON("/api/v1/wallet/get-date-range/" + addDays(new Date(),-7).getTime() +"/"+ new Date().getTime() + "/WITHDRAWAL", function (withdrawalData) {
                const withdrawalArray = Object.entries(withdrawalData).map(([key, value]) => [key, value]).sort((a, b) => Number(a[0]) - Number(b[0]));
                $.plot($("#chart_01"),[depositArray, withdrawalArray],chart_plot_01_settings);
            });
        });
    }
    //chart
    if ($("#chart_02").length) {
        $.getJSON("/api/v1/wallet/get-date-range/" + addDays(new Date(),-7).getTime() +"/"+ new Date().getTime() + "/BONUS", function (bonusData) {
            const bonusArray = Object.entries(bonusData).map(([key, value]) => [key, value]).sort((a, b) => Number(a[0]) - Number(b[0]));
            $.getJSON("/api/v1/wallet/get-date-range/" + addDays(new Date(),-7).getTime() +"/"+ new Date().getTime() + "/REWARD", function (rewardData) {
                const rewardArray = Object.entries(rewardData).map(([key, value]) => [key, value]).sort((a, b) => Number(a[0]) - Number(b[0]));
                $.plot($("#chart_02"), [bonusArray,rewardArray], chart_plot_01_settings);
            });
        });
    }
});
