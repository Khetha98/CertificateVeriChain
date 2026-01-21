"use client";
import { useEffect, useState } from "react";

import { API_BASE_URL } from "../lib/config";
import { apiFetch } from "../lib/api";

export default function ManageRoles() {

 const [users,setUsers] = useState<any[]>([]);

useEffect(() => {
  (async () => {
    const r = await apiFetch(`${API_BASE_URL}/admin/members`);
    if (r.ok) setUsers(await r.json());
  })();
}, []);


 return (
  <div className="max-w-3xl mx-auto p-10">

   <h1 className="text-2xl font-bold mb-6">
     Institution Members & Voting Roles
   </h1>

   {users.map(u=>(
     <div key={u.id}
       className="border p-3 mb-3">

       <p>{u.fullName}</p>
       <p>{u.role}</p>

     </div>
   ))}

  </div>
 );
}
