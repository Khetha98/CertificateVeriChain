"use client";

import { useState } from "react";
import { apiFetch } from "@/lib/api";
import { useRouter } from "next/navigation";
import {useEffect} from "react";

export default function MintCertificatePage() {
  const router = useRouter();
  const [studentName, setStudentName] = useState("");
  const [templateId, setTemplateId] = useState("");

  const [templates, setTemplates] = useState([]);

  useEffect(() => {
    apiFetch("http://localhost:9090/templates")
      .then(r => r.json())
      .then(setTemplates);
  }, []);

  <select>
    {templates.map(t => (
      <option key={t.id} value={t.id}>
        {t.templateName}
      </option>
    ))}
  </select>



  const [loading, setLoading] = useState(false);

  async function submit() {
    setLoading(true);

    const r = await apiFetch("http://localhost:9090/issuer/certificates", {
      method: "POST",
      body: JSON.stringify({ studentName, templateId }),
    });

    setLoading(false);

    if (!r.ok) {
      const text = await r.text();
      console.error("Mint failed:", r.status, text);
      alert("Mint failed");
      return;
    }


    alert("Certificate submitted for approval");
    setStudentName("");
    setTemplateId("");
  }

  return (
    <div className="max-w-xl mx-auto p-10">
      <h1 className="text-2xl font-bold mb-6">Mint Certificate</h1>

      <input
        className="border p-2 w-full mb-3"
        placeholder="Student Name"
        value={studentName}
        onChange={e => setStudentName(e.target.value)}
      />

      <select
        className="border p-2 w-full mb-3"
        value={templateId}
        onChange={e => setTemplateId(e.target.value)}
      >
        <option value="">Select certificate template</option>
        {templates.map(t => (
          <option key={t.id} value={t.id}>
            {t.templateName}
          </option>
        ))}
      </select>

      <button
        onClick={submit}
        className="bg-green-600 text-white px-4 py-2"
      >
        Mint
      </button>
    </div>
  );
}
