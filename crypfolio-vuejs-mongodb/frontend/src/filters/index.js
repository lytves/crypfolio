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