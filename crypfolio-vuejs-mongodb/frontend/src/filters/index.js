import Vue from 'vue'

Vue.filter('portfolioValues', function (value, currency) {

    if (typeof value !== "number") {
        return value;
    }

    let maximumFractionDigits = 2;

    switch (currency) {
        // increase the number of fraction digits
        case 'BTC':
        case 'ETH':
            maximumFractionDigits = 8;
            break;
    }

    let formatter = new Intl.NumberFormat('en-US', {
        maximumFractionDigits: maximumFractionDigits,
    });

    return formatter.format(value);
});

Vue.filter('generalValues', function (value, currency) {

    if (typeof value !== "number") {
        return value;
    }

    let maximumFractionDigits = 2;

    switch (currency) {
        case 'USD':
        case 'EUR':
            if (value < 1) {
                maximumFractionDigits = 4;
            }
            break;
        // increase the number of fraction digits
        case 'BTC':
        case 'ETH':
            maximumFractionDigits = 8;
            break;
    }

    let formatter = new Intl.NumberFormat('en-US', {
        maximumFractionDigits: maximumFractionDigits,
    });

    return formatter.format(value);
});

Vue.filter('percentsValues', function (value) {

    if (typeof value !== "number") {
        return value;
    }

    let formatter = new Intl.NumberFormat('en-US', {
        maximumFractionDigits: 2,
    });

    return formatter.format(value);
});


Vue.filter('marketcapValues', function (num, locale = 'en') {

    if (typeof num !== "number") {
        return num;
    }

    let formatter = new Intl.NumberFormat('en-US', {
        maximumFractionDigits: 2,
    });


    // Nine Zeroes for Billions
    return Math.abs(Number(num)) >= 1.0e+9

        ? formatter.format(Math.abs(Number(num)) / 1.0e+9) + "B"

        // Six Zeroes for Millions
        : Math.abs(Number(num)) >= 1.0e+6

            ? formatter.format(Math.abs(Number(num)) / 1.0e+6) + "M"

            // Three Zeroes for Thousands
            : Math.abs(Number(num)) >= 1.0e+3

                ? formatter.format(Math.abs(Number(num) / 1.0e+3)) + "k"

                : formatter.format(Math.abs(Number(num)));

});