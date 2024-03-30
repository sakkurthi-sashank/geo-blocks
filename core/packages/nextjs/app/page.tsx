"use client";

import Image from "next/image";
import type { NextPage } from "next";

const Home: NextPage = () => {
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
            <span className="text-indigo-600">Blockchain Serveices</span>
          </h1>
          <button className="btn btn-info">Get Started Now</button>
        </div>
      </div>
    </div>
  );
};

export default Home;
