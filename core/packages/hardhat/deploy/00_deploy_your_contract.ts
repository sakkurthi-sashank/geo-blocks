import { HardhatRuntimeEnvironment } from "hardhat/types";
import { DeployFunction } from "hardhat-deploy/types";

const deployYourContract: DeployFunction = async function (hre: HardhatRuntimeEnvironment) {
  const { deployer } = await hre.getNamedAccounts();
  const { deploy } = hre.deployments;

  // Modify the constructor arguments to match your contract
  const name = "Your NFT Contract";
  const symbol = "YNFT";
  const baseURI = "https://your-base-uri.com/";

  await deploy("YourContract", {
    from: deployer,
    args: [name, symbol, baseURI], // Pass the constructor arguments here
    log: true,
    autoMine: true,
  });
};

export default deployYourContract;
deployYourContract.tags = ["YourContract"];
