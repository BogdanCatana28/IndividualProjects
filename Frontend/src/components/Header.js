import React from "react";
import "./Header.css";
import logo from "./logo.png";

const Header = () => {
    return (
        <header className="header">
            <div className="logo-container">
                <img src={logo} alt="Bonelvio Logo" className="logo" />
            </div>
            <h1 className="title">BONELVIO TRANS REGISTRU INTRARI SI IESIRI</h1>
        </header>
    );
};

export default Header;