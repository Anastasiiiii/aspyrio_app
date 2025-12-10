import { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import Cookies from 'js-cookie';

const useWebSocket = (username, onMessage) => {
    const [isConnected, setIsConnected] = useState(false);
    const clientRef = useRef(null);
    const onMessageRef = useRef(onMessage);

    useEffect(() => {
        onMessageRef.current = onMessage;
    }, [onMessage]);

    useEffect(() => {
        if (!username) {
            console.log('useWebSocket: No username provided');
            return;
        }

        const token = Cookies.get('token');
        if (!token) {
            console.log('useWebSocket: No token found');
            return;
        }

        console.log('useWebSocket: Initializing WebSocket for user:', username);
        const socket = new SockJS('http://localhost:8080/ws');
        const stompClient = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            connectHeaders: {
                'Authorization': `Bearer ${token}`
            },
            onConnect: () => {
                setIsConnected(true);
                console.log('WebSocket connected for user:', username);
                
                // Subscribe to user-specific queue for group training requests
                const userQueue = `/user/${username}/queue/training-requests`;
                console.log('Subscribing to:', userQueue);
                stompClient.subscribe(userQueue, (message) => {
                    console.log('Received message on user queue:', message.body);
                    try {
                        const data = JSON.parse(message.body);
                        console.log('Parsed message data:', data);
                        onMessageRef.current(data);
                    } catch (error) {
                        console.error('Error parsing WebSocket message:', error);
                    }
                });
                
                // Subscribe to individual training requests queue
                const individualQueue = `/user/${username}/queue/individual-training-requests`;
                console.log('Subscribing to:', individualQueue);
                stompClient.subscribe(individualQueue, (message) => {
                    console.log('Received individual training request:', message.body);
                    try {
                        const data = JSON.parse(message.body);
                        console.log('Parsed individual request data:', data);
                        onMessageRef.current(data);
                    } catch (error) {
                        console.error('Error parsing individual training request:', error);
                    }
                });
                
                // Also subscribe to topic as fallback
                const topicQueue = `/topic/training-requests/${username}`;
                console.log('Subscribing to topic:', topicQueue);
                stompClient.subscribe(topicQueue, (message) => {
                    console.log('Received message on topic:', message.body);
                    try {
                        const data = JSON.parse(message.body);
                        console.log('Parsed message data from topic:', data);
                        onMessageRef.current(data);
                    } catch (error) {
                        console.error('Error parsing WebSocket message from topic:', error);
                    }
                });
            },
            onDisconnect: () => {
                setIsConnected(false);
            },
            onStompError: (frame) => {
                console.error('STOMP error:', frame);
                console.error('STOMP error headers:', frame.headers);
                console.error('STOMP error body:', frame.body);
            },
            onWebSocketError: (event) => {
                console.error('WebSocket error:', event);
            },
            debug: (str) => {
                console.log('STOMP debug:', str);
            }
        });

        clientRef.current = stompClient;
        stompClient.activate();

        return () => {
            if (stompClient && stompClient.active) {
                stompClient.deactivate();
            }
        };
    }, [username]);

    return { isConnected, client: clientRef.current };
};

export default useWebSocket;

