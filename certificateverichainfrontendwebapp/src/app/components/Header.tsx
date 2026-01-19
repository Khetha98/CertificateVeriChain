"use client";

import Image from "next/image";
import Link from "next/link";
import { useEffect, useRef, useState } from "react";
import { useAuth } from "@/lib/AuthContext";
import { useRouter } from "next/navigation";

export default function Header() {
  const { loggedIn, logout } = useAuth();
  const [open, setOpen] = useState(false);
  const router = useRouter();

  const dropdownRef = useRef<HTMLDivElement>(null);

  // ✅ Close dropdown when clicking outside
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setOpen(false);
      }
    }

    if (open) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [open]);

  return (
    <header className="bg-white border-b">
      <div className="max-w-7xl mx-auto flex items-center justify-between p-4">
      <div
        onClick={() => {
          if (loggedIn) {
            router.push("/institutionDashboard");
          } else {
            router.push("/");
          }
        }}
        className="flex items-center gap-2 cursor-pointer"
      >
        <Image src="/logo.png" alt="CertificateVeriChain" width={36} height={36} />
        <span className="font-semibold text-lg">CertificateVeriChain</span>
      </div>


        <nav className="flex gap-6 text-sm items-center">
          <Link href="/VerificationSystemPage">Verify</Link>
          <Link href="/InstitutionsPage">Institutions</Link>
          <Link href="/SecurityPage">Security</Link>
          <Link href="/PricingPage">Pricing</Link>
          <Link href="/ContactUsPage">Contact</Link>

          {!loggedIn && (
            <>
              <Link href="/auth/register" className="font-medium text-blue-600">
                Register
              </Link>
              <Link href="/auth/login" className="font-medium text-blue-600">
                Login
              </Link>
            </>
          )}

          {loggedIn && (
            <div ref={dropdownRef} className="relative">
              <button
                onClick={() => setOpen(o => !o)}
                className="w-9 h-9 rounded-full bg-blue-600 text-white flex items-center justify-center"
              >
                U
              </button>

              {open && (
                <div className="absolute right-0 mt-2 w-48 bg-white shadow rounded z-50">
                  <button
                    onClick={() => {
                      router.push("/templates/create");
                      setOpen(false);
                    }}
                    className="block w-full text-left px-4 py-2 hover:bg-gray-100"
                  >
                    Create Template
                  </button>

                  <button
                    onClick={() => {
                      router.push("/mint");
                      setOpen(false);
                    }}
                    className="block w-full text-left px-4 py-2 hover:bg-gray-100"
                  >
                    Mint Certificate
                  </button>

                  <button
                    onClick={() => {
                      router.push("/approvals");
                      setOpen(false);
                    }}
                    className="block w-full text-left px-4 py-2 hover:bg-gray-100"
                  >
                    Approvals
                  </button>

                  <button
                    onClick={() => {
                      logout();
                      setOpen(false);
                    }}
                    className="block w-full text-left px-4 py-2 text-red-600 hover:bg-gray-100"
                  >
                    Logout
                  </button>
                </div>
              )}
            </div>
          )}
        </nav>
      </div>
    </header>
  );
}
