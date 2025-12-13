export default function Metrics() {
  return (
    <section className="max-w-7xl mx-auto p-10 grid md:grid-cols-4 gap-6 text-center">
      <div>
        <p className="text-3xl font-bold">&lt;1s</p>
        <p className="text-gray-600">Verification</p>
      </div>
      <div>
        <p className="text-3xl font-bold">Multi-Sig</p>
        <p className="text-gray-600">Issuance</p>
      </div>
      <div>
        <p className="text-3xl font-bold">AWS KMS</p>
        <p className="text-gray-600">Key Security</p>
      </div>
      <div>
        <p className="text-3xl font-bold">SA-Focused</p>
        <p className="text-gray-600">High-Fraud Docs</p>
      </div>
    </section>
  );
}
