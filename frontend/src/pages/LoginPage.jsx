import {useEffect, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

export default function LoginPage() {

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const userString = localStorage.getItem('currentUser');
        if (userString) {
            navigate('/home');
        }
    }, [navigate]);

    const handleLogin = async (e) => {
        e.preventDefault();

        const credentials = btoa(`${email}:${password}`);

        try {
            const response = await api.get('/user/me', {
                headers: { Authorization: `Basic ${credentials}` }
            });

            localStorage.setItem('basicAuthToken', credentials);
            localStorage.setItem('currentUser', JSON.stringify(response.data));

            navigate('/home');
        } catch (error) {
            alert("Wrong email or password!");
        }
    };

    return (
        <div style={{ maxWidth: '400px', margin: '50px auto', textAlign: 'center' }}>
            <h2>Welcome Back</h2>

            <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>

                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />

                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />

                <button type="submit">Log In</button>
            </form>

            <div style={{ marginTop: '20px' }}>
                <p>Don't have an account?</p>
                <button onClick={() => navigate('/register')}>
                    Register Here
                </button>
            </div>
        </div>
    );
}