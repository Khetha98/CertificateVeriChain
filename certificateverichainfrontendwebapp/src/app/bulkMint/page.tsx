"use client";
import { useState } from "react";
import { apiFetch } from "../auth/login/page";

export default function BulkMintPage() {

 const [path,setPath] = useState("");
 const [log,setLog] = useState<any>(null);

 async function mint(){

   const r = await apiFetch(
     "http://localhost:9090/issuer/bulk/mint",
     { method:"POST", body: JSON.stringify(path)}
   );

   if(r.ok)
     setLog(await r.json());
 }

 return (
  <div className="max-w-lg mx-auto p-10">

   <h1 className="text-2xl font-bold mb-4">
     Bulk CSV Minting
   </h1>

   <input className="border p-2 w-full mb-4"
     value={path}
     onChange={e=>setPath(e.target.value)}
     placeholder="/home/you/certs.csv"
   />

   <button className="bg-purple-600 text-white px-4 py-2 w-full"
     onClick={mint}>
     Start Bulk Mint
   </button>

   {log && (
    <div className="mt-6 border p-4">
     <pre>{JSON.stringify(log,null,2)}</pre>
    </div>
   )}

  </div>
 );
}
