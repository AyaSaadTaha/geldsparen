import React from 'react'
import SavingsDiagram from "../components/SavingsDiagram.jsx";


function Dashboards({accounts}){
    return (
        <div>
            <p>Your dashboards overview.</p>
            <div style={{ marginTop: 16 }}>
                <SavingsDiagram accounts={accounts} />
            </div>
           {/* <p>Savings dashboard.</p>*/}
        </div>
    )
}

export default Dashboards;

