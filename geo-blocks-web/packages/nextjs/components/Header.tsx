"use client";

import React from "react";
import Link from "next/link";
import { RainbowKitCustomConnectButton } from "~~/components/scaffold-eth";

export const Header = () => {
  type HeaderMenuLink = {
    label: string;
    href: string;
    icon?: React.ReactNode;
  };

  const menuLinks: HeaderMenuLink[] = [
    {
      label: "Home",
      href: "/",
    },
    {
      label: "Debug",
      href: "/debug",
    },
    {
      label: "Transactions",
      href: "/transactions",
    },
    {
      label: "My NFTs",
      href: "/my-nfts",
    },
    {
      label: "Mint NFT",
      href: "/mint-nft",
    },
    {
      label: "My Profile",
      href: "/profile",
    },
  ];

  return (
    <div className="bg-gray-950 h-14 flex justify-between px-3">
      <div className="flex-row items-center justify-center text-gray-600 w-full hidden md:flex">
        {menuLinks.map(link => (
          <Link
            key={link.label}
            href={link.href}
            className="text-gray-400 hover:text-white px-3 py-2 rounded-md text-sm font-medium"
          >
            {link.label}
          </Link>
        ))}
      </div>

      <div className="flex flex-row items-center text-gray-600 justify-end w-full">
        <RainbowKitCustomConnectButton />
      </div>
    </div>
  );
};
