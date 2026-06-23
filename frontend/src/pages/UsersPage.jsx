import { useState, useEffect } from 'react';
import api from '../api';

export default function UsersPage() {

    const [users, setUsers] = useState([]);
    const [isLoading, setIsLoading] = useState(false);

    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [pageSize, setPageSize] = useState(5);
    const [totalElements, setTotalElements] = useState(0);

    const [editingUser, setEditingUser] = useState(null);

    const fetchUsers = async (pageIndex) => {
        setIsLoading(true);
        try {
            const response = await api.get(`/user/all?page=${pageIndex}&size=${pageSize}`);

            setUsers(response.data.content || []);
            const pageData = response.data.page || response.data;
            const pages = pageData.totalPages || 1;

            setTotalPages(pages);
            setTotalElements(pageData.totalElements || 0);

            if (pageIndex >= pages && pages > 0) {
                setCurrentPage(pages - 1);
            }
        } catch (error) {
            console.error("Failed to fetch users:", error);
            alert("Could not load users. Check your backend connection.");
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchUsers(currentPage, pageSize);
    }, [currentPage, pageSize]);


    const handleDelete = async (userId) => {
        if (window.confirm("Are you sure you want to delete this user?")) {
            try {
                await api.delete(`/user/delete/${userId}`);
                fetchUsers(currentPage);
            } catch (error) {
                console.error("Delete failed:", error);
                alert("Failed to delete user.");
            }
        }
    };

    const handleSaveEdit = async (e) => {
        e.preventDefault();
        try {
            await api.put(`/user/update`, {
                id: editingUser.id,
                firstName: editingUser.firstName,
                lastName: editingUser.lastName,
                email: editingUser.email,
                role: editingUser.role
            });
            setEditingUser(null);
            fetchUsers(currentPage);
        } catch (error) {
            console.error("Edit failed:", error);
            alert("Failed to update user.");
        }
    };

    return (
        <div style={{ maxWidth: '800px', margin: '0 auto' }}>
            <h2>User Management</h2>

            {/* The Users Table */}
            <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px', textAlign: 'left' }}>
                <thead>
                <tr style={{ backgroundColor: '#f4f4f4', borderBottom: '2px solid #ddd' }}>
                    <th style={{ padding: '12px' }}>ID</th>
                    <th style={{ padding: '12px' }}>First Name</th>
                    <th style={{ padding: '12px' }}>Last Name</th>
                    <th style={{ padding: '12px' }}>Email</th>
                    <th style={{ padding: '12px' }}>Role</th>
                    <th style={{ padding: '12px' }}>Actions</th>
                </tr>
                </thead>

                <tbody style={{ opacity: isLoading ? 0.5 : 1, transition: 'opacity 0.2s' }}>
                {users.map(user => (
                    <tr key={user.id} style={{ borderBottom: '1px solid #ddd' }}>
                        <td style={{ padding: '12px' }}>{user.id}</td>
                        <td style={{ padding: '12px' }}>{user.firstName}</td>
                        <td style={{ padding: '12px' }}>{user.lastName}</td>
                        <td style={{ padding: '12px' }}>{user.email}</td>
                        <td style={{ padding: '12px' }}>
                                <span style={{
                                    padding: '4px 8px',
                                    borderRadius: '12px',
                                    backgroundColor: user.role === 'ADMIN' ? '#ffebee' : '#e8f5e9',
                                    color: user.role === 'ADMIN' ? '#c62828' : '#2e7d32',
                                    fontWeight: 'bold',
                                    fontSize: '0.85em'
                                }}>
                                    {user.role || 'USER'}
                                </span>
                        </td>
                        <td style={{ padding: '12px', display: 'flex', gap: '10px' }}>
                            <button
                                onClick={() => setEditingUser(user)}
                                style={{ backgroundColor: '#4CAF50', color: 'white', border: 'none', padding: '5px 10px', cursor: 'pointer', borderRadius: '4px' }}
                            >
                                Edit
                            </button>
                            <button
                                onClick={() => handleDelete(user.id)}
                                style={{ backgroundColor: '#f44336', color: 'white', border: 'none', padding: '5px 10px', cursor: 'pointer', borderRadius: '4px' }}
                            >
                                Delete
                            </button>
                        </td>
                    </tr>
                ))}

                {/* Fallback if the database is empty */}
                {users.length === 0 && !isLoading && (
                    <tr>
                        <td colSpan="6" style={{ padding: '20px', textAlign: 'center' }}>No users found.</td>
                    </tr>
                )}
                </tbody>
            </table>

            {/* Pagination Controls */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '20px' }}>
                <div>
                    <label style={{ marginRight: '10px' }}>Rows per page:</label>
                    <select
                        value={pageSize}
                        onChange={(e) => {
                            setPageSize(Number(e.target.value));
                            setCurrentPage(0); // Reset to page 0 when changing size
                        }}
                        style={{ padding: '4px 8px', borderRadius: '4px' }}
                    >
                        <option value="5">5</option>
                        <option value="10">10</option>
                        <option value="20">20</option>
                    </select>
                </div>

                <div>
                    <button
                        disabled={currentPage === 0 || isLoading}
                        onClick={() => setCurrentPage(prev => prev - 1)}
                        style={{ padding: '8px 16px', cursor: (currentPage === 0 || isLoading) ? 'not-allowed' : 'pointer' }}
                    >
                        Previous
                    </button>

                    <span style={{ margin: '0 15px' }}>
                        Page {currentPage + 1} of {totalPages}
                        {totalElements > 0 && <span style={{ color: '#666', fontSize: '0.9em', marginLeft: '10px' }}>({totalElements} users)</span>}
                    </span>

                    <button
                        // Disable if we are on the last page or loading
                        disabled={currentPage >= totalPages - 1 || isLoading}
                        onClick={() => setCurrentPage(prev => prev + 1)}
                        style={{ padding: '8px 16px', cursor: (currentPage >= totalPages - 1 || isLoading) ? 'not-allowed' : 'pointer' }}
                    >
                        Next
                    </button>
                </div>
            </div>

            {/* Edit User Modal Overlay */}
            {editingUser && (
                <div style={{
                    position: 'fixed', top: 0, left: 0, width: '100%', height: '100%',
                    backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center'
                }}>
                    <div style={{ backgroundColor: 'white', padding: '30px', borderRadius: '8px', width: '400px' }}>
                        <h3>Edit User (ID: {editingUser.id})</h3>
                        <form onSubmit={handleSaveEdit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                            <input
                                type="text"
                                value={editingUser.firstName}
                                onChange={e => setEditingUser({...editingUser, firstName: e.target.value})}
                                required
                            />
                            <input
                                type="text"
                                value={editingUser.lastName}
                                onChange={e => setEditingUser({...editingUser, lastName: e.target.value})}
                                required
                            />
                            <input
                                type="email"
                                value={editingUser.email}
                                onChange={e => setEditingUser({...editingUser, email: e.target.value})}
                                required
                                style={{ padding: '8px' }}
                            />

                            <select
                                value={editingUser.role || 'USER'}
                                onChange={e => setEditingUser({...editingUser, role: e.target.value})}
                                required
                                style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px', backgroundColor: 'white' }}
                            >
                                <option value="USER">USER</option>
                                <option value="ADMIN">ADMIN</option>
                            </select>

                            <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                                <button type="submit" style={{ flex: 1, padding: '10px', backgroundColor: '#2196F3', color: 'white', border: 'none' }}>
                                    Save
                                </button>
                                <button type="button" onClick={() => setEditingUser(null)} style={{ flex: 1, padding: '10px' }}>
                                    Cancel
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}