import React, { useEffect, useState } from "react";
// imported the bootstrap elements directly so we can just call them
import { Alert, Button, Card, Collapse, Spinner, Table } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { isCustomerUser } from "../services/AuthService";
import { getOrdersForUser, pickupOrder } from "../services/OrderService";

/**
 * allows the customer to pick up orders that have been fulfilled
 * only fulfilled orders will show up, and it uses collapsible order cards
 */
const PickupOrderComponent = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pickupStatus, setPickupStatus] = useState({});
  const [error, setError] = useState(null);
  const [expandedOrderId, setExpandedOrderId] = useState(null);
  const navigate = useNavigate();

  const role = isCustomerUser() ? "customer" : "unauthorized";

  // fetch user's orders on mount
  useEffect(() => {
    if (role !== "customer") return;

    const fetchOrders = async () => {
      try {
        const res = await getOrdersForUser();
        setOrders(res.data || []);
      } catch (err) {
        console.error("no orders to grab", err);
        setError("Can't load orders. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, [role]);

  // filter only FULFILLED orders to display
  const fulfilledOrders = orders.filter(
    (order) => order.orderStatus === "FULFILLED"
  );

  // handles the pickup action
  const handlePickup = async (orderId) => {
    try {
      const result = await pickupOrder(orderId);
      // specific stat when order passed, try the thing using index.html animate? idk if it does anything
      if (result.status === 204) {
        setPickupStatus((prev) => ({ ...prev, [orderId]: "animating" }));

        // let the animation play before removing the order cause it was just going too fast maybe
        // 1000ms = 1sec
        setTimeout(async () => {
          const refreshed = await getOrdersForUser();
          setPickupStatus((prev) => ({ ...prev, [orderId]: "picked" }));
          setOrders(refreshed.data || []);
        }, 1000);
      } else {
        throw new Error("Pickup did not return success");
      }
    } catch (err) {
      console.error("Pickup failed", err);
      setPickupStatus((prev) => ({ ...prev, [orderId]: "error" }));
    }
  };

  // toggle collapse for a specific order
  const toggleCollapse = (orderId) => {
    setExpandedOrderId((prevId) => (prevId === orderId ? null : orderId));
  };

  // // unauth if somehow got here
  // if (role !== "customer") {
  //   return (
  //     <div className="container mt-4">
  //       <h3>Unauthorized</h3>
  //       <Alert variant="danger">You do not have access to this page.</Alert>
  //     </div>
  //   );
  // }

  // still loading
  if (loading) {
    return (
      <div className="container mt-4 text-center">
        <Spinner animation="border" />
        <p>Loading your orders...</p>
      </div>
    );
  }

  // no ready orders available message
  if (fulfilledOrders.length === 0) {
    return (
      <div className="container mt-4">
        <h3>Ready for Pickup</h3>
        <Alert variant="info">You have no ready orders at this time.</Alert>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <h3>Ready for Pickup</h3>
      {error && <Alert variant="danger">{error}</Alert>}
      <div className="row">
        {fulfilledOrders.map((order) => {
          const status = pickupStatus[order.orderId];

          return (
            <div
              key={order.orderId}
              className={`col-md-6 mb-4 ${
                status === "animating"
                  ? "animate__animated animate__fadeOutUp"
                  : ""
              }`}
            >
              <Card
                className={`shadow-sm ${
                  status === "picked" ? "border-success" : ""
                }`}
              >
                <Card.Header>
                  <div className="d-flex justify-content-between align-items-center">
                    <span>Order #{order.orderId}</span>
                    <Button
                      variant="link"
                      onClick={() => toggleCollapse(order.orderId)}
                      aria-controls={`order-details-${order.orderId}`}
                      aria-expanded={expandedOrderId === order.orderId}
                    >
                      {expandedOrderId === order.orderId
                        ? "Hide Details"
                        : "View Details"}
                    </Button>
                  </div>
                </Card.Header>
                <Collapse in={expandedOrderId === order.orderId}>
                  <div id={`order-details-${order.orderId}`}>
                    <Card.Body>
                      <Table striped bordered hover size="sm" className="mb-3">
                        <thead>
                          <tr>
                            <th>Name</th>
                            <th>Quantity</th>
                            <th>Type</th>
                          </tr>
                        </thead>
                        <tbody>
                          {order.orderItemResponseList.map((item, idx) => (
                            <tr key={`item-${idx}`}>
                              <td>{item.itemName}</td>
                              <td>{item.quantity}</td>
                              <td>Item</td>
                            </tr>
                          ))}
                          {order.orderRecipeResponseList.map((recipe, idx) => (
                            <tr key={`recipe-${idx}`}>
                              <td>{recipe.recipeName}</td>
                              <td>{recipe.quantity}</td>
                              <td>Drink</td>
                            </tr>
                          ))}
                        </tbody>
                      </Table>
                      <div className="d-flex justify-content-between align-items-center">
                        <span className="fw-semibold">
                          Status:{" "}
                          <span className="text-capitalize">
                            {order.orderStatus.toLowerCase()}
                          </span>
                        </span>
                        <div>
                          {status === "picked" && (
                            <span className="text-success fw-bold">
                              Picked Up
                            </span>
                          )}
                          {status === "error" && (
                            <span className="text-danger fw-bold">
                              Pickup Failed
                            </span>
                          )}
                          {!status && (
                            <Button
                              variant="success"
                              size="sm"
                              onClick={() => handlePickup(order.orderId)}
                            >
                              Pick Up
                            </Button>
                          )}
                        </div>
                      </div>
                    </Card.Body>
                  </div>
                </Collapse>
              </Card>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default PickupOrderComponent;
