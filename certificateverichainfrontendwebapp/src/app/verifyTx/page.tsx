"use client";
import { useState } from "react";
import { API_BASE_URL } from "../lib/config";
import { apiFetch } from "../lib/api";

export default function VerifyTxPage() {

 const [tx,setTx] = useState("");
 const [result,setResult] = useState<any>(null);

 async function verify() {

   const r = await apiFetch(
     `${API_BASE_URL}/verify-chain/tx/${tx}`
   );

   if(!r.ok){
     alert("Tx not found");
     return;
   }

   setResult(await r.json());
 }

 return (
  <div className="max-w-lg mx-auto p-10">

   <h1 className="text-2xl font-bold mb-4">
     On-Chain TX Verification
   </h1>

   <input className="border p-2 w-full mb-4"
     value={tx}
     onChange={e=>setTx(e.target.value)}
     placeholder="Enter Cardano tx hash"
   />

   <button className="bg-blue-600 text-white px-4 py-2 w-full"
     onClick={verify}>
     Verify Metadata
   </button>

   {result && (
    <div className="mt-6 border p-4">
     <p>Valid: {String(result.valid)}</p>
     <p>OnChain Hash: {result.onChainHash}</p>
     <p>DB Hash: {result.dbHash}</p>
    </div>
   )}

  </div>
 );
}
