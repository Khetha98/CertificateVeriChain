"use client";

import { useState, useEffect } from "react";
import { apiFetch } from "@/lib/api";
import jsPDF from "jspdf";

interface Template {
  id: number;
  templateName: string;
  certificateTypes: string[]; // list of allowed certificate types
}

export default function BulkMintPage() {
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<any>(null);

  const [templates, setTemplates] = useState<Template[]>([]);
  const [selectedTemplateId, setSelectedTemplateId] = useState<number | "">("");
  const [selectedCertificateType, setSelectedCertificateType] = useState<string>("");
  //const [certificateType, setCertificateType] = useState("");

  const [availableCertificateTypes, setAvailableCertificateTypes] = useState<string[]>([]);

  // Load templates from backend
  useEffect(() => {
    async function loadTemplates() {
      try {
        const res = await apiFetch("http://localhost:9090/templates");
        if (!res.ok) return;

        const data: Template[] = await res.json();
        setTemplates(data);
      } catch (err) {
        console.error("Failed to load templates", err);
        setTemplates([]);
      }
    }
    loadTemplates();
  }, []);

  // Update available certificate types when template changes
  useEffect(() => {
    if (selectedTemplateId) {
      const template = templates.find(t => t.id === selectedTemplateId);
      setAvailableCertificateTypes(template?.certificateTypes || []);
      setSelectedCertificateType(""); // reset selection
    } else {
      setAvailableCertificateTypes([]);
      setSelectedCertificateType("");
    }
  }, [selectedTemplateId]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();

    if (!file) return alert("Upload CSV file first");
    if (!selectedTemplateId) return alert("Select a certificate template first");
    if (!selectedCertificateType) return alert("Select a certificate type");

    const formData = new FormData();
    formData.append("file", file);
    formData.append("templateId", selectedTemplateId.toString());
    formData.append("certificateType", selectedCertificateType);

    setLoading(true);
    setResult(null);

    try {
      const res = await apiFetch("http://localhost:9090/issuer/bulk/mint", {
        method: "POST",
        body: formData,
      });

      if (!res.ok) {
        const text = await res.text();
        alert("Bulk mint failed: " + text);
        setLoading(false);
        return;
      }

      const data = await res.json();
      setResult(data);
    } catch (err) {
      console.error(err);
      alert("Bulk mint failed: network error");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="max-w-xl mx-auto p-6">
      <h1 className="text-2xl font-bold mb-4">Bulk/Nulk Certificate Mint</h1>

      <div className="mb-4 p-4 bg-blue-50 border-l-4 border-blue-400 text-blue-700 rounded">
        <p><strong>Instructions:</strong></p>
        <ul className="list-disc ml-5">
          <li>Upload a CSV file (.csv) with one certificate per line.</li>
          <li>CSV format: <code>studentName,studentSurname,studentIdentifier</code></li>
          <li>Example:</li>
          <li><code>Khethukuthula,Simamane,123456</code></li>
          <li><code>Jane,Doe,jane.doe@example.com</code></li>
          <li>All fields are required. Invalid lines will cause the upload to fail.</li>
          <li>Select the template and certificate type using the dropdowns below.</li>
        </ul>
      </div>

      <form onSubmit={handleSubmit} className="flex flex-col gap-3">
        {/* Template dropdown */}
        <select
          className="border p-2 w-full"
          value={selectedTemplateId}
          onChange={e => setSelectedTemplateId(Number(e.target.value) || "")}
        >
          <option value="">Select Certificate Template</option>
          {templates.map(t => (
            <option key={t.id} value={t.id}>
              {t.templateName}
            </option>
          ))}
        </select>

        {/* Certificate Type dropdown */}
        {/*<select
          className="border p-2 w-full"
          value={selectedCertificateType}
          onChange={e => setSelectedCertificateType(e.target.value)}
          disabled={!availableCertificateTypes.length}
        >
          <option value="">Select Certificate Type</option>
          {availableCertificateTypes.map(ct => (
            <option key={ct} value={ct}>{ct}</option>
          ))}
        </select>*/}

              <select
            className="border p-2 w-full mb-3"
            value={selectedCertificateType}
            onChange={e => setSelectedCertificateType(e.target.value)}
          >
            <option value="">Select certificate type</option>
            <option value="MATRIC">Matric</option>
            <option value="DEGREE">Degree</option>
            <option value="DIPLOMA">Diploma</option>
            <option value="SETA">SETA</option>
            <option value="TRADE_TEST">Trade Test</option>
            <option value="BBBEE">BBBEE</option>
            <option value="POLICE_CLEARANCE">Police Clearance</option>
            <option value="OTHER">Other</option>
          </select>

        {/* File input */}
        <input
          type="file"
          accept=".csv,text/csv"
          onChange={e => setFile(e.target.files?.[0] || null)}
        />

        <button
          type="submit"
          disabled={loading}
          className="bg-green-600 text-white px-4 py-2 rounded"
        >
          {loading ? "Minting..." : "Bulk Mint"}
        </button>
      </form>

      {result && (
        <div className="mt-6 p-4 bg-gray-50 border rounded">
          <h2 className="font-semibold">Bulk Mint Result</h2>
          {result?.verificationResponseList && (
          <div className="mt-4 flex gap-3">
            <button
              onClick={() => {
                const csv = result.verificationResponseList
                  .map(v =>
                    [
                      v.studentName,
                      v.studentSurname ?? "",
                      v.certificateUid,
                      v.certificateType,
                      result.batchUid
                    ].join(",")
                  )
                  .join("\n");

                navigator.clipboard.writeText(csv);
                alert("Batch CSV copied to clipboard");
              }}
              className="px-4 py-2 border rounded"
            >
              Copy batch as CSV
            </button>
            <button
            onClick={() => {
              const doc = new jsPDF();
              doc.setFont("helvetica");
              doc.setFontSize(12);

              let y = 20;

              doc.text("Batch Certificate Verification Data", 20, y);
              y += 10;
              doc.text(`Batch UID: ${result.batchUid}`, 20, y);
              y += 10;
              doc.text(`Transaction Hash: ${result.txHash}`, 20, y);
              y += 10;
              doc.text("------------------------------------", 20, y);
              y += 10;

              result.verificationResponseList.forEach((v, index) => {
                doc.text(
                  `${index + 1}. ${v.studentName} ${v.studentSurname || ""} | ${v.certificateUid} | ${v.certificateType}`,
                  20,
                  y
                );
                y += 8;
              });

              doc.save(`batch-${result.batchUid}.pdf`);
            }}
            className="px-4 py-2 bg-blue-600 text-white rounded"
          >
            Download batch PDF
          </button>

            </div>)}
        </div>
      )}
    </div>
  );
}
