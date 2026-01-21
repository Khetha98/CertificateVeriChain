"use client";

import { useState } from "react";
import { apiFetch } from "@/app/lib/api";
import { useRouter } from "next/navigation";
import {useEffect} from "react";

import jsPDF from "jspdf";
import { API_BASE_URL } from "../lib/config";


export default function MintCertificatePage() {
  const router = useRouter();
  const [studentName, setStudentName] = useState("");
  const [templateId, setTemplateId] = useState("");

  const [templates, setTemplates] = useState([]);
  const [issued, setIssued] = useState<any>(null);
  const [certificateType, setCertificateType] = useState("");
  const [studentIdentifier, setStudentIdentifier] = useState("");
  const [studentSurname, setStudentSurname] = useState("");

  useEffect(() => {
    const loadTemplates = async () => {
      try {
        const r = await apiFetch(`${API_BASE_URL}/templates`);
        if (!r.ok) return;

        const text = await r.text();
        setTemplates(text ? JSON.parse(text) : []);
      } catch (e) {
        console.error("Failed to load templates", e);
        setTemplates([]);
      }
    };

    loadTemplates();
  }, []);

  const [loading, setLoading] = useState(false);

  async function submit() {
    if (
      !studentName ||
      !studentSurname ||
      !studentIdentifier ||
      !certificateType ||
      !templateId
    ) {
      alert("Please fill in all fields");
      return;
    }

    setLoading(true);

    const r = await apiFetch(`${API_BASE_URL}/issuer/certificates`, {
      method: "POST",
      body: JSON.stringify({
        studentName,
        studentSurname,
        studentIdentifier,
        certificateType,
        templateId,
      }),
    });

    setLoading(false);

    if (!r.ok) {
      const text = await r.text();
      console.error("Mint failed:", r.status, text);
      alert("Mint failed");
      return;
    }

    const text = await r.text();
    const data = text ? JSON.parse(text) : null;
    setIssued(data);

    setStudentName("");
    setStudentSurname("");
    setStudentIdentifier("");
    setCertificateType("");
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
      <input
        className="border p-2 w-full mb-3"
        placeholder="Student Surname"
        value={studentSurname}
        onChange={e => setStudentSurname(e.target.value)}
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

      <input
        className="border p-2 w-full mb-3"
        placeholder="Student Number / Email / National ID"
        value={studentIdentifier}
        onChange={e => setStudentIdentifier(e.target.value)}
      />
      <select
      className="border p-2 w-full mb-3"
      value={certificateType}
      onChange={e => setCertificateType(e.target.value)}
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

      <button
        onClick={submit}
        className="bg-green-600 text-white px-4 py-2"
      >
        Mint
      </button>

    {issued && (
      <div className="mt-6 border rounded-lg p-6 bg-gray-50">
        <h2 className="font-semibold text-lg mb-4">
          Certificate Issued
        </h2>

        <p><strong>Certificate ID:</strong> {issued.certificateUid}</p>
        <p><strong>Student:</strong> {issued.studentName} {issued.studentSurname}</p>
        <p><strong>Identifier:</strong> {issued.studentIdentifier}</p>
        <p><strong>Type:</strong> {issued.certificateType}</p>
        <p><strong>Status:</strong> {issued.status}</p>
        <div className="mt-4 flex gap-3">
          {/* ✅ Copy all student/certificate data as CSV line */}
          <button
            onClick={() => {
              const csvLine = [
                issued.studentName,
                issued.studentSurname,
                issued.studentIdentifier,
                issued.certificateType,
                issued.certificateUid
              ].join(",");
              navigator.clipboard.writeText(csvLine);
              alert("Copied to clipboard:\n" + csvLine);
            }}
            className="px-4 py-2 border rounded"
        >
          Copy as CSV
        </button>

        {/* ✅ Download PDF via API call */}
        <button
          onClick={async () => {
              const doc = new jsPDF();

              doc.setFont("helvetica");
              doc.setFontSize(12);

              let y = 20;
              const line = (text) => {
                doc.text(text, 20, y);
                y += 8;
              };

              line("Certificate Verification Data");
              line("------------------------------");
              line(`Certificate ID: ${issued.certificateUid}`);
              line(`Student Name: ${issued.studentName} ${issued.studentSurname || ""}`);
              line(`Identifier: ${issued.studentIdentifier}`);
              line(`Certificate Type: ${issued.certificateType}`);
              line(`Issued By: ${issued.organization?.name || "Institution"}`);
              line(`Status: ${issued.status}`);

              doc.save(`verification-${issued.certificateUid}.pdf`);
          }}
          className="px-4 py-2 bg-blue-600 text-white rounded"
        >
          Download PDF
        </button>

      </div>

      </div>
    )}


    </div>
  );
}
