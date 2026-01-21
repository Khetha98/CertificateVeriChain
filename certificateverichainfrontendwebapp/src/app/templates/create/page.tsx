"use client";

import { useState } from "react";
import { apiFetch } from "@/lib/api";
import { useEffect } from "react";

export default function TemplateCreate() {
  const [name, setName] = useState("");
  const [desc, setDesc] = useState("");
  const [file, setFile] = useState<File | null>(null);
    const [templates, setTemplates] = useState([]);

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

    useEffect(() => {
      const loadTemplates = async () => {
        try {
          const r = await apiFetch("http://localhost:9090/templates");

          if (!r.ok) {
            setTemplates([]);
            return;
          }

          const text = await r.text();
          setTemplates(text ? JSON.parse(text) : []);
        } catch (e) {
          console.error("Failed to load templates", e);
          setTemplates([]);
        }
      };

      loadTemplates();
    }, []);


    const deleteTemplate = async (uid: string) => {
      const confirmed = confirm("Are you sure you want to delete this template?");
      if (!confirmed) return;

      const res = await apiFetch(
        `http://localhost:9090/templates/${uid}`,
        { method: "DELETE" }
      );

      if (!res.ok) {
        alert("Failed to delete certificate");
        return;
      }

    };

    const viewTemplate = async (id: number) => {
    const token = localStorage.getItem("token"); // same as apiFetch
    const res = await fetch(`http://localhost:9090/templates/${id}/view`, {
      headers: { Authorization: `Bearer ${token}` }
    });

    if (!res.ok) {
      alert("Failed to load template");
      return;
    }

    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    window.open(url, "_blank");
  };


return (
  <div className="p-6 max-w-3xl mx-auto space-y-10">
    
    {/* CREATE TEMPLATE FORM */}
    <div className="border rounded-lg p-6 space-y-4">
      <h2 className="text-xl font-semibold">Create Certificate Template</h2>

      <input
        className="border p-2 w-full"
        placeholder="Template name"
        onChange={e => setName(e.target.value)}
      />

      <input
        className="border p-2 w-full"
        placeholder="Description"
        onChange={e => setDesc(e.target.value)}
      />

      <input
        className="border p-2 w-full"
        type="file"
        accept="application/pdf"
        onChange={e => setFile(e.target.files?.[0] ?? null)}
      />

      <button
        className="bg-blue-600 text-white px-4 py-2 rounded"
        onClick={submit}
      >
        Create Template
      </button>
    </div>

    {/* TEMPLATE LIST */}
    <div className="space-y-4">
      <h2 className="text-xl font-semibold">Your Templates</h2>

      {templates.map((t: any) => (
        <div
          key={t.id}
          className="border rounded-lg p-4 flex justify-between items-center"
        >
          <div>
            <p className="font-medium">{t.templateName}</p>
            <p className="text-sm text-gray-600">{t.description}</p>
          </div>

          <div className="flex gap-4">
          <button
            onClick={() => viewTemplate(t.id)}
            className="text-blue-600 underline"
          >
            View
          </button>

            <button
              onClick={() => deleteTemplate(t.id)}
              className="text-red-600"
            >
              Delete
            </button>
          </div>
        </div>
      ))}
    </div>

  </div>
);

}
