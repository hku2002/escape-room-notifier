import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import AlertList from './pages/AlertList';
import ZeroWorldReservation from './pages/ZeroWorldReservation';
import EarthEscapeReservation from './pages/EarthEscapeReservation';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/users/alerts" element={<AlertList />} />
        <Route path="/reservations/zero-world" element={<ZeroWorldReservation />} />
        <Route path="/reservations/earth-escape" element={<EarthEscapeReservation />} />
        <Route path="/" element={<Navigate to="/reservations/zero-world" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
