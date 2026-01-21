"use client";

import { useState } from "react";
import { API_BASE_URL } from "../lib/config";

export default function ContactForm() {
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState(""); // For the popup/message

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setLoading(true);
    setStatus("");

    const form = e.currentTarget;
    const formData = new FormData(form);

    try {
      const response = await fetch(`${API_BASE_URL}/api/contact`, {
        method: "POST",
        body: JSON.stringify(Object.fromEntries(formData)),
        headers: { "Content-Type": "application/json" },
      });

      if (response.ok) {
        alert("🚀 Success! Your message has been sent."); // The Popup
        form.reset(); // This empties the fields
      } else {
        setStatus("Error: Could not send email.");
      }
    } catch (error) {
      setStatus("Error: Network failure.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="max-w-md mx-auto">
      <form onSubmit={handleSubmit} className="space-y-4">
        <input name="name" required placeholder="Full Name" className="border p-2 w-full text-black" />
        <input name="email" required type="email" placeholder="Email" className="border p-2 w-full text-black" />
        <input name="organization" placeholder="Organization" className="border p-2 w-full text-black" />
        <textarea name="message" required placeholder="Message" className="border p-2 w-full text-black" />

        <button 
          disabled={loading} 
          className={`w-full py-2 rounded text-white ${loading ? "bg-gray-400" : "bg-blue-600 hover:bg-blue-700"}`}
        >
          {loading ? "Sending..." : "Send Message"}
        </button>
      </form>
      {status && <p className="mt-2 text-red-500 text-center">{status}</p>}
    </div>
  );
}