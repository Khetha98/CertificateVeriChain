export default function Footer() {
  return (
    <footer className="border-t bg-gray-900 text-gray-300 ">
      <div className="max-w-7xl mx-auto p-8 grid md:grid-cols-3 gap-6 text-sm">
        <div>
          <h4 className="font-semibold text-white">CertificateVeriChain</h4>
          <p className="mt-2">
            Secure digital certificate verification for South Africa.
          </p>
        </div>

        <div>
          <h4 className="font-semibold text-white">Platform</h4>
          <ul className="mt-2 space-y-1">
            <li>Verification System</li>
            <li>Institution Dashboard</li>
            <li>Student Wallet (No Crypto)</li>
          </ul>
        </div>

        <div>
          <h4 className="font-semibold text-white">Legal</h4>
          <ul className="mt-2 space-y-1">
            <li>Privacy Policy</li>
            <li>Terms of Service</li>
            <li>POPIA Compliance</li>
          </ul>
        </div>
      </div>

      <div className="text-center text-xs text-gray-500 py-4">
        © {new Date().getFullYear()} CertificateVeriChain. All rights reserved.
      </div>
    </footer>
  );
}
