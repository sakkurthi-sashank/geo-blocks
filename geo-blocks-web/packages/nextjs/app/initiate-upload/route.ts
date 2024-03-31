import { ProtocolEnum, SpheronClient } from "@spheron/storage";

export async function POST() {
  try {
    const bucketName = "ntf-bucket";
    const protocol = ProtocolEnum.IPFS;

    if (!process.env.SPHERON_TOKEN) {
      throw new Error("SPHERON_TOKEN is not defined");
    }

    const client = new SpheronClient({
      token: process.env.SPHERON_TOKEN,
    });

    const { uploadToken } = await client.createSingleUploadToken({
      name: bucketName,
      protocol,
    });

    console.log(uploadToken);

    return new Response(
      JSON.stringify({
        uploadToken,
      }),
      {
        headers: {
          "Content-Type": "application/json",
        },
      },
    );
  } catch (error) {
    console.error(error);
    return new Response("Internal Server Error", {
      status: 500,
    });
  }
}
