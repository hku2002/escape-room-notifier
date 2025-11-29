import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import AlertList from './pages/AlertList';
import ZeroWorldReservation from './pages/ZeroWorldReservation';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/users/alerts" element={<AlertList />} />
        <Route path="/reservations/zero-world" element={<ZeroWorldReservation />} />
        <Route path="/" element={<Navigate to="/reservations/zero-world" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
