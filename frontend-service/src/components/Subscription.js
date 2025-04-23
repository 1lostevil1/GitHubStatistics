import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import * as d3 from 'd3';
import '../styles/Subscription.css';
import '../styles/Tooltip.css';

const buildHierarchy = (files, branch) => {
  const root = {
    name: 'root',
    children: [],
    value: 0,
    fullPath: '',
    isDirectory: true,
    branch: branch,
    isCollapsed: false,
    parent: null
  };

  files.forEach(file => {
    const pathParts = file.filename.split('/').filter(p => p);
    let currentNode = root;

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
          details: isFile ? { ...file } : null,
          branch: branch,
          isCollapsed: !isFile,
          parent: currentNode
        };
        currentNode.children.push(childNode);
      }

      if (isFile) {
        childNode.value = 1;
        let parent = childNode.parent;
        while (parent) {
          parent.value += 1;
          parent = parent.parent;
        }
      }

      if (!isFile) currentNode = childNode;
    });
  });

  return root;
};

const prepareDataForTreemap = (node) => {
  if (node.isDirectory && node.isCollapsed) {
    return { ...node, children: [] };
  }
  return {
    ...node,
    children: node.children ? node.children.map(prepareDataForTreemap) : []
  };
};

const updateNodeInTree = (node, targetFullPath, isCollapsed) => {
  if (node.fullPath === targetFullPath) {
    return { ...node, isCollapsed };
  }
  if (node.children) {
    return {
      ...node,
      children: node.children.map(child => 
        updateNodeInTree(child, targetFullPath, isCollapsed))
    };
  }
  return node;
};

const expandPath = (node, pathParts) => {
  if (pathParts.length === 0) return node;
  
  const [currentPart, ...remainingParts] = pathParts;
  const childIndex = node.children.findIndex(child => child.name === currentPart);
  
  if (childIndex === -1) return node;

  const child = node.children[childIndex];
  const updatedChild = remainingParts.length > 0 
    ? expandPath(
        { ...child, isCollapsed: false },
        remainingParts
      )
    : child;

  const newChildren = [...node.children];
  newChildren[childIndex] = updatedChild;
  
  return {
    ...node,
    children: newChildren,
    isCollapsed: false
  };
};

const Subscription = ({ username }) => {
  const [url, setUrl] = useState('');
  const [branches, setBranches] = useState({});
  const [refactoredFiles, setRefactoredFiles] = useState({});
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isConnected, setIsConnected] = useState(false);
  const navigate = useNavigate();
  const stompClient = useRef(null);
  const svgRefs = useRef({});
  const tooltipRef = useRef(null);

  const handleFileClick = (branch, filename) => {
    setBranches(prev => {
      const branchData = prev[branch];
      if (!branchData) return prev;

      const pathParts = filename.split('/').filter(p => p);
      const newData = expandPath(branchData.data, pathParts);

      return {
        ...prev,
        [branch]: {
          ...branchData,
          data: newData
        }
      };
    });
    setTimeout(() => {
      animateFileHighlight(branch, filename);
    }, 100);
  };

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
        setIsConnected(true);
        stompClient.current.subscribe(
          `/topic/messages/${storedUsername}`,
          (message) => {
            try {
              const updateRequest = JSON.parse(message.body);
              const hierarchy = buildHierarchy(updateRequest.files, updateRequest.branch);
              
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

              setRefactoredFiles(prev => ({
                ...prev,
                [updateRequest.branch]: updateRequest.files
                .filter(file => file.refactors > 0)
                .sort((a, b) => b.refactors - a.refactors)
              }));
            } catch (e) {
              console.error('Error processing message:', e);
            }
          }
        );
      }
    });
    stompClient.current.activate();

    return () => stompClient.current?.deactivate();
  }, [navigate]);

  useEffect(() => {
    if (!isConnected) return;

    const fetchCurrentUpdates = async () => {
      try {
        const token = localStorage.getItem('authToken');
        const response = await axios.get(
          'http://localhost:8090/api/secured/currentUpdates',
          { headers: { Authorization: `Bearer ${token}` } }
        );

        const updates = response.data;
        const newBranches = {};
        const newRefactored = {};

        Object.entries(updates).forEach(([branch, data]) => {
          const hierarchy = buildHierarchy(data.files, branch);
          newBranches[branch] = {
            data: hierarchy,
            meta: {
              count: data.files.length,
              timestamp: new Date().toISOString()
            }
          };
          newRefactored[branch] = data.files
          .filter(file => file.refactors > 0)
          .sort((a, b) => b.refactors - a.refactors);
        });

        setBranches(newBranches);
        setRefactoredFiles(newRefactored);
      } catch (err) {
        console.error('Error fetching current updates:', err);
      }
    };

    fetchCurrentUpdates();
  }, [isConnected]);

  const updateTooltipPosition = (event) => {
    const tooltip = d3.select(tooltipRef.current);
    if (!tooltip.node()) return;

    const margin = 20;
    const x = event.clientX + margin;
    const y = event.clientY + margin;

    tooltip
      .style('left', `${x}px`)
      .style('top', `${y}px`);
  };

  const toggleCollapse = (nodeData, isCollapsed) => {
    setBranches(prev => {
      const branchKey = nodeData.branch;
      const branch = prev[branchKey];
      if (!branch) return prev;

      const updatedData = updateNodeInTree(branch.data, nodeData.fullPath, isCollapsed);
      return {
        ...prev,
        [branchKey]: {
          ...branch,
          data: {
            ...updatedData,
            x0: branch.data.x0,
            y0: branch.data.y0,
            x1: branch.data.x1,
            y1: branch.data.y1
          }
        }
      };
    });
  };

  const animateFileHighlight = (branch, filename) => {
    const container = svgRefs.current[branch];
    if (!container) return;
  
    const fullPath = filename.startsWith('/') ? filename : `/${filename}`;
    const target = d3.select(container)
      .select(`rect[data-fullpath="${fullPath}"]`);
  
    if (target.empty()) return;
  
    const originalColor = target.attr('fill');
    const highlightColor = '#00ffff'; // Цвет мигания
  
    target
      .transition()
      .duration(200)
      .attr('fill', highlightColor)
      .transition()
      .duration(200)
      .attr('fill', originalColor)
      .transition()
      .duration(200)
      .attr('fill', highlightColor)
      .transition()
      .duration(200)
      .attr('fill', originalColor)
      .transition()
      .duration(200)
      .attr('fill', highlightColor)
      .transition()
      .duration(200)
      .attr('fill', originalColor);
  };

  useEffect(() => {
    const drawTreemap = (branch, data) => {
      const container = svgRefs.current[branch];
      if (!container) return;

      const width = container.clientWidth;
      const height = container.clientHeight;
      const tooltip = d3.select(tooltipRef.current);
      const SIX_MONTHS_AGO = new Date();
      SIX_MONTHS_AGO.setMonth(SIX_MONTHS_AGO.getMonth() - 6);

      const svg = d3.select(container)
        .selectAll('svg')
        .data([null])
        .join('svg')
        .attr('width', width)
        .attr('height', height);

      const preparedData = prepareDataForTreemap(data);
      const root = d3.hierarchy(preparedData)
        .sum(d => d.value)
        .sort((a, b) => b.value - a.value);

      d3.treemap()
        .size([width, height])
        .paddingTop(8)
        .paddingInner(4)
        .paddingOuter(2)
        .round(true)(root);

      const nodes = svg.selectAll('g')
        .data(root.descendants().filter(d => d.depth > 0), d => d.data.fullPath);

      nodes.exit().remove();

      const enter = nodes.enter()
        .append('g')
        .attr('transform', d => `translate(${d.x0},${d.y0})`)
        .attr('opacity', 0);

      enter.append('rect')
        .attr('data-fullpath', d => d.data.fullPath)
        .attr('width', d => d.x1 - d.x0)
        .attr('height', d => d.y1 - d.y0)
        .attr('rx', 6)
        .attr('ry', 6)
        .attr('stroke', '#fff')
        .attr('stroke-width', 1.5)
        .style('cursor', 'pointer')
        .attr('fill', d => {
          if (d.data.isDirectory) return '#2d2d2d';
          if (!d.data.details) return '#e0e0e0';
          const fileDate = new Date(d.data.details.date);
          return fileDate < SIX_MONTHS_AGO ? '#ff4444' : '#4CAF50';
        })
        .on('click', (event, d) => {
          event.stopPropagation();
          if (d.data.isDirectory) toggleCollapse(d.data, !d.data.isCollapsed);
        })
        .on('contextmenu', (event, d) => {
          event.preventDefault();
          if (d.data.isDirectory) toggleCollapse(d.data, true);
        })
        .on('mouseover', (event, d) => {
          tooltip.style('opacity', 1).html(`
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
                  ${Object.entries(d.data.details)
                    .filter(([key]) => !['filename', 'author', 'date'].includes(key))
                    .map(([key, value]) => `
                      <div class="metric">
                        <span class="label">${key}:</span>
                        <span class="value">${value}</span>
                      </div>`
                    ).join('')}
                </div>
              ` : `
              `}
            </div>
          `);
          updateTooltipPosition(event);
        })
        .on('mousemove', updateTooltipPosition)
        .on('mouseout', () => tooltip.style('opacity', 0));

      enter.merge(nodes)
        .transition()
        .duration(300)
        .attr('transform', d => `translate(${d.x0},${d.y0})`)
        .attr('opacity', 1);

      nodes.select('rect')
        .transition()
        .duration(300)
        .attr('width', d => d.x1 - d.x0)
        .attr('height', d => d.y1 - d.y0);
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

      setSuccess('Successfully subscribed!');
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
          <button onClick={handleSubscribe}>Visualize Changes</button>
        </div>
        {error && <div className="error">{error}</div>}
        {success && <div className="success">{success}</div>}
      </div>

      <div className="main-content">
        <div className="visualization">
          {Object.entries(branches).map(([branch, { data, meta }]) => (
            <div key={branch} className="branch">
              <div className="branch-info">
                <h3>{branch}</h3>
                <div className="stats">
                  <span>Files: {meta.count}</span>
                  <span>Updated: {new Date(meta.timestamp).toLocaleTimeString()}</span>
                </div>
              </div>
              <div className="branch-content">
                <div 
                  className="treemap-container"
                  ref={el => svgRefs.current[branch] = el}
                />
                <div className="refactored-list">
                  <h3>Refactored Files ({refactoredFiles[branch]?.length || 0})</h3>
                  {refactoredFiles[branch]?.length === 0 ? (
                    <div className="no-refactored">No refactored files</div>
                  ) : (
                    refactoredFiles[branch]?.map(file => (
                      <div 
                        key={`${branch}-${file.filename}`}
                        className="refactored-item"
                        onClick={() => handleFileClick(branch, file.filename)}
                        style={{ cursor: 'pointer' }}
                      >
                        <div className="file-path">{file.filename}</div>
                        <div className="file-stats">
                          <span>Author: {file.author}</span>
                          <span>Refactors: {file.refactors}</span>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
      
      <div ref={tooltipRef} className="tooltip"></div>
    </div>
  );
};

export default Subscription;