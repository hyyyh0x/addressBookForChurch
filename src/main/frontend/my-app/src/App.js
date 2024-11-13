import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import UserList from './UserList';

function App() {
  return (
    <Router>
      <div style={{ padding: '20px' }}>
        <h1>팔송정교회새신자부</h1>
        <Routes>
          <Route path="/" element={<UserList />} /> {/* Display UserList on the root path */}
        </Routes>
      </div>
    </Router>
  );
}

export default App;
