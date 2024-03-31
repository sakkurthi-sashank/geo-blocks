import { HardhatRuntimeEnvironment } from "hardhat/types";
import { DeployFunction } from "hardhat-deploy/types";
import { Contract } from "ethers";

const deployYourContract: DeployFunction = async function (hre: HardhatRuntimeEnvironment) {
  const { deployer } = await hre.getNamedAccounts();
  const { deploy } = hre.deployments;

  await deploy("YourContract", {
    from: deployer,
    args: [], // No constructor arguments needed as per the updated contract
    log: true,
  });

  // Get the deployed contract to interact with it after deploying.
  const yourContract = await hre.ethers.getContract<Contract>("YourContract", deployer);

  // Interact with the deployed contract here if needed
  // Mint NFTs if required
  const tokenId = await yourContract.mintNFT("Title", "Description", "ImageURL", deployer);
  console.log("NFT minted with tokenId:", tokenId.toString());
};

export default deployYourContract;
deployYourContract.tags = ["YourContract"];
