"use client";

import { ChangeEvent, useState } from "react";
import { upload } from "@spheron/browser-upload";
import { useAccount } from "wagmi";
import { useScaffoldContractWrite } from "~~/hooks/scaffold-eth/useScaffoldContractWrite";

export default function MintNFTPage() {
  const [file, setFile] = useState<File | null>(null);
  const [isNFTLoading, setIsNFTLoading] = useState(false);
  const [uploadLink, setUploadLink] = useState<string>("");
  const [dynamicLink, setDynamicLink] = useState<string>("");
  const { address: connectedAddress } = useAccount();

  const { writeAsync, isLoading, isMining } = useScaffoldContractWrite({
    contractName: "MyContract",
    functionName: "mintTo",
    args: [connectedAddress, `${uploadLink}/${file?.name}`],
    blockConfirmations: 1,
    onBlockConfirmation: txnReceipt => {
      console.log("Transaction blockHash", txnReceipt.blockHash);
    },
  });

  const handleUpload = async () => {
    if (!file) {
      alert("No file selected");
      return;
    }

    try {
      setIsNFTLoading(true);
      const response = await fetch("/initiate-upload", {
        method: "POST",
      });
      const responseJson = await response.json();
      const uploadResult = await upload([file], {
        token: responseJson.uploadToken,
      });

      setUploadLink(uploadResult.protocolLink);
      setDynamicLink(uploadResult.dynamicLinks[0]);
    } catch (err) {
      alert(err);
    } finally {
      setIsNFTLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto py-10 px-4 sm:px-6 lg:px-8">
      <h1 className="text-3xl font-bold text-center mb-10">Mint My NFT</h1>
      <div className="flex flex-col items-center">
        <input
          className="mb-4 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-violet-50 file:text-violet-700 hover:file:bg-violet-100"
          type="file"
          onChange={(e: ChangeEvent<HTMLInputElement>) => {
            if (e.target.files) {
              setFile(e.target.files[0]);
            }
          }}
        />
        <button
          className={`mb-4 px-6 py-2 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 ${
            isNFTLoading ? "opacity-50 cursor-not-allowed" : ""
          }`}
          onClick={handleUpload}
          disabled={isNFTLoading}
        >
          Upload
        </button>
        <div className="mb-4">
          <p>Upload Link: {uploadLink}</p>
          <p>Dynamic Link: {dynamicLink}</p>
          <a href={`${uploadLink}/${file?.name}`} className="text-blue-500 hover:text-blue-700">
            {file?.name}
          </a>
        </div>

        <button
          className={`px-6 py-2 border border-transparent text-base font-medium rounded-md text-white bg-green-600 hover:bg-green-700 ${
            isNFTLoading || isLoading || isMining ? "opacity-50 cursor-not-allowed" : ""
          }`}
          onClick={async () => {
            await writeAsync();
          }}
          disabled={isNFTLoading || isLoading || isMining}
        >
          Mint
        </button>
      </div>
    </div>
  );
}
