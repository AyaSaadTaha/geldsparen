import React from 'react'
import './card.css'

const Card = () => {
    return (
        <div className="balance-card">
            <div className="currency-symbol"></div>

            <div className="balance-label">Current Balance</div>

            <div className="balance-amount">2000</div>

            <div className="bank-info">
                <div className="bank-icon">📈</div>
                <div className="bank-details">
                    <span className="bank-name">Sparkasse Bank</span>
                    <span className="account-number">**** **** 7876</span>
                </div>
            </div>
        </div>
    );
};

export default Card;