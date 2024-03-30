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
  const [nftName, setNftName] = useState<string>("");
  const [nftDescription, setNftDescription] = useState<string>("");
  const { address: connectedAddress } = useAccount();

  const { writeAsync, isLoading, isMining } = useScaffoldContractWrite({
    contractName: "MyContract",
    functionName: "mintTo",
    // args: [`${dynamicLink}/${file?.name}`],
    args: [
      connectedAddress,
      "https://bafybeibkfuq5rqe6ztuaqtvblyj34y3ja2rxetmu2aycv474oizk7c47nu.ipfs.sphn.link/_d10f8daf-b1bf-4389-9054-350859230ebf.jpeg",
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
    <div>
      <h1>Mint NFT</h1>
      <input
        type="file"
        onChange={(e: ChangeEvent<HTMLInputElement>) => {
          if (e.target.files) {
            setFile(e.target.files[0]);
          }
        }}
      />
      <button onClick={handleUpload} disabled={isNFTLoading}>
        Upload
      </button>
      <div>
        <p>Upload Link: {uploadLink}</p>
        <p>Dynamic Link: {dynamicLink}</p>

        <a href={`${uploadLink}/${file?.name}`}>{file?.name}</a>
      </div>
      <input type="text" placeholder="NFT Name" value={nftName} onChange={e => setNftName(e.target.value)} />
      <input
        type="text"
        placeholder="NFT Description"
        value={nftDescription}
        onChange={e => setNftDescription(e.target.value)}
      />

      <button
        onClick={async () => {
          await writeAsync();
        }}
        disabled={isNFTLoading || isLoading || isMining}
      >
        Mint
      </button>
    </div>
  );
}
