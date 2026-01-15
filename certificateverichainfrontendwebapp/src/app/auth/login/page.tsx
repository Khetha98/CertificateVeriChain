// app/auth/login/page.tsx
"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/lib/AuthContext"; // Import useAuth

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const router = useRouter();
  const auth = useAuth(); // Access the auth context

  async function login() {
    const res = await fetch("http://localhost:9090/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    });

    const data = await res.json();
    
    // 👇 CRITICAL CHANGE: Use the context's login function
    if (data.token) {
      auth.login(data.token); 
      // navigate to dashboard
      router.push("/institutionDashboard"); 
    } else {
      // Handle login failure (e.g., show an error message)
      alert("Login failed. Check credentials.");
    }
  }

  return (
    // ... rest of your JSX remains the same
    <div className="max-w-md mx-auto p-10">
      <h1 className="text-2xl font-bold mb-6">Institution Login</h1>
      <input
        className="border p-2 w-full mb-4"
        placeholder="Email"
        value={email}
        onChange={e => setEmail(e.target.value)}
      />
      <input
        className="border p-2 w-full mb-4"
        type="password"
        placeholder="Password"
        value={password}
        onChange={e => setPassword(e.target.value)}
      />
      <button
        className="bg-blue-600 text-white px-4 py-2 w-full"
        onClick={login}
      >
        Sign In
      </button>
    </div>
  );
}
