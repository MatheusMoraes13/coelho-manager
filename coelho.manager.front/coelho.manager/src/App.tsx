import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

import DesignationsGenPage from './pages/DesignationsGenPage/DesignationsGenPage';
import GlobaLColors from './assets/GlobalColors';
import CNLQueryPage from './pages/CNLQueryPage/CNLQueryPage';
import MenuPage from './pages/MenuPage/MenuPage';
import ReportGeneratorPage from './pages/ReportPage/ReportPage';

export function App() {
  return (
    <>
    <GlobaLColors />
    <Router>
      <Routes>
        <Route path="/" element={<MenuPage />} />
        <Route path="/DesignationsGenPage" element={<DesignationsGenPage />} />
        <Route path="/CNLQueryPage" element={<CNLQueryPage />} />
        <Route path="/ReportPage" element={<ReportGeneratorPage />} />
      </Routes>
    </Router>
    </>
  );
}