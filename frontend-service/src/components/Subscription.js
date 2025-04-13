import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import * as d3 from 'd3';
import '../styles/Subscription.css';
import '../styles/Tooltip.css';

const buildHierarchy = (files) => {
  const root = {
    name: 'root',
    children: [],
    value: 0,
    fullPath: '',
    isDirectory: true
  };

  const totalChanges = files.reduce((sum, file) => sum + Math.max(file.changes, 1), 0) || 1;

  files.forEach(file => {
    const pathParts = file.filename.split('/').filter(p => p);
    let currentNode = root;
    
    const normalizedChanges = (Math.max(file.changes, 1) / totalChanges) * 100;

    pathParts.forEach((part, index) => {
      const isFile = index === pathParts.length - 1;
      let childNode = currentNode.children.find(n => n.name === part);

      if (!childNode) {
        childNode = {
          name: part,
          children: [],
          value: 0,
          fullPath: [...currentNode.fullPath.split('/'), part].join('/'),
          isDirectory: !isFile,
          details: isFile ? {...file, changes: normalizedChanges} : null
        };
        currentNode.children.push(childNode);
      }

      if (isFile) {
        childNode.value = normalizedChanges;
      }
      currentNode.value += normalizedChanges;
      
      if (!isFile) currentNode = childNode;
    });
  });

  return root;
};

const Subscription = ({ username }) => {
  const [url, setUrl] = useState('');
  const [branches, setBranches] = useState({});
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();
  const stompClient = useRef(null);
  const svgRefs = useRef({});
  const tooltipRef = useRef(null);

  useEffect(() => {
    const storedUsername = localStorage.getItem('username');
    if (!storedUsername) navigate('/');

    const socket = new SockJS('http://localhost:8090/ws');
    stompClient.current = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      connectHeaders: {
        Authorization: `Bearer ${localStorage.getItem('authToken')}`
      },
      onConnect: () => {
        stompClient.current.subscribe(`/topic/messages/${storedUsername}`, (message) => {
          try {
            const updateRequest = JSON.parse(message.body);
            const hierarchy = buildHierarchy(updateRequest.files);
            
            setBranches(prev => ({
              ...prev,
              [updateRequest.branch]: {
                data: hierarchy,
                meta: {
                  count: updateRequest.files.length,
                  timestamp: new Date().toISOString()
                }
              }
            }));
          } catch (e) {
            console.error('Error processing message:', e);
          }
        });
      }
    });
    stompClient.current.activate();

    return () => stompClient.current?.deactivate();
  }, [navigate]);

  const updateTooltipPosition = (event) => {
    const tooltip = d3.select(tooltipRef.current);
    if (!tooltip.node()) return;

    const margin = 20;
    let x = event.clientX + margin;
    let y = event.clientY + margin;

    tooltip
      .style('left', `${x}px`)
      .style('top', `${y}px`);
  };

  useEffect(() => {
    const drawTreemap = (branch, data) => {
      const container = svgRefs.current[branch];
      if (!container) return;

      const width = container.clientWidth;
      const height = 900; // Увеличена высота в 1.5 раза
      const tooltip = d3.select(tooltipRef.current);

      d3.select(container).selectAll('*').remove();
      const svg = d3.select(container)
        .append('svg')
        .attr('width', width)
        .attr('height', height);

      const root = d3.hierarchy(data)
        .sum(d => d.value)
        .sort((a, b) => b.value - a.value);

      d3.treemap()
        .size([width, height])
        .paddingTop(0)
        .paddingInner(0)
        .round(false)
        (root);

      const nodes = svg.selectAll('g')
        .data(root.descendants().filter(d => d.depth > 0))
        .enter().append('g')
        .attr('transform', d => `translate(${d.x0},${d.y0})`);

      const SIX_MONTHS_AGO = new Date();
      SIX_MONTHS_AGO.setMonth(SIX_MONTHS_AGO.getMonth() - 6);

      nodes.append('rect')
        .attr('width', d => d.x1 - d.x0)
        .attr('height', d => d.y1 - d.y0)
        .attr('fill', d => {
          if (d.data.isDirectory) return '#2d2d2d'; // Темно-серый цвет для директорий
          if (!d.data.details) return '#e0e0e0';
          const fileDate = new Date(d.data.details.date);
          return fileDate < SIX_MONTHS_AGO ? '#ff4444' : '#4CAF50';
        })
        .attr('stroke', '#fff')
        .attr('rx', 2)
        .style('cursor', 'pointer')
        .on('mouseover', (event, d) => {
          tooltip.style('opacity', 1)
            .html(`
              <div class="tooltip-header">
                ${d.data.isDirectory ? '📁 Directory' : '📄 File'}
              </div>
              <div class="tooltip-content">
                <div class="path">${d.data.fullPath}</div>
                ${d.data.details ? `
                  <div class="author-info">
                    <span class="author">👤 ${d.data.details.author}</span>
                    <span class="date">📅 ${new Date(d.data.details.date).toLocaleDateString('en-US', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric',
                      hour: '2-digit',
                      minute: '2-digit'
                    })}</span>
                  </div>
                  <div class="metrics">
                    <div class="metric">
                      <span class="label">Changes:</span>
                      <span class="value">${d.data.details.changes.toFixed(1)}%</span>
                    </div>
                    <div class="metric">
                      <span class="label">Additions:</span>
                      <span class="value">${d.data.details.additions}</span>
                    </div>
                    <div class="metric">
                      <span class="label">Deletions:</span>
                      <span class="value">${d.data.details.deletions}</span>
                    </div>
                    <div class="metric">
                      <span class="label">Status:</span>
                      <span class="value status-${d.data.details.status}">${d.data.details.status}</span>
                    </div>
                  </div>
                ` : `
                  <div class="metrics">
                    <div class="metric">
                      <span class="label">Contains:</span>
                      <span class="value">${d.data.children.length} items</span>
                    </div>
                    <div class="metric">
                      <span class="label">Total Changes:</span>
                      <span class="value">${d.data.value.toFixed(1)}%</span>
                    </div>
                  </div>
                `}
              </div>
            `);
          updateTooltipPosition(event);
        })
        .on('mousemove', updateTooltipPosition)
        .on('mouseout', () => tooltip.style('opacity', 0));
    };

    Object.entries(branches).forEach(([branch, { data }]) => {
      drawTreemap(branch, data);
    });
  }, [branches]);

  const handleSubscribe = async () => {
    try {
      const token = localStorage.getItem('authToken');
      if (!token) throw new Error('Authentication required');

      const urlPattern = /https:\/\/github.com\/([^/]+)\/([^/]+)\/tree\/([^/]+)/;
      if (!urlPattern.test(url)) throw new Error('Invalid GitHub URL format');

      await axios.post('/api/secured/subscribe', { url }, {
        headers: { Authorization: `Bearer ${token}` }
      });

      setSuccess('Successfully subscribed to branch!');
      setTimeout(() => setSuccess(''), 3000);
      setUrl('');

    } catch (err) {
      setError(err.message);
      setTimeout(() => setError(''), 3000);
    }
  };

  return (
    <div className="subscription-container">
      <div className="header">
        <h1>GitHub Changes Visualizer</h1>
        <div className="controls">
          <input
            type="url"
            placeholder="Enter GitHub branch URL"
            value={url}
            onChange={(e) => setUrl(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSubscribe()}
          />
          <button onClick={handleSubscribe}>
            Visualize Changes
          </button>
        </div>
        {error && <div className="error">{error}</div>}
        {success && <div className="success">{success}</div>}
      </div>

      <div className="visualization">
        {Object.entries(branches).map(([branch, { data, meta }]) => (
          <div 
          key={branch} 
          className="branch"
        >
          <div className="branch-info">
            <h3>{branch}</h3>
            <div className="stats">
              <span>Files: {meta.count}</span>
              <span>Updated: {new Date(meta.timestamp).toLocaleTimeString()}</span>
            </div>
          </div>
          <div 
            className="treemap-container"
            ref={el => svgRefs.current[branch] = el}
          />
        </div>
        ))}
      </div>
      
      <div ref={tooltipRef} className="tooltip"></div>
    </div>
  );
};

export default Subscription;