"use client";

import { Avatar } from "@readyplayerme/visage";
import { useEffect, useState } from "react";

export default function AvatarPage() {
  const [avatar, setAvatar] = useState("");

  useEffect(() => {
    setAvatar("https://models.readyplayer.me/6607e85d75cd33aacce08a77.glb");
  }, []);

  return (
    <div>
      {avatar ? (
        <Avatar
          modelSrc={
            "https://models.readyplayer.me/6607e85d75cd33aacce08a77.glb"
          }
          style={{
            width: "100%",
            height: "100%",
            minHeight: "100vh",
            minWidth: "100vw",
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
