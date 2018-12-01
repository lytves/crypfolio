/**
 * Listener to trigger modal close, when clicked on dialog mask
 * got it from https://stackoverflow.com/questions/14595835/primefaces-dialog-close-on-click-outside-of-the-dialog
 */
$(document).ready(function () {
    $("body").on("click", '.ui-dialog-mask', function () {
        idModal = this.id;
        idModal = idModal.replace("_modal", "");
        getWidgetVarById(idModal).hide();
    })
});

/**
 * Returns the PrimefacesWidget from ID
 * @param id
 * @returns {*}
 */
function getWidgetVarById(id) {
    for (var propertyName in PrimeFaces.widgets) {
        var widget = PrimeFaces.widgets[propertyName];
        if (widget && widget.id === id) {
            return widget;
        }
    }
}

/*
* depends of form input values - to enable/disable the button "Done"
* */
function enableDoneButton() {

    var buttonDone = document.getElementById("mainTabView:formPortfolioAddItem-transaction:add-transaction-done");
    var inputAmountValue = document.getElementById("mainTabView:formPortfolioAddItem-transaction:add-transaction-amount_hinput").value;
    var inputPriceValue = document.getElementById("mainTabView:formPortfolioAddItem-transaction:add-transaction-price_hinput").value;
    var inputTotalValue = document.getElementById("mainTabView:formPortfolioAddItem-transaction:add-transaction-total_hinput").value;

    // old version of the condition: if (Number(inputAmountValue) > 0 && inputPriceValue !== "" && inputTotalValue !== "")
    if (Number(inputAmountValue) > 0 && Number(inputPriceValue) > 0 && Number(inputTotalValue) > 0)
        buttonDone.classList.remove('ui-state-disabled');
    else
        buttonDone.classList.add('ui-state-disabled');
}

/*
* working of the button "All" which works with "Sell" transaction type
* */
function showButtonSellAll() {

    var el = document.getElementById("mainTabView:formPortfolioAddItem-transaction:add-transaction-amount-sell-all");

    if (el && el.style.display === 'block')
        el.style.display = 'none';
    else
        el.style.display = 'block';
}

/*
* ajax Status modal window delay
* */
var ajaxInProgress;

function startHandler() {
    ajaxInProgress = setTimeout(function () {
        PF('ajaxStatus').show();
    }, 300);
}

function endHandler() {
    clearTimeout(ajaxInProgress);
    PF('ajaxStatus').hide();
    ajaxInProgress = null;
}