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
* modal window Add/edit Item/transaction - to enable/disable showing of the button "Done"
 * depends of the correct input values in the form
* */
function enableDoneButton() {

    var buttonDone = document.getElementById("mainTabView:formPortfolioAddItem-transaction:add-transaction-done");
    var buttonDoneEditTransaction = document.getElementById(
        "mainTabView:formPortfolioAddItem-transaction:add-transaction-done-edit-transaction");

    var inputAmountValue = document.getElementById("mainTabView:formPortfolioAddItem-transaction:add-transaction-amount_hinput").value;
    var inputPriceValue = document.getElementById("mainTabView:formPortfolioAddItem-transaction:add-transaction-price_hinput").value;
    var inputTotalValue = document.getElementById("mainTabView:formPortfolioAddItem-transaction:add-transaction-total_hinput").value;

    // old version of the condition: if (Number(inputAmountValue) > 0 && inputPriceValue !== "" && inputTotalValue !== "")
    if (buttonDone && Number(inputAmountValue) > 0 && Number(inputPriceValue) > 0 && Number(inputTotalValue) > 0)
        buttonDone.classList.remove('ui-state-disabled');

    else if (buttonDoneEditTransaction && Number(inputAmountValue) > 0 && Number(inputPriceValue) > 0 && Number(inputTotalValue) > 0)
        buttonDoneEditTransaction.classList.remove('ui-state-disabled');

    else {
        if (buttonDone)
            buttonDone.classList.add('ui-state-disabled');
        else if (buttonDoneEditTransaction)
            buttonDoneEditTransaction.classList.add('ui-state-disabled');
    }
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
* remove highlight background color of selected row after closed dialog window
* && scroll up display to top of the page
* */
function portfolioDataTableRowUnselect() {
    // https://stackoverflow.com/questions/22270664/how-to-remove-a-class-from-elements-in-pure-javascript

    var selectedRows = document.querySelectorAll(".ui-datatable-selectable.ui-state-highlight");
    [].forEach.call(selectedRows, function (el) {
        el.classList.remove("ui-state-highlight");
    });
    selectedRows = document.querySelectorAll(".ui-datatable-selectable.ui-state-hover");
    [].forEach.call(selectedRows, function (el) {
        el.classList.remove("ui-state-hover");
    });

    window.scrollTo(0, 0);
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

/*
* fixing problem with duplicated datatable's sticky header on change tabs
* see: https://github.com/primefaces/primefaces/issues/960
* */
function refreshStickyHeader(widgetVars) {
    var widgetArray = widgetVars.split(',');

    for (var i = 0; i < widgetArray.length; i++) {

        var tableWidget_ID = widgetArray[i];

        var $this = PF(tableWidget_ID);

        if ($this) {

            var stickyContainer = $this.jq.find('.ui-datatable-sticky');

            if (stickyContainer) {

                stickyContainer.css('display', 'none');

                stickyContainer.css('width', $this.jq.width());
            }
        }
    }
}

/*
* edit the title of the modal window DlgPortfolioAddItem
* depends is it add item, add transaction or edit transaction
* */
function changeDlgPortfolioAddItemTitle(name) {

    var valTitle = document.getElementById("mainTabView:dlgPortfolioAddItem_title");

    if (valTitle != null) {

        if (name === "addTransaction") {

            valTitle.innerText = "Add Transaction";

        } else if (name === "editTransaction") {

            valTitle.innerText = "Edit Transaction";

        } else {

            valTitle.innerText = "Add item to Portfolio";
        }
    }
}