import { Routes, Route, NavLink, Navigate, useNavigate } from "react-router-dom";
import { useAuth } from "./auth.jsx";
import Auth from "./pages/Auth.jsx";
import Courses from "./pages/Courses.jsx";
import MyCourses from "./pages/MyCourses.jsx";
import Admin from "./pages/Admin.jsx";

function Protected({ role, children }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (role && user.role !== role) return <Navigate to="/" replace />;
  return children;
}

function Navbar() {
  const { user, logout } = useAuth();
  const nav = useNavigate();
  return (
    <header className="nav">
      <span className="brand">Learnly</span>
      <nav>
        {user && <NavLink to="/" end>Courses</NavLink>}
        {user?.role === "STUDENT" && <NavLink to="/my-courses">My learning</NavLink>}
        {user?.role === "ADMIN" && <NavLink to="/admin">Admin</NavLink>}
        {!user && <NavLink to="/login">Log in</NavLink>}
        {!user && <NavLink to="/register">Sign up</NavLink>}
        {user && (
          <button className="link" onClick={() => { logout(); nav("/login"); }}>
            Log out ({user.name.split(" ")[0]})
          </button>
        )}
      </nav>
    </header>
  );
}

export default function App() {
  return (
    <>
      <Navbar />
      <main className="container">
        <Routes>
          <Route path="/login" element={<Auth mode="login" />} />
          <Route path="/register" element={<Auth mode="register" />} />
          <Route path="/" element={<Protected><Courses /></Protected>} />
          <Route path="/my-courses" element={<Protected role="STUDENT"><MyCourses /></Protected>} />
          <Route path="/admin" element={<Protected role="ADMIN"><Admin /></Protected>} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </>
  );
}