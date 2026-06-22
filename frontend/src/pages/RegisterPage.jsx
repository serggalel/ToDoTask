import {useEffect, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

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
        <div style={{ maxWidth: '400px', margin: '50px auto', textAlign: 'center' }}>
            <h2>Create an Account</h2>

            <form onSubmit={handleRegister} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>

                <input
                    type="text"
                    placeholder="First Name"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                    required
                />

                <input
                    type="text"
                    placeholder="Last Name"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                    required
                />

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

                <button type="submit" style={{ padding: '10px', marginTop: '10px' }}>
                    Register
                </button>
            </form>

            <div style={{ marginTop: '20px' }}>
                <p>Already have an account?</p>
                <button type="button" onClick={() => navigate('/login')}>
                    Log In Here
                </button>
            </div>
        </div>
    );
}