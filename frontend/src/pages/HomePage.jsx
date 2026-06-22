import todotasksImage from '../assets/todotasksImage.jpg'

export default function HomePage() {

    const userString = localStorage.getItem('currentUser');
    const currentUser = userString ? JSON.parse(userString) : { firstName: 'Guest' };

    return (
        <div style={{ textAlign: 'center', maxWidth: '800px', margin: '0 auto' }}>
            <h1>Welcome, {currentUser.firstName}!</h1>
            <p style={{ color: '#666', marginBottom: '30px' }}>
                This is your central dashboard. Use the navigation bar above to navigate.
            </p>

            <img
                src={todotasksImage}
                alt="Home Page"
                style={{
                    width: '100%',
                    height: 'auto',
                    borderRadius: '12px',
                    boxShadow: '0 4px 12px rgba(0,0,0,0.15)'
                }}
            />
        </div>
    );
}