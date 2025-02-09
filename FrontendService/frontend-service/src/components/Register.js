import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const Register = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const registerResponse = await axios.post('/api/signup', {
        username,
        email,
        password,
      });

      if (registerResponse.status === 200) {
        console.log('User registered successfully!');
        navigate('/'); // После успешной регистрации перенаправляем на страницу логина
      }
    } catch (error) {
      if (error.response) {
        if (error.response.status === 400) {
          setError('Username or email already taken');
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

  const handleLoginRedirect = () => {
    navigate('/');
  };

  return (
    <div className="form-container">
      <h2>Register</h2>
      <form onSubmit={handleSubmit}>
        <div className="input-container">
          <input
            type="text"
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder=" "
          />
          <label htmlFor="username">Username</label>
        </div>
        <div className="input-container">
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder=" "
          />
          <label htmlFor="email">Email</label>
        </div>
        <div className="input-container">
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder=" "
          />
          <label htmlFor="password">Password</label>
        </div>
        <button type="submit">Register</button>
      </form>
      {error && <div className="error-message">{error}</div>}

      <div className="login-link">
        <button onClick={handleLoginRedirect}>Already have an account? Login</button>
      </div>
    </div>
  );
};

export default Register;
