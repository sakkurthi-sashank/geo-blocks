import { useScaffoldContractRead } from "~~/hooks/scaffold-eth/useScaffoldContractRead";

const NFTDisplayPage = () => {
  const { data } = useScaffoldContractRead({
    contractName: "YourContract",
    functionName: "listAllNFTs",
  });
  console.log(data);

  return (
    <div className="min-h-screen">
      <div className="container mx-auto px-4 py-12">
        <h1 className="text-4xl font-bold text-center mb-12">My NFT Collection</h1>
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {data?.map(nft => (
            <div key={nft.tokenId} className="bg-gray-950 rounded-lg shadow-md p-6">
              <img src={nft.imageURL} alt={nft.title} className="w-full h-48 object-cover mb-6" />
              <h2 className="text-lg font-semibold">{nft.title}</h2>
              <p className="text-gray-500 mb-4">{nft.description}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default NFTDisplayPage;
