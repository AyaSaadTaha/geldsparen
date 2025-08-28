import React, { useState } from 'react';

const CurrentAccountForm = () => {
    const [salary, setSalary] = useState('');
    const [payday, setPayday] = useState('');
    const [iban, setIban] = useState('');
    const [message, setMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('Adding account...');
        const token =localStorage.getItem('token');
        alert(token)
        try {
            const response = await fetch('http://localhost:8080/api/current-accounts', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    // IMPORTANT: Include the JWT token from the user's login response
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ salary, payday, iban }),
            });

            if (response.ok) {
                const data = await response.json();
                setMessage('Account added successfully!');
                console.log(data);
            } else if (response.status === 409) {
                setMessage('Error: Account already exists for this user.');
            } else {
                setMessage('Error adding account.');
            }
        } catch (error) {
            console.error('Network error:', error);
            setMessage('Network error, please try again.');
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2>Add Current Account</h2>
            <div>
                <label>Salary:</label>
                <input type="number" value={salary} onChange={(e) => setSalary(e.target.value)} required />
            </div>
            <div>
                <label>Payday (1-31):</label>
                <input type="number" value={payday} onChange={(e) => setPayday(e.target.value)} required min="1" max="31" />
            </div>
            <div>
                <label>IBAN:</label>
                <input type="text" value={iban} onChange={(e) => setIban(e.target.value)} required />
            </div>
            <button type="submit">Submit</button>
            {message && <p>{message}</p>}
        </form>
    );
};

export default CurrentAccountForm;