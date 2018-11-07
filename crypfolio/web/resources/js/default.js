function addWatchCoin() {
    document.getElementById('divWatchAddCoin').focus();

    coinInput = document.getElementById('mainTabView:formWatchAddCoin:watch-add-coin-name_input');
    document.getElementById('mainTabView:formWatchAddCoin:watch-add-coin-name_input').setAttribute("placeholder", coinInput.value);

    coinInput.value = '';
}

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