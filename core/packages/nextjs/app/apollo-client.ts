import { ApolloClient, InMemoryCache } from "@apollo/client";

const client = new ApolloClient({
  uri: "https://api.studio.thegraph.com/query/69553/eth-mumbai/0.1", // Replace with your API URL
  cache: new InMemoryCache(),
});

export default client;
