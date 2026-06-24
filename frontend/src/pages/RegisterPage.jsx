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

export default function RegisterPage() {

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const navigate = useNavigate();

    useEffect(() => {
        const userString = localStorage.getItem('currentUser');
        if (userString) {
            navigate('/home');
        }
    }, [navigate]);

    const handleRegister = async (e) => {
        e.preventDefault();

        try {
            await api.post('/user/register', {
                firstName: firstName,
                lastName: lastName,
                email: email,
                password: password
            });

            const credentials = btoa(`${email}:${password}`);

            const response = await api.get('/user/me', {
                headers: { Authorization: `Basic ${credentials}` }
            });

            localStorage.setItem('basicAuthToken', credentials);
            localStorage.setItem('currentUser', JSON.stringify(response.data));

            navigate('/home');

        } catch (error) {
            console.error("Full error object:", error);
            if (error.response && error.response.data) {
                alert(error.response.data);
            } else {
                alert("Registration failed. Please check your connection.");
            }
        }
    };

    return (
        <div style={styles.container}>
            <h2 style={styles.heading}>Create an Account</h2>

            <form onSubmit={handleRegister} style={styles.form}>

                <input
                    type="text"
                    placeholder="First Name"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                    required
                    style={styles.input}
                />

                <input
                    type="text"
                    placeholder="Last Name"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                    required
                    style={styles.input}
                />

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
                    Register
                </button>
            </form>

            <div style={styles.footer}>
                <p style={{ margin: '0 0 10px 0' }}>Already have an account?</p>
                <button type="button" onClick={() => navigate('/login')} style={styles.linkBtn}>
                    Log In Here
                </button>
            </div>
        </div>
    );
}