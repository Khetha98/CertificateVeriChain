import Image from "next/image";
import Link from "next/link";

export default function Hero() {
  return (
    <section className="bg-gray-50">
      <div className="max-w-7xl mx-auto grid md:grid-cols-2 gap-10 p-10 items-center">
        <div>
          <h1 className="text-4xl font-bold leading-tight">
            Instantly Verify South Africa’s Most Critical Certificates
          </h1>

          <p className="mt-4 text-gray-600">
            Secure, QR-coded digital credentials for matric, trade tests,
            police clearances, BBBEE, and qualifications.
          </p>

          <div className="mt-6 flex gap-4">
            <Link href="/auth/register" className="bg-blue-600 text-white px-6 py-3 rounded">
              Get Started
            </Link>
            <Link href="/VerificationSystemPage" className="border px-6 py-3 rounded">
              Verify Certificate
            </Link>
          </div>
        </div>

        <div className="flex justify-center">
          <Image
            src="/logo.png"
            alt="Verification Flow"
            width={320}
            height={320}
            priority
          />
        </div>
      </div>
    </section>
  );
}
