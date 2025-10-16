import axios from "axios"
import { getToken } from './AuthService';



/** Base URL for the Inventory API - Correspond to methods in Backend's InventoryController. */
const REST_API_BASE_URL = "http://localhost:8080/api/inventory"


axios.interceptors.request.use(function (config) {
    config.headers['Authorization'] = getToken()
    return config;
  }, function (error) {
    // Do something with request error
    return Promise.reject(error);
  });


/** GET Inventory - returns all inventory */
export const getInventory = () => axios.get(REST_API_BASE_URL)

/** PUT Inventory - updates the inventory */
export const updateInventory = (inventory) => axios.put(REST_API_BASE_URL, inventory)