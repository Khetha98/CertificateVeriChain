"use client";

import { useState } from "react";

export default function RegisterPage() {
  const [form, setForm] = useState({
    institutionName: "",
    registrationNumber: "",
    email: "",
    password: "",
  });

  async function register() {
    await fetch("http://localhost:9090/api/auth/register-institution", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(form),
    });

    alert("Registration submitted for approval");
  }

  return (
    <div className="max-w-md mx-auto p-10">
      <h1 className="text-2xl font-bold mb-6">Institution Registration</h1>

      {["institutionName", "registrationNumber", "email", "password"].map(f => (
        <input
          key={f}
          className="border p-2 w-full mb-4"
          placeholder={f.replace(/([A-Z])/g, " $1")}
          type={f === "password" ? "password" : "text"}
          onChange={e => setForm({ ...form, [f]: e.target.value })}
        />
      ))}

      <button
        onClick={register}
        className="bg-blue-600 text-white px-4 py-2 w-full"
      >
        Submit for Approval
      </button>
    </div>
  );
}
