"use client";

import { useState } from "react";
import { apiFetch } from "@/lib/api";

export default function TemplateCreate() {
  const [name, setName] = useState("");
  const [desc, setDesc] = useState("");
  const [file, setFile] = useState<File | null>(null);

  async function submit() {
    if (!file) {
      alert("Please upload a PDF");
      return;
    }

    const form = new FormData();
    form.append("name", name);
    form.append("description", desc);
    form.append("file", file);

    const r = await apiFetch("http://localhost:9090/templates", {
      method: "POST",
      body: form
    });

    if (!r.ok) {
      alert("Failed");
      return;
    }

    alert("Template created");
  }

  return (
    <div className="p-6">
      <input placeholder="Template name" onChange={e => setName(e.target.value)} />
      <input placeholder="Description" onChange={e => setDesc(e.target.value)} />

      <input
        type="file"
        accept="application/pdf"
        onChange={e => setFile(e.target.files?.[0] ?? null)}
      />

      <button onClick={submit}>Create Template</button>
    </div>
  );
}
