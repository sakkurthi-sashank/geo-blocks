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
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");

  const { writeAsync, isLoading, isMining } = useScaffoldContractWrite({
    contractName: "MyContract",
    functionName: "mintTo",
    args: [
      connectedAddress,
      "https://bafybeibzuo5ljwi47ob75kbvzlj2hykjqqpvtmmz3lrai77ley5laeevfy.ipfs.sphn.link/WhatsApp%20Image%202024-03-31%20at%207.47.24%20AM%20%281%29.jpeg",
    ],
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
    <div className="max-w-4xl mt-20 w-full bg-gray-950 mx-auto py-10 px-4 sm:px-6 lg:px-8 shadow-lg rounded-xl">
      <h1 className="text-3xl font-bold text-center mb-10 text-gray-100">Mint My NFT</h1>
      <div className="flex flex-col items-center space-y-6 max-w-xl mx-auto">
        <input
          className="mb-4 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-gray-50 file:text-gray-700 hover:file:bg-gray-100"
          type="file"
          onChange={(e: ChangeEvent<HTMLInputElement>) => {
            if (e.target.files) {
              setFile(e.target.files[0]);
            }
          }}
        />
        <input
          className="mb-4 w-full py-4 px-4 rounded-lg border-0 font-semibold bg-gray-900"
          type="input"
          value={title}
          placeholder="Title"
          onChange={e => setTitle(e.target.value)}
        />
        <input
          className="mb-4 w-full py-4 px-4 rounded-lg border-0 font-semibold bg-gray-900"
          type="input"
          value={description}
          placeholder="Description"
          onChange={e => setDescription(e.target.value)}
        />

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
          onClick={handleUpload}
          disabled={isNFTLoading || isLoading || isMining}
        >
          Upload File
        </button>

        <button
          className={`px-6 py-2 border border-transparent text-base font-medium rounded-md text-white bg-green-600 hover:bg-green-700 ${
            isNFTLoading || isLoading || isMining ? "opacity-50 cursor-not-allowed" : ""
          }`}
          onClick={async () => {
            console.log(uploadLink + "/" + file?.name);
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
