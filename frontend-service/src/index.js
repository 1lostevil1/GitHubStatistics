// index.js
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App'; // Компонент App
import { BrowserRouter } from 'react-router-dom'; // Импортируем BrowserRouter
import './styles/global.css'; // Глобальные стили

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <BrowserRouter> {/* Оборачиваем приложение в BrowserRouter */}
      <App />
    </BrowserRouter>
  </React.StrictMode>
);