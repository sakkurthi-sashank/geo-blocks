export default function ProfilePage() {
  return (
    <div className="max-w-4xl mt-20 w-full bg-gray-950 mx-auto py-10 px-4 sm:px-6 lg:px-8 shadow-lg rounded-xl">
      <h1 className="text-3xl font-bold text-center mb-10 text-gray-100">My Profile</h1>
      <div className="flex flex-col items-center space-y-6 max-w-xl mx-auto">
        <div className="flex flex-row items-center space-x-4">
          <div className="text-gray-100 font-semibold text-lg">Connected Address:</div>
          <div className="text-gray-400 font-semibold text-lg">0x1234...5678</div>
        </div>
        <div className="flex flex-row items-center space-x-4">
          <div className="text-gray-100 font-semibold text-lg">Balance:</div>
          <div className="text-gray-400 font-semibold text-lg">1000 ETH</div>
        </div>
        <div className="flex flex-row items-center space-x-4">
          <div className="text-gray-100 font-semibold text-lg">NFTs Owned:</div>
          <div className="text-gray-400 font-semibold text-lg">5</div>
        </div>
      </div>
    </div>
  );
}
