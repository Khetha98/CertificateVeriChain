import ContactForm from "../components/ContactForm";

export default function ContactUsPage() {
  return (
    <section className="max-w-4xl mx-auto p-10">
      <h1 className="text-3xl font-bold mb-6">Contact Us</h1>

      <div className="grid md:grid-cols-2 gap-10">
        <div>
          <p>Email: support@certificateverichain.co.za</p>
          <p>Phone: +27 XX XXX XXXX</p>
          <p className="mt-4 text-gray-600">
            Reach out for demos, partnerships, or institutional onboarding.
          </p>
        </div>

        <ContactForm />
      </div>
    </section>
  );
}
