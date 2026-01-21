"use client";

import { useEffect, useState } from "react";
import { apiFetch } from "@/lib/api";
import { useRouter } from "next/navigation";

export default function InstitutionDashboard() {
  const [certs, setCerts] = useState<any[]>([]);
  const router = useRouter();

  // fetchCerts can still be used for buttons
  const fetchCerts = async () => {
    try {
      const r = await apiFetch("http://localhost:9090/issuer/certificates");

      if (!r.ok) {
        alert("Failed to load certificates");
        return;
      }

      const text = await r.text(); // 👈 SAFE
      const data = text ? JSON.parse(text) : [];
      setCerts(data);

    } catch (err) {
      console.error(err);
      alert("Failed to load certificates");
    }
  };


  useEffect(() => {
    // wrap async call inside effect
    const load = async () => {
      await fetchCerts();
    };
    load();
  }, []);

  const handleRevoke = async (uid: string) => {
    const res = await apiFetch(
      `http://localhost:9090/issuer/certificates/${uid}/revoke`,
      { method: "POST" }
    );
    if (!res.ok) {
      alert("Failed to revoke certificate");
      return;
    }
    await fetchCerts();
  };

  const handleDelete = async (uid: string) => {
    const confirmed = confirm("Are you sure you want to delete this failed certificate?");
    if (!confirmed) return;

    const res = await apiFetch(
      `http://localhost:9090/issuer/certificates/${uid}`,
      { method: "DELETE" }
    );

    if (!res.ok) {
      alert("Failed to delete certificate");
      return;
    }

    await fetchCerts();
  };

  const successfulCerts = certs.filter(c => c.status === "ACTIVE");
  const failedCerts = certs.filter(c => c.status !== "ACTIVE");

  return (
    <div className="p-10 max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold mb-6">Organization Dashboard</h1>

      <button
        onClick={() => router.push("/mint")}
        className="bg-green-600 text-white px-4 py-2 mb-4"
      >
        Mint New Certificate
      </button>

      {/* Active Certificates */}
      <h2 className="text-xl font-semibold mb-4">Active Certificates</h2>
      {successfulCerts.map(c => (
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

      {/* Failed Certificates */}
      <h2 className="text-xl font-semibold mt-6 mb-2">Failed Certificates</h2>
      {failedCerts.map(c => (
        <div
          key={c.certificateUid}
          className="border p-3 mb-3 flex justify-between"
        >
          <span>{c.studentName}</span>
          <button
            className="bg-gray-600 text-white px-3 py-1"
            onClick={() => handleDelete(c.certificateUid)}
          >
            Delete
          </button>
        </div>
      ))}
    </div>
  );
}
