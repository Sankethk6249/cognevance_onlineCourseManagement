import { useEffect, useState } from "react";
import api, { errMsg } from "../api";
import { useAuth } from "../auth.jsx";

export const CATEGORIES = ["Programming", "Data Science", "Design", "Business", "DevOps"];
export const LEVELS = ["BEGINNER", "INTERMEDIATE", "ADVANCED"];

export default function Courses() {
  const { user } = useAuth();
  const isStudent = user.role === "STUDENT";
  const [courses, setCourses] = useState([]);
  const [enrolled, setEnrolled] = useState([]);
  const [q, setQ] = useState({ search: "", category: "", level: "" });
  const [msg, setMsg] = useState("");

  const load = async () => {
    try {
      const { data } = await api.get("/courses", { params: q });
      setCourses(data);
      if (isStudent) {
        const r = await api.get("/enrollments/my");
        setEnrolled(r.data.map((e) => e.course.id));
      }
    } catch (e) {
      setMsg(errMsg(e));
    }
  };

  useEffect(() => {
    const t = setTimeout(load, 300);
    return () => clearTimeout(t);
  }, [q]);

  const enroll = async (id) => {
    try {
      await api.post(`/enrollments/${id}`);
      setMsg("You are enrolled. Find it under My learning.");
      load();
    } catch (e) {
      setMsg(errMsg(e));
    }
  };

  return (
    <>
      <h1>Find your next course</h1>
      <div className="filters">
        <input placeholder="Search by title or instructor" value={q.search}
               onChange={(e) => setQ({ ...q, search: e.target.value })} />
        <select value={q.category} onChange={(e) => setQ({ ...q, category: e.target.value })}>
          <option value="">All categories</option>
          {CATEGORIES.map((c) => <option key={c}>{c}</option>)}
        </select>
        <select value={q.level} onChange={(e) => setQ({ ...q, level: e.target.value })}>
          <option value="">All levels</option>
          {LEVELS.map((l) => <option key={l}>{l}</option>)}
        </select>
      </div>
      {msg && <p className="notice">{msg}</p>}
      {courses.length === 0 && <p className="muted">No courses match your filters. Try clearing the search.</p>}
      <div className="grid">
        {courses.map((c) => (
          <article className="card" key={c.id}>
            <span className={`tag ${c.level.toLowerCase()}`}>{c.level}</span>
            <h3>{c.title}</h3>
            <p>{c.description}</p>
            <p className="muted small">{c.instructor} - {c.category} - {c.totalLessons} lessons</p>
            {isStudent && (
              enrolled.includes(c.id)
                ? <button className="btn ghost" disabled>Enrolled</button>
                : <button className="btn" onClick={() => enroll(c.id)}>Enroll</button>
            )}
          </article>
        ))}
      </div>
    </>
  );
}