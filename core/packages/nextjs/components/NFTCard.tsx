import { useAccount } from "wagmi";
import { useScaffoldContractRead } from "~~/hooks/scaffold-eth/useScaffoldContractRead";

const NFTDisplayPage = () => {
  const { address: connectedAddress } = useAccount();

  const { data } = useScaffoldContractRead({
    contractName: "YourContract",
    functionName: "listAllNFTs",
  });
  console.log(data);

  //   const data: readonly {
  //     tokenId: bigint;
  //     title: string;
  //     description: string;
  //     imageURL: string;
  // }[] | undefined

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-12">
        <h1 className="text-4xl font-bold text-center mb-12">My NFT Collection</h1>
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        </div>
      </div>
    </div>
  );
};

export default NFTDisplayPage;