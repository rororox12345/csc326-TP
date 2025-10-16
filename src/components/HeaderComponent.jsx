import React from "react";
import { NavLink, useNavigate } from "react-router-dom";
import {
  isAdminUser,
  isCustomerUser,
  isGuestUser,
  isStaffUser,
  isUserLoggedIn,
  logout,
} from "../services/AuthService";

const HeaderComponent = () => {
  const isAuth = isUserLoggedIn();
  const navigator = useNavigate();

  function handleLogout() {
    logout();
    navigator("/login");
  }

  return (
    <div>
      <header>
        <nav className="navbar navbar-expand-md navbar-dark bg-dark">
          <div>
            <a className="navbar-brand">WolfCafe</a>
          </div>
          <div className="collapse navbar-collapse">
            <ul className="navbar-nav">
              {isAdminUser() && isAuth && (
                <li className="nav-item">
                  <NavLink to="/manage-staff" className="nav-link">
                    Staff
                  </NavLink>
                </li>
              )}
              {isAdminUser() && isAuth && (
                <li className="nav-item">
                  <NavLink to="/manage-customers" className="nav-link">
                    Customers
                  </NavLink>
                </li>
              )}
              {isAdminUser() && isAuth && (
                <li className="nav-item">
                  <NavLink to="/taxrate" className="nav-link">
                    Tax Rate
                  </NavLink>
                </li>
              )}
              {isStaffUser() && isAuth && (
                <li className="nav-item">
                  <NavLink to="/inventory" className="nav-link">
                    Inventory
                  </NavLink>
                </li>
              )}
              {isStaffUser() && isAuth && (
                <li className="nav-item">
                  <NavLink to="/recipes" className="nav-link">
                    Recipes
                  </NavLink>
                </li>
              )}
              {isStaffUser() && isAuth && (
                <li className="nav-item">
                  <NavLink to="/items" className="nav-link">
                    Items
                  </NavLink>
                </li>
              )}
              {isStaffUser() && isAuth && (
                <li className="nav-item">
                  <NavLink to="/order-table" className="nav-link">
                    Fulfill Orders
                  </NavLink>
                </li>
              )}
              {(isCustomerUser() || isGuestUser()) && (
                <li className="nav-item">
                  <NavLink to="/place-order" className="nav-link">
                    Place Order
                  </NavLink>
                </li>
              )}
              {isCustomerUser() && (
                <li className="nav-item">
                  <NavLink to="/orders" className="nav-link">
                    Order History
                  </NavLink>
                </li>
              )}
              {isCustomerUser() && (
                <li className="nav-item">
                  <NavLink to="/pickup-order" className="nav-link">
                    Pickup Order
                  </NavLink>
                </li>
              )}
            </ul>
          </div>
          <ul className="navbar-nav">
            {!isAuth && (
              <li className="nav-item">
                <NavLink to="/register" className="nav-link">
                  Register
                </NavLink>
              </li>
            )}
            {!isAuth && (
              <li className="nav-item">
                <NavLink to="/login" className="nav-link">
                  Login
                </NavLink>
              </li>
            )}
            {isAuth && (
              <li className="nav-item">
                <NavLink
                  to="/login"
                  className="nav-link"
                  onClick={handleLogout}
                >
                  {isGuestUser()? "Login" : "Logout"}
                </NavLink>
              </li>
            )}
          </ul>
        </nav>
      </header>
    </div>
  );
};

export default HeaderComponent;
