import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

// get items/recipes, and get the placing an actual order service
import { isCustomerUser } from "../services/AuthService";
import { getAllItems } from "../services/ItemService";
import { placeOrder } from "../services/OrderService";
import { listRecipes } from "../services/RecipesService";
import { getTaxRate } from "../services/TaxRateService";

/**
 * lists the available items and recipes,
 * user selscts the items and gets the quants,
 * and applies a tip if needed
 * @returns the process for placing an order
 */
const PlaceOrderComponent = () => {
  // items + recipes available
  const [items, setItems] = useState([]);
  const [recipes, setRecipes] = useState([]);

  // what they picked
  const [selectedItems, setSelectedItems] = useState({});
  const [selectedRecipes, setSelectedRecipes] = useState({});

  // tip and how much they paid
  const [tip, setTip] = useState("");
  const [tipType, setTipType] = useState(-2); // gets tip type (must select 1)

  // tax rate
  const [taxRate, setTaxRate] = useState(0.0);

  // need to keep a running subtotal for how much their current tip is cause
  // we are doing it as percentages, etc
  const [subtotal, setSubtotal] = useState(0);
  const [amountPaid, setAmountPaid] = useState(""); // user input for payment

  const [errors, setErrors] = useState({}); // error if payment not enough or no items selected
  const [successMessage, setSuccessMessage] = useState("");

  const [page, setPage] = useState(1); // order pages

  const navigator = useNavigate();

  // load in the items and recipes once component is ready -> make suree we get the method first
  useEffect(() => {
    getAllItems().then((res) => setItems(res.data)); // fetch all item options
    listRecipes().then((res) => setRecipes(res.data)); // fetch all recipe options
    getTaxRate().then((res) => setTaxRate(res.data.percent)); // fetch taxRate
  }, []);

  // recalc subtotal if they change selections or if prices update
  useEffect(() => {
    let total = 0;

    // item prices * quantity
    items.forEach((item) => {
      const qty = parseInt(selectedItems[item.id]) || 0;
      if (qty > 0) total += item.price * qty;
    });

    // recipe prices * quantity
    recipes.forEach((recipe) => {
      const qty = parseInt(selectedRecipes[recipe.id]) || 0;
      if (qty > 0) total += recipe.price * qty;
    });

    setSubtotal(total); // set subtotal to re-render display
  }, [selectedItems, selectedRecipes, items, recipes]);

  // input handling for item quantities
  const handleItemChange = (id, quantity) => {
    setSelectedItems({ ...selectedItems, [id]: quantity });
  };

  // input handling for recipe quantities
  const handleRecipeChange = (id, quantity) => {
    setSelectedRecipes({ ...selectedRecipes, [id]: quantity });
  };

  // live calc of total and change (frontend only)
  const total =
    (subtotal * taxRate) / 100 +
    subtotal +
    (parseFloat(tip || 0) >= 0 ? parseFloat(tip || 0) : 0);
  const change = parseFloat(amountPaid || 0) - total;

  // submitting whatever they added to their "cart"
  const handleSubmitCart = (e) => {
    // don't reload the page or navigate anywhere else
    e.preventDefault();

    setErrors({}); // clear any old errors

    // get the orders as a map
    const orderItemRequestList = Object.entries(selectedItems)
      .filter(([_, qty]) => qty > 0)
      .map(([id, qty]) => ({
        itemId: parseInt(id),
        quantity: parseInt(qty),
      }));

    const orderRecipeRequestList = Object.entries(selectedRecipes)
      .filter(([_, qty]) => qty > 0)
      .map(([id, qty]) => ({
        recipeId: parseInt(id),
        quantity: parseInt(qty),
      }));

    // error check -> must select at least one item or drink to place an order
    // can't just order nth bruh
    if (
      orderItemRequestList.length === 0 &&
      orderRecipeRequestList.length === 0
    ) {
      setErrors({ page1: "Please select at least one item or drink." });
      return;
    }

    // Go to next page if successful.
    setPage(2);
  };

  // placing final order with tip and amount paid
  const handlePlaceOrder = (e) => {
    // don't reload the page or navigate anywhere else
    e.preventDefault();

    setErrors({}); // clear any old errors

    // get the orders as a map
    const orderItemRequestList = Object.entries(selectedItems)
      .filter(([_, qty]) => qty > 0)
      .map(([id, qty]) => ({
        itemId: parseInt(id),
        quantity: parseInt(qty),
      }));

    const orderRecipeRequestList = Object.entries(selectedRecipes)
      .filter(([_, qty]) => qty > 0)
      .map(([id, qty]) => ({
        recipeId: parseInt(id),
        quantity: parseInt(qty),
      }));

    // error check -> must select at least one item or drink to place an order
    // can't just order nth bruh
    if (
      orderItemRequestList.length === 0 &&
      orderRecipeRequestList.length === 0
    ) {
      setErrors({ page2: "Please select at least one item or drink." });
      return;
    }

    // payment validation -> gotta pay up bruh
    if (parseFloat(amountPaid || 0) < total) {
      setErrors({
        amountPaid: `Amount paid ($${parseFloat(amountPaid || 0).toFixed(
          2
        )}) is not enough.`,
      });
      return;
    }

    // tip option must be selected
    if (tipType === -2) {
      setErrors({ tip: "Please Select a Tip Option" });
      return;
    }

    // tip is a floating point var so gotta parse it (same for amt paid)
    const orderRequest = {
      orderItemRequestList,
      orderRecipeRequestList,
      tip: parseFloat(tip),
      amountPaid: parseFloat(amountPaid),
    };

    console.log("order request", orderRequest)
    // try/catch for submitting order check
    placeOrder(orderRequest)
      .then((res) => {
        setSuccessMessage(
          `Order placed successfully! Your change is ${res.data.toFixed(2)} `
        );
        if (isCustomerUser()) {
          setTimeout(() => navigator("/orders"), 1500);
        } else {
          setTimeout(() => navigator(0), 1500);
        }
      })
      .catch((err) => {
        console.error("order failed", err);

        if (errors.response?.status === 402) {
          setErrors({
            amountPaid: `Amount paid ($${parseFloat(amountPaid || 0).toFixed(
              2
            )}) is not enough.`,
          });
        } else {
          setErrors({ page2: "Order could not be placed. Please try again." });
        }
      });
  };

  return (
    <div className="container">
      {page === 1 && (
        <div>
          <form onSubmit={handleSubmitCart}>
            <br />
            <br />
            <h2 className="text-center">Place Order</h2>
            <br />
            <br />

            {/* item section card */}
            <div className="card mb-4 col-6 mx-auto shadow-sm">
              <div className="card-header">
                <h4 className="mb-0">Items</h4>
              </div>
              <div className="card-body p-3">
                {items.length > 0 && (
                  <table className="table table-bordered mb-0">
                    <thead>
                      <tr>
                        <th className="text-center">Select</th>
                        <th>Item Name</th>
                        <th style={{ width: "100px" }}>Quantity</th>
                      </tr>
                    </thead>
                    <tbody>
                      {items.map((item) => {
                        const isSelected = selectedItems[item.id] > 0;
                        return (
                          <tr key={item.id}>
                            <td className="text-center">
                              <input
                                type="checkbox"
                                className="form-check-input position-static"
                                checked={isSelected}
                                onChange={(e) =>
                                  setSelectedItems({
                                    ...selectedItems,
                                    [item.id]: e.target.checked ? 1 : 0,
                                  })
                                }
                              />
                            </td>
                            <td>{item.name}</td>
                            <td>
                              <input
                                type="number"
                                min="1"
                                className="form-control form-control-sm text-center mx-auto w-75"
                                value={selectedItems[item.id] || ""}
                                onChange={(e) =>
                                  handleItemChange(item.id, e.target.value)
                                }
                                disabled={!isSelected}
                              />
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                )}
              </div>
            </div>

            {/* recipe section card */}
            <div className="card mb-4 col-6 mx-auto shadow-sm">
              <div className="card-header">
                <h4 className="mb-0">Beverage</h4>
              </div>
              <div className="card-body p-3">
                {recipes.length > 0 && (
                  <table className="table table-bordered mb-0">
                    <thead>
                      <tr>
                        <th className="text-center">Select</th>
                        <th>Beverage Name</th>
                        <th style={{ width: "100px" }}>Quantity</th>
                      </tr>
                    </thead>
                    <tbody>
                      {recipes.map((recipe) => {
                        const isSelected = selectedRecipes[recipe.id] > 0;
                        return (
                          <tr key={recipe.id}>
                            <td className="text-center">
                              <input
                                type="checkbox"
                                className="form-check-input position-static"
                                checked={isSelected}
                                onChange={(e) =>
                                  setSelectedRecipes({
                                    ...selectedRecipes,
                                    [recipe.id]: e.target.checked ? 1 : 0,
                                  })
                                }
                              />
                            </td>
                            <td>{recipe.name}</td>
                            <td>
                              <input
                                type="number"
                                min="1"
                                className="form-control form-control-sm text-center mx-auto w-75"
                                value={selectedRecipes[recipe.id] || ""}
                                onChange={(e) =>
                                  handleRecipeChange(recipe.id, e.target.value)
                                }
                                disabled={!isSelected}
                              />
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                )}
              </div>
            </div>

            {/* go to summary */}
            <button type="submit" className="btn btn-outline-primary">
              Checkout →
            </button>

            <br />
            <br />

            {/* error if they try to go to summary without selections */}
            {errors.page1 && (
              <div className="alert alert-danger" role="alert">
                {errors.page1}
              </div>
            )}
          </form>
        </div>
      )}

      {page === 2 && (
        <div>
          <form onSubmit={handlePlaceOrder}>
            <br />
            <br />
            <h2 className="text-center">Checkout</h2>
            <br />
            <br />

            <div className="container text-center">
              <div className="row">
                {/* First column with order information and tip */}
                <div className="col">
                  <div className="card">
                    {/* Table with order summary: name, quantity, unit, subtotal */}

                    {Object.entries(selectedItems).filter(([_, qty]) => qty > 0)
                      .length > 0 && (
                      <table className="table table-sm">
                        <thead>
                          <tr>
                            <th style={{ width: "150px" }}>Item</th>
                            <th>Unit Price</th>
                            <th>Quantity</th>
                            <th>Subtotal</th>
                          </tr>
                        </thead>
                        <tbody className="table-group-divider">
                          {items
                            .filter((i) => selectedItems[i.id] > 0)
                            .map((item) => (
                              <tr key={item.id}>
                                <td>{item.name}</td>
                                <td>
                                  $
                                  {(parseFloat(item.price || 0) >= 0
                                    ? parseFloat(item.price || 0)
                                    : 0
                                  ).toFixed(2)}
                                </td>
                                <td>x{selectedItems[item.id]}</td>
                                <td>
                                  $
                                  {(parseFloat(item.price || 0) >= 0
                                    ? parseFloat(
                                        item.price * selectedItems[item.id] || 0
                                      )
                                    : 0
                                  ).toFixed(2)}
                                </td>
                              </tr>
                            ))}
                        </tbody>
                      </table>
                    )}

                    {Object.entries(selectedRecipes).filter(
                      ([_, qty]) => qty > 0
                    ).length > 0 && (
                      <table className="table table-sm">
                        <thead>
                          <tr>
                            <th style={{ width: "150px" }}>Beverage</th>
                            <th>Unit Price</th>
                            <th>Quantity</th>
                            <th>Subtotal</th>
                          </tr>
                        </thead>
                        <tbody className="table-group-divider">
                          {recipes
                            .filter((i) => selectedRecipes[i.id] > 0)
                            .map((recipe) => (
                              <tr key={recipe.id}>
                                <td>{recipe.name}</td>
                                <td>
                                  $
                                  {(parseFloat(recipe.price || 0) >= 0
                                    ? parseFloat(recipe.price || 0)
                                    : 0
                                  ).toFixed(2)}
                                </td>
                                <td>x{selectedRecipes[recipe.id]}</td>
                                <td>
                                  $
                                  {(parseFloat(recipe.price || 0) >= 0
                                    ? parseFloat(
                                        recipe.price *
                                          selectedRecipes[recipe.id] || 0
                                      )
                                    : 0
                                  ).toFixed(2)}
                                </td>
                              </tr>
                            ))}
                        </tbody>
                      </table>
                    )}
                  </div>

                  <br />

                  {/* Tip container */}
                  <div className="container col-6">
                    <div
                      className={`row mb-3 ${errors.tip ? "is-invalid" : ""}`}
                    >
                      <h5>Tip</h5>
                    </div>
                    <div className="row mb-2 g-1">
                      {/* First three tip options */}
                      <div className="col-4 d-grid">
                        <input
                          type="radio"
                          className="btn-check"
                          name="options-base"
                          id="option1"
                          autoComplete="off"
                          checked={tipType === 0.15}
                          onChange={() => {
                            setTipType(0.15);
                            setTip(subtotal * 0.15);
                          }}
                        />
                        <label
                          className="btn btn-outline-secondary"
                          htmlFor="option1"
                        >
                          15%
                        </label>
                      </div>
                      <div className="col-4 d-grid">
                        <input
                          type="radio"
                          className="btn-check"
                          name="options-base"
                          id="option2"
                          autoComplete="off"
                          checked={tipType === 0.2}
                          onChange={() => {
                            setTipType(0.2);
                            setTip(subtotal * 0.2);
                          }}
                        />
                        <label
                          className="btn btn-outline-secondary"
                          htmlFor="option2"
                        >
                          20%
                        </label>
                      </div>
                      <div className="col-4 d-grid">
                        <input
                          type="radio"
                          className="btn-check"
                          name="options-base"
                          id="option3"
                          autoComplete="off"
                          checked={tipType === 0.25}
                          onChange={() => {
                            setTipType(0.25);
                            setTip(subtotal * 0.25);
                          }}
                        />
                        <label
                          className="btn btn-outline-secondary"
                          htmlFor="option3"
                        >
                          25%
                        </label>
                      </div>
                    </div>
                    <div className="row mb-3 g-1">
                      {/* Last two tip options */}
                      <div className="col-6 d-grid">
                        <input
                          type="radio"
                          className="btn-check"
                          name="options-base"
                          id="option4"
                          autoComplete="off"
                          checked={tipType === 0}
                          onChange={() => {
                            setTipType(0);
                            setTip(0);
                          }}
                        />
                        <label
                          className="btn btn-outline-secondary"
                          htmlFor="option4"
                        >
                          No Tip
                        </label>
                      </div>
                      <div className="col-6 d-grid">
                        <input
                          type="radio"
                          className="btn-check"
                          name="options-base"
                          id="option5"
                          autoComplete="off"
                          checked={tipType === -1}
                          onChange={() => {
                            setTipType(-1);
                            setTip("");
                          }}
                        />
                        <label
                          className="btn btn-outline-secondary"
                          htmlFor="option5"
                        >
                          Custom
                        </label>
                      </div>
                    </div>
                    {errors.tip && tipType === -2 && (
                      <div className="invalid-feedback">{errors.tip}</div>
                    )}
                    {/* Custom tip field */}
                    {tipType === -1 && (
                      <div className="row">
                        <div className="input-group mb-3">
                          <span className="input-group-text">$</span>
                          <div className="form-floating">
                            <input
                              type="number"
                              required
                              min="0"
                              step="0.01"
                              value={tip}
                              onChange={(e) => setTip(e.target.value)}
                              className="form-control"
                              id="floatingInputGroup1"
                              placeholder="Custom Tip"
                            />
                            <label htmlFor="floatingInputGroup1">
                              Custom Tip
                            </label>
                          </div>
                        </div>
                      </div>
                    )}
                  </div>
                </div>

                {/* Second column with order summary and amount paid */}
                <div className="col card col-md-4 offset-md-1">
                  <h4>Order Summary</h4>
                  <table className="table">
                    <thead>
                      <tr>
                        <th scope="col"></th>
                        <th scope="col"></th>
                      </tr>
                    </thead>
                    <tbody className="table-group-divider">
                      <tr>
                        <td className="text-start ps-4">Subtotal</td>
                        <td className="text-end pe-4">
                          ${subtotal.toFixed(2)}
                        </td>
                      </tr>
                      <tr>
                        <td className="text-start ps-4">Tax</td>
                        <td className="text-end pe-4">
                          $
                          {(subtotal ? (taxRate / 100) * subtotal : 0).toFixed(
                            2
                          )}
                        </td>
                      </tr>
                      <tr>
                        <td className="text-start ps-4">Tip</td>
                        <td className="text-end pe-4">
                          $
                          {(parseFloat(tip || 0) >= 0
                            ? parseFloat(tip || 0)
                            : 0
                          ).toFixed(2)}
                        </td>
                      </tr>
                    </tbody>
                  </table>

                  {/* total */}
                  <div className="d-flex justify-content-between">
                    <div className="text-start ps-4">
                      <strong>
                        <h5>Total</h5>
                      </strong>
                    </div>
                    <div className="text-end pe-4">
                      <strong>
                        <h5>${total.toFixed(2)}</h5>
                      </strong>
                    </div>
                    {/* <p>
                      <strong>
                        Change: {change >= 0 ? `$${change.toFixed(2)}` : "—"}
                      </strong>
                    </p> */}
                  </div>

                  <br />
                  <br />

                  {/* how much the user is paying */}
                  <div className="input-group mb-3">
                    <span className="input-group-text">$</span>
                    <div className="form-floating">
                      <input
                        type="number"
                        required
                        min="0"
                        step="0.01"
                        className={`form-control ${
                          errors.amountPaid ? "is-invalid" : ""
                        }`}
                        id="floatingInputGroup2"
                        value={amountPaid}
                        onChange={(e) => setAmountPaid(e.target.value)}
                        placeholder="Amount Paid"
                      />
                      {errors.amountPaid && (
                        <div className="invalid-feedback">
                          {errors.amountPaid}
                        </div>
                      )}
                      <label htmlFor="floatingInputGroup2">Amount Paid</label>
                    </div>
                  </div>

                  {/* final submit button */}
                  <button type="submit" className="btn btn-success">
                    Place Order
                  </button>
                  <br />
                </div>
              </div>
            </div>

            <br />

            {/* Navigation button */}
            <div style={{ display: "flex", justifyContent: "flex-start" }}>
              <button
                type="button"
                align="left"
                className="btn btn-outline-primary"
                onClick={() => {
                  setPage(1);
                  setTipType(-2);
                  setTip("");
                }}
              >
                ← Edit Cart
              </button>
            </div>

            <br />
            <br />

            {/* error msg box */}
            {errors.page2 && (
              <div className="alert alert-danger" role="alert">
                {errors.page2}
              </div>
            )}
            {successMessage && (
              <div className="alert alert-success mt-3" role="alert">
                {successMessage}
              </div>
            )}
          </form>
        </div>
      )}
      <br />
      <br />
    </div>
  );
};

export default PlaceOrderComponent;
