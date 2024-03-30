"use client";

import Image from "next/image";
import { LogInWithAnonAadhaar, useAnonAadhaar } from "@anon-aadhaar/react";
import type { NextPage } from "next";
import { useEffect } from "react";


const Home: NextPage = () => {
  const [anonAadhaar] = useAnonAadhaar();

    useEffect(() => {
      console.log("Anon Aadhaar status: ", anonAadhaar.status);
    }, [anonAadhaar]);
  return (
    <div className="flex justify-between items-center">
      <div className="flex justify-between items-center mt-32 px-10 space-x-10">
        <div className="">
          <Image
            src="/homepage/map-image.jpeg"
            className="rounded-md overflow-hidden"
            alt="Map"
            width={700}
            height={100}
          />
        </div>
        <div className="flex items-center flex-col space-y-6 justify-center">
          <h1 className="max-w-3xl text-5xl font-bold">
            Discover Limitless Possibilities: Geo Blocks - Navigate Your Way to All-Inclusive{" "}
            <span className="text-indigo-600">Blockchain Services</span>
          </h1>
          <LogInWithAnonAadhaar nullifierSeed={1234} />
        </div>
      </div>
    </div>
  );
};

export default Home;