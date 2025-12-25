"use client";

import Image from "next/image";
import Link from "next/link";

export default function Header() {
  return (
    <header className=" bg-white">
      <div className="max-w-7xl mx-auto flex items-center justify-between p-4">
        <Link href="/" className="flex items-center gap-2">
          <Image src="/logo.png" alt="CertificateVeriChain" width={36} height={36} />
          <span className="font-semibold text-lg">CertificateVeriChain</span>
        </Link>

        <nav className="flex gap-6 text-sm">
          <Link href="/VerificationSystemPage">Verify</Link>
          <Link href="/InstitutionsPage">Institutions</Link>
          <Link href="/SecurityPage">Security</Link>
          <Link href="/PricingPage">Pricing</Link>
          <Link href="/ContactUsPage" className="font-medium">
            Contact
          </Link>
          <Link href="/auth/register" className="font-medium text-blue-600">
            Register
          </Link>
          <Link href="/auth/login" className="font-medium text-blue-600">
            Login
          </Link>
        </nav>

      </div>
    </header>
  );
}
