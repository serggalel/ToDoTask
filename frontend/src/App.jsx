import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import HomePage from "./pages/HomePage";
import MainLayout from "./components/MainLayout.jsx";

export default function App() {
  return (

      <BrowserRouter>
        <Routes>

          <Route path="/" element={<Navigate to="/login" replace />} />

          <Route path="/login" element={<LoginPage />} />
          <Route element={<MainLayout />}>
              <Route path="/home" element={<HomePage />} />

          </Route>

        </Routes>
      </BrowserRouter>
  );
}