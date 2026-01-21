"use client";

import { useState } from "react";
import { API_BASE_URL } from "../lib/config";

export default function ContactForm() {
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setLoading(true);

    const formData = new FormData(e.currentTarget);

    await fetch(`${API_BASE_URL}/api/contact`, {
      method: "POST",
      body: JSON.stringify(Object.fromEntries(formData)),
      headers: { "Content-Type": "application/json" },
    });

    setLoading(false);
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <input name="name" placeholder="Full Name" className="border p-2 w-full" />
      <input name="email" placeholder="Email" className="border p-2 w-full" />
      <input name="organization" placeholder="Organization" className="border p-2 w-full" />
      <textarea name="message" placeholder="Message" className="border p-2 w-full" />

      <button disabled={loading} className="bg-blue-600 text-white px-4 py-2 rounded">
        {loading ? "Sending..." : "Send Message"}
      </button>
    </form>
  );
}
