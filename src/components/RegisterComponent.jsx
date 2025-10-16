import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerAPICall } from "../services/AuthService";

const RegisterComponent = () => {
  const [name, setName] = useState("");
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [errors, setErrors] = useState({});

  const navigator = useNavigate();

  function handleRegistrationForm(e) {
    e.preventDefault();

    const register = { name, username, email, password };

    console.log(register);

    setErrors({});

    registerAPICall(register)
      .then((response) => {
        console.log(response.data);
        navigator("/login");
      })
      .catch((error) => {
        console.error(error);
        const errorsCopy = { ...errors };
        errorsCopy.general =
          "Unable to register\nUsername or email already in use";
        setErrors(errorsCopy);
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
              <h2 className="text-center">User Registration Form</h2>
            </div>
            <div className="card-body">
              <form onSubmit={handleRegistrationForm}>
                <div className="row mb-3">
                  <label className="col-md-3 control-label" align="right">
                    Name{" "}
                  </label>
                  <div className="col-md-9">
                    <input
                      type="text"
                      name="name"
                      className="form-control"
                      required
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                    ></input>
                  </div>
                </div>

                <div className="row mb-3">
                  <label className="col-md-3 control-label" align="right">
                    Username{" "}
                  </label>
                  <div className="col-md-9">
                    <input
                      type="text"
                      name="username"
                      className="form-control"
                      required
                      value={username}
                      onChange={(e) => setUsername(e.target.value)}
                    ></input>
                  </div>
                </div>

                <div className="row mb-3">
                  <label className="col-md-3 control-label" align="right">
                    Email{" "}
                  </label>
                  <div className="col-md-9">
                    <input
                      type="email"
                      name="email"
                      className="form-control"
                      required
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                    ></input>
                  </div>
                </div>

                <div className="row mb-3">
                  <label className="col-md-3 control-label" align="right">
                    Password{" "}
                  </label>
                  <div className="col-md-9">
                    <input
                      type="password"
                      name="password"
                      className="form-control"
                      required
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                    ></input>
                  </div>
                </div>

                <div className="form-group mb-3">
                  <button type="submit" className="btn btn-primary">
                    Submit
                  </button>
                </div>
              </form>
            </div>
            {errors.general && (
              <div
                className="alert alert-danger mt-3"
                role="alert"
                style={{ whiteSpace: "pre-wrap" }}
              >
                {errors.general}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterComponent;
