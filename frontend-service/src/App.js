import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import logo from './logo.svg';
import './App.css';

function App() {
  const [messages, setMessages] = useState([]);
  const [client, setClient] = useState(null);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    console.log('Initializing WebSocket connection...');
    const socket = new SockJS('http://localhost:8090/ws');
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      debug: (str) => console.log(str),
      onConnect: () => {
        console.log('Connected to WebSocket');
        setConnected(true);
        
        stompClient.subscribe('/topic/all', (message) => {
          console.log('Received message:', message.body);
          setMessages((prevMessages) => [...prevMessages, message.body]);
        });
      },
      onStompError: (frame) => {
        console.error('Broker reported error:', frame.headers['message']);
        console.error('Additional details:', frame.body);
      },
      onWebSocketError: (event) => {
        console.error('WebSocket Error:', event);
      },
      onDisconnect: () => {
        console.log('Disconnected from WebSocket');
        setConnected(false);
      },
    });
    
    stompClient.activate();
    setClient(stompClient);

    return () => {
      if (stompClient) {
        stompClient.deactivate();
        console.log('WebSocket connection closed.');
      }
    };
  }, []);

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <h1>WebSocket STOMP Chat</h1>
        <p>Status: {connected ? 'Connected' : 'Disconnected'}</p>
        <div className="message-box">
          <h2>Incoming Messages</h2>
          <ul>
            {messages.length > 0 ? (
              messages.map((msg, index) => <li key={index}>{msg}</li>)
            ) : (
              <p>No messages received yet.</p>
            )}
          </ul>
        </div>
      </header>
    </div>
  );
}

export default App;
