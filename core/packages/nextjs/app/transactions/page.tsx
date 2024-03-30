"use client";

import { useQuery } from "@apollo/client";
import { gql } from "@apollo/client";

export default function WritePage() {
  const GET_ITEMS = gql`
    query MyQuery {
      transfers {
        blockNumber
        blockTimestamp
        from
        id
        to
        tokenId
        transactionHash
      }
    }
  `;

  const { loading, error, data } = useQuery(GET_ITEMS);

  if (loading)
    return (
      <div className="flex h-screen w-full items-center justify-center">
        <div
          className="h-32 w-32 animate-spin rounded-full
                    border border-solid border-primary border-t-transparent"
        ></div>
      </div>
    );

  if (error) return <div>Error: {error.message}</div>;

  return (
    <div className="p-3">
      <h3 className="text-2xl text-gray-800 font-bold px-5 py-2.5">On Chain Transactions</h3>
      <div className="grid grid-cols-1 gap-4 p-4">
        {data.transfers.map((transfer: any) => (
          <div className="card max-w-2xl w-full space-y-3 bg-base-100 shadow-xl p-4" key={transfer.id}>
            <div>
              <span className="text-gray-600">Block Number :</span> {transfer.blockNumber}
            </div>
            <div>
              <span className="text-gray-600">Block Timestamp : </span>
              {transfer.blockTimestamp}
            </div>
            <div>
              <span className="text-gray-600">From : </span> {transfer.from}
            </div>
            <div>
              <span className="text-gray-600">To : </span> {transfer.to}
            </div>
            <div>
              <span className="text-gray-600">Token ID : </span> {transfer.tokenId}
            </div>
            <div>
              <span className="text-gray-600">Transaction Hash: </span>
              {transfer.transactionHash}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
