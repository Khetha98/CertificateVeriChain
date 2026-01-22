/*export async function apiFetch(url: string, options: RequestInit = {}) {
  const token = localStorage.getItem("token");

  const headers = new Headers();


  if (!(options.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  return fetch(url, {
    ...options,
    headers,
    credentials: "include"
  });
}*/

export async function apiFetch(
  url: string,
  options: RequestInit = {}
) {
  const token = localStorage.getItem("token");

  const headers = new Headers(options.headers);

  // ✅ ONLY set JSON if body is NOT FormData
  if (!(options.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const res = await fetch(url, {
    ...options,
    headers,
  });

  if (res.status === 401 || res.status === 403) {
    localStorage.removeItem("token");
    window.location.href = "/auth/login";
    throw new Error("Session expired");
  }

  return res;
}




