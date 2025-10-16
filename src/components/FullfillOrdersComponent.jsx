import React, { useEffect, useRef, useState } from "react";
import { isStaffUser } from "../services/AuthService";
import { fulfillOrder, getAllOrders } from "../services/OrderService";

/**
 * fulfills the orders passed in from the customer, tracks status of orders, and has a scrollable navbar
 * that was used from the bootstrap documentation for scrollspy
 * @returns
 */
const OrdersTableComponent = () => {
  const [orders, setOrders] = useState([]);
  const [lastPickup, setLastPickup] = useState(null);
  const scrollR = useRef(null);
  const role = isStaffUser() ? "staff" : "unauthorized";

  // fetches orders
  useEffect(() => {
    const fetch = async () => {
      try {
        const res = await getAllOrders();
        const newOrders = res.data || [];

        const previouslyFulfilled = orders
          .filter((o) => o.orderStatus === "FULFILLED")
          .map((o) => o.orderId);

        const nowPickedUp = newOrders.filter(
          (o) =>
            o.orderStatus === "PICKED_UP" &&
            previouslyFulfilled.includes(o.orderId)
        );

        if (nowPickedUp.length > 0) {
          setLastPickup(nowPickedUp.map((o) => o.orderId).join(", "));
        }

        setOrders(newOrders);
      } catch (error) {
        console.error("can't load orders", error);
      }
    };
    fetch();
  }, [role]);

  // bootstrap documentation for scroll reference and scroll spy, we can call it like this
  useEffect(() => {
    if (window.bootstrap && scrollR.current) {
      const scrollSpy = window.bootstrap.ScrollSpy.getOrCreateInstance(
        scrollR.current,
        {
          target: "#order-scrollspy",
          offset: 100,
        }
      );
      // manually refresh the orders list
      scrollSpy.refresh();
    }
  }, [orders]);

  // attempts to setup and call the fulfillment process for an order
  const fulfill = async (orderId) => {
    try {
      const success = await fulfillOrder(orderId);
      if (success) {
        const result = await getAllOrders();
        setOrders(result.data || []);
      }
    } catch (err) {
      console.error("Fulfill action failed", err);
    }
  };

  // sets the percentage of progress as a set percent for the bar to move across
  // divide into thirds cause that's easy
  const getProgressPercent = (status) => {
    switch (status) {
      case "PLACED":
        return 33;
      case "FULFILLED":
        return 66;
      case "PICKED_UP":
        return 100;
      default:
        return 0;
    }
  };

  // sets as a color for each status, default is just light
  const getProgressColor = (status) => {
    switch (status) {
      case "PLACED":
        return "bg-secondary";
      case "FULFILLED":
        return "bg-info";
      case "PICKED_UP":
        return "bg-success";
      default:
        return "bg-light";
    }
  };

  // if (role !== "staff") {
  //   return (
  //     <div className="container mt-4">
  //       <h3>Unauthorized</h3>
  //       <div className="alert alert-danger">
  //         You do not have access to this page.
  //       </div>
  //     </div>
  //   );
  // }

  // show nothing when there's no ready orders
  const visibleOrders = orders.filter(
    (order) =>
      order.orderStatus === "PLACED" || order.orderStatus === "FULFILLED"
  );

  if (visibleOrders.length === 0) {
    return (
      <div className="container mt-4">
        <h3>Orders to Fulfill</h3>
        <div className="alert alert-info">No orders to display.</div>
        {lastPickup && (
          <div className="alert alert-success mt-3">
            Order(s) #{lastPickup} have been picked up!
          </div>
        )}
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <h3>Orders to Fulfill</h3>
      {/** hey your order # is ready */}
      {lastPickup && (
        <div className="alert alert-success">
          Order(s) #{lastPickup} have been picked up!
        </div>
      )}

      {/** sticky top lets it grow from the top easily */}
      <div className="row">
        <div className="col-3">
          <div id="order-scrollspy" className="list-group sticky-top">
            {orders
              .filter(
                (order) =>
                  order.orderStatus === "PLACED" ||
                  order.orderStatus === "FULFILLED"
              )
              .map((order) => (
                <a
                  key={order.orderId}
                  href={`#order-${order.orderId}`}
                  className="list-group-item list-group-item-action"
                >
                  Order #{order.orderId}
                </a>
              ))}
          </div>
        </div>

        <div
          className="col-9"
          style={{ maxHeight: "70vh", overflowY: "auto", position: "relative" }}
          data-bs-spy="scroll"
          data-bs-target="#order-scrollspy"
          data-bs-offset="100"
          tabIndex="0"
          ref={scrollR}
        >
          {orders
            .filter(
              (order) =>
                order.orderStatus === "PLACED" ||
                order.orderStatus === "FULFILLED"
            )
            .map((order) => (
              <div
                id={`order-${order.orderId}`}
                key={order.orderId}
                className="mb-5 pb-2 border-bottom"
              >
                <h5>Order #{order.orderId}</h5>

                <table className="table table-bordered">
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
                    <tr>
                      <td colSpan="3">
                        {/* 2 buttons, status and fulfill? */}
                        <div className="d-flex justify-content-between align-items-center">
                          <span className="fw-semibold">
                            Status:{" "}
                            <span className="text-capitalize">
                              {order.orderStatus.toLowerCase()}
                            </span>
                          </span>
                          {order.orderStatus === "PLACED" && (
                            <button
                              className="btn btn-warning btn-sm"
                              onClick={() => fulfill(order.orderId)}
                            >
                              Fulfill
                            </button>
                          )}
                        </div>

                        <div className="progress mt-2">
                          <div
                            className={`progress-bar progress-bar-striped progress-bar-animated ${getProgressColor(
                              order.orderStatus
                            )}`}
                            role="progressbar"
                            style={{
                              width: `${getProgressPercent(
                                order.orderStatus
                              )}%`,
                            }}
                            aria-valuenow={getProgressPercent(
                              order.orderStatus
                            )}
                            aria-valuemin="0"
                            aria-valuemax="100"
                          >
                            {order.orderStatus}
                          </div>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            ))}
        </div>
      </div>
    </div>
  );
};

export default OrdersTableComponent;
