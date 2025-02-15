import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/Login.css'; // Подключаем стили

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const loginResponse = await axios.post('/api/createAuthToken', {
        username,
        password,
      });

      if (loginResponse.status === 200) {
        console.log('got token!');
        localStorage.setItem('authToken', loginResponse.data.token);

        // Сохраняем имя пользователя в localStorage
        localStorage.setItem('username', username);

        navigate('/home');
      }
    } catch (error) {
      if (error.response) {
        if (error.response.status === 401) {
          setError('Incorrect login or password');
        } else {
          setError('Server error. Please try again later.');
        }
      } else if (error.request) {
        setError('No response from server. Please check your connection.');
      } else {
        setError('An unexpected error occurred.');
      }

      setTimeout(() => {
        setError('');
      }, 3000);
    }
  };

  const handleRegisterRedirect = () => {
    navigate('/register');
  };

  return (
    <div className="form-container">
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <div className="input-container">
          <input
            type="text"
            id="username"
            name="username"
            autoComplete="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder=" "
          />
          <label htmlFor="username">Username</label>
        </div>
        <div className="input-container">
          <input
            type="password"
            id="password"
            name="password"
            autoComplete="current-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder=" "
          />
          <label htmlFor="password">Password</label>
        </div>
        <button type="submit">Login</button>
      </form>
      {error && <div className="error-message">{error}</div>}

      <div className="register-link">
        <button onClick={handleRegisterRedirect}>Don't have an account? Register</button>
      </div>
    </div>
  );
};

export default Login;
