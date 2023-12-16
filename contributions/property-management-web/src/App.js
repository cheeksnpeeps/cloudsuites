import React, { useEffect, useState } from 'react';
import logo from './logo.svg';
import './App.css';

const App = () => {

  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);

    fetch('api/v1/companies')
        .then(response => response.json())
        .then(data => {
            setCompanies(data);
            setLoading(false);
        })
  }, []);

  if (loading) {
    return <p>Loading...</p>;
  }

  return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <div className="App-intro">
            <h2>Test - Property Management List</h2>
            {companies.map(company =>
                <div key={company.id}>
                    {company.id}
                    {company.name}
                </div>
            )}
          </div>
        </header>
      </div>
  );
}

export default App;