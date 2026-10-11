import { useEffect, useState } from "react";
import api, { errMsg } from "../api";
import { CATEGORIES, LEVELS } from "./Courses.jsx";

const empty = { title: "", description: "", category: CATEGORIES[0], level: LEVELS[0], instructor: "", totalLessons: 10 };

export default function Admin() {
  const [tab, setTab] = useState("courses");
  const [courses, setCourses] = useState([]);
  const [enrollments, setEnrollments] = useState([]);
  const [students, setStudents] = useState([]);
  const [form, setForm] = useState(empty);
  const [editId, setEditId] = useState(null);
  const [msg, setMsg] = useState({ type: "", text: "" });

  const load = async () => {
    try {
      const [c, e, u] = await Promise.all([api.get("/courses"), api.get("/enrollments"), api.get("/users")]);
      setCourses(c.data); setEnrollments(e.data); setStudents(u.data.filter((x) => x.role === "STUDENT"));
    } catch (x) { setMsg({ type: "error", text: errMsg(x) }); }
  };
  useEffect(() => { load(); }, []);

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const save = async (e) => {
    e.preventDefault();
    if (form.title.trim().length < 3) return setMsg({ type: "error", text: "Title must be at least 3 characters" });
    if (Number(form.totalLessons) < 1) return setMsg({ type: "error", text: "Total lessons must be at least 1" });
    try {
      const body = { ...form, totalLessons: Number(form.totalLessons) };
      editId ? await api.put(`/courses/${editId}`, body) : await api.post("/courses", body);
      setMsg({ type: "notice", text: editId ? "Course updated" : "Course created" });
      setForm(empty); setEditId(null); load();
    } catch (x) { setMsg({ type: "error", text: errMsg(x) }); }
  };

  const edit = (c) => {
    setEditId(c.id);
    setForm({ title: c.title, description: c.description, category: c.category, level: c.level, instructor: c.instructor, totalLessons: c.totalLessons });
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const del = async (c) => {
    if (!window.confirm(`Delete "${c.title}" and all its enrollments?`)) return;
    try { await api.delete(`/courses/${c.id}`); setMsg({ type: "notice", text: "Course deleted" }); load(); }
    catch (x) { setMsg({ type: "error", text: errMsg(x) }); }
  };

  return (
    <>
      <h1>Admin dashboard</h1>
      <div className="stats">
        <div className="card"><strong>{courses.length}</strong><span>Courses</span></div>
        <div className="card"><strong>{students.length}</strong><span>Students</span></div>
        <div className="card"><strong>{enrollments.length}</strong><span>Enrollments</span></div>
      </div>
      <div className="tabs">
        {["courses", "enrollments", "students"].map((t) => (
          <button key={t} className={tab === t ? "active" : ""} onClick={() => setTab(t)}>{t[0].toUpperCase() + t.slice(1)}</button>
        ))}
      </div>
      {msg.text && <p className={msg.type}>{msg.text}</p>}

      {tab === "courses" && (
        <>
          <form className="card form" onSubmit={save}>
            <h3>{editId ? "Edit course" : "Add a course"}</h3>
            <label>Title<input required value={form.title} onChange={set("title")} /></label>
            <label>Instructor<input required value={form.instructor} onChange={set("instructor")} /></label>
            <label>Description<textarea required rows="3" value={form.description} onChange={set("description")} /></label>
            <div className="row">
              <label>Category<select value={form.category} onChange={set("category")}>{CATEGORIES.map((c) => <option key={c}>{c}</option>)}</select></label>
              <label>Level<select value={form.level} onChange={set("level")}>{LEVELS.map((l) => <option key={l}>{l}</option>)}</select></label>
              <label>Lessons<input type="number" min="1" required value={form.totalLessons} onChange={set("totalLessons")} /></label>
            </div>
            <div className="row">
              <button className="btn" type="submit">{editId ? "Save changes" : "Create course"}</button>
              {editId && <button type="button" className="btn ghost" onClick={() => { setEditId(null); setForm(empty); }}>Cancel</button>}
            </div>
          </form>
          <div className="scroll"><table>
            <thead><tr><th>Title</th><th>Category</th><th>Level</th><th>Lessons</th><th></th></tr></thead>
            <tbody>{courses.map((c) => (
              <tr key={c.id}><td>{c.title}</td><td>{c.category}</td><td>{c.level}</td><td>{c.totalLessons}</td>
                <td className="actions"><button className="btn ghost" onClick={() => edit(c)}>Edit</button><button className="btn danger" onClick={() => del(c)}>Delete</button></td></tr>
            ))}</tbody>
          </table></div>
        </>
      )}

      {tab === "enrollments" && (
        <div className="scroll"><table>
          <thead><tr><th>Student</th><th>Email</th><th>Course</th><th>Progress</th></tr></thead>
          <tbody>{enrollments.map((e) => (
            <tr key={e.id}><td>{e.studentName}</td><td>{e.studentEmail}</td><td>{e.course.title}</td><td>{e.progress}%</td></tr>
          ))}</tbody>
        </table>{enrollments.length === 0 && <p className="muted">No enrollments yet.</p>}</div>
      )}

      {tab === "students" && (
        <div className="scroll"><table>
          <thead><tr><th>Name</th><th>Email</th><th>Courses enrolled</th></tr></thead>
          <tbody>{students.map((s) => (
            <tr key={s.id}><td>{s.name}</td><td>{s.email}</td><td>{enrollments.filter((e) => e.studentEmail === s.email).length}</td></tr>
          ))}</tbody>
        </table>{students.length === 0 && <p className="muted">No students have registered yet.</p>}</div>
      )}
    </>
  );
}