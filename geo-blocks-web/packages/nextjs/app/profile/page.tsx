"use client";

import { useAnonAadhaar } from "@anon-aadhaar/react";
import { AnonAadhaarProof } from "@anon-aadhaar/react";
import GaugeChart from "react-gauge-chart";
import { useAccount } from "wagmi";
import { Balance } from "~~/components/scaffold-eth";

export default function ProfilePage() {
  const { address } = useAccount();
  const [anonAadhaar] = useAnonAadhaar();

  return (
    <div className="max-w-6xl mt-20 w-full bg-gray-950 mx-auto py-10 px-4 sm:px-6 lg:px-8 shadow-lg rounded-xl">
      <h1 className="text-3xl font-bold text-center mb-10 text-gray-100">My Profile</h1>

      <div className="flex flex-col items-center justify-center w-full">
        <h2 className="text-xl font-bold text-gray-100">Address</h2>
        {address}
        <h2 className="text-xl font-bold text-gray-100 mt-8">Balance</h2>
        <Balance address={address} />

        <h2 className="text-xl font-bold text-gray-100 mt-8">AnonAadhaar</h2>
        {anonAadhaar?.status === "logged-in" ? (
          <>
            <p>KYC Status: ✅</p>
            <p>✅ Proof is valid</p>
            <AnonAadhaarProof code={JSON.stringify(anonAadhaar.anonAadhaarProofs, null, 2)} />
          </>
        ) : (
          <p>❌ Proof is not valid</p>
        )}

        <h2 className="text-xl font-bold text-gray-100 mt-8">Credit Score</h2>
        <div className="mt-8 w-40">
          <GaugeChart id="gauge-chart1" percent={0.7} />
        </div>
      </div>
    </div>
  );
}
