var BurnVerifier = artifacts.require("BurnVerifier");
var ZetherVerifier = artifacts.require("ZetherVerifier");

module.exports = function(deployer) {
    deployer.deploy(ZetherVerifier);
    deployer.deploy(BurnVerifier);
}
