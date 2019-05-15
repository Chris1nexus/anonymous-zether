var CashToken = artifacts.require("CashToken");
var BurnVerifier = artifacts.require("BurnVerifier");
var ZetherVerifier = artifacts.require("ZetherVerifier");
var ZSC = artifacts.require("ZSC");

module.exports = function(deployer) {
    var cashToken, burnVerifier;
    CashToken.deployed().then( o => {
        cashToken = o.address;
        return BurnVerifier.deployed();
    }).then( o => {
        burnVerifier = o.address;
        return ZetherVerifier.deployed();
    }).then( o => {
        return deployer.deploy(ZSC, cashToken, o.address, burnVerifier, 3000);
    }).then( o => {
        console.log("===== Migration Complete =====");
    })
}
