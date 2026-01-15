"use client";

import { useEffect, useState } from "react";
import { apiFetch } from "@/lib/api";
import { useRouter } from "next/navigation";

export default function InstitutionDashboard() {
  const [certs, setCerts] = useState<any[]>([]);

  const router = useRouter();

  async function load() {
    const r = await apiFetch("http://localhost:9090/issuer/certificates");

    if (!r.ok) {
      alert("Failed to load certificates");
      return;
    }

    setCerts(await r.json());
  }

  useEffect(() => {
    load();
  }, []);

  const handleRevoke = async (uid: string) => {
    await apiFetch(
      `http://localhost:9090/issuer/certificates/${uid}/revoke`,
      { method: "POST" }
    );
    load();
  };

  return (
    <div className="p-10 max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold mb-6">Organization Dashboard</h1>

      <button
        onClick={() => router.push("/mint")}
        className="bg-green-600 text-white px-4 py-2 mb-4"
      >
        Mint New Certificate
      </button>

      <h2 className="text-xl font-semibold mb-4">Minted Certificates</h2>

      {certs.map(c => (
        <div
          key={c.certificateUid}
          className="border p-3 mb-3 flex justify-between"
        >
          <span>{c.studentName}</span>
          <button
            className="bg-red-600 text-white px-3 py-1"
            onClick={() => handleRevoke(c.certificateUid)}
          >
            Revoke
          </button>
        </div>
      ))}

    </div>
  );
}
