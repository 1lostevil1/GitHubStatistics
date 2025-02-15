import React, { useEffect, useState } from 'react';
import { Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import Home from './components/Home';
import Login from './components/Login';
import Register from './components/Register';
import axios from 'axios';
import Subscription from './components/Subscription';

const App = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(null); // Изначально null
  const navigate = useNavigate();
  const location = useLocation();

  const publicPaths = ['/', '/register']; // Публичные маршруты

  useEffect(() => {
    const token = localStorage.getItem('authToken');
    
    const checkAuth = async () => {
      try {
        if (token) {
          await axios.get('/api/secured/checkToken', {
            headers: { Authorization: `Bearer ${token}` }
          });
          setIsAuthenticated(true);
          // Перенаправляем на home только если на публичном маршруте
          if (publicPaths.includes(location.pathname)) {
            navigate('/home');
          }
        } else {
          throw new Error('No token');
        }
      } catch (error) {
        setIsAuthenticated(false);
        localStorage.removeItem('authToken');
        // Перенаправляем только если текущий путь не публичный
        if (!publicPaths.includes(location.pathname)) {
          navigate('/');
        }
      }
    };

    if (!publicPaths.includes(location.pathname)) {
      checkAuth();
    } else {
      setIsAuthenticated(false); // Для публичных маршрутов сразу false
    }
  }, [navigate, location.pathname]);

  // Пока проверяется авторизация, показываем "Loading..."
  if (isAuthenticated === null) {
    return <div>Loading...</div>;
  }

  return (
    <Routes>
      {/* Отображаем Subscription только если пользователь авторизован */}
      <Route path="/home" element={isAuthenticated ? <Subscription /> : <Login />} />
      <Route path="/" element={<Login />} />
      <Route path="/register" element={<Register />} />
    </Routes>
  );
};

export default App;
