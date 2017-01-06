$(document).ready(function () {
    // Camera Registrations
    $.ajax({
        url: "api/findAllCameraRegistrations",
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: "json",
        success: function (response) {
            var trHTML = '<caption>Camera Registrations</caption><thead><tr><th>UID</th><th>Street</th><th>Town</th><th>Max Speed Limit</th><th>Start Time</th></tr></thead><tbody>';
            $.each(response, function (key, value) {
                trHTML +=
                        '<tr><td>' + value.uid +
                        '</td><td>' + value.street +
                        '</td><td>' + value.town +
                        '</td><td>' + value.maxSpeedLimit +
                        '</td><td>' + value.startTime +
                        '</td></tr>';
            });
            trHTML += '</tbody>';
            $('#cameras-table').append(trHTML);
        }
    });

    // Priorities
    $.ajax({
        url: "api/findAllPrioritySightings",
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: "json",
        success: function (response) {
            var trHTML = '<caption>Suspicious Vehicles</caption><thead><tr><th>Registration</th><th>Vehicle Type</th><th>Current Speed</th><th>Capture Date</th><th>Camera UID</th></tr></thead><tbody>';
            $.each(response, function (key, value) {
                trHTML +=
                        '<tr><td class="reg">' + value.registration +
                        '</td><td>' + value.vehicleType +
                        '</td><td>' + value.currentSpeed +
                        '</td><td>' + value.captureDate +
                        '</td><td>' + value.camera.uid +
                        '</td></tr>';
            });
            trHTML += '</tbody>';
            $('#priority-table').append(trHTML);
        }
    });

    // History
    $("#priority-table").on('click', 'tr', function (e) {
        e.preventDefault();
        var reg = $(this).find('td.reg').html();
        if (typeof reg != 'undefined') {
            $.ajax({
                url: "api/findSightingHistory/" + reg,
                type: "GET",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                dataType: "json",
                success: function (response) {
                    $("#history-table tr").remove();
                    $('#history-caption').text("");
                    var historyCaption = "";
                    var trHTML = '<caption id="history-caption"></caption><thead><tr><th>Capture Date</th><th>Town</th><th>Street</th><th>Speed Limit</th><th>Current Speed</th></tr></thead><tbody>';
                    $.each(response, function (key, value) {
                        historyCaption = value.registration;
                        trHTML +=
                                '<tr><td>' + value.captureDate +
                                '</td><td>' + value.camera.town +
                                '</td><td>' + value.camera.street +
                                '</td><td>' + value.camera.maxSpeedLimit +
                                '</td><td>' + value.currentSpeed +
                                '</td></tr>';
                    });
                    trHTML += '</tbody>';
                    $('#history-table').append(trHTML);
                    $('#history-caption').append("Historical sightings for " + historyCaption);
                    $('html, body').animate({
                        scrollTop: $("#history-table").offset().top
                    }, 100);
                }
            });
        }
    });

    // Chart data
    function loadChartData() {
        $.ajax({
            url: "api/findSnapshotData",
            type: "GET",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            dataType: "json",
            success: function (response) {
                var labels = response.cameraUIDs;
                var series = [
                    response.snapshotSeries,
                    response.speedingSeries
                ];
                var chartData = {};
                chartData.labels = labels;
                chartData.series = series;

                new Chartist.Bar('.ct-chart', chartData, {
                    height: '400px',
                    seriesBarDistance: 10,
                    reverseData: true,
                    horizontalBars: true,
                    axisY: {
                        offset: 100
                    },
                    axisX: {
                        onlyInteger: true
                    }
                });
            }
        });
    }

    $("#chart-button").click(function () {
        loadChartData();
    });

    loadChartData();
});