export const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:9090";

// Add this temporary log to see what the browser actually sees
if (typeof window !== "undefined") {
    console.log("Current API Base URL:", API_BASE_URL);
}