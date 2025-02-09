import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Treemap, ResponsiveContainer, Tooltip, Label } from 'recharts';
import '../styles/Subscription.css';

// Helper function to extract the last `.cs` file name from a file path
const extractFileName = (filePath) => {
  const fileName = filePath.split('/').pop(); // Extract the last part after the last '/'
  return fileName.endsWith('.cs') ? fileName : ''; // Ensure it's a `.cs` file
};

// TreeMapChart component to display Treemap visualization
const TreeMapChart = ({ data = [], topicName }) => {
  console.log(`Data for Treemap (${topicName}):`, data);

  return (
    <ResponsiveContainer width="100%" height={800}> {/* Increased height for a larger Treemap */}
      <Treemap
        width={1200}  // Increased width for a much wider Treemap
        height={800}  // Height remains the same
        data={data}   // Pass formatted data to the Treemap
        dataKey="size" // Size of blocks is based on 'changes'
        stroke="#fff"
        fill="#82ca9d"
        isAnimationActive={false}
      >
        {/* Add labels inside each block */}
        <Label
          position="center"
          content={({ x, y, width, height, value }) => {
            const label = value.name;
            return (
              <text x={x + width / 2} y={y + height / 2} textAnchor="middle" dominantBaseline="middle" fill="#000" fontSize={12}>
                {label}
              </text>
            );
          }}
        />
      </Treemap>
      <Tooltip content={({ payload }) => {
        if (payload && payload.length) {
          const data = payload[0].payload;
          return (
            <div style={{ backgroundColor: "white", padding: "5px", border: "1px solid #ccc" }}>
              <p><strong>{data.name}</strong></p>
              <p>Changes: {data.size}</p>
              <p>Additions: {data.additions}</p>
              <p>Deletions: {data.deletions}</p>
            </div>
          );
        }
        return null;
      }} />
    </ResponsiveContainer>
  );
};

// Main Subscription Component
const Subscription = ({ username }) => {
  const [url, setUrl] = useState('');
  const [connected, setConnected] = useState(false);
  const [subscriptions, setSubscriptions] = useState({}); // Store data for multiple topics
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [stompClient, setStompClient] = useState(null);
  const [topicName, setTopicName] = useState('');
  const navigate = useNavigate();

  // Function to extract topic name from GitHub URL
  const extractTopicName = (url) => {
    const match = url.match(/^https:\/\/github\.com\/([^\/]+)\/([^\/]+)\/tree\/([^\/]+)$/);
    if (!match) return null;
    const [, owner, repo, branch] = match;
    return `${owner}${repo}${branch}`;
  };

  // UseEffect to handle WebSocket connection
  useEffect(() => {
    const socket = new SockJS('http://localhost:8090/ws');
    const client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        setConnected(true);
        console.log('Connected to WebSocket');
      },
      onDisconnect: () => {
        setConnected(false);
        console.log('Disconnected from WebSocket');
      },
      onWebSocketError: (event) => {
        console.error('WebSocket Error:', event);
      },
    });

    client.activate();
    setStompClient(client);

    return () => {
      if (client.connected) {
        client.deactivate();
      }
    };
  }, []);

  // Handle subscription to the topic
  const handleSubscribe = async () => {
    const token = localStorage.getItem('authToken');
    if (!token) {
      alert("Please login first.");
      navigate('/'); // Redirect to login page if token is missing
      return;
    }

    const topic = extractTopicName(url);
    if (!topic) {
      setError('Invalid GitHub repository format.');
      return;
    }
    setTopicName(topic);

    try {
      const response = await axios.post('/api/secured/subscribe', { url }, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (response.status === 200) {
        setSuccessMessage('Successfully subscribed to the topic!');
        console.log('Subscription successful:', response.data);

        if (stompClient) {
          // Log subscription to topic
          console.log(`Subscribed to topic: ${topic}`);

          stompClient.subscribe(`/topic/${topic}`, (message) => {
            console.log('Received message for topic:', topic, message.body);

            try {
              // Parse the incoming message into UpdateRequest
              const updateRequest = JSON.parse(message.body);
              console.log('Parsed incoming message:', updateRequest);

              // Extract the files array from the update request
              const files = updateRequest.files || [];
              console.log(`Files for topic ${topic}:`, files);

              // Map the files data into the format needed by TreemapChart
              const formattedData = files.map((file) => ({
                name: extractFileName(file.filename), // Extract only the last `.cs` filename
                size: file.changes,                   // Size of the block is based on 'changes'
                additions: file.additions,
                deletions: file.deletions,
              }));

              // Update the state for the specific topic with the formatted data
              setSubscriptions((prevSubscriptions) => ({
                ...prevSubscriptions,
                [topic]: formattedData, // Store formatted data for each topic
              }));

            } catch (error) {
              console.error('Error parsing incoming message:', error);
            }
          });
        }
      }
    } catch (error) {
      console.error('Subscription error:', error);
      if (error.response?.status === 401) {
        // Token is expired or invalid, force logout and redirect
        alert("Session expired. Please log in again.");
        localStorage.removeItem('authToken'); // Remove invalid token
        navigate('/'); // Redirect to login page
      } else {
        setError('Server error. Please try again later.');
        setTimeout(() => setError(''), 3000);
      }
    }
  };

  return (
    <div className="subscription-container">
      <h2>Subscribe to a GitHub Repository</h2>
      <input
        type="url"
        placeholder="Enter GitHub URL"
        value={url}
        onChange={(e) => setUrl(e.target.value)}
      />
      <button onClick={handleSubscribe}>Subscribe</button>
      {error && <p className="error-message">{error}</p>}
      {successMessage && <p className="success-message">{successMessage}</p>}
      {connected && <p>Status: Connected to WebSocket</p>}
      {topicName && <p>Subscribed to topic: {topicName}</p>}

      <div className="message-box">
        <h2>Incoming Messages</h2>
        {/* Render TreeMap for each topic */}
        {Object.keys(subscriptions).map((topic) => (
          <div key={topic} className="treemap-container">
            <h3>Treemap for {topic}</h3>
            <TreeMapChart data={subscriptions[topic]} topicName={topic} />
          </div>
        ))}
      </div>
    </div>
  );
};

export default Subscription;
