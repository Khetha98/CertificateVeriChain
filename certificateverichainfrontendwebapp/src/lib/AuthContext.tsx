"use client";

import { createContext, useContext, useEffect, useState } from "react";
import { useRouter } from "next/navigation";

type AuthContextType = {
  loggedIn: boolean;
  login: (token: string) => void;
  logout: () => void;
  ready: boolean;
};

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [loggedIn, setLoggedIn] = useState(false);
  const [ready, setReady] = useState(false);
  const router = useRouter();

  // ✅ client-only sync
  useEffect(() => {
    setLoggedIn(!!localStorage.getItem("token"));
    setReady(true);
  }, []);

  function login(token: string) {
    localStorage.setItem("token", token);
    setLoggedIn(true);
  }

  function logout() {
    localStorage.removeItem("token");
    setLoggedIn(false);
     router.push("/"); 
  }

  return (
    <AuthContext.Provider value={{ loggedIn, login, logout, ready }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
}

