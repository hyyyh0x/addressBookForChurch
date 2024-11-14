import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import UserList from './UserList';
import './App.css';

function App() {
  return (
    <div className="container">
          <header className="header">
            서김해교회
          </header>
          <UserList />
          <footer className="footer">
            © 2024 서김해교회 | 모든 권리 보유
          </footer>
        </div>
  );
}

export default App;
