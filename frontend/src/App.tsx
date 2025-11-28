import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import AlertList from './pages/AlertList';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/users/alerts" element={<AlertList />} />
        <Route path="/" element={<Navigate to="/users/alerts" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
