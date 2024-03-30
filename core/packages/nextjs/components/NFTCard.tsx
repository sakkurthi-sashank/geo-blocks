// Dummy NFT data
const nfts = [
  {
    id: 1,
    title: "NFT Title #1",
    artist: "Artist Name",
    price: "2.5 ETH",
    image:
      "https://dgbijzg00pxv8.cloudfront.net/0d336c68-834d-4ba5-b2d0-2979ace11ae2/000000-0000000000/59132355562186324700850821844442436394047262304927053951102538847327114254721/ITEM_PREVIEW1.gif",
  },
  {
    id: 2,
    title: "NFT Title #2",
    artist: "Artist Name",
    price: "3.0 ETH",
    image:
      "https://dgbijzg00pxv8.cloudfront.net/de2df7a8-de3f-4f7c-a7e6-267e3e9ef221/000000-0000000000/27964339865592756430076624915131963380151013737352726385027989693596440858920/ITEM_PREVIEW1.gif",
  },
  // Add more NFTs as needed
];

const NFTDisplayPage = () => {
  return (
    <div className="min-h-screen">
      <div className="mx-auto px-4 py-12">
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {nfts.map(nft => (
            <div key={nft.id} className="rounded-lg overflow-hidden shadow-sm border border-gray-300">
              <img src={nft.image} alt={nft.title} className="w-full" />
              <div className="p-4">
                <h3 className="text-xl font-bold">{nft.title}</h3>
                <p className="text-gray-400">{nft.artist}</p>
                <div className="flex justify-between items-center mt-4">
                  <span className="bg-blue-500 py-1 px-3 rounded-full text-xs">{nft.price}</span>
                  <button className="bg-green-500 hover:bg-green-700 font-bold py-2 px-4 rounded">Buy</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default NFTDisplayPage;
