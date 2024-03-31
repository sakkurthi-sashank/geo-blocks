"use client";

import React from "react";
import client from "../apollo-client";
import { ApolloProvider } from "@apollo/client";

export default function WriteLayout({ children }: { children: React.ReactNode }) {
  return (
    <div>
      <ApolloProvider client={client}>{children}</ApolloProvider>
    </div>
  );
}
