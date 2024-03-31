"use client";

import { useEffect } from "react";
import Image from "next/image";
import { LogInWithAnonAadhaar, useAnonAadhaar } from "@anon-aadhaar/react";
import type { NextPage } from "next";

const Home: NextPage = () => {
  const [anonAadhaar] = useAnonAadhaar();

  useEffect(() => {
    console.log("Anon Aadhaar status: ", anonAadhaar.status);
  }, [anonAadhaar]);
  return (
    <div className="flex justify-between h-[90vh] items-center">
      <div className="flex flex-wrap xl:flex-nowrap justify-center w-full items-center space-x-10 px-10">
        <div className="flex w-full justify-center">
          <Image
            src="/mobile-map.jpg"
            alt="Geo Blocks"
            width={300}
            height={200}
            className="rounded-md overflow-hidden"
          />
        </div>
        <div className="w-full flex justify-center items-center flex-col">
          <h1 className="text-center mt-10 scroll-m-20 max-w-5xl text-4xl font-extrabold tracking-tight lg:text-5xl">
            Discover Limitless Possibilities: Geo Blocks - Navigate Your Way to All-Inclusive{" "}
            <span className="text-indigo-600">Blockchain Services</span>
          </h1>
          <div className="mt-20">
            <LogInWithAnonAadhaar nullifierSeed={1234} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
