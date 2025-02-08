// App.js
import React, { useEffect, useState } from 'react';
import { Routes, Route, useNavigate } from 'react-router-dom';
import Home from './components/Home';
import Login from './components/Login';
import Register from './components/Register';
import axios from 'axios';

const App = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(null); // Для отслеживания состояния авторизации
  const navigate = useNavigate();

  // Проверяем валидность токена при монтировании компонента
  useEffect(() => {
    const token = localStorage.getItem('authToken');
    
    // Если токен есть, отправляем запрос на сервер для проверки
    if (token) {
      axios
        .get('/api/secured/checkToken', {
          headers: {
            Authorization: `Bearer ${token}`, // Отправляем токен в заголовке
          },
        })
        .then((response) => {
          // Если сервер возвращает успешный ответ, считаем пользователя авторизованным
          setIsAuthenticated(true);
          navigate('/'); // Перенаправляем на страницу home
        })
        .catch((error) => {
          // Если сервер отвечает ошибкой UNAUTHORIZED, перенаправляем на логин
          setIsAuthenticated(false);
          navigate('/'); // Перенаправляем на страницу логина
        });
    } else {
      // Если токена нет, перенаправляем на страницу логина
      setIsAuthenticated(false);
      navigate('/');
    }
  }, [navigate]);

  if (isAuthenticated === null) {
    return <div>Loading...</div>; // Показываем загрузку, пока не получим ответ от сервера
  }

  return (
    <Routes>
      <Route path="/home" element={<Home />} />
      <Route path="/" element={<Login />} />
      <Route path="/register" element={<Register />} />
    </Routes>
  );
};

export default App;
