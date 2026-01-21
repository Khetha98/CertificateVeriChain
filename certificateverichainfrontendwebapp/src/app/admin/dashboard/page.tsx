"use client";

import { useEffect, useState } from "react";
import { apiFetch } from "@/app/lib/api";

export default function AdminDashboard() {
  const [institutions, setInstitutions] = useState([]);

  useEffect(() => {
    apiFetch("/admin/institutions/pending")
      .then(res => res.json())
      .then(setInstitutions);
  }, []);

  async function approve(id: number) {
    await apiFetch(`/admin/institutions/${id}/approve`, { method: "POST" });
    setInstitutions(prev => prev.filter((i:any) => i.id !== id));
  }

  async function reject(id: number) {
    await apiFetch(`/admin/institutions/${id}/reject`, { method: "POST" });
    setInstitutions(prev => prev.filter((i:any) => i.id !== id));
  }

  return (
    <div className="p-10">
      <h1 className="text-2xl font-bold mb-6">Pending Institutions</h1>

      {institutions.map((org:any) => (
        <div key={org.id} className="border p-4 mb-4">
          <p><b>{org.name}</b></p>
          <p>{org.website}</p>

          <div className="flex gap-2 mt-3">
            <button onClick={() => approve(org.id)} className="bg-green-600 text-white px-4 py-1">
              Approve
            </button>
            <button onClick={() => reject(org.id)} className="bg-red-600 text-white px-4 py-1">
              Reject
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}
