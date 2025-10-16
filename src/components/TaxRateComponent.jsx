import { useEffect, useState } from 'react';
import { getTaxRate, updateTaxRate } from '../services/TaxRateService';

/** Creates the page for viewing and updating the tax rate. */
const TaxRateComponent = () => {
    const [taxRate, setTaxRate] = useState(0);
    const [newTaxRate, setNewTaxRate] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() => {
        fetchTaxRate();
    }, []);

    const fetchTaxRate = () => {
        getTaxRate()
            .then((response) => {
                setTaxRate(response.data.percent);
            })
            .catch((error) => {
                console.error(error);
                setErrorMessage("Failed to fetch tax rate");
            });
    };

    const handleSubmit = () => {
        if (newTaxRate === "") {
            setErrorMessage("Please enter a new tax rate before updating.");
            return;
        }

        const updatedTaxRate = { percent: parseFloat(newTaxRate) };

        updateTaxRate(updatedTaxRate)
            .then((response) => {
                setTaxRate(response.data.percent);
                setSuccessMessage("Tax rate updated successfully!");
                setErrorMessage("");
                setNewTaxRate("");
            })
            .catch((error) => {
                console.error(error);
                setErrorMessage("Failed to update tax rate.");
            });
    };

    return (
        <div className="container">
            <br />
            <h2 className="text-center">Tax Rate</h2>
            <br />
            <div className="text-center">
                <h4>Current Tax Rate: {taxRate}%</h4>
                <input
                    type="number"
                    step="0.1"
                    className="form-control mt-3"
                    placeholder="Enter new tax rate"
                    value={newTaxRate}
                    onChange={(e) => setNewTaxRate(e.target.value)}
                />
                <button className="btn btn-primary mt-3" onClick={handleSubmit}>
                    Update
                </button>
            </div>
            {successMessage && <div className="alert alert-success mt-3">{successMessage}</div>}
            {errorMessage && <div className="alert alert-danger mt-3">{errorMessage}</div>}
        </div>
    );
};

export default TaxRateComponent;
