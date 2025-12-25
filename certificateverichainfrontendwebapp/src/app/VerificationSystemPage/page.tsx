"use client";

import { useState } from "react";

export default function VerificationSystemPage() {
  const [certificateId, setCertificateId] = useState("");
  const [result, setResult] = useState<any>(null);

  async function verify() {
    const res = await fetch(
      `http://localhost:8080/api/verify/${certificateId}`
    );
    const data = await res.json();
    setResult(data);
  }

  return (
    <section className="max-w-5xl mx-auto p-10">
      <h1 className="text-3xl font-bold mb-6">Verify Certificate</h1>

      <input
        value={certificateId}
        onChange={(e) => setCertificateId(e.target.value)}
        placeholder="Enter Certificate ID"
        className="border p-2 w-full"
      />

      <button
        onClick={verify}
        className="mt-4 bg-blue-600 text-white px-4 py-2 rounded"
      >
        Verify
      </button>

      {result && (
        <pre className="mt-6 bg-gray-100 p-4 rounded">
          {JSON.stringify(result, null, 2)}
        </pre>
      )}
    </section>
  );
}
