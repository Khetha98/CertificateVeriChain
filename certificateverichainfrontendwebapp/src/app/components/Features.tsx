const FEATURES = [
  {
    title: "Tamper-Proof Credentials",
    description: "Cryptographically secured certificates that cannot be altered or forged.",
  },
  {
    title: "Instant Verification",
    description: "Scan a QR code and verify authenticity in under one second.",
  },
  {
    title: "Invisible Blockchain",
    description: "No wallets, tokens, or crypto knowledge required.",
  },
];

export default function Features() {
  return (
    <section className="max-w-7xl mx-auto p-10 grid md:grid-cols-3 gap-8">
      {FEATURES.map((f) => (
        <div key={f.title} className="rounded-xl p-6 bg-white shadow-sm hover:shadow-md transition">
          <h3 className="font-semibold text-lg">{f.title}</h3>
          <p className="mt-2 text-gray-600">{f.description}</p>
        </div>
      ))}
    </section>
  );
}
