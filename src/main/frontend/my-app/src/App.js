import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import UserList from './UserList';
import './App.css';

function App() {
const [searchQuery, setSearchQuery] = useState('');

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };
  return (
    <div className="container">
          <header className="header">
            <div className="title-container">
              <h1 className="church-title">서김해교회</h1>
            </div>
            <div className="bottom-header">
            <h2 className="list-title">성도 목록</h2>
                      <input
                        type="text"
                        placeholder="검색할 이름을 입력하세요"
                        value={searchQuery}
                        onChange={handleSearchChange}
                        className="search-input"
                      />
            </div>
          </header>
          <UserList searchQuery={searchQuery} />
          <footer className="footer">
            © 2024 서김해교회 | 모든 권리 보유
          </footer>
        </div>
  );
}

export default App;
