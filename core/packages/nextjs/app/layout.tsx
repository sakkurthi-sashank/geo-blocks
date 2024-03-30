'use client';
import "@rainbow-me/rainbowkit/styles.css";
import { AnonAadhaarProvider } from "@anon-aadhaar/react";
import { ScaffoldEthAppWithProviders } from "~~/components/ScaffoldEthAppWithProviders";
import { ThemeProvider } from "~~/components/ThemeProvider";
import "~~/styles/globals.css";


const ScaffoldEthApp = ({ children }: { children: React.ReactNode }) => {
  return (
    <html suppressHydrationWarning>
      <body>
        <AnonAadhaarProvider>
        <ThemeProvider enableSystem>
          <ScaffoldEthAppWithProviders>{children}</ScaffoldEthAppWithProviders>
          </ThemeProvider>
          </AnonAadhaarProvider>
      </body>
    </html>
  );
};

export default ScaffoldEthApp;