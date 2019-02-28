import Vue from 'vue'

Vue.filter('portfolioValues', function (value, currency) {

    console.log('currency', currency);

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