// Home.js
import React, { useState } from 'react';
import Subscription from './Subscription';
import '../styles/Subscription.css'; // Подключаем стили для Home

const Home = () => {
  const [username, setUsername] = useState('');

  return (
    <div className="home-container">
      <div className="header">
        <h1>WebSocket STOMP App</h1>
      </div>
      <Subscription username={username} />
    </div>
  );
};

export default Home;
