import { useAccount } from "wagmi";
import { useScaffoldContractRead } from "~~/hooks/scaffold-eth/useScaffoldContractRead";

const NFTCard = ({ id }: { id: bigint }) => {
  const { data } = useScaffoldContractRead({
    contractName: "MyContract",
    functionName: "tokenURI",
    args: [BigInt(id)],
  });

  return (
    <div className="max-w-sm rounded overflow-hidden shadow-lg">
      <img className="w-full max-h-52" src={data} alt={`NFT ${id}`} />
      <div className="px-6 py-4">
        <div className="font-bold text-xl mb-2">NFT #{id.toString()}</div>
        <p className="text-gray-700 text-base">This is a unique NFT in the collection.</p>
      </div>
    </div>
  );
};

const NFTDisplayPage = () => {
  const { address: connectedAddress } = useAccount();

  const { data } = useScaffoldContractRead({
    contractName: "MyContract",
    functionName: "balanceOf",
    args: [connectedAddress],
  });

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-12">
        <h1 className="text-4xl font-bold text-center mb-12">My NFT Collection</h1>
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {Array.from({ length: Number(data) })?.map((_, i) => (
            <NFTCard key={i} id={BigInt(i)} />
          ))}
        </div>
      </div>
    </div>
  );
};

export default NFTDisplayPage;
