import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getOrdersForUser } from "../services/OrderService";

/**
 * list orders component -> dummy component for now which we can fix later
 * has a rough outline of a table with an Order ID, Items that they got, Recipes used (if needed idk what needs to go here), Tip percentage (if needed again), and the total paid
 * we can strip it down to just id, items and total if that's cleaner (makes more sense ngl)
 * has a button to redirect back to placing an order once they've seen their history :)
 * @returns a component which displays how the user can place an order
 */
const ListOrdersComponent = () => {
  // grab the orders from us
  const [orders, setOrders] = useState([]);
  const navigate = useNavigate();

  // fetching orders
  useEffect(() => {
    fetchOrders();
  }, []);

  /**
   * grabs the orders from the user
   */
  const fetchOrders = () => {
    getOrdersForUser()
      .then((response) => {
        setOrders(response.data);
      })
      .catch((error) => {
        console.error("can't fetch orders", error);
      });
  };

  /**
   * goes to place an order by just navigating to the field (when the btn is pressed)
   */
  const goToPlaceOrder = () => {
    navigate("/place-order");
  };

  return (
    <div className="container">
      <br />
      <br />
      <h2 className="text-center">My Orders</h2>
      <button className="btn btn-primary mb-3" onClick={goToPlaceOrder}>
        Place New Order
      </button>
      <table className="table table-bordered table-striped">
        <thead>
          <tr>
            <th>Order ID</th>
            <th>Items</th>
            <th>Recipes</th>
            <th>Tip</th>
            <th>Total</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((order) => (
            <tr key={order.orderId}>
              <td>{order.orderId}</td>
              <td>
                <ul>
                  {order.orderItemResponseList.map((item) => (
                    <li key={item.itemId}>
                      {item.itemName} x{item.quantity}
                    </li>
                  ))}
                </ul>
              </td>
              <td>
                <ul>
                  {order.orderRecipeResponseList.map((recipe) => (
                    <li key={recipe.recipeId}>
                      {recipe.recipeName} x{recipe.quantity}
                    </li>
                  ))}
                </ul>
              </td>
              <td>${order.tip.toFixed(2)}</td>
              <td>${order.total.toFixed(2)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ListOrdersComponent;
