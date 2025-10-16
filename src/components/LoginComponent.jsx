import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  isAdminUser,
  isCustomerUser,
  isStaffUser,
  loginAPICall,
  saveLoggedInUser,
  storeToken,
} from "../services/AuthService";

const LoginComponent = () => {
  const [usernameOrEmail, setUsernameOrEmail] = useState("");
  const [password, setPassword] = useState("");

  const [errors, setErrors] = useState({});

  const navigator = useNavigate();

  async function handleLoginForm(e) {
    e.preventDefault();

    const loginObj = { usernameOrEmail, password };

    console.log(loginObj);

    await loginAPICall(usernameOrEmail, password)
      .then((response) => {
        console.log(response.data);
        setErrors({});

        // const token = 'Basic ' + window.btoa(usernameOrEmail + ':' + password);
        const token = "Bearer " + response.data.accessToken;

        const role = response.data.role;

        storeToken(token);
        saveLoggedInUser(usernameOrEmail, role);

        if (isAdminUser()) {
          navigator("/manage-staff");
        } else if (isStaffUser()) {
          navigator("/inventory");
        } else if (isCustomerUser()) {
          navigator("/place-order");
        }

        window.location.reload(false);
      })
      .catch((error) => {
        setErrors({ general: "Incorrect Login Information" });
        console.error("ERROR1" + error);
      });
  }

  async function handleGuestLogin() {
    //console.log(loginObj)

    await loginAPICall("guest", "123")
      .then((response) => {
        console.log(response.data);

        // const token = 'Basic ' + window.btoa(usernameOrEmail + ':' + password);
        const token = "Bearer " + response.data.accessToken;

        const role = response.data.role;

        storeToken(token);
        saveLoggedInUser("guest", role);
        navigator("/place-order");

        window.location.reload(false);
      })
      .catch((error) => {
        console.error("ERROR1" + error);
      });
  }

  return (
    <div className="container">
      <br />
      <br />
      <div className="row">
        <div className="col-md-6 offset-md-3 offset-md-3">
          <div className="card">
            <div className="card-header">
              <h2 className="text-center">Login Form</h2>
            </div>
            <div className="card-body">
              <form>
                <div className="row mb-3">
                  <label className="col-md-3 control-label">Username</label>
                  <div className="col-md-9">
                    <input
                      type="text"
                      name="usernameOrEmail"
                      className="form-control"
                      placeholder="Enter username or email"
                      value={usernameOrEmail}
                      onChange={(e) => setUsernameOrEmail(e.target.value)}
                    ></input>
                  </div>
                </div>

                <div className="row mb-3">
                  <label className="col-md-3 control-label">Password</label>
                  <div className="col-md-9">
                    <input
                      type="password"
                      name="password"
                      className="form-control"
                      placeholder="Enter password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                    ></input>
                  </div>
                </div>

                <div className="form-group mb-3">
                  <button
                    className="btn btn-primary"
                    onClick={(e) => handleLoginForm(e)}
                  >
                    Submit
                  </button>
                </div>
                <br />
                <a
                  className="text-primary"
                  style={{ cursor: "pointer", textDecoration: "underline" }}
                  onClick={handleGuestLogin}
                >
                  Continue as Guest
                </a>
              </form>
            </div>
          </div>
        </div>
      </div>
      <br />

      {errors.general && (
        <div className="alert alert-danger mt-3">{errors.general}</div>
      )}

      <br />
    </div>
  );
};

export default LoginComponent;
