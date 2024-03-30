"use client";

import { Avatar } from "@readyplayerme/visage";
import { useEffect, useState } from "react";

export default function AvatarPage() {
  const [avatar, setAvatar] = useState("");

  useEffect(() => {
    setAvatar("https://models.readyplayer.me/66080b7b2aa392635c4445d2.glb");
  }, []);

  return (
    <div>
      {avatar ? (
        <Avatar
          modelSrc={
            "https://models.readyplayer.me/66080b7b2aa392635c4445d2.glb"
          }
          style={{
            width: "100%",
            height: "100%",
            minHeight: "10vh",
            minWidth: "10vw",
            position: "absolute",
            top: "0",
            left: "0",
          }}
        />
      ) : (
        <div>Loading...</div>
      )}
    </div>
  );
}
