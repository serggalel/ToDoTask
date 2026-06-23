import { useState, useEffect } from 'react';
import api from '../api';
import { useNavigate } from "react-router-dom";

const PRIORITY_OPTIONS = ['LOW', 'MEDIUM', 'HIGH'];
const STATE_OPTIONS = ['TODO', 'IN_PROGRESS', 'DONE'];

const btn = {
    base:   { border: 'none', borderRadius: '4px', padding: '6px 14px', cursor: 'pointer', fontWeight: 500 },
    green:  { backgroundColor: '#4CAF50', color: 'white' },
    red:    { backgroundColor: '#f44336', color: 'white' },
    blue:   { backgroundColor: '#2196F3', color: 'white' },
    grey:   { backgroundColor: '#e0e0e0', color: '#333' },
    small:  { padding: '3px 9px', fontSize: '12px' },
};
const B = ({ color = 'grey', small, style, ...props }) => (
    <button style={{ ...btn.base, ...btn[color], ...(small ? btn.small : {}), ...style }} {...props} />
);

export default function TasksPage() {
    const navigate = useNavigate();
    const userString = localStorage.getItem('currentUser');
    const currentUser = userString ? JSON.parse(userString) : null;

    useEffect(() => { if (!currentUser) navigate('/login'); }, []);

    const [ownedTasks,      setOwnedTasks]      = useState([]);
    const [collabTasks,     setCollabTasks]      = useState([]);
    const [isLoading,       setIsLoading]        = useState(false);

    const [ownedPage,       setOwnedPage]        = useState(0);
    const [ownedTotalPages, setOwnedTotalPages]  = useState(1);
    const [collabPage,      setCollabPage]        = useState(0);
    const [collabTotalPages,setCollabTotalPages]  = useState(1);

    const [taskToDelete,    setTaskToDelete]     = useState(null);

    const [editingTask,     setEditingTask]      = useState(null);
    const [editForm,        setEditForm]         = useState({ name: '', priority: 'LOW', state: 'TODO' });

    const [currentCollabs,          setCurrentCollabs]         = useState([]);
    const [currentCollabsPage,      setCurrentCollabsPage]     = useState(0);
    const [currentCollabsTotalPages,setCurrentCollabsTotalPages] = useState(1);

    const [possibleCollabs,  setPossibleCollabs]  = useState([]);
    const [selectedCollabId, setSelectedCollabId] = useState('');

    const [collabsLoading, setCollabsLoading] = useState(false);

    const [isCreatingTask,  setIsCreatingTask]   = useState(false);
    const [createForm,      setCreateForm]       = useState({ name: '', priority: 'LOW' });

    // ── fetch main task lists ─────────────────────────────────────────────────
    const fetchTasks = async (oPage, cPage) => {
        if (!currentUser) return;
        setIsLoading(true);
        try {
            const url = `/task/getByUser/${currentUser.id}?owned_page=${oPage}&owned_size=5&collab_page=${cPage}&collab_size=5`;
            const { data } = await api.get(url);
            const ownedData = data.ownedTasks || {};
            const collabData = data.collabTasks || {};

            setOwnedTasks(ownedData.content || []);
            setOwnedTotalPages(ownedData.page?.totalPages ?? ownedData.totalPages ?? 1);

            setCollabTasks(collabData.content || []);
            setCollabTotalPages(collabData.page?.totalPages ?? collabData.totalPages ?? 1);
        } catch (err) {
            console.error("Failed to fetch tasks:", err);
        } finally {
            setIsLoading(false);
        }
    };

    const fetchCurrentCollabs = async (taskId, page) => {
        setCollabsLoading(true);
        try {
            const { data } = await api.get(`/user/getCollabs/${taskId}?page=${page}&size=5`);
            setCurrentCollabs(data.content || []);
            setCurrentCollabsTotalPages(data.page?.totalPages ?? data.totalPages ?? 1);
        } catch (err) {
            console.error("Failed to fetch current collaborators:", err);
        } finally {
            setCollabsLoading(false);
        }
    };

    const fetchPossibleCollabs = async (taskId) => {
        try {
            const { data } = await api.get(`/user/getPossCollabs/${taskId}`);
            setPossibleCollabs(data || []);
            setSelectedCollabId('');
        } catch (err) {
            console.error("Failed to fetch possible collaborators:", err);
        }
    };

    useEffect(() => {
        fetchTasks(ownedPage, collabPage);
    }, [ownedPage, collabPage]);

    useEffect(() => {
        if (!editingTask) return;
        setEditForm({ name: editingTask.name || '', priority: editingTask.priority || 'LOW', state: editingTask.state || 'TODO' });
        setCurrentCollabsPage(0);
        fetchCurrentCollabs(editingTask.id, 0);
        fetchPossibleCollabs(editingTask.id);
    }, [editingTask?.id]);

    useEffect(() => {
        if (editingTask) fetchCurrentCollabs(editingTask.id, currentCollabsPage);
    }, [currentCollabsPage]);

    const handleCreateTask = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            await api.post(`/task/create`, {
                name: createForm.name,
                priority: createForm.priority
            });
            setIsCreatingTask(false);
            setCreateForm({ name: '', priority: 'LOW' });
            fetchTasks(ownedPage, collabPage);
        } catch (err) {
            console.error("Create failed:", err);
            alert(err.response?.data || "Failed to create task");
            setIsLoading(false);
        }
    };

    const handleDelete = async () => {
        if (!taskToDelete) return;
        setIsLoading(true);
        try {
            await api.delete(`/task/delete/${taskToDelete}`);
            setTaskToDelete(null);
            fetchTasks(ownedPage, collabPage);
        } catch (err) {
            console.error("Delete failed:", err);
            setIsLoading(false);
        }
    };

    const handleSaveEdit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            await api.put(`/task/update`, {
                id:       editingTask.id,
                name:     editForm.name,
                priority: editForm.priority,
                state:    editForm.state,
            });
            setEditingTask(null);
            fetchTasks(ownedPage, collabPage);
        } catch (err) {
            console.error("Edit failed:", err);
            alert(err.response?.data || "Update failed");
            setIsLoading(false);
        }
    };

    const handleAddCollab = async () => {
        if (!selectedCollabId) return;

        const userIdNum = Number(selectedCollabId);
        const userToAdd = possibleCollabs.find(u => u.id === userIdNum);
        if (!userToAdd) return;

        setCurrentCollabs(prev => [...prev, userToAdd]);
        setPossibleCollabs(prev => prev.filter(u => u.id !== userIdNum));
        setSelectedCollabId('');

        try {
            await api.post(`/user/addCollab`, { userId: userIdNum, taskId: editingTask.id });

            fetchCurrentCollabs(editingTask.id, currentCollabsPage);
        } catch (err) {
            console.error("Add collaborator failed:", err);
            alert(err.response?.data || "Failed to add collaborator");
            fetchCurrentCollabs(editingTask.id, currentCollabsPage);
            fetchPossibleCollabs(editingTask.id);
        }
    };

    const handleRemoveCollab = async (collabUserId) => {
        const userToRemove = currentCollabs.find(u => u.id === collabUserId);
        if (!userToRemove) return;

        setCurrentCollabs(prev => prev.filter(u => u.id !== collabUserId));
        setPossibleCollabs(prev => [...prev, userToRemove]);

        try {
            await api.post(`/user/rmvCollab`, { userId: collabUserId, taskId: editingTask.id });

            fetchCurrentCollabs(editingTask.id, currentCollabsPage);
        } catch (err) {
            console.error("Remove collaborator failed:", err);
            alert(err.response?.data || "Failed to remove collaborator");
            fetchCurrentCollabs(editingTask.id, currentCollabsPage);
            fetchPossibleCollabs(editingTask.id);
        }
    };

    const tableStyle     = { width: '100%', borderCollapse: 'collapse', textAlign: 'left', backgroundColor: 'white', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' };
    const thStyle        = { padding: '12px', backgroundColor: '#f4f4f4' };
    const tdStyle        = { padding: '12px', borderBottom: '1px solid #eee' };
    const paginationStyle = { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '10px' };

    return (
        <div style={{ maxWidth: '900px', margin: '0 auto', opacity: isLoading ? 0.7 : 1, transition: 'opacity 0.2s' }}>
            <h2>Task Dashboard</h2>

            <div style={{ marginTop: '40px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '2px solid #2196F3', paddingBottom: '10px' }}>
                    <h3 style={{ margin: 0 }}>Your Tasks</h3>
                    <B color="blue" onClick={() => setIsCreatingTask(true)}>+ Create Task</B>
                </div>
                <table style={tableStyle}>
                    <thead>
                    <tr>
                        <th style={thStyle}>ID</th>
                        <th style={thStyle}>Name</th>
                        <th style={thStyle}>Priority</th>
                        <th style={thStyle}>State</th>
                        <th style={thStyle}>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {ownedTasks.length === 0 ? (
                        <tr><td colSpan="5" style={{ padding: '20px', textAlign: 'center', color: '#888' }}>You don't own any tasks yet.</td></tr>
                    ) : ownedTasks.map(task => (
                        <tr key={task.id}>
                            <td style={tdStyle}>{task.id}</td>
                            <td style={{ ...tdStyle, fontWeight: 'bold' }}>{task.name}</td>
                            <td style={tdStyle}>{task.priority}</td>
                            <td style={tdStyle}>{task.state}</td>
                            <td style={{ ...tdStyle, display: 'flex', gap: '8px' }}>
                                <B color="green" onClick={() => {
                                    setCurrentCollabs([]);
                                    setEditingTask(task)}}>Edit</B>
                                <B color="red"   onClick={() => setTaskToDelete(task.id)}>Delete</B>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
                <div style={paginationStyle}>
                    <B disabled={ownedPage === 0}                   onClick={() => setOwnedPage(p => p - 1)}>Previous</B>
                    <span>Page {ownedPage + 1} of {Math.max(ownedTotalPages, 1)}</span>
                    <B disabled={ownedPage >= ownedTotalPages - 1}  onClick={() => setOwnedPage(p => p + 1)}>Next</B>
                </div>
            </div>

            <div style={{ marginTop: '50px' }}>
                <h3 style={{ borderBottom: '2px solid #FF9800', paddingBottom: '10px' }}>You are a collaborator in</h3>
                <table style={tableStyle}>
                    <thead>
                    <tr>
                        <th style={thStyle}>Name</th>
                        <th style={thStyle}>Priority</th>
                        <th style={thStyle}>State</th>
                        <th style={thStyle}>Owner</th>
                    </tr>
                    </thead>
                    <tbody>
                    {collabTasks.length === 0 ? (
                        <tr><td colSpan="5" style={{ padding: '20px', textAlign: 'center', color: '#888' }}>You have no collaborative tasks.</td></tr>
                    ) : collabTasks.map(task => (
                        <tr key={task.id}>
                            <td style={{ ...tdStyle, fontWeight: 'bold' }}>{task.name}</td>
                            <td style={tdStyle}>{task.priority}</td>
                            <td style={tdStyle}>{task.state}</td>
                            <td style={tdStyle}>{task.ownerFirstName} {task.ownerLastName}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
                <div style={paginationStyle}>
                    <B disabled={collabPage === 0}                    onClick={() => setCollabPage(p => p - 1)}>Previous</B>
                    <span>Page {collabPage + 1} of {Math.max(collabTotalPages, 1)}</span>
                    <B disabled={collabPage >= collabTotalPages - 1}  onClick={() => setCollabPage(p => p + 1)}>Next</B>
                </div>
            </div>

            {isCreatingTask && (
                <Modal>
                    <h3 style={{ marginTop: 0 }}>Create New Task</h3>
                    <form onSubmit={handleCreateTask} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>

                        <label style={{ fontWeight: 'bold' }}>Name</label>
                        <input
                            type="text"
                            value={createForm.name}
                            onChange={e => setCreateForm(f => ({ ...f, name: e.target.value }))}
                            required
                            placeholder="e.g. Finish the report"
                            style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
                        />

                        <label style={{ fontWeight: 'bold' }}>Priority</label>
                        <select
                            value={createForm.priority}
                            onChange={e => setCreateForm(f => ({ ...f, priority: e.target.value }))}
                            style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
                        >
                            {PRIORITY_OPTIONS.map(p => <option key={p} value={p}>{p}</option>)}
                        </select>

                        <div style={{ display: 'flex', gap: '10px', marginTop: '8px' }}>
                            <button
                                type="submit"
                                style={{ flex: 1, padding: '10px', ...btn.base, ...btn.blue }}
                            >
                                Create Task
                            </button>
                            <B type="button" style={{ flex: 1, padding: '10px' }} onClick={() => setIsCreatingTask(false)}>
                                Cancel
                            </B>
                        </div>
                    </form>
                </Modal>
            )}
            {taskToDelete && (
                <Modal>
                    <h3>Are you sure?</h3>
                    <p>This action cannot be undone.</p>
                    <div style={{ display: 'flex', gap: '10px', marginTop: '20px' }}>
                        <B color="red"  style={{ flex: 1, padding: '10px' }} onClick={handleDelete}>Yes, Delete</B>
                        <B             style={{ flex: 1, padding: '10px' }} onClick={() => setTaskToDelete(null)}>Cancel</B>
                    </div>
                </Modal>
            )}

            {editingTask && (
                <Modal wide>
                    <h3 style={{ marginTop: 0 }}>Edit Task (ID: {editingTask.id})</h3>
                    <form onSubmit={handleSaveEdit} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>

                        <label style={{ fontWeight: 'bold' }}>Name</label>
                        <input
                            type="text"
                            value={editForm.name}
                            onChange={e => setEditForm(f => ({ ...f, name: e.target.value }))}
                            required
                            style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
                        />

                        <label style={{ fontWeight: 'bold' }}>Priority</label>
                        <select
                            value={editForm.priority}
                            onChange={e => setEditForm(f => ({ ...f, priority: e.target.value }))}
                            style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
                        >
                            {PRIORITY_OPTIONS.map(p => <option key={p} value={p}>{p}</option>)}
                        </select>

                        <label style={{ fontWeight: 'bold' }}>State</label>
                        <select
                            value={editForm.state}
                            onChange={e => setEditForm(f => ({ ...f, state: e.target.value }))}
                            style={{ padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
                        >
                            {STATE_OPTIONS.map(s => <option key={s} value={s}>{s}</option>)}
                        </select>

                        <label style={{ fontWeight: 'bold' }}>Current Collaborators</label>
                        {currentCollabs.length === 0 ? (
                            <p style={{ margin: 0, color: '#888', fontSize: '14px' }}>No collaborators yet.</p>
                        ) : (
                            <>
                                <table style={{ ...tableStyle, fontSize: '14px' }}>
                                    <thead>
                                    <tr>
                                        <th style={{ ...thStyle, padding: '8px' }}>Name</th>
                                        <th style={{ ...thStyle, padding: '8px' }}>Email</th>
                                        <th style={{ ...thStyle, padding: '8px' }}></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {currentCollabs.map(user => (
                                        <tr key={user.id}>
                                            <td style={{ ...tdStyle, padding: '8px' }}>{user.firstName} {user.lastName}</td>
                                            <td style={{ ...tdStyle, padding: '8px' }}>
                                                <B color="red" type="button" small onClick={() => handleRemoveCollab(user.id)}>Remove</B>
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                                <div style={paginationStyle}>
                                    <B small disabled={currentCollabsPage === 0}                          onClick={() => setCurrentCollabsPage(p => p - 1)}>Previous</B>
                                    <span style={{ fontSize: '13px' }}>Page {currentCollabsPage + 1} of {Math.max(currentCollabsTotalPages, 1)}</span>
                                    <B small disabled={currentCollabsPage >= currentCollabsTotalPages - 1} onClick={() => setCurrentCollabsPage(p => p + 1)}>Next</B>
                                </div>
                            </>
                        )}

                        <label style={{ fontWeight: 'bold' }}>Add Collaborator</label>
                        {possibleCollabs.length === 0 ? (
                            <p style={{ margin: 0, color: '#888', fontSize: '14px' }}>No users available to add.</p>
                        ) : (
                            <div style={{ display: 'flex', gap: '8px' }}>
                                <select
                                    value={selectedCollabId}
                                    onChange={e => setSelectedCollabId(e.target.value)}
                                    style={{ flex: 1, padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
                                >
                                    <option value="">— select a user —</option>
                                    {possibleCollabs.map(user => (
                                        <option key={user.id} value={user.id}>
                                            {user.firstName} {user.lastName}
                                        </option>
                                    ))}
                                </select>
                                <B color="blue" type="button" onClick={handleAddCollab} disabled={!selectedCollabId}>Add</B>
                            </div>
                        )}

                        <div style={{ display: 'flex', gap: '10px', marginTop: '8px' }}>
                            <button
                                type="submit"
                                style={{ flex: 1, padding: '10px', ...btn.base, ...btn.blue }}
                            >
                                Save Changes
                            </button>
                            <B type="button" style={{ flex: 1, padding: '10px' }} onClick={() => setEditingTask(null)}>
                                Cancel
                            </B>
                        </div>
                    </form>
                </Modal>
            )}
        </div>
    );
}

function Modal({ children, wide }) {
    return (
        <div style={{
            position: 'fixed', inset: 0,
            backgroundColor: 'rgba(0,0,0,0.5)',
            display: 'flex', justifyContent: 'center', alignItems: 'center',
            zIndex: 1000,
        }}>
            <div style={{
                backgroundColor: 'white',
                padding: '30px',
                borderRadius: '8px',
                width: wide ? '500px' : '320px',
                maxHeight: '90vh',
                overflowY: 'auto',
                boxShadow: '0 8px 32px rgba(0,0,0,0.2)',
            }}>
                {children}
            </div>
        </div>
    );
}