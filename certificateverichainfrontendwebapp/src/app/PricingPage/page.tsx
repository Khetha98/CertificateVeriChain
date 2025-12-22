export default function PricingPage() {
  return (
    <section className="max-w-5xl mx-auto p-10">
      <h1 className="text-3xl font-bold mb-4">Pricing</h1>

      <div className="grid md:grid-cols-3 gap-6">
        <div className="border p-6 rounded">
          <h2 className="font-bold">Starter</h2>
          <p className="text-gray-600">Pilot institutions</p>
          <p className="mt-4 font-bold">Free</p>
        </div>

        <div className="border p-6 rounded">
          <h2 className="font-bold">Professional</h2>
          <p className="text-gray-600">Active issuers</p>
          <p className="mt-4 font-bold">Per certificate</p>
        </div>

        <div className="border p-6 rounded">
          <h2 className="font-bold">Enterprise</h2>
          <p className="text-gray-600">Government scale</p>
          <p className="mt-4 font-bold">Custom</p>
        </div>
      </div>
    </section>
  );
}
