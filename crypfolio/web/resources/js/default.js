function addWatchCoin() {
    document.getElementById('divAddCoin').focus();

    coinInput = document.getElementById('mainTabView:formAddCoin:add-coin-name_input');
    document.getElementById('mainTabView:formAddCoin:add-coin-name_input').setAttribute("placeholder", coinInput.value);

    coinInput.value = '';
}
