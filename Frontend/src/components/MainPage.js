import React, { useEffect, useState } from "react";
import axios from "axios";
import "./MainPage.css";

const MainPage = () => {
    const [orders, setOrders] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [isEditing, setIsEditing] = useState(null);
    const [updatedOrder, setUpdatedOrder] = useState({});
    const [newOrder, setNewOrder] = useState(null);
    const [totalOrders, setTotalOrders] = useState(0);
    const itemsPerPage = 8;
    const today = new Date().toISOString().split("T")[0];

    useEffect(() => {
        const fetchOrders = async () => {
            try {
                const response = await axios.get(
                    `http://localhost:8080/api/orders?page=${currentPage - 1}&size=${itemsPerPage}`
                );
                setOrders(response.data.orders);
                setTotalOrders(response.data.totalOrders);
            } catch (error) {
                console.error("Error fetching data", error);
            }
        };
        fetchOrders();
    }, [currentPage, itemsPerPage]);

    const totalPages = Math.ceil(totalOrders / itemsPerPage);

    const handleNextPage = () => {
        if (currentPage < totalPages) {
            setCurrentPage(currentPage + 1);
        }
    };

    const handlePreviousPage = () => {
        if (currentPage > 1) {
            setCurrentPage(currentPage - 1);
        }
    };

    const handleAddOrder = () => {
        setCurrentPage(totalPages);
        setNewOrder({
            registrationDate: new Date().toISOString().split("T")[0],
            documentId: "",
            issuer: "",
            documentType: "Comanda",
            department: "Transport",
            recipient: "",
            extraNotes: "",
            documentDate: today,
        });
    };

    const handleSaveNewOrder = async () => {
        if (!newOrder.issuer || !newOrder.recipient) {
            alert("Te rugam asigura-te ca urmatoarele campuri sunt completate: Emitent, Destinatar!");
            return;
        }

        if (newOrder.issuer !== "Bonelvio" && (!newOrder.documentId || !newOrder.documentDate)) {
            alert("Te rugam completeaza numarul si data documentului!");
            return;
        }

        let documentDate = newOrder.documentDate;

        if (documentDate) {
            try {
                const parsedDate = new Date(documentDate);
                if (!isNaN(parsedDate.getTime())) {
                    const bucharestDate = new Date(parsedDate.toLocaleString('en-GB', { timeZone: 'Europe/Bucharest' }));
                    documentDate = bucharestDate.toISOString().split("T")[0];
                } else {
                    throw new Error('Invalid Date');
                }
            } catch (error) {
                console.error('Invalid date format', error);
                documentDate = new Date().toISOString().split("T")[0];
            }
        } else {
            documentDate = new Date().toISOString().split("T")[0];
        }

        const formattedOrder = {
            ...newOrder,
            registrationDate: new Date(newOrder.registrationDate).toISOString().split("T")[0],
            documentDate: documentDate,
        };

        try {
            const response = await axios.post("http://localhost:8080/api/orders", formattedOrder);
            setOrders((prevOrders) => [...prevOrders, response.data].sort((a, b) => a.orderId - b.orderId));
            setNewOrder(null);
        } catch (error) {
            console.error("Error adding order", error);
            alert("Comanda nu a putut fi inregistrata!");
        }
    };

    const handleEdit = (orderId) => {
        setIsEditing(orderId);
        const orderToEdit = orders.find((order) => order.orderId === orderId);
        setUpdatedOrder(orderToEdit);
    };

    const handleSave = async (orderId) => {
        if (!updatedOrder.issuer || !updatedOrder.recipient) {
            alert("Te rugam asigura-te ca urmatoarele campuri sunt completate: Emitent, Destinatar!");
            return;
        }

        if (updatedOrder.issuer === "Bonelvio") {
            const originalOrder = orders.find((order) => order.orderId === orderId);
            updatedOrder.documentId = originalOrder.documentId;
            updatedOrder.documentDate = originalOrder.documentDate;
        }

        try {
            const response = await axios.put(
                `http://localhost:8080/api/orders/${orderId}`,
                updatedOrder
            );
            const updatedOrders = orders.map((order) =>
                order.orderId === orderId ? response.data : order
            );
            setOrders(updatedOrders);
            setIsEditing(null);
        } catch (error) {
            console.error("Error updating order", error);
            alert("Comanda nu a putut fi actualizata!");
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setUpdatedOrder((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleNewOrderChange = (e) => {
        const { name, value } = e.target;

        if ((name === "issuer" || name === "recipient") && !value.trim()) {
            alert("Te rugam asigura-te ca urmatoarele campuri sunt completate: Emitent, Destinata!");
            return;
        }

        setNewOrder((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleCancelNewOrder = () => {
        setNewOrder(null);
    };

    const handleDownloadDocument = async (orderId) => {
        try {
            const response = await axios.get(
                `http://localhost:8080/api/orders/${orderId}/download`,
                { responseType: 'blob' }
            );

            const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = `comanda_${orderId}.docx`;
            link.click();
        } catch (error) {
            console.error("Error downloading document", error);
            alert("Nu s-a putut descarca documentul!");
        }
    };

    return (
        <div className="main-page">
            {newOrder ? (
                <button className="cancel-order-btn" onClick={handleCancelNewOrder}>
                    Anulare
                </button>
            ) : (
                <button className="add-order-btn" onClick={handleAddOrder}>
                    + Adaugare
                </button>
            )}

            <table className="orders-table">
                <thead>
                    <tr>
                        <th>Numar de Inregistrare</th>
                        <th colSpan="2">Data Inregistrarii</th>
                        <th colSpan="2">Numarul si Data Documentului</th>
                        <th>Emitent</th>
                        <th>Continutul Documentului</th>
                        <th>Compartimentul si Semnatura de primire</th>
                        <th>Destinatar</th>
                        <th>Numar de inregistrare la care se conexeaza si indicativul dosarului</th>
                        <th>Actiuni</th>
                    </tr>
                    <tr>
                        <th></th>
                        <th>Luna</th>
                        <th>Ziua</th>
                        <th>Numar Document</th>
                        <th>Data Document</th>
                        <th></th>
                        <th></th>
                        <th></th>
                        <th></th>
                        <th></th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    {orders.map((order) => (
                        <tr key={order.orderId}>
                            <td>{order.orderId}</td>
                            <td>
                                {new Date(order.registrationDate).toLocaleString("ro-RO", {
                                    month: "long",
                                }).replace(/^./, (match) => match.toUpperCase())}
                            </td>
                            <td>{new Date(order.registrationDate).getDate()}</td>
                            <td>
                                {isEditing === order.orderId ? (
                                    <input
                                        type="text"
                                        name="documentId"
                                        value={updatedOrder.documentId}
                                        onChange={handleChange}
                                        disabled={updatedOrder.issuer === "Bonelvio"}
                                    />
                                ) : (
                                    order.documentId
                                )}
                            </td>
                            <td>
                                {isEditing === order.orderId ? (
                                    <input
                                        type="date"
                                        name="documentDate"
                                        value={updatedOrder.documentDate}
                                        onChange={handleChange}
                                        disabled={updatedOrder.issuer === "Bonelvio"}
                                    />
                                ) : (
                                    new Date(order.documentDate).toLocaleDateString("en-GB")
                                )}
                            </td>
                            <td>
                                {isEditing === order.orderId ? (
                                    <input
                                        type="text"
                                        name="issuer"
                                        value={updatedOrder.issuer}
                                        onChange={handleChange}
                                    />
                                ) : (
                                    order.issuer
                                )}
                            </td>
                            <td>
                                {isEditing === order.orderId ? (
                                    <input
                                        type="text"
                                        name="documentType"
                                        value={updatedOrder.documentType}
                                        onChange={handleChange}
                                        disabled
                                    />
                                ) : (
                                    order.documentType
                                )}
                            </td>
                            <td>
                                {isEditing === order.orderId ? (
                                    <input
                                        type="text"
                                        name="department"
                                        value={updatedOrder.department}
                                        onChange={handleChange}
                                        disabled
                                    />
                                ) : (
                                    order.department
                                )}
                            </td>
                            <td>
                                {isEditing === order.orderId ? (
                                    <input
                                        type="text"
                                        name="recipient"
                                        value={updatedOrder.recipient}
                                        onChange={handleChange}
                                    />
                                ) : (
                                    order.recipient
                                )}
                            </td>
                            <td>
                                {isEditing === order.orderId ? (
                                    <input
                                        type="text"
                                        name="extraNotes"
                                        value={updatedOrder.extraNotes}
                                        onChange={handleChange}
                                    />
                                ) : (
                                    order.extraNotes
                                )}
                            </td>
                            <td>
                                {isEditing === order.orderId ? (
                                    <button className="cancel-order-btn" onClick={() => handleSave(order.orderId)}>
                                        Salvare
                                    </button>
                                ) : (
                                    <>
                                        <div className="button-container">
                                            <button className="edit-btn" onClick={() => handleEdit(order.orderId)}>
                                                Editare
                                            </button>
                                            {order.issuer === "Bonelvio" && (
                                                <button
                                                    className="cancel-order-btn"
                                                    onClick={() => handleDownloadDocument(order.orderId)}
                                                >
                                                    Descarcare
                                                </button>
                                            )}
                                        </div>
                                    </>
                                )}
                            </td>
                        </tr>
                    ))}
                    {newOrder && (
                        <tr>
                            <td>Comanda Noua</td>
                            <td>{new Date().toLocaleString("ro-RO", { month: "long", }).replace(/^./, (match) => match.toUpperCase())}</td>
                            <td>{new Date().getDate()}</td>
                            <td>
                                <input
                                    type="text"
                                    name="documentId"
                                    value={newOrder.documentId}
                                    onChange={handleNewOrderChange}
                                />
                            </td>
                            <td>
                                <input
                                    type="date"
                                    name="documentDate"
                                    value={newOrder.documentDate || ""}
                                    onChange={handleNewOrderChange}
                                />
                            </td>
                            <td>
                                <input
                                    type="text"
                                    name="issuer"
                                    value={newOrder.issuer}
                                    onChange={handleNewOrderChange}
                                />
                            </td>
                            <td>
                                <input
                                    type="text"
                                    name="documentType"
                                    value={newOrder.documentType}
                                    onChange={handleNewOrderChange}
                                />
                            </td>
                            <td>
                                <input
                                    type="text"
                                    name="department"
                                    value={newOrder.department}
                                    onChange={handleNewOrderChange}
                                />
                            </td>
                            <td>
                                <input
                                    type="text"
                                    name="recipient"
                                    value={newOrder.recipient}
                                    onChange={handleNewOrderChange}
                                />
                            </td>
                            <td>
                                <input
                                    type="text"
                                    name="extraNotes"
                                    value={newOrder.extraNotes}
                                    onChange={handleNewOrderChange}
                                />
                            </td>
                            <td>
                                <button className="cancel-order-btn" onClick={handleSaveNewOrder}>
                                    Creare
                                </button>
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>

            <div className="pagination">
                <button
                    className="add-order-btn"
                    onClick={() => setCurrentPage(1)}
                    disabled={currentPage === 1}
                >
                    Prima Pagina
                </button>
                <button
                    className="add-order-btn"
                    onClick={handlePreviousPage}
                    disabled={currentPage === 1}
                >
                    Pagina Anterioara
                </button>
                <span className="pagination-text">
                    <b>Pagina {currentPage} din {totalPages}</b>
                </span>
                <button
                    className="add-order-btn"
                    onClick={handleNextPage}
                    disabled={currentPage === totalPages}
                >
                    Pagina Urmatoare
                </button>
                <button
                    className="add-order-btn"
                    onClick={() => setCurrentPage(totalPages)}
                    disabled={currentPage === totalPages}
                >
                    Ultima Pagina
                </button>
            </div>
        </div>
    );
};

export default MainPage;
