import Vue from 'vue'

Vue.filter('portfolioValues', function (value, currency) {

    if (typeof value !== "number") {
        value = Number(value);
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

Vue.filter('generalValuesByCurrency', function (value, currency, useGrouping = false) {

    if (typeof value !== "number") {
        value = Number(value);
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
        // by default useGrouping: true - but it's provoked an error with input form field "number"
        useGrouping: useGrouping
    });

    return formatter.format(value);
});

Vue.filter('generalValuesWithGrouping', function (value) {

    if (typeof value !== "number") {
        value = Number(value);
    }

    if (value > 1000) {
        let formatter = new Intl.NumberFormat('en-US', {
            maximumFractionDigits: 2,
            // also by default useGrouping: true
            useGrouping: true
        });
        return formatter.format(value);
    } else {
        return value;
    }

});

Vue.filter('percentsValues', function (value) {

    if (typeof value !== "number") {
        value = Number(value);
    }

    let formatter = new Intl.NumberFormat('en-US', {
        maximumFractionDigits: 2,
    });

    let formatedValue = formatter.format(value);

    return Math.abs(Number(formatedValue)) === 0 ? Math.abs(Number(formatedValue)) : formatedValue
});


Vue.filter('marketcapValues', function (value, locale = 'en') {

    if (typeof value !== "number") {
        value = Number(value);
    }

    let formatter = new Intl.NumberFormat('en-US', {
        maximumFractionDigits: 2,
    });


    // Nine Zeroes for Billions
    return Math.abs(Number(value)) >= 1.0e+9

        ? formatter.format(Math.abs(Number(value)) / 1.0e+9) + "B"

        // Six Zeroes for Millions
        : Math.abs(Number(value)) >= 1.0e+6

            ? formatter.format(Math.abs(Number(value)) / 1.0e+6) + "M"

            // Three Zeroes for Thousands
            : Math.abs(Number(value)) >= 1.0e+3

                ? formatter.format(Math.abs(Number(value) / 1.0e+3)) + "k"

                : formatter.format(Math.abs(Number(value)));

});