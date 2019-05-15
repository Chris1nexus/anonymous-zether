var CashToken = artifacts.require("CashToken");

module.exports = function(deployer) {
    deployer.deploy(CashToken);
};
