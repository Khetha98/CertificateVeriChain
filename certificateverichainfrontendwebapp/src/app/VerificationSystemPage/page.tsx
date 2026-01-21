"use client";

import { useState } from "react";
import { API_BASE_URL } from "../lib/config";

export default function VerificationSystemPage() {
  const [certificateId, setCertificateId] = useState("");
  const [result, setResult] = useState<any>(null);

  async function verify() {
    const res = await fetch(
      `${API_BASE_URL}/api/verify/${certificateId}`
    );

    if (!res.ok) {
      alert("Certificate not found or revoked");
      return;
    }

    const data = await res.json();
    setResult(data);
  }

  return (
    <section className="max-w-xl p-10">
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
        <>

          <div className="mt-6 bg-gray-100 p-4 rounded">
            <p>
              <strong>Status:</strong>{" "}
              {result.valid ? "✅ Valid" : "❌ Invalid"}
            </p>
            <p>
              <strong>Message:</strong> {result.message}
            </p>

            {result.valid && (
              <>
                <p>
                  <strong>Student:</strong> {result.studentName}
                </p>
                <p>
                  <strong>Organization:</strong> {result.organizationName}
                </p>
                <p>
                  <strong>Issued At:</strong> {result.issuedAt}
                </p>
              </>
            )}
          </div>
        </>
      )}
    </section>
  );
}
