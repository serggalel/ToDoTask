import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080', // Your Spring Boot URL
    headers: {
        'Content-Type': 'application/json',
    }
});

api.interceptors.request.use((config) => {

    const basicAuthToken = localStorage.getItem('basicAuthToken');

    if (basicAuthToken) {
        config.headers.Authorization = `Basic ${basicAuthToken}`;
    }
    return config;
});

export default api;