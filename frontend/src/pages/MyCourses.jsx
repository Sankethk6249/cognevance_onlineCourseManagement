import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api, { errMsg } from "../api";

export default function MyCourses() {
  const [items, setItems] = useState([]);
  const [msg, setMsg] = useState("");

  const load = () => api.get("/enrollments/my").then((r) => setItems(r.data)).catch((e) => setMsg(errMsg(e)));
  useEffect(() => { load(); }, []);

  const bump = async (e, d) => {
    const n = Math.min(e.course.totalLessons, Math.max(0, e.completedLessons + d));
    try {
      await api.put(`/enrollments/${e.course.id}/progress`, { completedLessons: n });
      load();
    } catch (x) { setMsg(errMsg(x)); }
  };

  const drop = async (e) => {
    if (!window.confirm(`Unenroll from "${e.course.title}"? Your progress will be lost.`)) return;
    try { await api.delete(`/enrollments/${e.course.id}`); load(); } catch (x) { setMsg(errMsg(x)); }
  };

  return (
    <>
      <h1>My learning</h1>
      {msg && <p className="error">{msg}</p>}
      {items.length === 0 && <p className="muted">You have not enrolled in any course yet. <Link to="/">Browse courses</Link></p>}
      <div className="grid">
        {items.map((e) => (
          <article className="card" key={e.id}>
            <h3>{e.course.title}</h3>
            <p className="muted small">{e.course.instructor}</p>
            <div className="bar" role="progressbar" aria-valuenow={e.progress} aria-valuemin="0" aria-valuemax="100">
              <div style={{ width: `${e.progress}%` }} />
            </div>
            <p className="small">{e.completedLessons} of {e.course.totalLessons} lessons - {e.progress}%{e.progress === 100 && " - Completed"}</p>
            <div className="row">
              <button className="btn" onClick={() => bump(e, 1)} disabled={e.progress === 100}>Complete next lesson</button>
              <button className="btn ghost" onClick={() => bump(e, -1)} disabled={e.completedLessons === 0}>Undo</button>
              <button className="btn danger" onClick={() => drop(e)}>Unenroll</button>
            </div>
          </article>
        ))}
      </div>
    </>
  );
}