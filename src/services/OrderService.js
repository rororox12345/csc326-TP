import axios from "axios";
import { getToken } from "./AuthService";

const BASE_REST_API_URL = "http://localhost:8080/api/orders";

axios.interceptors.request.use(
  function (config) {
    config.headers["Authorization"] = `Bearer ${getToken()}`;
    return config;
  },
  function (error) {
    return Promise.reject(error);
  }
);

// place an order (POST request that we get)
export const placeOrder = (order) => axios.post(BASE_REST_API_URL, order);

// staff can get all the orderes in the system currently
export const getAllOrders = () => axios.get(BASE_REST_API_URL + "/staff");

// get a user's current orders to reference for order history (i think thats the ec too, but we also need to get a list of exisitng orders, and we need a retrieval function designed to create data as well)
// i'm gonna just make a setup page for listing the order history for now as well -> ListOrdersComponent
export const getOrdersForUser = () =>
  axios.get(BASE_REST_API_URL + "/customer");

// fulfill an order -> staff side -> fulfillorders component
export const fulfillOrder = (id) =>
  axios.put(BASE_REST_API_URL + "/" + id + "/fulfill");

// pickup an order -> customer side -> pickupordere component
export const pickupOrder = (id) =>
  axios.put(BASE_REST_API_URL + "/" + id + "/pickup");
