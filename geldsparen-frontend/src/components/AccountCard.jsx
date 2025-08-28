import React from 'react'
import './card.css'

const AccountCard = ({currentAccount}) => {

    // console log: props
    console.log("AccountCard got currentAccount:", currentAccount);

    const salary = Number(currentAccount?.salary ?? 0);
    const payday = currentAccount?.payday ?? "—";
    const iban = currentAccount?.iban ?? "";
    const maskedIban = iban ? "**** **** " + iban.slice(-4) : "—";


    return (
        <div className="balance-card">
            <div className="currency-symbol">€</div>

            <div className="balance-label">Current Balance</div>

            <div className="balance-amount">{salary.toLocaleString("de-DE")} €</div>

            <div className="bank-info">
                <div className="bank-icon">📈</div>
                <div className="bank-details">
                    <span className="bank-name">Sparkasse Bank</span>
                    <span className="account-number">{maskedIban}</span>
                    <span className="account-payday">Payday: {payday}</span>
                </div>
            </div>
        </div>
    );
};

export default AccountCard;