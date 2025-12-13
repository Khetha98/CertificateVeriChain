const STEPS = [
  "Institution uploads learner data",
  "Multi-sign approval & fraud checks",
  "QR-coded credential issued",
  "Instant verification by employer",
];

export default function HowItWorks() {
  return (
    <section className="bg-gray-50 p-10">
      <div className="max-w-5xl mx-auto">
        <h2 className="text-2xl font-bold mb-6">How It Works</h2>
        <ol className="space-y-4">
          {STEPS.map((step, index) => (
            <li key={step} className="flex gap-4">
              <span className="font-bold">{index + 1}</span>
              <span>{step}</span>
            </li>
          ))}
        </ol>
      </div>
    </section>
  );
}
