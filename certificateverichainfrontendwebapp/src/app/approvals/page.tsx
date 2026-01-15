"use client";

import { useEffect, useState } from "react";
import { apiFetch } from "@/lib/api";

export default function ApprovalsPage() {
  const [items, setItems] = useState([]);

  useEffect(() => {
    apiFetch("http://localhost:9090/approvals/pending")
      .then(r => r.json())
      .then(setItems);
  }, []);

  async function approve(uid: number) {
    await apiFetch(`http://localhost:9090/issuer/certificates/${uid}/approve`, {
      method: "POST"
    });
    setItems(items.filter(i => i.certificateUid !== uid));
  }

  return (
    <div className="max-w-4xl mx-auto p-8">
      <h1 className="text-2xl font-bold mb-6">Pending Approvals</h1>

      {items.length === 0 && <p>No pending approvals 🎉</p>}

      {items.map(a => (
        <div key={a.certificateUid} className="border p-4 mb-3 rounded">
          <div className="font-semibold">{a.studentName}</div>
          <div className="text-sm text-gray-600">
            Template: {a.templateName} · Issued by {a.issuerName}
          </div>

          <button
            onClick={() => approve(a.certificateUid)}
            className="mt-3 bg-blue-600 text-white px-4 py-1 rounded"
          >
            Approve
          </button>
        </div>
      ))}
    </div>
  );
}

