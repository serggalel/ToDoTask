import {useEffect, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

const styles = {
    container: { maxWidth: '400px', margin: '80px auto', padding: '40px', backgroundColor: '#ffffff', borderRadius: '8px', boxShadow: '0 4px 15px rgba(0,0,0,0.1)', textAlign: 'center' },
    heading: { color: '#333', marginBottom: '25px', marginTop: 0 },
    form: { display: 'flex', flexDirection: 'column', gap: '15px' },
    input: { padding: '12px', border: '1px solid #ccc', borderRadius: '4px', fontSize: '15px', boxSizing: 'border-box' },
    button: { padding: '12px', backgroundColor: '#2196F3', color: 'white', border: 'none', borderRadius: '4px', fontSize: '16px', fontWeight: 'bold', cursor: 'pointer', marginTop: '10px' },
    footer: { marginTop: '25px', color: '#666', fontSize: '14px' },
    linkBtn: { background: 'none', border: 'none', color: '#2196F3', cursor: 'pointer', fontWeight: 'bold', fontSize: '14px', padding: '0 5px' }
};

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
        <div style={styles.container}>
            <h2 style={styles.heading}>Welcome Back</h2>

            <form onSubmit={handleLogin} style={styles.form}>
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    style={styles.input}
                />

                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    style={styles.input}
                />

                <button type="submit" style={styles.button}>
                    Log In
                </button>
            </form>

            <div style={styles.footer}>
                <p style={{ margin: '0 0 10px 0' }}>Don't have an account?</p>
                <button type="button" onClick={() => navigate('/register')} style={styles.linkBtn}>
                    Register Here
                </button>
            </div>
        </div>
    );
}