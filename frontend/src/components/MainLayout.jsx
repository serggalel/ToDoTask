import { Outlet, Link, useNavigate, Navigate } from 'react-router-dom';

export default function MainLayout() {
    const navigate = useNavigate();

    const userString = localStorage.getItem('currentUser');
    const currentUser = userString ? JSON.parse(userString) : null;

    if (!currentUser) {
        return <Navigate to="/login" replace />;
    }

    const handleLogout = () => {
        localStorage.removeItem('basicAuthToken');
        localStorage.removeItem('currentUser');
        navigate('/login');
    };

    return (
        <div>
            <nav style={{
                display: 'flex',
                gap: '20px',
                padding: '15px',
                backgroundColor: '#282c34',
                color: 'white',
                alignItems: 'center'
            }}>
                <Link to="/home" style={{ color: 'white', textDecoration: 'none' }}>Home</Link>
                <Link to="/tasks" style={{ color: 'white', textDecoration: 'none' }}>My Tasks</Link>

                {currentUser.role === 'admin' && (
                    <Link to="/users" style={{ color: 'white', textDecoration: 'none' }}>All Users</Link>
                )}

                <button
                    onClick={handleLogout}
                    style={{ marginLeft: 'auto', padding: '5px 15px', cursor: 'pointer' }}
                >
                    Logout
                </button>
            </nav>

            <div style={{ padding: '20px' }}>
                <Outlet />
            </div>
        </div>
    );
}