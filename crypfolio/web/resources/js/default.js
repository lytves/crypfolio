/**
 * Listener to trigger modal close, when clicked on dialog mask
 * got it from https://stackoverflow.com/questions/14595835/primefaces-dialog-close-on-click-outside-of-the-dialog
 */
$(document).ready(function(){
    $("body").on("click",'.ui-dialog-mask',function () {
        idModal = this.id;
        idModal = idModal.replace("_modal","");
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
* 0 price - button "Done" enable/disable managed
* */
function showPrice(){

    var inputValue = document.getElementById("mainTabView:formPortfolioAddItem-transaction:add-transaction-amount_hinput").value;
    var buttonDone = document.getElementById("mainTabView:formPortfolioAddItem-transaction:add-transaction-done");

    if (inputValue === null || inputValue === '' || inputValue === '0' )
        buttonDone.classList.add('ui-state-disabled');
    else
        buttonDone.classList.remove('ui-state-disabled');
}

/*
* button "All" on Sell transaction type
* */
function showButtonSellAll(){

    var el = document.getElementById("mainTabView:formPortfolioAddItem-transaction:add-transaction-amount-sell-all");

    if( el && el.style.display === 'block')
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