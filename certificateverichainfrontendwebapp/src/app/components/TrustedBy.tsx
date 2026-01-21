export default function TrustedBy() {
  const partners = [
    "High Schools",
    "Universities",
    "SETA / NAMB",
    "SAPS",
    "BBBEE Agencies",
    "Employers",
  ];

  return (
    <section className="bg-white relative">
      <div className="max-w-7xl mx-auto p-10 text-center">
        <h3 className="text-gray-600 uppercase tracking-wide text-sm">
          Trusted By
        </h3>

        <div className="mt-6 grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6 text-sm font-medium">
          {partners.map((p) => (
            <div
              key={p}
              className="py-4 px-2 text-gray-500"
            >
              {p}
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
